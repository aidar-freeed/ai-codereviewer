package com.adins.mss.base.depositreport;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by angga.permadi on 8/30/2016.
 */
public class GetImageRequest extends MssRequestType {

    @SerializedName("uuid_task_h")
    String uuidTaskH;

    @SerializedName("question_id")
    String questionId;

    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
