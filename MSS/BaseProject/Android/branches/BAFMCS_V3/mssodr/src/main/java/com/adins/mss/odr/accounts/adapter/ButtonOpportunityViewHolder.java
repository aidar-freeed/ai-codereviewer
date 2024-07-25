package com.adins.mss.odr.accounts.adapter;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.odr.R;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class ButtonOpportunityViewHolder extends ExpandableRecyclerView.GroupViewHolder {
    public TextView text;
    private boolean expanded;

    public ButtonOpportunityViewHolder(View view){
        super(view);
        text = (TextView) itemView.findViewById(R.id.lblListHeader);
    }

    public void bind(String questioGroupName){
        setText(questioGroupName);
    }
    public void expand() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
        expanded = true;
    }

    public void collapse() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
        expanded = false;
    }

    public void setExpanded(boolean expanded) {
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
}