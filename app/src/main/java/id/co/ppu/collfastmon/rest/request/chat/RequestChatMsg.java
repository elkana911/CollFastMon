package id.co.ppu.collfastmon.rest.request.chat;

import id.co.ppu.collfastmon.pojo.chat.TrnChatMsg;

/**
 * Created by Eric on 18-Nov-16.
 */

public class RequestChatMsg {

    private TrnChatMsg msg;

    public TrnChatMsg getMsg() {
        return msg;
    }

    public void setMsg(TrnChatMsg msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RequestChatMsg{" +
                "msg=" + msg +
                '}';
    }
}
