package com.adins.mss.base.authentication;

import com.adins.mss.base.BaseCommunicationModel;


/**
 * JSON Format for authentication
 *
 * @author glen.iglesias
 * @see LoginUserRequest
 * @deprecated as of 17 Dec 2014, as BaseCommunicationModel
 */
public class AuthenticationModel extends BaseCommunicationModel {

    protected String password;
    protected boolean isFreshInstall;

    //	public AuthenticationModel(String userId, String password, boolean useDefault, Object additionalData, boolean freshInstall){
    public AuthenticationModel(String userId, String password, boolean useDefault, boolean freshInstall) {
        super(useDefault);
        this.userId = userId;
        this.password = password;
        this.isFreshInstall = freshInstall;
    }
}
