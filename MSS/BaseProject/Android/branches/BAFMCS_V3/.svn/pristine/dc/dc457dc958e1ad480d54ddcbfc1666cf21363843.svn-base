package com.adins.mss.odr.news;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.tool.Constants;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NewsHeaderTask extends AsyncTask<Void, Void, Integer>{
	private ProgressDialog progressDialog;
	private String errMsg = null;
	private News news;
	List<MobileContentH> tempNews;
	private String messageWait;
    private String messageEmpty;
    private int contentFrame;
    JsonResponseNewsHeader newsHeader;
    String result;
	private ErrorMessageHandler errorMessageHandler;
	NiftyDialogBuilder dialogBuilder;
	private WeakReference<FragmentActivity> activity;
	public NewsHeaderTask(final FragmentActivity mainActivity, String messageWait, String messageEmpty, int contentFrame) {
        this.activity = new WeakReference<FragmentActivity>(mainActivity);
        this.messageWait = messageWait;
        this.messageEmpty = messageEmpty;
        this.contentFrame = contentFrame;
        errorMessageHandler = new ErrorMessageHandler(new IShowError() {
			@Override
			public void showError(String errorSubject, String errorMsg, int notifType) {
				if(notifType == ErrorMessageHandler.DIALOG_TYPE){
					dialogBuilder = NiftyDialogBuilder.getInstance(mainActivity);
					dialogBuilder.withTitle(errorSubject)
							.withMessage(errorMsg)
							.show();
				}
			}
		});
    }
	@Override
    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(activity.get(), "", this.messageWait, true);
    }
	
	@Override
    protected Integer doInBackground(Void... params) {
		int code = 999;
		JsonRequestNews requestType = new JsonRequestNews();
		requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
		requestType.addImeiAndroidIdToUnstructured();
		try {
			news = new News(activity.get());
			tempNews = news.getAllNews();
			if(tempNews==null)
				tempNews = new ArrayList<MobileContentH>();
		} catch (Exception e) {
			FireCrash.log(e);
			// TODO: handle exception
		}



		if(Tool.isInternetconnected(activity.get())){
			String json = GsonHelper.toJson(requestType);
			String url = GlobalData.getSharedGlobalData().getURL_GET_NEWSHEADER();

			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
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
			}
			if (serverResult != null && serverResult.isOK()) {
				result = serverResult.getResult();
				try {
					newsHeader = GsonHelper.fromJson(result, JsonResponseNewsHeader.class);
					if (newsHeader.getStatus().getCode() == 0) {
						List<MobileContentH> listHeaderSVR = newsHeader.getListHeader();
						if (listHeaderSVR != null) {
							MobileContentDDataAccess.clean(activity.get());
							MobileContentHDataAccess.clean(activity.get());
							if (listHeaderSVR.size() > 0) {
								for (MobileContentH contentP : listHeaderSVR) {
									contentP.setUser(GlobalData.getSharedGlobalData().getUser());
									if (contentP.getUuid_parent_content().length() == 0) {
										contentP.setUuid_parent_content(null);
									}
									MobileContentHDataAccess.addOrReplace(activity.get(), contentP);
								}
							}
						}
					}
				} catch (Exception e) {
					FireCrash.log(e);
				}
			} else if (serverResult != null) {
				result = serverResult.getResult();
			} else {
				errMsg = activity.get().getString(R.string.no_promo_try_again);
			}
			try {
				code = newsHeader.getStatus().getCode();
			} catch (Exception e) {
				FireCrash.log(e);
				code=0;
			}
		}else{
			code = 0;
		}
		
		return code;
    }
	
	@Override
    protected void onPostExecute(Integer code) {
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				FireCrash.log(e);
			}
		}
		Bundle bundle = new Bundle();
		if(errMsg!=null){
			bundle.putBoolean(Constants.BUND_KEY_NEWS_ERROR, true);
			bundle.putString(Constants.BUND_KEY_NEWS_ERROR_MESSAGE, errMsg);
		}else {
			
			if(code!=0) {
				bundle.putBoolean(Constants.BUND_KEY_NEWS_ERROR, true);
				if(newsHeader.getStatus().getMessage()!=null)
					bundle.putString(Constants.BUND_KEY_NEWS_ERROR_MESSAGE, newsHeader.getStatus().getMessage());
				else
					bundle.putString(Constants.BUND_KEY_NEWS_ERROR_MESSAGE, String.valueOf(newsHeader.getStatus().getCode()));

			}else{
				bundle.putBoolean(Global.BUND_KEY_SURVEY_ERROR, false);
			}
		}
			Fragment fragment1 = new NewsActivity();
			fragment1.setArguments(bundle);
			
            FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
            transaction.replace(this.contentFrame, fragment1);
            transaction.addToBackStack(null);
            transaction.commit();
		}
//    }
}
