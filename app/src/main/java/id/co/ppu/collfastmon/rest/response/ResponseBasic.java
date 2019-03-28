package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBasic {
    @SerializedName("error")
    private Error error;
    @SerializedName("ip")
    private String ip;
}
