package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.screen.fragments.FragmentLKPList;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.util.Utility;

public class ActivityLKPList extends BasicActivity implements FragmentLKPList.OnLKPListListener {
    public static final String PARAM_COLLCODE = "collector.code";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_COLLNAME = "collector.name";
    public static final String PARAM_COLLTYPE = "collector.type";
    private static final int ACTIVITY_TASK_LOG = 67;
    private static final int ACTIVITY_GPS_LOG = 68;

    public String collCode = null;
    public String collName = null;
    public String collType = null;
    public String ldvNo = null;
    public Date lkpDate = null;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    public static Intent createIntent(Context ctx, CollJob detail){
        Intent i = new Intent(ctx, ActivityLKPList.class);

        i.putExtra(PARAM_COLLCODE, detail.getCollCode());
        i.putExtra(PARAM_COLLNAME, detail.getCollName());
        i.putExtra(PARAM_LKP_DATE, detail.getLkpDate().getTime());
        i.putExtra(PARAM_LDV_NO, detail.getLdvNo());
        i.putExtra(PARAM_COLLTYPE, detail.getCollType());

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lkplist);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setElevation(0);
//        }

        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        this.collCode = extras.getString(PARAM_COLLCODE);
        this.collName = extras.getString(PARAM_COLLNAME);
        this.collType = extras.getString(PARAM_COLLTYPE);
        this.ldvNo = extras.getString(PARAM_LDV_NO);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_mon);
            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        onClickFab();
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fr instanceof FragmentLKPList) {
            ((FragmentLKPList)fr).loadListFromServer(true, collCode, collName, ldvNo, lkpDate);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_mon, menu);

        Drawable drawable = menu.findItem(R.id.action_summary_lkp).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_summary_lkp).setIcon(drawable);

        Drawable drawableTaskLog = menu.findItem(R.id.action_task_log).getIcon();
        drawableTaskLog = DrawableCompat.wrap(drawableTaskLog);
        DrawableCompat.setTint(drawableTaskLog, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_task_log).setIcon(drawableTaskLog);

        Drawable drawableGpsLog = menu.findItem(R.id.action_gps_log).getIcon();
        drawableGpsLog = DrawableCompat.wrap(drawableGpsLog);
        DrawableCompat.setTint(drawableGpsLog, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_gps_log).setIcon(drawableGpsLog);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_summary_lkp) {
            startActivity(ActivitySummaryLKP.createIntent(this, this.collCode, this.lkpDate.getTime()));
            return true;
        } else if (id == R.id.action_task_log) {
            startActivityForResult(ActivityTaskLog.createIntent(this, this.collCode, this.lkpDate.getTime(), this.collName, this.ldvNo), ACTIVITY_TASK_LOG);
            return true;
        } else if (id == R.id.action_gps_log) {
            startActivityForResult(ActivityGPSLog.createIntent(this, this.collCode, this.lkpDate.getTime(), this.collName, this.ldvNo), ACTIVITY_GPS_LOG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLKPSelected(DisplayLDVDetails detail) {

        if (detail == null) {
            return;
        }

        Intent i = null;

        if (detail.getLdvFlag().equalsIgnoreCase("COL")) {
            // payment screen
            // kalo dari non lkp biasanya ldvNo kosong, bisa diatur di screen payment details
            i = ActivityPaymentDetails.createIntent(this, detail);

        } else if (detail.getLdvFlag().equalsIgnoreCase("PCU")) {
            // repo
            i = ActivityRepoEntry.createIntent(this, detail);
        } else if (detail.getLdvFlag().equalsIgnoreCase("NEW")) {
            // doing nothing
        } else {
            // vist result
            if (this.collType.equalsIgnoreCase("RPC")) {
                i = ActivityVisitResultRPC.createIntent(this, detail);
            }else
                i = ActivityVisitResult.createIntent(this, detail);
        }

        if (i == null) {
            Toast.makeText(this, "Belum dikunjungi, tidak ada data.", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == ACTIVITY_TASK_LOG) {
            if (data == null) {
                return;
            }

            String action = data.getStringExtra("ACTION");

            if (!TextUtils.isEmpty(action) && action.equals(Utility.ACTION_RESTART_ACTIVITY)) {
                setResult(RESULT_OK, data);
                finish();
            }

        } else if (requestCode == ACTIVITY_GPS_LOG) {

        }
    }

//    @Override
//    public void onStartRefresh() {
//        fab.hide();
//    }

//    @Override
//    public void onEndRefresh() {
//        fab.show();
//    }

    @Override
    public String getCollCode() {
        return this.collCode;
    }

    @Override
    public String getCollName() {
        return this.collName;
    }

    @Override
    public Date getLKPDate() {
        return this.lkpDate;
    }

    @Override
    public String getLDVNo() {
        return this.ldvNo;
    }
}
