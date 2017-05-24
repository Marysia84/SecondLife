package com.greensoft.secondlife._1;

import org.webrtc.SdpObserver;

/**
 * Created by zebul on 5/17/17.
 */

public interface SdpObserverFactory {

    SdpObserver createSdpObserver(PeerId peerId);
}
