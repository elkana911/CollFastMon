package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Eric on 22-Sep-16.
 */

public class ServerInfo extends RealmObject implements Serializable {

    @SerializedName("serverDate")
    private Date serverDate;

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverDate=" + serverDate +
                '}';
    }
}
