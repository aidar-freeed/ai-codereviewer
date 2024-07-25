package com.adins.mss.foundation.questiongenerator.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.dynamicform.QuestionGroup;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.HashMap;


public class QuestionView extends LinearLayout {

    protected QuestionBean questionBean;
    protected QuestionGroup questionGroup;
    protected QuestionViewListener listener;
    protected TextView label;
    //Glen 10 Oct 2014
    protected LayoutParams defLayout = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    protected String QuestionId;
    protected HashMap<String, Object> hashMap;
    protected int sequence = 0;
    protected boolean isChanged = false;
    //ini dikarenakan kasus :
    //saat buat onselecttecItem, listener sudah kepanggil pada saat dropdown kebentuk
    //padahal seharusnya belum boleh
    //segingga di kasih flag dulu
    //bangkit 15 des 14
    protected int isCanSetFlag = 0;
    protected boolean titleOnly;
    protected boolean expanded;

    public QuestionView(Context context) {
        super(context);
//		setExpanded(true);
    }

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public QuestionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //Glen 14 Oct 2014, enforce to initiate with bean
    //bangkit 16 des temporary not used, concern to memory usage
    public QuestionView(Context context, QuestionBean bean) {
        super(context);

        setOrientation(VERTICAL);

        //moved from subclass
        label = new TextView(context);
//		label.setTextColor(Color.parseColor("#0b5d66"));
        this.addView(label, defLayout);

        this.questionBean = bean;
//		setExpanded(true);
    }

    public QuestionGroup getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(QuestionGroup questionGroup) {
        this.questionGroup = questionGroup;
    }

    public boolean isTitleOnly() {
        return titleOnly;
    }

    public void setTitleOnly(boolean titleOnly) {
        this.titleOnly = titleOnly;
    }

    public TextView getLabel() {
        return label;
    }

    public void setLabel(TextView label) {
        this.label = label;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        if (isCanSetFlag >= 1) {
            this.isChanged = isChanged;
        } else {
            isCanSetFlag++;
        }
    }

    public int getIsCanSetFlag() {
        return isCanSetFlag;
    }

    public void setIsCanSetFlag(int isCanSetFlag) {
        this.isCanSetFlag = isCanSetFlag;
    }

    //=== Getter & Setter ===//

    public QuestionBean getQuestionBean() {
        return questionBean;
    }

    public void setQuestionBean(QuestionBean questionBean) {
        this.questionBean = questionBean;
    }

    public QuestionViewListener getListener() {
        return listener;
    }

    public void setListener(QuestionViewListener listener) {
        this.listener = listener;
    }

    public String getLabelText() {
        return label.getText().toString();
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    //Use this method to change visibility, as it usually needed in QuestionViewGenerator
    //not to confuse with View.setVisibility. this.setVisible(true) is the same as setVisibility(VISIBLE), and false as GONE
    public void setVisible(boolean isVisible) {
        if (isVisible) {
            this.setVisibility(VISIBLE);
        } else {
            this.setVisibility(GONE);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    interface QuestionViewListener {
        void onFinishEdit(QuestionView questionView, QuestionBean questionBean);
    }
}