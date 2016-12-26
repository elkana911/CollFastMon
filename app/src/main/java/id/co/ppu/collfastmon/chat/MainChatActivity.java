package id.co.ppu.collfastmon.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.fragments.FragmentChatActiveContacts;
import id.co.ppu.collfastmon.fragments.FragmentChatAllContacts;
import id.co.ppu.collfastmon.fragments.FragmentChatWith;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatMsg;
import id.co.ppu.collfastmon.rest.request.chat.RequestChatStatus;
import id.co.ppu.collfastmon.rest.request.chat.RequestGetChatHistory;
import id.co.ppu.collfastmon.rest.response.chat.ResponseGetChatHistory;
import id.co.ppu.collfastmon.util.ConstChat;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.NotificationUtils;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChatActivity extends BasicActivity implements FragmentChatActiveContacts.OnChatContactsListener, FragmentChatWith.OnChatWithListener, FragmentChatAllContacts.OnContactSelectedListener {

    public static final String PARAM_USER_CODE = "user.code";

    private String TAG = "MainChatActivity";
    private String userCode = "21080093";

    private BroadcastReceiver broadcastReceiver;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        handleNotification(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
//            return;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // to test push notification via fcm
                // use https://console.firebase.google.com/project/concise-clock-149708/notification

                // checking for type intent filter
                /*
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else */
                if (intent.getAction().equals(ConstChat.PUSH_NOTIFICATION)) {
/*
                    String from = intent.getStringExtra("from");
                    String message = intent.getStringExtra("message");
                    String chatFrom = intent.getStringExtra("chat_from");
                    String chatMessage = intent.getStringExtra("chat_msg");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
*/
                    handleNotification(intent);

//                    txtMessage.setText(message);
                }
            }
        };

        this.userCode = extras.getString(PARAM_USER_CODE);
