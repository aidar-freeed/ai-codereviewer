package com.adins.mss.base.dynamicform.form.questions;

import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.ScrollingLinearLayoutManager;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ButtonTextUrlViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.DateTimeQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.DigitalReceiptQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.DrawingQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.DropdownQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ExpandableRecyclerView;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ImageQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LocationQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LookupDukcapilQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LookupQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LuOnlineQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.MultipleQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.QuestionGroupViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.RadioQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.TextOnlineViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.TextQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.TextWithSuggestionQuestionViewHolder;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ValidationQuestionViewHolder;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by gigin.ginanjar on 03/09/2016.
 */
public class QuestionViewAdapter extends ExpandableRecyclerView.Adapter<RecyclerView.ViewHolder, QuestionGroupViewHolder, QuestionBean, String> implements ThemeLoader.ColorSetLoaderCallback {
    private static final int FADE_DURATION = 750; // in milliseconds
    private final LinkedHashMap<String, List<QuestionBean>> mValues;
    private final List<String> mGroups;
    private final FragmentActivity mActivity;
    private final int VIEW_TYPE_LOADING = 999;
    public ScrollingLinearLayoutManager linearLayoutManager;
    public ExpandableRecyclerView mRecyclerView;
    private OnQuestionClickListener mListener;
    private int lastPosition = -1;

    //Edittext colorstatelist object
    public static ColorStateList etBorderColorStateList;

    public QuestionViewAdapter(FragmentActivity activity, ExpandableRecyclerView recyclerView, List<String> groups, LinkedHashMap<String, List<QuestionBean>> items, OnQuestionClickListener listener) {
        mActivity = activity;
        mValues = items;
        mListener = listener;
        mGroups = groups;
        mRecyclerView = recyclerView;
        loadSavedTheme();
    }

    private void loadSavedTheme(){
        ThemeLoader themeLoader = new ThemeLoader(mActivity);
        themeLoader.loadSavedColorSet(this);
    }

    public static boolean IsTextQuestion(String answerType) {
        return answerType.equals(Global.AT_TEXT) ||
                answerType.equals(Global.AT_TEXT_MULTILINE) ||
                answerType.equals(Global.AT_CURRENCY) ||
                answerType.equals(Global.AT_NUMERIC) ||
                answerType.equals(Global.AT_DECIMAL) ||
                answerType.equals(Global.AT_PDF);
    }

    public static boolean IsDropdownQuestion(String answerType) {
        return answerType.equals(Global.AT_DROPDOWN) ||
                answerType.equals(Global.AT_DROPDOWN_W_DESCRIPTION);
    }

    public static boolean IsMultipleQuestion(String answerType) {
        return answerType.equals(Global.AT_MULTIPLE) ||
                answerType.equals(Global.AT_MULTIPLE_ONE_DESCRIPTION) ||
                answerType.equals(Global.AT_MULTIPLE_W_DESCRIPTION);
    }

    public static boolean IsRadioQuestion(String answerType) {
        return answerType.equals(Global.AT_RADIO) ||
                answerType.equals(Global.AT_RADIO_W_DESCRIPTION);
    }

    public static boolean IsImageQuestion(String answerType) {
        return answerType.equals(Global.AT_IMAGE) ||
                answerType.equals(Global.AT_IMAGE_W_GPS_ONLY) ||
                answerType.equals(Global.AT_IMAGE_W_LOCATION) ||
                answerType.equals(Global.AT_ID_CARD_PHOTO);
    }

    public static boolean IsLookupQuestion(String answerType) {
        return answerType.equals(Global.AT_LOV) ||
                answerType.equals(Global.AT_LOV_W_FILTER) ||
                answerType.equals(Global.AT_LOOKUP) ||
                answerType.equals(Global.AT_LOOKUP_DUKCAPIL);
    }

