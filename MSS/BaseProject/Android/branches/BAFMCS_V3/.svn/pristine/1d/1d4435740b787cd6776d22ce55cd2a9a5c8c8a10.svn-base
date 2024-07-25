package com.adins.mss.svy.common;

import android.content.Context;
import android.widget.Toast;

import com.adins.mss.svy.R;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class Toaster {

    public static void error(Context context, Exception ex, String extraReason) {
        Toast.makeText(context, context.getString(R.string.error_occure2, ex.getMessage(), extraReason), Toast.LENGTH_LONG)
          .show();
    }

    public static void error(Context context, String extraReason) {
        Toast.makeText(context, context.getString(R.string.error_occure1, extraReason), Toast.LENGTH_LONG)
                .show();
    }
}
