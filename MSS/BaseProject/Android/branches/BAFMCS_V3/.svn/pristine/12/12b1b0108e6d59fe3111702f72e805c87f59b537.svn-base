package com.adins.mss.coll.dashboardcollection;

import android.app.Application;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.models.ReportSummaryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.database.Database;

public class DashboardCollDataSource implements IDashboardCollDataSource {

    private Application context;

    private String uuidUser;
    private Database database;
    private Handler handler;

    public DashboardCollDataSource(Application application) {
        context = application;
        handler = new Handler(Looper.getMainLooper());
        database = DaoOpenHelper.getDb(context);
        uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
    }

    private void postToUIThread(Runnable runnable){
        handler.post(runnable);
    }

    public int getTotalOutstandingTask() {
        int total = 0;
        String rawQuery;
        if(!Global.PLAN_TASK_ENABLED){
            rawQuery = "SELECT COUNT(STATUS) FROM TR_TASK_H " +
                    "WHERE UUID_USER = ? "+
                    "AND ( STATUS = 'New' OR STATUS = 'Download')";
        }
        else {
            rawQuery = "SELECT count(taskh.STATUS) FROM TR_TASK_H taskh " +
                    "            inner join tr_plan_task planTask on planTask.UUID_TASK_H = taskh.UUID_TASK_H " +
                    "            WHERE taskh.UUID_USER = ? " +
                    "            AND taskh.STATUS in ('New','Download') " +
                    "            AND planTask.PLAN_STATUS in ('Started','Planned')";

        }

        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return total;

        if(cursor.moveToFirst()){
            total = cursor.getInt(0);
        }
        cursor.close();

        return total;
    }

    public double getOutstandingAmount() {
        double total = 0;
        String rawQuery;
        if(!Global.PLAN_TASK_ENABLED){
            rawQuery = "SELECT sum(tr_task_d.TEXT_ANSWER) FROM (" +
                    "    select *,max(assignment_date) from tr_task_h" +
                    "    group by appl_no" +
                    ") TR_TASK_H " +
                    "                INNER JOIN TR_TASK_D ON TR_TASK_H.UUID_TASK_H = TR_TASK_D.UUID_TASK_H " +
                    "                INNER JOIN MS_QUESTIONSET ON MS_QUESTIONSET.QUESTION_ID = TR_TASK_D.QUESTION_ID " +
                    "                WHERE " +
                    "                TR_TASK_H.UUID_USER = ? "+
                    "                AND tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION" +
                    "                AND tr_task_h.STATUS in ('New','Download')" +
                    "                AND MS_QUESTIONSET.TAG = 'TOTAL TAGIHAN' ";
        }
        else {
            rawQuery = "SELECT sum(tr_task_d.TEXT_ANSWER) FROM (" +
                    "    select *,max(assignment_date) from tr_task_h" +
                    "    group by appl_no" +
                    ") TR_TASK_H " +
                    "                INNER JOIN TR_TASK_D ON TR_TASK_H.UUID_TASK_H = TR_TASK_D.UUID_TASK_H " +
                    "                INNER JOIN TR_PLAN_TASK ON TR_TASK_H.UUID_TASK_H = TR_PLAN_TASK.UUID_TASK_H " +
                    "                INNER JOIN MS_QUESTIONSET ON MS_QUESTIONSET.QUESTION_ID = TR_TASK_D.QUESTION_ID " +
                    "                WHERE " +
                    "                TR_TASK_H.UUID_USER = ? "+
                    "                AND tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION" +
                    "                AND tr_task_h.STATUS in ('New','Download')" +
                    "                AND MS_QUESTIONSET.TAG = 'TOTAL TAGIHAN' " +
                    "                AND TR_PLAN_TASK.PLAN_STATUS in ('Started','Planned')";
        }
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return total;

        if(cursor.moveToFirst()){
            total = cursor.getDouble(0);
        }
        cursor.close();

        return total;
    }

