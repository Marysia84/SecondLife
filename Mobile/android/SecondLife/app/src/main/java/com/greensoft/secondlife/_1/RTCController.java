package com.greensoft.secondlife._1;

import com.greensoft.secondlife.RTCEventListener;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 5/16/17.
 */

public class RTCController {

    private Peers peers = new Peers();
    private RTCConnectionBuilder rtcConnectionBuilder;

    public RTCController(RTCConnectionBuilder rtcConnectionBuilder){

        this.rtcConnectionBuilder = rtcConnectionBuilder;
    }

    public List<Peer> restart(){

        final List<Peer> peers = removeAllPeers();
        rtcConnectionBuilder.dispose();
        return peers;
    }

    public boolean containsPeer(PeerId peerId){

        return peers.contains(peerId);
    }

    public void addPeer(PeerId peerId){

        Peer peer = createPeer(peerId);
        peers.add(peer);
    }

    public void removePeer(PeerId peerId)
            throws Peers.PeerNotExistsExeption {

        Peer peer = peers.remove(peerId);
        /*
        if(peers.isEmpty()){
            rtcConnectionBuilder.dispose();
        }*/
    }

    public List<Peer> removeAllPeers() {

        List<Peer> listOfPeers = peers.removeAll();
        //rtcConnectionBuilder.dispose();
        return listOfPeers;
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
