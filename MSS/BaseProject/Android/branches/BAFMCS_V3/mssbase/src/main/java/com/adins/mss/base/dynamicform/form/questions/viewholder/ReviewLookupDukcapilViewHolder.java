package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

public class ReviewLookupDukcapilViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout layout;
    public TextView mLabelNo;
    public TextView mQuestionLabel;
    public TextView mQuestionAnswer;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public OnQuestionClickListener listener;

    @Deprecated
    public ReviewLookupDukcapilViewHolder(View itemView) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.lookupDukcapilReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionDukcapilNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDukcapilTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionDukcapilTextAnswer);
    }

    public ReviewLookupDukcapilViewHolder(View itemView, FragmentActivity activity, OnQuestionClickListener listener) {
        super(itemView);
        mActivity = activity;
        layout = (RelativeLayout) itemView.findViewById(R.id.lookupDukcapilReviewLayout);
        mLabelNo = (TextView) itemView.findViewById(R.id.questionDukcapilNoLabel);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDukcapilTextLabel);
        mQuestionAnswer = (TextView) itemView.findViewById(R.id.questionDukcapilTextAnswer);
        this.listener = listener;
    }

    public void bind(final QuestionBean item, final int group, final int number) {
        bean = item;
        mLabelNo.setText(number + ".");
        String qLabel = bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);

        if(bean.getAnswer()!=null && !"".equals(bean.getAnswer())){
            mQuestionAnswer.setText(bean.getAnswer());
        }

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onReviewClickListener(bean, group, number - 1);
            }
        });
    }
}
