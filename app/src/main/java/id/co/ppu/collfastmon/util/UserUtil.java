package id.co.ppu.collfastmon.util;

/**
 * Created by Eric on 24-Feb-17.
 */

public class UserUtil {
    public static boolean userIsAdmin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }

    public static boolean userIsDemo(String username, String password) {
        return username.equals("demo") && password.equals("demo");
    }
}
