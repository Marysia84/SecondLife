package com.greensoft.secondlife;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Calendar;

import static com.greensoft.secondlife.MainActivity.CONFIGURATION_REQUEST;

public class ClockActivity extends AppCompatActivity {

    private boolean serviceBound = false;
    private SecondLifeService secondLifeService;
    private DigitalClock digitalClock;
    private ImageButton openConfigurationImageButton;

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
        digitalClock = (DigitalClock)findViewById(R.id.digitalClock);
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

        digitalClock.addTextChangedListener(textAutoResizeWatcher(digitalClock, 98, 256));
    }

    private TextWatcher textAutoResizeWatcher(final TextView view, final int MIN_SP, final int MAX_SP){

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {

                final int widthLimitPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
                Paint paint = new Paint();
                float fontSizeSP = pixelsToSp(view.getTextSize());
                paint.setTextSize(spToPixels(fontSizeSP));

                String viewText = view.getText().toString();

                float widthPixels = paint.measureText(viewText);

                // Increase font size if necessary.
                if (widthPixels < widthLimitPixels){
                    while (widthPixels < widthLimitPixels && fontSizeSP <= MAX_SP){
                        ++fontSizeSP;
                        paint.setTextSize(spToPixels(fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                    --fontSizeSP;
                }
                // Decrease font size if necessary.
                else {
                    while (widthPixels > widthLimitPixels || fontSizeSP > MAX_SP) {
                        if (fontSizeSP < MIN_SP) {
                            fontSizeSP = MIN_SP;
                            break;
                        }
                        --fontSizeSP;
                        paint.setTextSize(spToPixels(fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                }

                view.setTextSize(fontSizeSP);
            }
        };
    }

    private float pixelsToSp(float px) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    private float spToPixels(float sp) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
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
