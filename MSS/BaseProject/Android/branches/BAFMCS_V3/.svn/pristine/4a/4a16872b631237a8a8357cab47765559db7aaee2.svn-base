package com.adins.mss.base.loyalti.monthlypointacquisition;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.LoyaltyPointsResponse;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.GeneralParameterDao;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LoyaltyPointsDataSource implements ILoyaltyPointsDataSource {

    private Context context;//use app context
    private Handler handler;

    public LoyaltyPointsDataSource(Application application) {
        this.context = application;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void requestPointsData(LoyaltyPointsRequest reqData, ReqPointsListener listener) {
        if(listener == null)
            return;

        RequestPointsTask requestPointsTask = new RequestPointsTask(reqData,listener);
        Thread thread = new Thread(requestPointsTask);
        thread.start();
    }

    @Override
    public List<GeneralParameter> getJobsGenParam(List<String> jobs) {
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

        GeneralParameterDao dao = DaoOpenHelper.getDaoSession(context).getGeneralParameterDao();
        QueryBuilder qb = dao.queryBuilder();
        qb.where(GeneralParameterDao.Properties.Uuid_user.eq(uuidUser)
                ,GeneralParameterDao.Properties.Gs_value.in(jobs));
        qb.build();
        return qb.list();
    }

    private class RequestPointsTask implements Runnable {

        private LoyaltyPointsRequest request;
        private ReqPointsListener listener;

        public RequestPointsTask(LoyaltyPointsRequest request, ReqPointsListener listener) {
            this.request = request;
            this.listener = listener;
        }

        @Override
        public void run() {
            //request to ws here. wait for ws
            String jsonReq = GsonHelper.toJson(request);
            String url = GlobalData.getSharedGlobalData().getURL_LOYALTY_DETAIL_POINT();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, jsonReq);

            try {
                serverResult = httpConn.requestToServer(url, jsonReq, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            } catch (final Exception e) {
                e.printStackTrace();
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailed(e.getMessage());
                            }
                        }
                );
                return;
            }

            if(serverResult.getStatusCode() != 200){
                final String result = serverResult.getResult();
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailed(result);
                            }
                        }
                );
                return;
            }
            final LoyaltyPointsResponse response = GsonHelper.fromJson(serverResult.getResult(), LoyaltyPointsResponse.class);

            if (response.getStatus().getCode() != 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(response.getStatus().getMessage());
                    }
                });
                return;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(response);
                }
            });
        }
    }

}

