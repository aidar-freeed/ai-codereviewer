package com.adins.mss.base.dynamicform;

import com.adins.mss.dao.QuestionSet;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponseQuestionSet extends MssResponseType {
    /**
     * Property listQuestionSet
     */
    @SerializedName("listQuestionSet")
    List<QuestionSet> listQuestionSet;

    /**
     * Gets the listQuestionSet
     */
    public List<QuestionSet> getListQuestionSet() {
        return this.listQuestionSet;
    }

    /**
     * Sets the listQuestionSet
     */
    public void setListQuestionSet(List<QuestionSet> value) {
        this.listQuestionSet = value;
    }
}
