package com.adins.mss.coll.commons;

import android.app.Activity;

import com.adins.mss.base.commons.ViewImpl;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class ViewManager extends ViewImpl implements DepositReportCallback {
    public Activity activity;

    public ViewManager(Activity activity) {
        super(activity);
    }

    @Override
    public void OnFillHeader(int totalTask, int paidTask, int failTask, int visitTask) {}

    @Override
    public void OnFillDetail(HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch) {}

    @Override
    public void OnLoadReconcileData(List<TaskD> reconcileReport, int totalNeedPrint) {}

    @Override
    public void OnError(boolean value) {}

    @Override
    public void OnFinish(boolean value) {}
}
