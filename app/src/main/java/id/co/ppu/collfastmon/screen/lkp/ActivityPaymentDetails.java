package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.master.MstDelqReasons;
import id.co.ppu.collfastmon.pojo.master.MstLDVClassifications;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmResults;

public class ActivityPaymentDetails extends BasicActivity {

    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_CONTRACT_NO = "contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_LKP_DATE = "lkpDate";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;
    private Date lkpDate = null;

    @BindView(R.id.activity_payment_receive)
    View activityPaymentReceive;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

    @BindView(R.id.etPlatform)
    EditText etPlatform;

    @BindView(R.id.etAngsuranKe)
    EditText etAngsuranKe;

    @BindView(R.id.etAngsuran)
    EditText etAngsuran;

    @BindView(R.id.etDenda)
    EditText etDenda;

    @BindView(R.id.etDendaBerjalan)
    EditText etDendaBerjalan;

    @BindView(R.id.etBiayaTagih)
    EditText etBiayaTagih;

    @BindView(R.id.etDanaSosial)
    EditText etDanaSosial;

    @BindView(R.id.etPenerimaan)
    EditText etPenerimaan;

    @BindView(R.id.etCatatan)
    EditText etCatatan;

    @BindView(R.id.etNoRVB)
    EditText etNoRVB;

    @BindView(R.id.etKlasifikasi)
    EditText etKlasifikasi;

    @BindView(R.id.etAlasan)
    EditText etAlasan;

    public static Intent createIntent(Context ctx, DisplayLDVDetails detail){
        Intent i = new Intent(ctx, ActivityPaymentDetails.class);
        i.putExtra(PARAM_CONTRACT_NO, detail.getContractNo());
        i.putExtra(PARAM_LKP_DATE, detail.getLkpDate().getTime());
        i.putExtra(PARAM_COLLECTOR_ID, detail.getCollId());
        i.putExtra(PARAM_LDV_NO, detail.getLdvNo());

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        ButterKnife.bind(this);

        etContractNo.setTypeface(fontGoogle);
        etPlatform.setTypeface(fontGoogle);
        etNoRVB.setTypeface(fontGoogle);
        etKlasifikasi.setTypeface(fontGoogle);
        etAlasan.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.ldvNo = extras.getString(PARAM_LDV_NO);

            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);

            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);

        }

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_payment_dtl);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(contractNo);
        etAngsuran.setText(Utility.convertLongToRupiah(dtl.getMonthInst()));
        etAngsuranKe.setText(String.valueOf(dtl.getInstNo() + 1));
        etPlatform.setText(dtl.getPlatform());

        // load last save
        TrnRVColl trnRVColl = realm.where(TrnRVColl.class)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .equalTo("contractNo", contractNo)
                .findFirst();

        if (trnRVColl != null) {

            etPenerimaan.setText(Utility.convertLongToRupiah(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());

            etDenda.setText(Utility.convertLongToRupiah(trnRVColl.getPenaltyAc()));
//            etDendaBerjalan.setText(String.valueOf(trnRVColl.getDaysIntrAc()));
            etBiayaTagih.setText(Utility.convertLongToRupiah(trnRVColl.getCollFeeAc()));
            etDanaSosial.setText(Utility.convertLongToRupiah(0L));
            etNoRVB.setText(trnRVColl.getPk().getRbvNo());

            etKlasifikasi.setText(trnRVColl.getClassCode());
            RealmResults<MstLDVClassifications> rrLkpKlasifikasi = this.realm.where(MstLDVClassifications.class).findAll();
            for (MstLDVClassifications obj : rrLkpKlasifikasi) {
                if (obj.getClassCode().equals(trnRVColl.getClassCode())) {
                    etKlasifikasi.setText(obj.getDescription());
                    break;
                }
            }

            etAlasan.setText(trnRVColl.getDelqCode());
            RealmResults<MstDelqReasons> rrAlasan = this.realm.where(MstDelqReasons.class).findAll();
            for (MstDelqReasons obj : rrAlasan) {
                if (obj.getDelqCode().equals(trnRVColl.getDelqCode())) {
                    etAlasan.setText(obj.getDescription());
                    break;
                }
            }

        }

    }
}
