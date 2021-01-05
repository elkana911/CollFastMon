package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import id.co.ppu.collfastmon.util.Utility;

public class ActivityRepoEntry extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    @BindView(R.id.activity_repo_entri)
    View activityRepoEntri;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etPAL)
    EditText etPAL;

    @BindView(R.id.etTglTransaksi)
    EditText etTglTransaksi;

    @BindView(R.id.etKodeTarik)
    EditText etKodeTarik;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

    @BindView(R.id.etBASTK)
    EditText etBASTK;

    @BindView(R.id.radioSTNK)
    RadioButton radioSTNK;

    @BindView(R.id.radioNoSTNK)
    RadioButton radioNoSTNK;

    public static Intent createIntent(Context ctx, DisplayLDVDetails detail){
        Intent i = new Intent(ctx, ActivityRepoEntry.class);
        i.putExtra(PARAM_CONTRACT_NO, detail.getContractNo());
        i.putExtra(PARAM_COLLECTOR_ID, detail.getCollId());
        i.putExtra(PARAM_LDV_NO, detail.getLdvNo());

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_entry);

        ButterKnife.bind(this);
        etContractNo.setTypeface(fontGoogle);
        etPAL.setTypeface(fontGoogle);
        etBASTK.setTypeface(fontGoogle);
        etKodeTarik.setTypeface(fontGoogle);
        etTglTransaksi.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractNo = extras.getString(PARAM_CONTRACT_NO);
            collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

        TrnLDVDetails dtl = getRealmInstance().where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_repo_entry);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        etPAL.setText(dtl.getPalNo());

//        etTglTransaksi.setText(Utility.convertDateToString(serverDate, "dd-MMM-yyyy"));

        TrnLDVHeader header = getRealmInstance().where(TrnLDVHeader.class).findFirst();

        TrnRepo trnRepo = getRealmInstance().where(TrnRepo.class)
                .equalTo("contractNo", contractNo)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRepo != null) {
            etKomentar.setText(trnRepo.getRepoComments());
            etKodeTarik.setText(trnRepo.getRepoNo());
            etBASTK.setText(trnRepo.getBastbjNo());
//            spBASTK.setText(trnRepo.getBastbjNo());

            radioSTNK.setChecked(trnRepo.getStnkStatus() != null && trnRepo.getStnkStatus().equals("Y"));
            radioNoSTNK.setChecked(trnRepo.getStnkStatus() == null || !trnRepo.getStnkStatus().equals("Y"));

        }

    }
}
