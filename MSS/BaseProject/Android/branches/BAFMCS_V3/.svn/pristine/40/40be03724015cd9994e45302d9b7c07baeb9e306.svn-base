package com.adins.mss.coll.commons;

import android.content.Context;
import android.widget.Toast;

import com.adins.mss.coll.R;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class Toaster {

    public static void error(Context context, Exception ex, String extraReason) {
        Toast.makeText(context, context.getString(R.string.error_occure2, ex.getMessage(), extraReason), Toast.LENGTH_LONG)
          .show();
    }

    public static void warning(Context context, String reason) {
        Toast.makeText(context, context.getString(R.string.error_occure1,reason), Toast.LENGTH_LONG)
                .show();
    }
}
