package com.adins.mss.svy.login;

import android.content.Context;
import android.content.Intent;

import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.constant.Global;
import com.adins.mss.svy.ChangeLog;
import com.adins.mss.svy.MSSynchronizeActivity;


public class MSDefaultLoginModel extends DefaultLoginModel{

	public MSDefaultLoginModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Global.syncIntent = new Intent(getContext(), MSSynchronizeActivity.class);
	}

    @Override
    protected int getBuildNumber() {
    	int c = Global.BUILD_VERSION;//ChangeLog.getChangeLog().get(0).getBuildVersion(); 
    	AboutActivity.setChangeLog(ChangeLog.getChangeLog(getContext()), 2);
        return c;
    }

    @Override
    protected Intent getIntentSynchronize() {
		// TODO Auto-generated method stub
		return new Intent(getContext(), MSSynchronizeActivity.class);
//		return new Intent(getContext(), MSMainMenuActivity.class);
	}

}
