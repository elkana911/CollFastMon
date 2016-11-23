package id.co.ppu.collfastmon.lkp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;
import id.co.ppu.collfastmon.rest.request.RequestGetGPSHistory;
import id.co.ppu.collfastmon.rest.response.ResponseGetGPSHistory;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.etInterval)
    EditText etInterval;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.tvBottom)
    TextView tvBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_log);

        ButterKnife.bind(this);

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
                Toast.makeText(ActivityGPSLog.this, "You click", Toast.LENGTH_SHORT).show();
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

    private void changeSpinnerColor(Spinner spinner) {
        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(spinnerDrawable);
        }else{
            spinner.setBackgroundDrawable(spinnerDrawable);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {

        loadListFromServer(true);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        onClickFab(null); //ga tau knp mMap bisa null dibagian sini
    }

    public Date getSelectedFromDate() {
        String dateOnly = Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        String from = ((String)spHourStart.getSelectedItem());

        return Utility.convertStringToDate(dateOnly + " " + from, "yyyyMMdd H:mm");
    }

    public Date getSelectedToDate() {

        String dateOnly = Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
        String to = ((String)spHourEnd.getSelectedItem());

        return Utility.convertStringToDate(dateOnly + " " + to, "yyyyMMdd H:mm");
    }

    public synchronized void loadListFromServer(final boolean showProgress) {
        if (!NetUtil.isConnected(this)) {
            loadListFromLocal();
            return;
        }

        // check cache
        if (loadListFromLocal()) {
            return;
        }

        RequestGetGPSHistory req = new RequestGetGPSHistory();

        fillRequest(Utility.ACTION_GET_GPS, req);

        req.setCollectorCode(this.collCode);
        req.setFromDate(getSelectedFromDate());
        req.setToDate(getSelectedToDate());

        String business = ((String)spAction.getSelectedItem());

        req.setBusiness(business);

        if (req.getToDate().before(req.getFromDate())) {
            Utility.showDialog(this, "Error", "Invalid To Date.");
            return;
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");

        if (showProgress) {
            mProgressDialog.show();
        }

        Call<ResponseGetGPSHistory> call = getAPIService().getGPSHistory(req);
        call.enqueue(new Callback<ResponseGetGPSHistory>() {
            @Override
            public void onResponse(Call<ResponseGetGPSHistory> call, Response<ResponseGetGPSHistory> response) {

                if (!response.isSuccessful()) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(ActivityGPSLog.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                final ResponseGetGPSHistory respGetGPSMon = response.body();

                if (respGetGPSMon == null || respGetGPSMon.getData() == null) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Utility.showDialog(ActivityGPSLog.this, "No Data found", "Please try again.");
                    return;
                }

                if (respGetGPSMon.getError() != null) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Utility.showDialog(ActivityGPSLog.this, "Error (" + respGetGPSMon.getError().getErrorCode() + ")", respGetGPSMon.getError().getErrorDesc());
                    return;
                }

                // cache data
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(respGetGPSMon.getData());
                    }
                });

                loadListFromLocal();

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseGetGPSHistory> call, Throwable t) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Utility.showDialog(ActivityGPSLog.this, "Server Problem", t.getMessage());

            }
        });

    }

    private boolean loadListFromLocal() {

        RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class)
                .equalTo("collectorId", this.collCode)
                .between("lastupdateTimestamp", getSelectedFromDate(), getSelectedToDate())
                .findAll();

        boolean exist = all.size() > 0;

        if (mMap != null)
            mMap.clear();

        if (!exist)
            return false;

        if (mMap == null)
            return false;

        // spy tdk usah semuanya tampil, buat range time
        int timeDifference = Integer.parseInt(Utility.extractDigits(groupInterval[selectedInterval]));

        Date lastTimestamp = null;
        LatLng lastLatLng = null;

        int counterPin = 0;
        int counterUnknown = 0;
        // draw markers here
        for (int i = 0; i < all.size(); i++) {
            TrnCollPos pos = all.get(i);

            if (lastTimestamp != null) {
                long diffMin = Utility.getMinutesDiff(pos.getLastupdateTimestamp(), lastTimestamp);

                if (diffMin < timeDifference) {
                    continue;
                }
            }

            lastTimestamp = pos.getLastupdateTimestamp();

            final double lat = Double.parseDouble(pos.getLatitude());
            final double lng = Double.parseDouble(pos.getLongitude());

            if (pos.getLatitude() == null || pos.getLatitude().equals("0.0") || pos.getLatitude().equals("0")) {
                counterUnknown += 1;

                continue;
            } else {
                counterPin += 1;
            }

            LatLng sydney = new LatLng(lat, lng);

            lastLatLng = sydney;

            String time = Utility.convertDateToString(pos.getLastupdateTimestamp(), "HH:mm");

            // bisa ga tampilin yg per-15 menit saja biar usernya ga bingung ?

            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(time));//.snippet(time));
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

}
