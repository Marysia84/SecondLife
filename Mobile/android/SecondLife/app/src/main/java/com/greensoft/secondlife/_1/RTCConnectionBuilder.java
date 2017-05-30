package com.greensoft.secondlife._1;

import android.content.Context;

import com.greensoft.secondlife.PeerConnectionParameters;
import com.greensoft.secondlife.WebRtcClient;

import org.webrtc.AudioSource;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.VideoSource;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 5/16/17.
 */

public class RTCConnectionBuilder {

    private static boolean GLOBALS_INITIALIZED = false;
    private MediaConstraints mediaConstraints = new MediaConstraints();
    private List<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private List<LocalMediaStreamAvailableListener> localMediaStreamAvailableListeners = new LinkedList<>();

    private PeerConnectionFactory factory;
    private MediaStream localMediaStream;
    private VideoSource videoSource;
    private AudioSource audioSource;
    private PeerConnectionObserverFactory peerConnectionObserverFactory;
    private final SdpObserverFactory sdpObserverFactory;
    private PeerConnectionParameters pcParams;
    private boolean initailized;

    public RTCConnectionBuilder(Context appContext,
                                PeerConnectionObserverFactory peerConnectionObserverFactory,
                                SdpObserverFactory sdpObserverFactory,
                                PeerConnectionParameters params){

        if(!GLOBALS_INITIALIZED){
            PeerConnectionFactory.initializeAndroidGlobals(appContext,/*listener, */true, true, params.videoCodecHwAcceleration/*, mEGLcontext*/);
            GLOBALS_INITIALIZED = true;
        }

        this.peerConnectionObserverFactory = peerConnectionObserverFactory;
        this.sdpObserverFactory = sdpObserverFactory;
        pcParams = params;

        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("turn:rojarand.ddns.net:23478", "zebul", "szefu1"));

        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    }

    private void ensureInitailized(){

        if(initailized){
            return;
        }
        setUp();
        initailized = true;
    }

    public void dispose(){

        if(!initailized){
            return;
        }
        tearDown();
        initailized = false;
    }

    private void setUp() {

        factory = new PeerConnectionFactory();

        localMediaStream = factory.createLocalMediaStream("ARDAMS");
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

        videoSource = factory.createVideoSource(WebRtcClient.getVideoCapturer(), videoConstraints);
        localMediaStream.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));

        audioSource = factory.createAudioSource(new MediaConstraints());
        localMediaStream.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));

        notifyLocalStreamAvailableListener();

        videoSource.restart();
    }

    public void attachLocalStreamAvailableListener(
            LocalMediaStreamAvailableListener localMediaStreamAvailableListener){
        localMediaStreamAvailableListeners.add(localMediaStreamAvailableListener);
    }

    private void notifyLocalStreamAvailableListener() {

        for(LocalMediaStreamAvailableListener localMediaStreamAvailableListener:
                localMediaStreamAvailableListeners){
            localMediaStreamAvailableListener.onLocalMediaStreamAvailable(localMediaStream);
        }
    }

    private void tearDown() {

        try{

            videoSource.stop();
            videoSource.dispose();

            //audioSource.dispose();
            factory.dispose();

        /*
        factory.dispose();
        videoSource.stop();
        videoSource.dispose();
        */
        }
        catch(Exception exc_){

            exc_.printStackTrace();
        }
    }

    public RTCConnection buildRTCConnection(
            PeerId peerId) {

        ensureInitailized();
        final PeerConnection.Observer peerConnectionObserver =
                peerConnectionObserverFactory.createPeerConnectionObserver(peerId);
        final PeerConnection peerConnection = factory.createPeerConnection(
                iceServers, mediaConstraints, peerConnectionObserver);

        peerConnection.addStream(localMediaStream);
        final SdpObserver sdpObserver = sdpObserverFactory.createSdpObserver(peerId);
        RTCConnection rtcConnection = new RTCConnection(peerConnection, mediaConstraints, sdpObserver);
        return rtcConnection;
    }

    public boolean isInitailized() {
        return initailized;
    }
}
