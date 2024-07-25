package com.adins.mss.foundation.questiongenerator.form;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.List;


public class DropdownQuestionView extends MultiOptionQuestionViewAbstract {

    protected EditText txtDescription;
    private Spinner spinner;
    private TextView labelNoList;
    private List<OptionAnswerBean> mOptions;

    public DropdownQuestionView(Context context, QuestionBean bean) {
        super(context, bean);

        spinner = new Spinner(context);
        spinner.setPrompt("Select One");        //default prompt
        if (bean.isReadOnly()) {
            spinner.setClickable(false);
            spinner.setEnabled(false);
        }
        addView(spinner, defLayout);
        labelNoList = new TextView(context);
        labelNoList.setText("The list is not available, contact your administrator");
        labelNoList.setVisibility(View.GONE);
    }

    //Use setOptions instead
    public void setSpinnerOptions(Context context, List<OptionAnswerBean> options) {
        setOptions(context, options);
    }

    //Set as override of abstract method
    @Override
    public void setOptions(Context context, List<OptionAnswerBean> options) {

        this.options = options;


        int nextOptionIndex = -1;
        try {
            QuestionBean bean = this.getQuestionBean();
            String optionSelectedIdStr = QuestionBean.getAnswer(bean);
            if (optionSelectedIdStr == null)
                optionSelectedIdStr = bean.getLovCode();
            if (!options.isEmpty()) {
                //search new options selected id

                for (int i = 0; i < options.size(); i++) {
                    OptionAnswerBean option = options.get(i);
                    if (option.getCode().equalsIgnoreCase(optionSelectedIdStr)) {        //this is the same option (based on id)
                        nextOptionIndex = i;
                        break;
                    }
                }
                try {
                    labelNoList.setVisibility(View.GONE);
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            } else {
                labelNoList.setVisibility(View.VISIBLE);
                addView(labelNoList);
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }

        ArrayAdapter<OptionAnswerBean> spAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_style2, options);
        spAdapter.setDropDownViewResource(R.layout.spinner_style);
        spinner.setAdapter(spAdapter);

        //Glen 1 Sept 2014, set selected id on new spinner items
        if (nextOptionIndex >= 0 && nextOptionIndex < options.size()) {
            spinner.setSelection(nextOptionIndex);
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if (arg1 != null && arg1.getId() != 0 && !bean.isReadOnly()) {
                    //do your code here to avoid callback twice
                    setChanged(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //EMPTY
            }
        });
        this.mOptions = options;
        //Glen 14 Oct 2014, select saved selected options
        selectSavedOptionsFromBeans(this.getSelectedOptionAnswers());
    }

    @Override
    public void selectOption(int id, String desc) {
        if (spinner != null) {
            spinner.setSelection(id);
        }
        //description
        if (desc != null) {
            enableDescription(this.getContext());    // use same context as view
            txtDescription.setText(bean.getAnswer());
        }
    }

    @Override
    public void saveSelectedOptionToBean() {
        OptionAnswerBean selected = (OptionAnswerBean) spinner.getSelectedItem();

        //check description if any
        if (txtDescription != null) {
            String description = txtDescription.getText().toString();
            bean.setAnswer(description);
        }

        setSelectedOptionAnswer(selected);
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

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }


}
