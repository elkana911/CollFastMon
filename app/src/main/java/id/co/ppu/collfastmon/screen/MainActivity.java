package id.co.ppu.collfastmon.screen;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.screen.chat.MainChatActivity;
import id.co.ppu.collfastmon.screen.help.ActivityHelpWeb;
import id.co.ppu.collfastmon.screen.home.FragmentHomeSpv;
import id.co.ppu.collfastmon.screen.lkp.ActivityLKPList;
import id.co.ppu.collfastmon.screen.login.LoginActivity;
import id.co.ppu.collfastmon.screen.settings.SettingsActivity;
import id.co.ppu.collfastmon.test.ActivityDeveloper;
import id.co.ppu.collfastmon.util.ChatUtil;
import id.co.ppu.collfastmon.util.ConstChat;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.DemoUtil;
import id.co.ppu.collfastmon.util.NotificationUtils;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;

import static id.co.ppu.collfastmon.util.DataUtil.resetData;

public class MainActivity extends BasicActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentHomeSpv.OnCollectorListListener, OnMapReadyCallback {
    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";
    private static final int ACTIVITY_MONITORING = 50;
    private static final String TAG = "MainActivity";

    Handler handlerChatStatus = new Handler();
    private BroadcastReceiver broadcastReceiver;

//    private SectionsPagerAdapter mSectionsPagerAdapter;

    private boolean viewIsAtHome;
    private Menu menu;
    private int mSelectedNavMenuIndex = 0;
    private PendingIntent pendingIntent;

    private UserData currentUser;

    private GoogleMap mMap;

    @BindView(R.id.coordinatorLayout)
    public View coordinatorLayout;

    @BindView(R.id.llMap)
    LinearLayout llMap;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

//    @BindView(R.id.container_pager)
//    ViewPager mViewPager;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("handlerChatStatus", "Update Chat Log On status");

            ChatUtil.chatLogOn(MainActivity.this, DataUtil.getCurrentUserId(), new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {
                    //yg mana offline ?
                    ChatUtil.chatUpdateContacts(MainActivity.this, DataUtil.getCurrentUserId(), null);
                }

                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSkip() {
                    //yg mana offline ?
                    ChatUtil.chatUpdateContacts(MainActivity.this, DataUtil.getCurrentUserId(), null);
                }
            });

            // Repeat this the same runnable code block again another 2 seconds
            handlerChatStatus.postDelayed(runnableCode, Utility.CYCLE_CHAT_STATUS_MILLISEC);
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (DemoUtil.isDemo(this)) {
            promptSnackBar("WARNING! You're currently signed in as DEMO. Make sure you're OFFLINE");
        }

        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof FragmentHomeSpv) {
            ((FragmentHomeSpv) frag).onClickFab();
        }

        //fLsWT7b3aDQ:APA91bHMAZiTeyT5Wf7Akb9wIr-VA3XzuQ87cdjtdwRNz-0kkkfUgTNnKZMmx4AmYUvk0SrVQiK3wwk-R-nlUEZAT5v6R_0szPwSa6FmNH-77iqYOB_x0WpLaI21smEQD9_G-w9tyfaZ
        String androidId = Storage.getPref(Storage.KEY_ANDROID_ID, null);
        if (TextUtils.isEmpty(androidId)) {
            androidId = FirebaseInstanceId.getInstance().getToken();
        }

