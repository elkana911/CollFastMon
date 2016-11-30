package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collfastmon.pojo.trn.TrnTaskLog;

/**
 * Created by Eric on 18-Oct-16.
 */

public class ResponseGetTaskLog extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private List<TrnTaskLog> data;

    public List<TrnTaskLog> getData() {
        return data;
    }

    public void setData(List<TrnTaskLog> data) {
        this.data = data;
    }
}
