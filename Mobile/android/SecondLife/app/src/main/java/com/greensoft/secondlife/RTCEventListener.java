package com.greensoft.secondlife;

import com.greensoft.secondlife._1.PeerId;

import org.webrtc.MediaStream;

import java.util.Map;

/**
 * Created by zebul on 5/29/17.
 */

public interface RTCEventListener {

    void onCallReady(String callId);
    void onLocalStream(MediaStream localStream);
    void onAddRemoteStream(MediaStream remoteMediaStream, int endPoint);
    void onRemoveRemoteStream(int endPoint);
    void onPeersDownloaded(Map<String, String> clients);
    void onPeerConnected(PeerId remotePeerId);
    void onPeerDisconnected(PeerId remotePeerId);
}
