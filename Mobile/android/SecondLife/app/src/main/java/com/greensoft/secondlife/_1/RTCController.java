package com.greensoft.secondlife._1;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by zebul on 5/16/17.
 */

public class RTCController {

    private Peers peers = new Peers();
    private RTCConnectionBuilder rtcConnectionBuilder;

    public RTCController(RTCConnectionBuilder rtcConnectionBuilder){

        this.rtcConnectionBuilder = rtcConnectionBuilder;
    }

    public void restart(){

        rtcConnectionBuilder.turnOff();
        rtcConnectionBuilder.turnOn();
    }

    public boolean containsPeer(PeerId peerId){

        return peers.contains(peerId);
    }

    public void addPeer(PeerId peerId){

        if(!rtcConnectionBuilder.isTurnedOn()){
            rtcConnectionBuilder.turnOn();
        }

        Peer peer = createPeer(peerId);
        peers.add(peer);
    }

    public void removePeer(PeerId peerId)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.remove(peerId);
        peer.closeConnection();
        if(peers.isEmpty()){
            rtcConnectionBuilder.turnOff();
        }
    }

    public void createOffer(PeerId peerId)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.getPeer(peerId);
        peer.createOffer();
    }

    public void createAnswer(PeerId peerId, SessionDescription sdp)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.getPeer(peerId);
        peer.createAnswer(sdp);
    }

    public void setRemoteDescription(PeerId peerId, SessionDescription sdp)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.getPeer(peerId);
        peer.setRemoteDescription(sdp);
    }

    public void setLocalDescription(PeerId peerId, SessionDescription sdp)
            throws Peers.PeerNotExistsExeption {
        Peer peer = peers.getPeer(peerId);
        peer.setLocalDescription(sdp);
    }

    public void addIceCandidate(PeerId peerId, IceCandidate iceCandidate)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.getPeer(peerId);
        peer.addIceCandidate(iceCandidate);
    }

    private Peer createPeer(PeerId peerId) {

        RTCConnection rtcConnection = rtcConnectionBuilder.buildRTCConnection(peerId);
        Peer peer = new Peer(peerId, rtcConnection);
        return peer;
    }
}
