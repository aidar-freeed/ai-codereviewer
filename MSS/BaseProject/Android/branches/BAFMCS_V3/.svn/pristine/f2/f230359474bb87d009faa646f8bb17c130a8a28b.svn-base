package com.adins.mss.foundation.questiongenerator.form;

import android.content.Context;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.List;

public class RadioQuestionView extends MultiOptionQuestionViewAbstract {

    protected EditText txtDescription;
    private RadioGroup radioGroup;
    private boolean isFromDraft;


    public RadioQuestionView(Context context, QuestionBean bean) {
        super(context, bean);

        radioGroup = new RadioGroup(context);
        this.addView(radioGroup);
    }

    public void setRadioOptions(Context context, List<OptionAnswerBean> listOptions) {
        setOptions(context, listOptions);
    }

    @Override
    public void setOptions(Context context, List<OptionAnswerBean> options) {

        this.options = options;

        QuestionBean bean = getQuestionBean();

        //clear view before adding more option
        radioGroup.removeAllViews();

        int i = 0;
        for (OptionAnswerBean optBean : options) {

            RadioButton rb = new RadioButton(context);
            rb.setText(optBean.getValue());
//			rb.setTextColor(Color.parseColor("#0b5d66"));
            //manually set id
            rb.setId(i);

            if (bean.isReadOnly()) {
                rb.setEnabled(false);
            }

            radioGroup.addView(rb, defLayout);

            i++;
        }

        // set the radiogroup selected, after added.
        int selected = Tool.getSelectedIndex(options);
        if (selected != -1) {
            //Glen 1 Oct 2014, test using provided method from android
            getRadioGroup().check(selected);
        }


        if (bean.isReadOnly()) {
            //Glen 1 Oct 2014, integrate with new class
//			rdGroup.setClickable(false);
            getRadioGroup().setClickable(false);
        }
        //Gigin : supaya pas ganti pilihan radio maka question selanjutnya dihapus
        getRadioGroup().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group != null && group.getCheckedRadioButtonId() != -1) {
                    if (isFromDraft) {
                        isFromDraft = false;
                    } else {
                        setChanged(true);
                    }
                }
            }
        });
        selectSavedOptionsFromBeans(this.getSelectedOptionAnswers());

    }

    @Override
    public void enableDescription(Context context) {
        if (txtDescription == null) {
            txtDescription = new EditText(context);
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                    Global.DEFAULT_MAX_LENGTH)};
            txtDescription.setFilters(inputFilters);
            addView(txtDescription, defLayout);
        }
        txtDescription.setVisibility(VISIBLE);
    }

    @Override
    public void selectOption(int id, String desc) {
        if (radioGroup != null) {
            isFromDraft = true;
            radioGroup.check(id);
        }
        if (desc != null) {
            enableDescription(this.getContext());
            txtDescription.setText(desc);
        }
    }

    @Override
    public void saveSelectedOptionToBean() {
        //id returned is set as the order of elements in options
        int selectedRadioId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioId == -1) {
            setSelectedOptionAnswer(null);
        } else {
            OptionAnswerBean selectedItem = options.get(selectedRadioId);

            //check description if any
            if (txtDescription != null) {
                String description = txtDescription.getText().toString();
//				selectedItem.setValue(description);
                bean.setAnswer(description);
            } else {
//				selectedItem.setValue(null);
            }

            if (selectedItem != null) setSelectedOptionAnswer(selectedItem);
        }
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }


}
