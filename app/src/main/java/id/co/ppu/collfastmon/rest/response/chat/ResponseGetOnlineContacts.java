package id.co.ppu.collfastmon.rest.response.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import id.co.ppu.collfastmon.pojo.chat.TrnChatContact;
import id.co.ppu.collfastmon.rest.response.ResponseBasic;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eric on 20-Nov-16.
 */
@Getter
@Setter
public class ResponseGetOnlineContacts extends ResponseBasic implements Serializable {

    @SerializedName("data")
    private List<TrnChatContact> data;
}
