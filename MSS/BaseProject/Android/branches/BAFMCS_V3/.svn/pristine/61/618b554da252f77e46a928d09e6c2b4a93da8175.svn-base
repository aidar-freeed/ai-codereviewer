package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.JsonRequestTextOnline;
import com.adins.mss.base.dynamicform.JsonResponseTextOnline;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

public class TextOnlineViewHolder extends RecyclerView.ViewHolder{
    RecyclerView mRecyclerView;
    private FragmentActivity mActivity;
    QuestionView mView;
    private TextView mQuestionLabel;
    private EditText mQuestionAnswer;
    private ImageButton mButtonSearch;
    private QuestionBean bean;



    public TextOnlineViewHolder(View itemView, FragmentActivity mActivity) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionTextLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionTextAnswer);
        mButtonSearch = (ImageButton) itemView.findViewById(R.id.buttonSearchOnline);
        this.mActivity = mActivity;
    }

    public TextOnlineViewHolder(View itemView, RecyclerView recyclerView, FragmentActivity mActivity){
        super(itemView);
        mRecyclerView = recyclerView;
        mView = (QuestionView) itemView.findViewById(R.id.questionTextLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionTextAnswer);
        mButtonSearch = (ImageButton) itemView.findViewById(R.id.buttonSearchOnline);
        this.mActivity = mActivity;
    }

    public void bind(final QuestionBean item, int number) {
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

        if (null != answer && !answer.isEmpty()) {
            mQuestionAnswer.setText(answer);
        } else {
            mQuestionAnswer.setText(null);
        }

        if (Global.TRUE_STRING.equals(bean.getIs_mandatory())) {
            mQuestionAnswer.setHint(AppContext.getAppContext().getString(R.string.requiredField));
        } else {
            mQuestionAnswer.setHint("");
        }

        clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                EventBusHelper.post(bean);
                TextOnlineTask textOnlineTask= new TextOnlineTask(mActivity, bean);
                textOnlineTask.execute();
            }
        };
        mButtonSearch.setOnClickListener(clickListener);
    }
    private class TextOnlineTask extends AsyncTask<String, Void, String> {
        private QuestionBean bean;
        private Activity activity;
        private ProgressDialog progressDialog;
        private String errMessage;

        public TextOnlineTask(Activity activity, QuestionBean bean){
            this.activity = activity;
            this.bean=bean;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.please_wait), true);
        }

        @Override
        protected String doInBackground(String... strings) {
            if (Tool.isInternetconnected(activity)) {
                JsonRequestTextOnline request = new JsonRequestTextOnline();
                AuditDataType auditData = GlobalData.getSharedGlobalData().getAuditData();
                request.setAudit(auditData);
                request.setTaskId(CustomerFragment.header.getTask_id());
                request.setRefId(bean.getIdentifier_name());
                String url = GlobalData.getSharedGlobalData().getURL_GET_TEXT_ONLINE_ANSWER();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                HttpConnectionResult serverResult = null;
                String json = GsonHelper.toJson(request);
                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                } catch (Exception e) {
                    errMessage = activity.getString(R.string.msgConnectionFailed);
                    return null;
                }
                if (null != serverResult && serverResult.isOK()) {
                    return serverResult.getResult();
                } else {
                    errMessage = activity.getString(R.string.connection_failed);
                }
            } else {
                errMessage = activity.getString(R.string.no_internet_connection);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = null;
            }
            if (null != errMessage && !errMessage.isEmpty()) {
                Toast.makeText(activity, errMessage, Toast.LENGTH_SHORT).show();
            } else if (null != result) {
                processingResponseServer(result);
            }
        }

        private void processingResponseServer(String result) {
            JsonResponseTextOnline json = GsonHelper.fromJson(result, JsonResponseTextOnline.class);
            if (1 == json.getStatus().getCode() && "success".equalsIgnoreCase(json.getStatus().getMessage())) {
                String answers = json.getAnswer();
                QuestionBean mBean = Constant.listOfQuestion.get(bean.getIdentifier_name());
                mBean.setAnswer(answers);
                mBean.setReadOnly(true);
                mQuestionAnswer.setText(answers);
                EventBusHelper.post(json);
            } else {
                Toast.makeText(activity, json.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
