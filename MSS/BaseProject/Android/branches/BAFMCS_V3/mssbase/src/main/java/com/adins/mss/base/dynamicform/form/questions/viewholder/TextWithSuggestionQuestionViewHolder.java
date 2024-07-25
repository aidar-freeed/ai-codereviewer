package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.questiongenerator.form.QuestionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigin.ginanjar on 31/08/2016.
 */
public class TextWithSuggestionQuestionViewHolder extends RecyclerView.ViewHolder {
    public QuestionView mView;
    public TextView mQuestionLabel;
    public AutoCompleteTextView mQuestionAnswer;
    public QuestionBean bean;
    private Context mContext;
    private LookupArrayAdapter arrayAdapter;
    private List<OptionAnswerBean> options;

    public TextWithSuggestionQuestionViewHolder(View itemView) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionTwsLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTwsLabel);
        mQuestionAnswer = (AutoCompleteTextView) itemView.findViewById(R.id.questionTwsAnswer);
        mQuestionAnswer.requestFocus();
    }

    public TextWithSuggestionQuestionViewHolder(View itemView, Context context) {
        super(itemView);
        mView = (QuestionView) itemView.findViewById(R.id.questionTwsLayout);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTwsLabel);
        mQuestionAnswer = (AutoCompleteTextView) itemView.findViewById(R.id.questionTwsAnswer);
        mQuestionAnswer.requestFocus();
        mContext = context;
    }

    public void bind(QuestionBean item, int number) {
        bean = item;
        if (bean.getIs_mandatory().equals(Global.TRUE_STRING))
            mQuestionAnswer.setHint(mContext.getString(R.string.requiredField));
        else
            mQuestionAnswer.setHint("");
        String questionLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(questionLabel);
        if (null != bean.getAnswer() && !"".equalsIgnoreCase(bean.getAnswer())) {
            mQuestionAnswer.setText(bean.getAnswer());
        } else {
            mQuestionAnswer.setText("");
        }
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(bean.getMax_length())};
        mQuestionAnswer.setFilters(inputFilters);

        arrayAdapter = new LookupArrayAdapter(mContext, bean);
        mQuestionAnswer.setAdapter(arrayAdapter);
        mQuestionAnswer.setThreshold(1);
        mQuestionAnswer.setSingleLine();
        mQuestionAnswer.setDropDownBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.dropdown_background));

        mQuestionAnswer.addTextChangedListener(new TextWatcher() {
            String TempText = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //EMPTY
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // save answer to bean
                TempText = mQuestionAnswer.getText().toString().trim();
                if(!TempText.isEmpty() && null==bean.getSelectedOptionAnswers()){
                    TempText = "";
                }
                if (bean.isReadOnly()) {
                    mQuestionAnswer.setKeyListener(null);
                    mQuestionAnswer.setCursorVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                bean.setAnswer(s.toString().trim());
                if (bean.isRelevanted()) {
                    String newText = mQuestionAnswer.getText().toString().trim();
                    mView.setChanged(false);
                    if (!TempText.equals(newText)) {
                        mView.setChanged(true);
                    }
                }
            }
        });

        mQuestionAnswer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!bean.isReadOnly()) {
                    List<OptionAnswerBean> tempSelectedItems = bean.getSelectedOptionAnswers();
                    OptionAnswerBean newSelectedItem = arrayAdapter.getLookupItem(position);
                    if (tempSelectedItems != null && !tempSelectedItems.isEmpty()) {
                        if (tempSelectedItems.get(0).getUuid_lookup() != null
                                && !tempSelectedItems.get(0).getUuid_lookup().equals(newSelectedItem.getUuid_lookup())) {
                            mView.setChanged(true);
                            bean.setChange(true);
                        } else {
                            bean.setChange(false);
                        }
                    } else {
                        bean.setChange(false);
                    }
                    saveSelectedOptionToBean(newSelectedItem);
                } else {
                    bean.setChange(false);
                }
            }
        });
        if (bean.isReadOnly()) {
            mQuestionAnswer.setKeyListener(null);
            mQuestionAnswer.setCursorVisible(false);
            mQuestionAnswer.setEnabled(false);
        } else {
            mQuestionAnswer.setCursorVisible(true);
            mQuestionAnswer.setEnabled(true);
        }
    }

    private void saveSelectedOptionToBean(OptionAnswerBean option) {
        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();
        if(option == null){
            bean.setLovCode(null);
            bean.setLookupId(null);
            bean.setSelectedOptionAnswers(selectedOptionAnswers);
            return;
        }

        option.setSelected(true);
        selectedOptionAnswers.add(option);
        bean.setLovCode(option.getCode());
        bean.setLookupId(option.getUuid_lookup());
        bean.setSelectedOptionAnswers(selectedOptionAnswers);
        bean.setAnswer(option.toString());
    }

    private List<OptionAnswerBean> GetLookupFromDBTextWithSuggestion(Context context, QuestionBean bean, List<String> filters, String dynamicFilter) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<>();
        if (!filters.isEmpty()) {
            if (filters.size() == 1) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilterTextWithSuggestion(context, bean.getLov_group(), filters.get(0), dynamicFilter);
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 2) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilterTextWithSuggestion(context, bean.getLov_group(), filters.get(0), filters.get(1), dynamicFilter);
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 3) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilterTextWithSuggestion(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), dynamicFilter);
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 4) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilterTextWithSuggestion(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), dynamicFilter);
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 5) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilterTextWithSuggestion(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4), dynamicFilter);
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            }

        } else {
            if (bean.getChoice_filter() != null && bean.getChoice_filter().length() > 0) {
                List<Lookup> lookups = new ArrayList<>();
                optionAnswers = OptionAnswerBean.getOptionList(lookups);
            } else {
                String lovGroup = bean.getLov_group();
                if (lovGroup != null) {
                    List<Lookup> lookups = LookupDataAccess.getAllByLovGroupTextWithSuggestion(context, lovGroup, dynamicFilter);
                    if (lookups != null)
                        optionAnswers = OptionAnswerBean.getOptionList(lookups);
                }
            }
        }
        return optionAnswers;
    }

    private List<OptionAnswerBean> GetLookupFromDB(Context context, QuestionBean bean, List<String> filters) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<>();
        if (!filters.isEmpty()) {
            if (filters.size() == 1) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(context, bean.getLov_group(), filters.get(0));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 2) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(context, bean.getLov_group(), filters.get(0), filters.get(1));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 3) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 4) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 5) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(context, bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            }
        } else {
            if (bean.getChoice_filter() != null && bean.getChoice_filter().length() > 0) {
                List<Lookup> lookups = new ArrayList<>();
                optionAnswers = OptionAnswerBean.getOptionList(lookups);
            } else {
                String lovGroup = bean.getLov_group();
                if (lovGroup != null) {
                    List<Lookup> lookups = LookupDataAccess.getAllByLovGroup(context, lovGroup);
                    if (lookups != null)
                        optionAnswers = OptionAnswerBean.getOptionList(lookups);
                }
            }
        }
        return optionAnswers;
    }

    protected List<OptionAnswerBean> getOptionsForQuestion(QuestionBean bean, String dynamicFilter) {
        List<String> filters = new ArrayList<>();
        int constraintAmount = 0;
        if (bean.getChoice_filter() != null) {
            String[] tempfilters = Tool.split(bean.getChoice_filter(), Global.DELIMETER_DATA3);

            for (String newFilter : tempfilters) {
                int idxOfOpenBrace = newFilter.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = newFilter.indexOf('}');
                    String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace).toUpperCase();
                    if (tempIdentifier.contains("%")) {
                        filters.add(tempIdentifier);
                    } else {
                        int idxOfOpenAbs = tempIdentifier.indexOf("$");
                        if (idxOfOpenAbs != -1) {
                            String finalIdentifier = tempIdentifier.substring(idxOfOpenAbs + 1);
                            if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                                String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                                int idxOfOpenAt = loginId.indexOf('@');
                                if (idxOfOpenAt != -1) {
                                    loginId = loginId.substring(0, idxOfOpenAt);
                                }
                                filters.add(loginId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                                String branchId = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                                filters.add(branchId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                                String branchName = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                                filters.add(branchName);
                            } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                filters.add(uuidUser);
                            } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                                String job = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                                filters.add(job);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                                String dealerName = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                                filters.add(dealerName);
                            } else if (finalIdentifier.equals(Global.IDF_UUID_BRANCH)) {
                                String uuidBranch = GlobalData.getSharedGlobalData().getUser().getUuid_branch();
                                filters.add(uuidBranch);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_ID)) {
                                String dealerId = GlobalData.getSharedGlobalData().getUser().getUuid_dealer();
                                filters.add(dealerId);
                            }
                            constraintAmount++;
                        } else {
                            QuestionBean bean2 = Constant.getListOfQuestion().get(tempIdentifier);
                            if (bean2 != null) {
                                if (Global.AT_TEXT_WITH_SUGGESTION.equals(bean2.getAnswer_type())) {
                                    filters.add(bean2.getLovCode());
                                } else {
                                    for (OptionAnswerBean answerBean : bean2.getSelectedOptionAnswers()) {
                                        filters.add(answerBean.getCode());
                                    }
                                }
                                bean2.setRelevanted(true);
                                constraintAmount++;
                            }
                        }
                    }
                }
            }
        }
        List<OptionAnswerBean> optionAnswers;
        if (dynamicFilter != null) {
            optionAnswers = GetLookupFromDBTextWithSuggestion(mContext, bean, filters, dynamicFilter);
        } else {
            optionAnswers = GetLookupFromDB(mContext, bean, filters);
        }

        return optionAnswers;
    }

    class LookupArrayAdapter extends BaseAdapter implements Filterable {
        private Context mContext;
        private QuestionBean bean;
        private List<OptionAnswerBean> resultList = new ArrayList<>();

        public LookupArrayAdapter(Context context, QuestionBean bean) {
            this.mContext = context;
            this.bean = bean;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            return resultList.get(position).toString();
        }

        public OptionAnswerBean getLookupItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AutoTextViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.autotext_list, parent, false);
            }
            holder = new AutoTextViewHolder(convertView);
            holder.bind(getLookupItem(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<OptionAnswerBean> beanList = getOptionsForQuestion(bean, constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = beanList;
                        filterResults.count = beanList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<OptionAnswerBean>) results.values;
                        options = resultList;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public class AutoTextViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textView;
        public OptionAnswerBean bean;

        public AutoTextViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            textView = (TextView) itemView.findViewById(R.id.textauto);
        }

        public void bind(OptionAnswerBean optBean) {
            bean = optBean;
            textView.setText(bean.getValue());
        }
    }
}
