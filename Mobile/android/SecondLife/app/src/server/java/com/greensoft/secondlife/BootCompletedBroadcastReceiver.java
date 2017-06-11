package com.greensoft.secondlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by zebul on 6/8/17.
 */

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Intent startClockActivityIntent = new Intent(context, ClockActivity.class);
        startClockActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startClockActivityIntent);
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent startClockActivityIntent = new Intent(context, ClockActivity.class);
                context.startActivity(startClockActivityIntent);
            }
        }, 60*1000);
        */
    }
}
