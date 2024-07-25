package com.adins.mss.foundation.questiongenerator.form;

import android.content.Context;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiOptionQuestionViewAbstract extends QuestionView {

    protected List<OptionAnswerBean> options;
    protected QuestionBean bean;

    public MultiOptionQuestionViewAbstract(Context context, QuestionBean bean) {
        super(context, bean);
        this.bean = bean;
    }

    public abstract void setOptions(Context context, List<OptionAnswerBean> options);

    //commented, use saveAnswerToBean instead
    public abstract void saveSelectedOptionToBean();

    public abstract void selectOption(int id, String desc);


    public void selectOption(String lovCode, String description) {
        int indexOfOption = -1;
        int i = 0;
        for (OptionAnswerBean optAnsBean : options) {
            if (lovCode.equals(optAnsBean.getCode())) {
                indexOfOption = i;
                break;
            }
            i++;
        }
        if (indexOfOption > -1) selectOption(i, description);
    }

    public OptionAnswerBean getSelectedOptionAnswer() {
        //Change to Array
//		return this.questionBean.getSelectedOptionAnswer();
        OptionAnswerBean result = null;
        List<OptionAnswerBean> selectedOptions = this.questionBean.getSelectedOptionAnswers();
        if (selectedOptions != null && selectedOptions.size() > 0) {
            result = this.questionBean.getSelectedOptionAnswers().get(0);
        }

        return result;
    }

    public void setSelectedOptionAnswer(OptionAnswerBean option) {

        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<OptionAnswerBean>();
        if (option != null) {
            selectedOptionAnswers.add(option);
            questionBean.setLovCode(option.getCode());
            questionBean.setLookupId(option.getUuid_lookup());
        } else {
            questionBean.setLovCode(null);
            questionBean.setLookupId(null);
        }
        setSelectedOptionAnswers(selectedOptionAnswers);
    }

    public List<OptionAnswerBean> getSelectedOptionAnswers() {
        return this.questionBean.getSelectedOptionAnswers();
    }

    public void setSelectedOptionAnswers(List<OptionAnswerBean> options) {

        //set OptionAnswerBean as selected in case old logic still exist
        for (OptionAnswerBean option : options) {
            if (option != null) option.setSelected(true);
        }

        this.questionBean.setSelectedOptionAnswers(options);
        //we don't set the lovCode, because there are multiple of them
    }

    //Description textfield are handled in each subclass for now, changed to abstract
//	public void enableDescription(Context context){
    public abstract void enableDescription(Context context);

    //This method select all options with lovCode in beans. Need to be called manually form subclass
    //We code to use parameter instead of own selectedOptionAnswers in case it need to be from other source
    public void selectSavedOptionsFromBeans(List<OptionAnswerBean> beans) {

        if (beans == null) return;

        for (OptionAnswerBean optAnsBean : beans) {
            String lovCode = optAnsBean.getCode();
            String description = null;
            if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                    Global.AT_RADIO_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                    Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())) {
//				description = optAnsBean.getValue();
                description = bean.getAnswer();
            }
            selectOption(lovCode, description);
        }
    }

}
