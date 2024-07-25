package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.JsonRequestPdfDocument;
import com.adins.mss.base.dynamicform.JsonResponsePdfDocument;
import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.pdfrenderer.ViewPdfRendererFragment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.Base64;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;
import com.pax.utils.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
public class TextQuestionViewHolder extends RecyclerView.ViewHolder{
    public QuestionView mView;
    public TextView mQuestionLabel;
    public EditText mQuestionAnswer;
    public Button mCallButton;
    public QuestionBean bean;
    private String tempText = "";
    private RecyclerView mRecyclerView;
    private Activity mActivity;
    private Button mButtonView;

    public TextQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionTextLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionTextAnswer);
        mCallButton = itemView.findViewById(R.id.callPhoneNumber);
    }

    public TextQuestionViewHolder(View itemView, RecyclerView recyclerView) {
        super(itemView);
        mRecyclerView = recyclerView;
        mView = (QuestionView) itemView.findViewById(R.id.questionTextLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionTextAnswer);
        mCallButton = itemView.findViewById(R.id.callPhoneNumber);
    }

    public TextQuestionViewHolder(Activity context, View itemView, RecyclerView recyclerView) {
        super(itemView);
        mRecyclerView = recyclerView;
        mView = (QuestionView) itemView.findViewById(R.id.questionTextLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (EditText) itemView.findViewById(R.id.questionTextAnswer);
        mCallButton = itemView.findViewById(R.id.callPhoneNumber);
        mButtonView = (Button) itemView.findViewById(R.id.questionBtnViewPdf);
        mActivity = context;
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        final String answerType = bean.getAnswer_type();
        String qLabel = number + ". " + bean.getQuestion_label();
        ThemeUtility.setViewBackground(mQuestionAnswer, QuestionViewAdapter.etBorderColorStateList);//apply colorstatelist
        mQuestionLabel.setText(qLabel);

        if(bean.getMax_length() != null){
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    bean.getMax_length())};
            mQuestionAnswer.setFilters(inputFilters);

        }
        String qAnswer = bean.getAnswer();
        if (qAnswer != null && !qAnswer.isEmpty()) {
            mQuestionAnswer.setText(qAnswer);
        } else {
            mQuestionAnswer.setText(null);
            bean.setAnswer("");
        }
        mQuestionAnswer.setSingleLine(true);
        mQuestionAnswer.setMaxLines(1);
        if (Global.AT_DECIMAL.equals(answerType)) {
            if (qAnswer != null) {
                try {
                    NumberFormat nf = NumberFormat.getInstance(Locale.US);
                    Double finalAnswer = nf.parse(qAnswer).doubleValue();
                    mQuestionAnswer.setText(finalAnswer.toString());
                }catch(Exception e){
                    FireCrash.log(e);
                    Log.w("Exception","Cannot bind answer in : " + getClass().getSimpleName(), e);
                }
            }
            mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (Global.AT_TEXT_MULTILINE.equals(answerType)) {
            mQuestionAnswer.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            mQuestionAnswer.setSingleLine(false);
        } else if (Global.AT_CURRENCY.equals(answerType)) {
            mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (Global.AT_NUMERIC.equals(answerType)) {
            mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (null != item.getTag() && item.getTag().equalsIgnoreCase("CUSTOMER PHONE") &&
                    !Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
               setCallButton(item);
            }
        } else if (Global.AT_PDF.equals(answerType)) {
            mQuestionAnswer.setVisibility(View.GONE);
            GeneralParameter gs = GeneralParameterDataAccess.getOne(mActivity,
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_SHOW_PDF_VIEW_BUTTON);
            if (gs != null) {
                String showViewButton = gs.getGs_value();
                if (showViewButton.equals("TRUE")) {
                    mButtonView.setVisibility(View.VISIBLE);
                } else {
                    mButtonView.setVisibility(View.GONE);
                }
            } else {
                mButtonView.setVisibility(View.GONE);
            }
        } else {
            mQuestionAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        tempText = bean.getAnswer() != null ? bean.getAnswer() : "";

        mQuestionAnswer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //EMPTY
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (bean.isReadOnly()) {
                    mQuestionAnswer.setKeyListener(null);
                    mQuestionAnswer.setCursorVisible(false);
                }
                if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
                    if (bean.getAnswer() != null)
                        tempText = Tool.separateThousand(bean.getAnswer().trim());
                    if (tempText == null) tempText = "";
                } else {
                    if (bean.getAnswer() != null)
                        tempText = bean.getAnswer().trim();
                    if (tempText == null) tempText = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (bean.isRelevanted() || !bean.isChange()) {
                    if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
                        if (tempText.contains(".")) {
                            String newText = mQuestionAnswer.getText().toString().trim();
                            if (!newText.contains("."))
                                newText = Tool.separateThousand(newText);
                            if (!tempText.equals(newText)) {
                                if (!newText.contains(","))
                                    newText = Tool.separateThousand(newText);
                                if (!tempText.equals(newText)) {
                                    if (mRecyclerView != null) {
                                        ScrollingLinearLayoutManager layoutManager = (ScrollingLinearLayoutManager) mRecyclerView.getLayoutManager();
                                        layoutManager.setScrollEnable(false);
                                    }
                                    mView.setChanged(true);
                                    bean.setChange(true);
                                } else {
                                    mView.setChanged(false);
                                    bean.setChange(false);
                                }
                            } else {
                                boolean isSame = s.toString().equals(newText);
                                if (!isSame) {
                                    mView.setChanged(false);
                                    bean.setChange(false);
                                }
                            }
                        } else {
                            tempText = Tool.separateThousand(tempText);
                            String newText = mQuestionAnswer.getText().toString().trim();
                            if (!newText.contains("."))
                                newText = Tool.separateThousand(newText);
                            if (!tempText.equals(newText)) {
                                mView.setChanged(true);
                                bean.setChange(true);
                            } else {
                                mView.setChanged(false);
                                bean.setChange(false);
                            }
                        }
                    } else if (Global.AT_NUMERIC.equals(answerType) || Global.AT_DECIMAL.equals(answerType)
                            || Global.AT_TEXT.equals(answerType) || Global.AT_TEXT_MULTILINE.equals(answerType)) {
                        String newText = mQuestionAnswer.getText().toString().trim();
                        if (!tempText.equals(newText)) {
                            mView.setChanged(true);
                            if (tempText.equals(bean.getAnswer()))
                                bean.setChange(true);
                        } else {
                            mView.setChanged(false);
                            bean.setChange(false);
                        }
                    }
                }
                String newText = mQuestionAnswer.getText().toString().trim();
                if (bean.isRelevantMandatory()) {
                    if (null == bean.getAnswer())
                        bean.setAnswer("");
                    if (bean.getAnswer().equals(newText)) {
                        if (bean.getAnswer().equalsIgnoreCase("")) {
                            mQuestionAnswer.setHint(mActivity.getString(R.string.requiredField));
                        } else {
                            mQuestionAnswer.setHint("");
                        }
                    } else {
                        if (bean.getAnswer().equalsIgnoreCase("")) {
                            mQuestionAnswer.setHint(mActivity.getString(R.string.requiredField));
                        } else {
                            mQuestionAnswer.setHint("");
                        }
                        bean.setChange(true);
                        mView.setChanged(true);
                    }
                } else if (!bean.getRelevant_mandatory().equalsIgnoreCase("")) {
                    if (null == bean.getAnswer())
                        bean.setAnswer("");
                    if (null != bean && bean.getAnswer().equals(newText)) {
                        mQuestionAnswer.setHint("");
                    } else {
                        mQuestionAnswer.setHint("");
                        bean.setChange(true);
                        mView.setChanged(true);
                    }
                }
                bean.setAnswer(getFinalAnswer());
            }
        });
        mQuestionAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
                    if (!hasFocus) {
                        //Nendi: Bugfix Currency on change focus view as currency format
                        showCurrencyView();
                    } else {
                        showNormalView();
                    }
                }
            }
        });
        if (bean.isRelevanted())
            mQuestionAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (answerType.equals(Global.AT_CURRENCY)) {
            showCurrencyView();
        }
        if (bean.getIs_mandatory().equals(Global.TRUE_STRING))
            mQuestionAnswer.setHint(mActivity.getString(R.string.requiredField));
        else
            mQuestionAnswer.setHint("");
        if (bean.isRelevantMandatory()) {
            mQuestionAnswer.setHint(mActivity.getString(R.string.requiredField));
        } else {
            if (bean.isMandatory()) {
                mQuestionAnswer.setHint(mActivity.getString(R.string.requiredField));
            } else {
                mQuestionAnswer.setHint("");
            }
        }

        if (bean.isReadOnly()) {
            mQuestionAnswer.setSingleLine(false);
            mQuestionAnswer.setCursorVisible(false);
            mQuestionAnswer.setEnabled(false);
            //Nendi: 2019.06.13 | Bugfix PRDAITMSS-698 (Crash saat ganti value edit text setelah fokus pindah ke view lain)
            mQuestionAnswer.setFocusableInTouchMode(false);
            mQuestionAnswer.setFocusable(false);
            mQuestionAnswer.setClickable(false);
        } else {
            mQuestionAnswer.setCursorVisible(true);
            mQuestionAnswer.setEnabled(true);
            //Nendi: 2019.06.13 | Bugfix PRDAITMSS-698 (Crash saat ganti value edit text setelah fokus pindah ke view lain)
            mQuestionAnswer.setFocusableInTouchMode(true);
            mQuestionAnswer.setFocusable(true);
            mQuestionAnswer.setClickable(true);
        }

        mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docType;
                String identifier = item.getIdentifier_name();

                String DOCTYPE_FIDUCIA = "FIDUCIA";
                String DOCTYPE_SPK = "SPK";
                String DOCTYPE_RAL = "RAL";

                int i = 0;
                int length = 0;
                if (identifier.contains(DOCTYPE_FIDUCIA)) {
                    i = identifier.indexOf(DOCTYPE_FIDUCIA);
                    length = DOCTYPE_FIDUCIA.length();
                } else if (identifier.contains(DOCTYPE_SPK)) {
                    i = identifier.indexOf(DOCTYPE_SPK);
                    length = DOCTYPE_SPK.length();
                } else if (identifier.contains(DOCTYPE_RAL)) {
                    i = identifier.indexOf(DOCTYPE_RAL);
                    length = DOCTYPE_RAL.length();
                }
                docType = identifier.substring(i, i + length);
                getDocumentPDF(mActivity, docType);
            }
        });
    }

    public void showCurrencyView() {
        mQuestionAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
        String answer = mQuestionAnswer.getText().toString().trim();

        String currencyView = Tool.separateThousand(answer);
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                50)};
        mQuestionAnswer.setFilters(inputFilters);
        if (currencyView == null) currencyView = "";
        mQuestionAnswer.setText(currencyView);
    }

    public String getFinalAnswer() {
        String answer = mQuestionAnswer.getText().toString().trim();
        String finalAnswer = "";
        if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
            if (answer != null && answer.length() > 0) {
                String tempAnswer = Tool.deleteAll(answer, ",");
                String[] intAnswer = Tool.split(tempAnswer, ".");
                if (intAnswer.length > 1) {
                    if (intAnswer[1].equals("00"))
                        finalAnswer = intAnswer[0];
                    else {
                        finalAnswer = tempAnswer;
                    }
                } else {
                    finalAnswer = tempAnswer;
                }
            }
        } else {
            finalAnswer = answer;
        }
        return finalAnswer;
    }

    public void showNormalView() {
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                bean.getMax_length())};
        mQuestionAnswer.setFilters(inputFilters);
        String finalAnswer = getFinalAnswer();
        mQuestionAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);
        mQuestionAnswer.setText(finalAnswer);
    }

    private void setCallButton(final QuestionBean item){
        mCallButton.setVisibility(View.VISIBLE);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int button = v.getId();
                if (button == R.id.callPhoneNumber){
                    try{
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+item.getAnswer()));
                        mActivity.startActivity(dialIntent);
                    }catch (NullPointerException ex){
                        Log.w("Exception","Cannot get date time answer in : " + getClass().getSimpleName(), ex);
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void getDocumentPDF(final Context context, final String docType) {
        new AsyncTask<Void, Void, JsonResponsePdfDocument>() {
            private ProgressDialog progressDialog;
            private String fileLocation;
            private String message;
            private String agreementNo;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(context,
                        "", context.getString(R.string.progressWait), true);
            }

            @Override
            protected JsonResponsePdfDocument doInBackground(Void... arg0) {
                try {
                    TaskH taskH = TaskHDataAccess.getOneHeader(context, DynamicFormActivity.header.getUuid_task_h());
                    if (taskH != null) {
                        agreementNo = taskH.getAppl_no();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Agreement No is empty. Cannot request pdf document";
                    return null;
                }

                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/bafmcspdf" + "/" + agreementNo + "_" + docType + ".pdf";
                File fileOpen = new File(fileName);
                if (fileOpen.exists()) {
                    fileLocation = fileName;
                    JsonResponsePdfDocument responsePdfDocument = new JsonResponsePdfDocument();
                    JsonResponsePdfDocument.Status status = new JsonResponsePdfDocument.Status();
                    status.setCode(0);
                    responsePdfDocument.setStatusCode("200");
                    responsePdfDocument.setStatus(status);
                    return responsePdfDocument;
                } else {
                    try {
                        if (Tool.isInternetconnected(context)) {
                            User user = GlobalData.getSharedGlobalData().getUser();

                            JsonRequestPdfDocument requestPdfDocument = new JsonRequestPdfDocument();
                            requestPdfDocument.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                            requestPdfDocument.addImeiAndroidIdToUnstructured();
                            requestPdfDocument.setLoginId(user.getLogin_id());
                            requestPdfDocument.setAgreementNo(agreementNo);
                            requestPdfDocument.setDocType(docType);

                            String json = GsonHelper.toJson(requestPdfDocument);
                            String url = GlobalData.getSharedGlobalData().getURL_GET_PDF_DOCUMENT();
                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();

                            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                            HttpConnectionResult serverResult = null;
                            try {
                                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (null != serverResult) {
                                if (serverResult.isOK()) {
                                    JsonResponsePdfDocument responsePdfDocument = GsonHelper.fromJson(serverResult.getResult(), JsonResponsePdfDocument.class);
                                    if (responsePdfDocument.getStatus().getCode() == 0) {
                                        if (responsePdfDocument.getStatusCode().equals("200") || responsePdfDocument.getStatusCode().equals("00")) {
                                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/bafmcspdf");
                                            if (!file.exists()) {
                                                file.mkdirs();
                                            }

                                            String uriString = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/bafmcspdf" + "/" + agreementNo + "_" + docType + ".pdf";
                                            fileLocation = uriString;
                                            try {
                                                byte[] pdfBytes = Base64.decode(responsePdfDocument.getData());
                                                FileOutputStream os = new FileOutputStream(uriString);
                                                os.write(pdfBytes);
                                                os.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                message = "Error decoding pdf file!";
                                            }
                                            return responsePdfDocument;
                                        } else {
                                            message = responsePdfDocument.getMessage();
                                        }
                                        return responsePdfDocument;
                                    } else {
                                        message = responsePdfDocument.getStatus().getMessage();
                                    }
                                } else {
                                    message = serverResult.getResult();
                                }
                            } else {
                                message = "Failed to get result from Server";
                            }
                        } else {
                            message = context.getString(R.string.no_internet_connection);
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
            protected void onPostExecute(JsonResponsePdfDocument result) {
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

                        mActivity.startActivity(intent);
                    } catch (Exception ex) {
                        if (Global.IS_DEV) {
                            ex.printStackTrace();
                        }
                    }
                } else if (result != null && !(result.getStatusCode().equals("200") || result.getStatusCode().equals("00"))) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder.withTitle(context.getString(R.string.info_capital))
                            .isCancelableOnTouchOutside(false)
                            .withMessage(result.getMessage())
                            .withButton1Text(context.getString(R.string.btnOk))
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
                    Toast.makeText(context, context.getString(R.string.msgErrorParsingJson), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}