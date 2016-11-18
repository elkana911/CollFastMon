package id.co.ppu.collfastmon.pojo;

import java.util.Date;

/**
 * Please dont set as realm object.
 * Created by Eric on 01-Sep-16.
 */
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCollectorType() {
        return collectorType;
    }

    public void setCollectorType(String collectorType) {
        this.collectorType = collectorType;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBussUnit() {
        return bussUnit;
    }

    public void setBussUnit(String bussUnit) {
        this.bussUnit = bussUnit;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId='" + userId + '\'' +
                ", branchId='" + branchId + '\'' +
                ", branchName='" + branchName + '\'' +
                ", emailAddr='" + emailAddr + '\'' +
                ", jabatan='" + jabatan + '\'' +
                ", nik='" + nik + '\'' +
                ", alamat='" + alamat + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", collectorType='" + collectorType + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", birthDate=" + birthDate +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", fullName='" + fullName + '\'' +
                ", bussUnit='" + bussUnit + '\'' +
                '}';
    }
}
