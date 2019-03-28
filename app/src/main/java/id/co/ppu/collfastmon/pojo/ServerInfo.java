package id.co.ppu.collfastmon.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Eric on 22-Sep-16.
 */
@Getter
@Setter
@ToString
public class ServerInfo extends RealmObject implements Serializable {

    @SerializedName("serverDate")
    private Date serverDate;

}
