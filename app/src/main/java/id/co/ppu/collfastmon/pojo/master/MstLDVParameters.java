package id.co.ppu.collfastmon.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 07-Sep-16.
 */
public class MstLDVParameters extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("lkpFlag")
    private String lkpFlag;

    @SerializedName("description")
    private String description;

    @SerializedName("needComment")
    private String needComment;

    @SerializedName("needDate")
    private String needDate;

    @SerializedName("workFlag")
    private String workFlag;

    @SerializedName("needCollect")
    private String needCollect;

    @SerializedName("isActive")
    private String isActive;

    @SerializedName("maxPromiseDays")
    private Long maxPromiseDays;

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

    public String getLkpFlag() {
        return lkpFlag;
    }

    public void setLkpFlag(String lkpFlag) {
        this.lkpFlag = lkpFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeedComment() {
        return needComment;
    }

    public void setNeedComment(String needComment) {
        this.needComment = needComment;
    }

    public String getNeedDate() {
        return needDate;
    }

    public void setNeedDate(String needDate) {
        this.needDate = needDate;
    }

    public String getWorkFlag() {
        return workFlag;
    }

    public void setWorkFlag(String workFlag) {
        this.workFlag = workFlag;
    }

    public String getNeedCollect() {
        return needCollect;
    }

    public void setNeedCollect(String needCollect) {
        this.needCollect = needCollect;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
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

    public Long getMaxPromiseDays() {
        return maxPromiseDays;
    }

    public void setMaxPromiseDays(Long maxPromiseDays) {
        this.maxPromiseDays = maxPromiseDays;
    }

    @Override
    public String toString() {
        return description;
    }
}
