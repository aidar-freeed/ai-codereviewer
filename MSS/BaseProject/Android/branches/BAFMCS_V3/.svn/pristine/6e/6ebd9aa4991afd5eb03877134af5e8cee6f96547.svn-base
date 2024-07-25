package com.adins.mss.base.dynamicform.form.questions.viewholder;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by gigin.ginanjar on 01/09/2016.
 */
public class RadioQuestionViewHolder extends RecyclerView.ViewHolder implements TextWatcher {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mRadioEmpty;
    public RadioGroup mRadioLayout;
    public EditText mDescription;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    protected List<OptionAnswerBean> options;
    protected LinearLayout.LayoutParams defLayout = new LinearLayout.LayoutParams(
            MATCH_PARENT, WRAP_CONTENT);

    /**      
     *  @deprecated
     */
    @Deprecated
    public RadioQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionRadioLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionRadioLabel);
        mRadioEmpty = (TextView) itemView.findViewById(R.id.questionRadioEmpty);
        mRadioLayout = (RadioGroup) itemView.findViewById(R.id.radioQuestionList);
        mDescription = (EditText) itemView.findViewById(R.id.questionRadioDescription);
    }

    public RadioQuestionViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionRadioLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionRadioLabel);
        mRadioEmpty = (TextView) itemView.findViewById(R.id.questionRadioEmpty);
        mRadioLayout = (RadioGroup) itemView.findViewById(R.id.radioQuestionList);
        mDescription = (EditText) itemView.findViewById(R.id.questionRadioDescription);
        mActivity = activity;
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        options = bean.getOptionAnswers();
        String qLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);
        if (bean.isReadOnly()) {
            mRadioLayout.setClickable(false);
            mRadioLayout.setEnabled(false);
        } else {
            mRadioLayout.setClickable(true);
            mRadioLayout.setEnabled(true);
        }
        mRadioLayout.removeAllViews();
        mRadioLayout.clearCheck();
        int i = 0;
        if (options != null && !options.isEmpty()) {
            for (OptionAnswerBean optBean : options) {
                RadioButton rb = new RadioButton(mActivity);
                rb.setText(optBean.getValue());
                if (optBean.isSelected()) {
                    rb.setSelected(true);
                    rb.setChecked(true);
                } else {
                    rb.setSelected(false);
                    rb.setChecked(false);
                }
                rb.setId(i);
                rb.setEnabled(true);
                if (bean.isReadOnly()) {
                    rb.setEnabled(false);
                }
                mRadioLayout.addView(rb, defLayout);
                i++;
            }

            mRadioEmpty.setVisibility(View.GONE);
        } else {
            mRadioEmpty.setVisibility(View.VISIBLE);
        }
        mRadioLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group != null && group.getCheckedRadioButtonId() != -1) {
                    if (!bean.isReadOnly()) {
                        List<OptionAnswerBean> tempSelectedItems = bean.getSelectedOptionAnswers();
                        OptionAnswerBean newSelectedItem = options.get(checkedId);
                        if (tempSelectedItems != null && !tempSelectedItems.isEmpty()) {
                            if(!bean.isChange()) {
                                if (tempSelectedItems.get(0).getUuid_lookup() != null
                                        && !tempSelectedItems.get(0).getUuid_lookup().equals(newSelectedItem.getUuid_lookup())) {
                                    mView.setChanged(true);
                                    bean.setChange(true);
                                } else {
                                    bean.setChange(false);
                                }
                            }
                        } else {
                            bean.setChange(false);
                        }
                    } else {
                        bean.setChange(false);
                    }
                    saveSelectedOptionToBean();
                }
            }
        });
        if (Global.AT_RADIO_W_DESCRIPTION.equals(bean.getAnswer_type()))
            enableDescription(true);
        else
            enableDescription(false);
        mDescription.addTextChangedListener(this);
        selectSavedOptionsFromBeans(bean.getSelectedOptionAnswers());
    }

    private void selectSavedOptionsFromBeans(List<OptionAnswerBean> beans) {
        if (beans == null) return;

        for (OptionAnswerBean optAnsBean : beans) {
            String lovCode = optAnsBean.getCode();
            String description = null;
            if (Global.AT_RADIO_W_DESCRIPTION.equals(bean.getAnswer_type())) {
                description = bean.getAnswer();
            }
            selectOption(lovCode, description);
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
            mRadioLayout.check(i);

            if (description != null) {
                enableDescription(true);
                mDescription.setText(description);
            }
        } else {
            mRadioLayout.clearCheck();
        }
    }

    public void enableDescription(boolean enable) {
        if (enable) {
            if (null != bean.getMax_length() ) {
                InputFilter[] inputFilters = {new InputFilter.LengthFilter(
                        bean.getMax_length() == 0 ? Global.DEFAULT_MAX_LENGTH : bean.getMax_length())};
                mDescription.setFilters(inputFilters);
            }
            mDescription.setVisibility(View.VISIBLE);
        } else {
            mDescription.setVisibility(View.GONE);
        }
    }

    public void saveSelectedOptionToBean() {
        int selectedRadioId = mRadioLayout.getCheckedRadioButtonId();
        if (selectedRadioId == -1) {
            setSelectedOptionAnswer(null);
        } else {
            OptionAnswerBean selectedItem = options.get(selectedRadioId);
            //check description if any
            if (mDescription.getVisibility() == View.VISIBLE && !mDescription.getText().toString().trim().isEmpty()) {
                String description = mDescription.getText().toString();

                bean.setAnswer(description);
            } else {
                bean.setAnswer(null);
            }

            if (selectedItem != null) setSelectedOptionAnswer(selectedItem);
        }
    }

    private void setSelectedOptionAnswer(OptionAnswerBean option) {
        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();
        for (OptionAnswerBean mBean : options) {
            mBean.setSelected(false);
        }
        if (option != null) {
            option.setSelected(true);
            selectedOptionAnswers.add(option);
            bean.setLovCode(option.getCode());
            bean.setLookupId(option.getUuid_lookup());
        } else {
            bean.setLovCode(null);
            bean.setLookupId(null);
        }
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
        if (mDescription.getVisibility() == View.VISIBLE)
            saveSelectedOptionToBean();
    }
}
