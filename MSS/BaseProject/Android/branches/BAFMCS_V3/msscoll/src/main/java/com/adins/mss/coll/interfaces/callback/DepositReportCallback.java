package com.adins.mss.coll.interfaces.callback;

import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public interface DepositReportCallback {
    public void OnFillHeader(int totalTask, int paidTask, int failTask, int visitTask);
    public void OnFillDetail(HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch);
    public void OnLoadReconcileData(List<TaskD> reconcileReport, int totalNeedPrint);
    public void OnError(boolean value);
    public void OnFinish(boolean value);
}
