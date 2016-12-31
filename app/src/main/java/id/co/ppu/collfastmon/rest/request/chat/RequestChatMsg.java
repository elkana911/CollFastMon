package id.co.ppu.collfastmon.rest.request.chat;

import java.util.List;

import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatMsg {

    private List<TrnChatMsg> msg;

    public List<TrnChatMsg> getMsg() {
        return msg;
    }

    public void setMsg(List<TrnChatMsg> msg) {
        this.msg = msg;
    }
}
