package id.co.ppu.collfastmon.pojo;

import java.util.List;

import id.co.ppu.collfastmon.pojo.trn.TrnContractBuckets;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eric on 03-Nov-16.
 */
@Getter
@Setter
public class LKPDataMonitoring {

    private TrnLDVHeader header;
    private List<TrnLDVDetails> details;
    private List<TrnContractBuckets> buckets;

    private List<TrnRVColl> rvColl;
    private List<TrnRepo> repo;
    private List<TrnLDVComments> ldvComments;

}
