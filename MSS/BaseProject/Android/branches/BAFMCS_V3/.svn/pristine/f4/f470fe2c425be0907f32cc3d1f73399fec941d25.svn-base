package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.models.JsonDataRecommendationRequest;
import com.adins.mss.base.dynamicform.form.models.JsonDataRecommendationResponse;
import com.adins.mss.base.dynamicform.form.models.JsonDataSpRequest;
import com.adins.mss.base.dynamicform.form.models.JsonDataSpResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.HashMap;
import java.util.Map;

public class ButtonTextUrlViewHolder extends RecyclerView.ViewHolder {

    private final FragmentActivity mActivity;
    private final QuestionView mView;
    private final TextView mQuestionLabel;
    private final EditText mQuestionAnswer;
    private Button btnView;
    private QuestionBean mBean;

    public ButtonTextUrlViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mActivity = activity;
        mView = (QuestionView) itemView.findViewById(R.id.question_text_button_url_layout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.question_text_button_url_label);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.et_url);
        btnView = (Button) itemView.findViewById(R.id.btn_view);
    }

    public void bind(QuestionBean item, int number) {
        mBean = item;
        String qLabel = number + ". " + mBean.getQuestion_label();
        mQuestionLabel.setText(qLabel);
        mQuestionAnswer.setVisibility(View.GONE);

        final String tag = mBean.getTag();
        if(Global.TAG_RECOMMMENDATION_QUESTION.equals(tag)){
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionAnswer.setVisibility(View.VISIBLE);
                    String choiceFilter = mBean.getChoice_filter();
                    choiceFilter = choiceFilter.replaceAll("\\{", "");
                    choiceFilter = choiceFilter.replaceAll("\\}", "");
                    Map<String, String> data = new HashMap<>();
                    if (choiceFilter != null && !"".equalsIgnoreCase(choiceFilter)) {
                        String[] identifierFilters = choiceFilter.split(",");
                        for (int i = 0; i < identifierFilters.length; i++) {
                            String identifier = identifierFilters[i];
                            QuestionBean qBean = Constant.getListOfQuestion().get(identifier);
                            if (qBean != null) {
                                String answerBean = QuestionBean.getAnswer(qBean);
                                if (answerBean == null) {
                                    answerBean = "";
                                }
                                data.put(identifier, answerBean);
                            }
                        }
                    }

                    GetDataRecommendationAnswer getDataRecommendationAnswer = new GetDataRecommendationAnswer(data, tag);
                    getDataRecommendationAnswer.execute();
                }
            });
        }
        else if(Global.TAG_SP_QUESTION.equals(tag)){
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionAnswer.setVisibility(View.VISIBLE);
                    String choiceFilter = mBean.getChoice_filter();
                    choiceFilter = choiceFilter.replaceAll("\\{", "");
                    choiceFilter = choiceFilter.replaceAll("\\}", "");
                    Map<String, String> data = new HashMap<>();
                    if (choiceFilter != null && !"".equalsIgnoreCase(choiceFilter)) {
                        String[] identifierFilters = choiceFilter.split(",");
                        for (int i = 0; i < identifierFilters.length; i++) {
                            String identifier = identifierFilters[i];
                            QuestionBean qBean = Constant.getListOfQuestion().get(identifier);
                            if (qBean != null) {
                                String answerBean = QuestionBean.getAnswer(qBean);
                                if (answerBean == null) {
                                    answerBean = "";
                                }
                                data.put(identifier, answerBean);
                            }
                        }
                    }

                    GetDataSpAnswer getDataSpAnswer = new GetDataSpAnswer(data, tag);
                    getDataSpAnswer.execute();
                }
            });
        }
    }
    private class GetDataSpAnswer extends AsyncTask<Void, Void, JsonDataSpResponse>{
        private ProgressDialog progressDialog;
        private Map<String, String> dataCustomer;
        private String questionTag;

        public GetDataSpAnswer(Map<String, String> dataCustomer, String questionTag) {
            this.dataCustomer = dataCustomer;
            this.questionTag = questionTag;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.please_wait), true);
        }

        @Override
        protected JsonDataSpResponse doInBackground(Void... voids) {
            JsonDataSpResponse response = new JsonDataSpResponse();
            JsonDataSpRequest request = new JsonDataSpRequest();
            String uuidTaskH = DynamicFormActivity.getHeader().getUuid_task_h();
            request.setUuidTaskH(uuidTaskH);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            request.setQuestionTag(questionTag);
            request.setQuestionList(dataCustomer);


            String url = GlobalData.getSharedGlobalData().getURL_GET_DATA_QUESTION_BUTTON_TEXT();
            String json = GsonHelper.toJson(request);
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(mActivity, encrypt, decrypt);
            HttpConnectionResult serverResult;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                if(serverResult.isOK()){
                    String result =serverResult.getResult();
                    response =GsonHelper.fromJson(result, JsonDataSpResponse.class);
                }
            }catch (Exception e){
                FireCrash.log(e);
                try {
                    progressDialog.dismiss();
                }catch (Exception ex){
                    FireCrash.log(e);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JsonDataSpResponse result) {
            if(progressDialog.isShowing()){
                try{
                    progressDialog.dismiss();
                }catch (Exception e){
                    FireCrash.log(e);
                }
            }

            if (null != result) {
                if (result.getStatus().getCode() == 0 &&
                        "Success".equalsIgnoreCase(result.getStatus().getMessage())) {
                    if (null != result.getResult()) {
                        mQuestionAnswer.setText(result.getResult());
                        setUrlText(result.getResult());
                    } else {
                        mBean.setAnswer("-");
                        mQuestionAnswer.setText("-");
                    }
                    mQuestionAnswer.setVisibility(View.VISIBLE);
                } else {
                    String message = result.getStatus().getMessage();
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
                    dialogBuilder.withTitle(mActivity.getString(R.string.info_capital))
                            .withMessage(message)
                            .show();
                }
            } else {
                mBean.setAnswer("-");
                mQuestionAnswer.setText("-");
                mQuestionAnswer.setVisibility(View.VISIBLE);
            }
        }
    }



    private class GetDataRecommendationAnswer extends AsyncTask<Void, Void, JsonDataRecommendationResponse>{
        private ProgressDialog progressDialog;
        private Map<String, String> dataCustomer;
        private String questionTag;

        public GetDataRecommendationAnswer(Map<String, String> dataCustomer, String questionTag) {
            this.dataCustomer = dataCustomer;
            this.questionTag = questionTag;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.please_wait), true);
        }

        @Override
        protected JsonDataRecommendationResponse doInBackground(Void... voids) {
            JsonDataRecommendationResponse response = new JsonDataRecommendationResponse();
            JsonDataRecommendationRequest request = new JsonDataRecommendationRequest();
            String uuidTaskH = DynamicFormActivity.getHeader().getUuid_task_h();
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            request.setQuestionTag(questionTag);
            request.setQuestionList(dataCustomer);
            request.setUuidTaskH(uuidTaskH);

            String url = GlobalData.getSharedGlobalData().getURL_GET_DATA_QUESTION_BUTTON_TEXT();
            String json = GsonHelper.toJson(request);
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(mActivity, encrypt, decrypt);
            HttpConnectionResult serverResult;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                if(serverResult.isOK()){
                    String result = serverResult.getResult();
                    response =GsonHelper.fromJson(result, JsonDataRecommendationResponse.class);
                }
            }catch (Exception e){
                FireCrash.log(e);
                try {
                    progressDialog.dismiss();
                }catch (Exception ex){
                    FireCrash.log(e);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JsonDataRecommendationResponse response) {
            if(progressDialog.isShowing()){
                try{
                    progressDialog.dismiss();
                }catch (Exception e){
                    FireCrash.log(e);
                }
            }

            if (null != response) {
                if (response.getStatus().getCode() == 0 &&
                        "Success".equalsIgnoreCase(response.getStatus().getMessage())) {
                    if (null != response.getResult()) {
                        mBean.setAnswer(response.getResult());
                        mQuestionAnswer.setText(response.getResult());
                    } else {
                        mBean.setAnswer("-");
                        mQuestionAnswer.setText("-");
                    }
                    mQuestionAnswer.setVisibility(View.VISIBLE);
                } else {
                    mBean.setAnswer(null);

                    String message = response.getStatus().getMessage();
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
                    dialogBuilder.withTitle(mActivity.getString(R.string.info_capital))
                            .withMessage(message)
                            .show();
                }
            } else {
                mBean.setAnswer("-");
                mQuestionAnswer.setText("-");
                mQuestionAnswer.setVisibility(View.VISIBLE);
            }
        }
    }
    private void setUrlText(final String url) {
        SpannableString spannableString = new SpannableString(url);

        // Set the color to blue
        ForegroundColorSpan blueColor = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.blueUrl));
        spannableString.setSpan(blueColor, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Underline the text
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the text clickable
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBean.setAnswer(spannableString.toString());
        mQuestionAnswer.setText(spannableString);
        mQuestionAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        mQuestionAnswer.setVisibility(View.VISIBLE);
    }

}
