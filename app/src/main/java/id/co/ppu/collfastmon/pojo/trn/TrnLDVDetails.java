package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 29-Aug-16.
 */
public class TrnLDVDetails extends RealmObject implements Serializable {

    @SerializedName("pk")
    private TrnLDVDetailsPK pk;

    @SerializedName("period")
    private String period;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("ovdInstNo")
    private Long ovdInstNo;

    @SerializedName("ovdDueDate")
    private Date ovdDueDate;

    @SerializedName("dueDate")
    private Date dueDate;

    @SerializedName("instNo")
    private Long instNo;

    @SerializedName("principalAmount")
    private Long principalAmount;

    @SerializedName("interestAmount")
    private Long interestAmount;

    @SerializedName("principalAMBC")
    private Long principalAMBC;

    @SerializedName("interestAMBC")
    private Long interestAMBC;//AmountMustBeCollected

    @SerializedName("penaltyAMBC")
    private Long penaltyAMBC;

    @SerializedName("principalAmountCollected")
    private Long principalAmountCollected;

    @SerializedName("interestAmountCollected")
    private Long interestAmountCollected;

    @SerializedName("penaltyAmountCollected")
    private Long penaltyAmountCollected;

    @SerializedName("ldvFlag")
    private String ldvFlag;

    @SerializedName("workStatus")
    private String workStatus;

    @SerializedName("principalOutstanding")
    private Long principalOutstanding;

    @SerializedName("occupation")
    private String occupation;

    @SerializedName("subOccupation")
    private String subOccupation;

    @SerializedName("palNo")
    private String palNo;

    @SerializedName("flagToEmrafin")
    private String flagToEmrafin;

    @SerializedName("dateToEmrafin")
    private Date dateToEmrafin;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("dateDone")
    private Date dateDone;

    @SerializedName("startedTimestamp")
    private Date startedTimestamp;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    @SerializedName("monthInst")
    private Long monthInst;

    @SerializedName("daysIntrAmbc")
    private Long daysIntrAmbc;

    @SerializedName("collectionFee")
    private Long collectionFee;

    @SerializedName("lastPaidDate")
    private Date lastPaidDate;

    @SerializedName("dpd")
    private Long dpd;

    @SerializedName("platform")
    private String platform;

    @SerializedName("danaSosial")
    private Long danaSosial;

    public TrnLDVDetailsPK getPk() {
        return pk;
    }

    public void setPk(TrnLDVDetailsPK pk) {
        this.pk = pk;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Long getOvdInstNo() {
        return ovdInstNo;
    }

    public void setOvdInstNo(Long ovdInstNo) {
        this.ovdInstNo = ovdInstNo;
    }

    public Date getOvdDueDate() {
        return ovdDueDate;
    }

    public void setOvdDueDate(Date ovdDueDate) {
        this.ovdDueDate = ovdDueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getInstNo() {
        return instNo;
    }

    public void setInstNo(Long instNo) {
        this.instNo = instNo;
    }

    public Long getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Long principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Long getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(Long interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Long getPrincipalAMBC() {
        return principalAMBC;
    }

    public void setPrincipalAMBC(Long principalAMBC) {
        this.principalAMBC = principalAMBC;
    }

    public Long getInterestAMBC() {
        return interestAMBC;
    }

    public void setInterestAMBC(Long interestAMBC) {
        this.interestAMBC = interestAMBC;
    }

    public Long getPenaltyAMBC() {
        return penaltyAMBC;
    }

    public void setPenaltyAMBC(Long penaltyAMBC) {
        this.penaltyAMBC = penaltyAMBC;
    }

    public Long getPrincipalAmountCollected() {
        return principalAmountCollected;
    }

    public void setPrincipalAmountCollected(Long principalAmountCollected) {
        this.principalAmountCollected = principalAmountCollected;
    }

    public Long getInterestAmountCollected() {
        return interestAmountCollected;
    }

    public void setInterestAmountCollected(Long interestAmountCollected) {
        this.interestAmountCollected = interestAmountCollected;
    }

    public Long getPenaltyAmountCollected() {
        return penaltyAmountCollected;
    }

    public void setPenaltyAmountCollected(Long penaltyAmountCollected) {
        this.penaltyAmountCollected = penaltyAmountCollected;
    }

    public String getLdvFlag() {
        return ldvFlag;
    }

    public void setLdvFlag(String ldvFlag) {
        this.ldvFlag = ldvFlag;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Long getPrincipalOutstanding() {
        return principalOutstanding;
    }

    public void setPrincipalOutstanding(Long principalOutstanding) {
        this.principalOutstanding = principalOutstanding;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSubOccupation() {
        return subOccupation;
    }

    public void setSubOccupation(String subOccupation) {
        this.subOccupation = subOccupation;
    }

    public String getPalNo() {
        return palNo;
    }

    public void setPalNo(String palNo) {
        this.palNo = palNo;
    }

    public Date getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Date startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
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

    public Long getMonthInst() {
        return monthInst;
    }

    public void setMonthInst(Long monthInst) {
        this.monthInst = monthInst;
    }

    public Long getDaysIntrAmbc() {
        return daysIntrAmbc;
    }

    public void setDaysIntrAmbc(Long daysIntrAmbc) {
        this.daysIntrAmbc = daysIntrAmbc;
    }

    public Long getCollectionFee() {
        return collectionFee;
    }

    public void setCollectionFee(Long collectionFee) {
        this.collectionFee = collectionFee;
    }

    public Date getLastPaidDate() {
        return lastPaidDate;
    }

    public void setLastPaidDate(Date lastPaidDate) {
        this.lastPaidDate = lastPaidDate;
    }

    public Long getDpd() {
        return dpd;
    }

    public void setDpd(Long dpd) {
        this.dpd = dpd;
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

    @Override
    public String toString() {
        return "TrnLDVDetails{" +
                "pk=" + pk +
                ", period='" + period + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", custNo='" + custNo + '\'' +
                ", custName='" + custName + '\'' +
                ", ovdInstNo=" + ovdInstNo +
                ", ovdDueDate=" + ovdDueDate +
                ", dueDate=" + dueDate +
                ", instNo=" + instNo +
                ", principalAmount=" + principalAmount +
                ", interestAmount=" + interestAmount +
                ", principalAMBC=" + principalAMBC +
                ", interestAMBC=" + interestAMBC +
                ", penaltyAMBC=" + penaltyAMBC +
                ", principalAmountCollected=" + principalAmountCollected +
                ", interestAmountCollected=" + interestAmountCollected +
                ", penaltyAmountCollected=" + penaltyAmountCollected +
                ", ldvFlag='" + ldvFlag + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", principalOutstanding=" + principalOutstanding +
                ", occupation='" + occupation + '\'' +
                ", subOccupation='" + subOccupation + '\'' +
                ", palNo='" + palNo + '\'' +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", flagDone='" + flagDone + '\'' +
                ", dateDone=" + dateDone +
                ", startedTimestamp=" + startedTimestamp +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                ", monthInst=" + monthInst +
                ", daysIntrAmbc=" + daysIntrAmbc +
                ", collectionFee=" + collectionFee +
                ", lastPaidDate=" + lastPaidDate +
                ", dpd=" + dpd +
                ", platform='" + platform + '\'' +
                ", danaSosial=" + danaSosial +
                '}';
    }
}
