package com.adins.mss.base.todolist;

import com.adins.mss.dao.QuestionSet;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonQuestionSet extends MssResponseType {
    @SerializedName("listQuestionSet")
    private List<QuestionSet> listQuestionSet;

    public List<QuestionSet> getListQuestionSet() {
        return listQuestionSet;
    }

    public void setListQuestionSet(List<QuestionSet> listQuestionSet) {
        this.listQuestionSet = listQuestionSet;
    }


}
