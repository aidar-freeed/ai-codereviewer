package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.ReceiptHistoryRequest;
import com.adins.mss.coll.models.ReceiptHistoryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.ReceiptHistory;
import com.adins.mss.foundation.db.dataaccess.ReceiptHistoryDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;
import java.util.List;

public class ReceiptHistoryApi {

    private final Context context;

    public ReceiptHistoryApi(Context context) {
        this.context = context;
    }

    public ReceiptHistoryResponse request(String agreementNo, String taskId) throws IOException {
        ReceiptHistoryRequest request = new ReceiptHistoryRequest();
        request.setAgreementNo(agreementNo);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_GET_RECEIPT_HISTORY();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;
        // Firebase Performance Trace Network Request
        HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, requestJson);

        try {
            serverResult = httpConn.requestToServer(url, requestJson, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String responseJson = "";
        if (serverResult != null && serverResult.isOK()) {
            try {
                responseJson = serverResult.getResult();
            } catch (Exception e) {
            }
        }

        ReceiptHistoryResponse receiptHistoryResponse = GsonHelper.fromJson(responseJson, ReceiptHistoryResponse.class);
        if (null == receiptHistoryResponse) {
            List<ReceiptHistory> receiptHistoryList = ReceiptHistoryDataAccess.getAllByTask(context, taskId);
            if (null != receiptHistoryList) {
                return null;
            } else {
                return receiptHistoryResponse;
            }
        } else {
            return receiptHistoryResponse;
        }
    }

}
