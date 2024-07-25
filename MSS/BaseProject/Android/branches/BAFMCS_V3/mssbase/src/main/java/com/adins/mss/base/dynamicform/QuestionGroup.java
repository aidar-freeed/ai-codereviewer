package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.questiongenerator.QuestionBean;

/**
 * Created by gigin.ginanjar on 18/07/2016.
 */
public class QuestionGroup {
    private String question_group_id;
    private String question_group_name;
    private Integer question_group_order;

    public QuestionGroup(QuestionBean bean) {
        this.question_group_id = bean.getQuestion_group_id();
        this.question_group_name = bean.getQuestion_group_name();
        this.question_group_order = bean.getQuestion_group_order();
    }

    public String getQuestion_group_id() {
        return question_group_id;
    }

    public void setQuestion_group_id(String question_group_id) {
        this.question_group_id = question_group_id;
    }

    public String getQuestion_group_name() {
        return question_group_name;
    }

    public void setQuestion_group_name(String question_group_name) {
        this.question_group_name = question_group_name;
    }

    public Integer getQuestion_group_order() {
        return question_group_order;
    }

    public void setQuestion_group_order(Integer question_group_order) {
        this.question_group_order = question_group_order;
    }
}