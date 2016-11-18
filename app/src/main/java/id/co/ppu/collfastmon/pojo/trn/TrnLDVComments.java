package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnLDVComments extends RealmObject implements Serializable {

    @SerializedName("pk")
    private TrnLDVCommentsPK pk;

    @SerializedName("delqCode")
    private String delqCode;

    @SerializedName("classCode")
    private String classCode;

    @SerializedName("potensi")
    private Long potensi;

    @SerializedName("ocptCode")
    private String ocptCode;

    @SerializedName("ocptCodeSub")
    private String ocptCodeSub;

    @SerializedName("whoMet")
    private String whoMet;

    @SerializedName("promiseDate")
    private Date promiseDate;

    @SerializedName("actionPlan")
    private String actionPlan;

    @SerializedName("planPayAmount")
    private Long planPayAmount;

    @SerializedName("lkpComments")
    private String lkpComments;

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

    @SerializedName("status")
    private String status;

    @SerializedName("apDescription")
    private String apDescription;


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

    public TrnLDVCommentsPK getPk() {
        return pk;
    }

    public void setPk(TrnLDVCommentsPK pk) {
        this.pk = pk;
    }

    public String getDelqCode() {
        return delqCode;
    }

    public void setDelqCode(String delqCode) {
        this.delqCode = delqCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Long getPotensi() {
        return potensi;
    }

    public void setPotensi(Long potensi) {
        this.potensi = potensi;
    }

    public String getOcptCode() {
        return ocptCode;
    }

    public void setOcptCode(String ocptCode) {
        this.ocptCode = ocptCode;
    }

    public String getOcptCodeSub() {
        return ocptCodeSub;
    }

    public void setOcptCodeSub(String ocptCodeSub) {
        this.ocptCodeSub = ocptCodeSub;
    }

    public String getWhoMet() {
        return whoMet;
    }

    public void setWhoMet(String whoMet) {
        this.whoMet = whoMet;
    }

    public Date getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(Date promiseDate) {
        this.promiseDate = promiseDate;
    }

    public String getActionPlan() {
        return actionPlan;
    }

    public void setActionPlan(String actionPlan) {
        this.actionPlan = actionPlan;
    }

    public Long getPlanPayAmount() {
        return planPayAmount;
    }

    public void setPlanPayAmount(Long planPayAmount) {
        this.planPayAmount = planPayAmount;
    }

    public String getLkpComments() {
        return lkpComments;
    }

    public void setLkpComments(String lkpComments) {
        this.lkpComments = lkpComments;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApDescription() {
        return apDescription;
    }

    public void setApDescription(String apDescription) {
        this.apDescription = apDescription;
    }

    @Override
    public String toString() {
        return "TrnLDVComments{" +
                "pk=" + pk +
                ", delqCode='" + delqCode + '\'' +
                ", classCode='" + classCode + '\'' +
                ", potensi=" + potensi +
                ", ocptCode='" + ocptCode + '\'' +
                ", ocptCodeSub='" + ocptCodeSub + '\'' +
                ", whoMet='" + whoMet + '\'' +
                ", promiseDate=" + promiseDate +
                ", actionPlan='" + actionPlan + '\'' +
                ", planPayAmount=" + planPayAmount +
                ", lkpComments='" + lkpComments + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", flagToEmrafin='" + flagToEmrafin + '\'' +
                ", dateToEmrafin=" + dateToEmrafin +
                ", flagDone='" + flagDone + '\'' +
                ", dateDone=" + dateDone +
                ", status='" + status + '\'' +
                ", apDescription='" + apDescription + '\'' +
                ", startedTimestamp=" + startedTimestamp +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy='" + createdBy + '\'' +
                ", lastupdateBy='" + lastupdateBy + '\'' +
                ", lastupdateTimestamp=" + lastupdateTimestamp +
                '}';
    }
}
