package id.co.ppu.collfastmon.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.co.ppu.collfastmon.R;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChatBroadcast extends Fragment {

    private Realm realm;

    public FragmentChatBroadcast() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_broadcast, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;

//            listAdapter = null;
        }
    }

}
