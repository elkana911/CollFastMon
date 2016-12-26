package id.co.ppu.collfastmon.component;

import android.support.v4.app.Fragment;

import io.realm.Realm;

/**
 * Created by Eric on 27-Oct-16.
 */

public class BasicFragment extends Fragment {

    protected Realm realm;

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
