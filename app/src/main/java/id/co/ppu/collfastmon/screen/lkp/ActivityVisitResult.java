package id.co.ppu.collfastmon.screen.lkp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.master.MstDelqReasons;
import id.co.ppu.collfastmon.pojo.master.MstLDVClassifications;
import id.co.ppu.collfastmon.pojo.master.MstLDVParameters;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmResults;

public class ActivityVisitResult extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    @BindView(R.id.activity_visit_result)
    View activityVisitResult;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etJob)
    AutoCompleteTextView etJob;

    @BindView(R.id.etSubJob)
    AutoCompleteTextView etSubJob;

    @BindView(R.id.etTglJanjiBayar)
    EditText etTglJanjiBayar;

    @BindView(R.id.etLKPFlag)
    EditText etLKPFlag;

    @BindView(R.id.etKlasifikasi)
    EditText etKlasifikasi;

    @BindView(R.id.etAlasan)
    EditText etAlasan;

    @BindView(R.id.etPotensi)
    EditText etPotensi;

//    @BindView(R.id.spPotensi)
//    Spinner spPotensi;

    @BindView(R.id.etBertemuDengan)
    EditText etBertemuDengan;

    @BindView(R.id.etTindakSelanjutnya)
    EditText etTindakSelanjutnya;

//    @BindView(R.id.spTindakSelanjutnya)
//    Spinner spTindakSelanjutnya;

    @BindView(R.id.etJanjiBayar)
    EditText etJanjiBayar;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

//    @BindView(R.id.spLKPFlags)
//    Spinner spLKPFlags;

//    @BindView(R.id.spKlasifikasi)
//    Spinner spKlasifikasi;

