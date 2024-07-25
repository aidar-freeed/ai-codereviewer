package com.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonRequestTaskD;
import com.adins.mss.base.dynamicform.JsonResponseTaskD;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.timeline.activity.Timeline_Activity;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.main.MSMApplication;
import com.adins.mss.svy.MSMainMenuActivity;
import com.adins.mss.svy.SurveyApprovalListTask;
import com.adins.mss.svy.models.JsonRequestTaskWithMode;
import com.adins.mss.svy.tool.Constants;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

//import com.adins.mss.foundation.notification.Notification.Tone;
//import com.adins.mss.foundation.notification.Notification.Vibrate;

public class SurveyApprovalThread extends Thread {
	public static final String APPROVALLIST_NOTIFICATION_KEY = "APPROVALLIST_NOTIFICATION_KEY";
	public static final String APPROVALBRANCHLIST_NOTIFICATION_KEY = "APPROVALBRANCHLIST_NOTIFICATION_KEY";
	private Context context;
	private int interval; // in miliseconds
	private volatile boolean keepRunning = true;
	private volatile boolean isWait = false;
	public static long lastAssigmentDate = 0;
	String sentStatus = "";
	public static List<TaskH> taskHList = new ArrayList<TaskH>();
	public static int notifCount = 0;
	public String uuidUser ;

	public boolean firstNotif = true;

//	public Class mss;

