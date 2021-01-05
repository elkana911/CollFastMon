package id.co.ppu.collfastmon.screen.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collfastmon.BuildConfig;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.exceptions.ExpiredException;
import id.co.ppu.collfastmon.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collfastmon.listener.OnSuccessError;
import id.co.ppu.collfastmon.pojo.ServerInfo;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.screen.MainActivity;
import id.co.ppu.collfastmon.util.DemoUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.RootUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.UserUtil;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BasicActivity {

    private static final String TAG = "login";
    // UI references.
    @BindView(R.id.tilUsername)
    View tilUsername;

    @BindView(R.id.tilPassword)
    View tilPassword;

    @BindView(R.id.llServerDev)
    LinearLayout llServerDev;

    @BindView(R.id.username)
    AutoCompleteTextView mUserNameView;

    @BindView(R.id.cbRememberPwd)
    CheckBox cbRememberPwd;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.spServers)
    Spinner spServers;

    @BindView(R.id.imageLogo)
    ImageView imageLogo;

    @BindView(R.id.btnGetLKPUser)
    Button btnGetLKPUser;

    @BindView(R.id.etServerDevIP)
    EditText etServerDevIP;

    @BindView(R.id.etServerDevPort)
    EditText etServerDevPort;

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    private String getSelectedServer() {
        return spServers.getSelectedItem().toString();
//        return Utility.getServerName(spServers.getSelectedItemPosition());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String loginDate = Storage.getPref(Storage.KEY_LOGIN_DATE, null);

        if (!TextUtils.isEmpty(loginDate)) {
            UserData prevUserData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

            if (prevUserData != null)
                loginOffline(prevUserData.getUserId(), prevUserData.getUserPwd());
        }

//        Animation animZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
//        imageLogo.startAnimation(animZoomIn);

        tilUsername.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_light);

        ButterKnife.bind(this);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        tvVersion.setText("v" + versionName + (Utility.DEVELOPER_MODE ? "-dev" : ""));

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            imageLogo.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
//        getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
            centerActionBarTitle();
        }


        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = false;// getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
//                    Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
//                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
//                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });
        // Start the thread
        t.start();

        UserData prevUserData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

//        String lastUsername = Storage.getPreference(getApplicationContext(), Storage.KEY_USER_NAME_LAST);
        if (prevUserData != null)
            mUserNameView.setText(prevUserData.getUserId());

        String password_rem = Storage.getPref(Storage.KEY_PASSWORD_REMEMBER, null);
        if (!TextUtils.isEmpty(password_rem)) {
            if (password_rem.equalsIgnoreCase("true")) {
                cbRememberPwd.setChecked(true);

                // load user & password
                String lastPwd = Storage.getPref(Storage.KEY_PASSWORD_LAST, null);

                String username = mUserNameView.getText().toString();
                if (prevUserData != null && username.equalsIgnoreCase(prevUserData.getUserId())) {
                    mPasswordView.setText(lastPwd);
                } else
                    mPasswordView.setText(null);
            }
        }


        String ipDev = Storage.getPref(Storage.KEY_SERVER_DEV_IP, null);
        String portDev = Storage.getPref(Storage.KEY_SERVER_DEV_PORT, null);

        if (TextUtils.isEmpty(ipDev))
            ipDev = Utility.SERVER_DEV_IP; // Utility.SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][1];

        if (TextUtils.isEmpty(portDev))
            portDev = Utility.SERVER_DEV_PORT; // SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][2];

        etServerDevIP.setText(ipDev);
        etServerDevPort.setText(portDev);

        refreshServerList();

        String lastServer = Storage.getSelectedServerName();
        Utility.setSpinnerAsString(spServers, lastServer);

