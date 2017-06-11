package com.greensoft.secondlife;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.greensoft.secondlife._1.PeerId;
import com.greensoft.secondlife._1.RTCOrchestrator;

import org.json.JSONException;
import org.webrtc.MediaStream;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.greensoft.secondlife.MainActivity.AUDIO_CODEC_OPUS;
import static com.greensoft.secondlife.MainActivity.CONFIGURATION_REQUEST;
import static com.greensoft.secondlife.MainActivity.VIDEO_CODEC_VP9;

public class CameraViewActivity extends FragmentActivity
        implements RTCEventListener {

    private RelativeLayout progressBarRelativeLayout;
    private RelativeLayout viewPagerRelativeLayout;
    private ViewPager cameraViewPager;
    private CameraPagerAdapter cameraPagerAdapter;
    private ImageButton openConfigurationImageButton;

    private RTCOrchestrator rtcOrchestrator = null;
    private String peerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_demo);

        progressBarRelativeLayout = (RelativeLayout)findViewById(R.id.progressBarRelativeLayout);
        viewPagerRelativeLayout = (RelativeLayout)findViewById(R.id.viewPagerRelativeLayout);
        cameraViewPager = (ViewPager)findViewById(R.id.cameraViewPager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        cameraPagerAdapter = new CameraPagerAdapter(fragmentManager);
        cameraViewPager.setAdapter(cameraPagerAdapter);
        cameraViewPager.addOnPageChangeListener(cameraPagerAdapter);
        openConfigurationImageButton = (ImageButton)findViewById(R.id.openConfigurationImageButton);
        openConfigurationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });
        setUpRTC();
    }

    private void openSettingsActivity() {

        Intent i = new Intent(this, ConfigurationActivity.class);
        startActivityForResult(i, CONFIGURATION_REQUEST);
    }

    private void setUpRTC() {

        ConfigurationStore configurationStore = new ConfigurationStore(this);
        final Configuration configuration = configurationStore.load();

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        params.Host = configuration.ServerAddress;
        rtcOrchestrator = new RTCOrchestrator(this, params);
        rtcOrchestrator.attachRTCEventListener(this);
        rtcOrchestrator.start();
    }

    private void showTextMessageAsToast(final String textMessage){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraViewActivity.this, textMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {

        tearDownRTC();
        super.onDestroy();
    }

    private void tearDownRTC() {

        rtcOrchestrator.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        rtcOrchestrator.turnOnVideoSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        rtcOrchestrator.turnOffVideoSource();
    }

    @Override
    public void onCallReady(String peerId) {

        this.peerId = peerId;
        try {
            rtcOrchestrator.downloadPeers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerConnected(PeerId peerId) {

        showTextMessageAsToast("onPeerConnected: "+peerId.getId());
    }

    @Override
    public void onPeerDisconnected(PeerId peerId) {

        showTextMessageAsToast("onPeerDisconnected: "+peerId.getId());
    }

    @Override
    public void onLocalStream(MediaStream localStream) {

        showTextMessageAsToast("onLocalStream");
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteMediaStream, int endPoint) {

        showTextMessageAsToast("onAddRemoteStream");
        cameraPagerAdapter.updateRemoteStream(remoteMediaStream);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

        showTextMessageAsToast("onRemoveRemoteStream");
    }

    @Override
    public void onPeersDownloaded(final Map<String, String> peers) {

        showTextMessageAsToast("onPeersDownloaded");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int size = peers.size();
                if(size > 0){

                    cameraPagerAdapter.updateContent(peers);
                    progressBarRelativeLayout.setVisibility(View.GONE);
                    viewPagerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else{

                    progressBarRelativeLayout.setVisibility(View.VISIBLE);
                    viewPagerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    public void requestRemoteStream(PeerId remotePeerId) throws JSONException {
        rtcOrchestrator.sendMessageToRemotePeer(remotePeerId, "init", null);
    }

    class CameraPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener{

        private List<CameraFragment> cameraFragmentList = new LinkedList<>();

        public CameraPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return cameraFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return cameraFragmentList.get(position);
        }


        public void updateContent(Map<String, String> clients) {

            cameraFragmentList.clear();
            //cameraViewPager.setCurrentItem(0);
            final Set<Map.Entry<String, String>> clientEntries = clients.entrySet();
            for(Map.Entry<String, String> clientEntry :clientEntries){

                final String remotePeerId = clientEntry.getKey();
                cameraFragmentList.add(CameraFragment.newInstance(remotePeerId));
            }

            notifyDataSetChanged();
        }

        public void updateRemoteStream(MediaStream remoteStream) {

            final int currentItem = cameraViewPager.getCurrentItem();
            final CameraFragment cameraFragment = cameraFragmentList.get(currentItem);
            cameraFragment.onRemoteStream(remoteStream);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            int foo = 1;
            int bar = foo;
        }

        @Override
        public void onPageSelected(int position) {

            final CameraFragment cameraFragment = cameraFragmentList.get(position);
            cameraFragment.start();

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            int foo = 1;
            int bar = foo;
        }
    }
}
