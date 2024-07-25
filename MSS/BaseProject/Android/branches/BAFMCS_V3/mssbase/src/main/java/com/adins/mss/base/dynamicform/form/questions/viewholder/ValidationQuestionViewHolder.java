package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.JsonRequestValidationQuestion;
import com.adins.mss.base.dynamicform.JsonResponseValidationQuestion;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import org.acra.ACRA;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by noerhayati.dm on 8/1/2018.
 */

public class ValidationQuestionViewHolder extends RecyclerView.ViewHolder {

    public FragmentActivity mActivity;
    public QuestionView mView;
    public TextView mQuestionLabel;
    public EditText mQuestionAnswer;
    public Button mButtonValidation;
    public QuestionBean bean;
    public String resultOtp;
    public String resultStatus;
    private ProgressDialog progressDialog;
    private String tempText;
    private String errorMessage;
    private Integer maxDefaultRetry = 1;

    public ValidationQuestionViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mActivity = activity;
        mView = (QuestionView) itemView.findViewById(R.id.questionValidationLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionValidationLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionValidationAnswer);
        mButtonValidation = (Button) itemView.findViewById(R.id.btnCheckValidation);
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        String questionLabel = number + ". " + bean.getQuestion_label();
        String answer = bean.getAnswer();
        String btnLabel = AppContext.getAppContext().getString(R.string.btnValidation);
        View.OnClickListener clickListener;

        mQuestionLabel.setText(questionLabel);
        mQuestionAnswer.setSingleLine(true);

        mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (bean.isRelevanted()) {
            mQuestionAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            mQuestionAnswer.setHint(AppContext.getAppContext().getString(R.string.requiredField));
        } else {
            mQuestionAnswer.setHint("");
        }

        if (bean.isReadOnly()) {
            mQuestionAnswer.setSingleLine(false);
            mQuestionAnswer.setCursorVisible(false);
            mQuestionAnswer.setEnabled(false);
        } else {
            mQuestionAnswer.setCursorVisible(true);
            mQuestionAnswer.setEnabled(true);
        }

        if (answer != null && !answer.isEmpty()) {
            mQuestionAnswer.setText(answer);
        } else {
            mQuestionAnswer.setText(null);
        }

        tempText = bean.getAnswer() != null ? bean.getAnswer() : "";

        if (QuestionViewAdapter.IsValidationQuestion(Integer.valueOf(bean.getAnswer_type()))) {
            String lastAnswer = mQuestionAnswer.getText().toString().trim();
            if (tempText.equals(lastAnswer)) {
                bean.setBtnCheckClicked(true);
            }
        }

        mQuestionAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (bean.isReadOnly()) {
                    mQuestionAnswer.setKeyListener(null);
                    mQuestionAnswer.setCursorVisible(false);
                }

                if (null != bean.getAnswer()) {
                    tempText = bean.getAnswer().trim();
                }

