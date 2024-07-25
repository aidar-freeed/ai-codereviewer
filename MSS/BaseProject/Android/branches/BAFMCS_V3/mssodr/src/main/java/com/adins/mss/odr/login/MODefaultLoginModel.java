package com.adins.mss.odr.login;

import android.content.Context;
import android.content.Intent;

import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.constant.Global;
import com.adins.mss.odr.ChangeLog;
import com.adins.mss.odr.MOSynchronizeActivity;

public class MODefaultLoginModel extends DefaultLoginModel{

	public MODefaultLoginModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//bong 29 apr 15 - set Global intent to go to synhronize after force change password
		Global.syncIntent = new Intent(getContext(), MOSynchronizeActivity.class);
	}

    @Override
    protected int getBuildNumber() {
    	int c = Global.BUILD_VERSION;//ChangeLog.getChangeLog().get(0).getBuildVersion(); 
    	AboutActivity.setChangeLog(ChangeLog.getChangeLog(getContext()), 1);
        return c;
    }

    @Override
    protected Intent getIntentSynchronize() {
        // TODO Auto-generated method stub
		return new Intent(getContext(), MOSynchronizeActivity.class);
	}

}
