package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.models.LookupAnswerBean;
import com.adins.mss.base.dynamicform.form.questions.OnQuestionClickListener;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;

import java.util.List;

import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.IsRvMobileQuestion;
import static com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter.isTextOnlineQuestion;

/**
 * Created by gigin.ginanjar on 11/10/2016.
 */

public class LookupCriteriaViewAdapter extends RecyclerView.Adapter {
    FragmentActivity mActivity;
    List<LookupAnswerBean> beanList;
    OnQuestionClickListener mListener;

    public LookupCriteriaViewAdapter(FragmentActivity activity, List<LookupAnswerBean> beanList, OnQuestionClickListener listener) {
        this.mActivity = activity;
        this.beanList = beanList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (QuestionViewAdapter.IsTextQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_text_layout, parent, false);
            return new TextQuestionViewHolder(view);
        } else if (QuestionViewAdapter.IsDropdownQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_dropdown_layout, parent, false);
            return new DropdownQuestionViewHolder(view, mActivity);
        } else if (QuestionViewAdapter.IsMultipleQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_multiple_layout, parent, false);
            return new MultipleQuestionViewHolder(view, mActivity);
        } else if (QuestionViewAdapter.IsRadioQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_radio_layout, parent, false);
            return new RadioQuestionViewHolder(view, mActivity);
        } else if (QuestionViewAdapter.IsDateTimeQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_datetime_layout, parent, false);
            return new DateTimeQuestionViewHolder(mActivity, view);
        } else if (QuestionViewAdapter.IsTextWithSuggestionQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_textwithsuggestion_layout, parent, false);
            return new TextWithSuggestionQuestionViewHolder(view, mActivity);
        } else if (QuestionViewAdapter.IsLookupQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_lookup_layout, parent, false);
            return new LookupQuestionViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsImageQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_image_layout, parent, false);
            return new ImageQuestionViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsLocationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_location_layout, parent, false);
            return new LocationQuestionViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsDrawingQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_drawing_layout, parent, false);
            return new DrawingQuestionViewHolder(view, mActivity, mListener);
        } else if (QuestionViewAdapter.IsValidationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_validation_layout, parent, false);
            return new ValidationQuestionViewHolder(view, mActivity);
        } else if (IsRvMobileQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_digitalreceipt_layout, parent, false);
            return new DigitalReceiptQuestionViewHolder(view, mActivity);
        } else if(isTextOnlineQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_text_online_layout, parent, false);
            return new TextOnlineViewHolder(view, mActivity);
        } else if (QuestionViewAdapter.IsLuOnlineQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_lu_online_layout, parent, false);
            return new LuOnlineQuestionViewHolder(view, mActivity);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position) {
        if (mHolder instanceof TextQuestionViewHolder) {
            final TextQuestionViewHolder holder = (TextQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof DropdownQuestionViewHolder) {
            final DropdownQuestionViewHolder holder = (DropdownQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof RadioQuestionViewHolder) {
            final RadioQuestionViewHolder holder = (RadioQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof MultipleQuestionViewHolder) {
            final MultipleQuestionViewHolder holder = (MultipleQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof TextWithSuggestionQuestionViewHolder) {
            final TextWithSuggestionQuestionViewHolder holder = (TextWithSuggestionQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof DateTimeQuestionViewHolder) {
            final DateTimeQuestionViewHolder holder = (DateTimeQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof LookupQuestionViewHolder) {
            final LookupQuestionViewHolder holder = (LookupQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), 0, position + 1);
        } else if (mHolder instanceof LocationQuestionViewHolder) {
            final LocationQuestionViewHolder holder = (LocationQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), 0, position + 1);
        } else if (mHolder instanceof ImageQuestionViewHolder) {
            final ImageQuestionViewHolder holder = (ImageQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), 0, position + 1);
        } else if (mHolder instanceof DrawingQuestionViewHolder) {
            final DrawingQuestionViewHolder holder = (DrawingQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), 0, position + 1);
        } else if (mHolder instanceof ValidationQuestionViewHolder) {
            final ValidationQuestionViewHolder holder = (ValidationQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if(mHolder instanceof DigitalReceiptQuestionViewHolder) {
            final DigitalReceiptQuestionViewHolder holder = (DigitalReceiptQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof TextOnlineViewHolder) {
            final TextOnlineViewHolder holder = (TextOnlineViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        } else if (mHolder instanceof LuOnlineQuestionViewHolder) {
            final LuOnlineQuestionViewHolder holder = (LuOnlineQuestionViewHolder) mHolder;
            holder.bind(beanList.get(position), position + 1);
        }
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = Integer.valueOf(beanList.get(position).getAnswer_type());
        return viewType;
    }
}
