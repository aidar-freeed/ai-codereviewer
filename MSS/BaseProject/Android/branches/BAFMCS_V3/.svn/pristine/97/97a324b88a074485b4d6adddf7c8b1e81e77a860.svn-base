package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.libs.nineoldandroids.view.ViewHelper;
import com.adins.mss.base.R;

/**
 * Created by gigin.ginanjar on 03/09/2016.
 */
public class QuestionGroupViewHolder extends ExpandableRecyclerView.GroupViewHolder {
    public ImageView expandedIndicator;
    public TextView text;
    private boolean expanded;

    public QuestionGroupViewHolder(View view) {
        super(view);
        expandedIndicator = (ImageView) itemView.findViewById(R.id.questionGroupExpandedIndicator);
        text = (TextView) itemView.findViewById(R.id.txtQuestionGroup);
        text.setTypeface(null, Typeface.BOLD);
    }

    public void bind(String questioGroupName) {
        setText(questioGroupName);
    }

    public void expand() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setRotation(expandedIndicator, 180 * (float) (animation.getAnimatedValue()));
                expandedIndicator.postInvalidate();
            }
        });
        animator.start();
        expanded = true;
    }

    public void collapse() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setRotation(expandedIndicator, 180 * (float) (animation.getAnimatedValue()));
                expandedIndicator.postInvalidate();
            }
        });
        animator.start();
        expanded = false;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        ViewHelper.setRotation(expandedIndicator, expanded ? 180 : 0);
        this.expanded = expanded;
    }

    public String getText() {
        return text.getText().toString();
    }

    public void setText(String t) {
        text.setText(t);
    }
}