    public static boolean IsTextQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_TEXT) ||
                answerType == Integer.valueOf(Global.AT_TEXT_MULTILINE) ||
                answerType == Integer.valueOf(Global.AT_CURRENCY) ||
                answerType == Integer.valueOf(Global.AT_NUMERIC) ||
                answerType == Integer.valueOf(Global.AT_DECIMAL) ||
                answerType == Integer.valueOf(Global.AT_PDF);
    }

    public static boolean IsDropdownQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_DROPDOWN) ||
                answerType == Integer.valueOf(Global.AT_DROPDOWN_W_DESCRIPTION);
    }

    public static boolean IsMultipleQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_MULTIPLE) ||
                answerType == Integer.valueOf(Global.AT_MULTIPLE_ONE_DESCRIPTION) ||
                answerType == Integer.valueOf(Global.AT_MULTIPLE_W_DESCRIPTION);
    }

    public static boolean IsRadioQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_RADIO) ||
                answerType == Integer.valueOf(Global.AT_RADIO_W_DESCRIPTION);
    }

    public static boolean IsImageQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_IMAGE) ||
                answerType == Integer.valueOf(Global.AT_IMAGE_W_GPS_ONLY) ||
                answerType == Integer.valueOf(Global.AT_IMAGE_W_LOCATION) ||
                answerType == Integer.valueOf(Global.AT_ID_CARD_PHOTO);
    }

    public static boolean IsDateTimeQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_DATE) ||
                answerType == Integer.valueOf(Global.AT_DATE_TIME) ||
                answerType == Integer.valueOf(Global.AT_TIME);
    }

    public static boolean IsLocationQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_LOCATION);
    }

    public static boolean IsDrawingQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_DRAWING);
    }

    public static boolean IsTextWithSuggestionQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_TEXT_WITH_SUGGESTION);
    }

    public static boolean IsLookupQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_LOV) ||
                answerType == Integer.valueOf(Global.AT_LOV_W_FILTER) ||
                answerType == Integer.valueOf(Global.AT_LOOKUP) ||
                answerType == Integer.valueOf(Global.AT_LOOKUP_DUKCAPIL);
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
        setFadeAnimation2(holder.itemView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        if (IsTextQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_text_layout, parent, false);
            return new TextQuestionViewHolder(mActivity, view, mRecyclerView);
        } else if (IsDropdownQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_dropdown_layout, parent, false);
            return new DropdownQuestionViewHolder(view, mActivity);
        } else if (IsMultipleQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_multiple_layout, parent, false);
            return new MultipleQuestionViewHolder(view, mActivity);
        } else if (IsImageQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_image_layout, parent, false);
            return new ImageQuestionViewHolder(view, mActivity, mListener);
        } else if (IsRadioQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_radio_layout, parent, false);
            return new RadioQuestionViewHolder(view, mActivity);
        } else if (IsLocationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_location_layout, parent, false);
            return new LocationQuestionViewHolder(view, mActivity, mListener);
        } else if (IsDrawingQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_drawing_layout, parent, false);
            return new DrawingQuestionViewHolder(view, mActivity, mListener);
        } else if (IsDateTimeQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_datetime_layout, parent, false);
            return new DateTimeQuestionViewHolder(mActivity, view);
        } else if (IsTextWithSuggestionQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_textwithsuggestion_layout, parent, false);
            return new TextWithSuggestionQuestionViewHolder(view, mActivity);
        } else if (IsLookupQuestion(viewType)) {
            if(viewType == Integer.valueOf(Global.AT_LOOKUP_DUKCAPIL)){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_lookup_dukcapil_layout, parent, false);
                return new LookupDukcapilQuestionViewHolder(view, mActivity, mListener);
            } else{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_question_lookup_layout, parent, false);
                return new LookupQuestionViewHolder(view, mActivity, mListener);
            }
        } else if (IsValidationQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_validation_layout, parent, false);
            return new ValidationQuestionViewHolder(view, mActivity);
        } else if (IsRvMobileQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_digitalreceipt_layout, parent, false);
            return new DigitalReceiptQuestionViewHolder(view, mActivity);
        } else if (isTextOnlineQuestion(viewType)){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_text_online_layout, parent, false);
            return new TextOnlineViewHolder(view, mActivity);
        } else if (IsLuOnlineQuestion(viewType)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_lu_online_layout, parent, false);
            return new LuOnlineQuestionViewHolder(view, mActivity);
        }
        else if(IsButtonViewUrlQuestion(viewType)){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_button_text_url_layout, parent, false);
            return new ButtonTextUrlViewHolder(view, mActivity);
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
        if (mHolder instanceof TextQuestionViewHolder) {
            final TextQuestionViewHolder holder = (TextQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position),position + 1);
        } else if (mHolder instanceof DropdownQuestionViewHolder) {
            final DropdownQuestionViewHolder holder = (DropdownQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof RadioQuestionViewHolder) {
            final RadioQuestionViewHolder holder = (RadioQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof MultipleQuestionViewHolder) {
            final MultipleQuestionViewHolder holder = (MultipleQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof LocationQuestionViewHolder) {
            final LocationQuestionViewHolder holder = (LocationQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof ImageQuestionViewHolder) {
            final ImageQuestionViewHolder holder = (ImageQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof DrawingQuestionViewHolder) {
            final DrawingQuestionViewHolder holder = (DrawingQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof TextWithSuggestionQuestionViewHolder) {
            final TextWithSuggestionQuestionViewHolder holder = (TextWithSuggestionQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof DateTimeQuestionViewHolder) {
            final DateTimeQuestionViewHolder holder = (DateTimeQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof LookupQuestionViewHolder) {
            final LookupQuestionViewHolder holder = (LookupQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        }else if (mHolder instanceof LookupDukcapilQuestionViewHolder) {
            final LookupDukcapilQuestionViewHolder holder = (LookupDukcapilQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), group, position + 1);
        } else if (mHolder instanceof ValidationQuestionViewHolder) {
            final ValidationQuestionViewHolder holder = (ValidationQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if(mHolder instanceof DigitalReceiptQuestionViewHolder) {
            final DigitalReceiptQuestionViewHolder holder = (DigitalReceiptQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof TextOnlineViewHolder) {
            final TextOnlineViewHolder holder = (TextOnlineViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        } else if (mHolder instanceof LuOnlineQuestionViewHolder) {
            final LuOnlineQuestionViewHolder holder = (LuOnlineQuestionViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        }
        else if(mHolder instanceof ButtonTextUrlViewHolder) {
            final ButtonTextUrlViewHolder holder = (ButtonTextUrlViewHolder) mHolder;
            holder.bind(getChildItem(group, position), position + 1);
        }
        setFadeAnimation(mHolder.itemView, position);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void setFadeAnimation(View view, int position) {
        if (position > lastPosition) {
            lastPosition = position;
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(FADE_DURATION);
            view.startAnimation(anim);
        } else if (lastPosition > getItemCount() - 1) {
            lastPosition = getItemCount() - 1;
        } else {
//            setScaleAnimation(view);
            setFadeAnimation2(view);
        }
    }

    private void setFadeAnimation2(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION / 2);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(150);
        view.startAnimation(anim);
    }

    private void createEditTextBorderColorStateList(DynamicTheme dynamicTheme){
        if(dynamicTheme == null){
            return;
        }
        int etBorderFocusedColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"et_border_focused"));
        int etBorderDisabledColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"et_border_disabled"));
        int etBorderNormalColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"et_border_normal"));
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused},  // focused
                new int[] {-android.R.attr.state_enabled}, //disabled
                new int[] {}  // normal
        };

        int[] etbordercolorlist = new int[]{
            etBorderFocusedColor,etBorderDisabledColor,etBorderNormalColor
        };
        etBorderColorStateList = new ColorStateList(states,etbordercolorlist);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null && dynamicTheme.getThemeItemList().size() > 0){
            createEditTextBorderColorStateList(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {

    }

    public static boolean IsValidationQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_VALIDATION);
    }

    public static boolean IsRvMobileQuestion(int answerType) {
        return answerType == Integer.valueOf(Global.AT_RV_MOBILE);
    }

    public  static boolean isTextOnlineQuestion(int answerType){
        return answerType == Integer.valueOf(Global.AT_TEXT_ONLINE);
    }

    public static boolean IsButtonViewUrlQuestion(int answerType){
        return  answerType == Integer.valueOf(Global.AT_BUTTON_VIEW_URL);
    }

    public static boolean IsLuOnlineQuestion(int answerTYpe) {
        return answerTYpe == Integer.valueOf(Global.AT_LOOKUP_ONLINE);
    }
}
