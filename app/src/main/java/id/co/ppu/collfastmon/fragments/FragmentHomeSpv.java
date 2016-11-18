package id.co.ppu.collfastmon.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicFragment;
import id.co.ppu.collfastmon.component.DividerItemDecoration;
import id.co.ppu.collfastmon.listener.OnCollectorListListener;
import id.co.ppu.collfastmon.pojo.CollectorJob;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomeSpv extends BasicFragment {

    @BindView(R.id.etCabang)
    EditText etCabang;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.tvSeparator)
    TextView tvSeparator;

    @BindView(R.id.noData)
    View noData;

    @BindView(R.id.realm_recycler_view)
    RealmRecyclerView realm_recycler_view;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    CollListAdapter listAdapter;
    private OnCollectorListListener mListener;

    private DatePickerDialog.OnDateSetListener listenerDateTglLKP = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            etTglLKP.setText(Utility.convertDateToString(cal.getTime(), Utility.DATE_DISPLAY_PATTERN));

            realm_recycler_view.setVisibility(View.INVISIBLE);
            tvSeparator.setText("No Collector");

            // load cache
            final String createdBy = "JOB" + Utility.convertDateToString(cal.getTime(), "yyyyMMdd");

            fab.performClick();

        }
    };

    public void loadCollectorsFromServer(Date time) {
        ((OnCollectorListListener) getContext()).onCollLoad(time);
    }

    public void loadCollectorsFromLocal(Date time) {
        RealmResults<CollectorJob> rows =
                realm.where(CollectorJob.class).findAllSorted("countVisited", Sort.DESCENDING);

        long count = rows.size();
        String dateLabel = "Today";

        if (!Utility.isSameDay(new Date(), time)) {
            dateLabel = "";
        }

        tvSeparator.setText(dateLabel + " COLLECTORS: " + count);

        if (count < 1) {
            realm_recycler_view.setVisibility(View.INVISIBLE);

            noData.setVisibility(View.VISIBLE);
        } else {
            realm_recycler_view.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);
        }

        listAdapter = new CollListAdapter(this.getContext(), rows, true, true);

        realm_recycler_view.setAdapter(listAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_spv, container, false);

        ButterKnife.bind(this, view);
        UserData userData = (UserData) Storage.getObjPreference(getContext(), Storage.KEY_USER, UserData.class);

        etCabang.setText(userData.getBranchName());
        etTglLKP.setText(Utility.convertDateToString(new Date(), Utility.DATE_DISPLAY_PATTERN));

        etTglLKP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), listenerDateTglLKP, year, month, day).show();
            }
        });

//        realm_recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCollectorListListener) {
            mListener = (OnCollectorListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCollectorListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        if (getContext() instanceof OnCollectorListListener) {
            realm_recycler_view.setVisibility(View.INVISIBLE);

            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);

            loadCollectorsFromServer(dateLKP);
        }

    }

    public class CollListAdapter extends RealmBasedRecyclerViewAdapter<CollectorJob, CollListAdapter.DataViewHolder> {

        private int lastPosition = -1;

        public CollListAdapter(
                Context context,
                RealmResults<CollectorJob> realmResults,
                boolean automaticUpdate,
                boolean animateIdType) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_coll_list, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {

            final CollectorJob detail = realmResults.get(position);

            dataViewHolder.llRowLKP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnCollectorListListener) {
                        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                        ((OnCollectorListListener) getContext()).onCollSelected(detail, dateLKP);
                    }

                }
            });

            dataViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnCollectorListListener) {
                        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                        ((OnCollectorListListener) getContext()).onCollLocation(detail, dateLKP);
                    }

                }
            });

            TextView tvCollName = dataViewHolder.tvCollName;
            if (Build.VERSION.SDK_INT >= 24) {
                tvCollName.setText(Html.fromHtml("<strong>" + detail.getCollName() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvCollName.setText(Html.fromHtml("<strong>" + detail.getCollName() + "</strong>"));
            }

            TextView tvCollCode = dataViewHolder.tvCollCode;
            tvCollCode.setText(detail.getCollCode());

            TextView tvLastTask = dataViewHolder.tvLastTask;
            if (detail.getLastTask() == null) {
                tvLastTask.setText(null);
            }else
                tvLastTask.setText(detail.getLastTask() + " - " + Utility.convertDateToString(detail.getLastTaskTime(), "HH:mm:ss"));

            TextView tvLastLat = dataViewHolder.tvLastLat;
            tvLastLat.setText(detail.getLastLatitude());

            TextView tvLastLng = dataViewHolder.tvLastLng;
            tvLastLng.setText(detail.getLastLongitude());

            dataViewHolder.btnMap.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(detail.getLastLatitude())
                    || TextUtils.isEmpty(detail.getLastLongitude())
                    ) {
                dataViewHolder.btnMap.setVisibility(View.GONE);
            }

            TextView tvTotalVisited = dataViewHolder.tvTotalVisited;
            if (Build.VERSION.SDK_INT >= 24) {
                tvTotalVisited.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvTotalVisited.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP()));
            }

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

            @BindView(R.id.tvCollName)
            TextView tvCollName;

            @BindView(R.id.tvCollCode)
            TextView tvCollCode;

            @BindView(R.id.tvTotalVisited)
            TextView tvTotalVisited;

            @BindView(R.id.tvLastTask)
            TextView tvLastTask;

            @BindView(R.id.tvLastLat)
            TextView tvLastLat;

            @BindView(R.id.tvLastLng)
            TextView tvLastLng;

            @BindView(R.id.btnMap)
            ImageView btnMap;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);
            }
        }
    }

}
