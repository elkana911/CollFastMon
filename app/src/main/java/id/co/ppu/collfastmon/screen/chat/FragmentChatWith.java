package id.co.ppu.collfastmon.screen.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;
import id.co.ppu.collfastmon.util.ChatUtil;
import id.co.ppu.collfastmon.util.ConstChat;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentChatWith extends Fragment {

    public static final String PARAM_USERCODE1 = "collector.userCode1";
    public static final String PARAM_USERCODE2 = "collector.userCode2";

    private Realm realm;

    @BindView(R.id.etMsg)
    public EditText etMsg;

    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    private OnChatWithListener caller;

    public ChatListAdapter listAdapter;

    public String userCode1;
    public String userCode2;

    public FragmentChatWith() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userCode1 = getArguments().getString(PARAM_USERCODE1);
            userCode2 = getArguments().getString(PARAM_USERCODE2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_chat_with, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnChatWithListener) {
            caller = (OnChatWithListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnChatWithListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        caller = null;
    }


    @Override
    public void onStart() {
        super.onStart();

        this.realm = Realm.getDefaultInstance();

        // hapus soalnya mau getchathistory
        this.realm.beginTransaction();
        this.realm.where(TrnChatMsg.class)
                .equalTo("messageType", ConstChat.MESSAGE_TYPE_TIMESTAMP)
                .findAll().deleteAllFromRealm();
        this.realm.commitTransaction();

        if (caller != null) {
            caller.onGetChatHistory(this.userCode1, this.userCode2);
        }

        if (listAdapter == null) {

            RealmResults<TrnChatMsg> realmResults = DataUtil.queryChatMsg(realm, this.userCode1, this.userCode2)
                            .findAll().sort("createdTimestamp");

            // dibutuhkan listener ini spy kalo sudah terkirim ke server langsung update tampilan
            realmResults.addChangeListener(new RealmChangeListener<RealmResults<TrnChatMsg>>() {
                @Override
                public void onChange(RealmResults<TrnChatMsg> element) {
                    Log.e("FragmentChatWith", "change");

                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                }
            });

            listAdapter = new ChatListAdapter(
                    this.realm.where(TrnChatMsg.class)
                            .sort("createdTimestamp", Sort.ASCENDING).findAll()
            );
        }

        recycler_view.setAdapter(listAdapter);

        // ternyata masih 0 disini
//        int lastRow = listAdapter.getItemCount();
//        chats.scrollToPosition(lastRow);
//        chats.scrollToPosition(listAdapter.getItemCount());
        scrollToLast();

        etMsg.requestFocus();

        ChatUtil.chatSendQueueMessage(getActivity());
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

    public void afterAddMsg() {
        etMsg.setText(null);

//        listAdapter.notifyDataSetChanged();

        scrollToLast();
    }

    public void scrollToLast() {

        RealmResults<TrnChatMsg> realmResults = DataUtil.queryChatMsg(realm, this.userCode1, this.userCode2)
                        .findAll().sort("createdTimestamp");

        int rows = realmResults.size() - 1;
//        int rows2 = listAdapter.getItemCount();
//        int rows3 = listAdapter.getRealmResults().size();

        if (rows > 0)
            recycler_view.scrollToPosition(rows);

//        chats.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                chats.scrollToPosition(chats.getAdapter().getItemCount() - 1);
//            }
//        }, 1000);

    }

    public void clearChat() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Clear Chats");
        alertDialogBuilder.setMessage("Are you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (caller == null)
                    return;

                caller.onClearChatHistory(userCode1, userCode2);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();

    }

    public class ChatListAdapter extends RealmRecyclerViewAdapter<TrnChatMsg, RecyclerView.ViewHolder> implements Filterable {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public ChatListAdapter(RealmResults<TrnChatMsg> realmResults) {
            super(realmResults, true);
        }

        @Override
        public int getItemViewType(int position) {
            final TrnChatMsg detail = getData().get(position);

            if (detail.getMessageType() == null || detail.getMessageType().equals("0"))
                return TYPE_ITEM;
            else if (detail.getMessageType().equals("1"))
                return TYPE_HEADER;

            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_chat_header, viewGroup, false);
                return new VHHeader(v);

            } else if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_chat_list, viewGroup, false);
                return new DataViewHolder((FrameLayout) v);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder rvHolder, int position) {
            final TrnChatMsg detail = getData().get(position);

            if (!detail.isValid()) {
                return;
            }

            if (rvHolder instanceof VHHeader) {
                VHHeader holder = (VHHeader) rvHolder;
//                holder.txtTitle.setText(Utility.convertDateToString(detail.getCreatedTimestamp(), "EEE, d MMM yyyy"));
                holder.txtTitle.setText(detail.getMessage());

            } else if (rvHolder instanceof DataViewHolder) {
                DataViewHolder dataViewHolder = (DataViewHolder) rvHolder;

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dataViewHolder.llRowMsg.getLayoutParams();
                if (detail.getFromCollCode().equals(userCode1)) {
                    layoutParams.gravity = Gravity.END;
                    dataViewHolder.llRowMsg.setLayoutParams(layoutParams);

                    dataViewHolder.llRowMsg.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.bubble2));
                } else {
                    layoutParams.gravity = Gravity.START;
                    dataViewHolder.llRowMsg.setLayoutParams(layoutParams);

                    dataViewHolder.llRowMsg.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.bubble1));
                }

                TextView tvMsg = dataViewHolder.tvMsg;
                tvMsg.setText(detail.getMessage());

                TextView tvTime = dataViewHolder.tvTime;
                tvTime.setText(Utility.convertDateToString(detail.getCreatedTimestamp(), "HH:mm"));

                TextView tvStatus = dataViewHolder.tvStatus;
                tvStatus.setText(null);
