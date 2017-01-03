package id.co.ppu.collfastmon.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import id.co.ppu.collfastmon.util.ConstChat;
import id.co.ppu.collfastmon.util.NotificationUtils;

/**
 * Created by Eric on 13-Dec-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        //Displaying data in log
        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom()); //719224898380

        try {
            Intent pushNotification = new Intent(ConstChat.PUSH_NOTIFICATION);
            pushNotification.putExtra("from", remoteMessage.getFrom());

            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());  //Hello Test notification

                pushNotification.putExtra("body", remoteMessage.getNotification().getBody());
            }

            Log.e(TAG, "Data Size: " + remoteMessage.getData().size());
            if (remoteMessage.getData().size() > 0) {
                String key_from = remoteMessage.getData().get(ConstChat.KEY_FROM);
                String key_uid = remoteMessage.getData().get(ConstChat.KEY_UID);
                String key_msg = remoteMessage.getData().get(ConstChat.KEY_MESSAGE);
                String key_status = remoteMessage.getData().get(ConstChat.KEY_STATUS);
//                String key_seqno = remoteMessage.getData().get(ConstChat.KEY_SEQNO);
                String key_timestamp = remoteMessage.getData().get(ConstChat.KEY_TIMESTAMP);

                pushNotification.putExtra(ConstChat.KEY_FROM, key_from);

                // replace previous chat_msg with real data
                pushNotification.putExtra(ConstChat.KEY_UID, key_uid);

                pushNotification.putExtra(ConstChat.KEY_MESSAGE, key_msg);
                pushNotification.putExtra(ConstChat.KEY_STATUS, key_status);
//                pushNotification.putExtra(ConstChat.KEY_SEQNO, key_seqno);
                pushNotification.putExtra(ConstChat.KEY_TIMESTAMP, key_timestamp);

            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
//        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            NotificationUtils.playNotificationSound(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
