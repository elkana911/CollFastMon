package id.co.ppu.collfastmon.listener;

import java.util.List;

import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;

/**
 * Created by Eric on 05-Oct-16.
 */

public interface OnGetChatContactListener {
    void onSuccess(List<TrnChatContact> list);
    void onFailure(Throwable throwable);
    void onSkip();
}
