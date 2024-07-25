package com.adins.mss.coll.interfaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.depositreport.DetailTaskHRequest;
import com.adins.mss.base.depositreport.DetailTaskHResponse;
import com.adins.mss.base.depositreport.TaskLogHelper;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.TaskDBean;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.todolist.form.CashOnHandResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.Navigator;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.DepositReportReconcileApi;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.fragments.DepositReportDetailActivity;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.coll.models.DepositReportReconcileResponse;
import com.adins.mss.coll.models.DepositReportRequest;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskSummary;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.DepositReportDDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskSummaryDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class DepositReportImpl extends TaskLogImpl implements DepositReportInterface {

    private Context context;
    private int totalNeedPrint;
    private List<TaskD> reportsReconcile = new ArrayList<TaskD>();

    public DepositReportImpl(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void insertPrintItemForDeposit(){
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                "wasInsertedDepositPrint", Context.MODE_PRIVATE);
        String isInsert = sharedPref.getString("isInsertDepositPrint", Global.FALSE_STRING);

        List<PrintItem> printItemList = PrintItemDataAccess.getAll(context, "DUMYUUIDSCHEMEFORDEPOSITREPORT");

        if(isInsert.equals(Global.FALSE_STRING) || printItemList.size() == 0){
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
            PrintItemDataAccess.addOrReplace(context, itemLogo);

            PrintItem itemBranchName = new PrintItem(Tool.getUUID());
            itemBranchName.setPrint_type_id(Global.PRINT_BRANCH_NAME);
            itemBranchName.setPrint_item_label("Branch Name");
            itemBranchName.setPrint_item_order(2);
            itemBranchName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemBranchName);

            PrintItem itemBranchAddr = new PrintItem(Tool.getUUID());
            itemBranchAddr.setPrint_type_id(Global.PRINT_BRANCH_ADDRESS);
            itemBranchAddr.setPrint_item_label("Branch Address");
            itemBranchAddr.setPrint_item_order(2);
            itemBranchAddr.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemBranchAddr);

            PrintItem itemNewLineE= new PrintItem(Tool.getUUID());
            itemNewLineE.setPrint_type_id(Global.PRINT_NEW_LINE);
            itemNewLineE.setPrint_item_label("999New Line");
            itemNewLineE.setPrint_item_order(3);
            itemNewLineE.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemNewLineE);

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
            PrintItemDataAccess.addOrReplace(context, itemTitle);

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
            PrintItemDataAccess.addOrReplace(context, itemBatchId);

            PrintItem itemCollName = new PrintItem(Tool.getUUID());
            itemCollName.setPrint_type_id(Global.PRINT_USER_NAME);
            itemCollName.setPrint_item_label("Coll Name");
            itemCollName.setPrint_item_order(12);
            itemCollName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemCollName);

            PrintItem itemCollLoginId = new PrintItem(Tool.getUUID());
            itemCollLoginId.setPrint_type_id(Global.PRINT_LOGIN_ID);
            itemCollLoginId.setPrint_item_label("Coll Login ID");
            itemCollLoginId.setPrint_item_order(13);
            itemCollLoginId.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemCollLoginId);

            PrintItem itemTransferBy = new PrintItem(Tool.getUUID());
            itemTransferBy.setPrint_type_id(Global.PRINT_ANSWER);
            itemTransferBy.setPrint_item_label("Transfer By");
            itemTransferBy.setPrint_item_order(20);
            itemTransferBy.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemTransferBy);

            PrintItem itemCashierName = new PrintItem(Tool.getUUID());
            itemCashierName.setPrint_type_id(Global.PRINT_ANSWER);
            itemCashierName.setPrint_item_label("Cashier Name");
            itemCashierName.setPrint_item_order(30);
            itemCashierName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemCashierName);

            PrintItem itemAccountNo = new PrintItem(Tool.getUUID());
            itemAccountNo.setPrint_type_id(Global.PRINT_ANSWER);
            itemAccountNo.setPrint_item_label("Account No");
            itemAccountNo.setPrint_item_order(30);
            itemAccountNo.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemAccountNo);

            PrintItem itemBankName = new PrintItem(Tool.getUUID());
            itemBankName.setPrint_type_id(Global.PRINT_ANSWER);
            itemBankName.setPrint_item_label("Bank Name");
            itemBankName.setPrint_item_order(40);
            itemBankName.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemBankName);

            PrintItem itemTransDate = new PrintItem(Tool.getUUID());
            itemTransDate.setPrint_type_id(Global.PRINT_TIMESTAMP);
            itemTransDate.setPrint_item_label("Print Date");
            itemTransDate.setPrint_item_order(42);
            itemTransDate.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemTransDate);

            PrintItem itemNewLine3= new PrintItem(Tool.getUUID());
            itemNewLine3.setPrint_type_id(Global.PRINT_NEW_LINE);
            itemNewLine3.setPrint_item_label("999New Line");
            itemNewLine3.setPrint_item_order(45);
            itemNewLine3.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemNewLine3);

            PrintItem itemTitleDetail = new PrintItem(Tool.getUUID());
            itemTitleDetail.setPrint_type_id(Global.PRINT_LABEL_CENTER_BOLD);
            itemTitleDetail.setPrint_item_label("Deposit Detail");
            itemTitleDetail.setPrint_item_order(50);
            itemTitleDetail.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemTitleDetail);

            int ANO_order = 60;
            int DAM_order = 61;
            int DIV_order = 62;
            for(int i = 0 ; i<30 ; i++){
                PrintItem itemAgreementNo = new PrintItem(Tool.getUUID());
                itemAgreementNo.setPrint_type_id(Global.PRINT_ANSWER);
                itemAgreementNo.setPrint_item_label(i+"Agreement No");
                itemAgreementNo.setPrint_item_order(ANO_order);
                itemAgreementNo.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
                PrintItemDataAccess.addOrReplace(context, itemAgreementNo);

                PrintItem itemDepositAmt= new PrintItem(Tool.getUUID());
                itemDepositAmt.setPrint_type_id(Global.PRINT_ANSWER);
                itemDepositAmt.setPrint_item_label(i+"Deposit Amount");
                itemDepositAmt.setPrint_item_order(DAM_order);
                itemDepositAmt.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
                PrintItemDataAccess.addOrReplace(context, itemDepositAmt);

                PrintItem itemNewLine= new PrintItem(Tool.getUUID());
                itemNewLine.setPrint_type_id(Global.PRINT_NEW_LINE);
                itemNewLine.setPrint_item_label(i+"New Line");
                itemNewLine.setPrint_item_order(DIV_order);
                itemNewLine.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
                PrintItemDataAccess.addOrReplace(context, itemNewLine);

                ANO_order=ANO_order+3;
                DAM_order=DAM_order+3;
                DIV_order=DIV_order+3;
            }

            PrintItem itemTotalAmt= new PrintItem(Tool.getUUID());
            itemTotalAmt.setPrint_type_id(Global.PRINT_ANSWER);
            itemTotalAmt.setPrint_item_label("Total");
            itemTotalAmt.setPrint_item_order(200);
            itemTotalAmt.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemTotalAmt);

            PrintItem itemNewLineT= new PrintItem(Tool.getUUID());
            itemNewLineT.setPrint_type_id(Global.PRINT_NEW_LINE);
            itemNewLineT.setPrint_item_label("999New Line");
            itemNewLineT.setPrint_item_order(210);
            itemNewLineT.setUuid_scheme("DUMYUUIDSCHEMEFORDEPOSITREPORT");
            PrintItemDataAccess.addOrReplace(context, itemNewLineT);
        }
    }

    @Override
    public void generatePrintResultDepositReport(Activity activity, String cashierName, String total, DepositReportH report) {
        List<PrintItem> printItemList = PrintItemDataAccess.getAll(activity, "DUMYUUIDSCHEMEFORDEPOSITREPORT");

        //delete dulu yang ada di database, karena generate printResult dengan jawaban yang baru
        List<PrintResult> printResultByTaskH = PrintResultDataAccess.getAll(activity, report.getBatch_id());
        if(printResultByTaskH.size()>0){
            PrintResultDataAccess.delete(activity, report.getBatch_id());
        }
        PrintResult PRtransferBy = new PrintResult(Tool.getUUID());
        PRtransferBy.setPrint_type_id(Global.PRINT_ANSWER);
        PRtransferBy.setUser(GlobalData.getSharedGlobalData().getUser());
        PRtransferBy.setUuid_task_h(report.getBatch_id());
        for (PrintItem bean : printItemList) {
            PrintResult printResult = new PrintResult(Tool.getUUID());
            printResult.setPrint_type_id(bean.getPrint_type_id());
            printResult.setUser(GlobalData.getSharedGlobalData().getUser());
            printResult.setUuid_task_h(report.getBatch_id());

            if (bean.getPrint_type_id().equals(Global.PRINT_ANSWER)) {
                String label = bean.getPrint_item_label();
                if (label.equals("Batch ID")) {
                    printResult.setLabel(label);
                    printResult.setValue(report.getBatch_id());
                } else if (label.equals("Transfer By")) {
                    printResult.setLabel(label);
                    if (cashierName != null && cashierName.length() > 0) {
                        printResult.setValue("Cashier");
                        PRtransferBy.setLabel(label);
                        PRtransferBy.setValue("Cashier");
                    } else {
                        printResult.setValue("Bank");
                        PRtransferBy.setLabel(label);
                        PRtransferBy.setValue("Bank");
                    }
                } else if (label.equals("Cashier Name")) {
                    if (PRtransferBy.getValue().equals("Cashier")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getCashier_name());
                    }
                } else if (label.equals("Account No")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_account());
                    }
                } else if (label.equals("Bank Name")) {
                    if (PRtransferBy.getValue().equals("Bank")) {
                        printResult.setLabel(label);
                        printResult.setValue(report.getBank_name());
                    }
                } else if (label.contains("Agreement No")) {
                    int no = Integer.valueOf(label.replace("Agreement No", ""));
                    printResult.setLabel("Agreement No");
                    List<DepositReportD> reportDs = report.getDepositReportDList();
                    try {
                        TaskH taskHs = TaskHDataAccess.getOneHeader(activity, reportDs.get(no).getUuid_task_h());
                        String agreement_no = "";
                        if (taskHs != null)
                            agreement_no = taskHs.getAppl_no();
                        if (agreement_no == null)
                            agreement_no = "-";
                        printResult.setValue(agreement_no);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else if (label.contains("Deposit Amount")) {
                    int no = Integer.valueOf(label
                            .replace("Deposit Amount", ""));
                    printResult.setLabel("Deposit Amt");
                    List<DepositReportD> reportDs = report.getDepositReportDList();
                    try {
                        printResult.setValue(Tool.separateThousand(reportDs.get(no).getDeposit_amt()));
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else if (label.equals("Total")) {
                    printResult.setLabel(label);
                    printResult.setValue(String.valueOf(Tool.separateThousand(total)));
                }
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_BRANCH_ADDRESS)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser().getBranch_address());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BRANCH_NAME)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser().getBranch_name());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BT_ID)) {
                String btAddr = "?";
                try {
                    btAddr = BluetoothAdapter.getDefaultAdapter().getAddress();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(btAddr);
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id()
                    .equals(Global.PRINT_LABEL_CENTER)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(
                    Global.PRINT_LABEL_CENTER_BOLD)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGO)) {
                printResult.setLabel("");
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_NEW_LINE)) {
                String label = bean.getPrint_item_label();
                int no = Integer.valueOf(label.replace("New Line", ""));
                List<DepositReportD> reportDs = report.getDepositReportDList();
                int size = reportDs.size();
                if (no < size) {
                    printResult.setLabel("------------------------------");
                    printResult.setValue("\n");
                }
                if (no == 999) {
                    printResult.setLabel("==============================");
                    printResult.setValue("\n");
                }
                if (no == 998) {
                    printResult.setLabel("\n");
                    printResult.setValue("\n");
                }
            } else if (bean.getPrint_type_id().equals(Global.PRINT_TIMESTAMP)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_USER_NAME)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser()
                        .getFullname());
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGIN_ID)){
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser().getLogin_id());
            }
            if (printResult.getLabel() != null) {
                PrintResultDataAccess.add(activity, printResult);
            }
        }
    }

    @Override
    public void getDepositReportH(Activity activity, TaskListener listener) {
        DepositReportTask reportTask = new DepositReportTask(activity, listener);
        reportTask.execute();
    }

    @Override
    public void cleanDepositReportH() {
        Date today = CommonImpl.resetDate();
        DepositReportHDataAccess.deleteDepositReport(context, today);
    }

    @Override
    public void fillHeader(DepositReportCallback callback) {
//        List<TaskH> h = TaskHDataAccess.getTaskCollToday(context);
//        List<TaskH> allSentTask = TaskHDataAccess.getAllSentTask(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
//        List<TaskH> allSentTaskToday = new ArrayList<TaskH>();
//        for(TaskH taskH : allSentTask){
//            if(CommonImpl.dateIsToday(taskH.getSubmit_date())){
//                allSentTaskToday.add(taskH);
//            }
//        }

//        List<TaskH> allCollTask = TaskHDataAccess.getAllTaskCollection(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
//        List<TaskH> collTaskToday = new ArrayList<TaskH>();
//        for(TaskH taskH : allCollTask){
//            if(CommonImpl.dateIsToday(taskH.getAssignment_date())){
//                collTaskToday.add(taskH);
//            }
//        }

//    	int totalTask = TaskHDataAccess.getTotalTaskCollToday(getActivity());
//        int totalTask = collTaskToday.size();
//        totalTaskLabel.setText(totalTask + " Tasks");
       /* List<TaskD> totalTaskCollToday = TaskDDataAccess.getTotalTaskCollToday(getActivity());
        int totalTaskValue = 0;
        for (TaskD task : totalTaskCollToday) {
        	try {
        		totalTaskValue = Integer.parseInt(task.getText_answer());
			} catch (Exception e) {             FireCrash.log(e);
				totalTaskValue = 0;
			}
        }*/

//        int paidTask = TaskDDataAccess.getPaid(context).size();
//        int failTask = TaskDDataAccess.getFail(context).size();
//
//        List<TaskD> paidAllTask = TaskDDataAccess.getAllPaid(context);
//        paidTask = 0;
//        for(TaskD taskD:paidAllTask){
//            for(TaskH taskH :collTaskToday){
//                if(taskD.getUuid_task_h().equals(taskH.getUuid_task_h())){
//                    paidTask++;
//                }
//            }
//        }

//        List<TaskD> failAllTask = TaskDDataAccess.getAllFail(getActivity());
//        failTask = 0;
//        for(TaskH taskH : allSentTaskToday){
//        	/*List<TaskD> ds = TaskDDataAccess.isTaskPaid(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), taskH.getUuid_task_h());
//        	for(TaskD d : ds){
//        		String a = d.getUuid_task_d();
//        	}*/
//            if(!TaskDDataAccess.isTaskPaid(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), taskH.getUuid_task_h())){
//                failTask++;
//            }

//            int visitTask = paidTask + failTask;

//            callback.OnFillHeader(totalTask, paidTask, failTask, visitTask);
//        	List<TaskD> alltaskd = taskH.getTaskDList();
//        	for(TaskD d : alltaskd){
//        		String questionId = d.getQuestion_id();
//        		String questionGroupId = d.getQuestion_group_id();
//        		QuestionSet questionSet = QuestionSetDataAccess.getOne(getActivity(), taskH.getUuid_scheme(), questionId, questionGroupId);
//        		if(questionSet.getTag()!=null && Global.TAG_TOTAL.equals(questionSet.getTag())){
//        			tempPaid ++;
//        			break;
//        		}
//        	}
//        }

//        failTask = allSentTaskToday.size() - paidTask;


//        for(TaskD taskD:failAllTask){
//        	for(TaskH taskH :collTaskToday){
//        		if(taskD.getUuid_task_h().equals(taskH.getUuid_task_h())){
//        			failTask++;
//        		}
//        	}
//        }

//        this.totalTaskValue.setText(totalTask + " Tasks");
//        totalPaidLabel.setText(paidTask + " Tasks");
//        totalFailLabel.setText(failTask + " Tasks");
//        totalVisitLabel.setText(visitTask + " Tasks");

        // 2017/10/02 | olivia : ambil data summary dari table TaskSummary
        int totalTask = 0;
        int paidTask = 0;
        int failTask = 0;

        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

        List<TaskSummary> taskSummaries = TaskSummaryDataAccess.getAll(context, uuidUser);
        for (TaskSummary task : taskSummaries) {
            if(CommonImpl.dateIsToday(task.getDtm_crt()))
                totalTask++;
        }

        List<TaskSummary> taskPaid = TaskSummaryDataAccess.getAllPaid(context,uuidUser);
        List<DepositReportH> tempBatches = DepositReportHDataAccess.listOfBacth(context, uuidUser);
        List<String> uuidH = new ArrayList<>();

        for(DepositReportH reportH : tempBatches){
            if(CommonImpl.dateIsToday(reportH.getDtm_crt())){
                if(!uuidH.contains(reportH.getUuid_deposit_report_h())){
                    uuidH.add(reportH.getUuid_deposit_report_h());
                }
            }
        }

        for (TaskSummary task : taskPaid) {
            for (String uuid : uuidH) {
                List<DepositReportD> tempReportD = DepositReportDDataAccess.getAll(context, uuid);
                for (DepositReportD reportD : tempReportD) {
                    if (CommonImpl.dateIsToday(reportD.getDtm_crt())) {
                        if (reportD.getUuid_task_h().equalsIgnoreCase(task.getUuid_task_h()))
                            paidTask++;
                    }
                }
            }
        }

        List<TaskSummary> taskFail = TaskSummaryDataAccess.getAllFail(context, uuidUser);
        for (TaskSummary task : taskFail) {
            if (CommonImpl.dateIsToday(task.getDtm_crt()))
                failTask++;
        }

        int visitTask = paidTask + failTask;

        callback.OnFillHeader(totalTask, paidTask, failTask, visitTask);
    }

    @Override
    public void fillDetail(DepositReportCallback callback) {
        List<DepositReportH> tempBatches = DepositReportHDataAccess.listOfBacth(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        List<DepositReportH> batches = new ArrayList<DepositReportH>();
        for(DepositReportH reportH : tempBatches){
            if(CommonImpl.dateIsToday(reportH.getDtm_crt())){
                batches.add(reportH);
            }
        }
        HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch = packListOfBatch(batches);
        callback.OnFillDetail(packedListOfBatch);
    }

    @Override
    public HashMap<DepositReportH, List<DepositReportD>> packListOfBatch(List<DepositReportH> batches) {
        HashMap<DepositReportH, List<DepositReportD>> map = new HashMap<DepositReportH, List<DepositReportD>>();
        for (DepositReportH reportH : batches) {
        	map.put(reportH, reportH.getDepositReportDList());
        }
        return map;
    }

    @Override
    public void getReportsReconcile(DepositReportCallback callback) {
        reportsReconcile.clear();
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<TaskD> reports = TaskDDataAccess.getTaskDTagTotal(context, uuidUser);

        for (TaskD taskD : reports) {
            if (!taskD.getText_answer().equals("0") && !taskD.getText_answer().equals("")) {
                TaskH taskH = TaskHDataAccess.getOneHeader(context, taskD.getUuid_task_h());
                if (taskH != null && taskH.getIs_reconciled() != null) {
                    if (taskH.getIs_reconciled().equals("0")) {
                        reportsReconcile.add(taskD);
                    }
                }

                if (taskH != null) {
                    int printCount = taskH.getPrint_count() != null ? taskH.getPrint_count():0;
                    String rvNumber = taskH.getRv_number();
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    if (printCount > 0 || (rvNumber != null && !rvNumber.isEmpty()) || isRVinFront) {
                        // do nothing
                    } else {
                        try {
                            String uuidScheme = taskH.getUuid_scheme();
                            Scheme scheme = SchemeDataAccess.getOne(context, uuidScheme);
                            if (scheme != null) {
                                if(scheme.getIs_printable().equals(Global.TRUE_STRING))
                                    totalNeedPrint++;
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            totalNeedPrint++;
                        }
                    }
                }
            }
        }

        callback.OnLoadReconcileData(reportsReconcile, totalNeedPrint);
    }

    public double sumOfItems(List<TaskD> items) {
        double sum = 0;
        try {
            for (TaskD item : items) {
                String value = item.getText_answer();
                if(value==null || value.equals("")) value = "0";
                String tempAnswer = Tool.deleteAll(value, ",");
                String[] intAnswer = Tool.split(tempAnswer, ".");
                if(intAnswer.length>1){
                    if(intAnswer[1].equals("00"))
                        value = intAnswer[0];
                    else {
                        value=tempAnswer;
                    }
                }else{
                    value=tempAnswer;
                }
                double finalValue = Double.parseDouble(value);
                sum += finalValue;
            }
        } catch (Exception e) {             FireCrash.log(e);
            // TODO: handle exception
        }
        return sum;
    }

    @Override
    public SendDepositReportTask sendDepositReport(FragmentActivity activity) {
        SendDepositReportTask sendDepositReportTask = new SendDepositReportTask(activity);
        return sendDepositReportTask;
    }

    private class DepositReportTask extends AsyncTask<Void, Void, DepositReportReconcileResponse> {
        private ProgressDialog progressDialog;
        boolean isError = false;
        private TaskListener listener;
        private WeakReference<Activity> activity;

        public DepositReportTask(TaskListener listener) {
            this.listener = listener;
        }

        public DepositReportTask(Activity mContext, TaskListener listener) {
            this.activity = new WeakReference<Activity>(mContext);
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity.get());
            progressDialog.setMessage(activity.get().getString(R.string.progressWait));
            progressDialog.setCancelable(false);
            progressDialog.show();
//            progressDialog = ProgressDialog.show(context,
//                    "", context.getString(R.string.progressWait), true);
        }

        @Override
        protected DepositReportReconcileResponse doInBackground(Void... params) {
            if(Tool.isInternetconnected(activity.get())) {
//                TaskLogImpl log = new TaskLogImpl(context);
                List<TaskH> result = getListTaskLog();

                List<TaskH> onlineLog = TaskLogHelper.getTaskLog(activity.get());
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
                            TaskHDataAccess.addOrReplace(context, taskH);
                            result.add(taskH);
                        }
                    }
                }

                if (result != null && result.size() > 0) {
                    for (TaskH taskH : result) {
                        List<TaskD> taskDs = TaskDDataAccess.getAll(activity.get(), taskH.getUuid_task_h(),
                                TaskDDataAccess.ALL_TASK);
                        if (taskDs == null || taskDs.size() == 0) {
                            DetailTaskHResponse response = null;


                            DetailTaskHRequest request = new DetailTaskHRequest();
                            request.setUuidTaskH(taskH.getUuid_task_h());
                            request.setFlag(taskH.getFlag());
                            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(),
                                    GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
                            String url = GlobalData.getSharedGlobalData().getURL_GET_TASK_LOG();
                            HttpConnectionResult serverResult;

                            HttpMetric networkMetric =
                                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
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
                                        FireCrash.log(e);
                                        e.printStackTrace();
                                        isError = true;
                                    }
                                } else {
                                    isError = true;
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                e.printStackTrace();
                                isError = true;
                            }


                            if (isError) {
                                break;
                            }

                            if (response != null && response.getTaskDs() != null) {
//                                TaskDDataAccess.addOrReplace(activity.get(), response.getTaskDs());
                                for (TaskD task : response.getTaskDs()) {
                                    TaskD taskD = new TaskD(task.getUuid_task_d(), task.getQuestion_group_id(), task.getQuestion_id(), task.getOption_answer_id(), task.getText_answer(), null, task.getIs_final(), task.getIs_sent(), task.getLov(), task.getUsr_crt(), task.getDtm_crt(), task.getUuid_task_h(), task.getQuestion_label(),
                                            task.getLatitude(), task.getLongitude(), task.getMcc(), task.getMnc(), task.getLac(), task.getCid(), task.getGps_time(), task.getAccuracy(), task.getRegex(), task.getIs_readonly(), task.getLocation_image(), task.getIs_visible(), task.getUuid_lookup(), task.getTag(), task.getCount(), task.getImage_timestamp(), null);

                                    //olivia: jika tidak bisa insert TaskD akan generate uuid baru
                                    try {
                                        TaskDDataAccess.add(activity.get(), taskD);
                                    } catch (Exception e) {
                                        taskD.setUuid_task_d(Tool.getUUID());
                                        TaskDDataAccess.add(activity.get(), taskD);
                                    }
                                }
                            }
                        }
                    }
                }

                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                final List<TaskD> reports = TaskDDataAccess.getTaskDTagTotal(activity.get(), uuidUser);

                List<String> taskId = new ArrayList<String>();
                List<String> flag = new ArrayList<String>();
                TaskH taskH;
                for (TaskD taskD : reports) {
                    taskH = TaskHDataAccess.getOneHeader(activity.get(), taskD.getUuid_task_h());
                    //if(taskH.getIs_reconciled() == null) {
                    taskH.setIs_reconciled("0");
                    TaskHDataAccess.addOrReplace(context, taskH);
                    //}
                    taskId.add(taskH.getTask_id());
                    flag.add(taskH.getFlag());
                }
                DepositReportReconcileApi api = new DepositReportReconcileApi(activity.get());

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
                    FireCrash.log(e);
                }
            }

            if (isError) {
                Toast.makeText(activity.get(), activity.get().getString(R.string.jsonParseFailed), Toast.LENGTH_SHORT).show();
            }

            //Set Listener in These Section
            if (depositReportReconcileResponse == null) {
                List<DepositReportH> reports =
                        DepositReportHDataAccess.getAll(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());

                listener.onCompleteTask(reports);
//                ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
//                adapter = new DepositReportAdapter(getActivity(), reports);
//                list.setAdapter(adapter);
//
//                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                        // TODO Auto-generated method stub
//                        DepositReportDetailActivity.report = reports.get(position);
//                        Intent intent = new Intent(getActivity(), DepositReportDetailActivity.class);
//                        startActivity(intent);
//                    }
//                });
            } else if (depositReportReconcileResponse.getStatus().getCode() != 0) {
                errorMessageHandler.processError(activity.get().getString(R.string.server_error)
                        ,depositReportReconcileResponse.getStatus().getMessage()
                        ,ErrorMessageHandler.DIALOG_TYPE);
                return;
            } else {
                List<String> taskId = depositReportReconcileResponse.getTaskId();

                /*if(taskId.size() == 0) {
                    NiftyDialogBuilder.getInstance(activity.get())
                        .withTitle("INFO")
                        .withMessage(activity.get().getString(R.string.data_not_found))
                        .show();
                } else {*/
                if(taskId != null && taskId.size() > 0){
                    for (String task : taskId) {
                        TaskH taskH = TaskHDataAccess.getOneTaskHeader(activity.get(), task);
                        taskH.setIs_reconciled("1");
                        TaskHDataAccess.addOrReplace(activity.get(), taskH);
                    }
                }

                List<DepositReportH> reports = DepositReportHDataAccess.getAll(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user());

                listener.onCompleteTask(reports);
            }


