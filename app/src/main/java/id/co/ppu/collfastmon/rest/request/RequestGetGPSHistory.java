package id.co.ppu.collfastmon.rest.request;

import java.util.Date;

/**
 * Created by Eric on 23-Nov-16.
 */

public class RequestGetGPSHistory extends RequestBasic {
    private String collectorCode;

    private Date fromDate;

    private Date toDate;

    private String business;

    public String getCollectorCode() {
        return collectorCode;
    }

    public void setCollectorCode(String collectorCode) {
        this.collectorCode = collectorCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "RequestGetGPSHistory{" +
                "collectorCode='" + collectorCode + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", business='" + business + '\'' +
                '}';
    }
}
