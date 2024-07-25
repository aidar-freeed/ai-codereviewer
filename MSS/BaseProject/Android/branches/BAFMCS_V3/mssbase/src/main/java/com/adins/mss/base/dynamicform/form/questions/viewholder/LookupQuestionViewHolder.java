package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

/**
 * Created by gigin.ginanjar on 07/10/2016.
 */

public class LookupQuestionViewHolder extends RecyclerView.ViewHolder {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public TextView mTxtSelectedAnswer;
    public Button mButtonChooseLookup;
    public TableLayout criteriaTableLayout;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener mListener;
    private int group;
    private int position;

    public LookupQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLookupLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLookupLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionLookupAnswer);
        mTxtSelectedAnswer = (TextView) itemView.findViewById(R.id.txtSelectedAnswer);
        mButtonChooseLookup = (Button) itemView.findViewById(R.id.btnChooseLookup);
        criteriaTableLayout = (TableLayout) itemView.findViewById(R.id.questionLookupAnswerLayout);
    }

    public LookupQuestionViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionLookupLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionLookupLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionLookupAnswer);
        mButtonChooseLookup = (Button) itemView.findViewById(R.id.btnChooseLookup);
        mTxtSelectedAnswer = (TextView) itemView.findViewById(R.id.txtSelectedAnswer);
        criteriaTableLayout = (TableLayout) itemView.findViewById(R.id.questionLookupAnswerLayout);
        mActivity = activity;
        mListener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        this.group = group;
        position = number - 1;
        String qLabel = number + ". " + bean.getQuestion_label();

        mQuestionLabel.setText(qLabel);

        mButtonChooseLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLookupSelectedListener(bean, group, position);
            }
        });
        criteriaTableLayout.removeAllViews();
        if (bean.getAnswer() != null && !bean.getAnswer().isEmpty()) {
            mTxtSelectedAnswer.setVisibility(View.VISIBLE);
            mButtonChooseLookup.setText(mActivity.getString(R.string.change_answer));
            criteriaTableLayout.setVisibility(View.VISIBLE);
            mQuestionAnswer.setText(bean.getAnswer());
            String[] values = Tool.split(bean.getAnswer(), Global.DELIMETER_ROW);
            String[] lookupCode = Tool.split(values[0], Global.DELIMETER_DATA_LOOKUP);
            String[] lookupValue = null;
            if (values.length > 1)
                lookupValue = Tool.split(values[1], Global.DELIMETER_DATA_LOOKUP);
            StringBuilder lovCode = new StringBuilder();
            StringBuilder lovValue = new StringBuilder();
            for (int i = 0; i < lookupCode.length; i++) {
                TableRow row = (TableRow) LayoutInflater.from(mActivity).inflate(R.layout.lookup_criteria_row, criteriaTableLayout, false);
                String code = lookupCode[i];
                if (lovCode.length() != 0)
                    lovCode.append("\n");
                lovCode.append(code);
                if (lookupValue != null) {
                    String value = lookupValue[i] != null ? lookupValue[i] : "";
                    if (lovValue.length() != 0)
                        lovValue.append("\n");
                    lovValue.append(value);

                    TextView textDesc = (TextView) row.findViewById(R.id.fieldValue);
                    textDesc.setText(value);
                }
                if (i % 2 == 0) {
                    row.setBackgroundResource(R.color.tv_gray_light);
                } else if (i % 2 == 1) {
                    row.setBackgroundResource(R.color.tv_gray);
                }
                criteriaTableLayout.addView(row);
            }


        } else {
            mTxtSelectedAnswer.setVisibility(View.GONE);
            mButtonChooseLookup.setText(mActivity.getString(R.string.chooseLookup));
            criteriaTableLayout.setVisibility(View.GONE);
            mQuestionAnswer.setText("");
        }

        if (bean.isReadOnly()) {
            mButtonChooseLookup.setVisibility(View.GONE);
            mButtonChooseLookup.setEnabled(false);
        } else {
            mButtonChooseLookup.setVisibility(View.VISIBLE);
            mButtonChooseLookup.setEnabled(true);
        }

        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval())
            mButtonChooseLookup.setVisibility(View.GONE);
        else {
            mButtonChooseLookup.setVisibility(View.VISIBLE);
            if (bean.isReadOnly()) {
                mButtonChooseLookup.setVisibility(View.GONE);
                mButtonChooseLookup.setEnabled(false);
            } else {
                mButtonChooseLookup.setVisibility(View.VISIBLE);
                mButtonChooseLookup.setEnabled(true);
            }
        }
    }
}