//                tvStatus.setText(detail.getMessageStatus());
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                if (Utility.DEVELOPER_MODE && detail.getFromCollCode().equals(userCode1)) {

                    int idIcon;
                    if (detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME)
                            || detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_TRANSMITTING))
                        idIcon = R.mipmap.chat0;
                    else if (detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_SERVER_RECEIVED))
                        idIcon = R.mipmap.chat1;
                    else if (detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_DELIVERED))
                        idIcon = R.mipmap.chat2;
                    else if (detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_FAILED))
                        idIcon = R.mipmap.chatfailed;
                    else // if (detail.getMessageStatus().equals(ConstChat.MESSAGE_STATUS_READ_AND_OPENED))
                        idIcon = R.mipmap.chat3;

                    Drawable cekIcon = ContextCompat.getDrawable(getContext(), idIcon);
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, cekIcon, null);
                }

            }
        }

        @Override
        public Filter getFilter() {
            MyFilter filter = new MyFilter(this);
            return filter;
        }

        public void filterResults(String text) {
            text = text == null ? null : text.toLowerCase().trim();
            RealmQuery<TrnChatMsg> query = realm.where(TrnChatMsg.class);
            if (!(text == null || "".equals(text))) {
                query.contains("custName", text, Case.INSENSITIVE); // TODO: change field
            }
            updateData(query.findAllAsync());
        }


        private class MyFilter
                extends Filter {
            private final ChatListAdapter adapter;

            private MyFilter(ChatListAdapter adapter) {
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

            @BindView(R.id.llRowMsg)
            LinearLayout llRowMsg;

            @BindView(R.id.tvMsg)
            TextView tvMsg;

            @BindView(R.id.tvTime)
            TextView tvTime;

            @BindView(R.id.tvMessageStatus)
            TextView tvStatus;

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
    public interface OnChatWithListener {
        int onClearChatHistory(String collCode1, String collCode2);
        void onGetChatHistory(String collCode1, String collCode2);
    }

}
