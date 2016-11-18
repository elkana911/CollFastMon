package id.co.ppu.collfastmon.listener;

import java.util.Date;

import id.co.ppu.collfastmon.pojo.CollectorJob;
import id.co.ppu.collfastmon.pojo.trn.TrnCollector;

/**
 * Created by Eric on 27-Oct-16.
 */

public interface OnCollectorListListener {
    void onCollSelected(CollectorJob detail, Date lkpDate);
    void onCollLoad(Date lkpDate);
    void onStartRefresh();
    void onEndRefresh();

    void onCollLocation(CollectorJob detail, Date lkpDate);
}
