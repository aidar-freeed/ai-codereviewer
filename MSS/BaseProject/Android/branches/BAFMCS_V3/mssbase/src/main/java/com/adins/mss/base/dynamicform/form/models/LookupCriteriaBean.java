package com.adins.mss.base.dynamicform.form.models;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gigin.ginanjar on 07/10/2016.
 */

public class LookupCriteriaBean implements Serializable {
    @SerializedName("type")
    private String type;
    @SerializedName("isHide")
    private String isHide;
    @SerializedName("code")
    private String code;
    @SerializedName("value")
    private String value;
    @SerializedName("defAns")
    private List<ParameterAnswer> parameterAnswers;
    @ExcludeFromGson
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ParameterAnswer> getParameterAnswers() {
        return parameterAnswers;
    }

    public void setParameterAnswers(List<ParameterAnswer> parameterAnswers) {
        this.parameterAnswers = parameterAnswers;
    }
}
