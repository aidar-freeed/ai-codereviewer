package com.adins.mss.odr.accounts.adapter;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.libs.nineoldandroids.view.ViewHelper;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.odr.R;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class LeadHistoryHolder extends ExpandableRecyclerView.GroupViewHolder {
    public ImageView expandedIndicator;
    public TextView text;
    public TextView text2;
    public TextView text3;
    private boolean expanded;

    public LeadHistoryHolder(View view){
        super(view);
        expandedIndicator = (ImageView) itemView.findViewById(R.id.leadHistoryExpandedIndicator);
        text = (TextView) itemView.findViewById(R.id.txtStatusTaskAcc);
        text2 = (TextView) itemView.findViewById(R.id.txtProductAcc);
        text3 = (TextView) itemView.findViewById(R.id.txtDateAcc);
    }

    public void bind(LeadHistory leadHistory){
        setText(leadHistory.getLeadStatus());
        setText2(leadHistory.getLeadProduct());
        setText3(leadHistory.getLeadLastDate());
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

    public void setExpanded(boolean expanded) {
        ViewHelper.setRotation(expandedIndicator, expanded ? 180 : 0);
        this.expanded = expanded;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    public void setText(String t) {
        text.setText(t);
    }

    public String getText() {
        return text.getText().toString();
    }

    public void setText2(String t) {
        text2.setText(t);
    }

    public String getText2() {
        return text2.getText().toString();
    }

    public void setText3(String t) {
        text3.setText(t);
    }

    public String getText3() {
        return text3.getText().toString();
    }
}
