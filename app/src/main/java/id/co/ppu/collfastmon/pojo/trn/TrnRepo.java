package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnRepo extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("repoNo")
    private String repoNo;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("bastbjFlag")
    private String bastbjFlag;

    @SerializedName("palNo")
    private String palNo;

    @SerializedName("stnkStatus")
    private String stnkStatus;

    @SerializedName("bastbjNo")
    private String bastbjNo;

    @SerializedName("repoComments")
    private String repoComments;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

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

    public String getRepoNo() {
        return repoNo;
    }

    public void setRepoNo(String repoNo) {
        this.repoNo = repoNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getBastbjFlag() {
        return bastbjFlag;
    }

    public void setBastbjFlag(String bastbjFlag) {
        this.bastbjFlag = bastbjFlag;
    }

    public String getPalNo() {
        return palNo;
    }

    public void setPalNo(String palNo) {
        this.palNo = palNo;
    }

    public String getStnkStatus() {
        return stnkStatus;
    }

    public void setStnkStatus(String stnkStatus) {
        this.stnkStatus = stnkStatus;
    }

    public String getBastbjNo() {
        return bastbjNo;
    }

    public void setBastbjNo(String bastbjNo) {
        this.bastbjNo = bastbjNo;
    }

    public String getRepoComments() {
        return repoComments;
    }

    public void setRepoComments(String repoComments) {
        this.repoComments = repoComments;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    @Override
    public String toString() {
        return "TrnRepo{" +
                "repoNo='" + repoNo + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", custNo='" + custNo + '\'' +
                ", custName='" + custName + '\'' +
                ", bastbjFlag='" + bastbjFlag + '\'' +
                ", palNo='" + palNo + '\'' +
                ", stnkStatus='" + stnkStatus + '\'' +
                ", bastbjNo='" + bastbjNo + '\'' +
                ", repoComments='" + repoComments + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
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
