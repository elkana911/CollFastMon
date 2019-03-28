package id.co.ppu.collfastmon.pojo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Please dont set as realm object.
 * Created by Eric on 01-Sep-16.
 */
@Getter
@Setter
@ToString
public class UserData {

    private String userId;
    private String branchId;
    private String branchName;
    private String emailAddr;
    private String jabatan;
    private String nik;
    private String alamat;
    private String phoneNo;
    private String collectorType;
    private String userPwd;
    private String birthPlace;
    private Date birthDate;
    private String mobilePhone;
    private String fullName;
    private String bussUnit;

}
