package com.adins.mss.coll.fragments;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.depositreport.DepositReportBatchIdBean;
import com.adins.mss.base.depositreport.DepositReportBatchIdRequest;
import com.adins.mss.base.depositreport.DepositReportBatchIdResponse;
import com.adins.mss.base.depositreport.DetailTaskHRequest;
import com.adins.mss.base.depositreport.DetailTaskHResponse;
import com.adins.mss.base.depositreport.TaskLogHelper;
import com.adins.mss.base.tasklog.TaskLog;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.NewMCMainActivity;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.DepositReportReconcileApi;
import com.adins.mss.coll.models.DepositReportAdapter;
import com.adins.mss.coll.models.DepositReportReconcileResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.adins.mss.base.todolist.form.PriorityTabFragment.toDoList;

/**
 * Created by Aditya Purwa on 2/3/2015.
 */
public class DepositReportPCFragment extends Fragment {
	private static Menu mainMenu;
	DepositReportAdapter adapter;
	public List<TaskH> listTaskRecap;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mainMenu = menu;
//        inflater.inflate(R.menu.menu_deposit_report_fragment, menu);
		try {
			menu.findItem(R.id.menuMore).setVisible(true);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onAttach(Context activity) {
		setHasOptionsMenu(true);
		super.onAttach(activity);
		ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
		insertPrintItemForDeposit();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utility.freeMemory();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_deposit_report, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
		getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
		getActivity().setTitle(getString(R.string.title_mn_depositreportPC));

		Button recapitulateButton = (Button) view.findViewById(R.id.recapitulateButton);
		recapitulateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				recapitulate();
			}
		});

		final List<DepositReportH> reports = DepositReportHDataAccess.getAllForAllUserPC(getActivity());
		ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
		adapter = new DepositReportAdapter(getActivity(), reports);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				DepositReportPCDetailActivity.report = reports.get(position);
				Intent intent = new Intent(getActivity(), DepositReportPCDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id== R.id.menuMore){
			mainMenu.findItem(R.id.recapitulation).setVisible(true);
			mainMenu.findItem(R.id.summary).setVisible(true);
			mainMenu.findItem(R.id.mnViewMap).setVisible(false);
		}
		if (item.getItemId() == R.id.recapitulation) {
			recapitulate();
		}
		if (item.getItemId() == R.id.summary) {
			summarize();
		}
		return super.onOptionsItemSelected(item);
	}

	private void summarize() {
		DepositReportSummaryFragment fragment = new DepositReportSummaryFragment();
		FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
		transaction.replace(R.id.content_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void recapitulate() {
		if(Tool.isInternetconnected(getActivity())) {
			initialize();
//			DepositReportRecapitulateFragment fragment = new DepositReportRecapitulateFragment();
//			FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
//			transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
//			transaction.replace(R.id.content_frame, fragment);
//			transaction.addToBackStack(null);
//			transaction.commit();
		} else {
			Toast.makeText(getActivity(), getActivity().getString(R.string.jsonParseFailed), Toast.LENGTH_SHORT).show();
		}
	}

	private void initialize() {
		loadData();
	}

	private void loadData() {
		new AsyncTask<Void, Void, DepositReportReconcileResponse>() {
			private ProgressDialog progressDialog;
			boolean isError = false;
			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(getActivity(),
						"", getString(R.string.progressWait), true);
			}

			@Override
			protected DepositReportReconcileResponse doInBackground(Void... params) {
				if(Tool.isInternetconnected(getActivity())) {
					TaskLog log = new TaskLog(getActivity());
					List<TaskH> result = log.getListTaskLog();

					List<TaskH> onlineLog = TaskLogHelper.getTaskLog(getActivity());
					if (onlineLog != null) {
						if (result == null) result = new ArrayList<>();
						List<String> uuidListTaskH = new ArrayList<>();

						for (TaskH taskH : result) {
							uuidListTaskH.add(taskH.getUuid_task_h());
						}

						Iterator<TaskH> iterator = onlineLog.iterator();
						while (iterator.hasNext()) {
							TaskH taskH = iterator.next();

							if (uuidListTaskH.contains(taskH.getUuid_task_h())) {
								iterator.remove();
							}
						}

						if (onlineLog.size() > 0) {
							for (TaskH taskH : onlineLog) {
								taskH.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
								taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
								TaskHDataAccess.addOrReplace(getActivity(), taskH);
								result.add(taskH);
							}
						}
					}

					if (result != null && result.size() > 0) {
						for (TaskH taskH : result) {
							List<TaskD> taskDs = TaskDDataAccess.getAll(getActivity(), taskH.getUuid_task_h(),
									TaskDDataAccess.ALL_TASK);
							if (taskDs == null || taskDs.size() == 0) {
								DetailTaskHResponse response = null;

								DetailTaskHRequest request = new DetailTaskHRequest();
								request.setUuidTaskH(taskH.getUuid_task_h());
								request.setFlag(taskH.getFlag());
								request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

								HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(),
										GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
								String url = GlobalData.getSharedGlobalData().getURL_GET_TASK_LOG();
								HttpConnectionResult serverResult;
								// Firebase Performance Trace Network Request
								HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
										url, FirebasePerformance.HttpMethod.POST);
								Utility.metricStart(networkMetric, GsonHelper.toJson(request));

								try {
									serverResult = httpConn.requestToServer(url, GsonHelper.toJson(request),
											Global.DEFAULTCONNECTIONTIMEOUT);
									Utility.metricStop(networkMetric, serverResult);

									if (serverResult != null && serverResult.isOK()) {
										try {
											response = GsonHelper.fromJson(serverResult.getResult(),
													DetailTaskHResponse.class);
										} catch (Exception e) {
											e.printStackTrace();
											isError = true;
										}
									} else {
										isError = true;
									}
								} catch (Exception e) {
									e.printStackTrace();
									isError = true;
								}

								if (isError) {
									break;
								}

								if (response != null && response.getTaskDs() != null) {
									TaskDDataAccess.addOrReplace(getActivity(), response.getTaskDs());
								}
							}
						}
					}

					final List<TaskD> reports = TaskDDataAccess.getTaskDTagTotal(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());

					List<String> taskId = new ArrayList<String>();
					List<String> flag = new ArrayList<String>();
					TaskH taskH;
					for (TaskD taskD : reports) {
						taskH = TaskHDataAccess.getOneHeader(getActivity(), taskD.getUuid_task_h());
						//if(taskH.getIs_reconciled() == null) {
						taskH.setIs_reconciled("0");
						TaskHDataAccess.addOrReplace(getActivity(), taskH);
						//}
						taskId.add(taskH.getTask_id());
						flag.add(taskH.getFlag());
					}
					DepositReportReconcileApi api = new DepositReportReconcileApi(getActivity());

					try {
						return api.request(taskId, flag);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}else{
					isError = true;
					return null;
				}
			}

			@Override
			protected void onPostExecute(DepositReportReconcileResponse depositReportReconcileResponse) {
				super.onPostExecute(depositReportReconcileResponse);
				if (progressDialog != null && progressDialog.isShowing()) {
					try {
						progressDialog.dismiss();
					} catch (Exception e) {
					}
				}
				if (isError) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.jsonParseFailed), Toast.LENGTH_SHORT).show();
				}
				if (depositReportReconcileResponse == null) {
					final List<DepositReportH> reports = DepositReportHDataAccess.getAllForAllUserPC(getActivity());
					ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
					adapter = new DepositReportAdapter(getActivity(), reports);
					list.setAdapter(adapter);

					list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
							// TODO Auto-generated method stub
							DepositReportPCDetailActivity.report = reports.get(position);
							Intent intent = new Intent(getActivity(), DepositReportPCDetailActivity.class);
							startActivity(intent);
						}
					});
				} else if (depositReportReconcileResponse.getStatus().getCode() != 0) {
					NiftyDialogBuilder.getInstance(getActivity())
							.withMessage(depositReportReconcileResponse.getStatus().getMessage())
							.withTitle(getString(R.string.server_error))
							.withButton1Text(getString(R.string.btnClose))
							.setButton1Click(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									getActivity().finish();
								}
							})
							.show();
					return;
				} else {
					List<String> taskId = depositReportReconcileResponse.getTaskId();
					if(taskId!=null){
						for (String task : taskId) {
							TaskH taskH = TaskHDataAccess.getOneTaskHeader(getActivity(), task);
							taskH.setIs_reconciled("1");
							TaskHDataAccess.addOrReplace(getActivity(), taskH);
						}
					}

					final List<DepositReportH> reports = DepositReportHDataAccess.getAllForAllUserPC(getActivity());
					ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
					adapter = new DepositReportAdapter(getActivity(), reports);
					list.setAdapter(adapter);

					list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
							// TODO Auto-generated method stub
							DepositReportDetailActivity.report = reports.get(position);
							Intent intent = new Intent(getActivity(), DepositReportDetailActivity.class);
							startActivity(intent);
						}
					});

					toDoList = new ToDoList(getActivity());
					listTaskRecap = toDoList.getListTaskInStatusForMultiUser(ToDoList.SEARCH_BY_ALL_SENT, "BATCHID");

					if(listTaskRecap != null && listTaskRecap.size() >0 ){
						BatchIdTask task = new BatchIdTask();
						task.execute();
					} else {
						DepositReportPCRecapitulateFragment fragment = new DepositReportPCRecapitulateFragment();
						FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
						transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
						transaction.replace(R.id.content_frame, fragment);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				}
			}
		}.execute();

	}

	private class BatchIdTask extends AsyncTask<Void, Void, List<DepositReportBatchIdBean>> {
		private ProgressDialog progressDialog;
		private String errMessage, message;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(getContext(), "", getString(com.adins.mss.base.R.string.progressWait));
		}

		@Override
		protected List<DepositReportBatchIdBean> doInBackground(Void... Void) {
			if (Tool.isInternetconnected(getActivity())) {
				List<String> listUuidRequest = new ArrayList<>();
				for(TaskH taskHRequest : listTaskRecap){
					listUuidRequest.add(taskHRequest.getTask_id());
				}

				DepositReportBatchIdRequest request = new DepositReportBatchIdRequest();
				request.setListTaskID(listUuidRequest);
				request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

				String json = GsonHelper.toJson(request);
				String url = GlobalData.getSharedGlobalData().getURL_GET_BATCHID_LIST();
				boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
				boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
				HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
				HttpConnectionResult serverResult = null;
				// Firebase Performance Trace Network Request
				HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
						url, FirebasePerformance.HttpMethod.POST);
				Utility.metricStart(networkMetric, json);

				try {
					serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
					Utility.metricStop(networkMetric, serverResult);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (serverResult != null && serverResult.isOK()) {
					try {
						String responseBody = serverResult.getResult();
						DepositReportBatchIdResponse response = GsonHelper.fromJson(responseBody, DepositReportBatchIdResponse.class);

						if (response.getStatus().getCode() != 0) {
							message = response.getStatus().getMessage();
							return new ArrayList<>();
						} else {
							return response.getListBatchID();
						}
					} catch (Exception e) {
						if (Global.IS_DEV) {
							e.printStackTrace();
							errMessage = e.getMessage();
						}
					}
				} else {
					errMessage = getString(com.adins.mss.base.R.string.server_down);
				}

			} else {
				errMessage = getString(com.adins.mss.base.R.string.no_internet_connection);
			}
			return new ArrayList<>();
		}

		@Override
		protected void onPostExecute(List<DepositReportBatchIdBean> batchTask) {
			super.onPostExecute(batchTask);
			progressDialog.dismiss();

			if (batchTask != null && batchTask.size() > 0) {
				for(DepositReportBatchIdBean taskBean : batchTask){
					if("1".equalsIgnoreCase(taskBean.getFlagDeposit()) &&
							(taskBean.getBatchId() !=null && !taskBean.getBatchId().isEmpty())){
						TaskH taskH = TaskHDataAccess.getOneTaskHeader(getActivity(),taskBean.getTaskId());
						taskH.setBatch_id(taskBean.getBatchId());
						TaskHDataAccess.addOrReplace(getActivity(), taskH);
					}else if("0".equalsIgnoreCase(taskBean.getFlagDeposit())&&
							(taskBean.getBatchId() !=null && !taskBean.getBatchId().isEmpty())){
						TaskH taskH = TaskHDataAccess.getOneTaskHeader(getActivity(),taskBean.getTaskId());
						if("0".equalsIgnoreCase(taskH.getIs_reconciled())){
							taskH.setIs_reconciled("1");
							TaskHDataAccess.addOrReplace(getActivity(), taskH);
						}
					}
				}
			} else {
				if(message != null) {
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				} else if (errMessage != null) {
					Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
				}
			}

			DepositReportPCRecapitulateFragment fragment = new DepositReportPCRecapitulateFragment();
			FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
			transaction.replace(R.id.content_frame, fragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		try {
			if (adapter != null) {
//				loadData();
			}
		}catch (Exception e){
			if(Global.IS_DEV)
				e.printStackTrace();
		}
		getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
		getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
		getActivity().setTitle(getString(R.string.title_mn_depositreportPC));
	}


	public void insertPrintItemForDeposit(){
		ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getActivity(),
				"wasInsertedDepositPrint", Context.MODE_PRIVATE);
		String isInsert = sharedPref.getString("isInsertDepositPrint", Global.FALSE_STRING);
		if(isInsert.equals(Global.FALSE_STRING)){
			ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
			sharedPrefEditor.putString("isInsertDepositPrint", Global.TRUE_STRING);
			sharedPrefEditor.commit();

//    		PrintItemDataAccess.delete(getActivity(), "DUMYUUIDSCHEMEFORDEPOSITREPORT");

			String usr_crt = GlobalData.getSharedGlobalData().getUser().getUuid_user();
			Date date = Tool.getSystemDateTime();

			PrintItem itemLogo = new PrintItem(Tool.getUUID());
			itemLogo.setPrint_type_id(Global.PRINT_LOGO);
			itemLogo.setPrint_item_label("LOGO");
			itemLogo.setPrint_item_order(1);
			itemLogo.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemLogo);

			PrintItem itemBranchName = new PrintItem(Tool.getUUID());
			itemBranchName.setPrint_type_id(Global.PRINT_BRANCH_NAME);
			itemBranchName.setPrint_item_label("Branch Name");
			itemBranchName.setPrint_item_order(2);
			itemBranchName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemBranchName);

			PrintItem itemBranchAddr = new PrintItem(Tool.getUUID());
			itemBranchAddr.setPrint_type_id(Global.PRINT_BRANCH_ADDRESS);
			itemBranchAddr.setPrint_item_label("Branch Address");
			itemBranchAddr.setPrint_item_order(2);
			itemBranchAddr.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemBranchAddr);

			PrintItem itemNewLineE= new PrintItem(Tool.getUUID());
			itemNewLineE.setPrint_type_id(Global.PRINT_NEW_LINE);
			itemNewLineE.setPrint_item_label("999New Line");
			itemNewLineE.setPrint_item_order(3);
			itemNewLineE.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemNewLineE);
    		
    		/*PrintItem itemNewLine1= new PrintItem(Tool.getUUID());
    		itemNewLine1.setPrint_type_id(Global.PRINT_NEW_LINE);
    		itemNewLine1.setPrint_item_label("998New Line");
    		itemNewLine1.setPrint_item_order(4);
    		itemNewLine1.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
    		PrintItemDataAccess.addOrReplace(getActivity(), itemNewLine1);*/

			PrintItem itemTitle = new PrintItem(Tool.getUUID());
			itemTitle.setPrint_type_id(Global.PRINT_LABEL_CENTER_BOLD);
			itemTitle.setPrint_item_label("Bukti Deposit Report");
			itemTitle.setPrint_item_order(10);
			itemTitle.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemTitle);
    		
    		/*PrintItem itemNewLine2= new PrintItem(Tool.getUUID());
    		itemNewLine2.setPrint_type_id(Global.PRINT_NEW_LINE);
    		itemNewLine2.setPrint_item_label("998New Line");
    		itemNewLine2.setPrint_item_order(4);
    		itemNewLine2.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
    		PrintItemDataAccess.addOrReplace(getActivity(), itemNewLine2);*/

			PrintItem itemBatchId = new PrintItem(Tool.getUUID());
			itemBatchId.setPrint_type_id(Global.PRINT_ANSWER);
			itemBatchId.setPrint_item_label("Batch ID");
			itemBatchId.setPrint_item_order(11);
			itemBatchId.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemBatchId);

			PrintItem itemCollName = new PrintItem(Tool.getUUID());
			itemCollName.setPrint_type_id(Global.PRINT_USER_NAME);
			itemCollName.setPrint_item_label("Coll Name");
			itemCollName.setPrint_item_order(12);
			itemCollName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemCollName);

			PrintItem itemTransferBy = new PrintItem(Tool.getUUID());
			itemTransferBy.setPrint_type_id(Global.PRINT_ANSWER);
			itemTransferBy.setPrint_item_label("Transfer By");
			itemTransferBy.setPrint_item_order(20);
			itemTransferBy.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemTransferBy);

			PrintItem itemCashierName = new PrintItem(Tool.getUUID());
			itemCashierName.setPrint_type_id(Global.PRINT_ANSWER);
			itemCashierName.setPrint_item_label("Cashier Name");
			itemCashierName.setPrint_item_order(30);
			itemCashierName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemCashierName);

			PrintItem itemAccountNo = new PrintItem(Tool.getUUID());
			itemAccountNo.setPrint_type_id(Global.PRINT_ANSWER);
			itemAccountNo.setPrint_item_label("Account No");
			itemAccountNo.setPrint_item_order(30);
			itemAccountNo.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemAccountNo);

			PrintItem itemBankName = new PrintItem(Tool.getUUID());
			itemBankName.setPrint_type_id(Global.PRINT_ANSWER);
			itemBankName.setPrint_item_label("Bank Name");
			itemBankName.setPrint_item_order(40);
			itemBankName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemBankName);

			PrintItem itemTransDate = new PrintItem(Tool.getUUID());
			itemTransDate.setPrint_type_id(Global.PRINT_TIMESTAMP);
			itemTransDate.setPrint_item_label("Print Date");
			itemTransDate.setPrint_item_order(42);
			itemTransDate.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemTransDate);

			PrintItem itemNewLine3= new PrintItem(Tool.getUUID());
			itemNewLine3.setPrint_type_id(Global.PRINT_NEW_LINE);
			itemNewLine3.setPrint_item_label("999New Line");
			itemNewLine3.setPrint_item_order(45);
			itemNewLine3.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemNewLine3);

			PrintItem itemTitleDetail = new PrintItem(Tool.getUUID());
			itemTitleDetail.setPrint_type_id(Global.PRINT_LABEL_CENTER_BOLD);
			itemTitleDetail.setPrint_item_label("Deposit Detail");
			itemTitleDetail.setPrint_item_order(50);
			itemTitleDetail.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemTitleDetail);

			int ANO_order = 60;
			int DAM_order = 61;
			int DIV_order = 62;
			for(int i = 0 ; i<30 ; i++){
				PrintItem itemAgreementNo = new PrintItem(Tool.getUUID());
				itemAgreementNo.setPrint_type_id(Global.PRINT_ANSWER);
				itemAgreementNo.setPrint_item_label(i+"Agreement No");
				itemAgreementNo.setPrint_item_order(ANO_order);
				itemAgreementNo.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
				PrintItemDataAccess.addOrReplace(getActivity(), itemAgreementNo);

				PrintItem itemDepositAmt= new PrintItem(Tool.getUUID());
				itemDepositAmt.setPrint_type_id(Global.PRINT_ANSWER);
				itemDepositAmt.setPrint_item_label(i+"Deposit Amount");
				itemDepositAmt.setPrint_item_order(DAM_order);
				itemDepositAmt.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
				PrintItemDataAccess.addOrReplace(getActivity(), itemDepositAmt);

				PrintItem itemNewLine= new PrintItem(Tool.getUUID());
				itemNewLine.setPrint_type_id(Global.PRINT_NEW_LINE);
				itemNewLine.setPrint_item_label(i+"New Line");
				itemNewLine.setPrint_item_order(DIV_order);
				itemNewLine.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
				PrintItemDataAccess.addOrReplace(getActivity(), itemNewLine);

				ANO_order=ANO_order+3;
				DAM_order=DAM_order+3;
				DIV_order=DIV_order+3;
			}

			PrintItem itemTotalAmt= new PrintItem(Tool.getUUID());
			itemTotalAmt.setPrint_type_id(Global.PRINT_ANSWER);
			itemTotalAmt.setPrint_item_label("Total");
			itemTotalAmt.setPrint_item_order(200);
			itemTotalAmt.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemTotalAmt);

			PrintItem itemNewLineT= new PrintItem(Tool.getUUID());
			itemNewLineT.setPrint_type_id(Global.PRINT_NEW_LINE);
			itemNewLineT.setPrint_item_label("999New Line");
			itemNewLineT.setPrint_item_order(210);
			itemNewLineT.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
			PrintItemDataAccess.addOrReplace(getActivity(), itemNewLineT);
		}
	}
}

