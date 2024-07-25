/**
 *
 */
package com.adins.mss.foundation.db;

import android.content.Context;
import android.util.Log;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoMaster;
import com.adins.mss.dao.DaoMaster.DevOpenHelper;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import de.greenrobot.dao.database.Database;


/**
 * @author michael.bw
 */
public class DaoOpenHelper {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static Database db;


    public DaoOpenHelper(Context context) {
        try {
            String key = null;
            String storedKey = null;
            ObscuredSharedPreferences sharedPref =
                    ObscuredSharedPreferences.getPrefs(context, "GlobalData", Context.MODE_PRIVATE);
            if (sharedPref.getString("DB_SECRET_KEY", null) == null) {
                key = Tool.generateKey();
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putString("DB_SECRET_KEY", key);
                sharedPrefEditor.commit();
            }

            storedKey = sharedPref.getString("DB_SECRET_KEY", null);
            Log.d("storedkey",storedKey);

            if (Global.IS_DBENCRYPT) {
                DaoMaster.EncryptedDevOpenHelper helper = new DaoMaster.EncryptedDevOpenHelper(context, Global.MSMDB);
                db = helper.getWritableDatabase(storedKey);
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
            } else {
                DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Global.MSMDB);
                db = helper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FireCrash.log(e);
        }
    }

    /**
     * you can use DaoSession.clear(). However, it will clear all all objects from the session.
     * If you want to avoid that, you have to execute a regular query and delete the result entities
     * (for example with deleteInTx).
     */
    public static void clear() {
        if (daoSession != null)
            daoSession.clear();
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            new DaoOpenHelper(context);
        }
        return daoSession;
    }

    public static void closeAll() {
        if (daoSession != null) {
            daoSession.clear();
            db.close();
            daoSession = null;
        }
    }

    public static Database getDb(Context context) {
        if (daoMaster == null) {
            new DaoOpenHelper(context);
        }
        if(daoMaster == null){
            return null;
        }

        return daoMaster.getDatabase();
    }

    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            new DaoOpenHelper(context);
        }
        if(daoMaster == null)
            return null;

        return daoMaster;
    }

}
