package id.co.ppu.collfastmon.rest.request;

/**
 * Created by Eric on 21-Nov-16.
 */

public class RequestReopenBatch {

    private String ldvNo;
    private String spvCode;
    private String yyyyMMdd;

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

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
        return "RequestReopenBatch{" +
                "ldvNo='" + ldvNo + '\'' +
                ", spvCode='" + spvCode + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}
