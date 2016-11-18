package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collfastmon.pojo.CollectorJob;

/**
 * Created by Eric on 18-Oct-16.
 */

public class ResponseGetCollJob extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private List<CollectorJob> data;

    public List<CollectorJob> getData() {
        return data;
    }

    public void setData(List<CollectorJob> data) {
        this.data = data;
    }
}
