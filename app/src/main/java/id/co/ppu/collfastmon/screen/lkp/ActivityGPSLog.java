package id.co.ppu.collfastmon.screen.lkp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.util.DemoUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmResults;

public class ActivityGPSLog extends BasicActivity implements OnMapReadyCallback {

    public static final String PARAM_COLL_CODE = "collector.code";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_COLLNAME = "collector.name";

    public String collCode = null;
    public String collName = null;
    public String ldvNo = null;
    public Date lkpDate = null;

    final String[] groupInterval = {"1 Minute", "5 Minutes", "10 Minutes", "15 Minutes"};
    private int selectedInterval = 1;
//    public int chosenInterval = 10;

    private GoogleMap mMap;

    @BindView(R.id.spHourStart)
    Spinner spHourStart;

    @BindView(R.id.spHourEnd)
    Spinner spHourEnd;

    @BindView(R.id.spAction)
    Spinner spAction;

    @BindView(R.id.tilInterval)
    View tilInterval;

    @BindView(R.id.etInterval)
    EditText etInterval;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.tvBottom)
    TextView tvBottom;

    public static Intent createIntent(Context ctx, String collCode, long lkpDate, String collName, String ldvNo) {
        Intent i = new Intent(ctx, ActivityGPSLog.class);
        i.putExtra(PARAM_COLL_CODE, collCode);
        i.putExtra(PARAM_LKP_DATE, lkpDate);
        i.putExtra(PARAM_COLLNAME, collName);
        i.putExtra(PARAM_LDV_NO, ldvNo);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_log);

        ButterKnife.bind(this);

        etTglLKP.setTypeface(fontGoogle);
        etInterval.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        this.collCode = extras.getString(PARAM_COLL_CODE);
        this.collName = extras.getString(PARAM_COLLNAME);
        this.ldvNo = extras.getString(PARAM_LDV_NO);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_gps_log);
            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etInterval.setText(groupInterval[selectedInterval]);

        etTglLKP.setText(Utility.convertDateToString(this.lkpDate, Utility.DATE_DISPLAY_PATTERN));

        List<String> hoursWorkingList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hoursWorkingList.add("" + i + ":00");
        }

        HoursAdapter arrayAdapter = new HoursAdapter(this, android.R.layout.simple_spinner_item, hoursWorkingList);
        spHourStart.setAdapter(arrayAdapter);
        spHourEnd.setAdapter(arrayAdapter);

        changeSpinnerColor(spHourStart);
        changeSpinnerColor(spHourEnd);

