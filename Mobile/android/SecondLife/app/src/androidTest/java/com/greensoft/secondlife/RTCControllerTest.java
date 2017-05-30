package com.greensoft.secondlife;

import android.content.Context;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.greensoft.secondlife._1.LocalMediaStreamAvailableListener;
import com.greensoft.secondlife._1.PeerConnectionObserverFactory;
import com.greensoft.secondlife._1.PeerId;
import com.greensoft.secondlife._1.RTCConnectionBuilder;
import com.greensoft.secondlife._1.RTCController;
import com.greensoft.secondlife._1.SdpObserverFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 5/18/17.
 */

@RunWith(AndroidJUnit4.class)
public class RTCControllerTest implements LocalMediaStreamAvailableListener {

    private RTCConnectionBuilder rtcConnectionBuilder;
    private RTCController rtcController;

    @Before
    public void setUp() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();

        //given
        Point displaySize = new Point();
        displaySize.x = 640;
        displaySize.y = 480;
        //getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, MainActivity.VIDEO_CODEC_VP9, true, 1, MainActivity.AUDIO_CODEC_OPUS, true);

        rtcConnectionBuilder =
                new RTCConnectionBuilder(appContext, peerConnectionObserverFactory, sdpObserverFactory, params);
        rtcConnectionBuilder.attachLocalStreamAvailableListener(this);
        rtcController = new RTCController(rtcConnectionBuilder);
    }

    @After
    public void tearDown() throws Exception {

        //rtcController.removeAllPeers();
    }

    @Test
    public void when_peer_is_added_then_connector_is_initialized() throws Exception {

        //when
        final PeerId peerId = new PeerId("foo");
        rtcController.addPeer(peerId);
        //then
        assertTrue(rtcConnectionBuilder.isInitailized());
    }

    @Test(timeout=30000)
    public void when_peer_is_added_then_dispose_does_not_block() throws Exception {

        //when
        final PeerId peerId = new PeerId("foo");
        rtcController.addPeer(peerId);
        //then
        rtcController.dispose();
    }


    @Test
    public void when_all_peers_are_removed_then_connector_is_turned_off() throws Exception {

        PeerId [] peerIds = new PeerId[]{new PeerId("foo"), new PeerId("bar")};
        for(PeerId peerId: peerIds){
            rtcController.addPeer(peerId);
        }

        //when
        for(PeerId peerId: peerIds){
            rtcController.removePeer(peerId);
        }

        //then
        assertFalse(rtcConnectionBuilder.isInitailized());
    }

    SdpObserverFactory sdpObserverFactory = new SdpObserverFactory(){


        @Override
        public SdpObserver createSdpObserver(PeerId peerId) {
            return new SdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {

                }

                @Override
                public void onSetSuccess() {

                }

                @Override
                public void onCreateFailure(String s) {

                }

                @Override
                public void onSetFailure(String s) {

                }
            };
        }
    };

    PeerConnectionObserverFactory peerConnectionObserverFactory = new PeerConnectionObserverFactory(){

        @Override
        public PeerConnection.Observer createPeerConnectionObserver(PeerId peerId) {

            return new PeerConnection.Observer(){

                @Override
                public void onSignalingChange(PeerConnection.SignalingState signalingState) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onIceConnectionReceivingChange(boolean b) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onAddStream(MediaStream mediaStream) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onRemoveStream(MediaStream mediaStream) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onDataChannel(DataChannel dataChannel) {

                    int foo = 1;
                    int bar = foo;
                }

                @Override
                public void onRenegotiationNeeded() {

                    int foo = 1;
                    int bar = foo;
                }
            };
        }
    };

    @Override
    public void onLocalMediaStreamAvailable(MediaStream localMediaStream) {


    }
}
