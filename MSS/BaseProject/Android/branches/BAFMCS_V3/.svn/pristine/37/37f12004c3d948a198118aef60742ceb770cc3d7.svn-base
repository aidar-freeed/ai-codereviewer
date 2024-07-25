package com.adins.mss.foundation.questiongenerator;

import android.app.Activity;
import androidx.annotation.Keep;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dukcapil.ResponseImageDkcp;
import com.adins.mss.base.dynamicform.form.models.LookupAnswerBean;
import com.adins.mss.base.dynamicform.form.models.LookupCriteriaBean;
import com.adins.mss.base.dynamicform.form.questions.QuestionViewAdapter;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestionBean extends QuestionSet implements Serializable {

    public static final String PLACEMENT_KEY_USER = "${user}";
    public static final String PLACEMENT_KEY_BRANCH = "${branch}";
    public transient LocationInfo locationInfo;
    String latitude;
    String longitude;
    boolean Relevanted;
    boolean relevant_mandatory;

    private List<OptionAnswerBean> optionAnswers = new ArrayList<>();
    //Glen 27 Aug 2014, option relevant
    private String lookupId;
    //Glen 14 Oct 2014, because multiplequestiontype may have more than one answer, change to array
    @Keep
    private List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();
    //Glen 22 Sept 2014, have more than one option relevance
    private String[] optionRelevances;
    private String optionRelevancesString;
    //Glen 16 Oct 2014, more type of affectedQuestion
    private List<QuestionBean> affectedQuestionBeanVisibility = new ArrayList<>();
    private List<QuestionBean> affectedQuestionBeanOptions = new ArrayList<>();
    private List<QuestionBean> affectedQuestionBeanCalculation = new ArrayList<>();
    private List<QuestionBean> affectedQuestionBeanCopyValue = new ArrayList<>();
    private String identifier;
    //Glen 20 Oct 2014m new param
    private boolean isNoHoliday;
    private String flatOptionAnswers;
    private String flatLovFilters;
    private String flatQuestionRelevants;
    private String intTextAnswer;
    private String lovCode;
    @Keep
    private String answer;
    private byte[] imgAnswer;
    private String lovId;
    private byte[] imgLocation;
    private List<LookupCriteriaBean> lookupCriteriaList;
    private LookupCriteriaBean selectedCriteriaBean;
    private int isCanChange = 0;
    private boolean change;
    private List<LookupCriteriaBean> otherLookupCriteriaList;
    private List<LookupCriteriaBean> lookupRecipientList;
    private List<LookupAnswerBean> lookupsAnswerBean;
    private Date imgTimestamp;
    private String dataDukcapil;
    private ResponseImageDkcp responseImageDkcp;
    //2018-08-20 penambahan flag untuk answer type validation phone number
    private boolean btnCheckClicked;
    private ArrayList<byte []> listImgByteArray;

    public QuestionBean(QuestionSet questionSet) {
        setUuid_question_set(questionSet.getUuid_question_set());
        setQuestion_group_id(questionSet.getQuestion_group_id());
        setQuestion_group_name(questionSet.getQuestion_group_name());
        setQuestion_group_order(questionSet.getQuestion_group_order());
        setQuestion_id(questionSet.getQuestion_id());
        setQuestion_label(questionSet.getQuestion_label());
        setQuestion_order(questionSet.getQuestion_order());
        setAnswer_type(questionSet.getAnswer_type());
        setOption_answers(questionSet.getOption_answers());
        setChoice_filter(questionSet.getChoice_filter());
        setIs_mandatory(questionSet.getIs_mandatory());
        setMax_length(questionSet.getMax_length());
        setIs_visible(questionSet.getIs_visible());
        setIs_readonly(questionSet.getIs_readonly());
        setRegex(questionSet.getRegex());
        setRelevant_question(questionSet.getRelevant_question());
        setCalculate(questionSet.getCalculate());
        setConstraint_message(questionSet.getConstraint_message());
        setUsr_crt(questionSet.getUsr_crt());
        setDtm_crt(questionSet.getDtm_crt());
        setUsr_upd(questionSet.getUsr_upd());
        setDtm_upd(questionSet.getDtm_upd());
        setIdentifier_name(questionSet.getIdentifier_name());
        setUuid_scheme(questionSet.getUuid_scheme());
        setLov_group(questionSet.getLov_group());
        setTag(questionSet.getTag());
        setIs_holiday_allowed(questionSet.getIs_holiday_allowed());
        setImg_quality(questionSet.getImg_quality());
        setValidate_err_message(questionSet.getValidate_err_message());
        setQuestion_validation(questionSet.getQuestion_validation());
        setQuestion_value(questionSet.getQuestion_value());
        setForm_version(questionSet.getForm_version());
        setRelevant_mandatory(questionSet.getRelevant_mandatory());
    }

    public static LocationInfo getLocationInfoFromAnswer(TaskD answer) {
        LocationInfo info = new LocationInfo(Tool.getUUID());
        info.setLatitude(answer.getLatitude());
        info.setLongitude(answer.getLongitude());
        info.setCid(answer.getCid());
        info.setMcc(answer.getMcc());
        info.setMnc(answer.getMnc());
        info.setLac(answer.getLac());
        info.setAccuracy(answer.getAccuracy());
        info.setGps_time(answer.getGps_time());

        return info;
    }

    public static List<QuestionBean> matchAnswerToQuestion(Activity activity, final List<QuestionBean> listOfQuestions, final List<TaskD> listOfAnswers, String uuidScheme) {

        List<TaskD> optionTaskD = new ArrayList<>();
        for (int i = 0; i < listOfAnswers.size(); i++) {
            TaskD tempAnsBean = listOfAnswers.get(i);
            StringBuilder sb = new StringBuilder();
            QuestionSet tempAnsBean2 = QuestionSetDataAccess.getOne(activity,
                    uuidScheme, tempAnsBean.getQuestion_id(), tempAnsBean.getQuestion_group_id());
            QuestionBean qBean = null;
            for (QuestionBean bean2 : listOfQuestions) {
                if (bean2.getIdentifier_name().equals(tempAnsBean2.getIdentifier_name())) {
                    qBean = bean2;
                    break;
                }
            }
            String tempQgId = tempAnsBean.getQuestion_group_id();
            if (tempQgId == null)
                tempQgId = "";
            String tempQId = tempAnsBean.getQuestion_id();
            List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();
            String lookUpId = tempAnsBean.getUuid_lookup();
            if (lookUpId == null) {
                lookUpId = tempAnsBean.getOption_answer_id();
                try {
                    tempAnsBean.setUuid_lookup(lookUpId);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            String lovCode = tempAnsBean.getLov();
            if (lookUpId != null && lovCode != null) {
                try {
                    OptionAnswerBean selectedOption = null;
                    if(qBean.getTag() != null && qBean.getTag().equals(Global.TAG_RV_NUMBER)){
                        ReceiptVoucher rv = ReceiptVoucherDataAccess.getOne(activity,GlobalData.getSharedGlobalData().getUser().getUuid_user(),lookUpId);
                        if(rv != null){
                            selectedOption = new OptionAnswerBean(rv);
                        }
                        else {
                            selectedOption = new OptionAnswerBean(null,tempAnsBean.getText_answer(),null);
                            selectedOption.setUuid_lookup(lookUpId);
                            qBean.setAnswer(tempAnsBean.getText_answer());
                        }
                    }
                    else {
                        Lookup lookup = LookupDataAccess.getOne(activity, lookUpId);
                        selectedOption = new OptionAnswerBean(lookup);
                    }
                    selectedOption.setSelected(true);
                    selectedOptionAnswers.add(selectedOption);
                    sb.append(tempAnsBean.getText_answer());
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(qBean.getAnswer_type())
                || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(qBean.getAnswer_type())
                || Global.AT_MULTIPLE.equals(qBean.getAnswer_type())){

                for (int j = i + 1; j < listOfAnswers.size(); j++) {
                    TaskD newAnsBean = listOfAnswers.get(j);
                    String newQgId = newAnsBean.getQuestion_group_id();
                    String newQId = newAnsBean.getQuestion_id();
                    if (newQgId == null)
                        newQgId = "";
                    if (tempQgId.equals(newQgId) && tempQId.equals(newQId)) {
                        String nlookUpId = newAnsBean.getUuid_lookup();
                        if (nlookUpId == null) {
                            nlookUpId = newAnsBean.getOption_answer_id();
                            try {
                                newAnsBean.setUuid_lookup(nlookUpId);
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        }
                        String nlovCode = newAnsBean.getLov();
                        if (nlookUpId != null && nlovCode != null) {
                            try {
                                Lookup lookup2 = LookupDataAccess.getOne(activity, nlookUpId);
                                OptionAnswerBean selectedOption2 = new OptionAnswerBean(lookup2);
                                selectedOption2.setSelected(true);
                                selectedOptionAnswers.add(selectedOption2);
                                if (qBean != null && !Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(qBean.getAnswer_type())) {
                                    if (Global.AT_MULTIPLE_W_DESCRIPTION.equals(qBean.getAnswer_type())) {
                                        sb.append(Global.DELIMETER_DATA);
                                    } else {
                                        if (sb.length() > 0) {
                                            sb.append(Global.DELIMETER_DATA);
                                        }
                                    }
                                    sb.append(newAnsBean.getText_answer());
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        }
                    }
                    else {
                        i = j - 1;
                        break;
                    }
                }
            }
            if (sb.length() > 0) {
                tempAnsBean.setText_answer(sb.toString());
            }
            tempAnsBean.setUuid_lookup(OptionAnswerBean.optionToSelectedString(selectedOptionAnswers));
            optionTaskD.add(tempAnsBean);
        }


        final List<QuestionBean> questionBeans = new ArrayList<>();
        String tempqgId = "";
        String tempqId = "";
        for (TaskD ansBean : listOfAnswers) {
            String qgId = ansBean.getQuestion_group_id();
            if (qgId == null)
                qgId = "";
            String qId = ansBean.getQuestion_id();
            String textAnswer = ansBean.getText_answer();

            qLoop:
            for (QuestionBean qBean : listOfQuestions) {
                if (qgId.equalsIgnoreCase(qBean.getQuestion_group_id()) && qId.equalsIgnoreCase(qBean.getQuestion_id())) {
                    if (tempqgId.equals(qgId) && tempqId.equals(qId)) {
                        break qLoop;
                    } else {
                        tempqgId = qgId;
                        tempqId = qId;
                    }
                    String answerType = qBean.getAnswer_type();
                    if (Tool.isOptions(answerType)) {
                        String[] arrSelectedAnswer = null;
                        try {
                            arrSelectedAnswer = Tool.split(ansBean.getUuid_lookup(), Global.DELIMETER_DATA);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            arrSelectedAnswer = new String[0];
                        }

                        List<OptionAnswerBean> optionAnswers = qBean.getOptionAnswers();

                        //Glen 15 Oct 2014, save options on selectedOptionAnswer
                        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();

                        for (int j = 0; j < arrSelectedAnswer.length; j++) {
                            String selectedAnswer = arrSelectedAnswer[j];
                            //Glen 15 OCt 2014, new logic to copy answer to selectedOptionAnswer on question bean
                            if (optionAnswers == null || optionAnswers.isEmpty()) {
                                String lookUpId = selectedAnswer;
                                String lovCode = ansBean.getLov();
                                if (lookUpId != null && lovCode != null) {
                                    try {
                                        OptionAnswerBean selectedOption = null;
                                        if(qBean.getTag() != null && qBean.getTag().equals(Global.TAG_RV_NUMBER)){
                                            ReceiptVoucher rv = ReceiptVoucherDataAccess.getOne(activity,GlobalData.getSharedGlobalData().getUser().getUuid_user(),lookUpId);
                                            if(rv != null){
                                                selectedOption = new OptionAnswerBean(rv);
                                            }
                                            else {
                                                selectedOption = new OptionAnswerBean(null,ansBean.getText_answer(),null);
                                                selectedOption.setUuid_lookup(lookUpId);
                                                qBean.setAnswer(ansBean.getText_answer());
                                            }
                                        }
                                        else {
                                            Lookup lookup = LookupDataAccess.getOne(activity, lookUpId);
                                            selectedOption = new OptionAnswerBean(lookup);
                                        }
                                        selectedOption.setSelected(true);
                                        if (!qBean.getAnswer_type().equals(Global.AT_DROPDOWN) &&
                                                !qBean.getAnswer_type().equals(Global.AT_RADIO) &&
                                                !qBean.getAnswer_type().equals(Global.AT_MULTIPLE)) {
                                            qBean.setAnswer(textAnswer);
                                        }
                                        selectedOptionAnswers.add(selectedOption);
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        if (!qBean.getAnswer_type().equals(Global.AT_DROPDOWN) &&
                                                !qBean.getAnswer_type().equals(Global.AT_RADIO) &&
                                                !qBean.getAnswer_type().equals(Global.AT_MULTIPLE)) {
                                            qBean.setAnswer(textAnswer);
                                        }
                                    }

                                }
                            }
                        }

                        //Glen 15 Oct 2014, save on bean
                        qBean.setSelectedOptionAnswers(selectedOptionAnswers);
                        questionBeans.add(qBean);
                        break qLoop;
                    } else if (Global.AT_TEXT_WITH_SUGGESTION.equals(answerType)) {
                        List<OptionAnswerBean> optionAnswers = qBean.getOptionAnswers();

                        //Glen 15 Oct 2014, save options on selectedOptionAnswer
                        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();
                        //Glen 15 OCt 2014, new logic to copy answer to selectedOptionAnswer on question bean
                        if (optionAnswers == null || optionAnswers.isEmpty()) {
                            String lookUpId = ansBean.getUuid_lookup();
                            String lovCode = ansBean.getLov();
                            if (lookUpId != null && lovCode != null) {
                                try {
                                    Lookup lookup = LookupDataAccess.getOne(activity, lookUpId);
                                    OptionAnswerBean selectedOption = new OptionAnswerBean(lookup);
                                    selectedOption.setSelected(true);
                                    qBean.setAnswer(selectedOption.getValue());
                                    selectedOptionAnswers.add(selectedOption);
                                    qBean.setSelectedOptionAnswers(selectedOptionAnswers);
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    qBean.setAnswer(textAnswer);
                                }

                            }
                        }


                        //Glen 15 Oct 2014, save on bean
                        qBean.setSelectedOptionAnswers(selectedOptionAnswers);
                        questionBeans.add(qBean);
                        break qLoop;
                    } else if (Tool.isImage(answerType) || answerType.equals(Global.AT_DRAWING)) {
                        byte[] imgAnswer = ansBean.getImage();
                        qBean.setImgAnswer(imgAnswer);
                        if(ansBean.getImage_timestamp() != null){
                            qBean.setImgTimestamp(ansBean.getImage_timestamp());
                        }
                        try {
                            byte[] imgLocation = ansBean.getLocation_image();
                            qBean.setImgLocation(imgLocation);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        if (Tool.isHaveLocation(answerType)) {
                            LocationInfo info = new LocationInfo(Tool.getUUID());
                            info.setLatitude(ansBean.getLatitude());
                            info.setLongitude(ansBean.getLongitude());
                            info.setCid(ansBean.getCid());
                            info.setMcc(ansBean.getMcc());
                            info.setMnc(ansBean.getMnc());
                            info.setLac(ansBean.getLac());
                            info.setAccuracy(ansBean.getAccuracy());
                            info.setGps_time(ansBean.getGps_time());
                            info.setUser(GlobalData.getSharedGlobalData().getUser());

                            qBean.setLatitude(ansBean.getLatitude());
                            qBean.setLongitude(ansBean.getLongitude());
                            qBean.setLocationInfo(info);
                            qBean.setAnswer(textAnswer);
                        }else if (Global.AT_ID_CARD_PHOTO.equals(answerType)) {
                            qBean.setAnswer(textAnswer);
                            Gson gson = new Gson();
                            qBean.setResponseImageDkcp(gson.fromJson(ansBean.getData_dukcapil(),ResponseImageDkcp.class));
                        }
                        questionBeans.add(qBean);
                        break qLoop;
                    } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                        qBean.setLovId(ansBean.getLov());
                        qBean.setAnswer(textAnswer);
                        questionBeans.add(qBean);
                        break qLoop;
                    } else if (Tool.isHaveLocation(answerType)) {
                        qBean.setLocationInfo(QuestionBean.getLocationInfoFromAnswer(ansBean));
                        qBean.setLatitude(ansBean.getLatitude());
                        qBean.setLongitude(ansBean.getLongitude());
                        qBean.setAnswer(textAnswer);
                        questionBeans.add(qBean);
                        break qLoop;
                    } else {
                        qBean.setAnswer(textAnswer);
                        qBean.setIntTextAnswer(textAnswer);
                        questionBeans.add(qBean);
                        break qLoop;
                    }
                }
            }
        }

        List<QuestionBean> newQuestionBeans = new ArrayList<>();
        for (QuestionBean bean : listOfQuestions) {
            String uuid_qs = bean.getUuid_question_set();
            QuestionBean newBean = null;
            for (QuestionBean nbean : questionBeans) {
                String n_uuid_qs = nbean.getUuid_question_set();
                if (n_uuid_qs.equals(uuid_qs)) {
                    newBean = nbean;
                    break;
                } else {
                    newBean = bean;
                }
            }
            newQuestionBeans.add(newBean);

            if (questionBeans.isEmpty()) {
                newQuestionBeans.clear();
                newQuestionBeans.addAll(listOfQuestions);
            }
        }
        return newQuestionBeans;
    }

    public static List<QuestionBean> matchQuestionWithAnswer(Activity activity, List<QuestionBean> listOfQuestions, List<TaskD> listOfAnswers) {
        List<QuestionBean> questionBeans = new ArrayList<>();
        if (listOfQuestions.size() == listOfAnswers.size()) {
            int i = 0;
            for (QuestionBean bean : listOfQuestions) {
                try {
                    String answerType = bean.getAnswer_type();
                    String answer = listOfAnswers.get(i).getText_answer();
                    byte[] imgAnswer = listOfAnswers.get(i).getImage();
                    byte[] imgLocation = listOfAnswers.get(i).getLocation_image();
                    if (Tool.isOptions(answerType)) {

                        String[] arrSelectedAnswer = null;
                        try {
                            arrSelectedAnswer = Tool.split(answer, Global.DELIMETER_DATA);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            arrSelectedAnswer = new String[0];
                        }
                        OptionAnswerBean selectedOption = null;

                        List<OptionAnswerBean> optionAnswers = bean.getOptionAnswers();

                        //Glen 15 Oct 2014, save options on selectedOptionAnswer
                        List<OptionAnswerBean> selectedOptionAnswers = new ArrayList<>();

                        for (int j = 0; j < arrSelectedAnswer.length; j++) {
                            //Glen 15 OCt 2014, new logic to copy answer to selectedOptionAnswer on question bean
                            if (optionAnswers == null || optionAnswers.isEmpty()) {
                                String lookUpId = listOfAnswers.get(i).getUuid_lookup();
                                String lovCode = listOfAnswers.get(i).getLov();
                                if (lookUpId != null && lovCode != null) {
                                    Lookup lookup = LookupDataAccess.getOne(activity, lookUpId);
                                    selectedOption = new OptionAnswerBean(lookup);
                                    selectedOption.setSelected(true);
                                    selectedOptionAnswers.add(selectedOption);
                                }
                            }
                        }

                        //Glen 15 Oct 2014, save on bean
                        bean.setSelectedOptionAnswers(selectedOptionAnswers);

                    } else if (Tool.isImage(answerType)) {
                        try {
                            bean.setImgAnswer(imgAnswer);
                            bean.setImgLocation(imgLocation);
                            bean.setAnswer(answer);
                            Date timestamp = listOfAnswers.get(i).getImage_timestamp();
                            if(timestamp != null){
                                bean.setImgTimestamp(timestamp);
                            }
                        } catch (Exception e2) {
                            FireCrash.log(e2);
                        }
                    } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                        try {
                            String[] asnwersSelected = Tool.split(answer, Global.DELIMETER_SUBDATA);
                            bean.setLovId(asnwersSelected[0]);
                            bean.setAnswer(asnwersSelected[1]);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    } else if (Global.AT_ID_CARD_PHOTO.equals(answerType)) {
                        bean.setAnswer(answer);
                        Gson gson = new Gson();
                        bean.setResponseImageDkcp(gson.fromJson(bean.getDataDukcapil(),ResponseImageDkcp.class));
                    } else {
                        if ("null".equals(answer)) {
                            answer = null;
                        }
                        bean.setAnswer(answer);
                    }
                    if (Tool.isHaveLocation(answerType)) {
                        bean.setLatitude(listOfAnswers.get(i).getLatitude());
                        bean.setLongitude(listOfAnswers.get(i).getLongitude());
                    }
                    questionBeans.add(bean);
                    i++;
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                    continue;
                }
            }
        } else {
            throw new IllegalArgumentException("Question set and answer don't match.");
        }

        return questionBeans;

    }

    public static List<QuestionBean> getQuestionList(List<QuestionSet> questionSetList) {
        List<QuestionBean> questionBeanList = new ArrayList<>();
        try {
            for (QuestionSet bean : questionSetList) {
                QuestionBean qb = new QuestionBean(bean);
                questionBeanList.add(qb);

            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return questionBeanList;

    }

    public static String getAnswer(QuestionBean bean) {
        try {
            String answerType = bean.getAnswer_type();

            if (Global.AT_TEXT.equals(answerType) || Global.AT_NUMERIC.equals(answerType) ||
                    Global.AT_DECIMAL.equals(answerType) || Global.AT_DATE_TIME.equals(answerType) ||
                    Global.AT_DATE.equals(answerType) || Global.AT_TIME.equals(answerType) ||
                    Global.AT_CURRENCY.equals(answerType) || Global.AT_TEXT_MULTILINE.equals(answerType) ||
                    Tool.isImage(answerType) || Global.AT_TEXT_WITH_SUGGESTION.equals(answerType) ||
                    Global.AT_VALIDATION.equals(answerType) || Global.AT_RV_MOBILE.equals(answerType)) {
                return bean.getAnswer();
            } else if (Global.AT_GPS.equals(answerType) || Global.AT_GPS_N_LBS.equals(answerType)) {
                return bean.getAnswer();
            }
            //Glen 17 Oct 2014, new calculation type
            else if (Global.AT_CALCULATION.equals(answerType)) {
                return bean.getAnswer();
            } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                return bean.getLovId();
            }
            else if (Tool.isImage(answerType)) {
                return new String(bean.getImgAnswer());
            } else if (Global.AT_DROPDOWN.equals(answerType) || Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_RADIO.equals(answerType) || Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {
                if (bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty()) {
                    return bean.getSelectedOptionAnswers().get(0).getCode();
                }
                return bean.lovCode;

            } else if (Global.AT_MULTIPLE.equals(answerType) || Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)
                    || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
                if (bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (OptionAnswerBean answerBean : bean.getSelectedOptionAnswers()) {
                        if (sb.length() > 0)
                            sb.append(Global.DELIMETER_DATA);
                        sb.append(answerBean.getCode());

                    }
                    return sb.toString();
                }
                return bean.getAnswer();
            } else if (QuestionViewAdapter.IsLookupQuestion(answerType)) {
                return bean.getAnswer();
            }

            return null;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String getAnswerFromOptions(QuestionBean bean) {
        try {
            String answerType = bean.getAnswer_type();
            if (Global.AT_DROPDOWN.equals(answerType) || Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_RADIO.equals(answerType) || Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {
                if (bean.getSelectedOptionAnswers() != null) {
                    return bean.getSelectedOptionAnswers().get(0).getCode();
                }
                return bean.lovCode;

            } else if (Global.AT_MULTIPLE.equals(answerType) || Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)
                    || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
                if (bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (OptionAnswerBean answerBean : bean.getSelectedOptionAnswers()) {
                        if (sb.length() > 0)
                            sb.append(Global.DELIMETER_DATA);
                        sb.append(answerBean.getCode());

                    }
                    return sb.toString();
                }
                return bean.getAnswer();
            }

            return null;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isHaveAnswer(QuestionBean bean) {
        try {
            String answerType = bean.getAnswer_type();

            if (Global.AT_TEXT.equals(answerType) || Global.AT_NUMERIC.equals(answerType) ||
                    Global.AT_DECIMAL.equals(answerType) || Global.AT_DATE_TIME.equals(answerType) ||
                    Global.AT_DATE.equals(answerType) || Global.AT_TIME.equals(answerType) ||
                    Global.AT_CURRENCY.equals(answerType) || Global.AT_TEXT_MULTILINE.equals(answerType) ||
                    Global.AT_LOOKUP.equals(answerType) || Global.AT_TEXT_WITH_SUGGESTION.equals(answerType) ||
                    Global.AT_LOOKUP_DUKCAPIL.equals(answerType) || Global.AT_RV_MOBILE.equals(answerType) ||
                    Global.AT_VALIDATION.equals(answerType)) {
                return bean.getAnswer() != null && bean.getAnswer().length() > 0;
            } else if (Global.AT_GPS.equals(answerType) || Global.AT_GPS_N_LBS.equals(answerType)) {
                return bean.getAnswer() != null && bean.getAnswer().length() > 0;
            }
            //Glen 17 Oct 2014, new calculation type
            else if (Global.AT_CALCULATION.equals(answerType)) {
                return bean.getAnswer() != null && bean.getAnswer().length() > 0;
            } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                return bean.getLovId() != null && bean.getLovId().length() > 0;
            } else if (Global.AT_LOCATION.equals(answerType)) {
                return bean.getLocationInfo() != null;
            }
            else if (Tool.isImage(answerType) || Global.AT_DRAWING.equals(answerType)) {
                return bean.getImgAnswer() != null && bean.getImgAnswer().length > 0;
            } else if (Global.AT_DROPDOWN.equals(answerType) || Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_RADIO.equals(answerType) || Global.AT_RADIO_W_DESCRIPTION.equals(answerType)) {

                return bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty();

            } else if (Global.AT_MULTIPLE.equals(answerType) || Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)
                    || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {

                return bean.getSelectedOptionAnswers() != null && !bean.getSelectedOptionAnswers().isEmpty();
            } else if (Global.AT_BUTTON_VIEW_URL.equals(answerType)) {
                return bean.getAnswer() != null && bean.getAnswer().length() > 0;
            }

            return false;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return false;
        }
    }

    public static void resetAnswer(QuestionBean bean) {
        try {
            String answerType = bean.getAnswer_type();

            if (Global.AT_TEXT.equals(answerType) || Global.AT_NUMERIC.equals(answerType) ||
                    Global.AT_DECIMAL.equals(answerType) || Global.AT_DATE_TIME.equals(answerType) ||
                    Global.AT_DATE.equals(answerType) || Global.AT_TIME.equals(answerType) ||
                    Global.AT_CURRENCY.equals(answerType) || Global.AT_TEXT_MULTILINE.equals(answerType) ||
                    Global.AT_TEXT_WITH_SUGGESTION.equals(answerType) || Global.AT_LOOKUP.equals(answerType) ||
                    Global.AT_LOOKUP_DUKCAPIL.equals(answerType)) {
                bean.setSelectedOptionAnswers(new ArrayList<OptionAnswerBean>());
                bean.setLovCode("");
                bean.setAnswer(null);
            } else if(Global.AT_LOOKUP_DUKCAPIL.equals(answerType)){
                bean.setDataDukcapil(null);
            } else if (Global.AT_GPS.equals(answerType) || Global.AT_GPS_N_LBS.equals(answerType)) {
                bean.setAnswer(null);
            } else if (Global.AT_CALCULATION.equals(answerType)) {
                bean.setAnswer(null);
            } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                bean.setLovId(null);
            } else if (Global.AT_LOCATION.equals(answerType)) {
                bean.setLocationInfo(null);
            } else if (Tool.isImage(answerType) || Global.AT_DRAWING.equals(answerType)) {
                bean.setImgAnswer(null);
                bean.setImgTimestamp(null);
            } else if (Global.AT_DROPDOWN.equals(answerType) || Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_RADIO.equals(answerType) || Global.AT_RADIO_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_MULTIPLE.equals(answerType) || Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)
                    || Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(answerType)) {
                bean.setSelectedOptionAnswers(null);
            }

        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    public static String getAnswerToPrint(QuestionBean bean) {
        try {
            String answerType = bean.getAnswer_type();

            if (Global.AT_TEXT.equals(answerType) || Global.AT_NUMERIC.equals(answerType) ||
                    Global.AT_DECIMAL.equals(answerType) || Global.AT_CURRENCY.equals(answerType)) {
                return bean.getAnswer();
            } else if (Global.AT_GPS.equals(answerType) || Global.AT_GPS_N_LBS.equals(answerType)) {
                return bean.getAnswer();
            }
            //Glen 17 Oct 2014, new calculation type
            else if (Global.AT_CALCULATION.equals(answerType)) {
                return bean.getAnswer();
            } else if (Global.AT_DATE_TIME.equals(answerType) ||
                    Global.AT_DATE.equals(answerType) || Global.AT_TIME.equals(answerType)) {
                String answer = bean.getAnswer();
                if (answer == null || "".equals(answer.trim())) {
                    return "";
                } else {
                    String format = Formatter.getDateTimeFormat(answerType);
                    Date date = new Date(Formatter.stringToDate(answer));
                    return Formatter.formatDate(date, format);
                }
            } else if (Global.AT_LOV.equals(answerType) || Global.AT_LOV_W_FILTER.equals(answerType)) {
                String key = bean.getLovId();
                if (!Tool.isEmptyString(key)) {
                    String txt = null;
                    String value = bean.getAnswer();
                    if (key == null || "".equals(key.trim())) {
                        key = "";
                    }
                    if (value == null || "".equals(value.trim())) {
                        value = "";
                    }
                    txt = key + " - " + value;
                    return txt;
                } else {
                    return "";
                }
            } else if (Tool.isImage(answerType)) {
                return " [ IMAGE ] ";
            } else if (Global.AT_DROPDOWN.equals(answerType) || Global.AT_DROPDOWN_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_RADIO.equals(answerType) || Global.AT_RADIO_W_DESCRIPTION.equals(answerType) ||
                    Global.AT_MULTIPLE.equals(answerType) || Global.AT_MULTIPLE_W_DESCRIPTION.equals(answerType)) {
                StringBuilder sb = new StringBuilder();

                return sb.toString();
            } else {
                return bean.getAnswer();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return "";
        }
    }

    public List<LookupCriteriaBean> getLookupCriteriaList() {
        return lookupCriteriaList;
    }

    public void setLookupCriteriaList(List<LookupCriteriaBean> lookupCriteriaList) {
        this.lookupCriteriaList = lookupCriteriaList;
    }

    public Date getImgTimestamp() {
        return imgTimestamp;
    }

    public void setImgTimestamp(Date imgTimestamp) {
        this.imgTimestamp = imgTimestamp;
    }

    public String getIntTextAnswer() {
        return intTextAnswer;
    }

    public void setIntTextAnswer(String intTextAnswer) {
        this.intTextAnswer = intTextAnswer;
    }

    public LookupCriteriaBean getSelectedCriteriaBean() {
        return selectedCriteriaBean;
    }

    public void setSelectedCriteriaBean(LookupCriteriaBean selectedCriteriaBean) {
        this.selectedCriteriaBean = selectedCriteriaBean;
    }

    public void setIsCanChange(boolean isCanChange) {
        this.isCanChange = 1;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        if (isCanChange >= 1) {
            this.change = change;
        } else {
            isCanChange++;
        }
    }

    public List<LookupCriteriaBean> getLookupRecipientList() {
        return lookupRecipientList;
    }

    public void setLookupRecipientList(List<LookupCriteriaBean> lookupRecipientList) {
        this.lookupRecipientList = lookupRecipientList;
    }

    public List<LookupCriteriaBean> getOtherLookupCriteriaList() {
        return otherLookupCriteriaList;
    }

    public void setOtherLookupCriteriaList(List<LookupCriteriaBean> otherLookupCriteriaList) {
        this.otherLookupCriteriaList = otherLookupCriteriaList;
    }

    public LocationInfo getLocationInfo() {
        return this.locationInfo;
    }

    public void setLocationInfo(LocationInfo value) {
        this.locationInfo = value;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String value) {
        this.latitude = value;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String value) {
        this.longitude = value;
    }

    public List<OptionAnswerBean> getOptionAnswers() {
        return optionAnswers;
    }

    public void setOptionAnswers(List<OptionAnswerBean> optionAnswers) {

        this.optionAnswers = optionAnswers;
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    public String getLovId() {
        return lovId;
    }

    public void setLovId(String lovId) {
        this.lovId = lovId;
    }

    @Keep
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public byte[] getImgAnswer() {
        return imgAnswer;
    }

    public void setImgAnswer(byte[] imgAnswer) {
        this.imgAnswer = imgAnswer;
    }

    public byte[] getImgLocation() {
        return this.imgLocation;
    }

    public void setImgLocation(byte[] value) {
        this.imgLocation = value;
    }

    public String getFlatOptionAnswers() {
        return flatOptionAnswers;
    }

    public void setFlatOptionAnswers(String flatOptionAnswers) {
        this.flatOptionAnswers = flatOptionAnswers;
    }

    public String getFlatLovFilters() {
        return flatLovFilters;
    }

    public void setFlatLovFilters(String flatLovFilters) {
        this.flatLovFilters = flatLovFilters;
    }

    public String getFlatQuestionRelevants() {
        return flatQuestionRelevants;
    }

    public void setFlatQuestionRelevants(String flatQuestionRelevants) {
        this.flatQuestionRelevants = flatQuestionRelevants;
    }

    //special treatment for boolean
    public boolean isReadOnly() {
        return getIs_readonly().equals(Global.TRUE_STRING);
    }

    public void setReadOnly(boolean bool) {
        setIs_readonly(Formatter.booleanToString(bool));

    }

    public boolean isVisible() {
        return getIs_visible().equals(Global.TRUE_STRING);
    }

    public void setVisible(boolean bool) {
        setIs_visible(Formatter.booleanToString(bool));

    }

    public boolean isMandatory() {
        return getIs_mandatory().equals(Global.TRUE_STRING);
    }

    public void setMandatory(boolean bool) {
        setIs_mandatory(Formatter.booleanToString(bool));

    }

    public String getLookupId() {
        return lookupId;
    }

    public void setLookupId(String lookupId) {
        this.lookupId = lookupId;
    }

    public String[] getOptionRelevances() {
        return optionRelevances;
    }

    public void setOptionRelevances(String[] optionRelevances) {
        this.optionRelevances = optionRelevances;
    }

    public List<LookupAnswerBean> getLookupsAnswerBean() {
        return lookupsAnswerBean;
    }

    public void setLookupsAnswerBean(List<LookupAnswerBean> lookupsAnswerBean) {
        this.lookupsAnswerBean = lookupsAnswerBean;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isNoHoliday() {
        return isNoHoliday;
    }

    public void setNoHoliday(boolean isNoHoliday) {
        this.isNoHoliday = isNoHoliday;
    }

    public String getOptionRelevancesString() {
        return optionRelevancesString;
    }

    public void setOptionRelevancesString(String optionRelevancesString) {
        this.optionRelevancesString = optionRelevancesString;
    }

    @Keep
    public List<OptionAnswerBean> getSelectedOptionAnswers() {
        return selectedOptionAnswers;
    }

    public void setSelectedOptionAnswers(List<OptionAnswerBean> selectedOptionAnswers) {
        this.selectedOptionAnswers = selectedOptionAnswers;
    }

    public List<QuestionBean> getAffectedQuestionBeanVisibility() {
        return affectedQuestionBeanVisibility;
    }

    public void setAffectedQuestionBeanVisibility(
            List<QuestionBean> affectedQuestionBeanVisibility) {
        this.affectedQuestionBeanVisibility = affectedQuestionBeanVisibility;
    }

    public void addToAffectedQuestionBeanVisibility(QuestionBean bean) {
        if (affectedQuestionBeanVisibility.contains(bean)) return;
        affectedQuestionBeanVisibility.add(bean);
    }

    public List<QuestionBean> getAffectedQuestionBeanOptions() {
        return affectedQuestionBeanOptions;
    }

    public void setAffectedQuestionBeanOptions(
            List<QuestionBean> affectedQuestionBeanOptions) {
        this.affectedQuestionBeanOptions = affectedQuestionBeanOptions;
    }

    public void addToAffectedQuestionBeanOptions(QuestionBean bean) {
        if (affectedQuestionBeanOptions.contains(bean)) return;
        affectedQuestionBeanOptions.add(bean);
    }

    public List<QuestionBean> getAffectedQuestionBeanCalculation() {
        return affectedQuestionBeanCalculation;
    }

    public void setAffectedQuestionBeanCalculation(
            List<QuestionBean> affectedQuestionBeanCalculation) {
        this.affectedQuestionBeanCalculation = affectedQuestionBeanCalculation;
    }

    public void addToAffectedQuestionBeanCalculation(QuestionBean bean) {
        if (affectedQuestionBeanCalculation.contains(bean)) return;
        affectedQuestionBeanCalculation.add(bean);
    }

    public List<QuestionBean> getAffectedQuestionBeanCopyValue() {
        return affectedQuestionBeanCopyValue;
    }

    public void setAffectedQuestionBeanCopyValue(List<QuestionBean> affectedQuestionBeanCopyValue) {
        this.affectedQuestionBeanCopyValue = affectedQuestionBeanCopyValue;
    }

    public void addToAffectedQuestionBeanCopyValue(QuestionBean bean) {
        if (affectedQuestionBeanCopyValue.contains(bean)) return;
        affectedQuestionBeanCopyValue.add(bean);
    }

    public boolean isRelevanted() {
        return this.Relevanted;
    }

    public void setRelevanted(boolean value) {
        this.Relevanted = value;
    }

    public boolean isRelevantMandatory() {
        return this.relevant_mandatory;
    }

    public void setRelevantMandatory(boolean value) {
        this.relevant_mandatory = value;
        String relevant = getRelevant_mandatory();
        if (this.relevant_mandatory) {
            this.setIs_mandatory(Global.TRUE_STRING);
        } else if (!this.relevant_mandatory && !relevant.equalsIgnoreCase("")) {
            this.setIs_mandatory(Global.FALSE_STRING);
        }
    }

    public String getDataDukcapil() {
        return dataDukcapil;
    }

    public void setDataDukcapil(String dataDukcapil) {
        this.dataDukcapil = dataDukcapil;
    }

    public ResponseImageDkcp getResponseImageDkcp() {
        return responseImageDkcp;
    }

    public void setResponseImageDkcp(ResponseImageDkcp responseImageDkcp) {
        this.responseImageDkcp = responseImageDkcp;
    }

    public boolean isBtnCheckClicked() {
        return btnCheckClicked;
    }

    public void setBtnCheckClicked(boolean btnCheckClicked) {
        this.btnCheckClicked = btnCheckClicked;
    }

    public ArrayList<byte[]> getListImgByteArray() {
        return listImgByteArray;
    }

    public void setListImgByteArray(ArrayList<byte[]> listImgByteArray) {
        this.listImgByteArray = listImgByteArray;
    }

}
