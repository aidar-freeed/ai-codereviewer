package com.adins.mss.base.dynamicform.form.questions;

import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.List;

/**
 * Created by kusnendi.muhamad on 07/08/2017.
 */

public interface QuestionsValidatorInterface {
    public boolean validateAllMandatory(List<QuestionBean> listQuestion, boolean displayMessage);

    public boolean validateCurrentPage(boolean isCekValidate, boolean isSave);

    public List<String> validateGeneratedQuestionView(QuestionBean bean);

    public List<String> validateLookupQuestion(QuestionBean bean);

    public List<String> validateDrawingQuestion(QuestionBean bean);

    public List<String> validateImageQuestion(QuestionBean bean);

    public List<String> validateMultipleQuestion(QuestionBean bean);

    public List<String> validateTextQuestion(QuestionBean bean);

    public List<String> validateTextWithSuggestionQuestion(QuestionBean bean);

    public List<String> validateLocationQuestion(QuestionBean bean);

    public List<String> validateDateTimeQuestion(QuestionBean bean);

    public boolean validateByScript(String answer, String answerType, String script);
}