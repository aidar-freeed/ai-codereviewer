package com.adins.mss.coll.api;

import java.util.List;

/**
 * Created by adityapurwa on 16/03/15.
 */
public interface FakeSynchronizationCallback<T> {
    public void onSuccess(List<T> entities);

    public void onFailed();
}
