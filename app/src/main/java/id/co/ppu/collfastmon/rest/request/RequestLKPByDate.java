package id.co.ppu.collfastmon.rest.request;

/**
 * Created by Eric on 22-Sep-16.
 */

public class RequestLKPByDate {
    private String collectorCode;
    private String yyyyMMdd;

    public String getCollectorCode() {
        return collectorCode;
    }

    public void setCollectorCode(String collectorCode) {
        this.collectorCode = collectorCode;
    }

    public String getYyyyMMdd() {
        return yyyyMMdd;
    }

    public void setYyyyMMdd(String yyyyMMdd) {
        this.yyyyMMdd = yyyyMMdd;
    }

    @Override
    public String toString() {
        return "RequestLKPByDate{" +
                "collectorCode='" + collectorCode + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}
