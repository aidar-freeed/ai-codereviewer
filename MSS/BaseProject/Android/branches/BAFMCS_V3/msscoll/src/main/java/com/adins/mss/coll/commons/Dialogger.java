package com.adins.mss.coll.commons;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class Dialogger {
    public static void error(Context context, Exception ex, String extraReason) {
        new AlertDialog.Builder(context)
          .setTitle("Error")
          .setMessage(ex.getMessage() + "\r\n" + extraReason)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .create()
          .show();
    }
}
