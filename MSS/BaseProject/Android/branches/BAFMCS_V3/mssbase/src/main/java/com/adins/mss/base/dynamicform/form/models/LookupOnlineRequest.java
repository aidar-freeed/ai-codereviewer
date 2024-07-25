package com.adins.mss.base.dynamicform.form.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gigin.ginanjar on 14/10/2016.
 */

public class LookupOnlineRequest extends MssRequestType {
    @SerializedName("refId")
    private String identifier;
    @SerializedName("filter")
    private String choiceFilter;
    @SerializedName("lov_group")
    private String lovGroup;
    @SerializedName("value")
    private String userFilter;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getChoiceFilter() {
        return choiceFilter;
    }

    public void setChoiceFilter(String choiceFilter) {
        this.choiceFilter = choiceFilter;
    }

    public String getLovGroup() {
        return lovGroup;
    }

    public void setLovGroup(String lovGroup) {
        this.lovGroup = lovGroup;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }
}
