package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.View;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.base.timeline.MapsViewer;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;
import com.pax.utils.log;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.IsRvMobileQuestion;
import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.isTextOnlineQuestion;

/**
 * Created by gigin.ginanjar on 06/09/2016.
 */
public class ReviewTextViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout layout;
    public TextView mLabelNo;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public ImageView mCheckLocation;
    private Button mCallButton;
    public QuestionBean bean;
    public OnQuestionClickListener listener;
    private Activity mActivity;

    @Deprecated
    public ReviewTextViewHolder(View itemView) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.textReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionTextAnswer);
        mCheckLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mCallButton = itemView.findViewById(R.id.callPhoneNumber);
    }

    public ReviewTextViewHolder(View itemView, OnQuestionClickListener listener) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.textReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionTextAnswer);
        mCheckLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        this.listener = listener;
    }

    public ReviewTextViewHolder(Activity activity, View itemView, OnQuestionClickListener listener) {
        super(itemView);
        mActivity = activity;
        layout = (RelativeLayout) itemView.findViewById(R.id.textReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionTextAnswer);
        mCheckLocation = (ImageView) itemView.findViewById(R.id.imgLocationAnswer);
        mCallButton = itemView.findViewById(R.id.callPhoneNumber);
        this.listener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        mLabelNo.setText(number + ".");
        String qLabel = bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);

        String answerType = bean.getAnswer_type();

        String qAnswer = getFinalAnswer(answerType);
        if (qAnswer != null && !qAnswer.isEmpty()) {
            if (answerType.equals(Global.AT_CURRENCY))
                showCurrencyView(qAnswer);
            else
                mQuestionAnswer.setText(qAnswer);
        } else {
            mQuestionAnswer.setText(mActivity.getString(R.string.no_answer_found));
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onReviewClickListener(bean, group, number - 1);
            }
        });
        if (QuestionViewAdapter.IsLocationQuestion(Integer.valueOf(answerType)) && mActivity != null) {
            mCheckLocation.setVisibility(View.VISIBLE);
            mCheckLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.getAnswer() != null && bean.getAnswer().length() > 0) {
                        try {
                            String lat = bean.getLocationInfo().getLatitude();
                            String lng = bean.getLocationInfo().getLongitude();
                            int acc = bean.getLocationInfo().getAccuracy();
                            Intent intent = new Intent(mActivity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            intent.putExtra("accuracy", acc);
                            mActivity.startActivity(intent);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            String lat = bean.getLatitude();
                            String lng = bean.getLongitude();
                            Intent intent = new Intent(mActivity, MapsViewer.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            mActivity.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(mActivity, mActivity.getString(R.string.set_location),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            mCheckLocation.setVisibility(View.GONE);
        }
        if (null != item.getTag() && item.getTag().equalsIgnoreCase("CUSTOMER PHONE") &&
                !Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
            setCallButton(item);
        }
    }

    private String getFinalAnswer(String answerType) {
        int viewType = Integer.valueOf(answerType);
        if (QuestionViewAdapter.IsTextQuestion(viewType)) {
            return bean.getAnswer();
        } else if (QuestionViewAdapter.IsDropdownQuestion(viewType)) {
            return getOptionAnswer();
        } else if (QuestionViewAdapter.IsMultipleQuestion(viewType)) {
            return getOptionAnswer();
        } else if (QuestionViewAdapter.IsRadioQuestion(viewType)) {
            return getOptionAnswer();
        } else if (QuestionViewAdapter.IsLocationQuestion(viewType)) {
            return bean.getAnswer();
        } else if (QuestionViewAdapter.IsDateTimeQuestion(viewType)) {
            return getDateTimeAnswer();
        } else if (QuestionViewAdapter.IsTextWithSuggestionQuestion(viewType)) {
            return bean.getAnswer();
        } else if (QuestionViewAdapter.IsLookupQuestion(viewType)) {
            return bean.getAnswer();
        } else if (QuestionViewAdapter.IsValidationQuestion(viewType)) {
            return bean.getAnswer();
        } else if (IsRvMobileQuestion(viewType)) {
            return bean.getAnswer();
        } else if (isTextOnlineQuestion(viewType)) {
            return bean.getAnswer();
        } else if(QuestionViewAdapter.IsButtonViewUrlQuestion(viewType)){
            return bean.getAnswer();
        } else {
            return null;
        }
    }

    private String getDateTimeAnswer() {
        String finalformat = null;
        Date date = null;
        String finalAnswer = "";
        String answer = bean.getAnswer();
        String answerType = bean.getAnswer_type();
        try {
            if (Global.AT_DATE.equals(answerType)) {
                finalformat = Global.DATE_STR_FORMAT;
                if (answer.matches(Global.DATE_REGEX)){
                    date = Formatter.parseDate(answer, finalformat);
                }else {
                    String format = Global.DATE_STR_FORMAT_GSON;
                    date = Formatter.parseDate(answer, format);
                }
            } else if (Global.AT_TIME.equals(answerType)) {
                finalformat = Global.TIME_STR_FORMAT;
                if (answer.matches(Global.TIME_REGEX)){
                    date = Formatter.parseDate(answer,finalformat);
                }else {
                    String format = Global.TIME_STR_FORMAT2;
                    date = Formatter.parseDate(answer, format);
                }
            } else if (Global.AT_DATE_TIME.equals(answerType)) {
                finalformat = Global.DATE_TIME_STR_FORMAT;
                if (answer.length()>=19){
                    answer = answer.substring(0,16);
                }
                if (answer.matches(Global.DATETIME_REGEX)){
                    date = Formatter.parseDate(answer, finalformat);
                }else {
                    String format = Global.DATE_STR_FORMAT_GSON;
                    date = Formatter.parseDate(answer, format);
                }
            }
            finalAnswer = Formatter.formatDate(date, finalformat);
        }
        catch (ParseException ex){
            Log.w("Exception","Cannot get date time answer in : " + getClass().getSimpleName(), ex);
        }
        catch (Exception e){
            FireCrash.log(e);
            try{
                finalAnswer = answer;
            } catch (Exception e1) {
                if (Global.IS_DEV) {
                    Log.w("Exception","Cannot get answer in : " + getClass().getSimpleName(), e1);
                }
            }
        }

        return finalAnswer;
    }

    private String getOptionAnswer() {
        List<OptionAnswerBean> listOptions = bean.getSelectedOptionAnswers();
        int i = 0;
        String[] arrSelectedAnswer = null;
        try {
            arrSelectedAnswer= Tool.split(bean.getAnswer(), Global.DELIMETER_DATA);
        } catch (Exception e) {
            FireCrash.log(e);
            arrSelectedAnswer = new String[0];
        }
        StringBuilder sb = new StringBuilder();
        for (OptionAnswerBean optBean : listOptions) {
            if (optBean.isSelected()) {
                if (Tool.isOptionsWithDescription(bean.getAnswer_type())) {
                    if (i < arrSelectedAnswer.length)
                        sb.append(optBean.getValue() + " - " + arrSelectedAnswer[i] + "; ");
                    else
                        sb.append(optBean.getValue() + " - ; ");
                } else {
                    if (i > 0)
                        sb.append("\n");
                    sb.append(optBean.getValue());
                }

                if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type()) ||
                        Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type()) &&
                                i == listOptions.size() - 1) {
                    sb.append("\nDesc : " + bean.getAnswer());
                }
            } else {
                sb.append(AppContext.getAppContext().getString(R.string.no_selected_field));
            }
            i++;
        }
        if (listOptions == null || listOptions.isEmpty()) {
            sb.append(AppContext.getAppContext().getString(R.string.no_selected_field));
        }
        return sb.toString();
    }

    public void showCurrencyView(String answer) {
        mQuestionAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
        String currencyView = Tool.separateThousand(answer);
        if (currencyView == null) currencyView = "";
        mQuestionAnswer.setText(currencyView);
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
                        Log.w("Exception","Cannot set call button answer in : " + getClass().getSimpleName(), ex);
                    }
                }
            }
        });
    }
}
