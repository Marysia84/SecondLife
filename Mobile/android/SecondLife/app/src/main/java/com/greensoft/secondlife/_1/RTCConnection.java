package com.greensoft.secondlife._1;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by zebul on 5/17/17.
 */

public class RTCConnection {

    private PeerConnection peerConnection;
    private MediaConstraints mediaConstraints;
    private SdpObserver sdpObserver;

    public RTCConnection(
            PeerConnection peerConnection,
            MediaConstraints mediaConstraints,
            SdpObserver sdpObserver){

        this.peerConnection = peerConnection;
        this.mediaConstraints = mediaConstraints;
        this.sdpObserver = sdpObserver;
    }

    public void createOffer() {

        peerConnection.createOffer(sdpObserver, mediaConstraints);
    }

    public void createAnswer(SessionDescription sdp) {

        peerConnection.setRemoteDescription(sdpObserver, sdp);
        peerConnection.createAnswer(sdpObserver, mediaConstraints);
    }

    public void setLocalDescription(SessionDescription localDescription) {

        peerConnection.setLocalDescription(sdpObserver, localDescription);
    }

    public void setRemoteDescription(SessionDescription sdp) {

        peerConnection.setRemoteDescription(sdpObserver, sdp);
    }

    public void addIceCandidate(IceCandidate iceCandidate) {

        if(peerConnection.getRemoteDescription() != null){
            peerConnection.addIceCandidate(iceCandidate);
        }
    }

    public void closeConnection() {
        peerConnection.close();
        peerConnection.dispose();
    }
}
