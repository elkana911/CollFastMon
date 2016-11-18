package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collfastmon.pojo.LKPDataMonitoring;

/**
 * Created by Eric on 03-Nov-16.
 */

public class ResponseGetLKPMonitoring extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private LKPDataMonitoring data;

    public LKPDataMonitoring getData() {
        return data;
    }

    public void setData(LKPDataMonitoring data) {
        this.data = data;
    }
}
