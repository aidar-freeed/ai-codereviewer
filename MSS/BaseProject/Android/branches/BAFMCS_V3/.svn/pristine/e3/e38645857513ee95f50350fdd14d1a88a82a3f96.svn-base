package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.PaymentHistoryApi;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.fragments.PaymentHistoryDetailFragment;
import com.adins.mss.coll.fragments.PaymentHistoryFragment;
import com.adins.mss.coll.models.PaymentHistoryHBean;
import com.adins.mss.coll.models.PaymentHistoryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.dao.PaymentHistoryH;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.PaymentHistoryDDataAccess;
import com.adins.mss.foundation.db.dataaccess.PaymentHistoryHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class PaymentHistoryView extends ViewManager implements IShowError {
    private Activity activity;
    private Intent intent;
    private NetworkInfo activeNetworkInfo;
    private ConnectivityManager connectivityManager;
    private List<PaymentHistoryH> paymentHistoryHLocalList = null;
    private List<PaymentHistoryD> paymentHistoryDLocalList = null;
    private String transactionCode;
    PaymentHistoryResponse historyResponse;
    ImageButton imageButton;
    String taskId;
    private ErrorMessageHandler errorMessageHandler;

    public PaymentHistoryView(Activity activity) {
        super(activity);
        this.activity = activity;
        this.intent   = activity.getIntent();
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    public void onCreate() {
        taskId = intent.getStringExtra(Global.BUND_KEY_TASK_ID);

        initialize();
        imageButton = (ImageButton) activity.findViewById(R.id.imageBtnDownload);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            	TableLayout table = (TableLayout) findViewById(R.id.tablePaymentHeaders);
//                int index = 1;
//                table.removeViews(index, table.getChildCount()-1);
//                initialize();
                imageButton.setEnabled(false);
                imageButton.setClickable(false);
                saveData(activity, historyResponse);
            }
        });
    }

    protected void saveData(Context context, PaymentHistoryResponse paymentHistoryResp) {
//    	TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
//    	PaymentHistoryHDataAccess.delete(context, taskH.getUuid_task_h());
//		PaymentHistoryDDataAccess.delete(context, taskH.getUuid_task_h());

        if(paymentHistoryResp!=null && paymentHistoryResp.getStatus().getCode()==0){
            List<PaymentHistoryHBean> paymentHistoryHList = paymentHistoryResp.getPaymentHistoryHList();
            //List<PaymentHistoryH> paymentHistoryHList = paymentHistoryResp.getPaymentHistoryHList();
            //List<PaymentHistoryD> paymentHistoryDList = paymentHistoryResp.getPaymentHistoryDList();
            //List<PaymentHistoryH> paymentHistoryHLocalList = null;
            //List<PaymentHistoryD> paymentHistoryDLocalList = null;
            if(paymentHistoryHList!=null){

                TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                if(paymentHistoryHLocalList!=null && paymentHistoryHLocalList.size()>0){
                    PaymentHistoryHDataAccess.delete(context, taskH.getUuid_task_h());

                    if(paymentHistoryDLocalList!=null && paymentHistoryDLocalList.size()>0){
                        PaymentHistoryDDataAccess.delete(context, taskH.getUuid_task_h());
                    }
                }
                for(PaymentHistoryHBean paymentHistoryHBean : paymentHistoryHList){
                    String agreementNo = paymentHistoryHBean.getPaymentHistoryH().getAgreement_no();
                    if(agreementNo!=null && agreementNo.length()>0){
                        //paymentHistoryHLocalList = PaymentHistoryHDataAccess.getAll(context, agreementNo);
                        //paymentHistoryDLocalList = PaymentHistoryDDataAccess.getAll(context, uuidTaskH);
                        //if(paymentHistoryDLocalList.size()>0){
                        //	PaymentHistoryDDataAccess.delete(context, uuidTaskH);
                        //}
//            			taskH.setAppl_no(agreementNo);
//            			TaskHDataAccess.addOrReplace(context, taskH);
//            			if(paymentHistoryHBean.getPaymentHistoryDList()!=null){
//            				if(paymentHistoryHBean.getPaymentHistoryDList().size()>0){
//            					for(PaymentHistoryD paymentHistoryD : paymentHistoryHBean.getPaymentHistoryDList()){
//            						PaymentHistoryDDataAccess.delete(context, paymentHistoryD);
//            					}
//            					PaymentHistoryDDataAccess.delete(context, paymentHistoryHBean.getPaymentHistoryH().getUuid_task_h());
//
//            				}
//            			}


                        if(paymentHistoryHBean.getPaymentHistoryH().getUuid_payment_history_h()==null){
                            paymentHistoryHBean.getPaymentHistoryH().setUuid_payment_history_h(Tool.getUUID());
                        }
                        paymentHistoryHBean.getPaymentHistoryH().setUuid_task_h(taskH.getUuid_task_h());
                        PaymentHistoryHDataAccess.addOrReplace(context, paymentHistoryHBean.getPaymentHistoryH());

                        if(paymentHistoryHBean.getPaymentHistoryDList()!=null){
                            if(paymentHistoryHBean.getPaymentHistoryDList().size()>0){
                                for(PaymentHistoryD historyD : paymentHistoryHBean.getPaymentHistoryDList()){
                                    historyD.setUuid_payment_history_d(Tool.getUUID());
                                    historyD.setUuid_task_h(taskH.getUuid_task_h());
                                    historyD.setUuid_payment_history_h(paymentHistoryHBean.getPaymentHistoryH().getUuid_payment_history_h());
                                    PaymentHistoryDDataAccess.addOrReplace(context, historyD);
                                }
                            }
                        }
                    }
                }
            }
            Toast.makeText(context, context.getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        }else{
            Toaster.warning(context, activity.getString(R.string.failed_save_data));
        }
        imageButton.setEnabled(true);
        imageButton.setClickable(true);
    }

    private void initialize() {
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);
        paymentHistoryHLocalList = PaymentHistoryHDataAccess.getAllbyTask(activity, taskH.getUuid_task_h());
        paymentHistoryDLocalList = PaymentHistoryDDataAccess.getAll(activity, taskH.getUuid_task_h());

        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        new AsyncTask<String, Void, PaymentHistoryResponse>() {
            private ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity,
                        "", activity.getString(R.string.progressWait), true);

            }
            @Override
            protected PaymentHistoryResponse doInBackground(String... params) {
                PaymentHistoryApi api = new PaymentHistoryApi(activity);
                try {
                    if(Tool.isInternetconnected(activity)){
                        return api.request(params[0]);
                    }else{
                        return null;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PaymentHistoryResponse paymentHistoryResponse) {
                super.onPostExecute(paymentHistoryResponse);
                if (progressDialog!=null&&progressDialog.isShowing()){
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if(!GlobalData.isRequireRelogin()) {
                    if (paymentHistoryResponse == null) {
//                    NiftyDialogBuilder.getInstance(PaymentHistoryFragment.this)
//                            .withMessage("Unable to retrieve data from the server - offline mode")
//                            .withTitle("Server Error")
//                            .withButton1Text("Close")
//                            .setButton1Click(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    //finish();
//                                	NiftyDialogBuilder.getInstance(PaymentHistoryFragment.this).dismiss();
//                                }
//                            })
//                            .show();
                        //return;

                        //bong 25 mei 15 - display local data
                        {
                            TaskH taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);
                            List<PaymentHistoryH> paymentHistoryHeaderList = null;
                            if (null != taskH) {
                                //paymentHistoryHeaderList = PaymentHistoryHDataAccess.getAll(getApplicationContext(), taskH.getUuid_task_h());
                                paymentHistoryHeaderList = PaymentHistoryHDataAccess.getAllbyTask(activity, taskH.getUuid_task_h());
                                if (paymentHistoryHeaderList != null && !paymentHistoryHeaderList.isEmpty()) {
                                    TextView agreementNumber = (TextView) activity.findViewById(R.id.agreementNumber);
                                    //agreementNumber.setText(paymentHistoryResponse.getAgreementNo());
                                    agreementNumber.setText(paymentHistoryHeaderList.get(0).getAgreement_no());

                                    List<PaymentHistoryHBean> paymentHistoryHeaderBeanList = new ArrayList<PaymentHistoryHBean>();

                                    for (PaymentHistoryH paymentHistoryH : paymentHistoryHeaderList) {
                                        PaymentHistoryHBean paymentHistoryHBean = new PaymentHistoryHBean();
                                        List<PaymentHistoryD> paymentHistoryDList = PaymentHistoryDDataAccess.getAllByHistoryH(activity, paymentHistoryH.getUuid_payment_history_h());
                                        paymentHistoryHBean.setPaymentHistoryH(paymentHistoryH);
                                        paymentHistoryHBean.setPaymentHistoryDList(paymentHistoryDList);
                                        paymentHistoryHeaderBeanList.add(paymentHistoryHBean);
                                    }

                                    TableLayout table = (TableLayout) activity.findViewById(R.id.tablePaymentHeaders);
                                    int index = 1;
                                    for (PaymentHistoryHBean header : paymentHistoryHeaderBeanList) {
                                        View row = LayoutInflater.from(activity).inflate(R.layout.view_row_payment_history, table, false);
                                        List<PaymentHistoryD> details = new ArrayList<PaymentHistoryD>();
                                        if (header.getPaymentHistoryDList() != null) {
                                            for (PaymentHistoryD detail : header.getPaymentHistoryDList()) {
                                                if (detail.getUuid_task_h().equals(header.getPaymentHistoryH().getUuid_task_h())) {
                                                    detail.setReceipt_no(header.getPaymentHistoryH().getReceipt_no());
                                                    detail.setValue_date(header.getPaymentHistoryH().getValue_date());
                                                    detail.setPosting_date(header.getPaymentHistoryH().getPost_date());
                                                    detail.setPayment_amount(header.getPaymentHistoryH().getPayment_amount());
                                                    detail.setInstallment_amount(header.getPaymentHistoryH().getInstallment_amount());
                                                    detail.setInstallment_number(header.getPaymentHistoryH().getInstallment_number());
                                                    detail.setWop_code(header.getPaymentHistoryH().getWop_code());
                                                    detail.setPayment_amount(header.getPaymentHistoryH().getPayment_amount());
                                                    detail.setInstallment_amount(header.getPaymentHistoryH().getInstallment_amount());
                                                    detail.setInstallment_number(header.getPaymentHistoryH().getInstallment_number());
                                                    detail.setTransaction_type(header.getPaymentHistoryH().getTransaction_type());
                                                    detail.setWop_code(header.getPaymentHistoryH().getWop_code());
                                                    details.add(detail);
                                                }
                                            }
                                        }


                                   /* List<PaymentHistoryResponseDetail> paymentHistoryResponseDetailList = new ArrayList<PaymentHistoryResponseDetail>();
                                    for(PaymentHistoryD detail : details){
                                    	PaymentHistoryResponseDetail paymentHistoryResponseDetail = new PaymentHistoryResponseDetail();
                                        paymentHistoryResponseDetail.setDtmCrt(detail.getDtm_crt());
                                        paymentHistoryResponseDetail.setDtmUpd(detail.getDtm_upd());
                                        paymentHistoryResponseDetail.setOsAmountOd(detail.getOs_amount_od());
                                        paymentHistoryResponseDetail.setPaymentAllocationName(detail.getPayment_allocation_name());
                                        paymentHistoryResponseDetail.setReceiveAmount(detail.getReceive_amount());
                                        paymentHistoryResponseDetail.setStatus(null);
                                        paymentHistoryResponseDetail.setUnstructured(null);
                                        paymentHistoryResponseDetail.setUsrCrt(detail.getUsr_crt());
                                        paymentHistoryResponseDetail.setUsrUpd(detail.getUsr_upd());
                                        paymentHistoryResponseDetail.setUuidTaskId(detail.getUuid_task_h());
                                        paymentHistoryResponseDetail.setUuidViewPaymentHistoryD(detail.getUuid_payment_history_d());
                                        paymentHistoryResponseDetailList.add(paymentHistoryResponseDetail);
                                    }*/
                                        //row.setTag(details);
                                        row.setTag(R.string.tag_paymenthistoryDetail, details);
                                        row.setTag(R.string.tag_transactioncode, header.getPaymentHistoryH().getReceipt_no());

                                        TextView no = (TextView) row.findViewById(R.id.no);
                                        TextView transactionCode = (TextView) row.findViewById(R.id.transactionCode);
                                        TextView postingDate = (TextView) row.findViewById(R.id.postingDate);
                                        TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);
                                        TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);

                                        no.setText(String.valueOf(index++));
                                        transactionCode.setText(header.getPaymentHistoryH().getReceipt_no());
                                        postingDate.setText(Formatter.formatDate(header.getPaymentHistoryH().getPost_date(), Global.DATE_STR_FORMAT));

                                        String amtIns = header.getPaymentHistoryH().getInstallment_amount();
                                        if (amtIns != null && amtIns.length() > 0) {
                                            String part1 = amtIns.substring(amtIns.length() - 3);
                                            if (part1.substring(0, 1).equals(".")) {
                                                amountInstallment.setGravity(Gravity.RIGHT);
                                            }
                                        }


                                        String amtPaid = header.getPaymentHistoryH().getPayment_amount();
                                        if (amtPaid != null && amtPaid.length() > 0) {
                                            String part2 = amtPaid.substring(amtPaid.length() - 3);
                                            if (part2.substring(0, 1).equals(".")) {
                                                amountPaid.setGravity(Gravity.RIGHT);
                                            }
                                        }


                                        amountPaid.setText(header.getPaymentHistoryH().getPayment_amount());
                                        amountInstallment.setText(header.getPaymentHistoryH().getInstallment_amount());

//                                    if (index % 2 == 1) {
//                                        row.setBackgroundResource(R.color.tv_gray_light);
//                                    }else if(index % 2 == 0){
//                                        row.setBackgroundResource(R.color.tv_gray);
//                                    }

                                        row.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                            List<PaymentHistoryD> details =
//                                                    (List<PaymentHistoryD>) v.getTag();
//                                            openDetails((Serializable) details);
                                                gotoDetails(v);
                                            }
                                        });

                                        table.addView(row);
                                    }
                                } else {
                                    errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                            , activity.getString(R.string.no_data_found_offline)
                                            , ErrorMessageHandler.DIALOG_TYPE);
                                }
                            }
                        }


                    }
