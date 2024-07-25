package com.adins.mss.svy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.JsonResponseTaskList;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.svy.models.JsonRequestTaskWithMode;
import com.adins.mss.svy.tool.Constants;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class SurveyApprovalListTask extends AsyncTask<Void, Void, List<TaskH>> {
	public static final String KEY_BRANCH = "branch";
    private ProgressDialog progressDialog;
	private WeakReference<Context> context;
	private WeakReference<Activity> activity;
    private String messageWait;
    private String messageEmpty;
    private String mode;
    private String errMessage = null;
//    private Class<?> taskSurveyApprovalFragment;
    public Fragment SurveyApprovalFragment;
//    private FragmentActivity fragmentActivity;
//    private int contentFrame;
    //private User user = GlobalData.getSharedGlobalData().getUser();
//    public SurveyApprovalListTask(FragmentActivity fragmentActivity, String messageWait, String messageEmpty, int contentFrame, Fragment LogResultFragment) {
//        this.context = fragmentActivity;
//        this.fragmentActivity = fragmentActivity;
//        this.messageWait = messageWait;
//        this.messageEmpty = messageEmpty;
//        this.contentFrame = contentFrame;
//        this.SurveyApprovalFragment = LogResultFragment;
//    }

    public SurveyApprovalListTask(Activity context, String messageWait, String messageEmpty, String mode) {
        this.context = new WeakReference<Context>(context);
        this.messageWait = messageWait;
        this.messageEmpty = messageEmpty;
        this.mode = mode;
		this.activity = new WeakReference<Activity>(context);
    }

    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(this.context.get(), "", this.messageWait, true);
    }

    protected List<TaskH> doInBackground(Void... params) {
        List<TaskH> result = new ArrayList<TaskH>();
        if(Tool.isInternetconnected(context.get())){
	        JsonRequestTaskWithMode requestType = new JsonRequestTaskWithMode();
			requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
			requestType.addImeiAndroidIdToUnstructured();
			if(mode!=null && !mode.isEmpty())
				requestType.setMode(mode);

			String json = GsonHelper.toJson(requestType);
			String url = GlobalData.getSharedGlobalData().getURL_GET_LIST_APPROVAL();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
			HttpConnectionResult serverResult = null;

			//Firebase Performance Trace HTTP Request
			HttpMetric networkMetric =
					FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
			Utility.metricStart(networkMetric, json);

			try {
//				result = TaskHDataAccess.getAllApproval(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
//				if(result != null && result.size()>0)
//					serverResult = httpConn.requestToServer(url, json, Global.SORTCONNECTIONTIMEOUT);
//				else
				serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
				Utility.metricStop(networkMetric, serverResult);

			} catch (Exception e) {
				FireCrash.log(e);
				e.printStackTrace();
				errMessage = activity.get().getString(R.string.jsonParseFailed);
			}
			
			
			if(serverResult != null){
				if(serverResult.isOK()){
					try {
						String stringResult = serverResult.getResult();
						JsonResponseTaskList taskList = GsonHelper.fromJson(stringResult, JsonResponseTaskList.class);
						if(taskList.getStatus().getCode()==0){
							List<TaskH> listTaskH = taskList.getListTaskList();
							if(listTaskH!=null&&listTaskH.size()>0){			
				
								for(TaskH taskH : listTaskH){													
									taskH.setUser(GlobalData.getSharedGlobalData().getUser());
									taskH.setIs_verification(Global.TRUE_STRING);
									if(mode!=null && KEY_BRANCH.equals(mode))
										taskH.setAccess_mode(TaskHDataAccess.ACCESS_MODE_BRANCH);
									else
										taskH.setAccess_mode(TaskHDataAccess.ACCESS_MODE_USER);
									String uuid_scheme = taskH.getUuid_scheme();
									Scheme scheme = SchemeDataAccess.getOne(context.get(), uuid_scheme);
									if(scheme!=null){
										taskH.setScheme(scheme);										
										TaskH h = TaskHDataAccess.getOneHeader(context.get(), taskH.getUuid_task_h());
										String uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(context.get(), Global.TIMELINE_TYPE_APPROVAL).getUuid_timeline_type();
										boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType)!=null;
										if(h!=null&&h.getStatus()!=null){																				
											if(!ToDoList.isOldTask(h)){
												TaskHDataAccess.addOrReplace(context.get(), taskH);
												if(!wasInTimeline)
													TimelineManager.insertTimeline(context.get(), taskH);
											}
											else{
												if(h.getAccess_mode()!=null && h.getAccess_mode().length()>0){
													if(mode!=null && KEY_BRANCH.equals(mode)){
														if(h.getAccess_mode().equals(TaskHDataAccess.ACCESS_MODE_USER)){
															h.setAccess_mode(TaskHDataAccess.ACCESS_MODE_HYBRID);
															TaskHDataAccess.addOrReplace(context.get(), h);
														}
													}
													else{
														if(h.getAccess_mode().equals(TaskHDataAccess.ACCESS_MODE_BRANCH)){
															h.setAccess_mode(TaskHDataAccess.ACCESS_MODE_HYBRID);
															TaskHDataAccess.addOrReplace(context.get(), h);
														}
													}			
												}
											}
											result.add(h);
										}else{
											result.add(taskH);
											TaskHDataAccess.addOrReplace(context.get(), taskH);
											if(!wasInTimeline)
												TimelineManager.insertTimeline(context.get(), taskH);
										}
									}else{
										errMessage = activity.get().getString(R.string.scheme_not_found);
										if(mode!=null && KEY_BRANCH.equals(mode))
											result = TaskHDataAccess.getAllApprovalForBranch(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
										else
											result = TaskHDataAccess.getAllApprovalForUser(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
									}						
								}
							}
							//check for changed task in result(local db) data
							if(result != null && result.size() > 0){
								for (int i= 0; i<result.size(); i++) {
									boolean taskNotfound = false;
									TaskH taskh_local = result.get(i);
									if(taskh_local == null)
										continue;
									if(listTaskH != null && listTaskH.size() > 0){
										//delete taskh_local
										for(TaskH taskHServer :listTaskH){
											if(taskHServer != null && taskHServer.getTask_id().equals(taskh_local.getTask_id())){
												taskNotfound = true;
												break;
											}
										}
										if(!taskNotfound){
											//set taskh status and messages to changed
											taskh_local.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
                                            taskh_local.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                                            taskh_local.setMessage(context.get().getString(com.adins.mss.svy.R.string.taskChanged));
											TaskHDataAccess.addOrReplace(context.get(),taskh_local);
											TimelineManager.insertTimeline(context.get(),taskh_local);
										}
									}
								}
							}

						}else {
							errMessage = stringResult;
						}
					} catch (Exception e) {
						FireCrash.log(e);
						errMessage = activity.get().getString(R.string.jsonParseFailed);
					}
				}
				else{
					errMessage = serverResult.getResult();
					if(mode!=null && KEY_BRANCH.equals(mode))
						result = TaskHDataAccess.getAllApprovalForBranch(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
					else
						result = TaskHDataAccess.getAllApprovalForUser(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
				}
			}
			else{
				errMessage = "Something wrong from server";
				if(Global.IS_DEV)
					errMessage = "Error: serverResult == null";
				if(mode != null && KEY_BRANCH.equals(mode))
					result = TaskHDataAccess.getAllApprovalForBranch(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
				else
					result = TaskHDataAccess.getAllApprovalForUser(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
			}
	    }else{
			if(mode != null && KEY_BRANCH.equals(mode))
				result = TaskHDataAccess.getAllApprovalForBranch(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
			else
				result = TaskHDataAccess.getAllApprovalForUser(context.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
		}
/*        try {
            if(this.context != null) {
                result = TaskHDataAccess.getAllApproval(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
            } else if(this.activity != null) {
                result = TaskHDataAccess.getAllApproval(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
            } else {
                result = TaskHDataAccess.getAllApproval(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
            }
        } catch (Exception var6) {
            var6.printStackTrace();

            try {
                this.progressDialog.dismiss();
            } catch (Exception var5) {
                ;
            }

            this.errMessage = var6.getMessage();
        }*/

        return result;
    }

    protected void onPostExecute(List<TaskH> result) {
        if(this.progressDialog.isShowing()) {
            try {
                this.progressDialog.dismiss();
            } catch (Exception var4) {
			}
        }

        final NiftyDialogBuilder fragment;
        if (GlobalData.isRequireRelogin()) {

		} else if(this.errMessage != null) {
			if(activity.get().getString(R.string.scheme_not_found).equals(errMessage)){
				Constants.listOfApprovalTask = result;
				fragment = NiftyDialogBuilder.getInstance(this.context.get());
				fragment.withTitle(activity.get().getString(R.string.info_capital)).withMessage(this.errMessage).
						isCancelable(true)
						.withButton1Text(activity.get().getString(R.string.btnOk))
						.setButton1Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								fragment.dismiss();
								gotoPage();
							}
						}).show();
			}else {
				fragment = NiftyDialogBuilder.getInstance(this.context.get());
				fragment.withTitle(activity.get().getString(R.string.warning_capital)).withMessage(this.errMessage).show();
			}
        } else if(result != null && result.size() != 0) {
            Constants.listOfApprovalTask = result;
            gotoPage();
        } else {
            fragment = NiftyDialogBuilder.getInstance(this.context.get());
            fragment.withTitle(activity.get().getString(R.string.info_capital)).withMessage(this.messageEmpty).show();
        }

    }

	private void gotoPage() {
		Fragment fragment1 = null;
		if(mode!=null && !mode.isEmpty()){
			fragment1 = new com.adins.mss.svy.fragments.SurveyApprovalByBranchFragment();
		}else{
			fragment1 = new com.adins.mss.svy.fragments.SurveyApprovalFragment();
		}
		Bundle argument = new Bundle();
		if(mode!=null && !mode.isEmpty())
			argument.putBoolean(SurveyApprovalListTask.KEY_BRANCH, true);
		else
			argument.putBoolean(SurveyApprovalListTask.KEY_BRANCH, false);
		fragment1.setArguments(argument);
		FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
		transaction.replace(R.id.content_frame, fragment1);
		transaction.addToBackStack(null);
		transaction.commit();
	}

}


