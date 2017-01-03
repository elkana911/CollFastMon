package id.co.ppu.collfastmon.pojo.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 18-Dec-16.
 */

public class TrnChatMsg extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("uid")
    private String uid;

    // increment by 10, so someone may insert between it
    // maybe duplicates. be careful
//    @SerializedName("seqNo")
//    private Long seqNo;

    @SerializedName("fromCollCode")
    private String fromCollCode;

    @SerializedName("toCollCode")
    private String toCollCode;

    @SerializedName("message")
    private String message;

    // 0: common text, 1: timestamp, 2: server message ?
    @SerializedName("messageType")
    private String messageType;

    // 0 or null : unopen, 1: server receive, 2: target client receive, 3: read and opened
    @SerializedName("messageStatus")
    private String messageStatus;

    @SerializedName("createdTimestamp")
    private Date createdTimestamp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "TrnChatMsg{" +
                "uid='" + uid + '\'' +
                ", fromCollCode='" + fromCollCode + '\'' +
                ", toCollCode='" + toCollCode + '\'' +
                ", message='" + message + '\'' +
                ", messageType='" + messageType + '\'' +
                ", messageStatus='" + messageStatus + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }

    public String getFromCollCode() {
        return fromCollCode;
    }

    public void setFromCollCode(String fromCollCode) {
        this.fromCollCode = fromCollCode;
    }

    public String getToCollCode() {
        return toCollCode;
    }

    public void setToCollCode(String toCollCode) {
        this.toCollCode = toCollCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

}

