package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 22-Nov-16.
 */

public class CollJob extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("collCode")
    private String collCode;

    @SerializedName("collName")
    private String collName;

    @SerializedName("collType")
    private String collType;

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("countVisited")
    private Long countVisited;

    @SerializedName("countLKP")
    private Long countLKP;

    @SerializedName("lastTask")
    private String lastTask;

    @SerializedName("lastTaskTime")
    private Date lastTaskTime;

    private String lastLatitude;

    private String lastLongitude;

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public String getCollType() {
        return collType;
    }

    public void setCollType(String collType) {
        this.collType = collType;
    }

    public String getLdvNo() {
        return ldvNo;
    }

    public void setLdvNo(String ldvNo) {
        this.ldvNo = ldvNo;
    }

    public Long getCountVisited() {
        return countVisited;
    }

    public void setCountVisited(Long countVisited) {
        this.countVisited = countVisited;
    }

    public Long getCountLKP() {
        return countLKP;
    }

    public void setCountLKP(Long countLKP) {
        this.countLKP = countLKP;
    }

    public String getLastTask() {
        return lastTask;
    }

    public void setLastTask(String lastTask) {
        this.lastTask = lastTask;
    }

    public Date getLastTaskTime() {
        return lastTaskTime;
    }

    public void setLastTaskTime(Date lastTaskTime) {
        this.lastTaskTime = lastTaskTime;
    }

    public String getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(String lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public String getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(String lastLongitude) {
        this.lastLongitude = lastLongitude;
    }
}
