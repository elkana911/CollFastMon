package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Eric on 29-Aug-16.
 */
@Getter
@Setter
@ToString
public class DisplayLDVDetails extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("contractNo")
    private String contractNo;

    @SerializedName("ldvNo")
    private String ldvNo;

    @SerializedName("seqNo")
    private Long seqNo;

    @SerializedName("collId")
    private String collId;

    @SerializedName("lkpDate")
    private Date lkpDate;

    @SerializedName("custNo")
    private String custNo;

    @SerializedName("custName")
    private String custName;

    @SerializedName("workStatus")
    private String workStatus;

    @SerializedName("ldvFlag")
    private String ldvFlag;

    @SerializedName("flagDone")
    private String flagDone;

    @SerializedName("createdBy")
    private String createdBy;

}
