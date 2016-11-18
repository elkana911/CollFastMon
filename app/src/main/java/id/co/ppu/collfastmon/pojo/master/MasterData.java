package id.co.ppu.collfastmon.pojo.master;

import java.util.List;

/**
 * Created by Eric on 08-Sep-16.
 */
public class MasterData {
    private List<MstParam> params;
    private List<MstLDVStatus> ldpStatus;
    private List<MstLDVParameters> ldpParameters;
    private List<MstLDVClassifications> ldpClassifications;
    private List<MstDelqReasons> delqReasons;
    private List<MstOffices> offices;
    private List<MstPotensi> potensi;

    public List<MstParam> getParams() {
        return params;
    }

    public void setParams(List<MstParam> params) {
        this.params = params;
    }

    public List<MstLDVStatus> getLdpStatus() {
        return ldpStatus;
    }

    public void setLdpStatus(List<MstLDVStatus> ldpStatus) {
        this.ldpStatus = ldpStatus;
    }

    public List<MstLDVParameters> getLdpParameters() {
        return ldpParameters;
    }

    public void setLdpParameters(List<MstLDVParameters> ldpParameters) {
        this.ldpParameters = ldpParameters;
    }

    public List<MstLDVClassifications> getLdpClassifications() {
        return ldpClassifications;
    }

    public void setLdpClassifications(List<MstLDVClassifications> ldpClassifications) {
        this.ldpClassifications = ldpClassifications;
    }

    public List<MstDelqReasons> getDelqReasons() {
        return delqReasons;
    }

    public void setDelqReasons(List<MstDelqReasons> delqReasons) {
        this.delqReasons = delqReasons;
    }

    public List<MstOffices> getOffices() {
        return offices;
    }

    public void setOffices(List<MstOffices> offices) {
        this.offices = offices;
    }

    public List<MstPotensi> getPotensi() {
        return potensi;
    }

    public void setPotensi(List<MstPotensi> potensi) {
        this.potensi = potensi;
    }

}