//    @BindView(R.id.spAlasan)
//    Spinner spAlasan;

    public static Intent createIntent(Context ctx, DisplayLDVDetails detail) {
        Intent i = new Intent(ctx, ActivityVisitResult.class);
        i.putExtra(PARAM_CONTRACT_NO, detail.getContractNo());
        i.putExtra(PARAM_COLLECTOR_ID, detail.getCollId());
        i.putExtra(PARAM_LDV_NO, detail.getLdvNo());

        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_result);

        ButterKnife.bind(this);

        etContractNo.setTypeface(fontGoogle);
        etJob.setTypeface(fontGoogle);
        etSubJob.setTypeface(fontGoogle);
        etLKPFlag.setTypeface(fontGoogle);
        etKlasifikasi.setTypeface(fontGoogle);
        etAlasan.setTypeface(fontGoogle);
        etPotensi.setTypeface(fontGoogle);
        etBertemuDengan.setTypeface(fontGoogle);
        etTindakSelanjutnya.setTypeface(fontGoogle);
        etTglJanjiBayar.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

        TrnLDVDetails dtl = getRealmInstance().where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_visit_result);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        etJob.setText(dtl.getOccupation());
        etSubJob.setText(dtl.getSubOccupation());

        /*
        RealmResults<MstLDVParameters> rrLkpParameters = this.realm.where(MstLDVParameters.class).findAll();
        List<MstLDVParameters> ss = this.realm.copyFromRealm(rrLkpParameters);
        LKPParameterAdapter adapterLKPFlag = new LKPParameterAdapter(this, android.R.layout.simple_spinner_item, ss);
        MstLDVParameters hint = new MstLDVParameters();
        hint.setDescription(getString(R.string.spinner_please_select));
        adapterLKPFlag.insert(hint, 0);
        spLKPFlags.setAdapter(adapterLKPFlag);
        spLKPFlags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MstLDVParameters obj = (MstLDVParameters) adapterView.getItemAtPosition(i);
                etTglJanjiBayar.setVisibility(obj.getLkpFlag() != null && obj.getLkpFlag().equals("PTP") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RealmResults<MstLDVClassifications> rrLkpKlasifikasi = this.realm.where(MstLDVClassifications.class).findAll();
        List<MstLDVClassifications> lkpKlasifikasi = this.realm.copyFromRealm(rrLkpKlasifikasi);
        KlasifikasiAdapter adapterKlasifikasi = new KlasifikasiAdapter(this, android.R.layout.simple_spinner_item, lkpKlasifikasi);
        MstLDVClassifications hintKlasifikasi = new MstLDVClassifications();
        hintKlasifikasi.setDescription(getString(R.string.spinner_please_select));
        adapterKlasifikasi.insert(hintKlasifikasi, 0);
        spKlasifikasi.setAdapter(adapterKlasifikasi);
        spKlasifikasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updatePotensiList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RealmResults<MstDelqReasons> rrAlasan = this.realm.where(MstDelqReasons.class).findAll();
        List<MstDelqReasons> lkpAlasan = this.realm.copyFromRealm(rrAlasan);
        AlasanAdapter adapterAlasan = new AlasanAdapter(this, android.R.layout.simple_spinner_item, lkpAlasan);
        MstDelqReasons hintAlasan = new MstDelqReasons();
        hintAlasan.setDescription(getString(R.string.spinner_please_select));
        adapterAlasan.insert(hintAlasan, 0);
        spAlasan.setAdapter(adapterAlasan);
        spAlasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updatePotensiList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RealmResults<MstParam> rrTindakSelanjutnya = this.realm.where(MstParam.class).equalTo("moduleId", 10)
                .findAll();
        List<MstParam> listTindakSelanjutnya = this.realm.copyFromRealm(rrTindakSelanjutnya);
        TindakSelanjutnyaAdapter adapterTindakSelanjutnya = new TindakSelanjutnyaAdapter(this, android.R.layout.simple_spinner_item, listTindakSelanjutnya);
        MstParam hintTindakSelanjutnya = new MstParam();
        hintTindakSelanjutnya.setNotes(getString(R.string.spinner_please_select));
        adapterTindakSelanjutnya.insert(hintTindakSelanjutnya, 0);
        spTindakSelanjutnya.setAdapter(adapterTindakSelanjutnya);
        spTindakSelanjutnya.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
        // load last save
        TrnLDVComments trnLDVComments = realm.where(TrnLDVComments.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnLDVComments != null) {
            /*
            String lkpFlag = dtl.getLdvFlag();
            for(int i = 0; i < adapterLKPFlag.getCount(); i++) {
                if(lkpFlag.equals(adapterLKPFlag.getItem(i).getLkpFlag())){
                    spLKPFlags.setSelection(i);
                    break;
                }
            }


            String classCode = trnLDVComments.getClassCode();
            for(int i = 0; i < adapterKlasifikasi.getCount(); i++) {
                if(classCode.equals(adapterKlasifikasi.getItem(i).getClassCode())){
                    spKlasifikasi.setSelection(i);
                    break;
                }
            }

            String delqCode = trnLDVComments.getDelqCode();
            for(int i = 0; i < adapterAlasan.getCount(); i++) {
                if(delqCode.equals(adapterAlasan.getItem(i).getDelqCode())){
                    spAlasan.setSelection(i);
                    break;
                }
            }

            Long potensi = trnLDVComments.getPotensi();
            PotensiAdapter adapterPotensi = (PotensiAdapter) spPotensi.getAdapter();
            for(int i = 0; i < adapterPotensi.getCount(); i++) {
                if(potensi.longValue() == adapterPotensi.getItem(i).getPotensi().longValue()){
                    spPotensi.setSelection(i);
                    break;
                }
            }

            String actionPlanCode = trnLDVComments.getActionPlan();
            for(int i = 0; i < adapterTindakSelanjutnya.getCount(); i++) {
                if(actionPlanCode.equals(adapterTindakSelanjutnya.getItem(i).getValue())){
                    spTindakSelanjutnya.setSelection(i);
                    break;
                }
            }
*/
            if (trnLDVComments.getPromiseDate() != null)
                etTglJanjiBayar.setText(Utility.convertDateToString(trnLDVComments.getPromiseDate(), "d / M / yyyy"));

            if (trnLDVComments.getPlanPayAmount() != null)
                etJanjiBayar.setText(Utility.convertLongToRupiah(trnLDVComments.getPlanPayAmount()));

            etBertemuDengan.setText(trnLDVComments.getWhoMet());
            etTindakSelanjutnya.setText(trnLDVComments.getApDescription());


            etLKPFlag.setText(dtl.getLdvFlag());
            RealmResults<MstLDVParameters> rrLKPFlag = this.realm.where(MstLDVParameters.class).findAll();
            for (MstLDVParameters obj : rrLKPFlag) {
                if (obj.getLkpFlag().equals(dtl.getLdvFlag())) {
                    etLKPFlag.setText(obj.getDescription());
                    break;
                }
            }

            etTglJanjiBayar.setVisibility(dtl.getLdvFlag() != null && dtl.getLdvFlag().equals("PTP") ? View.VISIBLE : View.GONE);


            etKlasifikasi.setText(trnLDVComments.getClassCode());
            RealmResults<MstLDVClassifications> rrLkpKlasifikasi = this.realm.where(MstLDVClassifications.class).findAll();
            for (MstLDVClassifications obj : rrLkpKlasifikasi) {
                if (obj.getClassCode().equals(trnLDVComments.getClassCode())) {
                    etKlasifikasi.setText(obj.getDescription());
                    break;
                }
            }

            etAlasan.setText(trnLDVComments.getDelqCode());
            RealmResults<MstDelqReasons> rrAlasan = this.realm.where(MstDelqReasons.class).findAll();
            for (MstDelqReasons obj : rrAlasan) {
                if (obj.getDelqCode().equals(trnLDVComments.getDelqCode())) {
                    etAlasan.setText(obj.getDescription());
                    break;
                }
            }

            etPotensi.setText(String.valueOf(trnLDVComments.getPotensi()) + " %");
            etKomentar.setText(trnLDVComments.getLkpComments());
        }

    }
}
