package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;

public class ActivityVisitResultRPC extends BasicActivity {
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    @BindView(R.id.activity_visit_result_rpc)
    View activityVisitResultRPC;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etTglTransaksi)
    EditText etTglTransaksi;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

    public static Intent createIntent(Context ctx, DisplayLDVDetails detail) {
        Intent i = new Intent(ctx, ActivityVisitResultRPC.class);
        i.putExtra(PARAM_CONTRACT_NO, detail.getContractNo());
        i.putExtra(PARAM_COLLECTOR_ID, detail.getCollId());
        i.putExtra(PARAM_LDV_NO, detail.getLdvNo());

        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_result_rpc);

        ButterKnife.bind(this);

        etContractNo.setTypeface(fontGoogle);
        etTglTransaksi.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", this.contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_visit_result);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
//        etTglTransaksi.setText(dtl.getContractNo());
//        etKomentar.setText(dtl.getContractNo());

        // load last save
        TrnLDVComments trnLDVComments = this.getLDVComments(realm, this.ldvNo, this.contractNo).findFirst();

        if (trnLDVComments != null) {
            etKomentar.setText(trnLDVComments.getLkpComments());
        }
    }
}
