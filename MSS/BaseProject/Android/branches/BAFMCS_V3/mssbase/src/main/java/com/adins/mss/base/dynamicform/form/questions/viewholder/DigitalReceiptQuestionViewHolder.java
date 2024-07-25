package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.JsonRequestDigitalReceipt;
import com.adins.mss.base.dynamicform.JsonResponseDigitalReceipt;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import org.acra.ACRA;

import java.util.Date;

/**
 * Created by noerhayati.dm on 8/1/2018.
 */

public class DigitalReceiptQuestionViewHolder extends RecyclerView.ViewHolder {

    public FragmentActivity mActivity;
    public QuestionView mView;
    public TextView mQuestionLabel;
    public EditText mQuestionAnswer;
    public Button mButtonRequest;
    public QuestionBean bean;
    public String resultRvMOBILE;
    private ProgressDialog progressDialog;
    private String tempText;
    private String errorMessage;
    private Integer maxDefaultRetry = 1;

    public DigitalReceiptQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDigitalReceiptLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDigitalReceiptLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionDigitalReceiptAnswer);
        mButtonRequest = (Button) itemView.findViewById(R.id.btnGetDigitalReceipt);
    }

    public DigitalReceiptQuestionViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mActivity = activity;
        mView = (QuestionView) itemView.findViewById(R.id.questionDigitalReceiptLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDigitalReceiptLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionDigitalReceiptAnswer);
        mButtonRequest = (Button) itemView.findViewById(R.id.btnGetDigitalReceipt);
    }

    public void bind(final QuestionBean item, final int number) {
        bean = item;
        String questionLabel = number + ". " + bean.getQuestion_label();
        String answer = bean.getAnswer();
        View.OnClickListener clickListener;

        mQuestionLabel.setText(questionLabel);
        mQuestionAnswer.setSingleLine(true);
        mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (bean.isRelevanted()) {
            mQuestionAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        mQuestionAnswer.setSingleLine(false);
        mQuestionAnswer.setCursorVisible(false);
        mQuestionAnswer.setEnabled(false);

        if (answer != null && !answer.isEmpty()) {
            mQuestionAnswer.setText(answer);
            mButtonRequest.setVisibility(View.GONE);
        } else {
            mQuestionAnswer.setText(null);
        }

        tempText = bean.getAnswer() != null ? bean.getAnswer() : "";

        if (QuestionViewAdapter.IsRvMobileQuestion(Integer.valueOf(bean.getAnswer_type()))) {
            String lastAnswer = mQuestionAnswer.getText().toString().trim();
            if (tempText.equals(lastAnswer)) {
                bean.setBtnCheckClicked(true);
            }
        }

        clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                EventBusHelper.post(bean);
                bean.setBtnCheckClicked(true);
                String number = mQuestionAnswer.getText().toString().trim();
                GetDigitalReceiptMobile rvMobileTask = new GetDigitalReceiptMobile(mActivity, number);
                rvMobileTask.execute();
            }
        };

        mButtonRequest.setOnClickListener(clickListener);
    }

    private class GetDigitalReceiptMobile extends AsyncTask<Void, Void, JsonResponseDigitalReceipt> {
        public FragmentActivity activity;
        private String number;

        public GetDigitalReceiptMobile(FragmentActivity activity, String number) {
            this.activity = activity;
            this.number = number;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.please_wait_rv_mobile), true);
            progressDialog.show();
        }

        @Override
        protected JsonResponseDigitalReceipt doInBackground(Void... params) {

            JsonResponseDigitalReceipt responseDigitalReceipt = null;

            if (Tool.isInternetconnected(activity)) {
                JsonRequestDigitalReceipt requestDigitalReceipt = new JsonRequestDigitalReceipt();
                AuditDataType auditData = GlobalData.getSharedGlobalData().getAuditData();
                requestDigitalReceipt.setAudit(auditData);
                requestDigitalReceipt.addImeiAndroidIdToUnstructured();
                requestDigitalReceipt.setUuidUser(GlobalData.getSharedGlobalData().getUser().getUuid_user());

                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(mActivity, encrypt, decrypt);
                String url = GlobalData.getSharedGlobalData().getURL_GET_DIGITAL_RECEIPT();
                HttpConnectionResult serverResult;

                try {
                    String json = GsonHelper.toJson(requestDigitalReceipt);
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);

                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            responseDigitalReceipt = GsonHelper.fromJson(serverResult.getResult(), JsonResponseDigitalReceipt.class);
                        } catch (Exception e) {
                            ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                            ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", Tool.getSystemDateTime().toLocaleString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari server"));
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    ACRA.getErrorReporter().putCustomData("errorRequestToServer", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorRequestToServer", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request ke server"));
                    e.printStackTrace();
                }
            } else {
                errorMessage = AppContext.getAppContext().getString(R.string.no_internet_connection);
            }

            if (responseDigitalReceipt != null) {
                if (1 == responseDigitalReceipt.getStatus().getCode()) {
                    return responseDigitalReceipt;
                } else {
                    String message = responseDigitalReceipt.getStatus().getMessage();
                    errorMessage = message;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonResponseDigitalReceipt results) {
            super.onPostExecute(results);
            DynamicFormActivity.header.setPts_date(new Date());
            TaskHDataAccess.addOrReplace(activity, DynamicFormActivity.header.getTaskH());
            if (errorMessage != null) {
                if (errorMessage.equals(AppContext.getAppContext().getString(R.string.no_internet_connection).toString())) {
                    Toast.makeText(mActivity, AppContext.getAppContext().getString(R.string.no_internet_connection).toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
                errorMessage = null;
            } else {
                if (results != null) {
                    resultRvMOBILE = results.getRvNumberMobile();
                    mQuestionAnswer.setText(resultRvMOBILE);
                    mButtonRequest.setVisibility(View.GONE);
                    bean.setAnswer(resultRvMOBILE);
                    EventBusHelper.post(results);
                }
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }
            }
        }
    }

}
