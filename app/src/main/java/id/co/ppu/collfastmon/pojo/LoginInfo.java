package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 22-Sep-16.
 */

public class LoginInfo extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public LoginInfo() {
    }

    public LoginInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public LoginInfo(String key, Date value) {
        this.key = key;
        this.value = value == null ? null : String.valueOf(value.getTime());
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
