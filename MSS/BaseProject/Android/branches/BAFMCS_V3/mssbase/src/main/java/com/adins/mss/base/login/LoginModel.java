package com.adins.mss.base.login;

import android.content.Context;

import com.adins.mss.base.commons.ActivityModel;

/**
 * Created by Aditya Purwa on 1/6/2015.
 * Base class for login implementation.
 */
public abstract class LoginModel extends ActivityModel {
    /**
     * Initialize a new instance of context model.
     *
     * @param context The context for the model. Must be an activity.
     */
    public LoginModel(Context context) {
        super(context);
    }

    /**
     * Execute the login command.
     *
     * @return True if succeeded.
     */
    public abstract boolean login();

    /**
     * Execute the exit command.
     *
     * @return True if succeeded.
     */
    public abstract boolean exit();
}
