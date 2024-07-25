package com.adins.mss.foundation.scheme.sync;

import com.adins.mss.dao.QuestionSet;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncQuestionSetResponse extends MssResponseType {

    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("listQuestionSet")
    List<QuestionSet> listQuestionSet;

    public List<QuestionSet> getListQuestionSet() {
        return this.listQuestionSet;
    }

    public void setListQuestionSet(List<QuestionSet> value) {
        this.listQuestionSet = value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
