package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collfastmon.pojo.CollJob;

/**
 * Created by Eric on 18-Oct-16.
 */
public class ResponseGetCollJobList extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private List<CollJob> data;

    public List<CollJob> getData() {
        return data;
    }

    public void setData(List<CollJob> data) {
        this.data = data;
    }
}
