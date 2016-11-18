package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import id.co.ppu.collfastmon.pojo.UserData;

public class ResponseLogin extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private UserData data;

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

}
