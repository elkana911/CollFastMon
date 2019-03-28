package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eric on 22-Nov-16.
 */
@Getter
@Setter
public class CollJob extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("collCode")
    private String collCode;

    @SerializedName("collName")
    private String collName;

    @SerializedName("collType")
    private String collType;

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("countVisited")
    private Long countVisited;

    @SerializedName("countLKP")
    private Long countLKP;

    @SerializedName("lkpDate")
    private Date lkpDate;

    @SerializedName("absenDate")
    private Date absenDate;

    @SerializedName("lastTask")
    private String lastTask;

    @SerializedName("lastTaskTime")
    private Date lastTaskTime;

    @SerializedName("lastLatitude")
    private String lastLatitude;

    @SerializedName("lastLongitude")
    private String lastLongitude;

}
