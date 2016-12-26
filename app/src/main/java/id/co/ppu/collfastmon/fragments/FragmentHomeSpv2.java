package id.co.ppu.collfastmon.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomeSpv2 extends BasicFragment {

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
        RealmResults<CollJob> rows =
                realm.where(CollJob.class)
//                        .greaterThanOrEqualTo("lkpDate", time)
                        .findAllSorted("countVisited", Sort.DESCENDING);

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
        /*
        realm_recycler_view.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int textSize = 50;
            private int groupSpacing = 100;
//            private int itemsInGroup = 3;

            private Paint paint = new Paint();
            {
                paint.setTextSize(textSize);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) % itemsInGroup == 0) {
                    outRect.set(0, groupSpacing, 0, 0);
                }
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View view = parent.getChildAt(i);
                    int position = parent.getChildAdapterPosition(view);
                    if (position % itemsInGroup == 0) {
                        c.drawText("Group " + (position / itemsInGroup + 1), view.getLeft(),
                                view.getTop() - groupSpacing / 2 + textSize / 3, paint);
                    }
                }
            }
        });

        */
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

    public class CollListAdapter extends RealmBasedRecyclerViewAdapter<CollJob, CollListAdapter.DataViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private int lastPosition = -1;

        public CollListAdapter(
                Context context,
                RealmResults<CollJob> realmResults,
                boolean automaticUpdate,
                boolean animateIdType) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
                View v = inflater.inflate(R.layout.row_coll_list, viewGroup, false);
                return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {

            final CollJob detail = realmResults.get(position);

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

            boolean isSameDay = Utility.isSameDay(detail.getLkpDate(), new Date());

            if (isSameDay) {
                dataViewHolder.llHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRadanaBlue));
                dataViewHolder.tvTotVisit.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRadanaBlue));
            } else {
                dataViewHolder.llHeader.setBackgroundColor(Color.parseColor("#808080"));
                dataViewHolder.tvTotVisit.setBackgroundColor(Color.parseColor("#808080"));
            }

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
            dataViewHolder.pulsator.stop();
            if (TextUtils.isEmpty(detail.getLastLatitude())
                    || TextUtils.isEmpty(detail.getLastLongitude())
                    ) {
                dataViewHolder.btnMap.setVisibility(View.GONE);
            } else {
                // kalo hari yg sama saja
                if (isSameDay)
                    dataViewHolder.pulsator.start();
            }

            TextView tvTotalVisited = dataViewHolder.tvTotVisit;
            if (Build.VERSION.SDK_INT >= 24) {
                tvTotalVisited.setText(Html.fromHtml("" + detail.getCountVisited() + "/" + detail.getCountLKP(), Html.FROM_HTML_MODE_LEGACY));
//                tvTotalVisited.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvTotalVisited.setText(Html.fromHtml("" + detail.getCountVisited() + "/" + detail.getCountLKP()));
//                tvTotalVisited.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP()));
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

            @BindView(R.id.llHeader)
            LinearLayout llHeader;

            @BindView(R.id.tvCollName)
            TextView tvCollName;

            @BindView(R.id.tvCollCode)
            TextView tvCollCode;

            @BindView(R.id.tvTotVisit)
            TextView tvTotVisit;

            @BindView(R.id.tvLastTask)
            TextView tvLastTask;

            @BindView(R.id.tvLastLat)
            TextView tvLastLat;

            @BindView(R.id.tvLastLng)
            TextView tvLastLng;

            @BindView(R.id.btnMap)
            ImageView btnMap;

            @BindView(R.id.pulsator)
            PulsatorLayout pulsator;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;

                ButterKnife.bind(this, container);
            }
        }


        class VHHeader extends RealmSearchViewHolder {
            TextView txtTitle;

            public VHHeader(View itemView) {
                super(itemView);
                this.txtTitle = (TextView)itemView.findViewById(R.id.txtHeader);
            }
        }
    }

    public interface OnCollectorListListener {
        void onCollSelected(CollJob detail, Date lkpDate);
        void onCollLoad(Date lkpDate);
//        void onStartRefresh();
//        void onEndRefresh();

        void onCollLocation(CollJob detail, Date lkpDate);
    }

}