                if (null == tempText) {
                    tempText = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = mQuestionAnswer.getText().toString().trim();
                if (bean.isRelevanted()) {
                    if (!tempText.equals(newText)) {
                        mView.setChanged(true);
                        if (tempText.equals(bean.getAnswer())) {
                            bean.setChange(true);
                        }
                    }
                }
                if (!tempText.equals(newText)) {
                    bean.setBtnCheckClicked(false);
                    bean.isRelevanted();
                }

                if (bean.getIntTextAnswer() != null) {
                    if (!CustomerFragment.getIsEditable() && bean.getIntTextAnswer().equals(newText)) {
                        bean.setBtnCheckClicked(true);
                    }
                }

                bean.setAnswer(getFinalAnswer());
            }
        });

        clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if ("".equals(mQuestionAnswer.getText().toString().trim())) {
                    Toast.makeText(mActivity, bean.getQuestion_label() + " " + mActivity.getString(R.string.msgRequired), Toast.LENGTH_SHORT).show();
                } else {
                    EventBusHelper.post(bean);
                    TaskH taskH = DynamicFormActivity.header.getTaskH();

                    long nextCall = 0;
                    boolean canCall = false;
                    GeneralParameter gpInterval = GeneralParameterDataAccess.getOne(mActivity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_INTERVAL_CONNECTION);

                    long intervalTimer = maxDefaultRetry * new Long(Global.MINUTE);
                    if (gpInterval != null) {
                        intervalTimer = Integer.valueOf(gpInterval.getGs_value()) * new Long(Global.SECOND);
                    }

                    if (taskH.getPts_date() == null) {
                        canCall = true;
                    } else {
                        long nowDate = new Date().getTime();
                        long checkDate = taskH.getPts_date().getTime();
                        long timeDiff = nowDate - checkDate;
                        if (timeDiff >= intervalTimer) {
                            canCall = true;
                        } else {
                            nextCall = intervalTimer - timeDiff;
                        }
                    }

                    if (canCall) {
                        bean.setBtnCheckClicked(true);
                        String number = mQuestionAnswer.getText().toString().trim();
                        GetValidationQuestion validationTask = new GetValidationQuestion(mActivity, number);
                        validationTask.execute();
                    } else {
                        bean.setBtnCheckClicked(false);
                        String hmsTot = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nextCall),
                                TimeUnit.MILLISECONDS.toMinutes(nextCall) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(nextCall) % TimeUnit.MINUTES.toSeconds(1));
                        String msgRetryCall = AppContext.getAppContext().getString(R.string.retryCallAgain, hmsTot);
                        Toast.makeText(mActivity, msgRetryCall, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mButtonValidation.setText(btnLabel);
        mButtonValidation.setOnClickListener(clickListener);
    }

    public String getFinalAnswer() {
        String answer = mQuestionAnswer.getText().toString().trim();
        String finalAnswer = answer;
        return finalAnswer;
    }

    private class GetValidationQuestion extends AsyncTask<Void, Void, JsonResponseValidationQuestion> {
        public FragmentActivity activity;
        private String number;

        public GetValidationQuestion(FragmentActivity activity, String number) {
            this.activity = activity;
            this.number = number;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String number = mQuestionAnswer.getText().toString().trim();

            progressDialog = ProgressDialog.show(mActivity, "", AppContext.getAppContext().getString(R.string.please_wait_misscall, number), true);
            progressDialog.show();
        }

        @Override
        protected JsonResponseValidationQuestion doInBackground(Void... params) {

            JsonResponseValidationQuestion responseValidationTask = null;

            if (Tool.isInternetconnected(activity)) {
                JsonRequestValidationQuestion requestValidaton = new JsonRequestValidationQuestion();
                AuditDataType auditData = GlobalData.getSharedGlobalData().getAuditData();
                requestValidaton.setAudit(auditData);
                requestValidaton.addImeiAndroidIdToUnstructured();
                requestValidaton.setTaskId(CustomerFragment.header.getTask_id());
                requestValidaton.setPhoneNumber(number.replace("+", ""));

                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(mActivity, encrypt, decrypt);
                String url = GlobalData.getSharedGlobalData().getURL_CHECK_VALIDATIONQUESTION();
                HttpConnectionResult serverResult;

                try {
                    String json = GsonHelper.toJson(requestValidaton);
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);

                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            responseValidationTask = GsonHelper.fromJson(serverResult.getResult(), JsonResponseValidationQuestion.class);
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

            if (responseValidationTask != null) {
                if (0 == responseValidationTask.getStatus().getCode() ||
                        1 == responseValidationTask.getStatus().getCode() ||
                        2 == responseValidationTask.getStatus().getCode()) {
                    return responseValidationTask;
                } else {
                    String message = responseValidationTask.getStatus().getMessage();
                    errorMessage = message;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonResponseValidationQuestion results) {
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
                    resultStatus = results.getStatus().getMessage();
                    resultOtp = results.getOtp();

                    QuestionSet statusQuestion = QuestionSetDataAccess.getOneQuestionByTag(mActivity, Global.TAG_PHONEVERIF_MESSAGE);
                    QuestionSet otpQuestion = QuestionSetDataAccess.getOneQuestionByTag(mActivity, Global.TAG_PHONEVERIF_OTP);

                    if (null != statusQuestion) {
                        QuestionBean beanStatus = Constant.listOfQuestion.get(statusQuestion.getIdentifier_name());
                        beanStatus.setAnswer(resultStatus);
                        bean.setIntTextAnswer(mQuestionAnswer.getText().toString().trim());
                    }

                    if (null != otpQuestion) {
                        QuestionBean beanOtp = Constant.listOfQuestion.get(otpQuestion.getIdentifier_name());
                        if (resultOtp != null) {
                            String otp = resultOtp.substring(resultOtp.length() - 4);
                            beanOtp.setAnswer(otp);
                        } else {
                            beanOtp.setAnswer("");
                        }
                    }
                    EventBusHelper.post(results);
                }
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
