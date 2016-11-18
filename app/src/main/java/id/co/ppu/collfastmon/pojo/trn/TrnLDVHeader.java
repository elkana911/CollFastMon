package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Aug-16.
 */
public class TrnLDVHeader extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("ldvDate")
    private Date ldvDate;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("collCode")
    private String collCode;

    @SerializedName("unitTotal")
    private Long unitTotal;

    @SerializedName("prncAMBC")
    private Long prncAMBC;

    @SerializedName("prncAC")
    private Long prncAC;

    @SerializedName("intrAMBC")
    private Long intrAMBC;

    @SerializedName("intrAC")
    private Long intrAC;

    @SerializedName("ambcTotal")
    private Long ambcTotal;

    @SerializedName("acTotal")
    private Long acTotal;

    @SerializedName("workFlag")
    private String workFlag;

    @SerializedName("approvedDate")
    private Date approvedDate;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("closeBatch")
    private String closeBatch;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("dateDone")
    private Date dateDone;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Date getLdvDate() {
        return ldvDate;
    }

    public void setLdvDate(Date ldvDate) {
        this.ldvDate = ldvDate;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public Long getUnitTotal() {
        return unitTotal;
    }

    public void setUnitTotal(Long unitTotal) {
        this.unitTotal = unitTotal;
    }

    public Long getPrncAMBC() {
        return prncAMBC;
    }

    public void setPrncAMBC(Long prncAMBC) {
        this.prncAMBC = prncAMBC;
    }

    public Long getPrncAC() {
        return prncAC;
    }

    public void setPrncAC(Long prncAC) {
        this.prncAC = prncAC;
    }

    public Long getIntrAMBC() {
        return intrAMBC;
    }

    public void setIntrAMBC(Long intrAMBC) {
        this.intrAMBC = intrAMBC;
    }

    public Long getIntrAC() {
        return intrAC;
    }

    public void setIntrAC(Long intrAC) {
        this.intrAC = intrAC;
    }

    public Long getAmbcTotal() {
        return ambcTotal;
    }

    public void setAmbcTotal(Long ambcTotal) {
        this.ambcTotal = ambcTotal;
    }

    public Long getAcTotal() {
        return acTotal;
    }

    public void setAcTotal(Long acTotal) {
        this.acTotal = acTotal;
    }

    public String getWorkFlag() {
        return workFlag;
    }

    public void setWorkFlag(String workFlag) {
        this.workFlag = workFlag;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public String getCloseBatch() {
        return closeBatch;
    }

    public void setCloseBatch(String closeBatch) {
        this.closeBatch = closeBatch;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastupdateBy() {
        return lastupdateBy;
    }

    public void setLastupdateBy(String lastupdateBy) {
        this.lastupdateBy = lastupdateBy;
    }

    public Date getLastupdateTimestamp() {
        return lastupdateTimestamp;
    }

    public void setLastupdateTimestamp(Date lastupdateTimestamp) {
        this.lastupdateTimestamp = lastupdateTimestamp;
    }

    public String getFlagToEmrafin() {
        return flagToEmrafin;
    }

    public void setFlagToEmrafin(String flagToEmrafin) {
        this.flagToEmrafin = flagToEmrafin;
    }

    public Date getDateToEmrafin() {
        return dateToEmrafin;
    }

    public void setDateToEmrafin(Date dateToEmrafin) {
        this.dateToEmrafin = dateToEmrafin;
    }

    public String getFlagDone() {
        return flagDone;
    }

    public void setFlagDone(String flagDone) {
        this.flagDone = flagDone;
    }

    public Date getDateDone() {
        return dateDone;
    }

    public void setDateDone(Date dateDone) {
        this.dateDone = dateDone;
    }

    @Override
    public String toString() {
        return "TrnLDVHeader{" +
                "ldvNo='" + ldvNo + '\'' +
                ", ldvDate=" + ldvDate +
                ", officeCode='" + officeCode + '\'' +
                ", collCode='" + collCode + '\'' +
                ", unitTotal=" + unitTotal +
                ", prncAMBC=" + prncAMBC +
                ", prncAC=" + prncAC +
                ", intrAMBC=" + intrAMBC +
                ", intrAC=" + intrAC +
                ", ambcTotal=" + ambcTotal +
                ", acTotal=" + acTotal +
                ", workFlag='" + workFlag + '\'' +
                ", approvedDate=" + approvedDate +
                ", startedTimestamp=" + startedTimestamp +
                ", closeBatch='" + closeBatch + '\'' +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", flagDone='" + flagDone + '\'' +
                ", dateDone=" + dateDone +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
