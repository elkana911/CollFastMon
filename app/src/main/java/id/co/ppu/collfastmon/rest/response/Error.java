package id.co.ppu.collfastmon.rest.response;

import com.google.gson.annotations.SerializedName;

public class Error {
    @SerializedName("errorName")
    private String errorName;
    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("errorDesc")
    private String errorDesc;

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
