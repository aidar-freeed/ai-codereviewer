package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.InstallmentScheduleApi;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.fragments.InstallmentScheduleDetailFragment;
import com.adins.mss.coll.fragments.InstallmentScheduleFragment;
import com.adins.mss.coll.models.InstallmentScheduleResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.InstallmentScheduleDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import org.acra.ACRA;

import java.io.IOException;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class InstallmentScheduleView extends ViewManager {
    private Activity activity;
    private Context context;
    private String taskId;
    private InstallmentScheduleResponse scheduleResponse;
    private List<InstallmentSchedule> installmentScheduleLocalList = null;
    public static InstallmentSchedule detailInstallmentSchedule;
    ImageButton imageButton;

    public InstallmentScheduleView(Activity activity) {
        super(activity);
        this.activity   = activity;
        this.context    = activity.getApplicationContext();
    }

    @Override
    public void onCreate() {
        Bundle bundle = activity.getIntent().getExtras();
        taskId = bundle.getString(Global.BUND_KEY_TASK_ID);

        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        if(taskH != null){
            installmentScheduleLocalList = InstallmentScheduleDataAccess.getAllByTask(context, taskH.getUuid_task_h());
//	        List<InstallmentSchedule> installmentScheduleLocalAll = InstallmentScheduleDataAccess.getAll(getApplicationContext());
//            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            loadData();

            imageButton = (ImageButton) activity.findViewById(R.id.imageBtnDownload);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //            	TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
                    //                int index = 1;
                    //                table.removeViews(index, table.getChildCount()-1);
                    //            	loadData();
                    imageButton.setEnabled(false);
                    imageButton.setClickable(false);
                    try {
                        saveData(context, scheduleResponse);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Insert Installment schedule Error. "+ e.getMessage()));
                    }
                }
            });
        }
    }

    protected void saveData(Context context, InstallmentScheduleResponse installmentSchedResp) {
        if(installmentSchedResp!=null && installmentSchedResp.getStatus().getCode()==0){
            List<InstallmentSchedule> installmentScheduleList = installmentSchedResp.getInstallmentScheduleList();
            if(installmentScheduleList!=null && installmentScheduleList.size()>0){
                TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                InstallmentScheduleDataAccess.delete(context, taskH.getUuid_task_h());
                //InstallmentScheduleDataAccess.add(getApplicationContext(), installmentScheduleList);
//    	        	List<InstallmentSchedule> installmentScheduleLocalAll = InstallmentScheduleDataAccess.getAll(getApplicationContext());
                for(InstallmentSchedule installmentSchedule : installmentScheduleList){
                    if (installmentSchedule.getUuid_installment_schedule() == null){
                        installmentSchedule.setUuid_installment_schedule(Tool.getUUID());
                    }
                    installmentSchedule.setUuid_task_h(taskH.getUuid_task_h());
//    	        		InstallmentScheduleDataAccess.addOrReplace(context, installmentSchedule);
                }
                InstallmentScheduleDataAccess.addOrReplace(context, installmentScheduleList);

            }
            Toaster.warning(context, "Data Saved");
        }else{
            Toaster.warning(context, "Cannot saved data if no data or no internet connection");
        }
    }

    public void loadData() {
        new AsyncTask<Void, Void, InstallmentScheduleResponse>() {
            private ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity,
                        "", context.getString(R.string.progressWait), true);

            }
            @Override
            protected InstallmentScheduleResponse doInBackground(Void... params) {
                InstallmentScheduleApi api = new InstallmentScheduleApi(activity);
                try {

                    //bong 21 mei 15 - check internet connection
//                	if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                    if(isInternetConnected(context)){
                        return api.request(taskId);
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(InstallmentScheduleResponse installmentScheduleResponse) {
                super.onPostExecute(installmentScheduleResponse);
                if (progressDialog!=null&&progressDialog.isShowing()){
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }
                if (installmentScheduleResponse == null) {
//                    NiftyDialogBuilder.getInstance(InstallmentScheduleFragment.this)
//                            .withMessage("Unable to retrieve data from the server - offline mode")
//                            .withTitle("Server Error")
//                            .withButton1Text("Close")
//                            .setButton1Click(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    //finish();
//                                	NiftyDialogBuilder.getInstance(InstallmentScheduleFragment.this).dismiss();
//                                }
//                            })
//                            .show();
//                    return;

                    //bong 25 mei 15 - display local data
                    {
                        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                        if(taskH!=null){
                            List<InstallmentSchedule> installmentScheduleList = InstallmentScheduleDataAccess.getAllByTask(context, taskH.getUuid_task_h());
                            if(installmentScheduleList!=null){
                                TextView agreementNumber = (TextView) activity.findViewById(R.id.agreementNumber);
                                agreementNumber.setText(installmentScheduleList.get(0).getAgreement_no());

                                TableLayout table = (TableLayout) activity.findViewById(R.id.tableHeaders);
                                int index = 1;

                                for (InstallmentSchedule item : installmentScheduleList) {

                                    View row = LayoutInflater.from(activity).inflate(R.layout.view_row_installment_schedule, table, false);
                                    TextView no = (TextView) row.findViewById(R.id.no);
                                    TextView dueDate = (TextView) row.findViewById(R.id.dueDate);
                                    TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);
                                    TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);

//            	                    InstallmentScheduleItem installmentScheduleItem = new InstallmentScheduleItem();
//            	                    installmentScheduleItem.setAgreementNo(item.getAgreement_no());
//            	                    installmentScheduleItem.setBranchCode(item.getBranch_code());
//            	                    installmentScheduleItem.setDtmCrt(item.getDtm_crt());
//            	                    installmentScheduleItem.setDueDate(item.getDue_date());
//            	                    installmentScheduleItem.setInstallmentAmount(item.getInstallment_amount());
//            	                    installmentScheduleItem.setInstallmentNo(item.getInstallment_no());
//            	                    installmentScheduleItem.setInterestAmound(item.getInterest_amound());
//            	                    installmentScheduleItem.setLcAdminFee(item.getLc_admin_fee());
//            	                    installmentScheduleItem.setLcAdminFeePaid(item.getLc_admin_fee_paid());
//            	                    installmentScheduleItem.setLcAdminFeeWaive(item.getLc_admin_fee_waive());
//            	                    installmentScheduleItem.setLcDays(item.getLc_days());
//            	                    installmentScheduleItem.setLcInstlAmount(item.getLc_instl_amount());
//            	                    installmentScheduleItem.setLcInstlPaid(item.getLc_instl_paid());
//            	                    installmentScheduleItem.setLcInstlWaived(item.getLc_instl_waived());
//            	                    installmentScheduleItem.setOsInterestAmount(item.getOs_interest_amount());
//            	                    installmentScheduleItem.setOsPrincipalAmount(item.getOs_principal_amount());
//            	                    installmentScheduleItem.setPrincipalAmount(item.getPrincipal_amount());
//            	                    installmentScheduleItem.setUsrCrt(item.getUsr_crt());
//            	                    installmentScheduleItem.setUuidTaskId(item.getUuid_task_h());
                                    //row.setTag(item);
                                    row.setTag(item);

                                    no.setText(String.valueOf(index++));
                                    dueDate.setText(Formatter.formatDate(item.getDue_date(), Global.DATE_STR_FORMAT));

                                    String amtIns = item.getInstallment_amount();
                                    if(amtIns!=null && amtIns.length()>3){
                                        String part1 = amtIns.substring(amtIns.length()-3);
                                        if(part1.substring(0, 1).equals("."))
                                        {
                                            amountInstallment.setGravity(Gravity.RIGHT);
                                        }
                                    }


                                    String amtPaid = item.getInstallment_paid_amount();
                                    if(amtPaid!=null && amtPaid.length()>3){
                                        String part2 = amtPaid.substring(amtPaid.length()-3);
                                        if(part2.substring(0, 1).equals("."))
                                        {
                                            amountPaid.setGravity(Gravity.RIGHT);
                                        }
                                    }


                                    amountInstallment.setText(item.getInstallment_amount());
                                    amountPaid.setText(item.getInstallment_paid_amount());

                                    if (index % 2 == 1) {
                                        row.setBackgroundResource(R.color.tv_gray_light);
                                    } else if (index % 2 == 0) {
                                        row.setBackgroundResource(R.color.tv_gray);
                                    }

                                    row.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            detailInstallmentSchedule = (InstallmentSchedule) v.getTag();
                                            Intent intent = new Intent(activity, InstallmentScheduleDetailFragment.class);
//            	                            intent.putExtra(InstallmentScheduleDetailFragment.INSTALLMENT_SCHEDULE_DETAIL, detail);
                                            activity.startActivity(intent);
                                        }
                                    });

                                    table.addView(row);

                                }
                            }else{
                                NiftyDialogBuilder.getInstance(context)
                                        .withMessage(context.getString(R.string.no_data_found_offline))
                                        .withTitle(context.getString(R.string.warning_capital))
                                        .withIcon(android.R.drawable.ic_dialog_alert)
                                        .withButton1Text(context.getString(R.string.btnClose))
                                        .setButton1Click(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //finish();
                                                NiftyDialogBuilder.getInstance(context).dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                }

//                else{
                //if (installmentScheduleResponse != null) {
                else if (installmentScheduleResponse.getStatus().getCode() != 0) {
                    NiftyDialogBuilder.getInstance(context)
                            .withMessage(installmentScheduleResponse.getStatus().getMessage())
                            .withTitle(context.getString(R.string.server_error))
                            .withButton1Text(context.getString(R.string.btnClose))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.finish();
                                }
                            })
                            .show();
                    return;
                }
