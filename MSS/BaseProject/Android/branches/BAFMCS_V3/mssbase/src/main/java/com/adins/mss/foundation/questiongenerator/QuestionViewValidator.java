package com.adins.mss.foundation.questiongenerator;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.form.MultiOptionQuestionViewAbstract;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QuestionViewValidator {
    public static final String HOLIDAY_IS_NOT_ALLOWED = "1";
    private String msgRequired;
    private Context context;

    public QuestionViewValidator(String msgRequired, Context context) {
        this.msgRequired = msgRequired;
        this.context = context;
    }

    private static boolean regexIsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public List<String> validateTextQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        EditText text = (EditText) view.getChildAt(1);
        String answer = text.getText().toString().trim();
        if (Global.AT_CURRENCY.equals(bean.getAnswer_type())) {
            if (answer != null && answer.length() > 0) {
                String tempAnswer = Tool.deleteAll(answer, ",");
                String[] intAnswer = Tool.split(tempAnswer, ".");
                if (intAnswer.length > 1) {
                    if (intAnswer[1].equals("00"))
                        answer = intAnswer[0];
                    else {
                        answer = tempAnswer;
                    }
                } else {
                    answer = tempAnswer;
                }
            }
        }
        bean.setAnswer(answer);
        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if ("".equals(answer)) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";
        if (!regex.equals("") && !bean.getAnswer().trim().equals("")) {
            if (Global.IS_DEV) System.out.println("!regex.equals" + regex);
            if (regexIsMatch(bean.getAnswer(), bean.getRegex())) {

            } else {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
        }


        return errMessage;
    }

    //GIgin ~ validasi suggestion question
    public List<String> validateTextWithSuggestionQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        AutoCompleteTextView text = (AutoCompleteTextView) view.getChildAt(1);
        String tempAnswer = bean.getAnswer() != null ? bean.getAnswer() : "";
        String answer = text.getText().toString().trim();

        if (!tempAnswer.equals(answer)) {
            Lookup lookup = LookupDataAccess.getOneByCodeAndlovGroup(context, bean.getLov_group(), answer);
            if (lookup == null) {
                errMessage.add(bean.getQuestion_label() + ": " + answer + " " + context.getString(R.string.not_allowed));
            }
        } else if (!Tool.isInternetconnected(context)) {
            Lookup lookup = LookupDataAccess.getOneByCodeAndlovGroup(context, bean.getLov_group(), answer);
            if (lookup == null) {
                errMessage.add(bean.getQuestion_label() + ": " + answer + " " + context.getString(R.string.not_available));
            }
        }
        bean.setAnswer(answer);
        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if ("".equals(answer)) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";
        if (!regex.equals("") && !bean.getAnswer().trim().equals("")) {
            if (Global.IS_DEV) System.out.println("!regex.equals" + regex);
            if (regexIsMatch(bean.getAnswer(), bean.getRegex())) {

            } else {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
        }

        return errMessage;
    }

    public List<String> validateLocationQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {

        } else {
            LinearLayout row = (LinearLayout) view.getChildAt(2);
            TextView text = (TextView) row.getChildAt(0);
            String answer = text.getText().toString().trim();

            bean.setAnswer(answer);

            if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
                if ("".equals(answer)) {
                    errMessage.add(bean.getQuestion_label() + " " + msgRequired);
                }
            }
        }
        return errMessage;
    }

    public List<String> validateDateTimeQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        LinearLayout row = (LinearLayout) view.getChildAt(1);
        TextView text = (TextView) row.getChildAt(0);
        String answer = text.getText().toString();
        Date date2 = null;
        boolean isSet = true;
        String answerType = bean.getAnswer_type();
        try {

            String format = null;
            if (Global.AT_DATE.equals(answerType)) {
                format = Global.DATE_STR_FORMAT;
                date2 = Formatter.parseDate(answer, format);
                String finalAnswer = Formatter.formatDate(date2, Global.DATE_STR_FORMAT_GSON);
                bean.setAnswer(finalAnswer);
            } else if (Global.AT_TIME.equals(answerType)) {
                format = Global.TIME_STR_FORMAT;
                date2 = Formatter.parseDate(answer, format);
                String finalAnswer = Formatter.formatDate(date2, Global.TIME_STR_FORMAT2);
                bean.setAnswer(finalAnswer);
            } else if (Global.AT_DATE_TIME.equals(answerType)) {
                format = Global.DATE_TIME_STR_FORMAT;
                date2 = Formatter.parseDate(answer, format);
                String finalAnswer = Formatter.formatDate(date2, Global.DATE_STR_FORMAT_GSON);
                bean.setAnswer(finalAnswer);
            }
        } catch (Exception pe) {
            isSet = false;
        }

        //bong 19 mei 15 - validasi holiday
        if (bean.getIs_holiday_allowed() != null && !bean.getIs_holiday_allowed().equals(HOLIDAY_IS_NOT_ALLOWED)) { // if it is not allowed on holiday
            Date date3 = null;
            if (Global.AT_DATE_TIME.equals(answerType)) {
                String format2 = Global.DATE_STR_FORMAT;
                try {
                    date3 = Formatter.parseDate(bean.getAnswer(), format2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else date3 = date2;

            if (date3 != null) {
                Holiday hday = HolidayDataAccess.getOneByDate(context, date3);
                if (hday != null) {
                    errMessage.add(bean.getQuestion_label() + " "
                            + "can not set on " + hday.getH_desc());
                }
            }
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (!isSet) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        int txtMaxLength = 0;
//		validasi untuk question yg punya maxlength > 0
        try {
            if (bean.getMax_length() > 0) {

                Calendar now = Calendar.getInstance(TimeZone
                        .getDefault());
                Calendar date = Calendar.getInstance(TimeZone
                        .getDefault());
                date.setTime(date2);
                long diff = (date.getTime().getTime() - now.getTime()
                        .getTime());
                long diffDay = diff / 86400000;

                if (diffDay < 0) {
                    txtMaxLength = (int) diffDay;
                } else if (diffDay > bean.getMax_length()) {
                    txtMaxLength = (int) diffDay;
                } else {
                    txtMaxLength = bean.getMax_length();
                }
            } else {
                Date today = Tool.getSystemDate();
                Calendar now = Calendar.getInstance(TimeZone
                        .getDefault());
                now.setTime(today);
                Calendar date = Calendar.getInstance(TimeZone
                        .getDefault());
                date.setTime(date2);
                long diff = (date.getTime().getTime() - now.getTime()
                        .getTime());
                long diffDay = diff / 86400000;

                if (diffDay > 0) {
                    txtMaxLength = (int) diffDay;
                } else if (diffDay < bean.getMax_length()) {
                    txtMaxLength = (int) diffDay;
                } else {
                    txtMaxLength = bean.getMax_length();
                }
            }
            if (bean.getMax_length() == -999) {
                if (txtMaxLength > 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_before_today));
                }
            } else if (bean.getMax_length() < 0) {
                if (txtMaxLength > 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_before_today));
                } else if (txtMaxLength < bean.getMax_length()) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.more_than)
                            + " " + txtMaxLength + " " + context.getString(R.string.day) + "!");
                }
            } else if (bean.getMax_length() > 0) {
                if (txtMaxLength < 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_after_today));
                } else if (txtMaxLength > bean.getMax_length()) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.less_than)
                            + " " + bean.getMax_length() + " " + context.getString(R.string.day) + "!");
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return errMessage;
    }

    //Glen 14 Oct 2014, combine all multiple option question validation and save answer logic
    //validateMultipleOptionQuestion need no bean as parameter, MultipleQuestionView already has it
    public List<String> validateMultipleOptionQuestion(int idx, MultiOptionQuestionViewAbstract view) {
        List<String> errMessage = new ArrayList<>();

        //Let the subclass handle the saving method
        view.saveSelectedOptionToBean();

        QuestionBean bean = view.getQuestionBean();

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {

            if (bean.getSelectedOptionAnswers() == null || bean.getSelectedOptionAnswers().isEmpty()) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }
        return errMessage;
    }

    public List<String> validateDropdownQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        Spinner spinner = (Spinner) view.getChildAt(1);

        int selected = spinner.getSelectedItemPosition();
        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        int count = 0;
        for (OptionAnswerBean optBean : listOptions) {
            if (count == selected) {
                optBean.setSelected(true);
            } else {
                optBean.setSelected(false);
            }

            count++;
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateDropdownDescQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        Spinner spinner = (Spinner) view.getChildAt(1);

        int selected = spinner.getSelectedItemPosition();
        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        int count = 0;
        for (OptionAnswerBean optBean : listOptions) {
            if (count == selected) {
                optBean.setSelected(true);
                EditText txt = (EditText) view.getChildAt(2);
                String desc = txt.getText().toString();
                optBean.setValue(desc);
            } else {
                optBean.setSelected(false);
                optBean.setValue(null);
            }

            count++;
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateMultipleQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        int i = 0;
        for (OptionAnswerBean optBean : listOptions) {
            CheckBox chk = (CheckBox) view.getChildAt(i + 1);
            if (chk.isChecked()) {
                optBean.setSelected(true);
            } else {
                optBean.setSelected(false);
            }
            i++;
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateMultipleDescQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        int i = 0;
        for (OptionAnswerBean optBean : listOptions) {
            CheckBox chk = (CheckBox) view.getChildAt((i * 2) + 1);
            if (chk.isChecked()) {
                optBean.setSelected(true);
                EditText txt = (EditText) view.getChildAt((i * 2) + 2);
                optBean.setValue(txt.getText().toString());
            } else {
                optBean.setSelected(false);
                optBean.setValue(null);
            }
            i++;
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateRadioQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        if (listOptions != null && !listOptions.isEmpty()) {
            RadioGroup rdGroup = (RadioGroup) view.getChildAt(1);

            int i = 0;
            for (OptionAnswerBean optBean : listOptions) {
                RadioButton rb = (RadioButton) rdGroup.getChildAt(i);
                if (rb.isChecked()) {
                    optBean.setSelected(true);
                } else {
                    optBean.setSelected(false);
                }
                i++;
            }
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateRadioDescQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        List<OptionAnswerBean> listOptions = bean.getOptionAnswers();

        if (listOptions != null && !listOptions.isEmpty()) {
            RadioGroup rdGroup = (RadioGroup) view.getChildAt(1);

            int i = 0;
            for (OptionAnswerBean optBean : listOptions) {
                RadioButton rb = (RadioButton) rdGroup.getChildAt(i);
                if (rb.isChecked()) {
                    optBean.setSelected(true);
                } else {
                    optBean.setSelected(false);
                }
                i++;
            }

            int selected = Tool.getSelectedIndex(listOptions);
            if (selected != -1) {
                EditText desc = (EditText) view.getChildAt(2);
                OptionAnswerBean optBean = listOptions.get(selected);
                optBean.setValue(desc.getText().toString());
            }
        }

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (Tool.getSelectedIndex(listOptions) == -1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateImageQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();
        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
        } else {
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
                byte[] img = bean.getImgAnswer();
                if (img == null || img.length < 1) {
                    errMessage.add(bean.getQuestion_label() + " " + msgRequired);
                }
                String answerType = bean.getAnswer_type();
                if (answerType.equals(Global.AT_IMAGE_W_LOCATION) ||
                        answerType.equals(Global.AT_IMAGE_W_GPS_ONLY)) {
                    LocationInfo locationInfo = bean.getLocationInfo();
                    if (locationInfo != null) {
                        if (locationInfo.getLatitude().equals("0.0") || locationInfo.getLongitude().equals("0.0")) {
                            if (answerType.equals((Global.AT_IMAGE_W_GPS_ONLY))) {
                                errMessage.add(context.getString(R.string.gps_gd_error));
                            } else {
                                if (locationInfo.getMcc().equals("0") || locationInfo.getMnc().equals("0")) {
                                    errMessage.add(context.getString(R.string.lbs_gd_error));
                                }
                            }
                        }
                    } else {
                        errMessage.add(context.getString(R.string.gps_error));
                    }
                }
            }
        }

        return errMessage;
    }


    public List<String> validateDrawingDescQuestion(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = new ArrayList<>();

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            byte[] img = bean.getImgAnswer();
            if (img == null || img.length < 1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateGeneratedQuestionView(QuestionBean bean, int idx, LinearLayout view) {
        List<String> errMessage = null;

        if (bean.getIs_visible().equals(Global.FALSE_STRING))
            return null;

        String answerType = bean.getAnswer_type();

        if (Global.AT_TEXT.equals(answerType)) {
            errMessage = this.validateTextQuestion(bean, idx, view);
        } else if (Global.AT_TEXT_MULTILINE.equals(answerType)) {
            errMessage = this.validateTextQuestion(bean, idx, view);
        } else if (Global.AT_TEXT_WITH_SUGGESTION.equals(answerType)) {
            errMessage = this.validateTextWithSuggestionQuestion(bean, idx, view);
        } else if (Global.AT_CURRENCY.equals(answerType)) {
            errMessage = this.validateTextQuestion(bean, idx, view);
        } else if (Global.AT_GPS.equals(answerType)) {
            errMessage = this.validateLocationQuestion(bean, idx, view);
        } else if (Global.AT_GPS_N_LBS.equals(answerType)) {
            errMessage = this.validateLocationQuestion(bean, idx, view);
        } else if (Global.AT_LOCATION.equals(answerType)) {
            errMessage = this.validateLocationQuestion(bean, idx, view);
        } else if (Tool.isOptions(answerType)) {
            errMessage = this.validateMultipleOptionQuestion(idx, (MultiOptionQuestionViewAbstract) view);
        } else if (Global.AT_NUMERIC.equals(answerType)) {
            errMessage = this.validateTextQuestion(bean, idx, view);
        } else if (Global.AT_DECIMAL.equals(answerType)) {
            errMessage = this.validateTextQuestion(bean, idx, view);
        } else if (Global.AT_DATE.equals(answerType)) {
            errMessage = this.validateDateTimeQuestion(bean, idx, view);
        } else if (Global.AT_TIME.equals(answerType)) {
            errMessage = this.validateDateTimeQuestion(bean, idx, view);
        } else if (Global.AT_DATE_TIME.equals(answerType)) {
            errMessage = this.validateDateTimeQuestion(bean, idx, view);
        } else if (Tool.isImage(answerType)) {
            errMessage = this.validateImageQuestion(bean, idx, view);
        } else if (Global.AT_DRAWING.equals(answerType)) {
            errMessage = this.validateDrawingDescQuestion(bean, idx, view);
        }

        return errMessage;
    }
}
