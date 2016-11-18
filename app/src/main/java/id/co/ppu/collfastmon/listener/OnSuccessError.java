package id.co.ppu.collfastmon.listener;

/**
 * Created by Eric on 05-Oct-16.
 */

public interface OnSuccessError {
    void onSuccess(String msg);
    void onFailure(Throwable throwable);
    void onSkip();
}
