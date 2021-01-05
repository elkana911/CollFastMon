package id.co.ppu.collfastmon.screen.home;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicFragment;
import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Case;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

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

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

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

            recycler_view.setVisibility(View.INVISIBLE);
            tvSeparator.setText("No Collector");

            // load cache
            final String createdBy = "JOB" + Utility.convertDateToString(cal.getTime(), "yyyyMMdd");

            fab.performClick();
        }
    };

    public void loadCollectorsFromLocal(Date time) {

        checkRealmInstance();

        RealmResults<CollJob> rows = realm.where(CollJob.class)
//                        .greaterThanOrEqualTo("lkpDate", time)
//                        .findAllSorted("countVisited", Sort.DESCENDING, "collName", Sort.ASCENDING);
                        .findAll().sort(new String[]{"countLKP", "countVisited", "collName"}, new Sort[]{Sort.DESCENDING, Sort.DESCENDING, Sort.ASCENDING});

        String dateLabel = "Today";

        if (!Utility.isSameDay(new Date(), time)) {
            dateLabel = "";
        }

        //antisipasi collCode huruf Z
//        long count = rows.size();
        long count =
                realm.where(CollJob.class)
                        .notEqualTo("collCode", "Z")
                        .count();

        long activeColl = realm.where(CollJob.class)
                .greaterThan("countLKP", 0)
                .isNotNull("ldvNo")
                .count();

        tvSeparator.setText(dateLabel + " COLLECTORS: " + count + " (" + activeColl + " Assigned)");

        if (count < 1) {
            recycler_view.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
        } else {
            recycler_view.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);
        }

        listAdapter = new CollListAdapter(
                        this.realm.where(CollJob.class)
                                .sort("collName")
//                        .sort("rangeFCOMeters")
                                .findAll()
                );

        recycler_view.setAdapter(listAdapter);
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

        etTglLKP.setTypeface(fontGoogle);

        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        UserData userData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        etCabang.setText(userData.getBranchName());
        etTglLKP.setText(Utility.convertDateToString(new Date(), Utility.DATE_DISPLAY_PATTERN));

        etTglLKP.setOnClickListener(view1 -> {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), listenerDateTglLKP, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
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
            recycler_view.setVisibility(View.INVISIBLE);

            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);

            ((OnCollectorListListener) getContext()).getCollectorsByDate(dateLKP);
        }

    }

    public class CollListAdapter extends RealmRecyclerViewAdapter<CollJob, RecyclerView.ViewHolder> implements Filterable {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private int lastPosition = -1;

        CollListAdapter(RealmResults<CollJob> realmResults) {
            super(realmResults, true);

//            setSortKey("rangeFCOMeters");
//            setSortOrder(Sort.ASCENDING);
        }

        @Override
        public int getItemViewType(int position) {
            final CollJob detail = getData().get(position);

            String collCode = detail.getCollCode();
            if (collCode.equalsIgnoreCase("z"))
                return TYPE_HEADER;
            else
//            return super.getItemViewType(position);
                return TYPE_ITEM;
        }

        public void filterResults(String text) {
            text = text == null ? null : text.toLowerCase().trim();
            RealmQuery<CollJob> query = realm.where(CollJob.class);
            if (!(text == null || "".equals(text))) {
                query.contains("collName", text, Case.INSENSITIVE); // TODO: change field
            }
            updateData(query.findAllAsync());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_HEADER) {
//                View v = inflater.inflate(R.layout.row_coll_header, viewGroup, false);
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coll_header, viewGroup, false);
                return new VHHeader(v);

            } else if (viewType == TYPE_ITEM) {
//                View v = inflater.inflate(R.layout.row_coll_list, viewGroup, false);
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coll_list, viewGroup, false);
                return new DataViewHolder((FrameLayout) v);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder rvHolder, int position) {

            final CollJob detail = getData().get(position);

            if (rvHolder instanceof VHHeader) {

                VHHeader holder = (VHHeader) rvHolder;
                holder.txtTitle.setText("Unassigned (" + detail.getCountLKP() + ")");
            } else if (rvHolder instanceof DataViewHolder) {
                DataViewHolder holder = (DataViewHolder) rvHolder;

                holder.llRowLKP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof OnCollectorListListener) {
                            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                            ((OnCollectorListListener) getContext()).onShowCollector(detail, dateLKP);
                        }
                    }
                });

                holder.btnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof OnCollectorListListener) {
                            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? new Date() : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
                            ((OnCollectorListListener) getContext()).onShowGPSLocation(detail, dateLKP);
                        }
                    }
                });

                boolean isSameDay = Utility.isSameDay(detail.getLkpDate(), new Date());

                if (isSameDay && detail.getCountLKP() > 0) {
                    holder.llHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRadanaBlue));
                    holder.tvTotVisit.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRadanaBlue));
                } else {
                    holder.llHeader.setBackgroundColor(Color.parseColor("#808080"));
                    holder.tvTotVisit.setBackgroundColor(Color.parseColor("#808080"));
                }

                String collName = TextUtils.isEmpty(detail.getCollName()) ? detail.getCollCode() : detail.getCollName();
                TextView tvCollName = holder.tvCollName;

                if (Build.VERSION.SDK_INT >= 24) {
                    tvCollName.setText(Html.fromHtml("<b>" + collName + "</b>", Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvCollName.setText(Html.fromHtml("<b>" + collName + "</b>"));
                }

                TextView tvCollCode = holder.tvCollCode;
                tvCollCode.setText(detail.getCollCode());

                TextView tvLastTask = holder.tvLastTask;
                if (detail.getLastTask() == null) {
                    tvLastTask.setText(null);
                } else
                    tvLastTask.setText(detail.getLastTask() + " - " + Utility.convertDateToString(detail.getLastTaskTime(), "HH:mm:ss"));

                TextView tvLastLat = holder.tvLastLat;
                tvLastLat.setText(detail.getLastLatitude());

                TextView tvLastLng = holder.tvLastLng;
                tvLastLng.setText(detail.getLastLongitude());

                holder.btnMap.setVisibility(View.VISIBLE);
                holder.pulsator.stop();

                if (TextUtils.isEmpty(detail.getLastLatitude())
                        || TextUtils.isEmpty(detail.getLastLongitude())
                ) {
                    holder.btnMap.setVisibility(View.GONE);
                } else {
                    // kalo hari yg sama saja
                    if (isSameDay)
                        holder.pulsator.start();
                }

                TextView tvTotVisit = holder.tvTotVisit;
                String totVisit = "" + detail.getCountVisited() + "/" + detail.getCountLKP();
                if (Build.VERSION.SDK_INT >= 24) {
                    tvTotVisit.setText(Html.fromHtml(totVisit, Html.FROM_HTML_MODE_LEGACY));
//                tvTotVisit.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvTotVisit.setText(Html.fromHtml(totVisit));
//                tvTotVisit.setText(Html.fromHtml("<strong>Total Visited : " + detail.getCountVisited() + "</strong> / " + detail.getCountLKP()));
                }

                holder.tvAbsen.setText(detail.getAbsenDate() == null ? "Belum Absen" : "");

                Animation animation = AnimationUtils.loadAnimation(getContext(),
                        (position > lastPosition) ? R.anim.up_from_bottom
                                : R.anim.down_from_top);
                rvHolder.itemView.startAnimation(animation);
            }

            lastPosition = position;

        }

        @Override
        public Filter getFilter() {
            CollJobFilter filter = new CollJobFilter(this);
            return filter;
        }

        private class CollJobFilter
                extends Filter {
            private final CollListAdapter adapter;

            private CollJobFilter(CollListAdapter adapter) {
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

            @BindView(R.id.tvAbsen)
            TextView tvAbsen;

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

                tvCollCode.setTypeface(fontGoogle);
                tvLastTask.setTypeface(fontGoogle);
                tvAbsen.setTypeface(fontGoogle);
                tvCollName.setTypeface(fontGoogle);
            }
        }


        class VHHeader extends RecyclerView.ViewHolder {
            TextView txtTitle;

            public VHHeader(View itemView) {
                super(itemView);
                this.txtTitle = (TextView) itemView.findViewById(R.id.txtHeader);
            }
        }
    }

    public interface OnCollectorListListener {
        void onShowCollector(CollJob detail, Date lkpDate);

        void getCollectorsByDate(Date lkpDate);

        void onShowGPSLocation(CollJob detail, Date lkpDate);
    }

}
