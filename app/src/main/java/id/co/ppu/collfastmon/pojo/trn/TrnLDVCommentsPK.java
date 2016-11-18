package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Eric on 13-Sep-16.
 */
public class TrnLDVCommentsPK extends RealmObject implements Serializable {

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("contractNo")
    private String contractNo;

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

    @Override
    public String toString() {
        return "TrnLDVCommentsPK{" +
                "ldvNo='" + ldvNo + '\'' +
                ", seqNo=" + seqNo +
                ", contractNo='" + contractNo + '\'' +
                '}';
    }
}
