package com.greensoft.secondlife._1;

import org.webrtc.PeerConnection;

/**
 * Created by zebul on 5/17/17.
 */

public interface PeerConnectionObserverFactory {

    PeerConnection.Observer createPeerConnectionObserver(PeerId peerId);
}