//                ListView list = (ListView) getView().findViewById(R.id.recapitulationList);
//                adapter = new DepositReportAdapter(getActivity(), reports);
//                list.setAdapter(adapter);
//
//                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                        // TODO Auto-generated method stub
//                        DepositReportDetailActivity.report = reports.get(position);
//                        Intent intent = new Intent(getActivity(), DepositReportDetailActivity.class);
//                        startActivity(intent);
//                    }
//                });
            }
    }

    public class SendDepositReportTask extends AsyncTask<String, Void, List<String>>{
        private ProgressDialog progressDialog;
        private WeakReference<FragmentActivity> activity;
        private DepositReportH header;
        private ArrayList<DepositReportD> details;
        private String namaKasir;
        private String errMsg;
        private StringBuilder sb;
        public DepositReportCallback listener;
        public String total="";
        public Bitmap image;
        public Bitmap tempImage;

        public SendDepositReportTask(FragmentActivity activity){
            this.activity = new WeakReference<FragmentActivity>(activity);
        }
        @Override
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.progressSend), true);
        }
        @Override
        protected List<String> doInBackground(String... params) {
            List<String> results = new ArrayList<>();
            String result = "";
            if(Tool.isInternetconnected(activity.get())){
                sb = new StringBuilder();

                DepositReportRequest request = new DepositReportRequest();
                request.addImeiAndroidIdToUnstructured();
                header = new DepositReportH();
                header.setCashier_name("");
                header.setUuid_deposit_report_h(Tool.getUUID());
                header.setBatch_id(params[0]);
                header.setTransfered_date(Tool.getSystemDateTime());
                header.setDtm_crt(Tool.getSystemDateTime());
                header.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                header.setUser(GlobalData.getSharedGlobalData().getUser());

                if (params[4].equals(String.valueOf(View.VISIBLE))) {
                    String noRek = params[1];
                    String bankName = params[2];
                    // 2017/09/29 | olivia : penjagaan agar image tdk direcycle jika transfer gagal
                    if (noRek != null && noRek.length() > 0 && bankName != null && bankName.length() > 0 && image != null) {
                        byte[] byteImage = Utils.bitmapToByte(tempImage);
                        if (byteImage != null) {
                            header.setBank_account(noRek);
                            header.setBank_name(bankName);
                            header.setImage(byteImage);
                        } else
                            sb.append(activity.get().getString(R.string.evidence_required));
                    } else {
                        if(noRek == null || noRek.length() == 0)
                            sb.append(activity.get().getString(R.string.acc_no_required));
                        if (bankName == null || bankName.length() == 0)
                            sb.append(activity.get().getString(R.string.bank_name_required));
                        if (image == null)
                            sb.append(activity.get().getString(R.string.evidence_required));
                    }
//                    if(noRek!=null && noRek.length()>0)
//                        header.setBank_account(noRek);
//                    else
//                        sb.append(activity.getString(R.string.acc_no_required));
//
//                    if(bankName!=null && bankName.length()>0)
//                        header.setBank_name(bankName);
//                    else
//                        sb.append(activity.getString(R.string.bank_name_required));
//                    if(image == null)
//                        sb.append(activity.getString(R.string.evidence_required));
//                    else{
//                        byte[] byteImage = Utils.bitmapToByte(tempImage);
//                        if(byteImage!=null)
//                            header.setImage(byteImage);
//                        else
//                            sb.append(activity.getString(R.string.evidence_required));
//                    }
                } else {
                    namaKasir = params[3];
                    if(namaKasir!=null && namaKasir.length()>0)
                        header.setCashier_name(namaKasir);
                    else
                        sb.append(activity.get().getString(R.string.cashier_required));
                }

                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setReportHeader(header);

                if(sb.length()>0){
                    result = sb.toString();
                    results.add(0, result);
                }else{
                    details = new ArrayList<DepositReportD>();
                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    List<TaskD> tasks = TaskDDataAccess.getTaskDTagTotal(activity.get(), uuidUser);
                    List<TaskD> reportsReconcile = new ArrayList<TaskD>();

                    for (TaskD taskD : tasks) {
                        TaskH taskH = TaskHDataAccess.getOneHeader(activity.get(), taskD.getUuid_task_h());
                        if (taskH.getIs_reconciled().equals("0")) {
                            reportsReconcile.add(taskD);
                        }
                    }
                    for (TaskD task : reportsReconcile) {
                        TaskH taskH = TaskHDataAccess.getOneHeader(activity.get(), task.getTaskH().getUuid_task_h());

                        DepositReportD detail = new DepositReportD();
                        detail.setUuid_task_h(task.getTaskH().getUuid_task_h());
                        detail.setDtm_crt(Tool.getSystemDateTime());
                        detail.setUsr_crt(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        detail.setUuid_deposit_report_d(Tool.getUUID());
                        String value = task.getText_answer();

                        if (value == null || value.equals(""))
                            value = "0";
                        String tempAnswer = Tool.deleteAll(value, ",");
                        String[] intAnswer = Tool.split(tempAnswer, ".");
                        if (intAnswer.length > 1) {
                            if (intAnswer[1].equals("00"))
                                value = intAnswer[0];
                            else {
                                value = tempAnswer;
                            }
                        } else {
                            value = tempAnswer;
                        }

                        detail.setDeposit_amt(value);
                        detail.setAgreement_no(taskH.getAppl_no());

                        detail.setUuid_deposit_report_h(header.getUuid_deposit_report_h());
                        details.add(detail);
                    }

                    request.setListReportDetail(details);

                    String url = GlobalData.getSharedGlobalData().getURL_SENDDEPOSITREPORT();
                    String json = GsonHelper.toJson(request);
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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    MssRequestType cohRequest = new MssRequestType();
                    cohRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    cohRequest.addImeiAndroidIdToUnstructured();
                    String urlCoh = GlobalData.getSharedGlobalData().getURL_UPDATE_CASH_ON_HAND();
                    String jsonCoh = GsonHelper.toJson(cohRequest);
                    HttpConnectionResult serverResultCoh = null;

                    //Firebase Performance Trace HTTP Request
                    networkMetric = FirebasePerformance.getInstance().newHttpMetric(urlCoh, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, jsonCoh);

                    try {
                        serverResultCoh = httpConn.requestToServer(urlCoh, jsonCoh, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResultCoh);
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        errMsg = e.getMessage();
                    }

                    try {
                        result=serverResult.getResult();
                        results.add(0, result);
                        String resultCoh = serverResultCoh.getResult();
                        results.add(1, resultCoh);
                    } catch (Exception e) {             FireCrash.log(e);
                        // TODO: handle exception
                    }
                }
            }else{
                result = activity.get().getString(R.string.no_internet_connection);
                results.add(0, result);
            }


            return results;
        }
        @Override
        protected void onPostExecute(List<String> results) {
            boolean error = false;
            GlobalData.getSharedGlobalData().setButtonClicked(true);
            if (progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

            listener.OnFinish(true);
//            try {
//                imageBukti.setDrawingCacheEnabled(false);
//            }} catch (Exception e) {             FireCrash.log(e);{
//                e.printStackTrace();
//            }

            if(Global.IS_DEV)
                System.out.println(results);
            if(errMsg!=null){
                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(activity.get());
                dialog.withTitle(activity.get().getString(R.string.error_capital)).withMessage(this.errMsg).
                        withButton1Text("OK").
                        setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View paramView) {
                                dialog.dismiss();
                                CustomerFragment.doBack(activity.get());
                            }
                        }).
                        isCancelable(true).show();
                error = true;
            }else{
                if(activity.get().getString(R.string.no_internet_connection).equals(results.get(0))){
                    Toaster.warning(activity.get(), results.get(0));
                    error = true;
                }else{
                    if(sb!=null && sb.length()>0){
                        Toaster.warning(activity.get(), results.get(0));
                        error = true;
                    }else{
                        try{
                            MssResponseType responseType = GsonHelper.fromJson(results.get(0), MssResponseType.class);
                            if(responseType.getStatus().getCode() == 0){
                                DepositReportHDataAccess.add(activity.get(), header);
                                for(DepositReportD reportD : details){
                                    reportD.setIs_sent(Global.TRUE_STRING);
                                    reportD.setDepositReportH(header);
                                    DepositReportDDataAccess.add(activity.get(), reportD);
                                }
                                generatePrintResultDepositReport(activity.get(), namaKasir, total, header);

                                if(results.size()==2){
                                    try {
                                        CashOnHandResponse cashOnHandResponse = GsonHelper.fromJson(results.get(1), CashOnHandResponse.class);
                                        if (cashOnHandResponse.getStatus().getCode() == 0) {
                                            User user = GlobalData.getSharedGlobalData().getUser();
                                            user.setCash_on_hand(cashOnHandResponse.getCashOnHand());
                                            UserDataAccess.addOrReplace(activity.get(), user);
                                        }
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        e.printStackTrace();
                                        error = true;
                                    }
                                }

                                Navigator navigator = new Navigator(context);
                                navigator.route(Navigator.DEPOSIT_REPORT_RECAPITULATE);

                                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(activity.get());
                                dialog.withTitle(activity.get().getString(R.string.success)).
                                        withMessage(activity.get().getString(R.string.success_deposit)).
                                        withButton1Text("OK").
                                        setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                GlobalData.getSharedGlobalData().setButtonClicked(false);
                                                CustomerFragment.doBack(activity.get());
                                                DepositReportDetailActivity.report = header;
                                                Intent intent = new Intent(activity.get(), DepositReportDetailActivity.class);
                                                activity.get().startActivity(intent);
                                            }
                                        }).isCancelable(false).show();
                            }else{
                                if(GlobalData.isRequireRelogin()){
                                    DialogManager.showForceExitAlert(context,context.getString(R.string.msgLogout));
                                    return;
                                }
                                final NiftyDialogBuilder dialog= NiftyDialogBuilder.getInstance(activity.get());
                                dialog.withTitle(activity.get().getString(R.string.error_capital)).withMessage(results.get(0)).
                                        withButton1Text("OK").setButton1Click(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View paramView) {
                                                dialog.dismiss();
                                                CustomerFragment.doBack(activity.get());
                                                GlobalData.getSharedGlobalData().setButtonClicked(false);
                                            }
                                        }).isCancelable(true).show();
                            }
                        }
                        catch (Exception e2){
                            Toaster.warning(activity.get(), results.get(0));
                            error = true;
                        }
                    }
                }
            }

            if(error) {
                listener.OnError(true);
//                buttonSend.setClickable(true);
//                buttonSelectPhoto.setClickable(true);
            }
            GlobalData.getSharedGlobalData().setButtonClicked(false);
        }
    }
}
