package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;

/**
 * Created by Eric on 23-Nov-16.
 */

public class ResponseGetGPSHistory extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private List<TrnCollPos> data;

    public List<TrnCollPos> getData() {
        return data;
    }

    public void setData(List<TrnCollPos> data) {
        this.data = data;
    }
}
