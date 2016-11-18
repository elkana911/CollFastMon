package id.co.ppu.collfastmon.pojo.trn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 27-Oct-16.
 */

public class TrnCollector extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("collCode")
    private String collCode;

    @SerializedName("spvCode")
    private String spvCode;

    public String getCollCode() {
        return collCode;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getSpvCode() {
        return spvCode;
    }

    public void setSpvCode(String spvCode) {
        this.spvCode = spvCode;
    }

    @Override
    public String toString() {
        return "TrnCollector{" +
                "collCode='" + collCode + '\'' +
                ", spvCode='" + spvCode + '\'' +
                '}';
    }
}
