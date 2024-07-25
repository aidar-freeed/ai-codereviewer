package com.adins.mss.base.dynamicform.form.questions;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.questions.viewholder.DigitalReceiptQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.base.dynamicform.form.questions.viewholder.QuestionGroupViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ReviewImageViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ReviewLookupDukcapilViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ReviewLookupViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ReviewLuOnlineQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ReviewTextViewHolder;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.LinkedHashMap;
import java.util.List;

import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.IsRvMobileQuestion;
import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.isTextOnlineQuestion;

/**
 * Created by gigin.ginanjar on 06/09/2016.
 */
public class QuestionReviewAdapter extends ExpandableRecyclerView.Adapter<RecyclerView.ViewHolder, QuestionGroupViewHolder, QuestionBean, String> {
    private static final int FADE_DURATION = 300; // in milliseconds
    private final LinkedHashMap<String, List<QuestionBean>> mValues;
    private final List<String> mGroups;
    private final FragmentActivity mActivity;
    private final int VIEW_TYPE_LOADING = 999;
    public ScrollingLinearLayoutManager linearLayoutManager;
    public ExpandableRecyclerView mRecyclerView;
    private OnQuestionClickListener mListener;

    public QuestionReviewAdapter(FragmentActivity activity, ExpandableRecyclerView recyclerView, List<String> groups, LinkedHashMap<String, List<QuestionBean>> items, OnQuestionClickListener listener) {
        mActivity = activity;
        mValues = items;
        mListener = listener;
        mGroups = groups;
        mRecyclerView = recyclerView;
    }

    @Override
    public int getGroupItemCount() {
        return mGroups.size() - 1;
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
    public QuestionBean getChildItem(int group, int position) {
        return mValues.get(getGroupItem(group)).get(position);
    }

    @Override
    protected QuestionGroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_group_layout, parent, false);
        return new QuestionGroupViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(QuestionGroupViewHolder holder, int group) {
        super.onBindGroupViewHolder(holder, group);
        String qGroup = "";
        try {
            qGroup = getGroupItem(group);
        } catch (Exception e) {
            FireCrash.log(e);
        }

        holder.bind(qGroup);
        setFadeAnimation(holder.itemView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        if (QuestionViewAdapter.IsTextQuestion(viewType) || isTextOnlineQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsDropdownQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsMultipleQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsImageQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_image_layout, parent, false);
            return new ReviewImageViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsRadioQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsLocationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsDrawingQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_image_layout, parent, false);
            return new ReviewImageViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsDateTimeQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsTextWithSuggestionQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        } else if (QuestionViewAdapter.IsLookupQuestion(viewType)) {
            if(Integer.parseInt(Global.AT_LOOKUP_DUKCAPIL) == viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_review_lookup_dukcapil_layout, parent, false);
                return new ReviewLookupDukcapilViewHolder(view, mActivity, mListener);
            }else{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_review_lookup_layout, parent, false);
                return new ReviewLookupViewHolder(view, mActivity, mListener);
            }
        } else if (QuestionViewAdapter.IsValidationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(view, mListener);
        } else if (QuestionViewAdapter.IsRvMobileQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(view, mListener);
        } else if (QuestionViewAdapter.IsLuOnlineQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_review_lu_online_layout, parent, false);
            return new ReviewLuOnlineQuestionViewHolder(view, mActivity, mListener);
        } else if(QuestionViewAdapter.IsButtonViewUrlQuestion(viewType)){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_review_text_layout, parent, false);
            return new ReviewTextViewHolder(mActivity, view, mListener);
        }
        return null;
    }

    @Override
    public int getChildItemViewType(int group, int position) {
        int viewType = Integer.valueOf(getChildItem(group, position).getAnswer_type());
        return getChildItem(group, position) == null ? VIEW_TYPE_LOADING : viewType;
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder mHolder, int group, int position) {
        super.onBindChildViewHolder(mHolder, group, position);
        if (mHolder instanceof ReviewTextViewHolder) {
            final ReviewTextViewHolder holder = (ReviewTextViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof ReviewImageViewHolder) {
            final ReviewImageViewHolder holder = (ReviewImageViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof ReviewLookupViewHolder) {
            final ReviewLookupViewHolder holder = (ReviewLookupViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof ReviewLuOnlineQuestionViewHolder) {
            final ReviewLuOnlineQuestionViewHolder holder = (ReviewLuOnlineQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        }
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
