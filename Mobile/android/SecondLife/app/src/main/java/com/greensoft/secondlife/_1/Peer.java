package com.greensoft.secondlife._1;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by zebul on 5/16/17.
 */

public class Peer {

    private PeerId id;
    private RTCConnection rtcConnection;

    public Peer(PeerId id, RTCConnection rtcConnection) {

        this.id = id;
        this.rtcConnection = rtcConnection;
    }

    public PeerId getId() {
        return id;
    }

    public void createOffer() {

        rtcConnection.createOffer();
    }

    public void createAnswer(SessionDescription sdp) {

        rtcConnection.createAnswer(sdp);
    }

    public void setRemoteDescription(SessionDescription sdp) {

        rtcConnection.setRemoteDescription(sdp);
    }

    public void setLocalDescription(SessionDescription sdp) {
        rtcConnection.setLocalDescription(sdp);
    }

    public void addIceCandidate(IceCandidate iceCandidate) {

        rtcConnection.addIceCandidate(iceCandidate);
    }

    public void closeConnection() {

        rtcConnection.closeConnection();
    }
}
