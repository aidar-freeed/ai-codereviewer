package com.adins.mss.svy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.dynamicform.form.FragmentQuestion;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.svy.models.ActionOnVerification;
import com.adins.mss.svy.models.ActionOnVerification.ListAction;
import com.adins.mss.svy.models.JsonGetVerificationActionRequest;
import com.adins.mss.svy.models.JsonRequestRejectedVerificationTask;
import com.adins.mss.svy.models.JsonRequestUserList;
import com.adins.mss.svy.models.RequestRejectedWithResurvey;
import com.adins.mss.svy.models.SuggestOnVerification;
import com.adins.mss.svy.models.SuggestOnVerification.ListUser;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class SurveyVerificationActionActivity extends AppCompatActivity implements
		OnItemSelectedListener, OnCheckedChangeListener, OnClickListener {

	protected AppCompatSpinner spinnerAction;
	protected RadioGroup radioAction;
	protected ImageButton btnBack;
	protected ImageButton btnSubmit;
	protected ImageButton btnSave;
	protected EditText txtNotes;
	protected LinearLayout surveyorList;

	protected List<ListAction> actionList;
	protected List<ListUser> suggestionList;
	protected ListUser selectedChoice;
	protected String actionFlag = null;
	protected String ApplicationFlag = "";

	protected static final String Flag_4_Verify = "Flag_4_Verify";
	protected static final String Flag_4_Reject = "Flag_4_Reject";
	protected static final String Flag_4_RejectWithResurvey = "Flag_4_RejectWithResurvey";
	private String Flag_4_ChooseOne = "Flag_4_ChooseOne";

	public static final String STATUS_TASK_NOT_MAPPING = "5002";
	public static final String STATUS_TASK_DELETED = "2051";
	public static final String STATUS_IMEI_FAILED = "8103";
	public static final String STATUS_TASKD_MISSING = "4040";
	public static final String STATUS_SAVE_FAILED = "7878";

	private int mode;
	private String uuidTaskH;

	private FirebaseAnalytics screenName;

	protected LayoutParams defLayout = new LayoutParams(
			MATCH_PARENT, WRAP_CONTENT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screenName = FirebaseAnalytics.getInstance(this);
		setContentView(R.layout.new_survey_verification_action_activity);
		spinnerAction = (AppCompatSpinner) findViewById(R.id.spinnerAction);
		spinnerAction.setOnItemSelectedListener(this);

		radioAction = (RadioGroup) findViewById(R.id.radioAction);
		radioAction.removeAllViews();
		radioAction.setVisibility(View.GONE);
		radioAction.setOnCheckedChangeListener(this);

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnSubmit = (ImageButton) findViewById(R.id.btnSend);
		btnSave = (ImageButton) findViewById(R.id.btnSave);
		btnBack.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		LinearLayout saveLayout = this.findViewById(com.adins.mss.base.R.id.btnSaveLayout);

		if (TaskHDataAccess.STATUS_TASK_VERIFICATION.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus()) ||
				TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equalsIgnoreCase(DynamicFormActivity.getHeader().getStatus())) {
			saveLayout.setVisibility(View.VISIBLE);
		}

		txtNotes = (EditText) findViewById(R.id.txtNotes);
		surveyorList = (LinearLayout) findViewById(R.id.surveyorLayout);

		mode = getIntent().getIntExtra(Global.BUND_KEY_MODE_SURVEY, 0);
		ApplicationFlag = getIntent().getStringExtra(Global.BUND_KEY_FORM_NAME);
		uuidTaskH = getIntent().getStringExtra(Global.BUND_KEY_UUID_TASKH);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
		toolbar.setTitle(getString(R.string.title_mn_surveyverification));
		if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
			toolbar.setTitle(getString(R.string.title_mn_surveyapproval));
		}
		setSupportActionBar(toolbar);
		new GetListAction(this).execute(uuidTaskH);
	}

	@Override
	protected void onResume() {
		super.onResume();

		//Set Firebase screen name
		screenName.setCurrentScreen(this, getString(R.string.screen_name_verification_activity), null);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(Global.ENABLE_USER_HELP &&
				(Global.userHelpGuide.get(SurveyVerificationActionActivity.class.getSimpleName())!=null) ||
				Global.userHelpDummyGuide.get(SurveyVerificationActionActivity.class.getSimpleName()) != null){
			menu.findItem(R.id.mnGuide).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
			if (Global.LTM.getIsConnected()) {
				Global.LTM.removeLocationListener();
				Global.LTM.connectLocationClient();
			} else {
				CheckInManager.startGPSTracking(getApplicationContext());
			}
			Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
			findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
		}
		if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
			UserHelp.reloadUserHelp(getApplicationContext(), SurveyVerificationActionActivity.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					UserHelp.showAllUserHelp(SurveyVerificationActionActivity.this, SurveyVerificationActionActivity.this.getClass().getSimpleName());
				}
			}, SHOW_USERHELP_DELAY_DEFAULT);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
							   long id) {
		ListAction item = (ListAction) parent.getItemAtPosition(position);
		btnSubmit.setEnabled(true);
		if (item.getStatus_code().equals("RS")) {
			actionFlag = Flag_4_RejectWithResurvey;
			surveyorList.setVisibility(View.VISIBLE);
			new GetListUser().execute();
		} else if (item.getStatus_code().equals("R")) {
			actionFlag = Flag_4_Reject;
			surveyorList.setVisibility(View.GONE);
			radioAction.removeAllViews();
		} else if (item.getStatus_code().equals("P") || item.getStatus_code().equals("S")) {
			actionFlag = Flag_4_Verify;
			surveyorList.setVisibility(View.GONE);
			radioAction.removeAllViews();
		} else{
			actionFlag = Flag_4_ChooseOne;
			surveyorList.setVisibility(View.GONE);
			radioAction.removeAllViews();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		//EMPTY
	}

	private class GetListAction extends
			AsyncTask<String, Void, List<ListAction>> {
		private String errMessage;
		private ProgressDialog progressDialog;
		private Context context;
		private int errCode;

		GetListAction(Context context){
			this.context = context;
		}

		@Override
		protected List<ListAction> doInBackground(String... params) {
			List<ListAction> result = null;
			String uuidTaskH = params[0];

			if (Tool.isInternetconnected(getApplicationContext())) {
				JsonGetVerificationActionRequest requestType = new JsonGetVerificationActionRequest();
				requestType.setUuid_task_h(uuidTaskH);
				requestType.setAudit(GlobalData.getSharedGlobalData()
						.getAuditData());
				requestType.addImeiAndroidIdToUnstructured();

				String json = GsonHelper.toJson(requestType);
				String url = GlobalData.getSharedGlobalData()
						.getURL_GET_LIST_VERIFICATION();
				boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
				boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
				HttpCryptedConnection httpConn = new HttpCryptedConnection(SurveyVerificationActionActivity.this, encrypt, decrypt);
				HttpConnectionResult serverResult = null;

				//Firebase Performance Trace HTTP Request
				HttpMetric networkMetric =
						FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
				Utility.metricStart(networkMetric, json);

				try {
					serverResult = httpConn.requestToServer(url, json,
							Global.DEFAULTCONNECTIONTIMEOUT);
					Utility.metricStop(networkMetric, serverResult);
				} catch (Exception e) {
					FireCrash.log(e);
					e.printStackTrace();
					errMessage = e.getMessage();
				}
				if(serverResult != null && serverResult.isOK()) {
					String response = serverResult.getResult();

					try {
						ActionOnVerification taskList = GsonHelper.fromJson(response,
								ActionOnVerification.class);
						if (taskList.getStatus().getCode() == 0) {
							result = taskList.getListAction();
						} else {
							if (taskList.getStatus().getCode() == 1001) {
								errMessage = context.getString(R.string.taskChanged);
								TaskH taskH = TaskHDataAccess.getOneHeader(context, uuidTaskH);
								//set taskh status and messages to changed
								if (taskH != null && !taskH.getStatus().equals(TaskHDataAccess.STATUS_TASK_CHANGED)) {
									taskH.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
									TaskHDataAccess.addOrReplace(context, taskH);
									if (ApplicationFlag.equals(Global.APPROVAL_FLAG)) {
										taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
									} else if (ApplicationFlag.equals(Global.VERIFICATION_FLAG)) {
										taskH.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
									}
									taskH.setMessage(errMessage);
									TimelineManager.insertTimeline(context, taskH);
								}
							} else if (taskList.getStatus().getCode() == Global.FAILED_DRAFT_TASK_CODE) {
								errCode = Global.FAILED_DRAFT_TASK_CODE;
								errMessage = taskList.getStatus().getMessage();
							} else {
								errMessage = response;
							}
						}
					} catch (Exception e) {
						FireCrash.log(e);
						errMessage = e.getMessage();
					}
				}
			} else {
				errMessage = getString(R.string.no_internet_connection);
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<ListAction> result) {
			if (progressDialog.isShowing()) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					FireCrash.log(e);
				}
			}
			if (errMessage != null && errMessage.length() > 0) {
				if(GlobalData.isRequireRelogin()){
					return;
				}

				if(errMessage.equals(context.getString(R.string.taskChanged))){
					final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
					dialogBuilder.withTitle("Approval")
							.withMessage(errMessage)
							.withButton1Text("OK")
							.setButton1Click(new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									finishActivity();
									dialogBuilder.dismiss();
								}
							})
							.isCancelable(false)
							.isCancelableOnTouchOutside(false)
							.show();
				} else if(errCode == Global.FAILED_DRAFT_TASK_CODE) {
					//Set Choose One in Survey Verif Action List
					List<ActionOnVerification.ListAction> resultTemp = new ArrayList<>();
					ActionOnVerification.ListAction laTemp = new ActionOnVerification().new ListAction();
					laTemp.setStatus_desc(getString(R.string.chooseOne));
					laTemp.setUuid_status_task("");
					laTemp.setStatus_code("");
					resultTemp.add(laTemp);

					ArrayAdapter<ListAction> spAdapter = new ArrayAdapter<>(
							getApplicationContext(), R.layout.spinner_style,
							resultTemp);
					spAdapter.setDropDownViewResource(R.layout.spinner_style);
					spinnerAction.setAdapter(spAdapter);

					final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
					dialogBuilder.withTitle(getString(R.string.workflowByForm))
                            .withMessage(errMessage)
                            .withButton1Text("OK")
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    if (ApplicationFlag.equals(Global.VERIFICATION_FLAG)) {
										Message message = new Message();
										Bundle bundle = new Bundle();
										bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
										bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SAVE_QUESTION);
										message.setData(bundle);
										FragmentQuestion.questionHandler.sendMessage(message);
										finish();
									}
                                    dialogBuilder.dismiss();
                                }
                            })
                            .isCancelable(false)
                            .isCancelableOnTouchOutside(false)
                            .show();
				}
			} else {
				if (result != null && !result.isEmpty()) {
					//add choose one to result for default option
					ActionOnVerification.ListAction laTemp = new ActionOnVerification().new ListAction();
					laTemp.setStatus_desc(getString(R.string.chooseOne));
					laTemp.setUuid_status_task("");
					laTemp.setStatus_code("");
					result.add(0,laTemp);

					ArrayAdapter<ListAction> spAdapter = new ArrayAdapter<>(
							getApplicationContext(), R.layout.spinner_style,
							result);
					spAdapter.setDropDownViewResource(R.layout.spinner_style);
					spinnerAction.setAdapter(spAdapter);
					Handler handler = new Handler(Looper.getMainLooper());
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							UserHelp.showAllUserHelp(SurveyVerificationActionActivity.this,SurveyVerificationActionActivity.this.getClass().getSimpleName());
						}
					}, SHOW_USERHELP_DELAY_DEFAULT);
				}else{
					txtNotes.setVisibility(View.GONE);
					((TextView)findViewById(R.id.textView2)).setText(getString(R.string.action_not_available));
					findViewById(R.id.lblNotes).setVisibility(View.GONE);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					SurveyVerificationActionActivity.this, "",
					"Loading Action...", true);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			//EMPTY
		}
	}

	public void finishActivity() {
		Intent intent = new Intent(this, NewMainActivity.getMainMenuClass());
		intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
		startActivity(intent);
		finish();
	}

	private class GetListUser extends AsyncTask<String, Void, List<ListUser>> {

		private ProgressDialog progressDialog;
		private String errMessage;

		@Override
		protected List<ListUser> doInBackground(String... params) {
			List<ListUser> result = null;
			if (Tool.isInternetconnected(getApplicationContext())) {
				JsonRequestUserList request = new JsonRequestUserList();
				request.setUuid_task_h(uuidTaskH);
				if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
					if(Global.APPROVAL_BRANCH)
						request.setAccess_mode("branch");
					else
						request.setAccess_mode("user");
				} else {
					if(Global.VERIFICATION_BRANCH)
						request.setAccess_mode("branch");
					else
						request.setAccess_mode("user");
				}
				request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
				request.addImeiAndroidIdToUnstructured();

				String json = GsonHelper.toJson(request);
				String url = GlobalData.getSharedGlobalData()
						.getURL_GET_LIST_USER();
				boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
				boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
				HttpCryptedConnection httpConn = new HttpCryptedConnection(SurveyVerificationActionActivity.this, encrypt, decrypt);
				HttpConnectionResult serverResult = null;

				//Firebase Performance Trace HTTP Request
				HttpMetric networkMetric =
						FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
				Utility.metricStart(networkMetric, json);

				try {
					serverResult = httpConn.requestToServer(url, json,
							Global.SORTCONNECTIONTIMEOUT);
					Utility.metricStop(networkMetric, serverResult);
				} catch (Exception e) {
					FireCrash.log(e);
					e.printStackTrace();
					errMessage = e.getMessage();
				}
				if( serverResult!= null && serverResult.isOK()) {
					String response = serverResult.getResult();

					try {
						SuggestOnVerification resultServer = GsonHelper.fromJson(
								response, SuggestOnVerification.class);
						if (resultServer.getStatus().getCode() == 0) {
							result = resultServer.getListUser();
						} else {
							errMessage = response;
						}
					} catch (Exception e) {
						FireCrash.log(e);
						errMessage = e.getMessage();
					}
				}
			} else {
				errMessage = getString(R.string.no_internet_connection);
			}

			return result;
		}

		@Override
		protected void onPostExecute(List<ListUser> result) {
			if (progressDialog.isShowing()) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					FireCrash.log(e);
				}
			}
			if (errMessage != null && errMessage.length() > 0) {
				Toast.makeText(getApplicationContext(), errMessage,
						Toast.LENGTH_SHORT).show();
			} else {
				if (result != null && !result.isEmpty()) {
					suggestionList = result;
					int i = 0;
					radioAction.removeAllViews();
					radioAction.setVisibility(View.VISIBLE);
					for (ListUser optBean : result) {

						RadioButton rb = new RadioButton(
								getApplicationContext());
						rb.setTextColor(Color.BLACK);
						rb.setButtonDrawable(R.drawable.radio_button_background);
						rb.setId(i);


						if (optBean.getIs_suggested().equals("1")){
							rb.setText(optBean.getFullname()+" *");
							radioAction.addView(rb, 0, defLayout);
						}
						else{
							rb.setText(optBean.getFullname());
							radioAction.addView(rb, defLayout);
						}
						i++;
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					SurveyVerificationActionActivity.this, "",
					"Getting Suggestion User...", true);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			//EMPTY
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup paramRadioGroup, int id) {
		selectedChoice = suggestionList.get(id);
		btnSubmit.setEnabled(true);
	}

	@Override
	public void onBackPressed() {
		if(!Global.BACKPRESS_RESTRICTION){
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnBack) {
			finish();
		} else if (id == R.id.btnSend) {
			if(((TextView)findViewById(R.id.textView2)).getText().equals(getString(R.string.action_not_available))){
				Toast.makeText(SurveyVerificationActionActivity.this, getString(R.string.action_not_available), Toast.LENGTH_SHORT).show();
			}
			else if (txtNotes.getText() != null
					&& txtNotes.getText().toString().length() > 0) {
				if (Flag_4_Reject.equals(actionFlag)) {
					Global.getSharedGlobal().setIsVerifiedByUser(true);
					btnSubmit.setEnabled(false);
					if(ApplicationFlag.equals(Global.VERIFICATION_FLAG)){
						SurveyHeaderBean header = DynamicFormActivity.getHeader();
						header.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
						String notes = txtNotes.getText().toString();
						EventBusHelper.post(DynamicFormActivity.getHeader());
						sendRejectedTask(this, header,
								Global.FLAG_FOR_REJECTEDTASK, notes);
					}
					else if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
						SurveyHeaderBean header = DynamicFormActivity.getHeader();
						String notes = txtNotes.getText().toString();
						TaskH taskH = header.getTaskH();
						taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
						if (taskH.getSubmit_date() == null)
							taskH.setSubmit_date(Tool.getSystemDateTime());
						boolean isApprovalTask =true;
						EventBusHelper.post(DynamicFormActivity.getHeader());
						new TaskManager.ApproveTaskOnBackground(this, taskH, Global.FLAG_FOR_REJECTEDTASK, isApprovalTask, notes).execute();
						Intent intent = new Intent(this, NewMainActivity.getMainMenuClass());
						intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
						startActivity(intent);
						finish();
					}

				} else if (Flag_4_RejectWithResurvey.equals(actionFlag)) {
					if (selectedChoice != null) {
						Global.getSharedGlobal().setIsVerifiedByUser(true);
						btnSubmit.setEnabled(false);
						EventBusHelper.post(DynamicFormActivity.getHeader());

						if(ApplicationFlag.equals(Global.VERIFICATION_FLAG)){
							SurveyHeaderBean header = DynamicFormActivity.getHeader();
							header.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
							String notes = txtNotes.getText().toString();
							sendRejectedWithReSurveyTask(this,header,
									Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY,
									selectedChoice.getUuid_ms_user(),
									selectedChoice.getIs_suggested(), notes);
						}
						else if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
							SurveyHeaderBean header = DynamicFormActivity.getHeader();
							header.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
							String notes = txtNotes.getText().toString();
							sendRejectedWithReSurveyTask(this,header,
									Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY,
									selectedChoice.getUuid_ms_user(),
									selectedChoice.getIs_suggested(), notes);
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Select Suggestion User First!",
								Toast.LENGTH_SHORT).show();
					}
				} else if (Flag_4_Verify.equals(actionFlag)) {
					Global.getSharedGlobal().setIsVerifiedByUser(true);
					btnSubmit.setEnabled(false);
					if(ApplicationFlag.equals(Global.VERIFICATION_FLAG)){
						SurveyHeaderBean header = null;
						try {
							header = (SurveyHeaderBean) DynamicFormActivity.getHeader()
									.clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}

						if(header != null) {
							String notes = txtNotes.getText().toString();
							List<QuestionBean> listOfQuestion = new ArrayList<>(Constant.getListOfQuestion().values());
							header.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
							header.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
							if (header.getSubmit_date() == null)
								header.setSubmit_date(Tool.getSystemDateTime());
							EventBusHelper.post(DynamicFormActivity.getHeader());
							new TaskManager.VerifTaskOnBackground(this, mode, header, listOfQuestion, notes).execute();
							Intent intent = new Intent(this, NewMainActivity.getMainMenuClass());
							intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
							startActivity(intent);
							finish();
						}
					}
					else if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
						boolean isApprovalTask = true;
						SurveyHeaderBean header = null;
						try {
							header = (SurveyHeaderBean) DynamicFormActivity.getHeader()
									.clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}

						if(header != null){
							TaskH taskH = header.getTaskH();
							String notes = txtNotes.getText().toString();
							taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
							if (taskH.getSubmit_date() == null)
								taskH.setSubmit_date(Tool.getSystemDateTime());
							EventBusHelper.post(DynamicFormActivity.getHeader());
							new TaskManager.ApproveTaskOnBackground(this, taskH, Global.FLAG_FOR_APPROVALTASK, isApprovalTask, notes).execute();
							Intent intent = new Intent(this, NewMainActivity.getMainMenuClass());
							intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
							startActivity(intent);
							finish();
						}
					}
				} else if(Flag_4_ChooseOne.equals(actionFlag)){
					Toast.makeText(getApplicationContext(), R.string.pleaseChooseOne,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				String message = "Notes "+getString(R.string.msgRequired);
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		}else if(id == R.id.btnSave){
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
            bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SAVE_QUESTION);
            message.setData(bundle);
			FragmentQuestion.questionHandler.sendMessage(message);
			finish();
		}
	}

	public void sendRejectedWithReSurveyTask(final Activity activity,final SurveyHeaderBean header,
											 final String flag, final String uuid_ms_user, final String is_suggested,
											 final String notes) {
		new AsyncTask<String, Void, String>() {
			private ProgressDialog progressDialog;
			private String errMessage = null;
			private String errCode;

			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(
						SurveyVerificationActionActivity.this, "",
						getString(R.string.progressSend), true);
			}

			@Override
			protected String doInBackground(String... params) {
				String result = null;
				if (Tool.isInternetconnected(SurveyVerificationActionActivity.this)) {
					String uuidTaskH = header.getUuid_task_h();
					RequestRejectedWithResurvey request = new RequestRejectedWithResurvey();
					request.setUuid_task_h(uuidTaskH);
					request.setFlag(flag);
					request.setUuid_user(GlobalData.getSharedGlobalData()
							.getUser().getUuid_user());
					request.setUuid_ms_user(params[0]);
					request.setIs_suggested(params[1]);
					request.setNotes(params[2]);
					request.setAudit(GlobalData.getSharedGlobalData()
							.getAuditData());
					request.addImeiAndroidIdToUnstructured();

					String json = GsonHelper.toJson(request);
					String url = GlobalData.getSharedGlobalData()
							.getURL_SUBMITVERIFICATIONTASK();
					if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
						url = GlobalData.getSharedGlobalData()
								.getURL_SUBMITAPPROVALTASK();
					}
					boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
					boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
					HttpCryptedConnection httpConn = new HttpCryptedConnection(SurveyVerificationActionActivity.this, encrypt, decrypt);
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
						try {
							progressDialog.dismiss();
						} catch (Exception e1) {
							FireCrash.log(e1);
						}
						errMessage = e.getMessage();
					}

					try {
						result = serverResult.getResult();
						MssResponseType response = GsonHelper.fromJson(result,
								MssResponseType.class);
						if (response.getStatus().getCode() == 0) {
							result = "success";
						}
						else if(TaskManager.STATUS_TASK_CHANGED.
								equals(String.valueOf(response.getStatus().getCode()))){
							errMessage = activity.getString(R.string.taskChanged);
							//set taskh status and messages to changed
							if(!header.getStatus().equals(TaskHDataAccess.STATUS_TASK_CHANGED)){
								header.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
								TaskHDataAccess.addOrReplace(activity,header);
								if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
									header.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
								}
								else if(ApplicationFlag.equals(Global.VERIFICATION_FLAG)){
									header.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
								}
								header.setMessage(errMessage);
                                TimelineManager.insertTimeline(activity,header);
							}
						} else {
							errMessage = result;
							errCode = String.valueOf(response.getStatus().getCode());
						}
					} catch (Exception e) {
						FireCrash.log(e);
						errMessage = e.getMessage();
					}
				} else {
					result = getString(R.string.no_internet_connection);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (progressDialog.isShowing()) {
					try {
						progressDialog.dismiss();
					} catch (Exception e) {
						FireCrash.log(e);
					}
				}
				if (errMessage != null && errMessage.length() > 0) {
                    if(!TaskHDataAccess.STATUS_TASK_CHANGED.equals(header.getStatus())) {
                        TaskH taskH = header.getTaskH();
                        taskH.setFlag_survey(flag);
                        taskH.setUuid_resurvey_user(uuid_ms_user);
                        taskH.setResurvey_suggested(is_suggested);
                        taskH.setVerification_notes(notes);
                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);

                        if (errCode != null && errCode.equals(STATUS_TASK_NOT_MAPPING)) {
                            taskH.setMessage(getString(com.adins.mss.base.R.string.message_task_not_mapping) + " - Error (" + errCode + ")");
                        } else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
                            taskH.setMessage(getString(com.adins.mss.base.R.string.message_sending_deleted) + " - Error (" + errCode + ")");
                        } else if (errCode != null && errCode.equals(STATUS_IMEI_FAILED)) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
								taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_android_id_not_registered) + " - Error (" + errCode + ")");
							}else {
								taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_imei_not_registered) + " - Error (" + errCode + ")");
							}
						} else {
                            taskH.setMessage(getString(com.adins.mss.base.R.string.message_sending_failed) + " - Error (" + errCode + ")");
                        }

                        TimelineManager.insertTimeline(SurveyVerificationActionActivity.this, taskH);
                        TaskHDataAccess.addOrReplace(SurveyVerificationActionActivity.this, taskH);
                    }
					Intent intent = new Intent(SurveyVerificationActionActivity.this, NewMainActivity.getMainMenuClass());
					intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
					startActivity(intent);
					finish();
				} else {
					if (getString(R.string.no_internet_connection).equals(
							result)) {
						TaskH taskH = header.getTaskH();
						taskH.setFlag_survey(flag);
						taskH.setUuid_resurvey_user(uuid_ms_user);
						taskH.setResurvey_suggested(is_suggested);
						taskH.setVerification_notes(notes);
						taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
						taskH.setMessage(getString(com.adins.mss.base.R.string.no_internet_connection));
						TimelineManager.insertTimeline(SurveyVerificationActionActivity.this, taskH);
						TaskHDataAccess.addOrReplace(SurveyVerificationActionActivity.this, taskH);

						Intent intent = new Intent(SurveyVerificationActionActivity.this, NewMainActivity.getMainMenuClass());
						intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
						startActivity(intent);
						finish();
					} else {
						Global.getSharedGlobal().setIsVerifiedByUser(true);
						TaskH taskH = header.getTaskH();
						taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
						try {
							taskH.setSubmit_date(Tool.getSystemDateTime());
							TimelineManager.insertTimeline(
									SurveyVerificationActionActivity.this,
									taskH, true, true);
							TaskHDataAccess.addOrReplace(
									SurveyVerificationActionActivity.this,
									taskH);
						} catch (Exception e) {
							FireCrash.log(e);
							errMessage = e.getMessage();
						}

						Intent intent = new Intent(SurveyVerificationActionActivity.this, NewMainActivity.getMainMenuClass());
						intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
						startActivity(intent);
						finish();
					}
				}
			}
		}.execute(uuid_ms_user, is_suggested, notes);
	}

	public void sendRejectedTask(final Activity activity,
								 final SurveyHeaderBean header, final String flag, final String notes) {
		new AsyncTask<Void, Void, String>() {
			private ProgressDialog progressDialog;
			private String errMessage = null;
			private String errCode;

			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.progressSend), true);
			}

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				if (Tool.isInternetconnected(activity)) {
					String uuidTaskH = header.getUuid_task_h();
					JsonRequestRejectedVerificationTask request = new JsonRequestRejectedVerificationTask();
					request.setUuid_task_h(uuidTaskH);
					request.setFlag(flag);
					request.setNotes(notes);
					request.setAudit(GlobalData.getSharedGlobalData()
							.getAuditData());
					request.addImeiAndroidIdToUnstructured();

					String json = GsonHelper.toJson(request);
					String url = GlobalData.getSharedGlobalData()
							.getURL_SUBMITVERIFICATIONTASK();

					boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
					boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
					HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
					HttpConnectionResult serverResult = null;

					//Firebase Performance Trace HTTP Request
					HttpMetric networkMetric =
							FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
					Utility.metricStart(networkMetric, json);

					try {
						serverResult = httpConn.requestToServer(url, json);
						Utility.metricStop(networkMetric, serverResult);
					} catch (Exception e) {
						FireCrash.log(e);
						e.printStackTrace();
						try {
							progressDialog.dismiss();
						} catch (Exception e1) {
							FireCrash.log(e1);
						}
						errMessage = e.getMessage();
					}

					try {
						result = serverResult.getResult();
						MssResponseType response = GsonHelper.fromJson(result,
								MssResponseType.class);
						if (response.getStatus().getCode() == 0) {
							result = "success";
						}
						else if(TaskManager.STATUS_TASK_CHANGED.
								equals(String.valueOf(response.getStatus().getCode()))){
							errMessage = activity.getString(R.string.taskChanged);
							//set taskh status and messages to changed
							if(header != null && !header.getStatus().equals(TaskHDataAccess.STATUS_TASK_CHANGED)){
								header.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
								TaskHDataAccess.addOrReplace(activity,header);
								if(ApplicationFlag.equals(Global.APPROVAL_FLAG)){
									header.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
								}
								else if(ApplicationFlag.equals(Global.VERIFICATION_FLAG)){
									header.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
								}
								header.setMessage(errMessage);
                                TimelineManager.insertTimeline(activity,header);
							}
						}
						else {
							errMessage = result;
							errCode = String.valueOf(response.getStatus().getCode());
						}
					} catch (Exception e) {
						FireCrash.log(e);
						errMessage = e.getMessage();
					}
				} else {
					result = activity
							.getString(R.string.no_internet_connection);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (progressDialog.isShowing()) {
					try {
						progressDialog.dismiss();
					} catch (Exception e) {
						FireCrash.log(e);
					}
				}
				if (errMessage != null && errMessage.length() > 0) {
					if(!TaskHDataAccess.STATUS_TASK_CHANGED.equals(header.getStatus())) {
						TaskH taskH = header.getTaskH();
						taskH.setFlag_survey(flag);
						taskH.setVerification_notes(notes);
						taskH.setSubmit_date(Tool.getSystemDateTime());
						taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);

						if (errCode != null && errCode.equals(STATUS_TASK_NOT_MAPPING)) {
							taskH.setMessage(getString(com.adins.mss.base.R.string.message_task_not_mapping) + " - Error (" + errCode + ")");
						} else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
							taskH.setMessage(getString(com.adins.mss.base.R.string.message_sending_deleted) + " - Error (" + errCode + ")");
						}  else if (errCode != null && errCode.equals(STATUS_IMEI_FAILED)) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
								taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_android_id_not_registered) + " - Error (" + errCode + ")");
							} else {
								taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_imei_not_registered) + " - Error (" + errCode + ")");
							}
						} else {
							taskH.setMessage(getString(com.adins.mss.base.R.string.message_sending_failed) + " - Error (" + errCode + ")");
						}

						TimelineManager.insertTimeline(SurveyVerificationActionActivity.this, taskH);
						TaskHDataAccess.addOrReplace(SurveyVerificationActionActivity.this, taskH);
					}
					Intent intent = new Intent(activity, NewMainActivity.getMainMenuClass());
					intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
					activity.startActivity(intent);
					activity.finish();
				} else {
					if (activity.getString(R.string.no_internet_connection).equals(result)) {
						TaskH taskH = header.getTaskH();
						taskH.setFlag_survey(flag);
						taskH.setVerification_notes(notes);
						taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
						taskH.setMessage(getString(com.adins.mss.base.R.string.no_internet_connection));
						taskH.setSubmit_date(Tool.getSystemDateTime());
						TimelineManager.insertTimeline(SurveyVerificationActionActivity.this, taskH);
						TaskHDataAccess.addOrReplace(SurveyVerificationActionActivity.this, taskH);

						Intent intent = new Intent(activity, NewMainActivity.getMainMenuClass());
						intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
						activity.startActivity(intent);
						activity.finish();
					} else {
						TaskH taskH = header.getTaskH();
						if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
							taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
							taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
						} else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
							taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
						}

						try {
							if (taskH != null) {
								taskH.setSubmit_date(Tool.getSystemDateTime());
								TimelineManager.insertTimeline(activity, taskH,
										true, true);
							}

							TaskHDataAccess.addOrReplace(activity, taskH);
						} catch (Exception e) {
							FireCrash.log(e);
						}

						Intent intent = new Intent(activity, NewMainActivity.getMainMenuClass());
						intent.setAction(Global.MAINMENU_NOTIFICATION_KEY);
						activity.startActivity(intent);
						activity.finish();
					}
				}
			}
		}.execute();
	}
}