//        spHourEnd.setSelection(spHourEnd.getAdapter().getCount()-1);
        spHourStart.setSelection(8);
        spHourEnd.setSelection(20);


        List<String> actionList = new ArrayList<>();
        actionList.add("ALL");
        actionList.add("PAYMENT");
        actionList.add("VISIT");

        ActionAdapter actionAdapter = new ActionAdapter(this, android.R.layout.simple_spinner_item, actionList);
        spAction.setAdapter(actionAdapter);
        spAction.setSelection(0);
        spAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tilInterval.setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);
                onClickFab(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changeSpinnerColor(spAction);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_gps, menu);

        Drawable drawable = menu.findItem(R.id.action_refresh).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_refresh).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadListFromServer(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeSpinnerColor(Spinner spinner) {
        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(spinnerDrawable);
        } else {
            spinner.setBackgroundDrawable(spinnerDrawable);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {

        loadListFromLocal();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loadListFromServer(true);
//        onClickFab(null); //ga tau knp mMap bisa null dibagian sini
    }

    public Date getSelectedFromDate() {
        String dateOnly = Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        String from = ((String) spHourStart.getSelectedItem());

        return Utility.convertStringToDate(dateOnly + " " + from, "yyyyMMdd H:mm");
    }

    public Date getSelectedToDate() {

        String dateOnly = Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
        String to = ((String) spHourEnd.getSelectedItem());

        return Utility.convertStringToDate(dateOnly + " " + to, "yyyyMMdd H:mm");
    }

    public synchronized void loadListFromServer(final boolean showProgress) {

        if (DemoUtil.isDemo(this)) {
            loadListFromLocal();
            return;
        }

        if (!NetUtil.isConnected(this)) {
            loadListFromLocal();
            return;
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");

        if (showProgress) {
            mProgressDialog.show();
        }

        APIonBuilder.getGPSHistory(this, this.collCode, this.lkpDate, (e, result) -> {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            if (e != null) {
                Utility.showDialog(ActivityGPSLog.this, "Problem", e.getMessage());
                return;
            }

            if (result.getError() != null) {
                Utility.showDialog(ActivityGPSLog.this, "Error (" + result.getError().getErrorCode() + ")", result.getError().getErrorDesc());
                return;
            }

            // cache data
            realm.executeTransaction(realm -> {
                realm.where(TrnCollPos.class)
                        .equalTo("collectorId", collCode)
//                                .between("lastupdateTimestamp", getSelectedFromDate(), getSelectedToDate())
                        .findAll().deleteAllFromRealm();

                realm.copyToRealmOrUpdate(result.getData());
            });

            loadListFromLocal();

        });

    }

    private boolean loadListFromLocal() {

        RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class)
                .equalTo("collectorId", this.collCode)
                .between("lastupdateTimestamp", getSelectedFromDate(), getSelectedToDate())
                .findAll().sort("lastupdateTimestamp");

        boolean exist = all.size() > 0;

        tvBottom.setText("No Pin");

        if (mMap != null)
            mMap.clear();

        if (!exist || mMap == null)
            return false;

        // spy tdk usah semuanya tampil, buat range time
        int timeDifference = Integer.parseInt(Utility.extractDigits(groupInterval[selectedInterval]));

        Date lastTimestamp = null;
        LatLng lastLatLng = null;

        String business = ((String) spAction.getSelectedItem());
        int counterPin = 0;
        int counterUnknown = 0;
        // draw markers here
        for (int i = 0; i < all.size(); i++) {
            TrnCollPos pos = all.get(i);

            String contractNo = "";

            // khusus payment dan visit tidak perlu difference
            if (business.equalsIgnoreCase("PAYMENT")) {
                if (!pos.getUid().startsWith("~"))
                    continue;
            } else if (business.equalsIgnoreCase("VISIT")) {
                if (!pos.getUid().startsWith("!"))
                    continue;
            } else {
                if (pos.getUid().startsWith("~") || pos.getUid().startsWith("!")) {

                } else {
                    if (lastTimestamp != null) {
                        long diffMin = Utility.getMinutesDiff(lastTimestamp, pos.getLastupdateTimestamp());

                        if (pos.getUid().startsWith("~") || pos.getUid().startsWith("!")) {

                        } else {
                            if (diffMin < timeDifference) {
//                        lastTimestamp = pos.getLastupdateTimestamp();
                                continue;
                            }

                        }
                    }
                    lastTimestamp = pos.getLastupdateTimestamp();

                }

            }

            if (pos.getLatitude() == null || pos.getLatitude().equals("0.0") || pos.getLatitude().equals("0")) {
                counterUnknown += 1;

                continue;
            } else {
                counterPin += 1;
            }

            final double lat = Double.parseDouble(pos.getLatitude());
            final double lng = Double.parseDouble(pos.getLongitude());

            LatLng sydney = new LatLng(lat, lng);

            lastLatLng = sydney;

            String time = Utility.convertDateToString(pos.getLastupdateTimestamp(), "HH:mm");

            // minta tampilin no contract, masalahnya trncollpos ga ada, jd harus main2 string split
            if (pos.getUid().startsWith("~")) {
                contractNo = pos.getUid().split("~")[1];
            } else if (pos.getUid().startsWith("!")) {
                contractNo = pos.getUid().split("!")[1];
            }

                // bisa ga tampilin yg per-15 menit saja biar usernya ga bingung ?
            MarkerOptions mo = new MarkerOptions().position(sydney).title(time);

            if (!TextUtils.isEmpty(contractNo))
                mo.snippet(contractNo);

            Marker marker = mMap.addMarker(mo);

            if (pos.getUid().startsWith("~")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else if (pos.getUid().startsWith("!")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
//            marker.showInfoWindow();

        }

        if (lastLatLng != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastLatLng, 14);
            mMap.moveCamera(cameraUpdate);

        }

        if (counterPin < 1 && counterUnknown < 1) {
            tvBottom.setText("No Pin");
        } else {
            if (counterUnknown > 0) {
                tvBottom.setText("" + counterPin + " Pin(s), " + counterUnknown + " Unknown Location");
            } else {
                tvBottom.setText("" + counterPin + " Pin(s)");
            }
        }

        return exist;
    }

    @OnClick(R.id.etInterval)
    public void onClickInterval(View view) {
        // dialog radio button
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Pick Interval");
        dialog.setSingleChoiceItems(groupInterval, selectedInterval, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etInterval.setText(groupInterval[i]);

                selectedInterval = i;

                dialogInterface.dismiss();

                onClickFab(null);
            }

        });
        dialog.create().show();

    }

    public class HoursAdapter extends ArrayAdapter<String> {
        private Context ctx;
        private List<String> list;


        public HoursAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.WHITE);
//            tv.setText(list.get(position).getRvbNo());
            tv.setText(list.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setPadding(10, 20, 10, 20);
            label.setText(list.get(position));
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public class ActionAdapter extends ArrayAdapter<String> {
        private Context ctx;
        private List<String> list;


        public ActionAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
//            tv.setText(list.get(position).getRvbNo());
            tv.setText(list.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);

            String text = list.get(position);

            label.setPadding(10, 20, 10, 20);
            label.setText(text);
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            label.setTypeface(Typeface.DEFAULT_BOLD);
            if (text.equalsIgnoreCase("PAYMENT")) {
                label.setTextColor(Color.RED);
            } else if (text.equalsIgnoreCase("VISIT")) {
                label.setTextColor(Color.GREEN);
            }

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}
