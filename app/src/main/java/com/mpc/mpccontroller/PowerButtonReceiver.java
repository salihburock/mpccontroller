package com.mpc.mpccontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class PowerButtonReceiver extends BroadcastReceiver {

    private static final long SHORT_PRESS_THRESHOLD = 500; // 500 milliseconds
    private long lastScreenOffTime = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PowerButtonReceiver", "Received intent: " + intent.getAction());

        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            lastScreenOffTime = SystemClock.elapsedRealtime();
            Log.d("PowerButtonReceiver", "Screen turned off");
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            if (lastScreenOffTime != -1) {
                long timeElapsed = SystemClock.elapsedRealtime() - lastScreenOffTime;
                if (timeElapsed < SHORT_PRESS_THRESHOLD) {
                    // Detected a short press
                    Log.d("PowerButtonReceiver", "Short power button press detected.");
                }
                lastScreenOffTime = -1;
            }
        }
    }

}
