package com.adins.mss.odr.common;

import android.content.Context;
import com.adins.mss.base.GlobalData;

import java.util.Calendar;

/**
 * Created by Aditya Purwa on 3/5/2015.
 */
public class Generator {
    public static String generateBatchId(Context context) {

        Calendar now = Calendar.getInstance();
        return
                GlobalData.getSharedGlobalData().getUser().getLogin_id() +
                        now.get(Calendar.YEAR) +
                        (now.get(Calendar.MONTH) + 1) +
                        now.get(Calendar.DAY_OF_MONTH) +
                        PersistentCounter.getAndIncrement(context, "batchId");

    }
}
