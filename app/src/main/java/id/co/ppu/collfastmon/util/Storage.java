package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import id.co.ppu.collfastmon.pojo.LoginInfo;
import io.realm.Realm;

/**
 * Created by Eric on 16-Aug-16.
 */
public class Storage {
    public static final String PREF_APP = "RealmPref";

//    public static final String KEY_SERVER_ID = "serverID";
    // 5 nov 2019
    public static final String KEY_SERVER_NAME_ID = "serverNameID";
//    public static final String KEY_SERVER_DATE = "server.date";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_LAST_DAY = "user.lastMorning";
//    public static final String KEY_USER_NAME_LAST = "lastUsername";
    public static final String KEY_PASSWORD_REMEMBER = "password.remember";
    public static final String KEY_PASSWORD_LAST = "password.last";

    public static final String KEY_LOGIN_DATE = "login.date";
    public static final String KEY_LOGOUT_DATE = "logout.date";
    public static final String KEY_ANDROID_ID = "android.id";
    public static final String KEY_SERVER_DEV_IP = "server.dev.ip";
    public static final String KEY_SERVER_DEV_PORT = "server.dev.port";

    /**
     * @deprecated versi 1.3 always true
     */
    public static final String KEY_PREF_GENERAL_SHOWALL_COLL = "collectors.all";


/*    public static void savePreference(Context ctx, String key, String value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply(); //asynkron
    }

    public static void savePreferenceAsInt(Context ctx, String key, int value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply(); //asynkron
    }

    public static void savePreferenceAsBoolean(Context ctx, String key, boolean value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply(); //asynkron
    }

    public static void saveObjPreference(Context ctx, String key, Object value) {

        if (value == null) return;

        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();

        String json = new Gson().toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.commit();   //synkron
    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(PREF_APP, 0);
    }

    public static Object getObjPreference(Context ctx, String key, Class cls) {
        String val = null;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode

            Gson gson = new Gson();
            String json = userPrefs.getString(key, "");

            return new Gson().fromJson(json, cls);

        } catch (Exception e) {
            return null;
        }
    }

    public static String getPreference(Context ctx, String key) {
        String val = null;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
            val = userPrefs.getString(key, "");
        } catch (Exception e) {
            return null;
        }
        return val;
    }

    public static Integer getPrefAsInt(Context ctx, String key, int defaultValue) {
        int val;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
            val = userPrefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
        return val;
    }

    public static void clearObjOnSharedPref(Context ctx, String ObjPref) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(ObjPref, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
*/

    /**
     * H2U:
     * <br>savePref("StringKey", string);
     * <br>savePref("IntegerKey", String.valueOf(int));
     * <br>savePref("BooleanKey", String.valueOf(boolean));
     *
     * @param key
     * @param value
     */
    public static void savePref(final String key, final String value) {
        if (key == null)
            return;

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(new LoginInfo(key, value));
                }
            });

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void savePrefAsDate(final String key, final Date value) {
        savePref(key, value == null ? null : String.valueOf(value.getTime()));
    }

    public static void savePrefAsBoolean(final String key, final boolean value) {
        savePref(key, String.valueOf(value));
    }

    public static void savePrefAsJson(final String key, final Object value) {
//        String json = new Gson().toJson(value);
        String json = Utility.convertObjectToJsonUsingGson(value);
        savePref(key, json);
    }


    public static String getPref(final String key, final String defValue) {
        if (key == null)
            return null;

        Realm realm = Realm.getDefaultInstance();
        try {
            LoginInfo first = realm.where(LoginInfo.class)
                    .equalTo("key", key).findFirst();

            if (first == null) {
                return defValue;
            }

            return TextUtils.isEmpty(first.getValue()) ? defValue : first.getValue();

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static Integer getPrefAsInt(final String key, final int defValue) {
        String ret = getPref(key, String.valueOf(defValue));

        return ret == null ? defValue : new Integer(ret);
    }

    public static Date getPrefAsDate(final String key, final Date defValue) {
        // must in long format
        String ret = getPref(key, defValue == null ? null : String.valueOf(defValue.getTime()));

        return ret == null ? defValue : new Date(Long.parseLong(ret));
    }

    public static boolean getPrefAsBoolean(final String key, final boolean defValue) {
        String ret = getPref(key, String.valueOf(defValue));

        return Boolean.parseBoolean(ret);

    }

    public static Object getPrefAsJson(final String key, Class cls, final String defValue) {
        String ret = getPref(key, defValue);

        return Utility.convertJsonBackToObjectUsingGson(ret, cls);
//        return new Gson().fromJson(ret, cls);
    }

    public static File getCompressedImage(Context context, File rawFile, String photoId) throws IOException {
        InputStream in = new FileInputStream(rawFile);
        Bitmap bm2 = BitmapFactory.decodeStream(in);

        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(photoId + "-", ".jpg", outputDir);

        OutputStream stream = new FileOutputStream(outputFile);
        bm2.compress(Bitmap.CompressFormat.JPEG, 10, stream);
//        bm2.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        stream.close();
        in.close();

        return outputFile;
    }

    public static String getAndroidToken() {
        String androidId = getPref(KEY_ANDROID_ID, null);
        if (TextUtils.isEmpty(androidId)) {
            try {
                // may force close during disabling plugin com.google.gms.google-services
                androidId = FirebaseInstanceId.getInstance().getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return androidId;
    }

    public static String getLanguageId(Context ctx) {
        String langId = "en";
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            langId = sharedPrefs.getString("language", "id"); //id / en
        }catch (Exception e){
            e.printStackTrace();
        }
        return langId;

    }

    /**
     * Will be used by Ion.
     * @param relativePath without / on first character
     * @return &lt;serverName&gt;/relativePath
     */
    public static String getSelectedUrlServer(String relativePath) {
        return getSelectedUrlServer() + "/" + relativePath;
    }

    public static String getSelectedServerName() {
        return getPref(KEY_SERVER_NAME_ID, Utility.DEVELOPER_MODE ? Utility.SERVER_DEFAULT_LOKAL_SERVER : Utility.SERVER_DEFAULT_PROD_SERVER);
    }

    public static String getSelectedUrlServer() {
        return Utility.buildUrlAsString(getSelectedServerName());
    }

    public static String getSelectedRootUrlServer() {
        return Utility.buildRootUrlAsString(getSelectedServerName());
    }


}
