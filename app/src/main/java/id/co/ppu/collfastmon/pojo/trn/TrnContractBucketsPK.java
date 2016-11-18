package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 05-Sep-16.
 */
public class TrnContractBucketsPK extends RealmObject implements Serializable {
    @SerializedName("period")
    private String period;

    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("officeCode")
    private String officeCode;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    @Override
    public String toString() {
        return "TrnContractBucketsPK{" +
                "period='" + period + '\'' +
                ", contractNo='" + contractNo + '\'' +
                ", officeCode='" + officeCode + '\'' +
                '}';
    }
}
