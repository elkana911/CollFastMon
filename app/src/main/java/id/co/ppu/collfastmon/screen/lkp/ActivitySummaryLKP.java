package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import java.util.Date;

import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.screen.fragments.FragmentSummaryLKP;

public class ActivitySummaryLKP extends BasicActivity {

    public static final String PARAM_COLL_CODE = "collector.code";
    public static final String PARAM_LKP_DATE = "lkpDate";

    public String collCode = null;
    public Date lkpDate = null;

    public static Intent createIntent(Context ctx, String collCode, long lkpDate) {
        Intent i = new Intent(ctx, ActivitySummaryLKP.class);
        i.putExtra(PARAM_COLL_CODE, collCode);
        i.putExtra(PARAM_LKP_DATE, lkpDate);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_lkp);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        this.collCode = extras.getString(PARAM_COLL_CODE);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_summary_lkp);
//            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fr instanceof FragmentSummaryLKP) {
            ((FragmentSummaryLKP)fr).loadSummary(this.collCode, this.lkpDate);
        }

    }

}
