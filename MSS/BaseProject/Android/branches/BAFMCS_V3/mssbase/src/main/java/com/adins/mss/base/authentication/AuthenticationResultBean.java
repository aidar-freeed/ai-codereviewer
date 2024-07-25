package com.adins.mss.base.authentication;

import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Menu;
import com.adins.mss.dao.User;

import java.util.List;

public class AuthenticationResultBean {

    private String message;
    private String otaLink;
    private String lastUpdateVersion;
    //	private String job;
    private int loginResult;
    private boolean isActiveUser;
    private boolean forceUpdate;
    private boolean needUpdatePassword;
    private User userInfo;

    private List<GeneralParameter> generalParameters;

    private List<Menu> listMenu;


    public AuthenticationResultBean() {
        // TODO Auto-generated constructor stub
    }


    public List<Menu> getListMenu() {
        return listMenu;
    }


    public void setListMenu(List<Menu> listMenu) {
        this.listMenu = listMenu;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getOtaLink() {
        return otaLink;
    }


    public void setOtaLink(String otaLink) {
        this.otaLink = otaLink;
    }


    public String getLastUpdateVersion() {
        return lastUpdateVersion;
    }


    public void setLastUpdateVersion(String lastUpdateVersion) {
        this.lastUpdateVersion = lastUpdateVersion;
    }


//	public String getJob() {
//		return job;
//	}
//
//
//
//	public void setJob(String job) {
//		this.job = job;
//	}


    public int getLoginResult() {
        return loginResult;
    }


    public void setLoginResult(int loginResult) {
        this.loginResult = loginResult;
    }


    public boolean isActiveUser() {
        return isActiveUser;
    }


    public void setActiveUser(boolean isActiveUser) {
        this.isActiveUser = isActiveUser;
    }


    public boolean isForceUpdate() {
        return forceUpdate;
    }


    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }


    public boolean needUpdatePassword() {
        return needUpdatePassword;
    }


    public void setNeedUpdatePassword(boolean needUpdatePassword) {
        this.needUpdatePassword = needUpdatePassword;
    }


    public List<GeneralParameter> getGeneralParameters() {
        return generalParameters;
    }


    public void setGeneralParameters(List<GeneralParameter> generalParameters) {
        this.generalParameters = generalParameters;
    }


    public User getUserInfo() {
        return userInfo;
    }


    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

}
