package com.adins.mss.coll.login;

import android.content.Context;
import android.content.Intent;

import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.coll.ChangeLog;
import com.adins.mss.coll.MCSynchronizeActivity;
import com.adins.mss.constant.Global;


public class MCDefaultLoginModel extends DefaultLoginModel{

	public MCDefaultLoginModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//bong 29 apr 15 - set Global intent to go to synhronize after force change password
		Global.syncIntent = new Intent(getContext(), MCSynchronizeActivity.class);
	}

    @Override
    protected int getBuildNumber() {
    	int c = Global.BUILD_VERSION;//ChangeLog.getChangeLog().get(0).getBuildVersion(); 
    	AboutActivity.setChangeLog(ChangeLog.getChangeLog(getContext()), 0);
        return c;
    }

    @Override
    protected Intent getIntentSynchronize() {
        // TODO Auto-generated method stub
        return new Intent(getContext(), MCSynchronizeActivity.class);
	}

}
