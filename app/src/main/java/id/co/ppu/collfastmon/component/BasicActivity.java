package id.co.ppu.collfastmon.component;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.pojo.ServerInfo;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.settings.SettingsActivity;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by Eric on 06-Sep-16.
 */
public class BasicActivity extends AppCompatActivity {

    protected Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = Realm.getDefaultInstance();

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

    protected ApiInterface getAPIService() {
        return
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));
    }

}
