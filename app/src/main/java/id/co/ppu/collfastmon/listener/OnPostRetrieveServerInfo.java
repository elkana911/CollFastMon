package id.co.ppu.collfastmon.listener;

import id.co.ppu.collfastmon.pojo.ServerInfo;

/**
 * Created by Eric on 29-Sep-16.
 */
public interface OnPostRetrieveServerInfo {
    void onSuccess(ServerInfo serverInfo);

    void onFailure(Throwable throwable);
}
