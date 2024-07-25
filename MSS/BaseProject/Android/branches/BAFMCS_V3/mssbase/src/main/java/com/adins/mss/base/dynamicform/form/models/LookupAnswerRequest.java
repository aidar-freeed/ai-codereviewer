package com.adins.mss.base.dynamicform.form.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gigin.ginanjar on 07/10/2016.
 */

public class LookupAnswerRequest extends MssRequestType {
    @SerializedName("refId")
    private String identifier;
    @SerializedName("filters")
    private List<Parameter> parameters;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
