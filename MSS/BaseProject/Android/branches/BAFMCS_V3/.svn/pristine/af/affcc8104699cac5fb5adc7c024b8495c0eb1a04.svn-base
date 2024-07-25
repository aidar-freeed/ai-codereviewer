package com.adins.mss.base.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClearPdfBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ClearPdfSchedulerService.class);
        context.startService(i);
    }
}
