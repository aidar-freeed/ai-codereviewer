package com.adins.mss.odr.assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Keep;
import android.view.View;
import android.widget.AdapterView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.model.JsonRequestDetailOrder;
import com.adins.mss.odr.model.JsonResponseServer;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

public class LookupAssignment extends Activity{
	AQuery query ;
	OrderAssignmentAdapter adapter;
	List<ResponseServer> responseServer;
	String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.default_listview);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		query = new AQuery(this);
		query.id(android.R.id.list).itemClicked(this, "itemClick");
		String flag = getIntent().getStringExtra(Global.BUND_KEY_TASK);
		String uuid_task_h = getIntent().getStringExtra(Global.BUND_KEY_UUID_TASKH);
		new GetRequestDataLookup(uuid_task_h,flag).execute();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		Context context = newBase;
		Locale locale;
		try {
			locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
			if (null == locale) {
				locale = new Locale(LocaleHelper.ENGLSIH);
			}
			context = LocaleHelper.wrap(newBase, locale);
		} catch (Exception e) {
			locale = new Locale(LocaleHelper.ENGLSIH);
			context = LocaleHelper.wrap(newBase, locale);
		} finally {
			super.attachBaseContext(context);
		}
	}
	@Keep
	public void itemClick(AdapterView<?> parent, View v, int position, long id){
		Intent intent = new Intent();
		intent.putExtra(Global.BUND_KEY_ASSIGNEE_ID, responseServer.get(position).getKey());
		intent.putExtra(Global.BUND_KEY_ASSIGNEE_VALUE, responseServer.get(position).getValue());
		intent.putExtra(Global.BUND_KEY_ASSIGNEE_JOB, responseServer.get(position).getFlag());
		setResult(Global.REQUEST_CODE_LOOKUP, intent);
		finish();
	}
	private class GetRequestDataLookup extends AsyncTask<Void, Void, JsonResponseServer> {
		private ProgressDialog progressDialog;
		private String errMessage = null;
		private String uuid_task_h;
		private String flag;

		public GetRequestDataLookup(String uuid_task_h, String flag) {
			this.flag = flag;
			this.uuid_task_h=uuid_task_h;
		}
		
		@Override
		protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(LookupAssignment.this,
					"", getString(R.string.progressWait), true);
		}

		@Override
		protected JsonResponseServer doInBackground(Void... params) {
			String result = null;

			JsonRequestDetailOrder request = new JsonRequestDetailOrder();
			request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
			request.setUuid_task_h(uuid_task_h);
			request.setFlag(flag);
			String json = GsonHelper.toJson(request);
			String url = GlobalData.getSharedGlobalData().getURL_GET_LOOKUP();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(getApplicationContext(), encrypt, decrypt);
    		HttpConnectionResult serverResult = null;

			//Firebase Performance Trace HTTP Request
			HttpMetric networkMetric =
					FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
			Utility.metricStart(networkMetric, json);

			try {
				serverResult = httpConn.requestToServer(url, json);
				result = serverResult.getResult();
				Utility.metricStop(networkMetric, serverResult);
			}
			catch (Exception e) {
				e.printStackTrace();
				try {
					progressDialog.dismiss();
				} catch (Exception ex) {
				}
				errMessage = e.getMessage();
			}
			finally {
				
			}
			JsonResponseServer response =null;
			try {
				response = GsonHelper.fromJson(result, JsonResponseServer.class);
			} catch (Exception e) {
				errMessage = e.getMessage();
			}
			return response;
		}
		@Override
		protected void onPostExecute(JsonResponseServer result){
			if (progressDialog.isShowing()){
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
				}
			}
			if(errMessage!=null){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LookupAssignment.this);
				dialogBuilder.withTitle(getString(R.string.error_capital))
					.withMessage(errMessage)
					.show();
			}
			else if(result==null){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LookupAssignment.this);
				dialogBuilder.withTitle(getString(R.string.info_capital))
					.withMessage(errMessage)
					.show();
			}else{	
				if(result.getStatus().getCode()==0){
					responseServer = result.getListResponseServer();
					adapter = new OrderAssignmentAdapter(getApplicationContext(), responseServer, true);
					query.id(android.R.id.list).adapter(adapter);
				}else{
					NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LookupAssignment.this);
					dialogBuilder.withTitle(getString(R.string.info_capital))
						.withMessage(result.getStatus().getMessage())
						.show();
				}
			}
		}
	}
}
