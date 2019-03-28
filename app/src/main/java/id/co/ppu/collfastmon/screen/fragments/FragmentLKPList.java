package id.co.ppu.collfastmon.screen.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicFragment;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.LKPDataMonitoring;
import id.co.ppu.collfastmon.pojo.trn.TrnContractBuckets;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.util.DemoUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLKPList extends BasicFragment {

    private static final String TAG = FragmentLKPList.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.etCollectorName)
    EditText etCollectorName;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.tvSeparator)
    TextView tvSeparator;

    @BindView(R.id.tvLastUpdate)
    TextView tvLastUpdate;

    LKPListAdapter listAdapter;
    private OnLKPListListener mListener;

    public FragmentLKPList() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLKPListListener) {
            mListener = (OnLKPListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnLKPListListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_lkplist, container, false);

        ButterKnife.bind(this, view);

        etTglLKP.setTypeface(fontGoogle);
        etNoLKP.setTypeface(fontGoogle);

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void dumpData(String collCode, Date lkpDate, LKPDataMonitoring data) {
        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        Realm r = Realm.getDefaultInstance();
        r.executeTransaction(bgRealm -> {
            bgRealm.delete(DisplayLDVDetails.class);

            boolean d = bgRealm.where(TrnLDVHeader.class)
                    .equalTo("collCode", collCode)
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            TrnLDVHeader trnLDVHeader = data.getHeader();
            bgRealm.copyToRealmOrUpdate(trnLDVHeader);

            d = bgRealm.where(TrnLDVDetails.class)
                    .equalTo("pk.ldvNo", trnLDVHeader.getLdvNo())
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            bgRealm.copyToRealm(data.getDetails());

            for (TrnLDVDetails obj : data.getDetails()) {

                DisplayLDVDetails row = new DisplayLDVDetails();

//                                        row.setLkpDate(Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN));

                row.setSeqNo(obj.getPk().getSeqNo());
                row.setLdvNo(obj.getPk().getLdvNo());
                row.setCollId(trnLDVHeader.getCollCode());
                row.setLkpDate(trnLDVHeader.getLdvDate());
//                                        row.setCollId(collectorCode);
                row.setContractNo(obj.getContractNo());
                row.setCustName(obj.getCustName());
                row.setCustNo(obj.getCustNo());
                row.setCreatedBy(obj.getCreatedBy());
                row.setFlagDone(obj.getFlagDone());
                row.setLdvFlag(obj.getLdvFlag());
                row.setWorkStatus(obj.getWorkStatus());
//                                        if (DataUtil.isLKPSynced(realm, obj) > 0) {
//                                            displayTrnLDVDetails.setWorkStatus("SYNC");
//                                        }else{
//                                            displayTrnLDVDetails.setWorkStatus(obj.getWorkStatus());
//                                        }

                bgRealm.copyToRealm(row);
            }

            d = bgRealm.where(TrnContractBuckets.class)
                    .equalTo("collectorId", collCode)
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            bgRealm.copyToRealm(data.getBuckets());

            d = bgRealm.where(TrnRVColl.class)
                    .equalTo("collId", collCode)
//                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            bgRealm.copyToRealm(data.getRvColl());

            d = bgRealm.where(TrnLDVComments.class)
                    .equalTo("pk.ldvNo", trnLDVHeader.getLdvNo())
//                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            bgRealm.copyToRealm(data.getLdvComments());

            if (data.getRepo().size() > 0) {
                String[] repos = new String[data.getRepo().size()];

                for (int i = 0; i < data.getRepo().size(); i++) {
                    repos[i] = data.getRepo().get(i).getContractNo();
                }

                d = bgRealm.where(TrnRepo.class)
                        .in("contractNo", repos)
//                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                        .findAll()
                        .deleteAllFromRealm();

                bgRealm.copyToRealmOrUpdate(data.getRepo());
            }

        });
        r.close();

    }

    protected void loadListFromLocal() {

        final String collCode = ((OnLKPListListener) getContext()).getCollCode();
        final Date lkpDate = ((OnLKPListListener) getContext()).getLKPDate();

        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        Realm bgRealm = Realm.getDefaultInstance();

        try {
            bgRealm.beginTransaction();
            bgRealm.delete(DisplayLDVDetails.class);

            TrnLDVHeader header = bgRealm.where(TrnLDVHeader.class)
                    .equalTo("collCode", collCode)
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findFirst();

            RealmResults<TrnLDVDetails> all = bgRealm.where(TrnLDVDetails.class)
                    .equalTo("pk.ldvNo", header.getLdvNo())
                    .equalTo("createdBy", createdBy)
                    .findAll();

            for (TrnLDVDetails obj : all) {
                DisplayLDVDetails row = new DisplayLDVDetails();

//                                        row.setLkpDate(Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN));

                row.setSeqNo(obj.getPk().getSeqNo());
                row.setLdvNo(obj.getPk().getLdvNo());
                row.setCollId(header.getCollCode());
                row.setLkpDate(header.getLdvDate());
//                                        row.setCollId(collectorCode);
                row.setContractNo(obj.getContractNo());
                row.setCustName(obj.getCustName());
                row.setCustNo(obj.getCustNo());
                row.setCreatedBy(obj.getCreatedBy());
                row.setFlagDone(obj.getFlagDone());
                row.setLdvFlag(obj.getLdvFlag());
                row.setWorkStatus(obj.getWorkStatus());

                bgRealm.copyToRealmOrUpdate(row);
            }

            bgRealm.commitTransaction();

        } finally {
            if (bgRealm != null)
                bgRealm.close();
        }

        // due to the limitations of realmsearchview, searchable column has been disabled
        // because when sort by seqNo, i cant search by custname
        listAdapter = new LKPListAdapter(
                this.realm.where(DisplayLDVDetails.class)
                        .sort("seqNo")
//                        .sort("rangeFCOMeters")
                        .findAll()
        );

        recycler_view.setAdapter(listAdapter);
        recycler_view.invalidate();

        tvLastUpdate.setText("Last Update: " + Utility.convertDateToString(new Date(), "HH:mm"));

        tvSeparator.setText("" + listAdapter.getItemCount() + " CONTRACTS");


    }

    public void loadListFromServer(final boolean showProgress, String collCode, String collName, String ldvNo, Date lkpDate) {

        etCollectorName.setText(collName);
        etNoLKP.setText(ldvNo);
        etTglLKP.setText(Utility.convertDateToString(lkpDate, Utility.DATE_DISPLAY_PATTERN));

        if (!NetUtil.isConnected(getActivity())) {

            if (DemoUtil.isDemo(getActivity())) {

                final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

//                final LKPData lkpData = DemoUtil.buildLKP(new Date(), currentUser.getUserId(), currentUser.getBranchId(), createdBy);
                final LKPDataMonitoring data = DemoUtil.buildLKPData(collCode, "26160192", "6200020170223026160192", lkpDate, createdBy);

                dumpData(collCode, lkpDate, data);

            }

            loadListFromLocal();

            return;
        }

        tvSeparator.setText("Please Wait...");

        APIonBuilder.getLKPByDate(getActivity(), collCode, lkpDate, (e, result) -> {

            if (e != null) {
                Log.e(TAG, e.getMessage(), e);
                return;
            }

            if (result.getData() == null)
                return;

            if (result.getError() != null) {
                Utility.showDialog(getContext(), "Error (" + result.getError().getErrorCode() + ")", result.getError().getErrorDesc());
                return;
            }

            dumpData(collCode, lkpDate, result.getData());
            loadListFromLocal();

        });

    }

    public class LKPListAdapter extends RealmRecyclerViewAdapter<DisplayLDVDetails, RecyclerView.ViewHolder> implements Filterable {

        private int lastPosition = -1;
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
//        public LKPListAdapter(
//                Context context,
//                RealmResults<DisplayLDVDetails> realmResults,
//                boolean automaticUpdate,
//                boolean animateIdType) {
//            super(context, realmResults, automaticUpdate, animateIdType);
//        }

        LKPListAdapter(RealmResults<DisplayLDVDetails> realmResults) {
            super(realmResults, true);

//            setSortKey("rangeFCOMeters");
//            setSortOrder(Sort.ASCENDING);
        }

        @Override
        public int getItemViewType(int position) {
            final DisplayLDVDetails detail = getData().get(position);

//            String collCode = detail.getCollCode();
//            if (collCode.equalsIgnoreCase("z"))
//                return TYPE_HEADER;
//            else
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//            View v = inflater.inflate(R.layout.row_lkp_list, viewGroup, false);

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_lkp_list, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
//            return new LKPListAdapter.DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder rvHolder, int position) {

            final DisplayLDVDetails detail = getData().get(position);

            if (rvHolder instanceof DataViewHolder) {
                DataViewHolder holder = (DataViewHolder) rvHolder;

                holder.llRowLKP.setOnClickListener(view -> {
                    if (getContext() instanceof OnLKPListListener) {
//                        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date(): Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                        ((OnLKPListListener) getContext()).onLKPSelected(detail);
                    }
                });

                holder.llRowLKP.setBackgroundColor(Color.WHITE);    // must
                if (detail.getWorkStatus() == null || detail.getWorkStatus().equalsIgnoreCase("V")) {
                    holder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPGreen));
                } else if (detail.getWorkStatus().equalsIgnoreCase("W")) {
                    holder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPBlue));
                }

            /*
            Button btnDetails = dataViewHolder.btnDetails;
            btnDetails.setVisibility(detail.getWorkStatus().equals("V") || detail.getWorkStatus() == null ? View.VISIBLE : View.INVISIBLE);
            btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnLKPListListener) {
//                        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date(): Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                        ((OnLKPListListener)getContext()).onLKPSelected(detail);
                    }
                }
            });
            */

                TextView tvCustName = holder.tvCustName;
                if (Build.VERSION.SDK_INT >= 24) {
                    tvCustName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvCustName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>"));
                }

                TextView tvNoContract = holder.tvNoContract;
                tvNoContract.setText(detail.getContractNo());

                TextView tvWorkStatus = holder.tvWorkStatus;
                if (Build.VERSION.SDK_INT >= 24) {
                    tvWorkStatus.setText(Html.fromHtml("Work Status : <strong>" + detail.getWorkStatus() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvWorkStatus.setText(Html.fromHtml("Work Status : <strong>" + detail.getWorkStatus() + "</strong>"));
                }

                TextView tvLKPFlag = holder.tvLKPFlag;
                tvLKPFlag.setText("LKP Flag : " + detail.getLdvFlag());

//            Animation animation = AnimationUtils.loadAnimation(getContext(),
//                    (position > lastPosition) ? R.anim.up_from_bottom
//                            : R.anim.down_from_top);
//            dataViewHolder.itemView.startAnimation(animation);

            }

            lastPosition = position;

        }

        public void filterResults(String text) {
            text = text == null ? null : text.toLowerCase().trim();
            RealmQuery<DisplayLDVDetails> query = realm.where(DisplayLDVDetails.class);
            if (!(text == null || "".equals(text))) {
                query.contains("custName", text, Case.INSENSITIVE); // TODO: change field
            }
            updateData(query.findAllAsync());
        }

        @Override
        public Filter getFilter() {
            LKPListFilter filter = new LKPListFilter(this);
            return filter;
        }

        private class LKPListFilter
                extends Filter {
            private final LKPListAdapter adapter;

            private LKPListFilter(LKPListAdapter adapter) {
                super();
                this.adapter = adapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapter.filterResults(constraint.toString());
            }
        }

        public class DataViewHolder extends RecyclerView.ViewHolder {

            public FrameLayout container;

            @BindView(R.id.llRowLKP)
            LinearLayout llRowLKP;

            @BindView(R.id.tvCustName)
            TextView tvCustName;

            @BindView(R.id.tvNoContract)
            TextView tvNoContract;

            @BindView(R.id.tvWorkStatus)
            TextView tvWorkStatus;

            @BindView(R.id.tvLKPFlag)
            TextView tvLKPFlag;

//            @BindView(R.id.btnDetails)
//            Button btnDetails;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);

                tvCustName.setTypeface(fontArizon);
                tvNoContract.setTypeface(fontGoogle);
                tvWorkStatus.setTypeface(fontGoogle);
                tvLKPFlag.setTypeface(fontGoogle);
            }
        }
    }

    public interface OnLKPListListener {
        void onLKPSelected(DisplayLDVDetails detail);
//        void onStartRefresh();
//        void onEndRefresh();

        String getCollCode();

        String getCollName();

        Date getLKPDate();

        String getLDVNo();
    }
}
