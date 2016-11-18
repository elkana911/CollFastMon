package id.co.ppu.collfastmon.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 07-Sep-16.
 */
public class MstLDVClassifications extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("classCode")
    private String classCode;

    @SerializedName("description")
    private String description;

    @SerializedName("needSpecialCollect")
    private String needSpecialCollect;

    @SerializedName("visible")
    private String visible;

    @SerializedName("startedTimetamp")
    private Date startedTimetamp;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("lastupdateBy")
    private String lastupdateBy;

    @SerializedName("lastupdateTimestamp")
    private Date lastupdateTimestamp;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeedSpecialCollect() {
        return needSpecialCollect;
    }

    public void setNeedSpecialCollect(String needSpecialCollect) {
        this.needSpecialCollect = needSpecialCollect;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public Date getStartedTimetamp() {
        return startedTimetamp;
    }

    public void setStartedTimetamp(Date startedTimetamp) {
        this.startedTimetamp = startedTimetamp;
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
        return description;
    }
}
