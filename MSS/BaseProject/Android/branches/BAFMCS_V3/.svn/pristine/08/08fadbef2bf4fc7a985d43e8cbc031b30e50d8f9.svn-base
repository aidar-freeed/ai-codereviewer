package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by gigin.ginanjar on 01/09/2016.
 */
public class MultipleQuestionViewHolder extends RecyclerView.ViewHolder implements TextWatcher {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mMultipleEmpty;
    public LinearLayout mMultipleLayout;
    public EditText mDescription;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    protected List<OptionAnswerBean> options;
    protected LinearLayout.LayoutParams defLayout = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private List<CheckBox> listCheckBox;
    private List<EditText> listDescription;
    private List<OptionAnswerBean> selectedOptionAnswers;

    @Deprecated
    public MultipleQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionMultipleLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionMultipleLabel);
        mMultipleEmpty = (TextView) itemView.findViewById(R.id.questionMultipleEmpty);
        mMultipleLayout = (LinearLayout) itemView.findViewById(R.id.multipleQuestionListLayout);
        mDescription = (EditText) itemView.findViewById(R.id.questionMultipleDescription);
        listCheckBox = new ArrayList<>();
        listDescription = new ArrayList<>();
        selectedOptionAnswers = new ArrayList<>();
    }

    public MultipleQuestionViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionMultipleLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionMultipleLabel);
        mMultipleEmpty = (TextView) itemView.findViewById(R.id.questionMultipleEmpty);
        mMultipleLayout = (LinearLayout) itemView.findViewById(R.id.multipleQuestionListLayout);
        mDescription = (EditText) itemView.findViewById(R.id.questionMultipleDescription);
        mActivity = activity;
        listCheckBox = new ArrayList<>();
        listDescription = new ArrayList<>();
        selectedOptionAnswers = new ArrayList<>();
    }

    public void bind(final QuestionBean item, int number) {
        selectedOptionAnswers = new ArrayList<>();
        bean = item;
        options = bean.getOptionAnswers();
        String answerType = bean.getAnswer_type();

        boolean withDescription = false;
        boolean withOneDescription = false;
        if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)) {
            withDescription = true;
        }

        if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
            withOneDescription = true;
        }

        String qLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);
        mMultipleLayout.removeAllViews();
        listCheckBox.clear();
        listDescription.clear();

        int i = 0;
        String[] arrSelectedAnswer = null;
        List<OptionAnswerBean> optSelectedBean = null;
        try {
            arrSelectedAnswer= Tool.split(bean.getAnswer(), Global.DELIMETER_DATA);
            optSelectedBean=bean.getSelectedOptionAnswers();
        } catch (Exception e) {
            FireCrash.log(e);
            arrSelectedAnswer = new String[0];
            optSelectedBean = new ArrayList<>();
        }
        if (options != null && !options.isEmpty()) {
            for (OptionAnswerBean optBean : options) {
                CheckBox chk = new CheckBox(mActivity);
                chk.setText(optBean.getValue());
                chk.setChecked(optBean.isSelected());
                mMultipleLayout.addView(chk);
                listCheckBox.add(chk);
                if (bean.isReadOnly()) {
                    chk.setClickable(false);
                    chk.setEnabled(false);
                } else {
                    chk.setClickable(true);
                    chk.setEnabled(true);
                }
                String optBeanId = optBean.getUuid_lookup();
                if (withDescription) {
                    mDescription.setEnabled(false);
                    EditText desc = new EditText(mActivity);
                    if (null != bean.getMax_length() ) {
                        InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                                bean.getMax_length() == 0 ? Global.DEFAULT_MAX_LENGTH : bean.getMax_length())};
                        desc.setFilters(inputFilters);
                    }
                    qloop:
                    for (OptionAnswerBean nOptBean : optSelectedBean) {
                        String nOptBeanId = nOptBean.getUuid_lookup();
                        if (optBeanId.equals(nOptBeanId) && nOptBean.isSelected()) {
                            if (i < arrSelectedAnswer.length)
                                desc.setText(arrSelectedAnswer[i]);
                            i++;
                            break qloop;
                        }
                    }
                    desc.addTextChangedListener(this);
                    mMultipleLayout.addView(desc, defLayout);
                    listDescription.add(desc);
                }
                chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        saveSelectedOptionToBean();
                    }
                });
            }

            if (withOneDescription) {
                enableDescription(true);
                try {
                    mDescription.setText(bean.getAnswer());
                } catch (Exception e) {
                    FireCrash.log(e);
                    mDescription.setText("");
                }
                mDescription.addTextChangedListener(this);
            } else {
                enableDescription(false);
            }
            mMultipleEmpty.setVisibility(View.GONE);
        } else {
            mMultipleEmpty.setVisibility(View.VISIBLE);
        }

        selectSavedOptionsFromBeans();
    }

    private void selectSavedOptionsFromBeans() {
        List<OptionAnswerBean> beans = new ArrayList<>();
        for (int i = 0; i < bean.getSelectedOptionAnswers().size(); i++) {
            beans.add(bean.getSelectedOptionAnswers().get(i));
        }

        try {
            for (OptionAnswerBean optAnsBean : beans) {
                String lovCode = optAnsBean.getCode();
                String description = null;
                if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                        Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type())) {
                    description = bean.getAnswer();
                }
                selectOption(lovCode, description);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    private void selectOption(String lovCode, String description) {
        int indexOfOption = -1;
        int i = 0;
        for (OptionAnswerBean optAnsBean : options) {
            if (lovCode.equals(optAnsBean.getCode())) {
                indexOfOption = i;
                break;
            }
            i++;
        }
        if (indexOfOption > -1) {
            if (listCheckBox != null && listCheckBox.size() > i) {
                listCheckBox.get(i).setChecked(true);
            }
            if (description != null) {
                if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())
                        || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type())) {
                    enableDescription(true);
                    mDescription.setText(description);
                } else {
                    enableDescription(false);
                }
            }
        }

        //added from danu
        saveSelectedOptionToBean();
    }

    public void enableDescription(boolean enable) {
        if (enable) {
            if (null != bean.getMax_length() ) {
                if (bean.getAnswer_type().equals(Global.AT_MULTIPLE_W_DESCRIPTION)) {
                    InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                            bean.getMax_length() == 0 ? Global.DEFAULT_MAX_LENGTH :
                                    (bean.getMax_length() * options.size()) + bean.getMax_length())};
                    mDescription.setFilters(inputFilters);
                } else {
                    InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                            bean.getMax_length() == 0 ? Global.DEFAULT_MAX_LENGTH : bean.getMax_length())};
                    mDescription.setFilters(inputFilters);
                }
            }
            mDescription.setVisibility(View.VISIBLE);
        } else {
            mDescription.setVisibility(View.GONE);
        }
    }

    public void saveSelectedOptionToBean() {
        selectedOptionAnswers.clear();
        StringBuilder sb = new StringBuilder();
        boolean withDescription = false;
        boolean withOneDescription = false;
        if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())) {
            withDescription = true;
        }

        if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type())) {
            withOneDescription = true;
        }
        for (int i = 0; i < listCheckBox.size(); i++) {
            CheckBox chk = listCheckBox.get(i);
            if (chk.isChecked()) {
                OptionAnswerBean optAnsBean = options.get(i);        //assume checkbox is the same order as options

                if (sb.length() > 0) {
                    sb.append(Global.DELIMETER_DATA);
                }

                if (withDescription) {
                    EditText txtDescription = listDescription.get(i);
                    String description = txtDescription.getText().toString();
                    if (description.isEmpty()) {
                        sb.append(" ");
                    }
                    sb.append(description);
                }

                optAnsBean.setSelected(true);

                selectedOptionAnswers.add(optAnsBean);

            } else {
                options.get(i).setSelected(false);
            }
        }
        bean.setAnswer(sb.toString());

        if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())) {
            //added by danu
            enableDescription(true);
            mDescription.setText(sb.toString());
        }

        if (withOneDescription) {
            String description = mDescription.getText().toString().trim();
            bean.setAnswer(description);
        }

        bean.setLovCode(sb.toString());
        bean.setSelectedOptionAnswers(selectedOptionAnswers);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //EMPTY
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //EMPTY
    }

    @Override
    public void afterTextChanged(Editable s) {
        saveSelectedOptionToBean();
    }
}
