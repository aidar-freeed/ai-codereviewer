package com.adins.mss.base.dynamicform.form;

import android.content.Context;

import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.io.File;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 09/08/2017.
 */

public interface DynamicQuestion {
    public boolean loadQuestionForm();

    public boolean loadQuestionForReview(int targetLastPosition, boolean loadToFinish);

    public boolean isQuestVisibleIfRelevant(String relevantExpression, QuestionBean question);

    public List<OptionAnswerBean> getOptionsForQuestion(QuestionBean bean, boolean firstRequest);

    public List<OptionAnswerBean> GetLookupFromDB(QuestionBean bean, List<String> filters);

    public String replaceModifiers(String sourceString);

    public void syncRvNumber(final QuestionBean bean);

    public void setImageForImageQuestion(String path);

    public void setLocationForLocationQuestion();

    public void processImageFile(File file);

    public void deleteLatestPictureCreate(Context context);

    public void removeItem(int position);

    public void addItem(QuestionBean bean, int position, boolean fromDraft);

    public void changeItem(int position);

    public void notifyInsert(int position);

    public void removeQuestionLabel(int position);

    public String doCalculate(QuestionBean bean);
}
