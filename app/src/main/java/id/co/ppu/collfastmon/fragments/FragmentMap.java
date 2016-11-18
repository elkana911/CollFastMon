package id.co.ppu.collfastmon.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMap extends Fragment {

    public FragmentMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

}
