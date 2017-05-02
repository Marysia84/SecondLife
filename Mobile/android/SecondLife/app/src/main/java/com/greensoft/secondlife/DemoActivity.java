package com.greensoft.secondlife;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.webrtc.MediaStream;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.greensoft.secondlife.MainActivity.AUDIO_CODEC_OPUS;
import static com.greensoft.secondlife.MainActivity.CONFIGURATION_REQUEST;
import static com.greensoft.secondlife.MainActivity.VIDEO_CODEC_VP9;

public class DemoActivity extends FragmentActivity
        implements WebRtcClient.RtcListener{

    private RelativeLayout progressBarRelativeLayout;
    private RelativeLayout viewPagerRelativeLayout;
    private ViewPager cameraViewPager;
    private CameraPagerAdapter cameraPagerAdapter;
    private ImageButton openConfigurationImageButton;

    private WebRtcClient client;
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
        init();
    }

    private void openSettingsActivity() {

        Intent i = new Intent(this, ConfigurationActivity.class);
        startActivityForResult(i, CONFIGURATION_REQUEST);
    }

    private void init() {

        ConfigurationStore configurationStore = new ConfigurationStore(this);
        final Configuration configuration = configurationStore.load();

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        client = new WebRtcClient(this, this, configuration.ServerAddress, params, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(client != null) {
            client.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(client != null) {
            client.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if(client != null) {
            client.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onCallReady(String peerId) {

        this.peerId = peerId;
        try {
            client.fetchClients();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String newStatus) {

    }

    @Override
    public void onLocalStream(MediaStream localStream) {

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

        cameraPagerAdapter.updateRemoteStream(remoteStream);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

    }

    @Override
    public void onClientsFetched(final Map<String, String> clients) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int size = clients.size();
                if(size > 0){

                    cameraPagerAdapter.updateContent(clients);
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

    public void requestRemoteStream(String remotePeerId) throws JSONException {
        client.sendMessage(remotePeerId, "init", null);
    }

    class CameraPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener{

        private List<DemoFragment> demoFragmentList = new LinkedList<>();

        public CameraPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return demoFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return demoFragmentList.get(position);
        }


        public void updateContent(Map<String, String> clients) {

            demoFragmentList.clear();
            //cameraViewPager.setCurrentItem(0);
            final Set<Map.Entry<String, String>> clientEntries = clients.entrySet();
            for(Map.Entry<String, String> clientEntry :clientEntries){

                final String remotePeerId = clientEntry.getKey();
                demoFragmentList.add(DemoFragment.newInstance(remotePeerId));
            }

            notifyDataSetChanged();
        }

        public void updateRemoteStream(MediaStream remoteStream) {

            final int currentItem = cameraViewPager.getCurrentItem();
            final DemoFragment demoFragment = demoFragmentList.get(currentItem);
            demoFragment.onRemoteStream(remoteStream);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            int foo = 1;
            int bar = foo;
        }

        @Override
        public void onPageSelected(int position) {

            final DemoFragment demoFragment = demoFragmentList.get(position);
            demoFragment.start();

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            int foo = 1;
            int bar = foo;
        }
    }
}
