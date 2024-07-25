package com.adins.mss.svy.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.svy.R;
import com.adins.mss.svy.reassignment.JsonRequestDetailOrder;
import com.adins.mss.svy.reassignment.JsonResponseServer;
import com.adins.mss.svy.reassignment.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class LookupAssignment  extends AppCompatActivity{
	AQuery query ;
	OrderAssignmentAdapter adapter;
	List<ResponseServer> responseServer;
	ListView listView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon();

		if(Global.ENABLE_USER_HELP &&
				(Global.userHelpGuide.get(LookupAssignment.this.getClass().getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(LookupAssignment.this.getClass().getSimpleName()) != null){
			menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
		}
        return super.onPrepareOptionsMenu(menu);
    }

    private static Menu mainMenu;

    public static void updateMenuIcon() {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
			if (Global.LTM.getIsConnected()) {
				Global.LTM.removeLocationListener();
				Global.LTM.connectLocationClient();
			} else {
				CheckInManager.startGPSTracking(getApplicationContext());
			}
			Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
			findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
        }
		if(id==R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
			UserHelp.reloadUserHelp(getApplicationContext(), LookupAssignment.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(LookupAssignment.this, LookupAssignment.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.default_listview);

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        toolbar.setTitle(getString(R.string.title_mn_surveyassign));
        setSupportActionBar(toolbar);
		listView = findViewById(android.R.id.list);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		query = new AQuery(this);		
		query.id(android.R.id.list).itemClicked(this, "itemClick").margin(5, 5, 5, 5);
		String flag = getIntent().getStringExtra(Global.BUND_KEY_TASK);
		String uuid_task_h = getIntent().getStringExtra(Global.BUND_KEY_UUID_TASKH);
		new GetRequestDataLookup(uuid_task_h,flag).execute();
	}

	@Override
	public void onBackPressed() {

		if(!Global.BACKPRESS_RESTRICTION){
			super.onBackPressed();
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		Context context = newBase;
		Locale locale;
		try{
			locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
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
			HttpCryptedConnection httpConn = new HttpCryptedConnection(LookupAssignment.this, encrypt, decrypt);
    		HttpConnectionResult serverResult = null;

			//Firebase Performance Trace HTTP Request
			HttpMetric networkMetric =
					FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
			Utility.metricStart(networkMetric, json);

			try {
				serverResult = httpConn.requestToServer(url, json,  Global.DEFAULTCONNECTIONTIMEOUT);
				Utility.metricStop(networkMetric, serverResult);
				if(serverResult.isOK())
					result = serverResult.getResult();
			} catch (Exception e) {
				FireCrash.log(e);
				e.printStackTrace();
				try {
					progressDialog.dismiss();
				} catch (Exception ex) {
					FireCrash.log(e);
				}
				errMessage = e.getMessage();
			}

			JsonResponseServer response =null;
			try {
				if(null!=result)
					response = GsonHelper.fromJson(result, JsonResponseServer.class);
			} catch (Exception e) {             FireCrash.log(e);
				errMessage = e.getMessage();
			}
			return response;
		}
		@Override
		protected void onPostExecute(JsonResponseServer result){
			if (progressDialog.isShowing()){
				try {
					progressDialog.dismiss();
				} catch (Exception e) {             FireCrash.log(e);
				}
			}
			if(errMessage!=null){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LookupAssignment.this);
				dialogBuilder.withTitle(getApplicationContext().getString(R.string.error_capital))
					.withMessage(errMessage)
					.show();
			}
			else if(result != null){
				if(result.getStatus().getCode()==0){
					responseServer = result.getListResponseServer();
					adapter = new OrderAssignmentAdapter(getApplicationContext(), responseServer, true);
					query.id(android.R.id.list).adapter(adapter);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							UserHelp.showAllUserHelpWithListView(LookupAssignment.this,
									LookupAssignment.this.getClass().getSimpleName(),
									listView,0);
						}
					}, SHOW_USERHELP_DELAY_DEFAULT);
				}else{
					NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LookupAssignment.this);
					dialogBuilder.withTitle(getApplicationContext().getString(R.string.info_capital))
						.withMessage(result.getStatus().getMessage())
						.show();
				}
			}
		}
	}
}
