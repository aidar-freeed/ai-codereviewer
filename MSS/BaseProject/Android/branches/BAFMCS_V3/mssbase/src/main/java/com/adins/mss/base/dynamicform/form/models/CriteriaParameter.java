package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gigin.ginanjar on 11/10/2016.
 */

public class CriteriaParameter implements Serializable {
    @SerializedName("parameters")
    private List<Parameter> parameters;

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }


}