    public double getTotalAmountCollected() {
        double totalCollected = 0;
        String rawQuery = "SELECT sum(tr_task_d.TEXT_ANSWER) FROM (" +
                "       select *,max(assignment_date) from tr_task_h " +
                "       group by appl_no" +
                "    ) TR_TASK_H " +
                "                INNER JOIN TR_TASK_D ON TR_TASK_H.UUID_TASK_H = TR_TASK_D.UUID_TASK_H " +
                "                INNER JOIN MS_QUESTIONSET ON MS_QUESTIONSET.QUESTION_ID = TR_TASK_D.QUESTION_ID " +
                "                WHERE " +
                "                TR_TASK_H.UUID_USER = ? "+
                "                and TR_TASK_H.STATUS = 'Sent' " +
                "                and tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION " +
                "                AND MS_QUESTIONSET.TAG = 'TOTAL BAYAR'                ";
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return totalCollected;

        if(cursor.moveToFirst()){
            totalCollected = cursor.getDouble(0);
        }
        cursor.close();

        return totalCollected;
    }

    public double getTotalAmountToCollect() {
        double collectedAmount = getTotalAmountCollected();
        double toCollectAmount = 0;

        double total = 0;
        String rawQuery = "SELECT sum(tr_task_d.TEXT_ANSWER) FROM (" +
                "    select *,max(assignment_date) from tr_task_h" +
                "    group by appl_no" +
                ") TR_TASK_H " +
                "                INNER JOIN TR_TASK_D ON TR_TASK_H.UUID_TASK_H = TR_TASK_D.UUID_TASK_H " +
                "                INNER JOIN MS_QUESTIONSET ON MS_QUESTIONSET.QUESTION_ID = TR_TASK_D.QUESTION_ID " +
                "                WHERE " +
                "                TR_TASK_H.UUID_USER = ? "+
                "                AND tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION" +
                "                AND MS_QUESTIONSET.TAG = 'TOTAL TAGIHAN'                ";
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return toCollectAmount;

        if(cursor.moveToFirst()){
            total = cursor.getDouble(0);
            toCollectAmount = total - collectedAmount;
        }
        cursor.close();

        return toCollectAmount;
    }

    public List<CollResultDetail> getTaskCollectedDetails(){
        List<CollResultDetail> results = new ArrayList<>();

        String rawQuery = "select tr_task_h.APPL_NO agrNo,tr_task_h.CUSTOMER_NAME custName,tr_task_d.TEXT_ANSWER " +
                "from (" +
                "    select *,max(assignment_date) from tr_task_h" +
                "    group by appl_no" +
                ") tr_task_h " +
                "inner join tr_task_d on tr_task_h.UUID_TASK_H = tr_task_d.UUID_TASK_H " +
                "inner join ms_questionset on ms_questionset.QUESTION_ID = tr_task_d.QUESTION_ID " +
                "where tr_task_h.STATUS = 'Sent' " +
                "   and TR_TASK_H.UUID_USER = ? "+
                "   and tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION " +
                "   and ms_questionset.TAG = 'TOTAL BAYAR' ";
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return results;

        while (cursor.moveToNext() && cursor.getCount() > 0){
            String agrNo = cursor.getString(0);
            String custName = cursor.getString(1);
            String result = cursor.getString(2);
            results.add(new CollResultDetail(CollResultDetail.COLLECTED_TYPE,agrNo,custName,result));
        }
        cursor.close();

        return results;
    }

    public List<CollResultDetail> getTaskPTPDetails(){
        List<CollResultDetail> results = new ArrayList<>();

        String rawQuery = "select tr_task_h.APPL_NO,tr_task_h.CUSTOMER_NAME,tr_task_d.TEXT_ANSWER " +
                "from (" +
                "    select *,max(assignment_date) from tr_task_h" +
                "    group by appl_no" +
                ") tr_task_h " +
                "inner join tr_task_d on tr_task_h.UUID_TASK_H = tr_task_d.UUID_TASK_H " +
                "inner join ms_questionset on ms_questionset.QUESTION_ID = tr_task_d.QUESTION_ID " +
                "where tr_task_h.STATUS = 'Sent' " +
                "   and TR_TASK_H.UUID_USER = ? "+
                "   and tr_task_h.FORM_VERSION = ms_questionset.FORM_VERSION " +
                "   and ms_questionset.TAG = 'PTP DATE' ";
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return results;

        while (cursor.moveToNext() && cursor.getCount() > 0){
            String agrNo = cursor.getString(0);
            String custName = cursor.getString(1);
            String result = cursor.getString(2);
            results.add(new CollResultDetail(CollResultDetail.PTP_TYPE,agrNo,custName,result));
        }
        cursor.close();

        return results;
    }

