package id.co.ppu.collfastmon.exceptions;

/**
 * Created by Eric on 24-Feb-17.
 */

public class NoConnectionException extends RuntimeException {

    public NoConnectionException(Throwable ex) {
        super(ex);
    }
    public NoConnectionException(String s) {
        super(s);
    }

    public NoConnectionException() {
        super("");
    }
}
