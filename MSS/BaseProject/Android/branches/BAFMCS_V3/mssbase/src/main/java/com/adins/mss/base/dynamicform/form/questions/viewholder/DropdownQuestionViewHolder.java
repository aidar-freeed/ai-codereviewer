package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
public class DropdownQuestionViewHolder extends RecyclerView.ViewHolder implements TextWatcher, AdapterView.OnItemSelectedListener {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public TextView mLookupEmpty;
    public AppCompatSpinner mSpinnLookup;
    public EditText mDescription;
    public QuestionBean bean;
    public FragmentActivity mActivity;
    public ImageView img;
    protected List<OptionAnswerBean> options;
    private LookupAdapter adapter;

    @Deprecated
    public DropdownQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDropdownLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDropdownLabel);
        mLookupEmpty = (TextView) itemView.findViewById(R.id.questionDropdownEmpty);
        mSpinnLookup = (AppCompatSpinner) itemView.findViewById(R.id.spinnerQuestionList);
        mDescription = (EditText) itemView.findViewById(R.id.questionDropdownDescription);
        img = (ImageView) itemView.findViewById(R.id.img1);
    }

    public DropdownQuestionViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionDropdownLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionDropdownLabel);
        mLookupEmpty = (TextView) itemView.findViewById(R.id.questionDropdownEmpty);
        mSpinnLookup = (AppCompatSpinner) itemView.findViewById(R.id.spinnerQuestionList);
        mDescription = (EditText) itemView.findViewById(R.id.questionDropdownDescription);
        img = (ImageView) itemView.findViewById(R.id.img1);
        mActivity = activity;
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        options = bean.getOptionAnswers();
        String answerType = bean.getAnswer_type();
        String qLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);
        if (bean.isReadOnly()) {
            mSpinnLookup.setClickable(false);
            mSpinnLookup.setEnabled(false);
        } else {
            mSpinnLookup.setClickable(true);
            mSpinnLookup.setEnabled(true);
        }
        int nextOptionIndex = -1;
        try {
            if (bean.getSelectedOptionAnswers() == null || bean.getSelectedOptionAnswers().size() == 0) {
                if (bean.isReadOnly()) {
                    bean.setOptionAnswers(null);
                    options = new ArrayList<>();
                    img.setVisibility(View.GONE);
                } else {
                    img.setVisibility(View.VISIBLE);
                }
            }
            if (bean.isReadOnly())
                img.setVisibility(View.GONE);
            else
                img.setVisibility(View.VISIBLE);

            String optionSelectedIdStr = QuestionBean.getAnswer(bean);
            OptionAnswerBean optionAnswerBean = new OptionAnswerBean();
            optionAnswerBean.setCode(mActivity.getString(R.string.promptChooseOne));
            optionAnswerBean.setLov_group(bean.getLov_group());
            optionAnswerBean.setValue(mActivity.getString(R.string.promptChooseOne));
            if (optionSelectedIdStr == null)
                optionSelectedIdStr = bean.getLovCode();
            if (options != null && options.size() > 0) {
                for (int i = 0; i < options.size(); i++) {
                    OptionAnswerBean option = options.get(i);
                    if (options.get(i).getValue().equals(mActivity.getString(R.string.promptChooseOne))){
                        options.remove(i);
                        options = bean.getOptionAnswers();
                    }
                    if (option.getCode().equalsIgnoreCase(optionSelectedIdStr)) {        //this is the same option (based on id)
                        nextOptionIndex = i;
                        break;
                    }
                }
                options.add(0,optionAnswerBean);
                mLookupEmpty.setVisibility(View.GONE);
            } else {
                mLookupEmpty.setVisibility(View.VISIBLE);
                if (bean.isReadOnly()) {
                    mLookupEmpty.setText(mActivity.getString(R.string.no_answer_found));
                } else {
                    mLookupEmpty.setText(mActivity.getString(R.string.lookup_not_found));
                }
            }
        }
        catch(Exception e){
            FireCrash.log(e);
        }

        adapter = new LookupAdapter(mActivity, R.layout.spinner_style2, R.id.text_spin, options);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        mSpinnLookup.setAdapter(adapter);

        if (nextOptionIndex >= 0 && nextOptionIndex < options.size()) {
            mSpinnLookup.setSelection(nextOptionIndex);
        }

        mSpinnLookup.setOnItemSelectedListener(this);

        if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType)) {
            enableDescription(true);
            mDescription.addTextChangedListener(this);
        } else {
            enableDescription(false);
        }
        selectSavedOptionsFromBeans(bean.getSelectedOptionAnswers());
        mSpinnLookup.setFocusableInTouchMode(true);
    }

    private void setSelectedOptionAnswer(OptionAnswerBean option) {
        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<OptionAnswerBean>();
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

    private void selectSavedOptionsFromBeans(List<OptionAnswerBean> beans) {

        if (beans != null) {
            for (OptionAnswerBean optAnsBean : beans) {
                String lovCode = optAnsBean.getCode();
                String description = null;
                if (Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                        Global.AT_RADIO_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                        Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type())) {
                    description = bean.getAnswer();
                }
                if (lovCode==null){
                    selectOption("",description);
                }else {
                    selectOption(lovCode, description);
                }
            }
        }
    }

    public void saveSelectedOptionToBean() {
        OptionAnswerBean selected = (OptionAnswerBean) mSpinnLookup.getSelectedItem();
        if (mDescription.getVisibility() == View.VISIBLE && !mDescription.getText().toString().trim().isEmpty()) {
            bean.setAnswer(mDescription.getText().toString().trim());
        } else {
            bean.setAnswer("");
        }
        setSelectedOptionAnswer(selected);
    }

    private void selectOption(String lovCode, String desc) {
        int indexOfOption = -1;
        int i = 0;
        for (OptionAnswerBean optAnsBean : options) {
            if ("".equals(optAnsBean.getCode())){
                indexOfOption = -1;
                break;
            }else if (lovCode.equals(optAnsBean.getCode())) {
                indexOfOption = i;
                break;
            }
            i++;
        }
        if (indexOfOption > -1) {
            if (mSpinnLookup != null) {
                mSpinnLookup.setSelection(i);
            }

            if (desc != null) {
                enableDescription(true);
                mDescription.setText(bean.getAnswer());
            } else {
                enableDescription(false);
            }
        }else if (indexOfOption == -1){
            if (mSpinnLookup!=null){
                mSpinnLookup.setSelection(0);
            }
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!bean.isReadOnly())
            saveSelectedOptionToBean();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent != null && view.getId() != 0) {
            //do your code here to avoid callback twice
            if (!bean.isReadOnly()) {
                List<OptionAnswerBean> tempSelectedItems = bean.getSelectedOptionAnswers();
                OptionAnswerBean newSelectedItem = (OptionAnswerBean) mSpinnLookup.getSelectedItem();
                if (tempSelectedItems != null && tempSelectedItems.size() > 0) {
//                    if (!tempSelectedItems.get(0).getUuid_lookup().equals(newSelectedItem.getUuid_lookup())) {
//                        mView.setChanged(true);
//                        bean.setChange(true);
//                    } else {
//                        bean.setChange(false);
                    if(!bean.isChange()) {
                        if (tempSelectedItems.get(0).getUuid_lookup()==null||!tempSelectedItems.get(0).getUuid_lookup().equals(newSelectedItem.getUuid_lookup())) {
                            mView.setChanged(true);
                            bean.setChange(true);
                        } else {
                            bean.setChange(false);
                        }
                    }
                } else {
                    bean.setChange(false);
                }
                saveSelectedOptionToBean();
            } else {
                bean.setChange(false);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class LookupAdapter extends ArrayAdapter<OptionAnswerBean> {
        private Context context;
        private List<OptionAnswerBean> values;

        public LookupAdapter(Context context, int resource, int textViewResourceId, List<OptionAnswerBean> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.values = objects;
        }
        public  class ViewHolder {
            public TextView nameTextView;
        }

        public int getCount() {
            return values.size();
        }

        public OptionAnswerBean getItem(int position) {
            return values.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getViewInternal(position, convertView, parent);
//            LayoutInflater inflater = mActivity.getLayoutInflater();
//            View view = inflater.inflate(R.layout.spinner_style2, parent, false);
//            TextView label = (TextView) view.findViewById(R.id.text_spin);
//            label.setText(values.get(position).getValue());
//            return label;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getViewInternal(position, convertView, parent);
        }
        // fungsi untuk wrap text opsi dropdown pada LOV
        private View getViewInternal(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                view = inflater.inflate(R.layout.spinner_style2, parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) view.findViewById(R.id.text_spin);
                view.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.nameTextView.setText(values.get(position).getValue());
            return view;
        }
    }
}
