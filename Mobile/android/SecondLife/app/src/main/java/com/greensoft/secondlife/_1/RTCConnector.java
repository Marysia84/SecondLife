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

/**
 * Created by zebul on 5/16/17.
 */

public class RTCConnector {

    private static boolean GLOBALS_INITIALIZED = false;
    private MediaConstraints mediaConstraints = new MediaConstraints();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();

    private PeerConnectionFactory factory;
    private MediaStream localMediaStream;
    private VideoSource videoSource;
    private PeerConnectionObserverFactory peerConnectionObserverFactory;
    private final SdpObserverFactory sdpObserverFactory;
    private PeerConnectionParameters pcParams;
    private boolean turnedOn;

    public RTCConnector(Context appContext,
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

    public void turnOn(){

        doTurnOn();
        turnedOn = true;
    }

    public void turnOff(){

        doTurnOff();
        turnedOn = false;
    }

    private void doTurnOn() {
        factory = new PeerConnectionFactory();

        localMediaStream = factory.createLocalMediaStream("ARDAMS");
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

        videoSource = factory.createVideoSource(WebRtcClient.getVideoCapturer(), videoConstraints);
        localMediaStream.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localMediaStream.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));
    }

    private void doTurnOff() {
        factory.dispose();
    }

    public RTCConnection createRTCConnection(
            PeerId peerId) {

        final PeerConnection.Observer peerConnectionObserver =
                peerConnectionObserverFactory.createPeerConnectionObserver(peerId);
        final PeerConnection peerConnection = factory.createPeerConnection(
                iceServers, mediaConstraints, peerConnectionObserver);

        final SdpObserver sdpObserver = sdpObserverFactory.createSdpObserver(peerId);
        RTCConnection rtcConnection = new RTCConnection(peerConnection, mediaConstraints, sdpObserver);
        return rtcConnection;
    }


    public boolean isTurnedOn() {
        return turnedOn;
    }
}
