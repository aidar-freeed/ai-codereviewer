package com.adins.mss.base.crashlytics;

import android.text.format.DateFormat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.User;
import com.adins.mss.logger.Logger;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Calendar;

/**
 * Created by ahmadkamilalmasyhur on 19/02/2018.
 */

public class FireCrash {

    private static FirebaseCrashlytics instance;

    public static void setInstance(FirebaseCrashlytics instance) {
        FireCrash.instance = instance;
    }

    //info dont use final StackTraceElement[] ste = Thread.currentThread().getStackTrace(); to get method name or classname or others
    //https://stackoverflow.com/questions/421280/how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection

    //Format Catch Error sebisa mungkin disamakan catch(exception e) lalu jika ada kemungkinan sama maka catch(exception e2) dst
    public static void log(Throwable e) {
        if(instance == null)
            return;

        String date = DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString();
        User user = GlobalData.getSharedGlobalData().getUser();
        if (null != user) {
            instance.log(date +
                    ":: loginId : " + user.getLogin_id() +
                    ":: fullName : " + user.getFullname() +
                    ":: branchId : " + user.getBranch_id() +
                    ":: branchName : " + user.getBranch_name() +
                    ":: branchAddress : " + user.getBranch_address() +
                    ":: flagJob : " + user.getFlag_job() +
                    ":: jobDescr : " + user.getJob_description() +
                    ":: dealerName : " + user.getDealer_name() +
                    ":: uuidUser :  " + user.getUuid_user()
            );
            instance.setCustomKey("loginId", user.getLogin_id());
            instance.setCustomKey("fullName", user.getFullname());
            instance.setCustomKey("branchId", user.getBranch_id());
            instance.setCustomKey("branchName", user.getBranch_name());
            instance.setCustomKey("branchAddress", user.getBranch_address());
            instance.setCustomKey("flagJob", user.getFlag_job());
            instance.setCustomKey("jobDescr", user.getJob_description());
            instance.setCustomKey("dealerName", user.getDealer_name());
            instance.setCustomKey("uuidUser", user.getUuid_user());
            instance.setCustomKey("uuidBranch", user.getBranch_id());
        } else {
            instance.log("User : Not Found");
            instance.setCustomKey("loginId", "Tidak Ada Data");
            instance.setCustomKey("fullName", "Tidak Ada Data");
        }
        instance.recordException(e);
    }

    public static void log(Exception e) {
        if(instance == null)
            return;

        User user = GlobalData.getSharedGlobalData().getUser();
        if (null != user) {
            instance.setCustomKey("loginId", user.getLogin_id());
            instance.setCustomKey("fullName", user.getFullname());
            instance.setCustomKey("branchId", user.getBranch_id());
            instance.setCustomKey("branchName", user.getBranch_name());
            instance.setCustomKey("branchAddress", user.getBranch_address());
            instance.setCustomKey("flagJob", user.getFlag_job());
            instance.setCustomKey("jobDescr", user.getJob_description());
            instance.setCustomKey("dealerName", user.getDealer_name());
            instance.setCustomKey("uuidUser", user.getUuid_user());
        } else {
            instance.log("User : Not Found");
            instance.setCustomKey("loginId", "-");
            instance.setCustomKey("fullName", "-");
        }
        instance.recordException(e);
    }

    public static void log(Exception e, String message) {
        if(instance == null)
            return;

        User user = GlobalData.getSharedGlobalData().getUser();
        if (null != user) {
            instance.setCustomKey("loginId", user.getLogin_id());
            instance.setCustomKey("fullName", user.getFullname());
            instance.setCustomKey("branchId", user.getBranch_id());
            instance.setCustomKey("branchName", user.getBranch_name());
            instance.setCustomKey("branchAddress", user.getBranch_address());
            instance.setCustomKey("flagJob", user.getFlag_job());
            instance.setCustomKey("jobDescr", user.getJob_description());
            instance.setCustomKey("dealerName", user.getDealer_name());
            instance.setCustomKey("uuidUser", user.getUuid_user());
            instance.setCustomKey("message", message);
        } else {
            instance.log("User : Not Found");
            instance.setCustomKey("loginId", "-");
            instance.setCustomKey("fullName", "-");
        }
        instance.recordException(e);
        Logger.printStackTrace(e);
    }

}
