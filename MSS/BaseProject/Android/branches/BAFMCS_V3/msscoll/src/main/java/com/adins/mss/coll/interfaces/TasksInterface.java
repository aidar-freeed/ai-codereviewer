package com.adins.mss.coll.interfaces;

import android.content.Context;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.coll.interfaces.callback.SaveDataInstallmentCallback;
import com.adins.mss.coll.interfaces.callback.TaskReportCallback;
import com.adins.mss.coll.models.InstallmentScheduleResponse;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public interface TasksInterface {
    public void saveDataInstallmentSchedule(Context context, InstallmentScheduleResponse installmentSchedResp, String taskId, SaveDataInstallmentCallback callback);
    public void getDataInstallmentSchedule(String taskId, TaskListener listener);
}
