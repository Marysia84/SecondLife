package com.greensoft.secondlife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.greensoft.secondlife.MainActivity.CONFIGURATION_REQUEST;

public class ClockActivity extends AppCompatActivity {

    private boolean serviceBound = false;
    private SecondLifeService secondLifeService;
    private TextView digitalClock;
    private ImageButton openConfigurationImageButton;

    private Handler handler;
    private Runnable ticker;
    private int previousMinute = -1;
    private Configuration configuration;

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
        setContentView(R.layout.activity_clock);
        digitalClock = (TextView)findViewById(R.id.digitalClock);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/digital-7.ttf");
        digitalClock.setTypeface(tf);
        openConfigurationImageButton = (ImageButton)findViewById(R.id.openConfigurationImageButton);
        openConfigurationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSettingsActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateFontSize(100);
        configuration = new ConfigurationStore(this).load();
        if(!configuration.ManageClockVisiblity){
            digitalClock.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTickPerSecond();
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTickPerSecond();
    }

    private void startTickPerSecond()
    {
        handler=new Handler();
        ticker = new Runnable()
        {
            public void run()
            {
                updateClock();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                handler.postAtTime(ticker, next);
            }
        };
        ticker.run();
    }

    private void stopTickPerSecond() {

        if(handler!=null)
        {
            handler.removeCallbacks(ticker);
        }
    }

    private void updateClock() {

        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);

        boolean minuteChanged = currentMinute != previousMinute;
        previousMinute = currentMinute;
        boolean hmOnly = true;
        SimpleDateFormat timeFormatter = new SimpleDateFormat(hmOnly ? "HH:mm" : "HH:mm:ss");
        if(minuteChanged){

            if(configuration.ManageClockVisiblity){

                final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                if(6<= hourOfDay && hourOfDay <22){
                    ensureClockVisible();
                }
                else{
                    ensureClockInvisible();
                }
            }

            int seconds = calendar.get(Calendar.SECOND);
            calendar.set(Calendar.SECOND, 0);
            //get rid of this shit
            final String textToUpdateFontSize = timeFormatter.format(calendar.getTime());
            updateMaxFontSize(digitalClock, "55:55");
            calendar.set(Calendar.SECOND, seconds);
        }

        final String currentTime = timeFormatter.format(calendar.getTime());
        if(hmOnly){
            if(minuteChanged){
                digitalClock.setText(currentTime);
            }
        }
        else{

            digitalClock.setText(currentTime);
        }
    }

    private void ensureClockVisible() {

        if(digitalClock.getVisibility() != View.VISIBLE){
            digitalClock.setVisibility(View.VISIBLE);
        }
    }

    private void ensureClockInvisible() {

        if(digitalClock.getVisibility() != View.INVISIBLE){
            digitalClock.setVisibility(View.INVISIBLE);
        }
    }

    protected char findBiggestDigit(){

        return 'a';
    }

    protected static void updateMaxFontSize(TextView view, String viewText){

        final int maxWidthPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
        final int maxHeightPixels = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();

        Paint paint = new Paint();
        paint.setTypeface(view.getTypeface());

        float fontSizeSP = pixelsToSp(view.getContext(), view.getTextSize());
        paint.setTextSize(spToPixels(view.getContext(), fontSizeSP));

        Rect bounds = new Rect();
        paint.getTextBounds(viewText, 0, viewText.length(), bounds);
        int width = bounds.width();
        int height = bounds.height();

        //float widthPixels = paint.measureText(viewText);

        while((width<maxWidthPixels) && (height<maxHeightPixels)){
            paint.setTextSize(spToPixels(view.getContext(), ++fontSizeSP));
            paint.getTextBounds(viewText, 0, viewText.length(), bounds);
            width = bounds.width();
            height = bounds.height();
        }

        while((width>=maxWidthPixels) || (height>=maxHeightPixels)){
            paint.setTextSize(spToPixels(view.getContext(), --fontSizeSP));
            paint.getTextBounds(viewText, 0, viewText.length(), bounds);
            width = bounds.width();
            height = bounds.height();
        }

        view.setTextSize(--fontSizeSP);

        /*
        // Increase font size if necessary.
        if (widthPixels < maxWidthPixels){
            while (widthPixels < maxWidthPixels){
                ++fontSizeSP;
                paint.setTextSize(spToPixels(view.getContext(), fontSizeSP));
                widthPixels = paint.measureText(viewText);
            }
            --fontSizeSP;
        }
        // Decrease font size if necessary.
        else {
            while (widthPixels > maxWidthPixels) {
                --fontSizeSP;
                paint.setTextSize(spToPixels(view.getContext(),fontSizeSP));
                widthPixels = paint.measureText(viewText);
            }
        }
        */
        //paint.getTextBounds();



        //view.setTextSize(fontSizeSP);
    }

    private static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    private static float spToPixels(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, SecondLifeService.class);
        startService(intent);
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);
        makeSureServiceIsAlive();
    }

    private void makeSureServiceIsAlive() {

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, SecondLifeService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every 20 seconds
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                10* 1000, pintent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SecondLifeService.SecondLifeServiceBinder binder = (SecondLifeService.SecondLifeServiceBinder) service;
            secondLifeService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    private void openSettingsActivity() {

        Intent i = new Intent(this, ConfigurationActivity.class);
        startActivityForResult(i, CONFIGURATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIGURATION_REQUEST) {

            /*
            if(resultCode != Activity.RESULT_OK) {
                return;
            }
            final Bundle extras = data.getExtras();
            final Configuration configuration = (Configuration)extras.getSerializable(Configuration.KEY);
            */
        }
    }
}
