package id.co.ppu.collfastmon.rest.request.chat;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatStatus {
    private String collCode;

    private String status; //-1 UNAVAILABLE, 0 - OFFLINE, 1 - ONLINE, 2 - INVISIBLE

    private String message;

    private String androidId;

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    @Override
    public String toString() {
        return "RequestChatStatus{" +
                "collCode='" + collCode + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", androidId='" + androidId + '\'' +
                '}';
    }
}
