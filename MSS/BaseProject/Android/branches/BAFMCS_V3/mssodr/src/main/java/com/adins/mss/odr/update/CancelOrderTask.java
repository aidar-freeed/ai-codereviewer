package com.adins.mss.odr.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.JsonResponseSubmitTask;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.common.Toaster;
import com.adins.mss.odr.model.JsonRequestCheckOrder;

import org.apache.http.NameValuePair;

import java.util.List;

public class CancelOrderTask extends AsyncTask<Void, Void, String>{
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private Activity activity;
	List<NameValuePair> params;
	public CancelOrderTask(Activity activity, List<NameValuePair> params){
		this.activity = activity;
		this.params = params;
	}
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity,
				"", activity.getString(R.string.progressWait), true);
	}
	@Override
	protected String doInBackground(Void... params) {
		String result="";
		if(Tool.isInternetconnected(activity)){
			JsonRequestCheckOrder request = new JsonRequestCheckOrder();
			request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
			request.setOrderNumber(this.params.get(0).getValue());
			request.setFlag(this.params.get(1).getValue());
			
			String json = GsonHelper.toJson(request);
			String url = GlobalData.getSharedGlobalData().getURL_GET_CANCELORDER();

			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
			HttpConnectionResult serverResult = null;
			try {
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
				result = serverResult.getResult();
			} catch (Exception e) {
				e.printStackTrace();	
				errMessage = activity.getString(R.string.cancel_order_error);
			}
		}else{
			result = activity.getString(R.string.no_internet_connection);
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result){
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
			}
		}
		if(!GlobalData.isRequireRelogin()){
			if(errMessage!=null){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
				dialogBuilder.withTitle("Error")
					.withMessage(errMessage)
					.show();
			}else {
				if (result.equals(activity.getString(R.string.no_internet_connection))) {
					Toaster.warning(activity, result);
				} else {
					JsonResponseSubmitTask response = GsonHelper.fromJson(result, JsonResponseSubmitTask.class);
					int code = response.getStatus().getCode();
					if (code == 0) {
						if (response.getResult().contains("Success")) {
							NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
							dialogBuilder.withTitle(activity.getString(R.string.success))
									.withMessage(activity.getString(R.string.order_canceled))
									.isCancelable(false)
									.withButton1Text(activity.getString(R.string.btnOk))
									.setButton1Click(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											activity.finish();
										}
									})
									.show();
						}
					} else {
						NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
						dialogBuilder.withTitle("Error")
								.withMessage(result)
								.show();
					}
				}
			}
		} else {
			DialogManager.showForceExitAlert(activity, activity.getString(com.adins.mss.base.R.string.msgLogout));
		}
	}
}