//        handleIntent(getIntent());

        ChatUtil.chatLogOn(this, DataUtil.getCurrentUserId(), null);

        // Start the initial runnable task by posting through the handler
        handlerChatStatus.post(runnableCode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // biasa dipanggil saat user click notification bar
        super.onNewIntent(intent);

//        setIntent(intent);

        // jika user click notificationsnya
        if (intent.getExtras() != null) {
            Intent i = new Intent(this, MainChatActivity.class);
            i.putExtra(MainChatActivity.PARAM_USER_CODE, currentUser.getUserId());
            i.putExtras(intent);
//            i.putExtra(ActivitySummaryLKP.PARAM_LKP_DATE, this.lkpDate.getTime());
            startActivity(i);

        }

        NotificationUtils.clearNotifications(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        // Removes pending code execution
        handlerChatStatus.removeCallbacks(runnableCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();


//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mSectionsPagerAdapter);

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
                    // new push notification is received
//                    handleNotification(intent);
                    Bundle extras = intent.getExtras();
                    if (extras == null) {
                        Log.e(TAG, "No extras found");
                        return;
                    }

                    String key_from = intent.getStringExtra(ConstChat.KEY_FROM);
                    String key_uid = intent.getStringExtra(ConstChat.KEY_UID);
                    String key_msg = intent.getStringExtra(ConstChat.KEY_MESSAGE);
                    String key_status = intent.getStringExtra(ConstChat.KEY_STATUS);
//                    String key_seqno = intent.getStringExtra(ConstChat.KEY_SEQNO);
                    String key_timestamp = intent.getStringExtra(ConstChat.KEY_TIMESTAMP);

                    Log.e(TAG, "chatFrom:" + key_from + "\nchatMessage:" + key_msg);

                    boolean appInBg = NotificationUtils.isAppIsInBackground(getApplicationContext());

                    NotificationUtils.showNotificationMessage(MainActivity.this, key_from, key_msg, "", intent);

                    // kalo key_from null, dan body ada isinya brarti cuma pesan aja ga perlu start new intent

                    if (key_uid == null) {
                        String body = intent.getStringExtra("body");
                        NotificationUtils.showNotificationMessage(MainActivity.this, key_from, body, "", intent);
                    } else {
//                    di MainActivity memang semua broadcast message pasti ditaruh di notificationbar sehingga harus new intent, onCreate tdk akan terbaca melainkan ke onNewIntent
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        //must redefine supaya dieksekusi dari notificationbar
                        resultIntent.putExtras(extras);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        NotificationUtils.showNotificationMessage(MainActivity.this, key_from, key_msg, "", resultIntent);
                    }

//                    txtMessage.setText(message);
                }
            }
        };

        currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Menu mn = navigationView.getMenu();
        if (mn != null) {
            MenuItem miDeveloper = mn.findItem(R.id.nav_developer);
            if (miDeveloper != null) {
                miDeveloper.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miChats = mn.findItem(R.id.nav_chats);
            if (miChats != null) {
                miChats.setVisible(Utility.DEVELOPER_MODE);
            }

            MenuItem miReset = mn.findItem(R.id.nav_reset);
            if (miReset != null) {
//                miReset.setVisible(Utility.DEVELOPER_MODE);
            }

        }

        View v = navigationView.getHeaderView(0);

        currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        if (currentUser == null)
            backToLoginScreen();

        try {
            TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
            tvProfileName.setText(currentUser.getFullName());

            TextView tvProfileEmail = ButterKnife.findById(v, R.id.tvProfileEmail);
            tvProfileEmail.setText(currentUser.getEmailAddr());
        } catch (Exception e) {
            e.printStackTrace();
            backToLoginScreen();
        }

        final ImageView imageView = ButterKnife.findById(v, R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(menuItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // from camera
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 44);//zero can be replaced with any action code
                        } else if (item == 1) {
                            // from gallery
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 55);//one can be replaced with any action code
                        } else if (item == 2) {
                            // delete
                            Drawable icon;
                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_a_photo_black_24dp, getTheme());
                            } else {
                                icon = getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp, getTheme());
                            }

                            Drawable drawable = AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_account_circle_black_24dp);
                            imageView.setImageDrawable(drawable);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        // is collector photo available ?
        boolean photoNotAvail = true;
        if (photoNotAvail) {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_account_circle_black_24dp);
            imageView.setImageDrawable(drawable);
        }

        if (savedInstanceState == null) {
            displayView(R.id.nav_home);
        } else {
            // recover state
            mSelectedNavMenuIndex = savedInstanceState.getInt(SELECTED_NAV_MENU_KEY);
            displayView(mSelectedNavMenuIndex);
        }

        boolean b = DataUtil.isMasterDataDownloaded(this, realm, true);
        Storage.savePref(Storage.KEY_LOGIN_DATE, new Date().toString());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);

        fragment.getMapAsync(this);

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(ConstChat.PUSH_NOTIFICATION));

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//        resultIntent.putExtra("message", key_msg);

        // register GCM registration complete receiver
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        if (llMap.isShown()) {
            llMap.setVisibility(View.GONE);
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();

            if (!viewIsAtHome) {
                int x = getSupportFragmentManager().getBackStackEntryCount();

                if (x > 1) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else
                    displayView(R.id.nav_home);
            } else {
                //display logout dialog
//                moveTaskToBack(true);
                logout();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_paymentEntry) {
//            openPaymentEntry();

            return false;
        } else if (id == R.id.nav_developer) {

            Intent i = new Intent(this, ActivityDeveloper.class);
            startActivity(i);

            return false;
        } else if (id == R.id.nav_chats) {

            Intent i = new Intent(this, MainChatActivity.class);
            i.putExtra(MainChatActivity.PARAM_USER_CODE, currentUser.getUserId());
//            i.putExtra(ActivitySummaryLKP.PARAM_LKP_DATE, this.lkpDate.getTime());
            startActivity(i);

            return false;
        } else if (id == R.id.nav_reset) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Reset Data");
            alertDialogBuilder.setMessage("This will Logout Application.\nAre you sure?");
            //null should be your on click listener
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    resetData();
                    backToLoginScreen();

                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.show();


            return false;
        } else if (id == R.id.nav_logout) {
            logout();

            return false;
        } else if (id == R.id.nav_closeBatch) {
            drawer.closeDrawers();

//            closeBatch();

            return false;
        } else if (id == R.id.nav_manualSync) {

            drawer.closeDrawers();

            return false;
        } else if (id == R.id.nav_clearSyncTables) {
//            clearSyncTables();

            return false;
        } else
            displayView(id);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 999);
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, ActivityHelpWeb.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backToLoginScreen() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("finish", true); // if you are checking for this in your other Activities
        if (Build.VERSION.SDK_INT >= 11) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        // flag as clean logout
        Storage.savePref(Storage.KEY_LOGIN_DATE, null);
        Storage.savePref(Storage.KEY_LOGOUT_DATE, new Date().toString());

        realm.beginTransaction();
        realm.delete(TrnCollPos.class);
        realm.commitTransaction();

        ChatUtil.chatLogOff(this, DataUtil.getCurrentUserId(), null);

        startActivity(intent);
