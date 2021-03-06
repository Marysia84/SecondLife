package com.greensoft.secondlife;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.greensoft.secondlife._1.PeerId;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements RTCEventListener {


    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String AUDIO_CODEC_OPUS = "opus";

    private final static int VIDEO_CALL_SENT = 666;
    public final static int CONFIGURATION_REQUEST = 999;
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    public static final int REMOTE_X = 0;
    public static final int REMOTE_Y = 0;
    public static final int REMOTE_WIDTH = 100;
    public static final int REMOTE_HEIGHT = 100;
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv;
    private ImageButton openConfigurationImageButton;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    public WebRtcClient client;
    public String mSocketAddress;
    private String callerId;

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
        setContentView(R.layout.activity_main);
        //mSocketAddress = "http://192.168.1.10:13000/";
        mSocketAddress = "http://192.168.1.106:13000/";

        vsv = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        openConfigurationImageButton = (ImageButton)findViewById(R.id.openConfigurationImageButton);
        openConfigurationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });

        // local and remote render
        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        ConfigurationStore configurationStore = new ConfigurationStore(this);
        final Configuration configuration = configurationStore.load();
        if(configuration.PeerName.length()==0){
            openSettingsActivity();
        }
    }

    private void init() {

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        client = new WebRtcClient(this, this, mSocketAddress, params, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
        if(client != null) {
            client.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
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
    public void onCallReady(String callId) {

        try {
            client.fetchClients();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (callerId != null) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            call(callId);
        }
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
        startCam();
    }

    public void call(String callId) {
        startCam();
    }

    private void openSettingsActivity() {

        Intent i = new Intent(this, ConfigurationActivity.class);
        startActivityForResult(i, CONFIGURATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIGURATION_REQUEST) {

            if(resultCode != Activity.RESULT_OK) {
                return;
            }
            final Bundle extras = data.getExtras();
            final Configuration configuration = (Configuration)extras.getSerializable(Configuration.KEY);
            try {
                answer("ERPthmRJVZs-bLWhAAAA");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //startCam();

        }
    }

    public void startCam() {
        // Camera settings
        client.start(getDeviceName());
    }

    @Override
    public void onPeerConnected(PeerId peerId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPeerDisconnected(PeerId peerId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "DISCONNECTED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));

        boolean mirror = true;
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, mirror);
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteMediaStream, int endPoint) {
        remoteMediaStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));

        boolean mirror = true;
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, mirror);
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, mirror);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

        boolean mirror = true;
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, mirror);
    }

    @Override
    public void onPeersDownloaded(Map<String, String> clients) {

        int foo = 1;
        int bar = foo;
    }

    /** Returns the consumer friendly device Name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
