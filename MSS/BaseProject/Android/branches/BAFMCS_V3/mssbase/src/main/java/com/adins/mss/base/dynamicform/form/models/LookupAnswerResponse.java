package com.adins.mss.base.dynamicform.form.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gigin.ginanjar on 07/10/2016.
 */

public class LookupAnswerResponse extends MssResponseType {
    @SerializedName("answer")
    public List<LookupCriteriaBean> answer;
    @SerializedName("otherAnswer")
    public List<LookupCriteriaBean> otherAnswer;
    @SerializedName("recipient")
    public List<LookupCriteriaBean> recipient;

    public List<LookupCriteriaBean> getRecipient() {
        return recipient;
    }

    public void setRecipient(List<LookupCriteriaBean> recipient) {
        this.recipient = recipient;
    }

    public List<LookupCriteriaBean> getAnswer() {
        return answer;
    }

    public void setAnswer(List<LookupCriteriaBean> answer) {
        this.answer = answer;
    }

    public List<LookupCriteriaBean> getOtherAnswer() {
        return otherAnswer;
    }

    public void setOtherAnswer(List<LookupCriteriaBean> otherAnswer) {
        this.otherAnswer = otherAnswer;
    }
}