//        this.collName = extras.getString(PARAM_COLLNAME);
//        this.ldvNo = extras.getString(PARAM_LDV_NO);
//        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_chat);
//            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Realm r = Realm.getDefaultInstance();
        try {
            r.beginTransaction();
            r.delete(TrnChatContact.class);
            r.commitTransaction();

        } finally {
            if (r != null) {
                r.close();
            }
        }

        Fragment fr = new FragmentChatActiveContacts();

        Bundle bundle = new Bundle();
        bundle.putString(FragmentChatActiveContacts.PARAM_USERCODE, this.userCode);
        fr.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fr);
        ft.commit();

    }

    private void handleNotification(Intent intent) {

        if (intent == null)
            return;

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e(TAG, "No extras found");
            return;
        }

        final String key_from = intent.getStringExtra(ConstChat.KEY_FROM);
        final String key_uid = intent.getStringExtra(ConstChat.KEY_UID);
        final String key_msg = intent.getStringExtra(ConstChat.KEY_MESSAGE);
        final String key_status = intent.getStringExtra(ConstChat.KEY_STATUS);
        final String key_seqno = intent.getStringExtra(ConstChat.KEY_SEQNO);
        final String key_timestamp = intent.getStringExtra(ConstChat.KEY_TIMESTAMP);

        Log.e(TAG, "chatFrom:" + key_from + "\nchatMessage:" + key_msg + "\nchatUid: " + key_uid);

        // jika key_status = 1 maka update ke server kalo udah diterima dan dibuka
        if (TextUtils.isEmpty(key_status)) {
            return;
        }

        if (key_status.equalsIgnoreCase(ConstChat.MESSAGE_STATUS_SERVER_RECEIVED)) {
            // tell sender your message has been open and read, tp masalahnya layar mati jg kesini
            Call<ResponseBody> call = getAPIService().updateMessageStatus(key_uid, Utility.isScreenOff(this) ? ConstChat.MESSAGE_STATUS_DELIVERED : ConstChat.MESSAGE_STATUS_READ_AND_OPENED);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } else if (key_status.equalsIgnoreCase(ConstChat.MESSAGE_STATUS_READ_AND_OPENED)) {
            // update db
            final TrnChatMsg trnChatMsg = this.realm.where(TrnChatMsg.class)
                    .equalTo("uid", key_uid)
                    .findFirst();

            if (trnChatMsg != null) {

                this.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        trnChatMsg.setMessageStatus(key_status);
                        realm.copyToRealmOrUpdate(trnChatMsg);
                    }
                });
            } else {
                Call<ResponseGetChatHistory> call = getAPIService().getMessage(key_uid);
                call.enqueue(new Callback<ResponseGetChatHistory>() {
                    @Override
                    public void onResponse(Call<ResponseGetChatHistory> call, Response<ResponseGetChatHistory> response) {
                        if (response.isSuccessful()) {
                            final ResponseGetChatHistory body = response.body();

                            if (body != null) {

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealm(body.getData());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseGetChatHistory> call, Throwable t) {

                    }
                });

            }
        } else if (key_status.equalsIgnoreCase(ConstChat.MESSAGE_STATUS_ALL_READ_AND_OPENED)) {
            this.realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TrnChatMsg> unreadMessages = realm.where(TrnChatMsg.class)
                            .equalTo("fromCollCode", userCode)
                            .equalTo("toCollCode", key_from)
                            .notEqualTo("messageStatus", ConstChat.MESSAGE_STATUS_READ_AND_OPENED)
                            .findAll();

                    if (unreadMessages.size() < 1)
                        return;

                    for (TrnChatMsg msg : unreadMessages) {
                        msg.setMessageStatus(ConstChat.MESSAGE_STATUS_READ_AND_OPENED);
                        realm.copyToRealmOrUpdate(msg);
                    }

                }
            });

        }

        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag instanceof FragmentChatActiveContacts) {
            TrnChatContact contact = this.realm.where(TrnChatContact.class)
                    .equalTo("collCode", key_from)
                    .findFirst();

            if (contact == null) {
                List<String> collsCode = new ArrayList<>();
                collsCode.add(this.userCode);
                collsCode.add(key_from);

                NetUtil.chatGetContacts(MainChatActivity.this, collsCode, new OnSuccessError() {
                    @Override
                    public void onSuccess(String msg) {
                        TrnChatContact contact = realm.where(TrnChatContact.class)
                                .equalTo("collCode", key_from)
                                .findFirst();

                        String val1 = userCode;
                        String val2 = contact.getCollCode();

                        onContactSelected(contact);

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSkip() {

                    }
                });
            }

        } else if (frag instanceof FragmentChatWith) {

            NotificationUtils.clearNotifications(this);

            if (!TextUtils.isEmpty(key_uid))
                this.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        TrnChatMsg msg = realm.where(TrnChatMsg.class)
                                .equalTo("uid", key_uid)
                                .findFirst();

                        if (msg == null) {
                            msg = new TrnChatMsg();
                            msg.setUid(key_uid);
                            msg.setSeqNo(Long.parseLong(key_seqno));
                            msg.setFromCollCode(((FragmentChatWith) frag).userCode2);
                            msg.setToCollCode(((FragmentChatWith) frag).userCode1);
                            msg.setMessage(key_msg);
                            msg.setMessageType(ConstChat.MESSAGE_TYPE_COMMON);
                            msg.setCreatedTimestamp(Utility.convertStringToDate(key_timestamp, "yyyyMMddHHmmss"));

                        } else {
                        }
                    msg.setMessageStatus(ConstChat.MESSAGE_STATUS_READ_AND_OPENED);
                    realm.copyToRealmOrUpdate(msg);
                }
            });

            ((FragmentChatWith) frag).listAdapter.notifyDataSetChanged();
            ((FragmentChatWith) frag).scrollToLast();
//            chats.scrollToPosition(listAdapter.getItemCount());
        }

//        String chatFrom = intent.getStringExtra("chat_from");
//        String chatMessage = intent.getStringExtra("chat_msg");
//        String chatTime = intent.getStringExtra("chat_time");

