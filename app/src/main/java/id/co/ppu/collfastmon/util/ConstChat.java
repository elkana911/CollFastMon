package id.co.ppu.collfastmon.util;

/**
 * Created by Eric on 13-Dec-16.
 */

public class ConstChat {
    public static final String MESSAGE_TYPE_COMMON = "0";
    public static final String MESSAGE_TYPE_TIMESTAMP = "1";

    public static final String MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME = "0";
    public static final String MESSAGE_STATUS_SERVER_RECEIVED = "1";
    public static final String MESSAGE_STATUS_DELIVERED = "2";
    public static final String MESSAGE_STATUS_READ_AND_OPENED = "3";
    public static final String MESSAGE_STATUS_ALL_READ_AND_OPENED = "9";

    public static final String KEY_FROM = "key_from";
    public static final String KEY_UID = "key_uid";
    public static final String KEY_STATUS = "key_status";
    public static final String KEY_SEQNO = "key_seqno";
    public static final String KEY_TIMESTAMP = "key_timestamp";
    public static final String KEY_MESSAGE = "key_message";

    // global topic to receive app wide push notifications
//    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
//    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}
