package com.millo.test1.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by camillo on 17/04/16.
 */
public class MyReceiver extends BroadcastReceiver {

    final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.v(TAG, "there is a broadcast");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Intent pushIntent = new Intent(context, BackgroundService.class);
            //context.startService(pushIntent);
        }
        else if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
        }
        else if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
        }
    }
}