package id.co.ppu.collfastmon.rest.request;

import java.util.List;

import id.co.ppu.collfastmon.pojo.trn.TrnCollPos;

/**
 * Created by Eric on 13-Oct-16.
 */
public class RequestSyncLocation extends RequestBasic{
    private List<TrnCollPos> list;

    public List<TrnCollPos> getList() {
        return list;
    }

    public void setList(List<TrnCollPos> list) {
        this.list = list;
    }
}
