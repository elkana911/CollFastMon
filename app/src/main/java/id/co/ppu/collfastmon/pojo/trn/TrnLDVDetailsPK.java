package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 07-Sep-16.
 */
public class TrnLDVDetailsPK extends RealmObject implements Serializable {

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("seqNo")
    private Long seqNo;

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

    @Override
    public String toString() {
        return "TrnLDVDetailsPK{" +
                "ldvNo='" + ldvNo + '\'' +
                ", seqNo=" + seqNo +
                '}';
    }
}