	public SurveyApprovalThread(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		interval = Global.MINUTE * 10;
		try {
			uuidUser = GlobalData.getSharedGlobalData().getUser()
					.getUuid_user();
			if(uuidUser!=null){
				if (GeneralParameterDataAccess.getOne(context, uuidUser, "PRM04_F5IN")
						.getGs_value() != null
						&& !GeneralParameterDataAccess
								.getOne(context, uuidUser, "PRM04_F5IN").getGs_value()
								.isEmpty()) {
					interval = Integer.parseInt(GeneralParameterDataAccess.getOne(
							context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000; // from
																					// milisecond
																					// to
																					// second
				}
			}else{
				keepRunning = false;
			}
		} catch (Exception e) {
			FireCrash.log(e);
			if(Global.user!=null){
				uuidUser = Global.user.getUuid_user();
				if(uuidUser!=null){
					if (GeneralParameterDataAccess.getOne(context, uuidUser, "PRM04_F5IN")
							.getGs_value() != null
							&& !GeneralParameterDataAccess
									.getOne(context, uuidUser, "PRM04_F5IN").getGs_value()
									.isEmpty()) {
						interval = Integer.parseInt(GeneralParameterDataAccess.getOne(
								context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000; // from
																						// milisecond
																						// to
																						// second
					}
				}
			}else{
				keepRunning = false;
			}
		}		
	}

	@Override
	public void run() {
		while (keepRunning) {
			try {
				synchronized (this) {
					if (isWait) {
						this.wait();
					}
				}

				if (Tool.isInternetconnected(context)) {
						if(NewMainActivity.mnSurveyApproval!=null){
							notifCount = 0;
							taskHList = getServerNewTask(false);
							notifCount = taskHList.size();
							// save to local
							if(taskHList!=null && taskHList.size()>0)
								ProcessTaskList(notifCount, taskHList, false);
						} else if(NewMainActivity.mnApprovalByBranch !=null){
							notifCount = 0;
							taskHList = getServerNewTask(true);
							notifCount = taskHList.size();
							// save to local
							if(taskHList!=null &&taskHList.size()>0)
								ProcessTaskList(notifCount, taskHList, true);
						}						
				}

				// bong 10 apr 15 - Gigin request penjagaan jika PRM F5IN dari
				// server = 0
				// minimal notif jalan satu kali
				if (interval == 0) {
					keepRunning = false;
				}
				/*Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						// UI code goes here
						try {
							if (MainMenuActivity.mnSVYApproval != null)
								MainMenuActivity.mnSVYApproval.setCounter(String.valueOf(Constants.getCounterApprovalTask(context)));
							if (MSMainMenuActivity.mnSVYApprovalByBranch != null)
								MSMainMenuActivity.mnSVYApprovalByBranch.setCounter(String.valueOf(Constants.getCounterApprovalTaskByBranch(context)));
							if (MainMenuActivity.menuAdapter != null)
								MainMenuActivity.menuAdapter.notifyDataSetChanged();
							if (Timeline_Activity.getTimelineHandler() != null)
								Timeline_Activity.getTimelineHandler().sendEmptyMessage(0);
//							long taskListCounter = ToDoList.getAllCounter(context);
//							if(Timeline_Activity.query!=null)
//								Timeline_Activity.query.id(R.id.txtJumlahOutstanding).text(String.valueOf(taskListCounter));
						} catch (Exception e) {             FireCrash.log(e);
							// TODO: handle exception
						}
					}
				});*/
				try {
//					MainMenuActivity.setDrawerCounter();
					NewMainActivity.setCounter();
				} catch (Exception e) {
					FireCrash.log(e);
					// TODO: handle exception
				}
				sleep(interval);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void ProcessTaskList(int notifCount, List<TaskH> taskHList, boolean isBranch) {
//		List<String> listUuidTaskH = null;
		if (taskHList.size() > 0) {
//			listUuidTaskH = new ArrayList<>();
			for (TaskH taskH : taskHList) {
//				listUuidTaskH.add(taskH.getUuid_task_h());
				taskH.setUser(GlobalData.getSharedGlobalData()
						.getUser());
				taskH.setUuid_user(GlobalData
						.getSharedGlobalData().getUser()
						.getUuid_user());
				taskH.setIs_verification(Global.TRUE_STRING);
				if (isBranch)
					taskH.setAccess_mode(TaskHDataAccess.ACCESS_MODE_BRANCH);
				else
					taskH.setAccess_mode(TaskHDataAccess.ACCESS_MODE_USER);
				String uuid_timelineType = TimelineTypeDataAccess
						.getTimelineTypebyType(context,
								Global.TIMELINE_TYPE_APPROVAL)
						.getUuid_timeline_type();
				boolean wasInTimeline = TimelineDataAccess
						.getOneTimelineByTaskH(context,
								GlobalData
										.getSharedGlobalData()
										.getUser()
										.getUuid_user(),
								taskH.getUuid_task_h(),
								uuid_timelineType) != null;

				String uuid_scheme = taskH.getUuid_scheme();
				Scheme scheme = SchemeDataAccess.getOne(
						context, uuid_scheme);
				if (scheme != null) {
					taskH.setScheme(scheme);
					TaskH h = TaskHDataAccess.getOneTaskHeader(
							context, taskH.getTask_id());
					if (h != null && h.getStatus() != null) {
						if (!ToDoList.isOldTask(h)) {
							TaskHDataAccess.addOrReplace(
									context, taskH);
							if (!wasInTimeline)
								TimelineManager.insertTimeline(
										context, taskH);
						} else {
							if (h.getAccess_mode() != null && h.getAccess_mode().length() > 0) {
								if (!isBranch && h.getAccess_mode()
										.equals(TaskHDataAccess.ACCESS_MODE_BRANCH)) {
									h.setAccess_mode(TaskHDataAccess.ACCESS_MODE_HYBRID);
									TaskHDataAccess
											.addOrReplace(
													context, h);
								} else if (isBranch && h.getAccess_mode()
										.equals(TaskHDataAccess.ACCESS_MODE_USER)) {
									h.setAccess_mode(TaskHDataAccess.ACCESS_MODE_HYBRID);
									TaskHDataAccess
											.addOrReplace(
													context, h);
								}
							}
							notifCount--;
						}
					} else {
						TaskHDataAccess.addOrReplace(context,
								taskH);
						if (!wasInTimeline)
							TimelineManager.insertTimeline(
									context, taskH);
					}

					// tambahin buat get answer
					TaskD d = TaskDDataAccess.getOneByTaskH(context, taskH.getUuid_task_h());
					boolean isHaveAnswer = d != null;
					if (!isHaveAnswer) {
						JsonRequestTaskD request = new JsonRequestTaskD();
						request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
						request.setuuid_task_h(taskH.getUuid_task_h());
						String json = GsonHelper.toJson(request);
						String url = GlobalData.getSharedGlobalData().getURL_GET_VERIFICATION();
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
							FireCrash.log(e);
							e.printStackTrace();
						}
						if (serverResult.isOK()) {
							try {
								String result = serverResult.getResult();

								JsonResponseTaskD response = GsonHelper.fromJson(result, JsonResponseTaskD.class);
								if (response.getStatus().getCode() == 0) {
									List<TaskD> taskDs = response.getListTask();

									if (taskDs.size() > 0) {

										TaskH h2 = TaskHDataAccess.getOneHeader(
												context, taskH.getUuid_task_h());
										if (h2 != null && h2.getStatus() != null) {
											if (!ToDoList.isOldTask(h2)) {
												taskH.setScheme(scheme);
												taskH.setStatus(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD);
												TaskHDataAccess.addOrReplace(context, taskH);
												for (TaskD taskD : taskDs) {
													taskD.setTaskH(taskH);
													TaskDDataAccess.addOrReplace(context, taskD);
												}
											}
										} else {
											taskH.setScheme(scheme);
											taskH.setStatus(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD);
											TaskHDataAccess.addOrReplace(context, taskH);
											for (TaskD taskD : taskDs) {
												taskD.setTaskH(taskH);
												TaskDDataAccess.addOrReplace(context, taskD);
											}
										}
									}

								} else {
									if (Global.IS_DEV)
										System.out.println(result);
								}

							} catch (Exception e) {
								FireCrash.log(e);
							}
						}
					}
				} else {
					notifCount--;
				}
			}

			String notifTitle = context.getString(R.string.approval_tasks, notifCount);
			String message = context.getString(R.string.approval_tasks_remaining, notifCount);
			Intent resultIntent = new Intent(context, MSMApplication.getInstance().getHomeClass());
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

		/*Vibrate vibrate = null;
		try {
			vibrate = Vibrate
					.valueOf(GeneralParameterDataAccess.getOne(
							context, uuidUser, "PRM14_VIB")
							.getGs_value());
		} catch (Exception ex) {
			vibrate = Vibrate.OFF;
		}

		Tone tone = null;
		try {
			tone = Tone.valueOf(GeneralParameterDataAccess
					.getOne(context, uuidUser, "PRM15_TON")
					.getGs_value());
		} catch (Exception ex) {
			tone = Tone.OFF;
		}*/
//		boolean autoClear = true;
//		try {
//			autoClear = Boolean
//					.parseBoolean(GeneralParameterDataAccess
//							.getOne(context, uuidUser,
//									"PRM16_ACN").getGs_value());
//		} catch (Exception ex) {
//			// ex.printStackTrace();
//			autoClear = true;
//		}

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(NotificationThread.getNotificationIcon());
			builder.setContentTitle(notifTitle);
			builder.setContentText(message).setNumber(notifCount);
			builder.setPriority(NORM_PRIORITY);
//		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		builder.setSound(alarmSound);
//		builder.setVibrate(new long[] { 1000});
//		builder.setLights(Color.RED, 3000, 3000);

			NotificationCompat.BigTextStyle inboxStyle =
					new NotificationCompat.BigTextStyle();
			inboxStyle.setBigContentTitle(notifTitle);

			if (isBranch) {
				inboxStyle.bigText(message + " " + context.getString(R.string.click_to_open_approval_branch));
				resultIntent.setAction(APPROVALBRANCHLIST_NOTIFICATION_KEY);
			} else {
				inboxStyle.bigText(message + " " + context.getString(R.string.click_to_open_approval));
				resultIntent.setAction(APPROVALLIST_NOTIFICATION_KEY);
			}

			builder.setDefaults(android.app.Notification.DEFAULT_ALL);
			builder.setStyle(inboxStyle);
			builder.setAutoCancel(true);

			PendingIntent pendingIntent = PendingIntent
					.getActivity(context, 0, resultIntent, 0);
			Notification.getSharedNotification().setDefaultIcon(
					R.drawable.icon_notif_new);

			builder.setContentIntent(pendingIntent);

			if (notifCount > 0) {
				NotificationManager mNotificationManager =
						(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(3, builder.build());
				notifCount = 0;
			}
		}
		//check for changed task in result(local db) data
		List<TaskH> localTaskHs = TaskHDataAccess.getAllApproval(context,GlobalData.getSharedGlobalData().getUser().getUuid_user());
		if(localTaskHs != null && localTaskHs.size() > 0){
			for (int i= 0; i<localTaskHs.size(); i++) {
				boolean taskNotfound = false;
				TaskH taskh_local = localTaskHs.get(i);
				if(taskh_local == null)
					continue;
				if(taskHList != null && taskHList.size() > 0){
					//delete taskh_local
					for(TaskH taskHServer :taskHList){
						if(taskHServer != null && taskHServer.getTask_id().equals(taskh_local.getTask_id())){
							taskNotfound = true;
							break;
						}
					}
					if(!taskNotfound){
						taskh_local.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
						taskh_local.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
						taskh_local.setMessage(context.getString(com.adins.mss.svy.R.string.taskChanged));
						TaskHDataAccess.addOrReplace(context,taskh_local);
						TimelineManager.insertTimeline(context,taskh_local);
					}
				}
			}
		}

//		List<TaskH> taskHs = TaskHDataAccess.getAllTaskByStatus(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD);
//		if (listUuidTaskH != null) {
//			if (taskHs != null) {
//				for (TaskH h : taskHs) {
//					String uuid_task_h = h.getUuid_task_h();
//					boolean isSame = false;
//					for (String uuid_from_server : listUuidTaskH) {
//						if (uuid_task_h.equals(uuid_from_server)) {
//							isSame = true;
//							break;
//						}
//					}
//					if (!isSame) {
//						TaskHDataAccess.deleteWithRelation(context, h);
//					}
//				}
//			}
//		}
	}

	public synchronized void requestWait() {
		isWait = true;
	}

	public synchronized void stopWaiting() {
		isWait = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	public synchronized void requestStop() {
		Notification.getSharedNotification().clearNotifAll(context);
		keepRunning = false;
	}

	public List<TaskH> getServerNewTask(boolean isBranch) {
		JsonRequestTaskWithMode requestType = new JsonRequestTaskWithMode();
		requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
		requestType.addImeiAndroidIdToUnstructured();
		if(isBranch)
			requestType.setMode(SurveyApprovalListTask.KEY_BRANCH);
		String json = GsonHelper.toJson(requestType);
		String url = GlobalData.getSharedGlobalData().getURL_GET_LIST_APPROVAL();
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
			FireCrash.log(e);
			e.printStackTrace();
		}
		
		sentStatus = serverResult.getResult();

		if(Global.IS_DEV)
			System.out.println("Here is sent status from notif Approval : " + sentStatus);
		// need object to get json reply from server
		JsonResponseRetrieveTaskList jrsrtl = new JsonResponseRetrieveTaskList();
		jrsrtl = GsonHelper.fromJson(sentStatus, JsonResponseRetrieveTaskList.class);

		// new task must be saved to database
		if (jrsrtl.getListTaskList() != null)
			return jrsrtl.getListTaskList();
		else
			return new ArrayList<TaskH>();
	}


	public static List<TaskH> getTaskHList() {
		return taskHList;
	}
}
