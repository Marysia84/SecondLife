package com.greensoft.secondlife;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by zebul on 4/19/17.
 */

public class SecondLifeService extends Service
        implements WebRtcClient.RtcListener{

    private static final int EXECUTE_COMMAND = 1;
    private static final int SERVICE_NOTIFICATION_ID = 1;
    private static final String SERVICE_NAME = SecondLifeService.class.getSimpleName();

    private String callerId;
    private WebRtcClient client;
    private String mSocketAddress = "http://192.168.1.10:13000/";
    private SecondLifeServiceBinder secondLifeServiceBinder = new SecondLifeServiceBinder();

    public class SecondLifeServiceBinder extends Binder {
        SecondLifeService getService() {
            return SecondLifeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return secondLifeServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        keepInForeground();
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
    public void onDestroy() {

        if(client != null) {
            client.onDestroy();
        }
        super.onDestroy();
    }

    private void init() {

        Point displaySize = new Point();
        displaySize.x = 640;
        displaySize.y = 480;
        //getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, MainActivity.VIDEO_CODEC_VP9, true, 1, MainActivity.AUDIO_CODEC_OPUS, true);

        client = new WebRtcClient(this, mSocketAddress, params, null);
    }

    @Override
    public void onCallReady(String callId) {
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

    public void startCam() {
        // Camera settings
        client.start("android_test1");
    }

    @Override
    public void onStatusChanged(final String newStatus) {

        showTextMessageAsToast(newStatus);
    }

    @Override
    public void onLocalStream(MediaStream localStream) {

        int foo = 1;
        int bar = foo;
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

        int foo = 1;
        int bar = foo;
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

        int foo = 1;
        int bar = foo;
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
