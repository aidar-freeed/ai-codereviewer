package com.adins.mss.coll.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.JsonRequestPdfReceiptHistory;
import com.adins.mss.base.dynamicform.JsonResponsePdfReceiptHistory;
import com.adins.mss.base.pdfrenderer.ViewPdfRendererFragment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.ReceiptHistoryApi;
import com.adins.mss.coll.models.ReceiptHistoryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.ReceiptHistory;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.ReceiptHistoryDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.Base64;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReceiptHistoryFragment extends FragmentActivity {

    public ReceiptHistoryResponse rHistoryResponse;
    private String taskId;
    private String agreementNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_receipt_history);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        Bundle bundle = getIntent().getExtras();
        taskId = bundle.getString(Global.BUND_KEY_TASK_ID);
        agreementNo = bundle.getString(Global.BUND_KEY_AGREEMENT_NO);

        loadData();
    }

    public void saveData(Context context, ReceiptHistoryResponse receiptHistoryResponse) {
        List<ReceiptHistory> receiptHistoryList = receiptHistoryResponse.getReceiptHistoryList();
        if (receiptHistoryList != null && receiptHistoryList.size() > 0) {
            TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
            ReceiptHistoryDataAccess.delete(context, taskH.getTask_id());
            for (ReceiptHistory receiptHistory : receiptHistoryList) {
                receiptHistory.setUuid_task_h(taskH.getTask_id());
                receiptHistory.setUuid_receipt_history(Tool.getUUID());
                receiptHistory.setAgreement_no(agreementNo);
            }
            ReceiptHistoryDataAccess.add(getApplicationContext(), receiptHistoryList);
        }
    }

    public void loadData() {
        new AsyncTask<Void, Void, ReceiptHistoryResponse>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(ReceiptHistoryFragment.this,
                        "", getString(R.string.progressWait), true);

            }

            @Override
            protected ReceiptHistoryResponse doInBackground(Void... params) {
                ReceiptHistoryApi api = new ReceiptHistoryApi(ReceiptHistoryFragment.this);
                try {

                    //bong 21 mei 15 - check internet connection
                    if (Tool.isInternetconnected(getApplicationContext())) {
                        return api.request(agreementNo, taskId);
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(ReceiptHistoryResponse receiptHistoryResponse) {
                super.onPostExecute(receiptHistoryResponse);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                    }
                }

                if (receiptHistoryResponse == null) {
                    TaskH taskH = TaskHDataAccess.getOneTaskHeader(getApplicationContext(), taskId);
                    if (taskH != null) {
                        List<ReceiptHistory> receiptHistoryList = ReceiptHistoryDataAccess.getAllByTask(getApplicationContext(), taskH.getUuid_task_h());
                        if (receiptHistoryList != null) {
                            TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
                            int index = 1;

                            for (final ReceiptHistory item : receiptHistoryList) {
                                View row = LayoutInflater.from(ReceiptHistoryFragment.this).inflate(R.layout.view_row_receipt_history, table, false);
                                TextView contractNumber = (TextView) row.findViewById(R.id.contractNumber);
                                TextView receiptNumber = (TextView) row.findViewById(R.id.receiptNumber);
                                TextView paymentDate = (TextView) row.findViewById(R.id.paymentDate);
                                TextView file = (TextView) row.findViewById(R.id.file);
                                row.setTag(item);

                                contractNumber.setText(item.getAgreement_no());
                                receiptNumber.setText(item.getReceipt_no());
                                paymentDate.setText(item.getPayment_date() + "");
                                file.setText("(download)");
                                SpannableString spannableString = new SpannableString(file.getText());
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        String receiptNo = item.getReceipt_no();
                                        if (receiptNo.contains("/")) {
                                            receiptNo = receiptNo.replace("/", "");
                                        }
                                        getDocumentPDF(getApplicationContext(), item.getAgreement_no(), receiptNo);
                                    }
                                };
                                spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                file.setText(spannableString);
                                file.setMovementMethod(LinkMovementMethod.getInstance());
                                file.setHighlightColor(Color.TRANSPARENT);
                                if (index % 2 == 1) {
                                    row.setBackgroundResource(R.color.tv_gray_light);
                                } else if (index % 2 == 0) {
                                    row.setBackgroundResource(R.color.tv_gray);
                                }
                                table.addView(row);
                            }
                        } else {
                            NiftyDialogBuilder.getInstance(ReceiptHistoryFragment.this)
                                    .withMessage(getString(R.string.no_data_found_offline))
                                    .withTitle(getString(R.string.no_data_found_offline))
                                    .withIcon(android.R.drawable.ic_dialog_alert)
                                    .withButton1Text(getString(R.string.btnClose))
                                    .setButton1Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //finish();
                                            NiftyDialogBuilder.getInstance(ReceiptHistoryFragment.this).dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                } else if (receiptHistoryResponse.getStatus().getCode() != 0) {
                    NiftyDialogBuilder.getInstance(ReceiptHistoryFragment.this)
                            .withMessage(receiptHistoryResponse.getStatus().getMessage())
                            .withTitle(getString(R.string.server_error))
                            .withButton1Text(getString(R.string.btnClose))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            })
                            .show();
                    return;
                } else if (!receiptHistoryResponse.getStatusCode().equalsIgnoreCase("00")) {
                    NiftyDialogBuilder.getInstance(ReceiptHistoryFragment.this)
                            .withMessage(receiptHistoryResponse.getMessage())
                            .withTitle(getString(R.string.message))
                            .withButton1Text(getString(R.string.btnClose))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            })
                            .show();
                    return;
                } else {
                    rHistoryResponse = receiptHistoryResponse;
                    saveData(getApplicationContext(), rHistoryResponse);

                    TableLayout table = (TableLayout) findViewById(R.id.tableHeaders);
                    int index = 1;

                    for (final ReceiptHistory item : receiptHistoryResponse.getReceiptHistoryList()) {
                        View row = LayoutInflater.from(ReceiptHistoryFragment.this).inflate(R.layout.view_row_receipt_history, table, false);
                        TextView contractNumber = (TextView) row.findViewById(R.id.contractNumber);
                        TextView receiptNumber = (TextView) row.findViewById(R.id.receiptNumber);
                        TextView paymentDate = (TextView) row.findViewById(R.id.paymentDate);
                        TextView file = (TextView) row.findViewById(R.id.file);
                        row.setTag(item);

                        contractNumber.setText(rHistoryResponse.getAgreementNo());
                        receiptNumber.setText(item.getReceipt_no());
                        paymentDate.setText(item.getPayment_date() + "");
                        file.setText("(download)");
                        SpannableString spannableString = new SpannableString(file.getText());
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                String receiptNo = item.getReceipt_no();
                                if (receiptNo.contains("/")) {
                                    receiptNo = receiptNo.replace("/", "");
                                }
                                getDocumentPDF(getApplicationContext(), item.getAgreement_no(), receiptNo);
                            }
                        };
                        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        file.setText(spannableString);
                        file.setMovementMethod(LinkMovementMethod.getInstance());
                        file.setHighlightColor(Color.TRANSPARENT);
                        if (index % 2 == 1) {
                            row.setBackgroundResource(R.color.tv_gray_light);
                        } else if (index % 2 == 0) {
                            row.setBackgroundResource(R.color.tv_gray);
                        }
                        table.addView(row);
                    }
                    if (receiptHistoryResponse.getReceiptHistoryList().size() == 0) {
                        NiftyDialogBuilder.getInstance(ReceiptHistoryFragment.this)
                                .withMessage(R.string.no_data_from_server)
                                .withTitle(getString(R.string.info_capital))
                                .withButton1Text(getString(R.string.btnClose))
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                }

            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getDocumentPDF(final Context context, final String agreementNo, final String receiptNo) {
        new AsyncTask<Void, Void, JsonResponsePdfReceiptHistory>() {
            private ProgressDialog progressDialog;
            private String fileLocation;
            private String message;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(ReceiptHistoryFragment.this,
                        "", getString(R.string.progressWait), true);
            }

            @Override
            protected JsonResponsePdfReceiptHistory doInBackground(Void... arg0) {
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/bafmcspdf" + "/" + agreementNo + "_" + receiptNo + ".pdf";
                File fileOpen = new File(fileName);
                if (fileOpen.exists()) {
                    fileLocation = fileName;
                    JsonResponsePdfReceiptHistory responsePdfReceiptHistory = new JsonResponsePdfReceiptHistory();
                    JsonResponsePdfReceiptHistory.Status status = new JsonResponsePdfReceiptHistory.Status();
                    status.setCode(0);
                    responsePdfReceiptHistory.setStatusCode("00");
                    responsePdfReceiptHistory.setStatus(status);
                    return responsePdfReceiptHistory;
                } else {
                    try {
                        if (Tool.isInternetconnected(context)) {
                            JsonRequestPdfReceiptHistory requestPdfDocument = new JsonRequestPdfReceiptHistory();
                            requestPdfDocument.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                            requestPdfDocument.addImeiAndroidIdToUnstructured();
                            requestPdfDocument.setAgreementNo(agreementNo);
                            requestPdfDocument.setInvoiceNo(receiptNo);

                            String json = GsonHelper.toJson(requestPdfDocument);
                            String url = GlobalData.getSharedGlobalData().getURL_GET_RECEIPT_HISTORY_PDF();
                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();

                            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
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
                            if (null != serverResult) {
                                if (serverResult.isOK()) {
                                    JsonResponsePdfReceiptHistory responsePdfReceiptHistory = GsonHelper.fromJson(serverResult.getResult(), JsonResponsePdfReceiptHistory.class);
                                    if (responsePdfReceiptHistory.getStatus().getCode() == 0) {
                                        if (responsePdfReceiptHistory.getStatusCode().equals("200") || responsePdfReceiptHistory.getStatusCode().equals("00")) {
                                            String pathFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/bafmcspdf";
                                            File filePath = new File(pathFolder);
                                            if (!filePath.exists()) {
                                                filePath.mkdirs();
                                            }

                                            String uriString = filePath.getAbsolutePath() + "/" + agreementNo + "_" + receiptNo + ".pdf";
                                            fileLocation = uriString;

                                            try {
                                                File file = new File(uriString);
                                                byte[] pdfBytes = Base64.decode(responsePdfReceiptHistory.getPdf());
                                                FileOutputStream os = new FileOutputStream(file);
                                                os.write(pdfBytes);
                                                os.flush();
                                                os.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                message = "Error decoding pdf file!";
                                            }
                                            return responsePdfReceiptHistory;
                                        } else {
                                            message = responsePdfReceiptHistory.getMessage();
                                        }
                                        return responsePdfReceiptHistory;
                                    } else {
                                        message = responsePdfReceiptHistory.getStatus().getMessage();
                                    }
                                } else {
                                    message = serverResult.getResult();
                                }
                            } else {
                                message = "Failed to get result from Server";
                            }
                        } else {
                            message = context.getString(com.adins.mss.base.R.string.no_internet_connection);
                        }
                        return null;
                    } catch (Exception e) {
                        if (Global.IS_DEV) {
                            e.printStackTrace();
                        }
                        Log.e("Info", e.getMessage());
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(JsonResponsePdfReceiptHistory result) {
                super.onPostExecute(result);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (result != null && (result.getStatusCode().equals("200") || result.getStatusCode().equals("00"))) {
                    try {
                        Intent intent = new Intent(context, ViewPdfRendererFragment.class);
                        Bundle extras = new Bundle();
                        extras.putString("FILE_LOC", fileLocation);
                        intent.putExtras(extras);

                        startActivity(intent);
                    } catch (Exception ex) {
                        if (Global.IS_DEV) {
                            ex.printStackTrace();
                        }
                    }
                } else if (result != null && !(result.getStatusCode().equals("200") || result.getStatusCode().equals("00"))) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder.withTitle(context.getString(com.adins.mss.base.R.string.info_capital))
                            .isCancelableOnTouchOutside(false)
                            .withMessage(result.getMessage())
                            .withButton1Text(context.getString(com.adins.mss.base.R.string.btnOk))
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                }
                            })
                            .show();
                } else if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(com.adins.mss.base.R.string.msgErrorParsingJson), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
