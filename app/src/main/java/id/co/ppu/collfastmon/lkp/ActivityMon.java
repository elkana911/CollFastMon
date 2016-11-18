package id.co.ppu.collfastmon.lkp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.fragments.FragmentMonitoring;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;

public class ActivityMon extends BasicActivity implements FragmentMonitoring.OnLKPListListener {
    public static final String PARAM_COLLCODE = "collector.code";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_COLLNAME = "collector.name";

    public String collCode = null;
    public String collName = null;
    public String ldvNo = null;
    public Date lkpDate = null;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        this.collCode = extras.getString(PARAM_COLLCODE);
        this.collName = extras.getString(PARAM_COLLNAME);
        this.ldvNo = extras.getString(PARAM_LDV_NO);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_mon);
            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fr instanceof FragmentMonitoring) {
            ((FragmentMonitoring)fr).setListVisibility(false);
            ((FragmentMonitoring)fr).loadListFromServer(true);
        }

//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
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
            Intent i = new Intent(this, ActivitySummaryLKP.class);
            i.putExtra(ActivitySummaryLKP.PARAM_COLL_CODE, this.collCode);
            i.putExtra(ActivitySummaryLKP.PARAM_LKP_DATE, this.lkpDate.getTime());

            startActivity(i);
            return true;
        } else if (id == R.id.action_task_log) {
            Intent i = new Intent(this, ActivityTaskLog.class);
            i.putExtra(ActivityTaskLog.PARAM_COLL_CODE, this.collCode);
            i.putExtra(ActivityTaskLog.PARAM_LKP_DATE, this.lkpDate.getTime());
            i.putExtra(ActivityTaskLog.PARAM_COLLNAME, this.collName);
            i.putExtra(ActivityTaskLog.PARAM_LDV_NO, this.ldvNo);

            startActivity(i);
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
            i = new Intent(this, ActivityPaymentDetails.class);
            i.putExtra(ActivityPaymentDetails.PARAM_CONTRACT_NO, detail.getContractNo());
            i.putExtra(ActivityPaymentDetails.PARAM_LKP_DATE, detail.getLkpDate().getTime());
            i.putExtra(ActivityPaymentDetails.PARAM_COLLECTOR_ID, detail.getCollId());
            i.putExtra(ActivityPaymentDetails.PARAM_LDV_NO, detail.getLdvNo());

        } else if (detail.getLdvFlag().equalsIgnoreCase("PCU")) {
            // repo
            i = new Intent(this, ActivityRepoEntry.class);
            i.putExtra(ActivityRepoEntry.PARAM_CONTRACT_NO, detail.getContractNo());
            i.putExtra(ActivityRepoEntry.PARAM_COLLECTOR_ID, detail.getCollId());
            i.putExtra(ActivityRepoEntry.PARAM_LDV_NO, detail.getLdvNo());
        } else if (detail.getLdvFlag().equalsIgnoreCase("NEW")) {
            // doing nothing
        } else {
            // vist result
            i = new Intent(this, ActivityVisitResult.class);
            i.putExtra(ActivityVisitResult.PARAM_CONTRACT_NO, detail.getContractNo());
            i.putExtra(ActivityVisitResult.PARAM_COLLECTOR_ID, detail.getCollId());
            i.putExtra(ActivityVisitResult.PARAM_LDV_NO, detail.getLdvNo());
        }

        if (i == null)
            return;

        startActivity(i);
    }

    @Override
    public void onStartRefresh() {
        fab.hide();
    }

    @Override
    public void onEndRefresh() {
        fab.show();
    }

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
