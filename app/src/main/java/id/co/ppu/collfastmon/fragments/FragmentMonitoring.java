package id.co.ppu.collfastmon.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.DividerItemDecoration;
import id.co.ppu.collfastmon.pojo.DisplayLDVDetails;
import id.co.ppu.collfastmon.pojo.LKPDataMonitoring;
import id.co.ppu.collfastmon.pojo.trn.TrnContractBuckets;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.request.RequestLKPByDate;
import id.co.ppu.collfastmon.rest.response.ResponseGetLKPMonitoring;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.data;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentMonitoring extends Fragment {

    @BindView(R.id.realm_recycler_view)
    RealmRecyclerView realm_recycler_view;

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

    public FragmentMonitoring() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLKPListListener) {
            mListener = (OnLKPListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLKPListListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_mon, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm_recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        realm_recycler_view.setOnRefreshListener(
                new RealmRecyclerView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //harus pake asynctask dan sejenisnya
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                setListVisibility(false);

                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                loadListFromServer(true);

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                // dismiss loading progress
                                realm_recycler_view.setRefreshing(false);
                            }
                        }.execute();


                    }
                }
        );
        realm_recycler_view.setOnLoadMoreListener(
                new RealmRecyclerView.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(Object lastItem) {

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                // utk ilangin loading progress
                                realm_recycler_view.disableShowLoadMore();
                            }
                        }.execute();

                    }
                }
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        setListVisibility(false);
//        realm_recycler_view.setAdapter(null);
        if (listAdapter == null) {
            Realm r = Realm.getDefaultInstance();

            boolean b = DataUtil.isMasterDataDownloaded(getContext(), r, false);

            try {
                RealmResults<DisplayLDVDetails> rows =
                        r.where(DisplayLDVDetails.class).findAll();

                listAdapter = new LKPListAdapter(getContext(), rows, true, true);
            } finally {
                if (r != null)
                    r.close();
            }
        }

        realm_recycler_view.setAdapter(listAdapter);

        tvSeparator.setText("" + listAdapter.getItemCount() + " CONTRACTS");

        OnLKPListListener interfc = ((OnLKPListListener) getContext());
        etCollectorName.setText(interfc.getCollName());
        etNoLKP.setText(interfc.getLDVNo());
        etTglLKP.setText(Utility.convertDateToString(interfc.getLKPDate(), Utility.DATE_DISPLAY_PATTERN));

        loadListFromServer(true);
    }

    private void dumpData(String collCode, Date lkpDate, Realm bgRealm, LKPDataMonitoring data) {
        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        bgRealm.delete(DisplayLDVDetails.class);

        boolean d = bgRealm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                .findAll()
                .deleteAllFromRealm();

        TrnLDVHeader trnLDVHeader = data.getHeader();
        bgRealm.copyToRealm(trnLDVHeader);

        d = bgRealm.where(TrnLDVDetails.class)
                .equalTo("pk.ldvNo", trnLDVHeader.getLdvNo())
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                .findAll()
                .deleteAllFromRealm();

        bgRealm.copyToRealm(data.getDetails());

        for (TrnLDVDetails obj : data.getDetails()) {

            DisplayLDVDetails row = bgRealm.createObject(DisplayLDVDetails.class);

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
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                .findAll()
                .deleteAllFromRealm();

        bgRealm.copyToRealm(data.getRvColl());

        d = bgRealm.where(TrnLDVComments.class)
                .equalTo("pk.ldvNo", trnLDVHeader.getLdvNo())
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
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
                    .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                    .findAll()
                    .deleteAllFromRealm();

            bgRealm.copyToRealmOrUpdate(data.getRepo());
        }

    }

    public void loadListFromLocal() {
//        dumpData(collCode, lkpDate, bgRealm, respGetLKPMonitoring.getData());

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
                DisplayLDVDetails row = bgRealm.createObject(DisplayLDVDetails.class);

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

            }

        } finally{
            bgRealm.commitTransaction();
            if (bgRealm != null)
                bgRealm.close();
        }

        tvLastUpdate.setText("Last Update: OFFLINE");

        tvSeparator.setText("" + listAdapter.getItemCount() + " CONTRACTS");

        setListVisibility(true);

    }

    public synchronized void loadListFromServer(final boolean showProgress) {

        if (!NetUtil.isConnected(getActivity())) {
            loadListFromLocal();
            return;
        }

        // get value from activity. jgn di onCreateView krn blm
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras == null) {
            return;
        }

        if (showProgress) {
            realm_recycler_view.setRefreshing(true);
        }

        if (!(getContext() instanceof OnLKPListListener)) {
            return;
        }

        ((OnLKPListListener) getContext()).onStartRefresh();
        final String collCode = ((OnLKPListListener) getContext()).getCollCode();
        final Date lkpDate = ((OnLKPListListener) getContext()).getLKPDate();

        tvSeparator.setText("Please Wait...");

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getContext(), Storage.KEY_SERVER_ID, 0)));

        RequestLKPByDate req = new RequestLKPByDate();
        req.setCollectorCode(collCode);
        req.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        Call<ResponseGetLKPMonitoring> call = fastService.getLKPByDate(req);
        call.enqueue(new Callback<ResponseGetLKPMonitoring>() {
            @Override
            public void onResponse(Call<ResponseGetLKPMonitoring> call, Response<ResponseGetLKPMonitoring> response) {

                setListVisibility(true);

                if (getContext() instanceof OnLKPListListener) {
                    ((OnLKPListListener) getContext()).onEndRefresh();
                }

                realm_recycler_view.setRefreshing(false);

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(getContext(), "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                final ResponseGetLKPMonitoring respGetLKPMonitoring = response.body();

                if (respGetLKPMonitoring == null || respGetLKPMonitoring.getData() == null) {
                    Utility.showDialog(getContext(), "No LKP found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (respGetLKPMonitoring.getError() != null) {
                    Utility.showDialog(getContext(), "Error (" + respGetLKPMonitoring.getError().getErrorCode() + ")", respGetLKPMonitoring.getError().getErrorDesc());
                    return;
                }

                // save db here
                final Realm r = Realm.getDefaultInstance();
                r.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {

                        dumpData(collCode, lkpDate, bgRealm, respGetLKPMonitoring.getData());

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
//                                    notify to fix java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position 13(offset:13).state:15
                        listAdapter.notifyDataSetChanged();

                        tvLastUpdate.setText("Last Update: " + Utility.convertDateToString(new Date(), "HH:mm"));

                        tvSeparator.setText("" + listAdapter.getItemCount() + " CONTRACTS");

                        r.close();

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // Transaction failed and was automatically canceled.
                        Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_LONG).show();
                        error.printStackTrace();

                        r.close();
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponseGetLKPMonitoring> call, Throwable t) {

                setListVisibility(true);

                tvSeparator.setText("NO COLLECTOR");

                if (getContext() instanceof OnLKPListListener) {
                    ((OnLKPListListener) getContext()).onEndRefresh();

                    Utility.showDialog(getContext(), "Server Problem", t.getMessage());
                }

                realm_recycler_view.setRefreshing(false);

            }
        });

    }

    public void setListVisibility(boolean b) {
        realm_recycler_view.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }


    public class LKPListAdapter extends RealmBasedRecyclerViewAdapter<DisplayLDVDetails, LKPListAdapter.DataViewHolder> {

        private int lastPosition = -1;

        public LKPListAdapter(
                Context context,
                RealmResults<DisplayLDVDetails> realmResults,
                boolean automaticUpdate,
                boolean animateIdType) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        @Override
        public LKPListAdapter.DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_lkp_list, viewGroup, false);
            return new LKPListAdapter.DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(LKPListAdapter.DataViewHolder dataViewHolder, int position) {

            final DisplayLDVDetails detail = realmResults.get(position);

            /*
            dataViewHolder.llRowLKP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnLKPListListener) {
//                        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date(): Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                        ((OnLKPListListener)getContext()).onLKPSelected(detail);
                    }
                }
            });
            */

            dataViewHolder.llRowLKP.setBackgroundColor(Color.WHITE);    // must
            if (detail.getWorkStatus() == null || detail.getWorkStatus().equalsIgnoreCase("V")) {
                dataViewHolder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPGreen));
            } else if (detail.getWorkStatus().equalsIgnoreCase("W")) {
                dataViewHolder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPBlue));
            }

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

            TextView tvCustName = dataViewHolder.tvCustName;
            if (Build.VERSION.SDK_INT >= 24) {
                tvCustName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvCustName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>"));
            }

            TextView tvNoContract = dataViewHolder.tvNoContract;
            tvNoContract.setText(detail.getContractNo());

            TextView tvWorkStatus = dataViewHolder.tvWorkStatus;
            if (Build.VERSION.SDK_INT >= 24) {
                tvWorkStatus.setText(Html.fromHtml("Work Status : <strong>" + detail.getWorkStatus() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvWorkStatus.setText(Html.fromHtml("Work Status : <strong>" + detail.getWorkStatus() + "</strong>"));
            }

            TextView tvLKPFlag = dataViewHolder.tvLKPFlag;
            tvLKPFlag.setText("LKP Flag : " + detail.getLdvFlag());

            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            dataViewHolder.itemView.startAnimation(animation);
            lastPosition = position;

        }

        public class DataViewHolder extends RealmSearchViewHolder {

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

            @BindView(R.id.btnDetails)
            Button btnDetails;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);
            }
        }
    }

    public interface OnLKPListListener {
        void onLKPSelected(DisplayLDVDetails detail);
        void onStartRefresh();
        void onEndRefresh();

        String getCollCode();
        String getCollName();
        Date getLKPDate();
        String getLDVNo();
    }
}
