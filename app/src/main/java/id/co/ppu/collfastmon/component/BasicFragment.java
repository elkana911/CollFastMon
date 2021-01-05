package id.co.ppu.collfastmon.component;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;

/**
 * Created by Eric on 27-Oct-16.
 */

public class BasicFragment extends Fragment {

    protected Realm realm;
    protected Typeface fontArizon;  //dont have number
    protected Typeface fontGoogle;
    protected Typeface fontSamsungBold;
    protected Typeface fontSamsung;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontArizon = Typeface.createFromAsset(getContext().getAssets(), Utility.FONT_ARIZON);
        fontGoogle = Typeface.createFromAsset(getContext().getAssets(), Utility.FONT_GOOGLE);
        fontSamsung = Typeface.createFromAsset(getContext().getAssets(), Utility.FONT_SAMSUNG);
        fontSamsungBold = Typeface.createFromAsset(getContext().getAssets(), Utility.FONT_SAMSUNG_BOLD);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkRealmInstance();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    protected void checkRealmInstance() {
        if (this.realm == null)
            this.realm = Realm.getDefaultInstance();
    }

}