//                }

//              //bong 21 mei 15 - if failed get data from server - use local data
//                if(installmentScheduleResponse==null){
//                	installmentScheduleResponse = new InstallmentScheduleResponse();
//                	installmentScheduleResponse.setInstallmentScheduleList(installmentScheduleLocalList);
//                	installmentScheduleResponse.setAgreementNo("");
//                }

                else {
                    scheduleResponse = installmentScheduleResponse;
                    TextView agreementNumber = (TextView) activity.findViewById(R.id.agreementNumber);
                    agreementNumber.setText(installmentScheduleResponse.getAgreementNo());

                    TableLayout table = (TableLayout) activity.findViewById(R.id.tableHeaders);
                    int index = 1;

                    if(installmentScheduleResponse.getInstallmentScheduleList().size()==0){
                        NiftyDialogBuilder.getInstance(activity)
                                .withMessage(R.string.no_data_from_server)
                                .withTitle(context.getString(R.string.info_capital))
                                .withButton1Text(context.getString(R.string.btnClose))
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        activity.finish();
                                    }
                                })
                                .show();
                    }

                    for (InstallmentSchedule item : installmentScheduleResponse.getInstallmentScheduleList()) {

                        View row = LayoutInflater.from(context).inflate(R.layout.view_row_installment_schedule, table, false);
                        TextView no = (TextView) row.findViewById(R.id.no);
                        TextView dueDate = (TextView) row.findViewById(R.id.dueDate);
                        TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);
                        TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);

	                    /*InstallmentScheduleItem installmentScheduleItem = new InstallmentScheduleItem();
	                    installmentScheduleItem.setAgreementNo(item.getAgreement_no());
	                    installmentScheduleItem.setBranchCode(item.getBranch_code());
	                    installmentScheduleItem.setDtmCrt(item.getDtm_crt());
	                    installmentScheduleItem.setDueDate(item.getDue_date());
	                    installmentScheduleItem.setInstallmentAmount(item.getInstallment_amount());
	                    installmentScheduleItem.setInstallmentNo(item.getInstallment_no());
	                    installmentScheduleItem.setInterestAmound(item.getInterest_amound());
	                    installmentScheduleItem.setLcAdminFee(item.getLc_admin_fee());
	                    installmentScheduleItem.setLcAdminFeePaid(item.getLc_admin_fee_paid());
	                    installmentScheduleItem.setLcAdminFeeWaive(item.getLc_admin_fee_waive());
	                    installmentScheduleItem.setLcDays(item.getLc_days());
	                    installmentScheduleItem.setLcInstlAmount(item.getLc_instl_amount());
	                    installmentScheduleItem.setLcInstlPaid(item.getLc_instl_paid());
	                    installmentScheduleItem.setLcInstlWaived(item.getLc_instl_waived());
	                    installmentScheduleItem.setOsInterestAmount(item.getOs_interest_amount());
	                    installmentScheduleItem.setOsPrincipalAmount(item.getOs_principal_amount());
	                    installmentScheduleItem.setPrincipalAmount(item.getPrincipal_amount());
	                    installmentScheduleItem.setUsrCrt(item.getUsr_crt());
	                    installmentScheduleItem.setUuidTaskId(item.getUuid_task_h());*/
                        //row.setTag(item);
                        row.setTag(item);

                        no.setText(String.valueOf(index++));
                        dueDate.setText(Formatter.formatDate(item.getDue_date(), Global.DATE_STR_FORMAT));
                        amountInstallment.setText(item.getInstallment_amount());
                        amountPaid.setText(item.getInstallment_paid_amount());

                        String amtIns = item.getInstallment_amount();
                        if(amtIns!=null && amtIns.length()>3){
                            String part1 = amtIns.substring(amtIns.length()-3);
                            if(part1.substring(0, 1).equals("."))
                            {
                                amountInstallment.setGravity(Gravity.RIGHT);
                            }
                        }


                        String amtPaid = item.getInstallment_paid_amount();
                        if(amtPaid!=null && amtPaid.length()>3){
                            String part2 = amtPaid.substring(amtPaid.length()-3);
                            if(part2.substring(0, 1).equals("."))
                            {
                                amountPaid.setGravity(Gravity.RIGHT);
                            }
                        }

                        if (index % 2 == 1) {
                            row.setBackgroundResource(R.color.tv_gray_light);
                        } else if (index % 2 == 0) {
                            row.setBackgroundResource(R.color.tv_gray);
                        }

                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detailInstallmentSchedule = (InstallmentSchedule) v.getTag();
                                Intent intent = new Intent(context, InstallmentScheduleDetailFragment.class);
//	                            intent.putExtra(InstallmentScheduleDetailFragment.INSTALLMENT_SCHEDULE_DETAIL, detail);
                                activity.startActivity(intent);
                            }
                        });

                        table.addView(row);

                    }
                }

            }
        }.execute();
    }

}
