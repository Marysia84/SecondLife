package com.greensoft.secondlife;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.greensoft.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Camera;

import org.webrtc.*;


/**
 * Created by zebul on 4/15/17.
 */

public class WebRtcClient {
    private final static String TAG = WebRtcClient.class.getCanonicalName();
    private final static int MAX_PEER = 2;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private PeerConnectionFactory factory;
    private HashMap<String, Peer> peers = new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    private RTCEventListener mListener;
    private Socket client;

    public WebRtcClient(Context context, RTCEventListener listener, String host, PeerConnectionParameters params, EglBase.Context baseContext) {
        mListener = listener;
        pcParams = params;
        PeerConnectionFactory.initializeAndroidGlobals(context,/*listener, */true, true,
                params.videoCodecHwAcceleration/*, mEGLcontext*/);
        factory = new PeerConnectionFactory();
        MessageHandler messageHandler = new MessageHandler();

        try {
            client = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.on("id", messageHandler.onId);
        client.on("message", messageHandler.onMessage);
        client.on("fetch_clients", messageHandler.onFetchClients);
        client.connect();

        /*
          // draft-nandakumar-rtcweb-stun-uri-01
          // stunURI       = scheme ":" stun-host [ ":" stun-port ]
          // scheme        = "stun" / "stuns"
          // stun-host     = IP-literal / IPv4address / reg-name
          // stun-port     = *DIGIT
          // draft-petithuguenin-behave-turn-uris-01
          // turnURI       = scheme ":" turn-host [ ":" turn-port ]
          //                 [ "?transport=" transport ]
          // scheme        = "turn" / "turns"
          // transport     = "udp" / "tcp" / transport-ext
          // transport-ext = 1*unreserved
          // turn-host     = IP-literal / IPv4address / reg-name
          // turn-port     = *DIGIT
        */
        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("turn:rojarand.ddns.net:23478", "zebul", "szefu1"));

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    }

