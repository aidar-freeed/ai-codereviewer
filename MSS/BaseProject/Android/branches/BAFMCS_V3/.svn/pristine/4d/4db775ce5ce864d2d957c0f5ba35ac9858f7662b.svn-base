package com.adins.mss.odr.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class Toaster {

    public static void error(Context context, Exception ex, String extraReason) {
        Toast.makeText(context, "Error occurred: " + ex.getMessage() + " - " + extraReason, Toast.LENGTH_LONG)
          .show();
    }

    public static void warning(Context context, String reason) {
        Toast.makeText(context, reason, Toast.LENGTH_LONG)
                .show();
    }
}
