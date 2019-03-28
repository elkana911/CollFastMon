package id.co.ppu.collfastmon.screen.chat;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChatAllContacts extends DialogFragment {

    public static final String PARAM_USERCODE = "user.code";
    private String userCode;

    private Realm realm;
    private ContactListAdapter mAdapter;

    private OnContactSelectedListener mListener;

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    public FragmentChatAllContacts() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Pick Contract");
        return dialog;

    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
        loadList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_all_contacts, container, false);

        ButterKnife.bind(this, view);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null)
                    mAdapter.filterResults(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//        contacts.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactSelectedListener) {
            mListener = (OnContactSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnContactSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadList() {

        long count = this.realm.where(TrnChatContact.class).count();

        getDialog().setTitle("Pick a Contact (" + count + ")");

        if (mAdapter == null) {
//            mAdapter = new ContactListAdapter(
//                    getContext(),
//                    this.realm,
//                    "nickName"
//            );
            mAdapter = new ContactListAdapter(
                    this.realm.where(TrnChatContact.class)
                            .sort("nickName").findAll()
            );
        }
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(mAdapter);

    }

    public class ContactListAdapter extends RealmRecyclerViewAdapter<TrnChatContact, RecyclerView.ViewHolder> implements Filterable {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public ContactListAdapter(RealmResults<TrnChatContact> realmResults) {
            super(realmResults, true);
        }

        @Override
        public int getItemViewType(int position) {
            final TrnChatContact detail = getData().get(position);

            // sementara ga ada pembedanya
            /*
            if (detail.getMessageType() == null || detail.getMessageType().equals("0"))
                return TYPE_ITEM;
            else if (detail.getMessageType().equals("1"))
                return TYPE_HEADER;
                */

            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            /*if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_basic_header, viewGroup, false);
                return new VHHeader(v);

            } else */if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_chat_contact_list, viewGroup, false);
                return new DataViewHolder((FrameLayout) v);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder rvHolder, int position) {
            final TrnChatContact detail = getData().get(position);
            if (!detail.isValid()) {
                return;
            }

//            if (rvHolder instanceof VHHeader) {
//                VHHeader holder = (VHHeader) rvHolder;
//                holder.txtTitle.setText(detail.getRoom());
//
//                return;
//            }

            DataViewHolder dataViewHolder = (DataViewHolder) rvHolder;

            dataViewHolder.llRowContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnContactSelectedListener) {
                        ((OnContactSelectedListener) getContext()).onContactSelected(detail);

                        getDialog().dismiss();
                    }

                }
            });

            dataViewHolder.llRowContact.setBackgroundColor(Color.WHITE);    // must

            StringBuffer sbTitle = new StringBuffer("<strong>");
            sbTitle.append(detail.getNickName());
            sbTitle.append("</strong>");

            if (userCode != null && detail.getCollCode().equals(userCode)) {
                sbTitle.append(" (You)");
            }

            TextView contactName = dataViewHolder.tvContactName;
            if (Build.VERSION.SDK_INT >= 24) {
                contactName.setText(Html.fromHtml(sbTitle.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                contactName.setText(Html.fromHtml(sbTitle.toString()));
            }

            TextView tvContactCode = dataViewHolder.tvContactCode;
            tvContactCode.setText(detail.getCollCode());

            TextView tvStatus = dataViewHolder.tvStatus;
            tvStatus.setText(detail.getStatusMsg());
        }

        @Override
        public Filter getFilter() {
            MyFilter filter = new MyFilter(this);
            return filter;
        }

        public void filterResults(String text) {
            text = text == null ? null : text.toLowerCase().trim();
            RealmQuery<TrnChatContact> query = realm.where(TrnChatContact.class);
            if (!(text == null || "".equals(text))) {
                query.contains("custName", text, Case.INSENSITIVE); // TODO: change field
            }
            updateData(query.findAllAsync());
        }


        private class MyFilter
                extends Filter {
            private final ContactListAdapter adapter;

            private MyFilter(ContactListAdapter adapter) {
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

            @BindView(R.id.llRowContact)
            LinearLayout llRowContact;

            @BindView(R.id.tvContactName)
            TextView tvContactName;

            @BindView(R.id.tvContactCode)
            TextView tvContactCode;

            @BindView(R.id.tvStatus)
            TextView tvStatus;

            @BindView(R.id.tvContactShortName)
            TextView tvContactShortName;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);
            }

        }
    }

    public interface OnContactSelectedListener {
        void onContactSelected(TrnChatContact contact);
    }


}
