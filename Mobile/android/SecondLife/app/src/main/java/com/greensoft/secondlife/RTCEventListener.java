package com.greensoft.secondlife;

import org.webrtc.MediaStream;

import java.util.Map;

/**
 * Created by zebul on 5/29/17.
 */

public interface RTCEventListener {

    void onCallReady(String callId);
    void onStatusChanged(String newStatus);
    void onLocalStream(MediaStream localStream);
    void onAddRemoteStream(MediaStream remoteStream, int endPoint);
    void onRemoveRemoteStream(int endPoint);
    void onClientsFetched(Map<String, String> clients);
}
