package com.adins.mss.foundation.sync.api;

/**
 * Created by adityapurwa on 12/03/15.
 */
public interface SynchronizationMapper<T> {
    public void map(String property, String value, T target);
}
