package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.List;

/**
 * Temporary Data for Dynamic Question Form
 */
public class DataForDynamicQuestion {

    /**
     * Property selectedForm
     */
    FormBean selectedForm;

    /**
     * Property listOfQuestion
     */
    List<QuestionBean> listOfQuestion;

    /**
     * Property selectedHeader
     */
    SurveyHeaderBean selectedHeader;

    /**
     * Property mode
     */
    int mode;

    /**
     * Gets the selectedForm
     */
    public FormBean getSelectedForm() {
        return this.selectedForm;
    }

    /**
     * Sets the selectedForm
     */
    public void setSelectedForm(FormBean value) {
        this.selectedForm = value;
    }

    /**
     * Gets the listOfQuestion
     */
    public List<QuestionBean> getListOfQuestion() {
        return this.listOfQuestion;
    }

    /**
     * Sets the listOfQuestion
     */
    public void setListOfQuestion(List<QuestionBean> value) {
        this.listOfQuestion = value;
    }

    /**
     * Gets the selectedHeader
     */
    public SurveyHeaderBean getSelectedHeader() {
        return this.selectedHeader;
    }

    /**
     * Sets the selectedHeader
     */
    public void setSelectedHeader(SurveyHeaderBean value) {
        this.selectedHeader = value;
    }

    /**
     * Gets the mode
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Sets the mode
     */
    public void setMode(int value) {
        this.mode = value;
    }
}