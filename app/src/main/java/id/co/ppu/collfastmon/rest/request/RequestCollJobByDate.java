package id.co.ppu.collfastmon.rest.request;

import java.util.Date;

/**
 * Created by Eric on 03-Nov-16.
 */

public class RequestCollJobByDate extends RequestBasic{
    private String collCode;

    private Date lkpDate;

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public Date getLkpDate() {
        return lkpDate;
    }

    public void setLkpDate(Date lkpDate) {
        this.lkpDate = lkpDate;
    }

    @Override
    public String toString() {
        return "RequestCollJobByDate{" +
                "collCode='" + collCode + '\'' +
                ", lkpDate=" + lkpDate +
                '}';
    }
}
