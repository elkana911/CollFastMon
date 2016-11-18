package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 29-Aug-16.
 */
public class DisplayLDVDetails extends RealmObject implements Serializable {

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("collId")
    private String collId;

    @SerializedName("lkpDate")
    private Date lkpDate;

    @PrimaryKey
    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("workStatus")
    private String workStatus;

    @SerializedName("ldvFlag")
    private String ldvFlag;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("createdBy")
    private String createdBy;

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
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

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public Date getLkpDate() {
        return lkpDate;
    }

    public void setLkpDate(Date lkpDate) {
        this.lkpDate = lkpDate;
    }

    public String getFlagDone() {
        return flagDone;
    }

    public void setFlagDone(String flagDone) {
        this.flagDone = flagDone;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getLdvFlag() {
        return ldvFlag;
    }

    public void setLdvFlag(String ldvFlag) {
        this.ldvFlag = ldvFlag;
    }

    @Override
    public String toString() {
        return "DisplayLDVDetails{" +
                "ldvNo='" + ldvNo + '\'' +
                ", seqNo=" + seqNo +
                ", collId='" + collId + '\'' +
                ", lkpDate=" + lkpDate +
                ", contractNo='" + contractNo + '\'' +
                ", custNo='" + custNo + '\'' +
                ", custName='" + custName + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", ldvFlag='" + ldvFlag + '\'' +
                ", flagDone='" + flagDone + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
