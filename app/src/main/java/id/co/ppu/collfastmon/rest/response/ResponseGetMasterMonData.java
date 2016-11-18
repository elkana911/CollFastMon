package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collfastmon.pojo.master.MasterData;
import id.co.ppu.collfastmon.pojo.master.MasterMonData;

/**
 * Created by Eric on 08-Sep-16.
 */
public class ResponseGetMasterMonData extends ResponseBasic implements Serializable{
    @SerializedName("data")
    private MasterMonData data;

    public MasterMonData getData() {
        return data;
    }

    public void setData(MasterMonData data) {
        this.data = data;
    }
}