//        int x = Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0);
//        Utility.setSpinnerAsString(spServers, Utility.getServerName(x));

        if (Utility.DEVELOPER_MODE) {
            btnGetLKPUser.setVisibility(View.VISIBLE);

        }

    }

    private void refreshServerList(){
        List<String> servers = new ArrayList<>();
        for (int i = 0; i < Utility.SERVERS.length; i++) {

            if (!Utility.DEVELOPER_MODE) {
                if (Utility.SERVERS[i][0].startsWith("local")
                        || Utility.SERVERS[i][0].startsWith("dev-")
                )
                    continue;
                else
                    servers.add(Utility.SERVERS[i][0]);
            } else {
                if (Utility.SERVERS[i][0].startsWith("local")
                        || Utility.SERVERS[i][0].startsWith("dev-")
                )
                    servers.add(Utility.SERVERS[i][0]);
                else
                    continue;
            }

//            servers.add(Utility.SERVERS[i][0]);
        }

        ServerAdapter arrayAdapter = new ServerAdapter(this, android.R.layout.simple_spinner_item, servers);
        spServers.setAdapter(arrayAdapter);
        spServers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String itemAtPosition = (String) adapterView.getItemAtPosition(i);

                if (itemAtPosition.startsWith("dev")) {
                    llServerDev.setVisibility(View.VISIBLE);
                } else
                    llServerDev.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {

        if (RootUtil.isDeviceRooted()) {
            Utility.showDialog(this, "Rooted", "This device is rooted. Unable to open application.");
            return;
        }

        try {
            /*
            if (!NetUtil.isConnected(this)) {
                Utility.showDialog(this, getString(R.string.title_no_connection), getString(R.string.error_online_required));
                return;
            }
            */

            String selectedServer = getSelectedServer(); // Utility.getServerName(spServers.getSelectedItemPosition());
            if (selectedServer.startsWith("dev")) {
                int id = Utility.getServerID(selectedServer);
                Utility.SERVERS[id][1] = etServerDevIP.getText().toString();
                Utility.SERVERS[id][2] = etServerDevPort.getText().toString();
            }

            final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Checking version...");

            checkVersion(new OnSuccessError() {
                @Override
                public void onSuccess(String msg) {

                    Utility.dismissDialog(mProgressDialog);

                    if (!TextUtils.isEmpty(msg)) {
//                        Utility.showDialog(LoginActivity.this, "Message", msg);
                    }

                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.dismissDialog(mProgressDialog);

                    Utility.throwableHandler(LoginActivity.this, throwable, true);
                }

                @Override
                public void onSkip() {
                    Utility.dismissDialog(mProgressDialog);

                    try {
                        attemptLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startActivity(new Intent(this, MainActivity.class));

    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpClick() {
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void resetData() {
        if (realm == null)
            return;

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

    }

    private void attemptLogin() throws Exception {

        /*
        Utility.SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][1];
                ipDev = Utility.SERVER_DEV_IP; // Utility.SERVERS[Utility.getServerID(Utility.SERVER_DEV_NAME)][1];
        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));
        */

        /*
        disabled due to complaints
        Date sysDate = new Date();
        if (sysDate.after(Utility.convertStringToDate(Utility.DATE_EXPIRED_YYYYMMDD, "yyyyMMdd"))) {
            Utility.showDialog(this, "Expired App", "This application version is expired. Please update from the latest");
//            return;
        }
        */

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        UserData prevUserData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        if (prevUserData == null) {
            loginOnline(username, password);
        } else {
            if (!username.equalsIgnoreCase(prevUserData.getUserId())) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Reset Data");
                alertDialogBuilder.setMessage("You are using different account, previous data will be reset.\nDo you want to login as " + username + " ?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (realm != null) {

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.deleteAll();
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    loginOnline(username, password);

                                }
                            });

                        }

                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();

            } else {
                Date lastMorning = (Date) Storage.getPrefAsJson(Storage.KEY_USER_LAST_DAY, Date.class, null);
                if (lastMorning == null) {
                    loginOffline(username, password);
                } else if (Utility.isSameDay(lastMorning, new Date())) {
                    loginOffline(username, password);
                } else
                    loginOnline(username, password);

            }
        }
    }

    private boolean isUserIsDemo() {
        // Store values at the time of the login attempt.
        final String username = mUserNameView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();

        return UserUtil.userIsDemo(username, password);
    }

    /*
    ga wajib online
     */
    private void checkVersion(final OnSuccessError listener) {
        if (!NetUtil.isConnected(this) || isUserIsDemo()) {
//            Toast.makeText(this, getString(R.string.error_online_required), Toast.LENGTH_LONG).show();

            if (listener != null) {
                listener.onSkip();
            }

            return;
        }

        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;

        APIonBuilder.getAppVersion(this, getSelectedServer(), (e, result) -> {
            if (e != null) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage(), e);

                if (listener != null)
                    listener.onFailure(new RuntimeException("Get Version failed"));
                return;
            }

            if (result.getAsString().equals(versionName)) {
                if (listener != null) {
                    listener.onSuccess(result.getAsString());
                }
            } else {

                if (!Utility.DEVELOPER_MODE) {
                    if (listener != null) {
                        listener.onFailure(new ExpiredException("Please update app to latest version"));
                    }

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result.getAsString())));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    if (listener != null) {
                        listener.onSuccess("Version skipped because developer mode");
                    }

                }
            }
        });
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3; // need to allow demo as password
    }

    private void retrieveServerInfo(final OnPostRetrieveServerInfo listener) {

        APIonBuilder.getServerInfo(this, getSelectedServer(), (e, result) -> {
            Utility.disableScreen(LoginActivity.this, false);

            if (e != null) {

                if (listener != null) {
                    listener.onFailure(e);
                }

                Utility.showDialog(LoginActivity.this, "Error", e.getMessage());

                return;
            }

            LoginActivity.this.realm.executeTransaction(realm -> {
                realm.delete(ServerInfo.class);
                realm.copyToRealm(result.getData());

                if (listener != null) {
                    listener.onSuccess(result.getData());
                }
            });
        });
    }

    private void loginOnline(final String username, final String password) {

        if (UserUtil.userIsDemo(username, password)) {
            final ServerInfo si = new ServerInfo();
            si.setServerDate(new Date());

            LoginActivity.this.realm.executeTransaction(realm -> {
                realm.delete(ServerInfo.class);
                realm.copyToRealm(si);
            });

            Storage.savePrefAsJson(Storage.KEY_USER, DemoUtil.buildDemoUser());

            // able to control nextday shpuld re-login to server
            Storage.savePrefAsJson(Storage.KEY_USER_LAST_DAY, new Date());

            // final check
            startMainActivity();
            return;
        }

        if (!NetUtil.isConnectedUnlessToast(this)) {
            return;
        }

        Utility.disableScreen(this, true);

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Logging in... ");

        try {
            retrieveServerInfo(new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {

                    APIonBuilder.login(LoginActivity.this, getSelectedServer(), username, password, (e, result) -> {
                        Utility.dismissDialog(mProgressDialog);

                        if (e != null) {

                            Utility.disableScreen(LoginActivity.this, false);

                            Log.e("fast", e.getMessage(), e);

                            Utility.showDialog(LoginActivity.this, "Problem", e.getMessage());

                            return;
                        }

                        if (result.getError() != null) {

                            Utility.showDialog(LoginActivity.this, "Error (" + result.getError().getErrorCode() + ")", result.getError().getErrorDesc());

                        } else {

                            Storage.savePrefAsJson(Storage.KEY_USER, result.getData());
                            // able to control nextday shpuld re-login to server
                            Storage.savePrefAsJson(Storage.KEY_USER_LAST_DAY, new Date());

                            // final check
                            startMainActivity();
                        }
                    });

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.disableScreen(LoginActivity.this, false);
                    Utility.dismissDialog(mProgressDialog);

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.dismissDialog(mProgressDialog);
        }

    }

    private void startMainActivity() {
        String ipDev = etServerDevIP.getText().toString();
        String portDev = etServerDevPort.getText().toString();

        Storage.savePref(Storage.KEY_SERVER_DEV_IP, ipDev);
        Storage.savePref(Storage.KEY_SERVER_DEV_PORT, portDev);

//        Storage.savePref(Storage.KEY_SERVER_ID, String.valueOf(Utility.getServerID(spServers.getSelectedItem().toString())));
//        Storage.savePreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, Utility.getServerID(spServers.getSelectedItem().toString()));

        String selectedServer = spServers.getSelectedItem().toString();
        Storage.savePref(Storage.KEY_SERVER_NAME_ID, selectedServer);

        if (cbRememberPwd.isChecked()) {
            final String password = mPasswordView.getText().toString();

            String cbRememberPwds = String.valueOf(cbRememberPwd.isChecked());

            Storage.savePref(Storage.KEY_PASSWORD_REMEMBER, cbRememberPwds);
            Storage.savePref(Storage.KEY_PASSWORD_LAST, password);

        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        overridePendingTransition(R.anim.activity_slide_right, R.anim.activity_slide_left);
    }

    private void loginOffline(String username, String password) {

        String logoutDate = Storage.getPref(Storage.KEY_LOGOUT_DATE, null);

        if (TextUtils.isEmpty(logoutDate)) {
            resetData();

        }

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Logging in...");

        UserData prevUserData = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        String pwd = prevUserData.getUserPwd() == null ? "" : prevUserData.getUserPwd();

        if (prevUserData == null || !pwd.equals(password)) {
            Utility.showDialog(this, "Invalid Login", getString(R.string.error_invalid_login));
        } else {

            startMainActivity();

        }

        Utility.dismissDialog(mProgressDialog);
    }

    @OnClick(R.id.btnGetLKPUser)
    public void onClickGetLKPUser() {

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Get Any LKP User...");

        APIonBuilder.getAnyUser(this, getSelectedServer(), (e, result) -> {
            Utility.dismissDialog(mProgressDialog);

            if (e != null) {
                return;
            }

            if (result.getError() == null) {
                if (result.getData() != null) {
                    mUserNameView.setText(result.getData()[0]);
                    mPasswordView.setText(result.getData()[1]);
                }
            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class ServerAdapter extends ArrayAdapter<String> {
        private Context ctx;
        private List<String> list;


        public ServerAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setPadding(10, 20, 10, 20);
            label.setText(list.get(position));
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}

