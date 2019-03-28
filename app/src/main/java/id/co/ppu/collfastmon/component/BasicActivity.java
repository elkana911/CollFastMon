package id.co.ppu.collfastmon.component;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.pojo.ServerInfo;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import id.co.ppu.collfastmon.rest.request.RequestBasic;
import id.co.ppu.collfastmon.screen.settings.SettingsActivity;
import id.co.ppu.collfastmon.util.DataUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;

/**
 * Created by Eric on 06-Sep-16.
 */
public class BasicActivity extends AppCompatActivity {

    protected Realm realm;
    protected Typeface fontArizon;
    protected Typeface fontGoogle;
    protected Typeface fontSamsungBold;
    protected Typeface fontSamsung;

    protected void onRealmChangeListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = Realm.getDefaultInstance();

        this.realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                onRealmChangeListener();
            }
        });

        changeLocale(Storage.getLanguageId(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
        }


        fontArizon = Typeface.createFromAsset(getAssets(), Utility.FONT_ARIZON);
        fontGoogle = Typeface.createFromAsset(getAssets(), Utility.FONT_GOOGLE);
        fontSamsung = Typeface.createFromAsset(getAssets(), Utility.FONT_SAMSUNG);
        fontSamsungBold = Typeface.createFromAsset(getAssets(), Utility.FONT_SAMSUNG_BOLD);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }else  if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected Date getServerDate(Realm realm) {
        return realm.where(ServerInfo.class)
                .findFirst()
                .getServerDate();

    }

//    https://android--code.blogspot.co.id/2015/09/android-how-to-center-align-actionbar.html
    protected void centerActionBarTitle() {
        ActionBar ab = getSupportActionBar();
        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);

        // Set text to display in TextView
        tv.setText(ab.getTitle());
        tv.setTypeface(Typeface.SERIF, Typeface.BOLD);

        // Set the text color of TextView
//        tv.setTextColor(Color.BLACK);

        TextViewCompat.setTextAppearance(tv, android.R.style.TextAppearance_DeviceDefault_Widget_ActionBar_Title);

        // Set TextView text alignment to center
        tv.setGravity(Gravity.CENTER);

        // Set the ActionBar display option
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(tv);
    }

    protected RealmQuery<TrnLDVComments> getLDVComments(Realm realm, String ldvNo, String contractNo) {
        /*
        dont use global realm due to error: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
         */
        return realm.where(TrnLDVComments.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);
    }

    protected RealmQuery<TrnRepo> getRepo(Realm realm, String contractNo) {
        return realm.where(TrnRepo.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);

    }

    protected RealmQuery<TrnRVColl> getRVColl(Realm realm, String contractNo) {
        return realm.where(TrnRVColl.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);
    }

    protected UserData getCurrentUser() {
        UserData currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        return currentUser;
    }

//    protected String getCurrentUserId() {
//        UserData currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);
//        if (currentUser == null)
//            return null;
//
//        return currentUser.getUserId();
//    }

    protected void fillRequest(String actionName, RequestBasic req) {
        try {
            double[] gps = id.co.ppu.collfastmon.screen.location.Location.getGPS(this);
            String latitude = String.valueOf(gps[0]);
            String longitude = String.valueOf(gps[1]);
            req.setLatitude(latitude);
            req.setLongitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();

            req.setLatitude("0.0");
            req.setLongitude("0.0");
        }

        req.setActionName(actionName);
        req.setUserId(DataUtil.getCurrentUserId());
        req.setSysInfo(Utility.buildSysInfoAsCsv(this));


    }

    protected String getAndroidToken() {
        return Storage.getAndroidToken();
    }

    //http://www.sureshjoshi.com/mobile/changing-android-locale-programmatically/
    protected boolean changeLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }
}
