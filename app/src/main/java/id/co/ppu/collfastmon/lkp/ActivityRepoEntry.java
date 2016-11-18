package id.co.ppu.collfastmon.lkp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_entry);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractNo = extras.getString(PARAM_CONTRACT_NO);
            collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_repo_entry);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        etPAL.setText(dtl.getPalNo());

//        etTglTransaksi.setText(Utility.convertDateToString(serverDate, "dd-MMM-yyyy"));

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class).findFirst();

        TrnRepo trnRepo = this.realm.where(TrnRepo.class)
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
