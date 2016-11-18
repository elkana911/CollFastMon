package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnRVColl extends RealmObject implements Serializable {
    @SerializedName("pk")
    private TrnRVCollPK pk;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("processDate")
    private Date processDate;

    @SerializedName("transDate")
    private Date transDate;

    @SerializedName("collId")
    private String collId;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("instNo")
    private Long instNo;

    @SerializedName("receivedAmount")
    private Long receivedAmount;

    @SerializedName("statusFlag")
    private String statusFlag;

    @SerializedName("paymentFlag")
    private Long paymentFlag;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("dateDone")
    private Date dateDone;

    @SerializedName("notes")
    private String notes;

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("penaltyAc")
    private Long penaltyAc;

    @SerializedName("collFeeAc")
    private Long collFeeAc;

    @SerializedName("daysIntrAc")
    private Long daysIntrAc;

    @SerializedName("platform")
    private String platform;

    @SerializedName("danaSosial")
    private Long danaSosial;

    @SerializedName("closeBatch")
    private String closeBatch;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    public TrnRVCollPK getPk() {
        return pk;
    }

    public void setPk(TrnRVCollPK pk) {
        this.pk = pk;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getInstNo() {
        return instNo;
    }

    public void setInstNo(Long instNo) {
        this.instNo = instNo;
    }

    public Long getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(Long receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public Long getPaymentFlag() {
        return paymentFlag;
    }

    public void setPaymentFlag(Long paymentFlag) {
        this.paymentFlag = paymentFlag;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Long getPenaltyAc() {
        return penaltyAc;
    }

    public void setPenaltyAc(Long penaltyAc) {
        this.penaltyAc = penaltyAc;
    }

    public Long getCollFeeAc() {
        return collFeeAc;
    }

    public void setCollFeeAc(Long collFeeAc) {
        this.collFeeAc = collFeeAc;
    }

    public Long getDaysIntrAc() {
        return daysIntrAc;
    }

    public void setDaysIntrAc(Long daysIntrAc) {
        this.daysIntrAc = daysIntrAc;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getDanaSosial() {
        return danaSosial;
    }

    public void setDanaSosial(Long danaSosial) {
        this.danaSosial = danaSosial;
    }

    public String getCloseBatch() {
        return closeBatch;
    }

    public void setCloseBatch(String closeBatch) {
        this.closeBatch = closeBatch;
    }

    @Override
    public String toString() {
        return "TrnRVColl{" +
                "pk=" + pk +
                ", officeCode='" + officeCode + '\'' +
                ", processDate=" + processDate +
                ", transDate=" + transDate +
                ", collId='" + collId + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", instNo=" + instNo +
                ", receivedAmount=" + receivedAmount +
                ", statusFlag='" + statusFlag + '\'' +
                ", paymentFlag=" + paymentFlag +
                ", startedTimestamp=" + startedTimestamp +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", flagDone='" + flagDone + '\'' +
                ", dateDone=" + dateDone +
                ", notes='" + notes + '\'' +
                ", ldvNo='" + ldvNo + '\'' +
                ", penaltyAc=" + penaltyAc +
                ", collFeeAc=" + collFeeAc +
                ", daysIntrAc=" + daysIntrAc +
                ", platform='" + platform + '\'' +
                ", danaSosial=" + danaSosial +
                ", closeBatch='" + closeBatch + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
