package id.co.ppu.collfastmon.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 28-Oct-16.
 */

public class MstPotensi extends RealmObject implements Serializable {

    @SerializedName("delqId")
    private String delqId;

    @SerializedName("classCode")
    private String classCode;

    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("potensi")
    private Long potensi;

    @SerializedName("potensiDesc")
    private String potensiDesc;

    @SerializedName("isActive")
    private String isActive;

    public String getDelqId() {
        return delqId;
    }

    public void setDelqId(String delqId) {
        this.delqId = delqId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public Long getPotensi() {
        return potensi;
    }

    public void setPotensi(Long potensi) {
        this.potensi = potensi;
    }

    public String getPotensiDesc() {
        return potensiDesc;
    }

    public void setPotensiDesc(String potensiDesc) {
        this.potensiDesc = potensiDesc;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return potensiDesc;
    }
}
