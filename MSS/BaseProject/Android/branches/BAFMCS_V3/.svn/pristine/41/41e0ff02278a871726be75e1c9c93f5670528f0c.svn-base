package com.adins.mss.odr.news;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

public class NewsContentTask extends AsyncTask<Void, Void, Boolean>{
	private Activity activity;
	private ProgressDialog progressDialog;
	private String errMsg = null;
	private News news;
	private String messageWait;
    private String messageEmpty;
    private boolean noSubList;
    String uuid_mobile_content_h;
	public NewsContentTask(Activity activity, String messageWait, String messageEmpty, String uuid_mobile_content_h, boolean noSublist) {
        this.activity = activity;
        this.messageWait = messageWait;
        this.messageEmpty = messageEmpty;
        this.uuid_mobile_content_h = uuid_mobile_content_h;
        this.noSubList=noSublist;
    }
	@Override
    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
    }
	
	@Override
    protected Boolean doInBackground(Void... params) {
		String result;
		JsonRequestNews requestType = new JsonRequestNews();
		requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
		requestType.addImeiAndroidIdToUnstructured();
		requestType.setuuid_mobile_content_h(uuid_mobile_content_h);
		String json = GsonHelper.toJson(requestType);
		String url = GlobalData.getSharedGlobalData().getURL_GET_NEWSHEADER();

		boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
		boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
		HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
		HttpConnectionResult serverResult = null;

		//Firebase Performance Trace HTTP Request
		HttpMetric networkMetric =
				FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
		Utility.metricStart(networkMetric, json);
		
		List<MobileContentD> objectDetail = new News(activity).getlistContentOnDate(uuid_mobile_content_h);
		
		try {
			if(objectDetail!=null && objectDetail.size()>0)
				serverResult = httpConn.requestToServer(url, json, Global.SORTCONNECTIONTIMEOUT);
			else
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
			Utility.metricStop(networkMetric, serverResult);
		} catch (Exception e) {
			e.printStackTrace();
			errMsg = e.getMessage();
		}
		if(serverResult.isOK()){
			result = serverResult.getResult();
			JsonResponseNewsContent content = GsonHelper.fromJson(result, JsonResponseNewsContent.class);
			if(content.getStatus().getCode()==0){
				List<MobileContentD> listContent = content.getListMobileContentD();
				news= new News(activity);
				MobileContentH contentH = news.getContent(uuid_mobile_content_h);
				if(listContent!=null || listContent.size()>0){
					for(MobileContentD contentD : listContent){
						contentD.setMobileContentH(contentH);
						MobileContentDDataAccess.addOrReplace(activity, contentD);
					}
				}
				if(listContent.isEmpty()){
					if(MobileContentDDataAccess.getAll(activity, uuid_mobile_content_h).isEmpty()){
						errMsg = activity.getResources().getString(R.string.data_not_found);
					}
				}
			}
			else{
				errMsg=result;
			}
		}
		return serverResult.isOK();
    }
	
	@Override
    protected void onPostExecute(Boolean result) {
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				FireCrash.log(e);
			}
		}
		NiftyDialogBuilder dialogBuilder;
		if(errMsg!=null){
			if(GlobalData.isRequireRelogin()){
				return;
			}
			if(errMsg.equals(activity.getResources().getString(R.string.data_not_found))){
				dialogBuilder = NiftyDialogBuilder.getInstance(activity);
				dialogBuilder.withTitle(activity.getResources().getString(R.string.info_capital)).withIcon(android.R.drawable.ic_dialog_info).withMessage(activity.getString(com.adins.mss.base.R.string.empty_data)).show();
			}else{
				dialogBuilder = NiftyDialogBuilder.getInstance(activity);
				dialogBuilder.withTitle(activity.getResources().getString(R.string.error_capital)).withIcon(android.R.drawable.ic_dialog_alert).withMessage(this.errMsg).show();
			}
		}else {
			Intent intent = new Intent(activity, NewsContentActivity.class);
			intent.putExtra("uuid_taskH", uuid_mobile_content_h);			
			activity.startActivity(intent);
			if(noSubList)
				activity.finish();
		}
    }
}
