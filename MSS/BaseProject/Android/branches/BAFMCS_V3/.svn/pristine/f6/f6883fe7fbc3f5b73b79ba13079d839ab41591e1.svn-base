package com.adins.mss.coll.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.dummy.MyDashboardItemDummyAdapter;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.MonthlyPointsChartView;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Kompetisi;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class GetDetailListKompetisi extends AsyncTask<Void, Void, String> {
    BeanResps beanResps;
    private Activity context;
    private ProgressDialog progressDialog;
    public DetailKompetisiResponse dataDetail;
    String errMsg = "";
    RecyclerView recyclerViewDashBoard;
    MyDashBoardItemRecyclerViewAdapter adapter;
    String className;


//    public GetDetailListKompetisi(RecyclerView recyclerView, MyDashBoardItemRecyclerViewAdapter adapter, FragmentActivity activity, BeanResps beanRespsData) {
//        recyclerViewDashBoard = recyclerView;
//        context = activity;
//        beanResps = beanRespsData;
//        this.adapter = adapter;
//    }

    public GetDetailListKompetisi(RecyclerView recyclerView, MyDashBoardItemRecyclerViewAdapter adapter, FragmentActivity activity) {
        recyclerViewDashBoard = recyclerView;
        context = activity;
        this.adapter = adapter;
    }

    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.please_wait_dialog), true);

        }

        @Override
        protected String doInBackground(Void... voids) {
            if (Tool.isInternetconnected(context)) {

                Gson gson = new GsonBuilder().setDateFormat("ddMMyyyyHHmmss").registerTypeHierarchyAdapter(byte[].class,
                        new GsonHelper.ByteArrayToBase64TypeAdapter()).create();
                String result;
                DetailKompetisiRequest requestType = new DetailKompetisiRequest();
//                ArrayList<String> data = null;
//                data = new ArrayList<>();
//                for(int i = 0 ; i < dataKompetisireq.size() ; i++){
//                    data.add(dataKompetisireq.get(i).getMembershipProgramCode());
//                }

                String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();

                if(loginId.contains("@")){
                    String[] separated = loginId.split("@");
                    requestType.setLOGIN_ID(separated[0]);
                }else {
                    requestType.setLOGIN_ID(loginId);
                }

                requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
//                    requestType.addItemToUnstructured(new KeyValue("imei", GlobalData.getSharedGlobalData().getImei()), false);

                String json = GsonHelper.toJson(requestType);
                String url = GlobalData.getSharedGlobalData().getURL_GET_DETAILKOMPETISI();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    return errMsg;
                }

                if(serverResult.getStatusCode() == 200){
                    try {
                        dataDetail = gson.fromJson(serverResult.getResult(), DetailKompetisiResponse.class);
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                }else {
                    errMsg = context.getString(R.string.failed_get_data_try_again);
                }

                return errMsg;
            } else {
                errMsg = context.getString(R.string.no_internet_connection);
                return errMsg;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (errMsg.length() > 0) {
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            if(dataDetail == null || dataDetail.getResultList() == null
                    || dataDetail.getResultList().isEmpty() || dataDetail.getResultList().get(0).getTEAM_MEMBER() == null){
                Toast.makeText(context, context.getString(R.string.failed_get_data_try_again), Toast.LENGTH_SHORT).show();
                return;
            }

            adapter.setDataDetailKompetisi(dataDetail);
            if (Global.userHelpDummyGuide.get(className) !=null &&
                    Global.userHelpDummyGuide.size()>0 &&
                    Global.userHelpDummyGuide.get(className).size()>0 &&
                    Global.ENABLE_USER_HELP) {
                MyDashboardItemDummyAdapter dummyAdapter = new MyDashboardItemDummyAdapter(context,recyclerViewDashBoard,adapter);
                recyclerViewDashBoard.setAdapter(dummyAdapter);
            }else {
                adapter.notifyDataSetChanged();
                recyclerViewDashBoard.setAdapter(adapter);
            }
        }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