//                else
//                {
                    else if (paymentHistoryResponse.getStatus().getCode() != 0) {
                        errorMessageHandler.processError(activity.getString(R.string.server_error)
                                , paymentHistoryResponse.getStatus().getMessage()
                                , ErrorMessageHandler.DIALOG_TYPE);
                        return;
                    }
//                }
                    else {
                        historyResponse = paymentHistoryResponse;
                        TextView agreementNumber = (TextView) activity.findViewById(R.id.agreementNumber);
                        agreementNumber.setText(paymentHistoryResponse.getAgreementNo());

                        TableLayout table = (TableLayout) activity.findViewById(R.id.tablePaymentHeaders);
                        int index = 1;
                        if (paymentHistoryResponse.getPaymentHistoryHList().size() == 0) {
                            errorMessageHandler.processError(activity.getString(R.string.info_capital)
                                    , activity.getString(R.string.no_data_from_server)
                                    , ErrorMessageHandler.DIALOG_TYPE);
                        }
                        for (PaymentHistoryHBean header : paymentHistoryResponse.getPaymentHistoryHList()) {
                            View row = LayoutInflater.from(activity).inflate(R.layout.view_row_payment_history, table, false);
                            List<PaymentHistoryD> details = new ArrayList<PaymentHistoryD>();
                            for (PaymentHistoryD detail : header.getPaymentHistoryDList()) {
                                //if (detail.getUuid_task_h().equals(header.getPaymentHistoryH().getUuid_task_h())) {
                                detail.setReceipt_no(header.getPaymentHistoryH().getReceipt_no());
                                detail.setValue_date(header.getPaymentHistoryH().getValue_date());
                                detail.setPosting_date(header.getPaymentHistoryH().getPost_date());
                                detail.setPayment_amount(header.getPaymentHistoryH().getPayment_amount());
                                detail.setInstallment_amount(header.getPaymentHistoryH().getInstallment_amount());
                                detail.setInstallment_number(header.getPaymentHistoryH().getInstallment_number());
                                detail.setWop_code(header.getPaymentHistoryH().getWop_code());
                                detail.setPayment_amount(header.getPaymentHistoryH().getPayment_amount());
                                detail.setInstallment_amount(header.getPaymentHistoryH().getInstallment_amount());
                                detail.setInstallment_number(header.getPaymentHistoryH().getInstallment_number());
                                detail.setWop_code(header.getPaymentHistoryH().getWop_code());
                                detail.setTransaction_type(header.getPaymentHistoryH().getTransaction_type());
                                details.add(detail);
                                //}
                            }

/*                        List<PaymentHistoryD> paymentHistoryResponseDetailList = new ArrayList<PaymentHistoryD>();
                        for(PaymentHistoryD detail : details){
                        	PaymentHistoryResponseDetail paymentHistoryResponseDetail = new PaymentHistoryResponseDetail();
                            paymentHistoryResponseDetail.setDtmCrt(detail.getDtm_crt());
                            paymentHistoryResponseDetail.setDtmUpd(detail.getDtm_upd());
                            paymentHistoryResponseDetail.setOsAmountOd(detail.getOs_amount_od());
                            paymentHistoryResponseDetail.setPaymentAllocationName(detail.getPayment_allocation_name());
                            paymentHistoryResponseDetail.setReceiveAmount(detail.getReceive_amount());
                            paymentHistoryResponseDetail.setStatus(paymentHistoryResponse.getStatus());
                            paymentHistoryResponseDetail.setUnstructured(paymentHistoryResponse.getUnstructured());
                            paymentHistoryResponseDetail.setUsrCrt(detail.getUsr_crt());
                            paymentHistoryResponseDetail.setUsrUpd(detail.getUsr_upd());
                            paymentHistoryResponseDetail.setUuidTaskId(detail.getUuid_task_h());
                            paymentHistoryResponseDetail.setUuidViewPaymentHistoryD(detail.getUuid_payment_history_d());
                            paymentHistoryResponseDetailList.add(paymentHistoryResponseDetail);
                        }*/
                            //row.setTag(details);
                            row.setTag(R.string.tag_paymenthistoryDetail, details);
                            row.setTag(R.string.tag_transactioncode, header.getPaymentHistoryH().getReceipt_no());

                            TextView no = (TextView) row.findViewById(R.id.no);
                            TextView transactionCode = (TextView) row.findViewById(R.id.transactionCode);
                            TextView postingDate = (TextView) row.findViewById(R.id.postingDate);
                            TextView amountPaid = (TextView) row.findViewById(R.id.amountPaid);
                            TextView amountInstallment = (TextView) row.findViewById(R.id.amountInstallment);

                            no.setText(String.valueOf(index++));
                            transactionCode.setText(header.getPaymentHistoryH().getTransaction_type());
                            postingDate.setText(null != header.getPaymentHistoryH().getPost_date() ?
                                    Formatter.formatDate(header.getPaymentHistoryH().getPost_date(), Global.DATE_STR_FORMAT) : "-");
                            amountPaid.setText(header.getPaymentHistoryH().getPayment_amount());
                            amountInstallment.setText(header.getPaymentHistoryH().getInstallment_amount());

                            String amtIns = header.getPaymentHistoryH().getInstallment_amount();
                            if (amtIns != null && amtIns.length() > 0) {
                                String part1 = amtIns.substring(amtIns.length() - 3);
                                if (part1.substring(0, 1).equals(".")) {
                                    amountInstallment.setGravity(Gravity.RIGHT);
                                }
                            }


                            String amtPaid = header.getPaymentHistoryH().getPayment_amount();
                            if (amtPaid != null && amtPaid.length() > 0) {
                                String part2 = amtPaid.substring(amtPaid.length() - 3);
                                if (part2.substring(0, 1).equals(".")) {
                                    amountPaid.setGravity(Gravity.RIGHT);
                                }
                            }

//                        if (index % 2 == 1) {
//                            row.setBackgroundResource(R.color.tv_gray_light);
//                        }else if(index % 2 == 0){
//                            row.setBackgroundResource(R.color.tv_gray);
//                        }

                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                List<PaymentHistoryD> details =
//                                        (List<PaymentHistoryD>) v.getTag();
//                                openDetails((Serializable) details);
                                    gotoDetails(v);
                                }
                            });

                            table.addView(row);
                        }
                    }
                }
            }
        }.execute(taskId);
    }

    private void openDetails(Serializable details) {
        Intent intent = new Intent(activity, PaymentHistoryDetailFragment.class);
        intent.putExtra(PaymentHistoryDetailFragment.PAYMENT_HISTORY_DETAIL, details);
        activity.startActivity(intent);
    }

    private void gotoDetails(View v) {
        PaymentHistoryFragment.details = (List<PaymentHistoryD>) v.getTag(R.string.tag_paymenthistoryDetail);
        transactionCode = (String)v.getTag(R.string.tag_transactioncode);
        Intent intent = new Intent(activity, PaymentHistoryDetailFragment.class);
        intent.putExtra(PaymentHistoryDetailFragment.BUND_KEY_TRANSACTIONCODE, transactionCode);
        activity.startActivity(intent);
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        NiftyDialogBuilder.getInstance(activity)
                .withMessage(errorMsg)
                .withTitle(errorSubject)
                .isCancelableOnTouchOutside(false)
                .withButton1Text(activity.getString(R.string.btnClose))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //finish();
                        NiftyDialogBuilder.getInstance(activity).dismiss();
                    }
                })
                .show();
    }
}
