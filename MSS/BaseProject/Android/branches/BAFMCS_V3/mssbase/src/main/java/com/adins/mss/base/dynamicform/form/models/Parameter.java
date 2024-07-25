package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gigin.ginanjar on 11/10/2016.
 */

public class Parameter {
    @SerializedName("refId")
    private String refId;
    @SerializedName("flag")
    private String flag;
    @SerializedName("answer")
    private String answer;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
