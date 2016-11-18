package id.co.ppu.collfastmon;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.fragments.FragmentHomeSpv;
import id.co.ppu.collfastmon.listener.OnCollectorListListener;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.lkp.ActivityMon;
import id.co.ppu.collfastmon.login.LoginActivity;
import id.co.ppu.collfastmon.pojo.CollectorJob;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.response.ResponseGetCollJob;
import id.co.ppu.collfastmon.settings.SettingsActivity;
import id.co.ppu.collfastmon.test.ActivityDeveloper;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.co.ppu.collfastmon.util.DataUtil.resetData;

public class MainActivity extends BasicActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnCollectorListListener, OnMapReadyCallback {
    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";

    private boolean viewIsAtHome;
    private Menu menu;
    private int mSelectedNavMenuIndex = 0;
    private PendingIntent pendingIntent;

    private UserData currentUser;

    private GoogleMap mMap;

    @BindView(R.id.llMap)
    LinearLayout llMap;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private final CharSequence[] menuItems = {
            "From Camera", "From Gallery", "Delete Photo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        currentUser = (UserData) Storage.getObjPreference(this, Storage.KEY_USER, UserData.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRadanaBlue));
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
                miDeveloper.setVisible(Utility.developerMode);
            }

            MenuItem miChats = mn.findItem(R.id.nav_chats);
            if (miChats != null) {
                miChats.setVisible(Utility.developerMode);
            }

            MenuItem miReset = mn.findItem(R.id.nav_reset);
            if (miReset != null) {
//                miReset.setVisible(Utility.developerMode);
            }

        }

        View v = navigationView.getHeaderView(0);

        currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        TextView tvProfileName = ButterKnife.findById(v, R.id.tvProfileName);
        tvProfileName.setText(currentUser.getFullName());

        TextView tvProfileEmail = ButterKnife.findById(v, R.id.tvProfileEmail);
        tvProfileEmail.setText(currentUser.getEmailAddr());

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
        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGIN_DATE, new Date().toString());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);

        fragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        if (llMap.isShown()) {
            llMap.setVisibility(View.GONE);
        }else
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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

//            Intent i = new Intent(this, ActivityChats.class);
//            startActivity(i);

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
        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGIN_DATE, null);
        Storage.savePreference(getApplicationContext(), Storage.KEY_LOGOUT_DATE, new Date().toString());

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

        }/* else if (viewId == R.id.nav_loa) {
            fragment = new FragmentLKPList();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentLKPList.ARG_PARAM1, currentUser.getUserId());
            fragment.setArguments(bundle);

            title = "LKP List";
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
            UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
            getSupportActionBar().setSubtitle(userData.getFullName());
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        drawer.closeDrawer(GravityCompat.START);

    }


    private void logout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Log Out");
        alertDialogBuilder.setMessage("Are you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: clear cookie
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
    }


    @Override
    public void onCollSelected(CollectorJob detail, Date lkpDate) {
        Intent i = new Intent(this, ActivityMon.class);
        i.putExtra(ActivityMon.PARAM_COLLCODE, detail.getCollCode());
        i.putExtra(ActivityMon.PARAM_COLLNAME, detail.getCollName());
        i.putExtra(ActivityMon.PARAM_LKP_DATE, lkpDate.getTime());
        i.putExtra(ActivityMon.PARAM_LDV_NO, detail.getLdvNo());
        startActivity(i);
    }

    @Override
    public void onCollLoad(final Date lkpDate) {
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
    public void onStartRefresh() {

    }

    @Override
    public void onEndRefresh() {

    }

    @Override
    public void onCollLocation(CollectorJob detail, Date lkpDate) {
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
        mMap.clear();
        LatLng sydney = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(collName).snippet(visited));
        marker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 14);
        mMap.moveCamera(cameraUpdate);



        llMap.setVisibility(View.VISIBLE);
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(lat, lng))
//                .title("Hello world"));

        /*
        Fragment fragment = new FragmentMap();
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_map, fragment);
            ft.commit();
        }

        */
    }

    public void getCollectors(final Date lkpDate, boolean useCache, final OnSuccessError listener) {

//        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        // should check apakah ada data lkp yg masih kecantol di hari kemarin

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting Collectors data from server.\nPlease wait...");
        mProgressDialog.show();

        boolean b = DataUtil.isMasterDataDownloaded(this, realm, true);

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        RequestCollJobByDate req = new RequestCollJobByDate();
        req.setSpvCode(currentUser.getUserId());
        req.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        Call<ResponseGetCollJob> call = fastService.getCollectorsJob(req);
        call.enqueue(new Callback<ResponseGetCollJob>() {
            @Override
            public void onResponse(Call<ResponseGetCollJob> call, Response<ResponseGetCollJob> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    final ResponseGetCollJob respGetCollJob = response.body();

                    if (respGetCollJob == null) {
                        Utility.showDialog(MainActivity.this, "No Collector found", "You have empty List");
                        return;
                    }

                    if (respGetCollJob.getError() != null) {
                        Utility.showDialog(MainActivity.this, "Error (" + respGetCollJob.getError().getErrorCode() + ")", respGetCollJob.getError().getErrorDesc());
                    } else {

                        if (respGetCollJob.getData() == null) {

                        } else {
                            // save db here
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {

                                    boolean d = bgRealm.where(CollectorJob.class).findAll().deleteAllFromRealm();

                                    // replace taskcode ke string
                                    for (int i = 0; i < respGetCollJob.getData().size(); i++) {

                                        MstTaskType first = bgRealm.where(MstTaskType.class)
                                                .equalTo("taskCode", respGetCollJob.getData().get(i).getLastTask())
                                                .findFirst();

                                        if (first != null) {
                                            respGetCollJob.getData().get(i).setLastTask(first.getShortDesc());
                                        }
                                    }

                                    bgRealm.copyToRealmOrUpdate(respGetCollJob.getData());

                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    if (listener != null)
                                        listener.onSuccess(null);


                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // Transaction failed and was automatically canceled.
                                    Toast.makeText(MainActivity.this, "Error while getting Collectors", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();

                                    if (listener != null)
                                        listener.onFailure(error);
                                }
                            });


                        }
                    }
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(MainActivity.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onFailure(null);

                }
            }

            @Override
            public void onFailure(Call<ResponseGetCollJob> call, Throwable t) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.e("eric.onFailure", t.getMessage(), t);

                if (listener != null)
                    listener.onFailure(t);

                Utility.showDialog(MainActivity.this, "Server Problem", t.getMessage());
            }
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
}
