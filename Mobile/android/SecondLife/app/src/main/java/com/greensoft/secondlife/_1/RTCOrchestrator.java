package com.greensoft.secondlife._1;

import android.content.Context;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.greensoft.log.Logger;
import com.greensoft.secondlife.PeerConnectionParameters;
import com.greensoft.secondlife.Restartable;
import com.greensoft.secondlife.WebRtcClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.greensoft.secondlife.SecondLifeService.getDeviceName;

/**
 * Created by zebul on 5/22/17.
 */

public class RTCOrchestrator implements Restartable {

    private static final String TAG = RTCOrchestrator.class.getSimpleName();
    private Socket client;
    private String host;
    private RTCController rtcController;
    private HashMap<String, Command> commandMap;

    public RTCOrchestrator(Context context, String host, PeerConnectionParameters params){

        RTCConnector rtcConnector = new RTCConnector(
                context, peerConnectionObserverFactory, sdpObserverFactory, params);

        rtcController = new RTCController(rtcConnector);
        this.host = host;
        this.commandMap = new HashMap<>();
        commandMap.put("init", new CreateOfferCommand());
        commandMap.put("offer", new CreateAnswerCommand());
        commandMap.put("answer", new SetRemoteSDPCommand());
        commandMap.put("candidate", new AddIceCandidateCommand());
        commandMap.put("restartVideoStream", new RestartVideoStreamCommand());
    }

    @Override
    public void start(){

        try {
            client = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.on("id", onId);
        client.on("message", onMessage);
        client.on("fetch_clients", onFetchClients);
        client.connect();
    }

    @Override
    public void stop(){

        client.disconnect();
        client.close();
    }

    private Emitter.Listener onId = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String id = (String) args[0];

            emitReadyToStream(getDeviceName());
            //mListener.onCallReady(id);
        }
    };

    private void emitReadyToStream(String deviceName) {

        try {
            JSONObject message = new JSONObject();
            message.put("name", getDeviceName());
            client.emit("readyToStream", message);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

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
                Logger.e(TAG, e.getMessage());
            }
        }
        //mListener.onClientsFetched(clients);
        }
    };

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
                        Logger.e(TAG, e.getMessage());
                    }
                }

                PeerId peerId = new PeerId(from);
                if(!rtcController.containsPeer(peerId)){

                    rtcController.addPeer(peerId);
                }
                commandMap.get(type).execute(peerId, payload);
            } catch (JSONException e) {
                Logger.e(TAG, e.getMessage());
            } catch (Peers.PeerNotExistsExeption peerNotExistsExeption) {
                Logger.e(TAG, peerNotExistsExeption.getMessage());
            }
        }
    };

    private interface Command{
        void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption;
    }

    private class CreateOfferCommand implements Command {
        public void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption {

            Logger.d(TAG,"CreateOfferCommand");
            rtcController.createOffer(peerId);
        }
    }

    private class CreateAnswerCommand implements Command {
        public void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption {

            Logger.d(TAG,"CreateAnswerCommand");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            rtcController.createAnswer(peerId, sdp);
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption {

            Logger.d(TAG,"SetRemoteSDPCommand");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            rtcController.setRemoteDescription(peerId, sdp);
        }
    }

    private class AddIceCandidateCommand implements Command {
        public void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption {

            Logger.d(TAG,"AddIceCandidateCommand");
            IceCandidate candidate = new IceCandidate(
                    payload.getString("id"),
                    payload.getInt("label"),
                    payload.getString("candidate"));
            rtcController.addIceCandidate(peerId, candidate);
        }
    }

    private class RestartVideoStreamCommand implements Command {
        public void execute(PeerId peerId, JSONObject payload) throws JSONException, Peers.PeerNotExistsExeption {

            rtcController.restart();
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
    public void sendMessage(PeerId peerId, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", peerId.getId());
        message.put("type", type);
        message.put("payload", payload);
        client.emit("message", message);
    }

    public void fetchClients() throws JSONException {
        JSONObject message = new JSONObject();
        client.emit("fetch_clients", message);
    }

    SdpObserverFactory sdpObserverFactory = new SdpObserverFactory(){


        @Override
        public SdpObserver createSdpObserver(final PeerId peerId) {
            return new SdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sdp) {

                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("type", sdp.type.canonicalForm());
                        payload.put("sdp", sdp.description);
                        sendMessage(peerId, sdp.type.canonicalForm(), payload);
                        rtcController.setLocalDescription(peerId, sdp);
                        Logger.d(TAG,"Peer.onCreateSuccess");
                    } catch (JSONException e) {
                        Logger.e(TAG,"Peer.onCreateSuccess: "+e.getMessage());
                    } catch (Peers.PeerNotExistsExeption e) {
                        Logger.e(TAG,"Peer.onCreateSuccess: "+e.getMessage());
                    }
                }

                @Override
                public void onSetSuccess() {
                    Logger.d(TAG,"SdpObserverFactory.onSetSuccess");
                }

                @Override
                public void onCreateFailure(String s) {
                    Logger.d(TAG,"SdpObserverFactory.onCreateFailure:"+s);
                }

                @Override
                public void onSetFailure(String s) {
                    Logger.d(TAG,"SdpObserverFactory.onSetFailure:"+s);
                }
            };
        }
    };

    PeerConnectionObserverFactory peerConnectionObserverFactory = new PeerConnectionObserverFactory(){

        @Override
        public PeerConnection.Observer createPeerConnectionObserver(final PeerId peerId) {

            return new PeerConnection.Observer(){

                @Override
                public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                    Logger.d(TAG,"PeerConnectionObserverFactory.onSignalingChange:"+signalingState);
                }

                @Override
                public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

                    Logger.d(TAG,"onIceConnectionChange: "+iceConnectionState);
                    boolean removePeer =
                            (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED)||
                                    (iceConnectionState == PeerConnection.IceConnectionState.FAILED);
                    if(removePeer) {
                        removePeer();
                    }
                }

                @Override
                public void onIceConnectionReceivingChange(boolean b) {
                    Logger.d(TAG,"PeerConnectionObserverFactory.onIceConnectionReceivingChange:"+b);
                }

                @Override
                public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                    Logger.d(TAG,"PeerConnectionObserverFactory.onIceGatheringChange: "+iceGatheringState);
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {

                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("label", iceCandidate.sdpMLineIndex);
                        payload.put("id", iceCandidate.sdpMid);
                        payload.put("candidate", iceCandidate.sdp);
                        sendMessage(peerId, "candidate", payload);
                        Logger.d(TAG,"onIceCandidate: "+iceCandidate);
                    } catch (JSONException e) {
                        Logger.e(TAG,"onIceCandidate: "+e.getMessage());
                    }
                }

                @Override
                public void onAddStream(MediaStream mediaStream) {

                    Logger.d(TAG,"onAddStream "+mediaStream.label());
                    // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
                    //mListener.onAddRemoteStream(mediaStream, endPoint+1);
                }

                @Override
                public void onRemoveStream(MediaStream mediaStream) {

                    Logger.d(TAG, "onRemoveStream "+mediaStream.label());
                    removePeer();
                }

                @Override
                public void onDataChannel(DataChannel dataChannel) {

                    Logger.d(TAG,"Peer.onDataChannel");
                }

                @Override
                public void onRenegotiationNeeded() {

                    Logger.d(TAG,"Peer.onRenegotiationNeeded");
                }

                private void removePeer(){

                    try {
                        rtcController.removePeer(peerId);
                    } catch (Peers.PeerNotExistsExeption e) {
                        Logger.e(TAG,"removePeer: "+e.getMessage());
                    }
                }
            };
        }
    };

}
