package id.co.ppu.collfastmon.rest.request;

import java.util.Date;

/**
 * Created by Eric on 03-Nov-16.
 */
public class RequestCollJobBySpv extends RequestBasic{
    private String spvCode;

    private Date lkpDate;

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

    @Override
    public String toString() {
        return "RequestCollJobBySpv{" +
                "spvCode='" + spvCode + '\'' +
                ", lkpDate=" + lkpDate +
                '}';
    }
}
