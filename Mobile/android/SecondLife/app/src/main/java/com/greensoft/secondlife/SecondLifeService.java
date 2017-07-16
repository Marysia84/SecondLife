package com.greensoft.secondlife;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.greensoft.log.LogSubscriber;
import com.greensoft.log.Logger;
import com.greensoft.log.subscribers.http.HttpServerLogger;
import com.greensoft.secondlife._1.PeerId;
import com.greensoft.secondlife._1.RTCOrchestrator;

import org.json.JSONException;
import org.webrtc.MediaStream;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zebul on 4/19/17.
 */

public class SecondLifeService extends Service
        implements RTCEventListener {

    private static final int EXECUTE_COMMAND = 1;
    private static final int SERVICE_NOTIFICATION_ID = 1;
    private static final String SERVICE_NAME = SecondLifeService.class.getSimpleName();
    private static final String TAG = SERVICE_NAME;

    private String callerId;
    //private WebRtcClient client;
    private RTCOrchestrator rtcOrchestrator;
    private SecondLifeServiceBinder secondLifeServiceBinder = new SecondLifeServiceBinder();
    private List<Restartable> restartables = new LinkedList<Restartable>();
    public class SecondLifeServiceBinder extends Binder {
        SecondLifeService getService() {
            return SecondLifeService.this;
        }
    }

    /** Command to the service to display a message */
    static final int MSG_SAY_HELLO = 1;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    /*
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return secondLifeServiceBinder;
    }
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        keepInForeground();
        //Toast.makeText(this, "SecondLifeService.onStartCommand", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    private void keepInForeground() {

        Context appContext = getApplicationContext();
        Notification notification = createServiceNotification(appContext);
        startForeground(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public void onCreate() {

        super.onCreate();
        init();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        //Toast.makeText(this, "SecondLifeService.onStart", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {

        uninit();
        super.onDestroy();
    }

    private void init() {

        ConfigurationStore configurationStore = new ConfigurationStore(this);
        Configuration configuration = configurationStore.load();
        showTextMessageAsToast("Service will connect to: "+configuration.ServerAddress);
        //configuration

        if(com.greensoft.secondlife.AppConfiguration.ENABLE_LOG_CAT){
            LogSubscriber logSubscriber = new LogCatSubscriber();
            Logger.addLogSubscriber(logSubscriber);
        }

        if(com.greensoft.secondlife.AppConfiguration.ENABLE_HTTP_LOGGER){
            AssetHttpFileReader httpFileReader = new AssetHttpFileReader(this);
            final HttpServerLogger httpServerLogger = new HttpServerLogger(8888, httpFileReader, 100);
            Logger.addLogSubscriber(httpServerLogger);
            restartables.add(new Restartable() {
                @Override
                public void start() {
                    try {
                        Logger.i(TAG, "httpServerLogger is starting");
                        httpServerLogger.start();
                        Logger.i(TAG, "httpServerLogger started");
                    } catch (IOException exc_) {

                        Logger.removeLogSubscriber(httpServerLogger);
                        Logger.e(TAG, exc_);
                    }
                }

                @Override
                public void stop() {

                    try{
                        httpServerLogger.stop();
                    } catch (Exception exc_) {

                        Logger.e(TAG, exc_);
                    }
                }
            });
        }

        Point displaySize = new Point();
        displaySize.x = 640;
        displaySize.y = 480;
        //getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                displaySize.x, displaySize.y, 30, 1, MainActivity.VIDEO_CODEC_VP9, true, 1, MainActivity.AUDIO_CODEC_OPUS, true);

        //client = new WebRtcClient(this, this, configuration.ServerAddress, params, null);

        params.Host = configuration.ServerAddress;
        rtcOrchestrator = new RTCOrchestrator(this, params);
        rtcOrchestrator.attachRTCEventListener(this);
        restartables.add(rtcOrchestrator);

        for(Restartable lifecycle: restartables){
            lifecycle.start();
        }

        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int streamMaxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, streamMaxVolume, 0);
    }

    private void uninit() {
        /*
        if(client != null) {
            client.onDestroy();
        }*/
        Collections.reverse(restartables);//dispose in reverse order
        for(Restartable lifecycle: restartables){
            lifecycle.stop();
        }
    }

    @Override
    public void onCallReady(String callId) {

        showTextMessageAsToast("onCallReady: "+callId);
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
        //client.sendMessageToRemotePeer(callerId, "init", null);
        startCam();
    }

    public void call(String callId) {
        startCam();
    }

    public void startCam() {
        // Camera settings
        //client.start(getDeviceName());
    }

    @Override
    public void onPeerConnected(PeerId remotePeerId) {

        Message message = handler.obtainMessage(EXECUTE_COMMAND);
        message.obj = new InitConnectionCommand(remotePeerId);
        message.sendToTarget();
        showTextMessageAsToast("onPeerConnected:"+remotePeerId.getId());
    }

    @Override
    public void onPeerDisconnected(PeerId remotePeerId) {

        /*
            try {
                rtcOrchestrator.sendMessageToRemotePeer(remotePeerId, "init", null);
            } catch (JSONException e) {
                Logger.e(TAG, "onPeerConnected:"+e.getMessage());
            }*/
        showTextMessageAsToast("onPeerDisconnected");
    }

    @Override
    public void onLocalStream(MediaStream localStream) {

        int foo = 1;
        int bar = foo;
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteMediaStream, int endPoint) {

        showTextMessageAsToast("onAddRemoteStream");
        Logger.i(TAG, "onAddRemoteStream");
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

        showTextMessageAsToast("onRemoveRemoteStream");
        Logger.i(TAG, "onRemoveRemoteStream");
    }

    @Override
    public void onPeersDownloaded(Map<String, String> clients) {

    }

    final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == EXECUTE_COMMAND){

                Command command = (Command)msg.obj;
                command.execute();
            }
            return false;
        }
    });

    interface Command{

        void execute();
    }

    class InitConnectionCommand implements Command{

        private PeerId remotePeerId;
        InitConnectionCommand(PeerId remotePeerId){
            this.remotePeerId = remotePeerId;
        }
        @Override
        public void execute() {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        rtcOrchestrator.sendMessageToRemotePeer(remotePeerId, "init", null);
                    } catch (JSONException e) {
                        Logger.e(TAG, "onPeerConnected:"+e.getMessage());
                    }
                }
            }, 5000);
        }
    }

    class ShowToastCommand implements Command{

        private Context context;
        private String textMessage;

        ShowToastCommand(Context context, String textMessage){

            this.context = context;
            this.textMessage = textMessage;
        }

        @Override
        public void execute() {

            Toast.makeText(context, textMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void showTextMessageAsToast(String textMessage){

        Message message = handler.obtainMessage(EXECUTE_COMMAND);
        message.obj = new ShowToastCommand(this, textMessage);
        message.sendToTarget();
    }

    public Notification createServiceNotification(Context appContext_){

        Intent intent = new Intent(appContext_, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_videocam_white_18dp)
                        .setContentTitle( SERVICE_NAME )
                        .setContentText( "Beware you are spied" )
                        .setContentIntent(pendingIntent);

        Notification notification =  notificationBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }
}
