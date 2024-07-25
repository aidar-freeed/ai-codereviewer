package com.adins.mss.foundation.scheme.sync;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public interface SyncQuestionSetListener {
    void onProgress();

    void onError(SyncQuestionSetResponse response);

    void onSuccess(SyncQuestionSetResponse response, String uuidScheme);
}
