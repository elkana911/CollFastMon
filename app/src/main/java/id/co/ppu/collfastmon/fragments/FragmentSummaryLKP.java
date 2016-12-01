package id.co.ppu.collfastmon.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSummaryLKP extends Fragment {

//    public static final String ARG_PARAM1 = "collector.code";
//    public static final String PARAM_LKP_DATE = "lkpDate";

//    private String collectorCode;
//    private Date lkpDate = null;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.etCabang)
    EditText etCabang;

    @BindView(R.id.etARStaff)
    EditText etARStaff;

    // table
    @BindView(R.id.etTargetPenerimaan)
    EditText etTargetPenerimaan;

    @BindView(R.id.etTertagihPenerimaan)
    EditText etTertagihPenerimaan;

    @BindView(R.id.etTargetUnit)
    EditText etTargetUnit;

    @BindView(R.id.etTertagihUnit)
    EditText etTertagihUnit;

    // non lkp
    @BindView(R.id.etNonLKPPenerimaan)
    EditText etNonLKPPenerimaan;

    @BindView(R.id.etNonLKPUnit)
    EditText etNonLKPUnit;

    @BindView(R.id.etTotalTertagih)
    EditText etTotalTertagih;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            collectorCode = getArguments().getString(ARG_PARAM1);
//
//            long lkpdate = getArguments().getLong(PARAM_LKP_DATE);
//            this.lkpDate = new Date(lkpdate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary_lkp, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public void loadSummary(String collCode, Date serverDate) {

        if (TextUtils.isEmpty(collCode)) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        try {
//            Date serverDate = lkpDate == null ? new Date() : lkpDate;
//        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
            String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

            // TODO: ask pak yoce, jika kasusnya hr ini udah closebatch, inquiry hr kmrn akan donlot header kmrn ?
            TrnLDVHeader header = realm.where(TrnLDVHeader.class)
                    .equalTo("collCode", collCode)
                    .equalTo("createdBy", createdBy)
                    .findFirst();

            if (header == null) {
                return;
            }

            UserData userData = (UserData) Storage.getObjPreference(getContext(), Storage.KEY_USER, UserData.class);

            // based on collectorcode
            long unitTarget = 0;
            RealmResults<TrnLDVDetails> ldvDetailses = realm.where(TrnLDVDetails.class)
                    .equalTo("createdBy", createdBy)
                    .findAll();
            for (TrnLDVDetails ldvDetails : ldvDetailses) {
                if (ldvDetails.getPk().getLdvNo().equals(header.getLdvNo())) {
                    unitTarget += 1;
                }
            }
/*
        long unitTarget = this.realm.where(TrnLDVDetails.class)
//                .equalTo("")
                .count();
*/

            StringBuilder rvColl = new StringBuilder();
            rvColl.append(Utility.convertDateToString(serverDate, "dd"))
                    .append(Utility.convertDateToString(serverDate, "MM"))
                    .append(Utility.convertDateToString(serverDate, "yyyy"))
                    .append(collCode);

            RealmResults<TrnRVColl> trnRVColls = realm.where(TrnRVColl.class).findAll();

            long receivedAmount = realm.where(TrnRVColl.class)
                    .equalTo("pk.rvCollNo", rvColl.toString())
                    .isNull("ldvNo")
                    .sum("receivedAmount")
                    .longValue();

            // TODO: ask pak yoce maksudnya count(mc_trn_rvcoll.ldv_no) yg ldv_no nya null ??
            long unitNonLKP = realm.where(TrnRVColl.class)
                    .equalTo("pk.rvCollNo", rvColl.toString())
//                    .equalTo("collId", collCode)
                    .isNull("ldvNo")
                    .count();

//        long _totalRVColl = this.realm.where(TrnRVColl.class).count();

            long totalTertagih = realm.where(TrnRVColl.class)
                    .equalTo("pk.rvCollNo", rvColl.toString())
                    .sum("receivedAmount")
                    .longValue();

//        long totalTertagih = header.getPrncAC() + header.getIntrAC() + receivedAmount.longValue();

            etNoLKP.setText(header.getLdvNo());

            etTglLKP.setText(DateUtils.formatDateTime(getContext(), serverDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));
//        etTglLKP.setText(DateUtils.formatDateTime(getContext(), header.getLdvDate().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

            etCabang.setText(userData.getBranchName());

            etARStaff.setText(userData.getFullName());

            long targetPenerimaan = header.getPrncAMBC() + header.getIntrAMBC();
            etTargetPenerimaan.setText(Utility.convertLongToRupiah(targetPenerimaan));

            String ldvNo = header.getLdvNo();

            Number sumReceivedAmount = realm.where(TrnRVColl.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("pk.rvCollNo", rvColl.toString())
//                    .equalTo("collId", collCode)
                    .sum("receivedAmount");

            long tertagihPenerimaan = sumReceivedAmount.longValue(); //header.getPrncAC() + header.getIntrAC();
            etTertagihPenerimaan.setText(Utility.convertLongToRupiah(tertagihPenerimaan));

            etTargetUnit.setText(String.valueOf(unitTarget));

            long tertagihUnit = realm.where(TrnRVColl.class)
                    .equalTo("ldvNo", ldvNo)
                    .equalTo("pk.rvCollNo", rvColl.toString())
//                    .equalTo("collId", collCode)
                    .count();

//        long tertagihUnit = header.getUnitTotal();
            etTertagihUnit.setText(String.valueOf(tertagihUnit));

            etNonLKPPenerimaan.setText(Utility.convertLongToRupiah(receivedAmount));
            etNonLKPUnit.setText(String.valueOf(unitNonLKP));

            etTotalTertagih.setText(Utility.convertLongToRupiah(totalTertagih));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null)
                realm.close();
        }

    }


}
