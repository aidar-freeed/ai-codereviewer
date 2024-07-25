package com.adins.mss.odr.accounts.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.odr.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class AccountButtonAdapter extends ExpandableRecyclerView.Adapter<RecyclerView.ViewHolder, ButtonOpportunityViewHolder, LeadHistory, String>  {
    private static final int FADE_DURATION = 300; // in milliseconds
    private final LinkedHashMap<String, List<LeadHistory>> mValues;
    private final List<String> mGroups;
    private final FragmentActivity mActivity;
    private final int VIEW_TYPE_LOADING = 999;
    public ScrollingLinearLayoutManager linearLayoutManager;
    public ExpandableRecyclerView mRecyclerView;
    private OnQuestionClickListener mListener;

    public AccountButtonAdapter(FragmentActivity activity, ExpandableRecyclerView recyclerView, LinkedHashMap<String, List<LeadHistory>> items, OnQuestionClickListener listener){
        mActivity=activity;
        mValues=items;
        mListener=listener;
        mGroups=new ArrayList<>();
        mGroups.add("Opportunities");
        mRecyclerView=recyclerView;
    }
    @Override
    public int getGroupItemCount() {
        return mGroups.size()-1;
    }

    @Override
    public int getChildItemCount(int group) {
        return mValues.get(mGroups.get(group)).size();
    }

    @Override
    public String getGroupItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public LeadHistory getChildItem(int group, int position) {
        return mValues.get(getGroupItem(group)).get(position);
    }

    @Override
    protected ButtonOpportunityViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_groupbutton, parent, false);
        return new ButtonOpportunityViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(ButtonOpportunityViewHolder holder, int group) {
        super.onBindGroupViewHolder(holder, group);
        String qGroup = "";
        try {
            qGroup = getGroupItem(group);
        }catch (Exception e){}

        holder.bind(qGroup);
        setFadeAnimation(holder.itemView);
    }

    @Override
    protected LeadHistoryHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lead, parent, false);
       return new LeadHistoryHolder(view);
    }

    @Override
    public int getChildItemViewType(int group, int position) {
        int viewType=1;
        return getChildItem(group, position) == null ? VIEW_TYPE_LOADING : viewType;
    }

    public void onBindChildViewHolder(RecyclerView.ViewHolder mHolder, int group, int position) {
        super.onBindChildViewHolder(mHolder, group, position);
        final LeadHistoryHolder holder = (LeadHistoryHolder) mHolder;
        holder.bind(getChildItem(group, position));
        setFadeAnimation(mHolder.itemView);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
}
