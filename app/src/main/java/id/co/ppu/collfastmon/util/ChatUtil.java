package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.os.AsyncTask;

import com.koushikdutta.async.future.FutureCallback;

import java.util.Date;
import java.util.List;

import id.co.ppu.collfastmon.listener.OnGetChatContactListener;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.rest.response.chat.ResponseGetOnlineContacts;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChatUtil {
    public static void updateMessageStatus(List<TrnChatMsg> messages, String messageStatus) {
        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(realm -> {
                for (TrnChatMsg _obj : messages) {
                    _obj.setMessageStatus(messageStatus);
                }
            });

        }finally {
            r.close();
        }
    }

    public static void chatSendQueueMessage(final Context ctx) {
        if (Utility.isScreenOff(ctx) || !NetUtil.isConnected(ctx))
            return;

        AsyncTask.execute(() -> {
            // Do something here on the main thread
            final Realm r = Realm.getDefaultInstance();
            try{

                // 1. check transmitting data
                final RealmResults<TrnChatMsg> anyTransmittingData = r.where(TrnChatMsg.class)
                        .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_TRANSMITTING)
                        .findAll();

                if (anyTransmittingData.size() > 0) {
//                Log.d(TAG, "There are " + anyTransmittingData.size() + " chats transmitting");
                    // wait until all chats sent to server
                    // if 5 minutes expired will be reset to unopened or firsttime
                    r.executeTransaction(realm -> {
                        for (TrnChatMsg _obj : anyTransmittingData) {
                            long minutesAge = Utility.getMinutesDiff(_obj.getCreatedTimestamp(), new Date());

                            if (minutesAge > 5) {
                                _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME);
                            }
                        }
                    });
                    return;
                }

                // 2. check pending message
                final RealmResults<TrnChatMsg> pendings = r.where(TrnChatMsg.class)
                        .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME)
                        .findAll();

                if (pendings.size() > 0) {
                    updateMessageStatus(pendings, ConstChat.MESSAGE_STATUS_TRANSMITTING);

                    APIonBuilder.sendMessages(ctx, r.copyFromRealm(pendings), (e, result) -> {

                        if (e != null) {
                            updateMessageStatus(pendings, ConstChat.MESSAGE_STATUS_FAILED);
                            return;
                        }

                        updateMessageStatus(pendings, ConstChat.MESSAGE_STATUS_SERVER_RECEIVED);

                    });

                }

            }finally{
                if (r != null)
                    r.close();
            }

        });
    }

    public static void chatGetContacts(Context ctx, final List<String> collectorsCode, final OnGetChatContactListener listener) {

        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        APIonBuilder.getChatContacts(ctx, collectorsCode, (e, result) -> {

            if (e != null) {
                if (listener != null)
                    listener.onFailure(e);
                return;
            }

            if (result == null || result.getData() == null) {
                return;
            }

            if (result.getError() != null) {
                if (listener != null) {
                    String msg = "Error (" + result.getError().getErrorCode() + ")\n" + result.getError().getErrorDesc();
                    listener.onFailure(new RuntimeException(msg));
                }

                return;
            }

            if (listener != null) {
                listener.onSuccess(result.getData());
            }
        });

    }

    public static void chatGetGroupContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {

        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        APIonBuilder.getGroupContacts(ctx, collCode, null, null, null, new FutureCallback<ResponseGetOnlineContacts>() {
            @Override
            public void onCompleted(Exception e, ResponseGetOnlineContacts result) {

                if (e != null) {
                    if (listener != null)
                        listener.onFailure(e);

                    return;
                }

                if (result == null || result.getData() == null) {
                    return;
                }

                if (result.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + result.getError().getErrorCode() + ")\n" + result.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }

                if (listener != null) {
                    listener.onSuccess(result.getData());
                }
            }
        });

    }

    public static void chatUpdateContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {

        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        APIonBuilder.getOnlineContacts(ctx, collCode, null, null, null, (e, result) -> {

            if (e != null) {
                if (listener != null)
                    listener.onFailure(e);

                return;
            }

            if (result == null || result.getData() == null) {
                return;
            }

            if (result.getError() != null) {
                if (listener != null) {
                    String msg = "Error (" + result.getError().getErrorCode() + ")\n" + result.getError().getErrorDesc();
                    listener.onFailure(new RuntimeException(msg));
                }

                return;
            }

            if (listener != null) {
                listener.onSuccess(result.getData());
            }
        });

    }

    public static void chatLogOff(Context ctx, String collCode, final OnSuccessError listener) {
        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        String userStatus = ConstChat.FLAG_OFFLINE;
        String userMsg = "Offline";

        APIonBuilder.sendStatus(ctx, collCode, userStatus, userMsg, (e, result) -> {

            if (e != null) {
                if (listener != null)
                    listener.onFailure(e);
                return;
            }

            Realm r = Realm.getDefaultInstance();
            try {
                r.beginTransaction();
                r.delete(TrnChatContact.class);
                r.commitTransaction();
            } finally {
                if (r != null)
                    r.close();
            }

            if (listener != null)
                listener.onSuccess(null);
        });

    }

    public static void chatLogOn(Context ctx, String collCode, final OnSuccessError listener) {
        // send to server that current contact is online
        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        String userStatus = ConstChat.FLAG_ONLINE;
        String userMsg = "Available";

        APIonBuilder.sendStatus(ctx, collCode, userStatus, userMsg, (e, result) -> {

            if (e != null) {
                if (listener != null)
                    listener.onFailure(e);
                return;
            }

            if (listener != null)
                listener.onSuccess(null);
        });

    }

    public static void chatCheckUserStatus(Context ctx, String collCode, final OnSuccessError listener){
        if (!NetUtil.isConnected(ctx)) {
            return;
        }

        APIonBuilder.checkUserStatus(ctx, collCode, (e, result) -> {

            if (e != null) {
                if (listener != null)
                    listener.onFailure(e);
                return;
            }

            if (listener != null)
                listener.onSuccess(result.getAsString());
        });
    }

}
