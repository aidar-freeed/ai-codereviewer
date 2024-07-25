package com.adins.mss.base.dynamicform.form.models;

import com.adins.mss.dao.QuestionSet;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

/**
 * Created by gigin.ginanjar on 11/10/2016.
 */

public class LookupAnswerBean extends QuestionBean {
    public boolean canEdit;

    public LookupAnswerBean(QuestionSet questionSet) {
        super(questionSet);
    }

    public LookupAnswerBean(QuestionBean questionBean) {
        super(questionBean);
        setOptionAnswers(questionBean.getOptionAnswers());
        setSelectedOptionAnswers(questionBean.getSelectedOptionAnswers());
        setAnswer(questionBean.getAnswer());
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
