package id.co.ppu.collfastmon.pojo;

import java.util.List;

import id.co.ppu.collfastmon.pojo.trn.TrnContractBuckets;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;

/**
 * Created by Eric on 03-Nov-16.
 */

public class LKPDataMonitoring {

    private TrnLDVHeader header;
    private List<TrnLDVDetails> details;
    private List<TrnContractBuckets> buckets;

    private List<TrnRVColl> rvColl;
    private List<TrnRepo> repo;
    private List<TrnLDVComments> ldvComments;

    public TrnLDVHeader getHeader() {
        return header;
    }

    public void setHeader(TrnLDVHeader header) {
        this.header = header;
    }

    public List<TrnLDVDetails> getDetails() {
        return details;
    }

    public void setDetails(List<TrnLDVDetails> details) {
        this.details = details;
    }

    public List<TrnContractBuckets> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<TrnContractBuckets> buckets) {
        this.buckets = buckets;
    }

    public List<TrnRVColl> getRvColl() {
        return rvColl;
    }

    public void setRvColl(List<TrnRVColl> rvColl) {
        this.rvColl = rvColl;
    }

    public List<TrnRepo> getRepo() {
        return repo;
    }

    public void setRepo(List<TrnRepo> repo) {
        this.repo = repo;
    }

    public List<TrnLDVComments> getLdvComments() {
        return ldvComments;
    }

    public void setLdvComments(List<TrnLDVComments> ldvComments) {
        this.ldvComments = ldvComments;
    }
}
