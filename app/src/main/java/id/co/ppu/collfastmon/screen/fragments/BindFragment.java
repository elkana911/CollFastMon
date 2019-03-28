package id.co.ppu.collfastmon.screen.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import id.co.ppu.collfastmon.component.BasicFragment;

public abstract class BindFragment extends BasicFragment {

    protected abstract int getLayoutResourceView();

    protected void setupView(View view) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceView(), container, false);
        ButterKnife.bind(this, view);

        setupView(view);

        return view;
    }
}
