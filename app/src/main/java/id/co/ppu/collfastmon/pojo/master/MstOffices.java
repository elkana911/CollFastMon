package id.co.ppu.collfastmon.pojo.master;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eric on 22-Sep-16.
 */

public class MstOffices extends RealmObject implements Serializable{

    @PrimaryKey
    @SerializedName("officeCode")
    private String officeCode;

    @SerializedName("officeType")
    private String officeType;

    @SerializedName("regionalId")
    private String regionalId;

    @SerializedName("address1")
    private String address1;

    @SerializedName("address2")
    private String address2;

    @SerializedName("address3")
    private String address3;

    @SerializedName("city")
    private String city;

    @SerializedName("phone1")
    private String phone1;

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getOfficeType() {
        return officeType;
    }

    public void setOfficeType(String officeType) {
        this.officeType = officeType;
    }

    public String getRegionalId() {
        return regionalId;
    }

    public void setRegionalId(String regionalId) {
        this.regionalId = regionalId;
    }

    @Override
    public String toString() {
        return "MstOffices{" +
                "officeCode='" + officeCode + '\'' +
                ", officeType='" + officeType + '\'' +
                ", regionalId='" + regionalId + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", city='" + city + '\'' +
                ", phone1='" + phone1 + '\'' +
                '}';
    }
}
