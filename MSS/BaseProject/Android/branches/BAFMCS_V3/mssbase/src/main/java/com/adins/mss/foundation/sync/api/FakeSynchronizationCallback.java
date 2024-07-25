package com.adins.mss.foundation.sync.api;

import java.util.List;

/**
 * Created by adityapurwa on 16/03/15.
 */
public interface FakeSynchronizationCallback<T> {
    void onSuccess(List<T> entities);

    void onFailed(String errMessage);
}
