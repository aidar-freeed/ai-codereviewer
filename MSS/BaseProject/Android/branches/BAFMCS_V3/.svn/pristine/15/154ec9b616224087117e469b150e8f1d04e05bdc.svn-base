package com.adins.mss.base.dynamicform.form.questions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.resolver.DateResolver;
import com.adins.mss.base.dynamicform.form.resolver.DateTimeResolver;
import com.adins.mss.base.dynamicform.form.resolver.TimeResolver;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.operators.IfElseFunction;
import com.adins.mss.foundation.questiongenerator.NotEqualSymbol;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.OperatorSet;
import com.gadberry.utility.expression.symbol.AndSymbol;
import com.gadberry.utility.expression.symbol.OrSymbol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gigin.ginanjar on 30/08/2016.
 */
public class QuestionsValidator implements QuestionsValidatorInterface {
    private static String TAG = "QuestionValidator";
    public static final String HOLIDAY_IS_NOT_ALLOWED = "1";
    private String msgRequired;
    private String dropdownRequired;
    private Context context;

    public QuestionsValidator(String msgRequired, Context context) {
        this.msgRequired = msgRequired;
        this.context = context;
    }

    public static boolean regexIsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public List<String> validateGeneratedQuestionView(QuestionBean bean) {
        List<String> errMessage = null;
        String answerType = bean.getAnswer_type();
        if (bean.getIs_visible().equals(Global.FALSE_STRING))
            return Collections.emptyList();
        if (QuestionViewAdapter.IsDropdownQuestion(Integer.valueOf(answerType))){
            return validateDropdownQuestion(bean);
        }else if (QuestionViewAdapter.IsTextQuestion(Integer.valueOf(answerType))) {
            return validateTextQuestion(bean);
        } else if (QuestionViewAdapter.IsDateTimeQuestion(Integer.valueOf(answerType))) {
            return validateDateTimeQuestion(bean);
        } else if (QuestionViewAdapter.IsTextWithSuggestionQuestion(Integer.valueOf(answerType))) {
            return validateTextWithSuggestionQuestion(bean);
        } else if (QuestionViewAdapter.IsLocationQuestion(Integer.valueOf(answerType))) {
            return validateLocationQuestion(bean);
        } else if (Tool.isOptions(answerType)) {
            return validateMultipleQuestion(bean);
        } else if (QuestionViewAdapter.IsImageQuestion(Integer.valueOf(answerType))) {
            return validateImageQuestion(bean);
        } else if (QuestionViewAdapter.IsDrawingQuestion(Integer.valueOf(answerType))) {
            return validateDrawingQuestion(bean);
        } else if (QuestionViewAdapter.IsLookupQuestion(Integer.valueOf(answerType))) {
            return validateLookupQuestion(bean);
        } else if (QuestionViewAdapter.IsValidationQuestion(Integer.valueOf(answerType))) {
            return validatePhoneNumberQuestion(bean);
        } else if (QuestionViewAdapter.IsRvMobileQuestion(Integer.valueOf(answerType))) {
            return validateRvMobileQuestion(bean);
        } else if (QuestionViewAdapter.isTextOnlineQuestion(Integer.valueOf(answerType))) {
            return validateTextQuestion(bean);
        } else if (QuestionViewAdapter.IsButtonViewUrlQuestion(Integer.valueOf(answerType))){
            return validateTextQuestion(bean);
        }

        return errMessage;
    }

