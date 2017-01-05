package id.co.ppu.collfastmon.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.RealmSearchView;
import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChatAllContacts extends DialogFragment {

    public static final String PARAM_USERCODE = "user.code";
    private String userCode;

    private Realm realm;
    private ContactListAdapter mAdapter;

    private OnContactSelectedListener mListener;

    @BindView(R.id.contacts)
    RealmSearchView contacts;

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

        contacts.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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
                    + " must implement OnContactSelectedListener");
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
            mAdapter = new ContactListAdapter(
                    getContext(),
                    this.realm,
                    "nickName"
            );
        }
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        recycler_view.setLayoutManager(mLayoutManager);
        contacts.setAdapter(mAdapter);

    }

    public class ContactListAdapter extends RealmSearchAdapter<TrnChatContact, ContactListAdapter.DataViewHolder> {

        public ContactListAdapter(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey) {
            super(context, realm, filterKey);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_chat_contact_list, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {
            final TrnChatContact detail = realmResults.get(position);

            if (!detail.isValid()) {
                return;
            }

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

            TextView tvContactShortName = dataViewHolder.tvContactShortName;
            tvContactShortName.setText(Utility.getFirstTwoChars(detail.getNickName()));

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
            }

        }

        public class DataViewHolder extends RealmSearchViewHolder {

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
