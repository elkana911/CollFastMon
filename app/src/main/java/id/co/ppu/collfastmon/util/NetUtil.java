package id.co.ppu.collfastmon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import id.co.ppu.collfastmon.R;

/**
 * Created by Eric on 30-Aug-16.
 */
public class NetUtil {

    public static final String SERVER_SCHEME = "http";
    public static final String SERVER_USERNAME = "admin";
    public static final String SERVER_PWD = "4dminMobil3";

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connec.getActiveNetworkInfo() != null)
                && (connec.getActiveNetworkInfo().isAvailable())
                && (connec.getActiveNetworkInfo().isConnected());
    }


    public static boolean isConnectedUnlessToast(Context ctx) {
        boolean ret = isConnected(ctx);

        if (ret)
            return true;

        Toast.makeText(ctx, ctx.getString(R.string.error_offline), Toast.LENGTH_SHORT).show();

        return false;
    }
}