    public List<String> validateLookupQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        if(Global.AT_LOOKUP_DUKCAPIL.equals(bean.getAnswer_type())){
            if (bean.isRelevantMandatory()) {
                if (bean.getAnswer() == null || bean.getAnswer().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + msgRequired);
                }else if(bean.getDataDukcapil()==null || "".equals(bean.getDataDukcapil())){
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.validation_required));
                }
            }
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
                if (bean.getAnswer() == null || bean.getAnswer().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + msgRequired);
                }else if(bean.getDataDukcapil()==null || "".equals(bean.getDataDukcapil())){
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.validation_required));
                }
            }
        }else {
            if (bean.isRelevantMandatory() && (bean.getAnswer() == null || bean.getAnswer().isEmpty())) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING) && (bean.getAnswer() == null || bean.getAnswer().isEmpty())) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
            if (errMessage.isEmpty() && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
                String script = bean.getQuestion_validation();
                String answerType = bean.getAnswer_type();
                String answer = QuestionBean.getAnswer(bean);
                if (!validateByScript(answer, answerType, script)) {
                    if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                        errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                    } else {
                        errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                    }
                }
            }
        }

        return errMessage;
    }
    public List<String> validateDropdownQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        if (Global.AT_DROPDOWN.equals(bean.getAnswer_type()) ||
                Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type())){
            dropdownRequired = context.getString(R.string.dropdownmsgRequired);
            if (bean.isRelevantMandatory() && bean.getSelectedOptionAnswers().get(0).getCode().equals(context.getString(R.string.promptChooseOne))) {
                errMessage.add(dropdownRequired + " "+bean.getQuestion_label());
            }
            if (Global.TRUE_STRING.equals(bean.getIs_mandatory()) && null != bean.getSelectedOptionAnswers().get(0).getCode() &&
                    context.getString(R.string.promptChooseOne).equals(bean.getSelectedOptionAnswers().get(0).getCode())) {
                errMessage.add(dropdownRequired + " "+bean.getQuestion_label());
            }
            if (!bean.getRegex().isEmpty() && bean.getAnswer() != null && !regexIsMatch(bean.getAnswer(), bean.getRegex())) {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
            if(bean.getOptionAnswers() == null || bean.getOptionAnswers().isEmpty()){
                errMessage.add(context.getString(R.string.lookup_not_available, bean.getLov_group()));
            }
            if(bean.getSelectedOptionAnswers()!= null && !bean.getSelectedOptionAnswers().isEmpty() ){//validate whether in new task / save draft
                for(int i = 0 ; i < bean.getOptionAnswers().size() ; i++){
                    if (!bean.getOptionAnswers().get(i).getCode().equals(bean.getSelectedOptionAnswers().get(0).getCode())){ // if LOV changed, set isChange to true
                        bean.setIsCanChange(true);
                        bean.setChange(true);
                    } else {
                        bean.setIsCanChange(false);
                        bean.setChange(false);
                        break;
                    }
                }
                if(bean.isChange()){
                   errMessage.add(dropdownRequired + " " + bean.getQuestion_label());
               }
            }
        }
        return errMessage;
    }
    public List<String> validateDrawingQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            byte[] img = bean.getImgAnswer();
            if (img == null || img.length < 1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }
        if (bean.isRelevantMandatory()) {
            byte[] img = bean.getImgAnswer();
            if (img == null || img.length < 1) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        return errMessage;
    }

    public List<String> validateImageQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        if (DynamicFormActivity.getIsVerified() || DynamicFormActivity.getIsApproval()) {
        } else {
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING) || bean.isRelevantMandatory()) {
                byte[] img = bean.getImgAnswer();
                if (img == null || img.length < 1) {
                    errMessage.add(bean.getQuestion_label() + " " + msgRequired);
                    return errMessage;
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

    public List<String> validateMultipleQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();

        if ((bean.getIs_mandatory().equals(Global.TRUE_STRING) || bean.isRelevantMandatory()) && (bean.getSelectedOptionAnswers() == null || bean.getSelectedOptionAnswers().isEmpty())) {
            errMessage.add(bean.getQuestion_label() + " " + msgRequired);
        }
        if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type()) ||
                Global.AT_RADIO_W_DESCRIPTION.equals(bean.getAnswer_type())) {
            if ((!bean.getRegex().isEmpty() && bean.getAnswer() != null) && !regexIsMatch(bean.getAnswer(), bean.getRegex())) {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
        } else if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(bean.getAnswer_type()) && !bean.getSelectedOptionAnswers().isEmpty()) {
            String[] mDescription = bean.getAnswer().split(";");
            for (int i=0; i<mDescription.length; i++) {
                mDescription[i] = mDescription[i].trim();
                if (!mDescription[i].isEmpty() && (!bean.getRegex().isEmpty() && bean.getRegex() != null) && !regexIsMatch(mDescription[i], bean.getRegex())) {
                    errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
                    break;
                }
            }
        }
        if (errMessage.isEmpty() && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation();
            String answerType = bean.getAnswer_type();
            String answer = QuestionBean.getAnswer(bean);
            if (!validateByScript(answer, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }
        return errMessage;
    }

    public List<String> validateTextQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        String answer = bean.getAnswer();
        if (Global.AT_CURRENCY.equals(bean.getAnswer_type()) && (answer != null && answer.length() > 0)) {
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
        bean.setAnswer(answer);
        if ((Global.TRUE_STRING.equals(bean.getIs_mandatory()) || bean.isRelevantMandatory()) && (answer == null || "".equals(answer))) {
            errMessage.add(bean.getQuestion_label() + " " + msgRequired);
        }

        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";

        if (errMessage.isEmpty() && !regex.equals("") && ((answer != null && !answer.isEmpty()) || bean.isMandatory() || bean.isRelevantMandatory())) {
            if (Global.IS_DEV) Log.i(TAG,"!regex.equals" + regex);
            if (!regexIsMatch(bean.getAnswer(), bean.getRegex())) {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
        }
        if (errMessage.isEmpty() && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation();
            String answerType = bean.getAnswer_type();
            if (!validateByScript(answer, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }
        return errMessage;
    }

    public List<String> validateTextWithSuggestionQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        String answer = bean.getAnswer();
        Lookup selectedOption = null;
        if (answer != null && !answer.isEmpty()) {
            List<OptionAnswerBean> selectedOptions = bean.getSelectedOptionAnswers();
            if (selectedOptions == null || selectedOptions.isEmpty()) {
                selectedOption = LookupDataAccess.getOneByValueAndlovGroup(context, bean.getLov_group(), answer);
                if (selectedOption == null) {
                    if (!Tool.isInternetconnected(context))
                        errMessage.add(bean.getQuestion_label() + ": " + answer + " " + context.getString(R.string.not_available));
                    else
                        errMessage.add(bean.getQuestion_label() + ": " + answer + " " + context.getString(R.string.not_allowed));
                } else{
                    List<OptionAnswerBean> selected = new ArrayList<>();
                    selected.add(new OptionAnswerBean(selectedOption));
                    bean.setSelectedOptionAnswers(selected);
                }
            }else{
                selectedOption = selectedOptions.get(0);
            }
        }
        if ((Global.TRUE_STRING.equals(bean.getIs_mandatory()) || bean.isRelevantMandatory()) && (answer == null || answer.isEmpty())) {
            if (!errMessage.isEmpty())
                errMessage.add("\n");
            errMessage.add(bean.getQuestion_label() + " " + msgRequired);
        }
        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";
        if (!regex.equals("") && !bean.getAnswer().trim().equals("")) {
            if (Global.IS_DEV) Log.i(TAG,"!regex.equals" + regex);
            if (!regexIsMatch(bean.getAnswer(), bean.getRegex())) {
                errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
            }
        }
        if (errMessage.isEmpty() && selectedOption != null && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation();
            String answerType = bean.getAnswer_type();
            String answer2 = selectedOption.getCode();
            if (!validateByScript(answer2, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }
        return errMessage;
    }

    public List<String> validateLocationQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();

        if (!DynamicFormActivity.getIsVerified() && !DynamicFormActivity.getIsApproval()) {
            String answer = bean.getAnswer();

            if ((Global.TRUE_STRING.equals(bean.getIs_mandatory()) || bean.isRelevantMandatory()) && (answer == null || "".equals(answer))) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }
        return errMessage;
    }

    public List<String> validateDateTimeQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();

        String answer = bean.getAnswer();
        Date date2 = null;
        boolean isSet = true;
        String answerType = bean.getAnswer_type();
        try {

            String format = null;
            if (Global.AT_DATE.equals(answerType) || Global.AT_DATE_TIME.equals(answerType)) {
                format = Global.DATE_STR_FORMAT_GSON;
                date2 = Formatter.parseDate(answer, format);
            } else if (Global.AT_TIME.equals(answerType)) {
                format = Global.TIME_STR_FORMAT2;
                date2 = Formatter.parseDate(answer, format);
            }
        } catch (Exception pe) {
            try {
                String format = null;
                if (Global.AT_DATE.equals(answerType)) {
                    format = Global.DATE_STR_FORMAT;
                    date2 = Formatter.parseDate(answer, format);
                } else if (Global.AT_TIME.equals(answerType)) {
                    format = Global.TIME_STR_FORMAT;
                    date2 = Formatter.parseDate(answer, format);
                } else if (Global.AT_DATE_TIME.equals(answerType)) {
                    format = Global.DATE_TIME_STR_FORMAT;
                    date2 = Formatter.parseDate(answer, format);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                isSet = false;
            }
        }

        if (bean.getIs_holiday_allowed() != null && !bean.getIs_holiday_allowed().equals(HOLIDAY_IS_NOT_ALLOWED)) {
            Date date3 = null;
            if (Global.AT_DATE_TIME.equals(answerType)) {
                String date4;
                try {
                    date4 = Formatter.formatDate(date2, Global.DATE_STR_FORMAT2);
                    date3 = Formatter.parseDate(date4, Global.DATE_STR_FORMAT2);
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                }
            } else {
                if(null != answer) {
                    try {
                        date3 = Formatter.parseDate(answer, Global.DATE_STR_FORMAT_GSON);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            }

            if (date3 != null) {
                Holiday hday = HolidayDataAccess.getOneByDate(context, date3);
                if (hday != null) {
                    errMessage.add(bean.getQuestion_label() + " "
                            + "can not set on " + hday.getH_desc());
                }
            }
        }

        if ((Global.TRUE_STRING.equals(bean.getIs_mandatory()) || bean.isRelevantMandatory()) && !isSet) {
            errMessage.add(bean.getQuestion_label() + " " + msgRequired);
        }

        int txtMaxLength  = 0;
        try {
            //Nendi: 2019.07.01 | Add null check
            int maxLength = (bean.getMax_length() == null) ? 0 : bean.getMax_length();
            if (maxLength > 0) {
                Calendar now = Calendar.getInstance(TimeZone
                        .getDefault());
                Calendar date = Calendar.getInstance(TimeZone
                        .getDefault());
                date.setTime(date2);
                if (Global.AT_TIME.equals(answerType)) {
                    date.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    date.set(Calendar.MONTH, now.get(Calendar.MONTH));
                    date.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                }
                long diff = (date.getTime().getTime() - now.getTime()
                        .getTime());
                long diffDay = diff / 86400000;
                long diffHour = diff % 86400000;
                long diffHours = diffHour / 3600000;
                if (diffHours > 0) {
                    diffDay += 1;
                }

                if (diffDay < 0) {
                    txtMaxLength = (int) diffDay;
                } else if (diffDay > maxLength) {
                    txtMaxLength = (int) diffDay;
                } else {
                    txtMaxLength = maxLength;
                }
            } else {
                Date today = Tool.getSystemDate();
                Calendar now = Calendar.getInstance(TimeZone
                        .getDefault());
                now.setTime(today);
                Calendar date = Calendar.getInstance(TimeZone
                        .getDefault());
                date.setTime(date2);
                if (Global.AT_TIME.equals(answerType)) {
                    date.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    date.set(Calendar.MONTH, now.get(Calendar.MONTH));
                    date.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                }
                long diff = (date.getTime().getTime() - now.getTime()
                        .getTime());
                long diffDay = diff / 86400000;

                if (diffDay > 0) {
                    txtMaxLength = (int) diffDay;
                } else if (diffDay < maxLength) {
                    txtMaxLength = (int) diffDay;
                } else {
                    txtMaxLength = maxLength;
                }
            }

            if (maxLength == -999) {
                if (txtMaxLength > 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_before_today));
                }
            } else if (maxLength < 0) {
                if (txtMaxLength > 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_before_today));
                } else if (txtMaxLength < maxLength) {
                    Calendar date = Calendar.getInstance(TimeZone
                            .getDefault());
                    date.setTime(Tool.getSystemDateTime());
                    date.add(Calendar.DATE, maxLength);
                    Date newDate = date.getTime();
                    String sDate = Formatter.formatDate(newDate, Global.DATE_STR_FORMAT3);
                    String message = bean.getQuestion_label() + " " + context.getString(R.string.more_than_date, sDate);
                    errMessage.add(message);
                }
            } else if (maxLength > 0) {
                if (txtMaxLength < 0) {
                    errMessage.add(bean.getQuestion_label() + " " + context.getString(R.string.date_must_after_today));
                } else if (txtMaxLength > maxLength) {
                    Calendar date = Calendar.getInstance(TimeZone
                            .getDefault());
                    date.setTime(Tool.getSystemDateTime());
                    date.add(Calendar.DATE, maxLength);
                    Date newDate = date.getTime();
                    String sDate = Formatter.formatDate(newDate, Global.DATE_STR_FORMAT3);
                    String message = bean.getQuestion_label() + " " + context.getString(R.string.less_than_date, sDate);
                    errMessage.add(message);
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        if (errMessage.isEmpty() && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation().trim();
            if (!validateByScript(answer, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }
        return errMessage;
    }

    private List<String> validatePhoneNumberQuestion(QuestionBean bean) {
        List<String> errMessage = new ArrayList<>();
        String answer = bean.getAnswer();
        bean.setAnswer(answer);

        if (bean.getIs_mandatory().equals(Global.TRUE_STRING)) {
            if (answer == null || "".equals(answer)) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";

        if (errMessage.size() == 0) {
            if (!regex.equals("")) {
                if ((answer != null && !answer.isEmpty()) || bean.isMandatory()) {
                    System.out.println("!regex.equals" + regex);
                    if (regexIsMatch(bean.getAnswer(), bean.getRegex())) {

                    } else {
                        errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
                    }
                }
            }
        }

        if (!bean.isBtnCheckClicked()) {
            if (!"".equals(answer)) {
                errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.click_button_misscall_required));
            } else {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        if (errMessage.size() == 0 && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation();
            String answerType = bean.getAnswer_type();
            if (!validateByScript(answer, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }

        return errMessage;
    }

    public boolean validateByScript(String answer, String answerType, String script) {
        boolean isValid = false;
        String format = null;
        String convertedExpression = new String(script);        //make a copy of
        if (convertedExpression == null || convertedExpression.length() == 0) {
            return true;
        } else {
            boolean needReplacing = true;
            while (needReplacing) {
                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        String finalIdentifier = identifier.substring(idxOfOpenAbs + 1);
                        String flatAnswer = "";
                        if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                            int idxOfOpenAt = flatAnswer.indexOf('@');
                            if (idxOfOpenAt != -1) {
                                flatAnswer = flatAnswer.substring(0, idxOfOpenAt);
                            }
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                        } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                        } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                        } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                            flatAnswer = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                        } else if (finalIdentifier.equals(Global.IDF_ANSWER_BEAN)) {
                            try {
                                if (answerType.equals(Global.AT_TIME)) {
                                    format = Global.TIME_STR_FORMAT;
                                    String formatDate = Global.TIME_STR_FORMAT2;
                                    Date date2 = null;
                                    try {
                                        date2 = Formatter.parseDate(answer, formatDate);
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        date2 = Formatter.parseDate(answer, format);
                                    }
                                    Calendar now = Calendar.getInstance(TimeZone
                                            .getDefault());
                                    Calendar date = Calendar.getInstance(TimeZone
                                            .getDefault());
                                    date.setTime(date2);
                                    date.set(Calendar.YEAR, now.get(Calendar.YEAR));
                                    date.set(Calendar.MONTH, now.get(Calendar.MONTH));
                                    date.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                                    flatAnswer = Formatter.formatDate(date.getTime(), format);
                                } else if (answerType.equals(Global.AT_DATE_TIME)) {
                                    format = Global.DATE_TIME_STR_FORMAT;
                                    String formatDate = Global.DATE_STR_FORMAT_GSON;
                                    Date date2 = null;
                                    try {
                                        date2 = Formatter.parseDate(answer, formatDate);
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        date2 = Formatter.parseDate(answer, format);
                                    }
                                    Calendar date = Calendar.getInstance(TimeZone
                                            .getDefault());
                                    date.setTime(date2);
                                    flatAnswer = Formatter.formatDate(date.getTime(), format);
                                } else if (answerType.equals(Global.AT_DATE)) {
                                    format = Global.DATE_STR_FORMAT;
                                    String formatDate = Global.DATE_STR_FORMAT_GSON;
                                    Date date2 = null;
                                    try {
                                        date2 = Formatter.parseDate(answer, formatDate);
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        date2 = Formatter.parseDate(answer, format);
                                    }
                                    Calendar date = Calendar.getInstance(TimeZone
                                            .getDefault());
                                    date.setTime(date2);
                                    flatAnswer = Formatter.formatDate(date.getTime(), format);
                                } else {
                                    flatAnswer = answer;
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV)
                                    e.printStackTrace();
                            }
                        } else if (finalIdentifier.equals(Global.IDF_THIS_YEAR)) {
                            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                            flatAnswer = String.valueOf(cal.get(Calendar.YEAR));
                        } else if (finalIdentifier.equals(Global.IDF_NOWADAYS)) {
                            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                            try {
                                if (answerType.equals(Global.AT_TIME)) {
                                    format = Global.TIME_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                } else if (answerType.equals(Global.AT_DATE)) {
                                    format = Global.DATE_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                } else {
                                    format = Global.DATE_TIME_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV)
                                    e.printStackTrace();
                            }
                        } else if (finalIdentifier.equals(Global.IDF_YESTERDAY)) {
                            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                            cal.add(Calendar.DATE, -1);
                            try {
                                if (answerType.equals(Global.AT_TIME)) {
                                    format = Global.TIME_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                } else if (answerType.equals(Global.AT_DATE)) {
                                    format = Global.DATE_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                } else {
                                    format = Global.DATE_TIME_STR_FORMAT;
                                    flatAnswer = Formatter.formatDate(cal.getTime(), format);
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV)
                                    e.printStackTrace();
                            }
                        }
                        String regexDigit = "\\d+(?:\\.\\d+)?";
                        if (flatAnswer != null && flatAnswer.length() > 0) {
                            if(flatAnswer.matches(regexDigit)) {
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                            } else {
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", "'"+flatAnswer+"'");
                            }
                        } else {
                            //if there's no answer, just hide the question
                            return false;
                        }

                    } else {
                        QuestionBean bean = Constant.getListOfQuestion().get(identifier);
                        if (bean != null) {
                            String flatAnswer = QuestionBean.getAnswer(bean);
                            if (flatAnswer != null && flatAnswer.length() > 0) {
                                //NOTE: though it's possible to just iterate on flatAnswer substrings, we prefer to stay on method if size is 1
                                String[] answers = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                                if (answers.length == 1) {
                                    try {
                                        if (bean.getAnswer_type().equals(Global.AT_TIME)) {
                                            format = Global.TIME_STR_FORMAT;
                                            String formatDate = Global.TIME_STR_FORMAT2;
                                            Date date2 = null;
                                            try {
                                                date2 = Formatter.parseDate(answers[0], formatDate);
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                date2 = Formatter.parseDate(answers[0], format);
                                            }
                                            Calendar now = Calendar.getInstance(TimeZone
                                                    .getDefault());
                                            Calendar date = Calendar.getInstance(TimeZone
                                                    .getDefault());
                                            date.setTime(date2);
                                            date.set(Calendar.YEAR, now.get(Calendar.YEAR));
                                            date.set(Calendar.MONTH, now.get(Calendar.MONTH));
                                            date.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                                            flatAnswer = Formatter.formatDate(date.getTime(), format);
                                        } else if (bean.getAnswer_type().equals(Global.AT_DATE_TIME)) {
                                            format = Global.DATE_TIME_STR_FORMAT;
                                            String formatDate = Global.DATE_STR_FORMAT_GSON;
                                            Date date2 = null;
                                            try {
                                                date2 = Formatter.parseDate(answers[0], formatDate);
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                date2 = Formatter.parseDate(answers[0], format);
                                            }
                                            Calendar date = Calendar.getInstance(TimeZone
                                                    .getDefault());
                                            date.setTime(date2);
                                            flatAnswer = Formatter.formatDate(date.getTime(), format);
                                        } else if (bean.getAnswer_type().equals(Global.AT_DATE)) {
                                            format = Global.DATE_STR_FORMAT;
                                            String formatDate = Global.DATE_STR_FORMAT_GSON;
                                            Date date2 = null;
                                            try {
                                                date2 = Formatter.parseDate(answers[0], formatDate);
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                date2 = Formatter.parseDate(answers[0], format);
                                            }
                                            Calendar date = Calendar.getInstance(TimeZone
                                                    .getDefault());
                                            date.setTime(date2);
                                            flatAnswer = Formatter.formatDate(date.getTime(), format);
                                        } else {
                                            flatAnswer = answers[0];
                                        }
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        e.printStackTrace();
                                    }
                                    String regexDigit = "\\d+(?:\\.\\d+)?";
                                    if(flatAnswer.matches(regexDigit)) {
                                        convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                                    } else {
                                        convertedExpression = convertedExpression.replace("{" + identifier + "}", "'"+flatAnswer+"'");
                                    }
                                } else {
                                    //NOTE: going into in-depth loop, won't go outside of this 'else'
                                    for (int i = 0; i < answers.length; i++) {
                                        String regexDigit = "\\d+(?:\\.\\d+)?";
                                        String convertedSubExpression = "";
                                        if(answers[i].matches(regexDigit)) {
                                            convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                                        } else {
                                            convertedSubExpression = convertedExpression.replace("{" + identifier + "}", "'"+answers[i]+"'");
                                        }
                                        boolean isVisible = validateByScript(answer, answerType, convertedSubExpression);
                                        if (isVisible) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            } else {
                                flatAnswer = "0";
                                convertedExpression = convertedExpression.replace("{" + identifier + "}", flatAnswer);
                            }
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
                    needReplacing = false;
                }
            }
            try {
                OperatorSet opSet = OperatorSet.getStandardOperatorSet();
                opSet.addOperator("!=", NotEqualSymbol.class);
                opSet.addOperator("if", IfElseFunction.class);
                opSet.addOperator("and", AndSymbol.class);
                opSet.addOperator("or", OrSymbol.class);
                convertedExpression = convertedExpression.replace("\"","'");
                Expression exp = new Expression(convertedExpression);
                exp.setOperatorSet(opSet);
                if (answerType.equals(Global.AT_DATE_TIME)) {
                    exp.setResolver(new DateTimeResolver());
                } else if (answerType.equals(Global.AT_DATE)) {
                    exp.setResolver(new DateResolver());
                } else if (answerType.equals(Global.AT_TIME)) {
                    exp.setResolver(new TimeResolver());
                }
                isValid = exp.evaluate().toBoolean();
                return isValid;
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                return false;
            }
        }
    }

    public boolean validateAllMandatory(List<QuestionBean> listOfQuestions, boolean displayMessage) {
        boolean result = true;
        for (QuestionBean bean : listOfQuestions) {
            if (bean.getIs_mandatory().equals(Global.TRUE_STRING) && bean.getIs_visible().equals(Global.TRUE_STRING)) {
                boolean isHaveAnswer = QuestionBean.isHaveAnswer(bean);
                if (!isHaveAnswer) {        //tidak ada isi
                    if (displayMessage)
                        Toast.makeText(context, bean.getQuestion_label() + " " + context.getString(R.string.msgRequired), Toast.LENGTH_SHORT).show();
                    result = false;
                }
            }
        }
        return result;
    }

    private List<String> validateRvMobileQuestion(QuestionBean bean) {//}, TextQuestionViewHolder view) {
        List<String> errMessage = new ArrayList<>();
        String answer = bean.getAnswer();//view.mQuestionAnswer.getText().toString().trim();
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
            if (answer == null || "".equals(answer)) {
                errMessage.add(bean.getQuestion_label() + " " + msgRequired);
            }
        }

        String regex = "";
        regex = bean.getRegex();
        if (regex == null) regex = "";

        if (errMessage.size() == 0) {
            if (!regex.equals("")) {
                if ((answer != null && !answer.isEmpty()) || bean.isMandatory()) {
                    System.out.println("!regex.equals" + regex);
                    if (regexIsMatch(bean.getAnswer(), bean.getRegex())) {

                    } else {
                        errMessage.add(bean.getQuestion_label() + " " + "Invalid Input Format");
                    }
                }
            }
        }
        if (errMessage.size() == 0 && bean.getQuestion_validation() != null && !bean.getQuestion_validation().isEmpty() && QuestionBean.isHaveAnswer(bean)) {
            String script = bean.getQuestion_validation();
            String answerType = bean.getAnswer_type();
            if (!validateByScript(answer, answerType, script)) {
                if (bean.getValidate_err_message() != null && !bean.getValidate_err_message().isEmpty()) {
                    errMessage.add(bean.getQuestion_label() + " " + bean.getValidate_err_message());
                } else {
                    errMessage.add(bean.getQuestion_label() + ": " + context.getString(R.string.input_not_valid));
                }
            }
        }
        return errMessage;
    }

    @Override
    public boolean validateCurrentPage(boolean isCekValidate, boolean isSave) {
        return false;
    }
}
