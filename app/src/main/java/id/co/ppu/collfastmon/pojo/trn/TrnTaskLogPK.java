package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 15-Nov-16.
 */

public class TrnTaskLogPK extends RealmObject implements Serializable {
    @SerializedName("userCode")
    private String userCode;

    @SerializedName("taskDate")
    private Date taskDate;

    @SerializedName("taskCode")
    private String taskCode;

    @SerializedName("seqNo")
    private Long seqNo;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Date taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    @Override
    public String toString() {
        return "TrnTaskLogPK{" +
                "userCode='" + userCode + '\'' +
                ", taskDate=" + taskDate +
                ", taskCode='" + taskCode + '\'' +
                ", seqNo=" + seqNo +
                '}';
    }

}
