package id.co.ppu.collfastmon;

import android.app.Application;
import android.util.Log;

import com.koushikdutta.ion.Ion;

import id.co.ppu.collfastmon.util.DataUtil;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Eric on 26-Oct-16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Ion.getDefault(this).configure().setGson(DataUtil.buildCustomDataFactory());

        initRealm();
    }

    private void initRealm(){
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("CollFastMon", "onTerminate");
    }
}
