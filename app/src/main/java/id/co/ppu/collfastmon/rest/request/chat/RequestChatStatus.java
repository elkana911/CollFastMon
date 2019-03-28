package id.co.ppu.collfastmon.rest.request.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Eric on 18-Nov-16.
 */
@Getter
@Setter
@ToString
public class RequestChatStatus {
    private String collCode;

    private String status; //-1 UNAVAILABLE, 0 - OFFLINE, 1 - ONLINE, 2 - INVISIBLE

    private String message;

    private String androidId;

}
