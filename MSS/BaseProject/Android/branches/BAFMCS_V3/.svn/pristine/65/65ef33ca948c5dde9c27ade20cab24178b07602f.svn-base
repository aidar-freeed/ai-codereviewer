package com.adins.mss.coll.interfaces;

import android.app.Activity;
import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public interface DepositReportInterface {
    public void fillHeader(DepositReportCallback callback);
    public void fillDetail(DepositReportCallback callback);
    public void cleanDepositReportH();
    public void insertPrintItemForDeposit();
    public void generatePrintResultDepositReport(Activity activity, String cashierName, String total, DepositReportH report);
    public void getReportsReconcile(DepositReportCallback callback);
    public DepositReportImpl.SendDepositReportTask sendDepositReport(FragmentActivity activity);
    public double sumOfItems(List<TaskD> items);
    public void getDepositReportH(Activity activity, TaskListener listener);
    public HashMap<DepositReportH, List<DepositReportD>> packListOfBatch(List<DepositReportH> batches);
}
