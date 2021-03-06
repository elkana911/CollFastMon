package id.co.ppu.collfastmon.screen.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.listener.OnGetChatContactListener;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class FragmentChatActiveContacts extends Fragment {

    public static final String PARAM_USERCODE = "user.code";
    private String userCode;

    private Realm realm;

    @BindView(R.id.etChatCollCode)
    EditText etChatCollCode;

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.tvSeparator)
    TextView tvSeparator;

    private OnChatContactsListener caller;

    private ContactListAdapter listAdapter;
    private boolean isOnline = false;

    public FragmentChatActiveContacts() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userCode = getArguments().getString(PARAM_USERCODE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_active, container, false);
        ButterKnife.bind(this, view);

        etChatCollCode.setText(userCode);
//        contacts.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    public void getGroupContacts() {
        if (caller == null)
            return;

        caller.onGetGroupContacts(new OnGetChatContactListener() {

            @Override
            public void onSuccess(final List<TrnChatContact> list) {

                if (list != null && realm != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(TrnChatContact.class);

                            realm.copyToRealmOrUpdate(list);
                        }
                    });

                    tvSeparator.setText(list.size() + " CONTACTS");
                }
//                else
//                    tvSeparator.setText("NO CONTACTS");

            }

            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSkip() {

            }
        });

    }

    private void showSnackbar(String msg) {
        try {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    msg, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnIsOffline)
    public void checkStatusOffline() {
        if (caller == null)
            return;

        if (true)
            return;

        caller.isOffline(etChatCollCode.getText().toString(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
//                if (listAdapter != null)
//                    listAdapter.notifyDataSetChanged();
//                    getGroupContacts();
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable == null) {
                    return;
                }

                showSnackbar(throwable.getMessage());
            }

            @Override
            public void onSkip() {

            }
        });
    }

    @OnClick(R.id.btnChatLogoff)
    public void sendStatusOffline() {
        if (caller == null)
            return;

        caller.onLogoff(etChatCollCode.getText().toString(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {
//                listAdapter.notifyDataSetChanged();

                isOnline = false;
            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSkip() {

            }
        });
    }

    @OnClick(R.id.btnChatLogon)
    public void sendStatusOnline() {
        if (caller == null)
            return;

        // harus bilang online dulu baru loading contacts
        caller.onLogon(etChatCollCode.getText().toString(), new OnSuccessError() {
            @Override
            public void onSuccess(String msg) {

                isOnline = true;

                getGroupContacts();
            }

            @Override
            public void onFailure(Throwable t) {

                if (t == null)
                    return;

                showSnackbar(t.getMessage());
            }

            @Override
            public void onSkip() {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        this.realm = Realm.getDefaultInstance();

        this.realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                if (listAdapter != null)
                    listAdapter.notifyDataSetChanged();
            }
        });

        if (listAdapter == null) {
            listAdapter = new ContactListAdapter(
                    this.realm.where(TrnChatContact.class)
                            .sort("nickName").findAll()
            );
        }

        tvSeparator.setText("" + listAdapter.getItemCount() + " CONTACTS");

        recycler_view.setAdapter(listAdapter);

        checkStatusOffline();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;

            listAdapter = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnChatContactsListener) {
            caller = (OnChatContactsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        caller = null;
    }


    public class ContactListAdapter extends RealmRecyclerViewAdapter<TrnChatContact, RecyclerView.ViewHolder> implements Filterable {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public ContactListAdapter(RealmResults<TrnChatContact> realmResults) {
            super(realmResults, true);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_basic_header, viewGroup, false);
                return new VHHeader(v);

            } else if (viewType == TYPE_ITEM) {
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

            DataViewHolder dataViewHolder = (DataViewHolder) rvHolder;

            dataViewHolder.llRowContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnChatContactsListener) {
                        ((OnChatContactsListener) getContext()).onContactSelected(detail);
                    }

                }
            });

            dataViewHolder.llRowContact.setBackgroundColor(Color.WHITE);    // must

            StringBuffer sbTitle = new StringBuffer("<strong>");
            sbTitle.append(detail.getNickName());
            sbTitle.append("</strong>");

            if (detail.getCollCode().equals(etChatCollCode.getText().toString())) {
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

            TextView tvContactShortName = dataViewHolder.tvContactShortName;
            tvContactShortName.setText(Utility.getFirstTwoChars(detail.getNickName()).toUpperCase());
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), Utility.FONT_SAMSUNG);
            tvContactShortName.setTypeface(font);

            // http://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
            Drawable background = tvContactShortName.getBackground();
            if (background instanceof GradientDrawable) {
                // cast to 'ShapeDrawable'
                GradientDrawable shapeDrawable = (GradientDrawable) background;

                position %= 15;

                int resColor = R.color.chatContact1;

                if (position == 1)
                    resColor = R.color.chatContact2;
                else if (position == 2)
                    resColor = R.color.chatContact3;
                else if (position == 3)
                    resColor = R.color.chatContact4;
                else if (position == 4)
                    resColor = R.color.chatContact5;
                else if (position == 5)
                    resColor = R.color.chatContact6;
                else if (position == 6)
                    resColor = R.color.chatContact7;
                else if (position == 7)
                    resColor = R.color.chatContact8;
                else if (position == 8)
                    resColor = R.color.chatContact9;
                else if (position == 9)
                    resColor = R.color.chatContact10;
                else if (position == 10)
                    resColor = R.color.chatContact11;
                else if (position == 11)
                    resColor = R.color.chatContact12;
                else if (position == 12)
                    resColor = R.color.chatContact13;
                else if (position == 13)
                    resColor = R.color.chatContact14;
                else if (position == 14)
                    resColor = R.color.chatContact15;

                shapeDrawable.setColor(ContextCompat.getColor(getContext(), resColor));

                contactName.setTextColor(ContextCompat.getColor(getContext(), resColor));
            }

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
        public Filter getFilter() {
            MyFilter filter = new MyFilter(this);
            return filter;
        }

        public void filterResults(String text) {
            text = text == null ? null : text.toLowerCase().trim();
            RealmQuery<TrnChatContact> query = realm.where(TrnChatContact.class);
            if (!(text == null || "".equals(text))) {
                query.contains("nickName", text, Case.INSENSITIVE); // TODO: change field
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

        class VHHeader extends RecyclerView.ViewHolder {
            TextView txtTitle;

            public VHHeader(View itemView) {
                super(itemView);
                this.txtTitle = (TextView) itemView.findViewById(R.id.txtHeader);
            }
        }
    }

    public interface OnChatContactsListener {
        void onGetGroupContacts(OnGetChatContactListener listener);
        void onGetOnlineContacts(OnGetChatContactListener listener);

        void onContactSelected(TrnChatContact contact);

        void onContactClearChats(TrnChatContact contact);

        void onLogon(String collCode, OnSuccessError listener);
        void onLogoff(String collCode, OnSuccessError listener);

        void isOffline(String collCode, OnSuccessError listener);
    }

}
