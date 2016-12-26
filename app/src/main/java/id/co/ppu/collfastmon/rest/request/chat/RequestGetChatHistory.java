package id.co.ppu.collfastmon.rest.request.chat;

/**
 * Created by Eric on 21-Nov-16.
 */

public class RequestGetChatHistory {

    private String fromCollCode;
    private String toCollCode;
    private String yyyyMMdd;

    public String getFromCollCode() {
        return fromCollCode;
    }

    public void setFromCollCode(String fromCollCode) {
        this.fromCollCode = fromCollCode;
    }

    public String getToCollCode() {
        return toCollCode;
    }

    public void setToCollCode(String toCollCode) {
        this.toCollCode = toCollCode;
    }

    public String getYyyyMMdd() {
        return yyyyMMdd;
    }

    public void setYyyyMMdd(String yyyyMMdd) {
        this.yyyyMMdd = yyyyMMdd;
    }

    @Override
    public String toString() {
        return "RequestGetChatHistory{" +
                "fromCollCode='" + fromCollCode + '\'' +
                ", toCollCode='" + toCollCode + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}
