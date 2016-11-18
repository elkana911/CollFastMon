package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnRVCollPK extends RealmObject implements Serializable {
    @SerializedName("rvCollNo")
    private String rvCollNo;

    @SerializedName("rbvNo")
    private String rbvNo;

    public String getRvCollNo() {
        return rvCollNo;
    }

    public void setRvCollNo(String rvCollNo) {
        this.rvCollNo = rvCollNo;
    }

    public String getRbvNo() {
        return rbvNo;
    }

    public void setRbvNo(String rbvNo) {
        this.rbvNo = rbvNo;
    }

    @Override
    public String toString() {
        return "TrnRVCollPK{" +
                "rvCollNo='" + rvCollNo + '\'' +
                ", rbvNo='" + rbvNo + '\'' +
                '}';
    }
}