//                moveTaskToBack(true);
        finish();

    }

    private void displayView(int viewId) {
        Fragment fragment = null;
        String title = null;
        viewIsAtHome = false;

        if (this.menu != null) {
            // action_search is now disabled due to the limitation of realmsearchview. filterkey cant search by custName if data sorted by seqNo
//            MenuItem item = this.menu.findItem(R.id.action_search);
//            item.setVisible(viewId == R.id.nav_loa);
        }

        navigationView.setCheckedItem(viewId);

        title = getString(R.string.menu_home);

        if (viewId == R.id.nav_home) {
            fragment = new FragmentHomeSpv();

            viewIsAtHome = true;

        }/* else if (viewId == R.id.nav_chats) {

        } else if (viewId == R.id.nav_summaryLKP) {
            fragment = new FragmentSummaryLKP();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentSummaryLKP.ARG_PARAM1, currentUser.getUserId());
//            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getSecUser().get(0).getUserName());
            fragment.setArguments(bundle);

            title = "Summary LKP";
        }
        */

        /* else if (viewId == R.id.nav_paymentEntry) {
            title = "Payment Entry";

        } else if (viewId == R.id.nav_customerProgram) {
            title = "Customer Program";

        } else if (viewId == R.id.nav_marketingPromo) {
            title = "Marketing Promo";
        } else if (viewId == R.id.nav_nearlyPayment) {
            title = "Nearly Payment";
        } else if (viewId == R.id.nav_radanaOffice) {
            title = "Radana Office";
        }*/

        if (viewId != R.id.nav_home) {

        }

        mSelectedNavMenuIndex = viewId;

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            UserData userData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);
            getSupportActionBar().setSubtitle(userData.getFullName());
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        drawer.closeDrawer(GravityCompat.START);

    }


    private void logout() {

        Utility.confirmDialog(this, "Log Out", getString(R.string.prompt_quit), () -> {
            // TODO: clear cookie

            backToLoginScreen();
        });

    }

    private void openChat(String from, String to) {
        if (TextUtils.isEmpty(from) && TextUtils.isEmpty(to)) {
            return;
        }

    }

    @Override
    public void onShowCollector(CollJob detail, Date lkpDate) {
        startActivityForResult(ActivityLKPList.createIntent(this, detail), ACTIVITY_MONITORING);
    }

    @Override
    public void getCollectorsByDate(final Date lkpDate) {
        final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof FragmentHomeSpv) {
            getCollectors(lkpDate, false, new OnSuccessError() {

                @Override
                public void onSuccess(String msg) {
                    ((FragmentHomeSpv) frag).loadCollectorsFromLocal(lkpDate);
                }

                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSkip() {

                }
            });
        }
    }

    @Override
    public void onShowGPSLocation(CollJob detail, Date lkpDate) {
//        contentMap.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(detail.getLastLatitude())
                || TextUtils.isEmpty(detail.getLastLongitude())
        ) {
            return;
        }

        final double lat = Double.parseDouble(detail.getLastLatitude());
        final double lng = Double.parseDouble(detail.getLastLongitude());
        final String collName = detail.getCollName();
        final String visited = detail.getCountVisited() + " / " + detail.getCountLKP();

        /*
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng sydney = new LatLng(lat, lng);
                googleMap.clear();
                Marker marker = googleMap.addMarker(new MarkerOptions().position(sydney).title(collName).snippet("Mobile"));
                marker.showInfoWindow();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 14);
                googleMap.moveCamera(cameraUpdate);
            }
        });
        */
        LatLng sydney = new LatLng(lat, lng);

        if (mMap != null) {

            mMap.clear();
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(collName).snippet(visited));
            marker.showInfoWindow();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 14);
            mMap.moveCamera(cameraUpdate);
        } else
            Toast.makeText(MainActivity.this, "Sorry, unable to get the location. Try again next time.", Toast.LENGTH_LONG).show();

        llMap.setVisibility(View.VISIBLE);

    }

    public void getCollectors(final Date lkpDate, boolean useCache, final OnSuccessError listener) {

        // should check apakah ada data lkp yg masih kecantol di hari kemarin

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Getting Collectors data from server.\nPlease wait...");

        boolean b = DataUtil.isMasterDataDownloaded(this, realm, true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean b1 = sp.getBoolean(Storage.KEY_PREF_GENERAL_SHOWALL_COLL, false);

//        if (!NetUtil.isConnected(this)
//                && DemoUtil.isDemo(this)) {
        if (DemoUtil.isDemo(this)) {

            final List<CollJob> data = DemoUtil.buildCollectors();

            realm.executeTransaction(realm -> DataUtil.saveCollectorsToDB(realm, data));

            Utility.dismissDialog(mProgressDialog);

            if (listener != null)
                listener.onSuccess(null);

            return;
        }

        // default is show all
        APIonBuilder.getCollectorsJobEx(this, lkpDate, (e, result) -> {
            Utility.dismissDialog(mProgressDialog);

            if (e != null) {

                Log.e("eric.onFailure", e.getMessage(), e);

                if (listener != null)
                    listener.onFailure(e);

                Utility.showDialog(MainActivity.this, "Problem", e.getMessage());
                return;
            }

            if (result.getError() != null) {

                Utility.showDialog(MainActivity.this, "Error (" + result.getError().getErrorCode() + ")", result.getError().getErrorDesc());

                return;
            }

            // save db here
            realm.executeTransactionAsync(bgRealm -> DataUtil.saveCollectorsToDB(bgRealm, result.getData()), () -> {
                if (listener != null)
                    listener.onSuccess(null);


            }, error -> {
                // Transaction failed and was automatically canceled.
                Toast.makeText(MainActivity.this, "Error while getting Collectors", Toast.LENGTH_LONG).show();
                error.printStackTrace();

                if (listener != null)
                    listener.onFailure(error);
            });

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (client != null && !client.isConnected()) {
            client.connect();
        }

        if (client != null)
            AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (client != null) {
            AppIndex.AppIndexApi.end(client, getIndexApiAction());
            client.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == ACTIVITY_MONITORING) {
            if (data == null) {
                return;
            }

            String action = data.getStringExtra("ACTION");

            if (!TextUtils.isEmpty(action) && action.equals(Utility.ACTION_RESTART_ACTIVITY)) {
//                final Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

//                if (frag != null && frag instanceof FragmentHomeSpv) {
//                    ((FragmentHomeSpv)frag).onClickFab();
//                }

//                setResult(RESULT_OK, data);
//                finish();
            }

        }

    }

    public void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(String message, int duration) {
        Snackbar.make(coordinatorLayout, message, duration).show();
    }

    public void promptSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);

        snackbar.show();
    }

    /*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    fragment = new FragmentHome();
                    break;
                case 1:
                    fragment = new FragmentChatActiveContacts();
                    break;
            }

            if (fragment != null) {

                fragment.setArguments(args);
                return fragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }

    }
*/
}
