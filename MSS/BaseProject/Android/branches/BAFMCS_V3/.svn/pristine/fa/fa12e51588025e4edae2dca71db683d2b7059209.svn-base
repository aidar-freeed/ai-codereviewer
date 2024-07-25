package com.adins.mss.svy.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.svy.reassignment.JsonRequestCheckOrder;
import com.adins.mss.svy.reassignment.JsonResponseServer;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

public class OrderAssignmentTask extends AsyncTask<Void, Void, JsonResponseServer> {
	private ProgressDialog progressDialog;
	private Context context;
	private String messageWait;
	private String messageEmpty;
	private String errMessage = null;
	private FragmentActivity fragmentActivity;
	private int contentFrame;
	public OrderAssignmentTask(FragmentActivity fragmentActivity, String messageWait, String messageEmpty,int contentFrame){
		this.context=fragmentActivity;
		this.fragmentActivity = fragmentActivity;
		this.messageWait = messageWait;
		this.messageEmpty = messageEmpty;
		this.contentFrame = contentFrame;
	}
	
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(context, "", messageWait, true);
	}
	
	@Override
	protected JsonResponseServer doInBackground(Void... params) {
		String resp = null;
		JsonResponseServer result = null;
		if(Tool.isInternetconnected(context)){
			JsonRequestCheckOrder requestCheckOrder = new JsonRequestCheckOrder();
	        requestCheckOrder.setAudit(GlobalData.getSharedGlobalData().getAuditData());
	        requestCheckOrder.setFlag(Global.FLAG_FOR_ORDERASSIGNMENT);
	        String json = GsonHelper.toJson(requestCheckOrder);
	        String url = GlobalData.getSharedGlobalData().getURL_GET_LIST_ASSIGNMENT();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(fragmentActivity, encrypt, decrypt);
			HttpConnectionResult serverResult = null;

			//Firebase Performance Trace HTTP Request
			HttpMetric networkMetric =
					FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
			Utility.metricStart(networkMetric, json);

			try {
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
				Utility.metricStop(networkMetric, serverResult);
			} catch (Exception e) {
				FireCrash.log(e);
				e.printStackTrace();
				errMessage=e.getMessage();
			}
			
	        resp = serverResult.getResult();
			
			try {
				result = GsonHelper.fromJson(resp, JsonResponseServer.class);
				if(result.getStatus().getCode()!=0){
					errMessage= result.getStatus().getMessage();
				}
			} catch (Exception e) {
				FireCrash.log(e);
				errMessage = context.getString(R.string.jsonParseFailed);
			}
		}else{
			errMessage = context.getString(R.string.no_internet_connection);
		}
		return result;
	}

	@Override
	protected void onPostExecute(JsonResponseServer result) {
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				FireCrash.log(e);
			}
		}
		if (GlobalData.isRequireRelogin()) {

		} else if (errMessage != null) {
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(fragmentActivity);
			dialogBuilder.withTitle(fragmentActivity.getString(R.string.error_capital))
				.withMessage(errMessage)
				.show();
		}
		else if (result == null){
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(fragmentActivity);
			dialogBuilder.withTitle(fragmentActivity.getString(R.string.info_capital))
				.withMessage(messageEmpty)
				.show();
		}
		else{
			Bundle bundle = new Bundle();
			bundle.putSerializable("resultJson",result);
			Fragment fragment = new OrderAssignmentActivity();
			fragment.setArguments(bundle);
			
			FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
			transaction.replace(R.id.content_frame, fragment);
			transaction.addToBackStack(null);
		    transaction.commit();
		}
	}
}