    private interface Command{
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateOfferCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Logger.d(TAG,"CreateOfferCommand");
            Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }
    }

    private class CreateAnswerCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Logger.d(TAG,"CreateAnswerCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Logger.d(TAG,"SetRemoteSDPCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Logger.d(TAG,"AddIceCandidateCommand");
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("id"),
                        payload.getInt("label"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    private class RestartVideoStreamCommand implements Command{
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Logger.d(TAG,"RestartVideoStreamCommand");

            videoSource.stop();
            videoSource.restart();
        }
    }

    /**
     * Send a message through the signaling server
     *
     * @param to id of recipient
     * @param type type of message
     * @param payload payload of message
     * @throws JSONException
     */
    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        client.emit("message", message);
    }

    public void fetchClients() throws JSONException {
        JSONObject message = new JSONObject();
        client.emit("fetch_clients", message);
    }

    private class MessageHandler {
        private HashMap<String, Command> commandMap;

        private MessageHandler() {
            this.commandMap = new HashMap<>();
            commandMap.put("init", new CreateOfferCommand());
            commandMap.put("offer", new CreateAnswerCommand());
            commandMap.put("answer", new SetRemoteSDPCommand());
            commandMap.put("candidate", new AddIceCandidateCommand());
            commandMap.put("restartVideoStream", new RestartVideoStreamCommand());
        }

        private Emitter.Listener onMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String from = data.getString("from");
                    String type = data.getString("type");
                    Logger.d(TAG,"onMessage from: "+from+", type: "+type);
                    JSONObject payload = null;
                    if(!type.equals("init")) {
                        try {
                            payload = data.getJSONObject("payload");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // if peer is unknown, try to add him
                    if(!peers.containsKey(from)) {
                        // if MAX_PEER is reach, ignore the call
                        int endPoint = findEndPoint();
                        if(endPoint != MAX_PEER) {
                            Peer peer = addPeer(from, endPoint);
                            //TODO
                            if(localMS != null){

                                peer.pc.addStream(localMS);
                            }

                            commandMap.get(type).execute(from, payload);
                        }
                    } else {
                        commandMap.get(type).execute(from, payload);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        private Emitter.Listener onId = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String id = (String) args[0];
                mListener.onCallReady(id);
            }
        };

        private Emitter.Listener onFetchClients = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Map<String, String> clients = new HashMap<>();
                JSONArray clientsArray = (JSONArray) args[0];
                for(int i=0; i<clientsArray.length(); i++){
                    try {
                        JSONObject jsonObject = (JSONObject)clientsArray.get(i);
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        clients.put(id, name);
                    } catch (JSONException e) {

                    }
                }
                mListener.onClientsFetched(clients);
            }
        };
    }

    private class Peer implements SdpObserver, PeerConnection.Observer{
        private PeerConnection pc;
        private String id;
        private int endPoint;

        public Peer(String id, int endPoint) {
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;
            if(localMS != null){

                pc.addStream(localMS); //, new MediaConstraints()
            }

            mListener.onStatusChanged("CONNECTING");
        }

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
                sendMessage(id, sdp.type.canonicalForm(), payload);
                pc.setLocalDescription(Peer.this, sdp);
                Logger.d(TAG,"Peer.onCreateSuccess");
            } catch (JSONException e) {
                Logger.e(TAG,"Peer.onCreateSuccess: "+e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {}

        @Override
        public void onCreateFailure(String s) {}

        @Override
        public void onSetFailure(String s) {}

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {}

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            Logger.d(TAG,"Peer.onIceConnectionChange: "+iceConnectionState);
            boolean removePeer =
                    (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED)||
                    (iceConnectionState == PeerConnection.IceConnectionState.FAILED);
            if(removePeer) {
                removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

            int foo = 1;
            int bar = foo;
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Logger.d(TAG,"Peer.onIceGatheringChange: "+iceGatheringState);
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("label", candidate.sdpMLineIndex);
                payload.put("id", candidate.sdpMid);
                payload.put("candidate", candidate.sdp);
                sendMessage(id, "candidate", payload);
                Logger.d(TAG,"Peer.onIceCandidate: "+candidate);
            } catch (JSONException e) {
                Logger.e(TAG,"Peer.onIceCandidate: "+e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Logger.d(TAG,"onAddStream "+mediaStream.label());
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            mListener.onAddRemoteStream(mediaStream, endPoint+1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Logger.d(TAG, "onRemoveStream "+mediaStream.label());
            removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

            Logger.d(TAG,"Peer.onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {

            Logger.d(TAG,"Peer.onRenegotiationNeeded");
        }
    }

    private Peer addPeer(String id, int endPoint) {

        Logger.d(TAG,"add peer: "+id + " " + endPoint);
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);
        endPoints[endPoint] = true;
        return peer;
    }

    private void removePeer(String id) {

        Logger.d(TAG,"remove peer: "+id);
        Peer peer = peers.get(id);
        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.pc.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }

    /**
     * Call this method in Activity.turnOffVideoSource()
     */
    public void onPause() {
        if(videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if(videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        for (Peer peer : peers.values()) {
            peer.pc.dispose();
        }
        videoSource.dispose();
        factory.dispose();
        client.disconnect();
        client.close();
    }

    private int findEndPoint() {
        for(int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    /**
     * Start the client.
     *
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.
     *
     * @param name client name
     */
    public void start(String name){
        setCamera();
        try {
            JSONObject message = new JSONObject();
            message.put("name", name);
            client.emit("readyToStream", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCamera(){
        localMS = factory.createLocalMediaStream("ARDAMS");
        if(pcParams.videoCallEnabled){
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoSource = factory.createVideoSource(getVideoCapturer(), videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));
        mListener.onLocalStream(localMS);
    }

    public static VideoCapturer getVideoCapturer() {
        String frontCameraDeviceName = getNameOfFrontFacingDevice();//VideoCapturerAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName);
    }

    // Returns the name of the camera with camera index. Returns null if the
    // camera can not be used.
    public static String getDeviceName(int index) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(index, info);
        } catch (Exception exc) {
            Logger.e(TAG, exc);
            return null;
        }

        String facing =
                (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) ? "front" : "back";
        return "Camera " + index + ", Facing " + facing
                + ", Orientation " + info.orientation;
    }

    // Returns the name of the front facing camera. Returns null if the
    // camera can not be used or does not exist.
    public static String getNameOfFrontFacingDevice() {
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    return getDeviceName(i);
            } catch (Exception exc) {
                Logger.e(TAG, exc);
            }
        }
        return null;
    }

    // Returns the name of the back facing camera. Returns null if the
    // camera can not be used or does not exist.
    public static String getNameOfBackFacingDevice() {
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                    return getDeviceName(i);
            } catch (Exception exc) {
                Logger.e(TAG, exc);
            }
        }
        return null;
    }

}
