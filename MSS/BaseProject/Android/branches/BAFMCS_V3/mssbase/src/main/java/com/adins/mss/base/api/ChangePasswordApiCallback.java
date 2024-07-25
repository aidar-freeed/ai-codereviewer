package com.adins.mss.base.api;

/**
 * Created by adityapurwa on 30/03/15.
 */
public interface ChangePasswordApiCallback {
    void onFailed(String message);

    void onSuccess();
}
