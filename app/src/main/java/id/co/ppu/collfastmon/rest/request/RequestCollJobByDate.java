package id.co.ppu.collfastmon.rest.request;

import java.util.Date;

/**
 * Created by Eric on 03-Nov-16.
 */

public class RequestCollJobByDate {
    private String spvCode;

    private Date lkpDate;

    private String ldvNo;

    public String getSpvCode() {
        return spvCode;
    }

    public void setSpvCode(String spvCode) {
        this.spvCode = spvCode;
    }

    public Date getLkpDate() {
        return lkpDate;
    }

    public void setLkpDate(Date lkpDate) {
        this.lkpDate = lkpDate;
    }

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    @Override
    public String toString() {
        return "RequestCollJobByDate{" +
                "spvCode='" + spvCode + '\'' +
                ", lkpDate=" + lkpDate +
                ", ldvNo='" + ldvNo + '\'' +
                '}';
    }
}
