package com.adins.mss.base.authentication;

import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonLogin extends MssResponseType {

    @SerializedName("user")
    private User user;
    @SerializedName("listGeneralParameter")
    private List<GeneralParameter> listGeneralParameter;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GeneralParameter> getListGeneralParameter() {
        return listGeneralParameter;
    }

    public void setListGeneralParameter(List<GeneralParameter> listGeneralParameter) {
        this.listGeneralParameter = listGeneralParameter;
    }

}
