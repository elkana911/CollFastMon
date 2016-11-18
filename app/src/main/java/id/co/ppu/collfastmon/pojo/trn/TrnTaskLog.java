package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 15-Nov-16.
 */

public class TrnTaskLog extends RealmObject implements Serializable {
    @SerializedName("pk")
    private TrnTaskLogPK pk;

    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("deviceModel")
    private String deviceModel;

    @SerializedName("serialNo")
    private String serialNo;

    public TrnTaskLogPK getPk() {
        return pk;
    }

    public void setPk(TrnTaskLogPK pk) {
        this.pk = pk;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String toString() {
        return "TrnTaskLog{" +
                "pk=" + pk +
                ", officeCode='" + officeCode + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", serialNo='" + serialNo + '\'' +
                '}';
    }
}
