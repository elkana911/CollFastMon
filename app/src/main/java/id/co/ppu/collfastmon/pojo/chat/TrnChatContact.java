package id.co.ppu.collfastmon.pojo.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 18-Dec-16.
 */

public class TrnChatContact extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("collCode")
    private String collCode;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("photoUrl")
    private String photoUrl;

    @SerializedName("statusMsg")
    private String statusMsg;

    @SerializedName("androidId")
    private String androidId;

    @SerializedName("room")
    private String room;

    /**
     * BOT, HUMAN
     */
    @SerializedName("contactType")
    private String contactType;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "TrnChatContact{" +
                "collCode='" + collCode + '\'' +
                ", nickName='" + nickName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", androidId='" + androidId + '\'' +
                ", room='" + room + '\'' +
                ", contactType='" + contactType + '\'' +
                '}';
    }
}
