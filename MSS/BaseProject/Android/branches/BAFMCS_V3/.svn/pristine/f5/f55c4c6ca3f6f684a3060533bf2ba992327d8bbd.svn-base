package com.adins.mss.base.authentication;

import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Menu;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class LoginUserResponse extends MssResponseType implements Serializable {
    //	private Map<String, String> listUser = Collections.<String, String> emptyMap();
    @SerializedName("user")
    private User user;
    @SerializedName("listGeneralParameter")
    private List<GeneralParameter> listGeneralParameter = Collections.emptyList();
    @SerializedName("listMenu")
    private List<Menu> listMenu = Collections.emptyList();

//	public Map<String, String> getListUser() {
//		return listUser;
//	}
//	public void setListUser(Map<String, String> listUser) {
//		this.listUser = listUser;
//	}

    public List<Menu> getListMenu() {
        return listMenu;
    }

    public void setListMenu(List<Menu> listMenu) {
        this.listMenu = listMenu;
    }

    public List<GeneralParameter> getListGeneralParameter() {
        return listGeneralParameter;
    }

    public void setListGs(List<GeneralParameter> listGeneralParameter) {
        this.listGeneralParameter = listGeneralParameter;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
