package id.co.ppu.collfastmon.rest.request;

/**
 * Created by Eric on 03-Nov-16.
 */

public class RequestCollJobByDate {
    private String spvCode;
    private String yyyyMMdd;

    public String getSpvCode() {
        return spvCode;
    }

    public void setSpvCode(String spvCode) {
        this.spvCode = spvCode;
    }

    public String getYyyyMMdd() {
        return yyyyMMdd;
    }

    public void setYyyyMMdd(String yyyyMMdd) {
        this.yyyyMMdd = yyyyMMdd;
    }

    @Override
    public String toString() {
        return "RequestCollJobByDate{" +
                "spvCode='" + spvCode + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}
