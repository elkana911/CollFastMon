package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eric on 18-Oct-16.
 */

public class ResponseUserPwd extends ResponseBasic implements Serializable {
    @SerializedName("data")
    private String[] data;

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
