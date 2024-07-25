package com.adins.mss.foundation.questiongenerator.form;

import android.content.Context;
import android.text.InputFilter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.ArrayList;
import java.util.List;

public class MultipleQuestionView extends MultiOptionQuestionViewAbstract {

    private boolean withDescription = false;
    private boolean withOneDescription = false;
    private LinearLayout optionGroup;
    private List<CheckBox> checkBoxes;
    private List<EditText> txtDescriptions;

    private List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();

    public MultipleQuestionView(Context context, QuestionBean bean) {
        super(context, bean);
        this.bean = bean;
        optionGroup = new LinearLayout(context);
        addView(optionGroup, defLayout);
        optionGroup.setOrientation(VERTICAL);

        checkBoxes = new ArrayList<>();
        txtDescriptions = new ArrayList<>();
    }

    @Override
    public void setOptions(Context context, List<OptionAnswerBean> options) {

        this.options = options;

        //clear option before set a new one
        optionGroup.removeAllViews();
        checkBoxes.clear();
        int i = 0;
        String[] arrSelectedAnswer = null;
        List<OptionAnswerBean> optSelectedBean = null;
        try {
            arrSelectedAnswer = Tool.split(bean.getAnswer(), Global.DELIMETER_DATA);
            optSelectedBean = bean.getSelectedOptionAnswers();
        } catch (Exception e) {
            FireCrash.log(e);
            arrSelectedAnswer = new String[0];
            optSelectedBean = new ArrayList<>();
        }
        for (OptionAnswerBean optBean : options) {
            CheckBox chk = new CheckBox(context);
            chk.setText(optBean.getValue());
            chk.setChecked(optBean.isSelected());
            optionGroup.addView(chk);
            checkBoxes.add(chk);
            String optBeanId = optBean.getUuid_lookup();
            if (withDescription) {
                EditText desc = new EditText(context);
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                        Global.DEFAULT_MAX_LENGTH)};
                desc.setFilters(inputFilters);
                qloop:
                for (OptionAnswerBean nOptBean : optSelectedBean) {
                    String nOptBeanId = nOptBean.getUuid_lookup();
                    if (optBeanId.equals(nOptBeanId) && nOptBean.isSelected()) {
                        desc.setText(arrSelectedAnswer[i]);
                        i++;
                        break qloop;
                    }
                }

                optionGroup.addView(desc, defLayout);
                txtDescriptions.add(desc);
            }
        }
        if (withOneDescription) {
            EditText desc = new EditText(context);
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    Global.DEFAULT_MAX_LENGTH)};
            desc.setFilters(inputFilters);
            try {
                desc.setText(bean.getAnswer());
            } catch (Exception e) {
                FireCrash.log(e);
            }
            optionGroup.addView(desc, defLayout);
            txtDescriptions.add(desc);
        }

        //Glen 14 Oct 2014, select saved selected options
        selectSavedOptionsFromBeans(getSelectedOptionAnswers());

    }

    @Override
    public void enableDescription(Context context) {
        withDescription = true;
    }

    public void enableOneDescription(Context context) {
        withOneDescription = true;
    }

    @Override
    public void selectOption(int id, String desc) {
        if (checkBoxes != null && checkBoxes.size() > id) {
            checkBoxes.get(id).setChecked(true);
        }
    }

    @Override
    public void saveSelectedOptionToBean() {
        selectedOptionAnswers.clear();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox chk = checkBoxes.get(i);
            if (chk.isChecked()) {
                OptionAnswerBean optAnsBean = options.get(i);        //assume checkbox is the same order as options

                if (sb.length() > 0) {
                    sb.append(Global.DELIMETER_DATA);
                }

                if (withDescription) {
                    EditText txtDescription = txtDescriptions.get(i);
                    String description = txtDescription.getText().toString();
                    sb.append(description);
                }
                optAnsBean.setSelected(true);

                selectedOptionAnswers.add(optAnsBean);


            }
        }
        questionBean.setAnswer(sb.toString());
        if (withOneDescription) {
            EditText txtDescription = txtDescriptions.get(0);
            String description = txtDescription.getText().toString();
            questionBean.setAnswer(description);
        }

        //Glen 23 Oct 2014
        questionBean.setLovCode(sb.toString());

        questionBean.setSelectedOptionAnswers(selectedOptionAnswers);
    }

}
