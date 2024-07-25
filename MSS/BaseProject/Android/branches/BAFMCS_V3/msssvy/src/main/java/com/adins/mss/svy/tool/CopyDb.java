package com.adins.mss.svy.tool;

import android.content.Context;

import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import android.content.SharedPreferences;

public class CopyDb extends DaoOpenHelper {
	
	Context context;
	
	public CopyDb(Context context) {
		super(context);
		this.context = context;
	}
	
	public void copyTable(){

		final String DBDestination = "/data/data/" + context.getPackageName() + "/databases/msmdb";
		try(OutputStream os = new FileOutputStream(DBDestination);
			InputStream is = context.getAssets().open("msmdb")){
//			File file = new File("/data/data/" + context.getPackageName() + "/databases/", "msmdb");
			ObscuredSharedPreferences pref = ObscuredSharedPreferences.getPrefs(context,Authentication.SHARED_PREF, Context.MODE_PRIVATE);
			boolean isDbWrite = pref.getBoolean(Authentication.SHARED_PREF_KEY_DB_SAVED, false);
			if(!isDbWrite){

				byte [] buffer = new byte[1024];
				int length;
				while((length = is.read(buffer)) > 0){
					os.write(buffer, 0, length);
				}
				os.flush();
				if(Global.IS_DEV)
					System.out.println("Copy db from assets success");
				ObscuredSharedPreferences.Editor sharedPrefEditor = pref.edit();
				sharedPrefEditor.putBoolean(Authentication.SHARED_PREF_KEY_DB_SAVED, true);
				sharedPrefEditor.apply();
			} else {
				if(Global.IS_DEV)
					System.out.println("Database msmdb already exist");
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
		
}
