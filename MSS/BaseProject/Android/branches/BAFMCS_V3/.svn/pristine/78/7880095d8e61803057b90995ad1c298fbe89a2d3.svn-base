package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.ktpValidation.DukcapilApi;
import com.adins.mss.base.ktpValidation.JsonResponseKtpValidation;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.io.IOException;

public class LookupDukcapilQuestionViewHolder extends RecyclerView.ViewHolder {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mResult;
    public Button btnValidate;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener mListener;

    public LookupDukcapilQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLookupDukcapilLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLookupDukcapilLabel);
        mResult = (TextView)itemView.findViewById(R.id.txtResult);
        btnValidate = (Button) itemView.findViewById(R.id.btnValidasiDukcapil);
    }

    public LookupDukcapilQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLookupDukcapilLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLookupDukcapilLabel);
        mResult = (TextView)itemView.findViewById(R.id.txtResult);
        btnValidate = (Button) itemView.findViewById(R.id.btnValidasiDukcapil);
        mActivity = activity;
        mListener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        String qLabel = number + ". " + bean.getQuestion_label();

        mQuestionLabel.setText(qLabel);
        mResult.setText(bean.getAnswer());

        btnValidate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (bean.getChoice_filter() != null || bean.getChoice_filter().isEmpty()) {
                    String[] tempfilters = Tool.split(bean.getChoice_filter(), Global.DELIMETER_DATA3);
                    String refId = tempfilters[0];
                    refId = refId.replace("{", "");
                    refId = refId.replace("}", "");
                    final QuestionBean ktp = Constant.getListOfQuestion().get(refId);
                    if(ktp!=null){
                        if(ktp.getAnswer()!=null && !"".equals(ktp.getAnswer())){
                            final String nomorKTP = ktp.getAnswer();
                            if(nomorKTP != null && nomorKTP.length()>0){
                                new AsyncTask<Void, Void, String>() {
                                    private ProgressDialog progressDialog;

                                    @Override
                                    protected void onPreExecute() {
                                        progressDialog = ProgressDialog.show(mActivity,
                                                "", mActivity.getString(R.string.progressWait), true, false);
                                        mResult.setText("");
                                        bean.setAnswer(null);
                                        bean.setDataDukcapil(null);
                                    }

                                    @Override
                                    protected String doInBackground(Void... params) {
                                        String responseKtpValidation = "";
                                        DukcapilApi api = new DukcapilApi(mActivity);
                                        try {
                                            if (Tool.isInternetconnected(mActivity)) {
                                                responseKtpValidation = api.request(ktp.getAnswer(), ktp.getIdentifier_name());
                                                return responseKtpValidation;
                                            }else{
                                                return mActivity.getString(R.string.no_internet_connection);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(String responseKtpValidation) {
                                        super.onPostExecute(responseKtpValidation);
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            try {
                                                progressDialog.dismiss();
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                            }
                                        }
                                        JsonResponseKtpValidation resp = null;
                                        try {
                                            resp = GsonHelper.fromJson(responseKtpValidation, JsonResponseKtpValidation.class);
                                        }catch (Exception e){
                                            FireCrash.log(e);
                                        }

                                        if(resp==null){
                                            String message = null;
                                            JsonResponseKtpValidation dataDukcapil = new JsonResponseKtpValidation();
                                            dataDukcapil.setResult("0");
                                            bean.setDataDukcapil(GsonHelper.toJson(dataDukcapil));
                                            message =  mActivity.getString(R.string.msgErrorParsingJson);
                                            bean.setAnswer(message);
                                            mResult.setText(message);
                                        }else{
                                            String message = mActivity.getString(R.string.no_data_from_server);
                                            if (null!=resp.getResult() && resp.getResult().equalsIgnoreCase("1")) {
                                                bean.setDataDukcapil(responseKtpValidation);
                                                message = resp.getMessage();
                                            }
                                            else if(null!=resp.getResult() && resp.getResult().equalsIgnoreCase("-1")){
                                                bean.setDataDukcapil(responseKtpValidation);
                                                message = nomorKTP +" tidak valid";
                                            }
                                            else if(null!=resp.getResult() && resp.getResult().equalsIgnoreCase("0")){
                                                bean.setDataDukcapil(responseKtpValidation);
                                                message = nomorKTP +" Offline";
                                            }
                                            bean.setAnswer(message);
                                            mResult.setText(message);
                                        }

                                        bean.setIsCanChange(true);
                                        bean.setChange(true);
                                        mView.setChanged(true);

                                        mListener.onValidasiDukcapilListener(bean, group, number-1);

                                    }
                                }.execute();
                            }else{
                                Toast.makeText(mActivity, R.string.ktp_notice, Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(mActivity, R.string.ktp_notice, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(mActivity, R.string.ktp_notice, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


}
