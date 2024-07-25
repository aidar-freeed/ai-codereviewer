package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

/**
 * Created by gigin.ginanjar on 13/10/2016.
 */

public class ReviewLookupViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout layout;
    public TextView mLabelNo;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public TableLayout criteriaTableLayout;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener listener;

    @Deprecated
    public ReviewLookupViewHolder(View itemView) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.lookupReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionTextAnswer);
        criteriaTableLayout = (TableLayout) itemView.findViewById(R.id.reviewLookupAnswerLayout);
    }

    public ReviewLookupViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mActivity = activity;
        layout = (RelativeLayout) itemView.findViewById(R.id.lookupReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionTextAnswer);
        criteriaTableLayout = (TableLayout) itemView.findViewById(R.id.reviewLookupAnswerLayout);
        this.listener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        mLabelNo.setText(number + ".");
        String qLabel = bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);

        String answerType = bean.getAnswer_type();

        String qAnswer = bean.getAnswer();
        criteriaTableLayout.removeAllViews();
        if (qAnswer != null && !qAnswer.isEmpty()) {
            mQuestionAnswer.setVisibility(View.GONE);
            criteriaTableLayout.setVisibility(View.VISIBLE);
            String[] values = Tool.split(qAnswer, Global.DELIMETER_ROW);
            String[] lookupCode = Tool.split(values[0], Global.DELIMETER_DATA_LOOKUP);
            String[] lookupValue = Tool.split(values[1], Global.DELIMETER_DATA_LOOKUP);
            StringBuilder lovCode = new StringBuilder();
            StringBuilder lovValue = new StringBuilder();
            for (int i = 0; i < lookupCode.length; i++) {
                TableRow row = (TableRow) LayoutInflater.from(mActivity).inflate(R.layout.lookup_criteria_row, criteriaTableLayout, false);
                String code = lookupCode[i];
                if (lovCode.length() != 0)
                    lovCode.append("\n");
                lovCode.append(code);

                String value = lookupValue[i] != null ? lookupValue[i] : "";
                if (lovValue.length() != 0)
                    lovValue.append("\n");
                lovValue.append(value);

                TextView textDesc = (TextView) row.findViewById(R.id.fieldValue);
                textDesc.setText(value);
                if (i % 2 == 1) {
                    row.setBackgroundResource(R.color.tv_gray_light);
                } else if (i % 2 == 0) {
                    row.setBackgroundResource(R.color.tv_gray);
                }
                criteriaTableLayout.addView(row);
            }
        } else {
            mQuestionAnswer.setVisibility(View.VISIBLE);
            mQuestionAnswer.setText(mActivity.getString(R.string.no_answer_found));
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewClickListener(bean, group, number - 1);
            }
        });
    }
}