//        Log.e(TAG, "chatFrom:" + chatFrom + "\nchatMessage:" + chatMessage + "\nchatTime:" + chatTime);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(ConstChat.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag == null)
            return;

        if (frag instanceof FragmentChatWith) {

            final EditText etMsg = ((FragmentChatWith) frag).etMsg;

            etMsg.setError(null);

            if (TextUtils.isEmpty(etMsg.getText())) {
                etMsg.setError(getString(R.string.error_field_required));
                return;
            }

            if (etMsg.getText().length() >= 256) {
                etMsg.setError(getString(R.string.error_value_too_long));
                return;
            }

            final RequestChatMsg req = new RequestChatMsg();

            final TrnChatMsg msg = new TrnChatMsg();
            msg.setUid(java.util.UUID.randomUUID().toString());

            Realm r = Realm.getDefaultInstance();
            try {
                Number max = r.where(TrnChatMsg.class).max("seqNo");

                long maxL = 0;
                if (max != null)
                    maxL = max.longValue() + 10L;

                msg.setSeqNo(maxL);
            } finally {
                if (r != null)
                    r.close();
            }

            msg.setFromCollCode(((FragmentChatWith) frag).userCode1);
            msg.setToCollCode(((FragmentChatWith) frag).userCode2);
            msg.setMessage(etMsg.getText().toString());
            msg.setMessageType(ConstChat.MESSAGE_TYPE_COMMON);
            msg.setMessageStatus(ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME);
            msg.setCreatedTimestamp(new Date());

            req.setMsg(msg);

            Call<ResponseBody> call = getAPIService().sendMessage(req);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    final ResponseBody resp = response.body();

                    try {
                        String msgStatus = resp.string();

                        Log.e(TAG, "msgStatus = " + msgStatus);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Realm r = Realm.getDefaultInstance();

                    try {

                        r.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                /*
                                TrnChatMsg msg = new TrnChatMsg();

                                msg.setCreatedTimestamp(req.getTimestamp());
                                msg.setMessage(req.getMessage());
                                msg.setToCollCode(req.getToCollCode());
                                msg.setFromCollCode(req.getFromCollCode());
                                msg.setUid(req.getUid());
                                msg.setMessageType("0");*/

                                if (msg.getFromCollCode().equals(msg.getToCollCode())) {
                                    msg.setMessageStatus(ConstChat.MESSAGE_STATUS_READ_AND_OPENED);

                                    // send back to server kalo sudah dibaca
                                    RequestChatMsg req2 = new RequestChatMsg();
                                    req2.setMsg(msg);
                                    Call<ResponseBody> call2 = getAPIService().sendMessage(req2);
                                    call2.enqueue(new retrofit2.Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            // ignore
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            // ignore
                                        }
                                    });

                                } else {
                                    msg.setMessageStatus(ConstChat.MESSAGE_STATUS_SERVER_RECEIVED);
                                }
                                realm.copyToRealm(msg);

                            }
                        });
                    } finally {
                        r.close();
                    }

                    ((FragmentChatWith) frag).afterAddMsg();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


        } else if (frag instanceof FragmentChatActiveContacts) {
//            Intent i = new Intent(this, ActivityChatRegisteredContacts.class);
//            startActivityForResult(i, 11);

            DialogFragment d = new FragmentChatAllContacts();
            Bundle bundle = new Bundle();
            bundle.putString(FragmentChatAllContacts.PARAM_USERCODE, this.userCode);
            d.setArguments(bundle);

            d.show(getSupportFragmentManager(), "dialog");

        }

    }

    @Override
    public void onGetGroupContacts(OnSuccessError listener) {
        NetUtil.chatGetGroupContacts(this, this.userCode, listener);
    }

    @Override
    public void onGetOnlineContacts(final OnSuccessError listener) {
        NetUtil.chatUpdateContacts(this, this.userCode, listener);
    }

    @Override
    public void onContactSelected(TrnChatContact contact) {
        // open fragmentchatwith
        fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainChatActivity.this, R.drawable.ic_send_black_24dp));

        FragmentChatWith fr = new FragmentChatWith();

        Bundle bundle = new Bundle();
        bundle.putString(FragmentChatWith.PARAM_USERCODE1, this.userCode);
        bundle.putString(FragmentChatWith.PARAM_USERCODE2, contact.getCollCode());
        fr.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fr);
        ft.addToBackStack(null);
        ft.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(contact.getNickName());
        }

    }

    @Override
    public void onContactClearChats(TrnChatContact contact) {
        // why ?
    }

    @Override
    public void onLogon(String collCode, final OnSuccessError listener) {
        NetUtil.chatLogOn(this, collCode, listener);
    }

    @Override
    public void onLogoff(String collCode, final OnSuccessError listener) {
        NetUtil.chatLogOff(this, collCode, listener);
    }

    @Override
    public void isOffline(String collCode, final OnSuccessError listener) {
        if (!NetUtil.isConnected(this)) {
            return;
        }

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        Call<ResponseBody> call = getAPIService().checkStatus(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // update display status here
                if (!response.isSuccessful()) {

                    ResponseBody errorBody = response.errorBody();

                    try {
                        if (listener != null) {
                            listener.onFailure(new RuntimeException(errorBody.string()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                final ResponseBody resp = response.body();

                try {
                    String s = resp.string();

                    if (s != null) {
                        int statusCode = Integer.parseInt(s);

                        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                        if (frag != null)
                            if (frag instanceof FragmentChatActiveContacts) {
                                if (statusCode == 0)
                                    ((FragmentChatActiveContacts) frag).sendStatusOffline();
                                else if (statusCode == 1)
                                    ((FragmentChatActiveContacts) frag).sendStatusOnline();
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                Realm r = Realm.getDefaultInstance();
                try {
                    r.beginTransaction();
                    r.delete(TrnChatContact.class);
                    r.commitTransaction();
                } finally {
                    if (r != null)
                        r.close();
                }
*/
                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    @Override
    public void onGetChatHistory(final String collCode1, final String collCode2) {
        if (!NetUtil.isConnected(this)) {
            return;
        }

        Realm r = Realm.getDefaultInstance();
        try {

            RealmResults<TrnChatMsg> sorted = r.where(TrnChatMsg.class)
                    .equalTo("fromCollCode", collCode1)
                    .equalTo("toCollCode", collCode2)
                    .findAllSorted("createdTimestamp", Sort.ASCENDING);

            if (sorted.size() > 0) {
//                return;
            }
        } finally {
            r.close();
        }

        RequestGetChatHistory req = new RequestGetChatHistory();
        req.setFromCollCode(collCode1);
        req.setToCollCode(collCode2);
        req.setYyyyMMdd("");

        Call<ResponseGetChatHistory> call = getAPIService().getChatHistory(req);
        call.enqueue(new Callback<ResponseGetChatHistory>() {
            @Override
            public void onResponse(Call<ResponseGetChatHistory> call, Response<ResponseGetChatHistory> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                final ResponseGetChatHistory resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.showDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    Utility.showDialog(MainChatActivity.this, "Error (" + resp.getError().getErrorCode() + ")", resp.getError().getErrorDesc());
                    return;
                }

                // MANIPULASI DATA
                // berhubung aq ingin menampilkan tanggal BEDA HARI sebagai header, maka dilakukan insert row tambahan sebagai
                // penanda di recylerview sebagai header. ciri khasnya jika seqNo biasanya kelipatan 10 maka ada ditengah2nya
                Realm _r = Realm.getDefaultInstance();

                _r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
//                        realm.where(TrnChatMsg.class)
//                        .equalTo("fromCollCode", collCode1)
//                        .equalTo("toCollCode", collCode2)
//                            ;
                        List<TrnChatMsg> list = new ArrayList<TrnChatMsg>();

                        Date lastDate = null;
                        for (TrnChatMsg obj : resp.getData()) {

                            if (lastDate == null) {
                                lastDate = obj.getCreatedTimestamp();

                                TrnChatMsg header = new TrnChatMsg();

                                header.setUid(java.util.UUID.randomUUID().toString());
                                header.setFromCollCode(obj.getFromCollCode());
                                header.setToCollCode(obj.getToCollCode());
                                header.setCreatedTimestamp(obj.getCreatedTimestamp());
                                header.setMessageType(ConstChat.MESSAGE_TYPE_TIMESTAMP);
                                header.setMessageStatus(ConstChat.MESSAGE_STATUS_READ_AND_OPENED);

                                header.setSeqNo(obj.getSeqNo() - 5L);
                                header.setMessage(Utility.convertDateToString(lastDate, "EEE, d MMM yyyy"));

                                list.add(header);
                                list.add(obj);

                                continue;
                            }
                            if (!Utility.isSameDay(lastDate, obj.getCreatedTimestamp())) {
                                lastDate = obj.getCreatedTimestamp();

                                TrnChatMsg header = new TrnChatMsg();

                                header.setUid(java.util.UUID.randomUUID().toString());
                                header.setFromCollCode(obj.getFromCollCode());
                                header.setToCollCode(obj.getToCollCode());
                                header.setCreatedTimestamp(obj.getCreatedTimestamp());
                                header.setMessageType(ConstChat.MESSAGE_TYPE_TIMESTAMP);
                                header.setMessageStatus(ConstChat.MESSAGE_STATUS_READ_AND_OPENED);

                                header.setSeqNo(obj.getSeqNo() - 5L);
                                header.setMessage(Utility.convertDateToString(lastDate, "EEE, d MMM yyyy"));

                                list.add(header);
                                list.add(obj);

                            } else
                                list.add(obj);
                        }

                        realm.copyToRealmOrUpdate(list);
                    }
                });

                _r.close();

                final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (frag instanceof FragmentChatWith) {
                    ((FragmentChatWith) frag).listAdapter.notifyDataSetChanged();
                    ((FragmentChatWith) frag).scrollToLast();
                }


            }

            @Override
            public void onFailure(Call<ResponseGetChatHistory> call, Throwable t) {

            }
        });

    }
}
