package id.co.ppu.collfastmon.fcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import id.co.ppu.collfastmon.util.ConstChat;

/**
 * unused
 * Created by Eric on 23-Dec-16.
 */

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {
    private final String TAG = "FirebaseDataReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "I'm in!!!");

        String dataBundle = intent.getStringExtra(ConstChat.KEY_FROM);
        Log.e(TAG, dataBundle);

        Intent in= new Intent(context, MyFirebaseMessagingService.class);
//        in.putExtra("sender",smsFrom)
        in.putExtras(intent);
        // Start the service, keeping the device awake while it is launching.
        ComponentName comp = new ComponentName(context.getPackageName(),
                MyFirebaseMessagingService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

    }
}