    public List<CollResultDetail> getTaskFailedDetails(){
        List<CollResultDetail> results = new ArrayList<>();

        String rawQuery = "select th.appl_no,th.customer_name,lov.value " +
                "from (" +
                "    select *,max(assignment_date) from tr_task_h" +
                "    group by appl_no" +
                ") th " +
                "inner join tr_task_d td on th.UUID_TASK_H = td.UUID_TASK_H " +
                "inner join ms_questionset qs on qs.QUESTION_ID = td.QUESTION_ID " +
                "inner join ms_lookup lov on lov.code = td.lov " +
                "where th.STATUS = 'Sent'" +
                "   and th.UUID_USER = ? "+
                "   and th.FORM_VERSION = qs.FORM_VERSION   " +
                "   and qs.TAG = 'COLLECTION RESULT' " +
                "   and td.lov not in ('PAID','PTP','CRSIT01','CRSIT001','CRSIT002') ";
        Cursor cursor = database.rawQuery(rawQuery,new String[]{uuidUser});
        if(cursor == null)
            return results;

        while (cursor.moveToNext() && cursor.getCount() > 0){
            String agrNo = cursor.getString(0);
            String custName = cursor.getString(1);
            String result = cursor.getString(2);
            results.add(new CollResultDetail(CollResultDetail.FAILED_TYPE,agrNo,custName,result));
        }
        cursor.close();

        return results;
    }

    @Override
    public void getTotalOutstandingTaskAsync(final DashboardResultListener<Integer> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final int result = getTotalOutstandingTask();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getOutstandingAmountAsync(final DashboardResultListener<Double> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final double result = getOutstandingAmount();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getTotalAmountCollectedAsync(final DashboardResultListener<Double> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final double result = getTotalAmountCollected();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getTotalAmountToCollectAsync(final DashboardResultListener<Double> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final double result = getTotalAmountToCollect();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getTaskCollectedDetailsAsync(final DashboardResultListener<List<CollResultDetail>> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final List<CollResultDetail> result = getTaskCollectedDetails();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getTaskPTPDetailsAsync(final DashboardResultListener<List<CollResultDetail>> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final List<CollResultDetail> result = getTaskPTPDetails();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void getTaskFailedDetailsAsync(final DashboardResultListener<List<CollResultDetail>> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(listener == null)
                        return;

                    final List<CollResultDetail> result = getTaskFailedDetails();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(result);
                        }
                    });
                }
                catch (final Exception e){
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void requestDashboardData(final DashboardResultListener<ReportSummaryResponse> listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                Gson gson = new Gson();
                MssRequestType request = new MssRequestType();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                String data = gson.toJson(request);

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_REPORTSUMMARY(), FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, data);

                try {
                    final HttpConnectionResult result = httpConn.requestToServer(
                            GlobalData.getSharedGlobalData().getURL_GET_REPORTSUMMARY(),
                            data, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, result);

                    if(result.getStatusCode() != 200){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new Exception(result.getResult()));
                            }
                        });
                        return;
                    }

                    final ReportSummaryResponse response = GsonHelper.fromJson(result.getResult(),ReportSummaryResponse.class);
                    if(response.getStatus().getCode() != 0){
                        postToUIThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new Exception(response.getStatus().getMessage()));
                            }
                        });
                        return;
                    }

                    //success
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResult(response);
                        }
                    });
                }
                catch (final Exception e){
                    e.printStackTrace();
                    postToUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        });
        thread.start();
    }
}
