package com.adins.mss.base.log;

import android.app.Activity;
import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Log {
    public static final int SECOND = 1000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final int IMAGE_ONLY = 1;
    public static final int NON_IMAGE_ONLY = 2;
    public static final int ALL_TASK = 3;
    public static final String AT_LOV = "015";
    public static final String AT_LOV_W_FILTER = "016";
    private static final int MAXIMUM_SIZE_KEEP = 30;
    private static final int MAXIMUM_DAYS_KEEP = 1 * DAY;
    /* PRINT ITEM TYPE */
    public static String PRINT_NO_ANSWER = "001";
    public static String PRINT_ANSWER = "002";
    //Glen 9 Aug 2014, new type : timestamp
    public static String PRINT_TIMESTAMP = "004";
    // bong Oct 28th, 2014 - adding from fif
    public static String PRINT_LOGO = "005";
    public static String PRINT_USER = "006";
    public static String PRINT_LABEL_CENTER = "007";
    public static String PRINT_LABEL_CENTER_BOLD = "008";
    public static String PRINT_PRINTER_ID = "003";
    private static List<TaskH> listTaskH;
    private static List<TaskD> listTaskD;
    private Context context;
    private String userId;

    public Log() {
    }

    public Log(Context context) {
        this.context = context;
        userId = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        if(userId == null){
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(context,
                "GlobalData", Context.MODE_PRIVATE);
            userId = sharedPref.getString("UUID_USER", "");
            User user = UserDataAccess.getOne(context,userId);
            GlobalData.getSharedGlobalData().setUser(user);
        }
    }

    public List<TaskH> getAllSentTask() {
        listTaskH = TaskHDataAccess.getAllSentTask(context, userId);
        int listSize = listTaskH.size();
        List<TaskH> listTaskHDelete = new ArrayList<>();
        if (listSize > 0) {
            int flag = MAXIMUM_SIZE_KEEP;
            switch (flag) {
                case MAXIMUM_DAYS_KEEP:
                    Date sysdate = Tool.getSystemDateTime();
                    long batasDel = sysdate.getTime()
                            - MAXIMUM_DAYS_KEEP;
                    listTaskHDelete = TaskHDataAccess.getAllDeleteTask(context, userId, String.valueOf(batasDel));
                    TaskHDataAccess.deleteListWithRelation(context, listTaskHDelete);
                break;
                case MAXIMUM_SIZE_KEEP:
                    if (listSize > MAXIMUM_SIZE_KEEP) {
                        for (int i = MAXIMUM_SIZE_KEEP; i <= listSize; i++) {
                            listTaskHDelete.add(listTaskH.get(i));
                        }
                        TaskHDataAccess.deleteListWithRelation(context, listTaskHDelete);
                    }
                break;
                default:
                    break;
            }
        }
        return TaskHDataAccess.getAllSentTask(context, userId);
    }

    public List<TaskH> getAllSentTaskWithLimited() {
        listTaskH = TaskHDataAccess.getAllSentTask(context, userId);
        int listSize = listTaskH.size();
        List<TaskH> listTaskHDelete = new ArrayList<>();
        if (listSize > 0) {
            int MAXIMUM_DATA_KEEP = GlobalData.getSharedGlobalData().getMaxDataInLog();
            if (MAXIMUM_DATA_KEEP != 0 && listSize > MAXIMUM_DATA_KEEP) {
                for (int i = MAXIMUM_DATA_KEEP; i < listSize; i++) {
                    listTaskHDelete.add(listTaskH.get(i));
                }
                TaskHDataAccess
                        .deleteListWithRelation(context, listTaskHDelete);
            }
        }
        listTaskH = TaskHDataAccess.getAllSentTask(context, userId);
        return listTaskH;
    }

    /**
     * This is used to get all task detail from a taskId
     *
     * @param taskId
     * @param withImage - IMAGE_ONLY = 1, NON_IMAGE_ONLY = 2, ALL_TASK = 3
     * @return
     */
    public List<TaskD> getTaskD(String taskId, int withImage) {
        return TaskDDataAccess.getAllByTaskId(context, userId, taskId, withImage);
    }

    /**
     * Get question set of a taskId
     *
     * @param taskId
     * @return
     */
    public List<QuestionSet> getListQuestionSet(String taskId) {
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        return QuestionSetDataAccess.getAllByFormVersion(context, taskH.getUuid_scheme(), taskH.getForm_version());
    }

    /**
     * Close this activity
     *
     * @param activity
     */
    public void close(Activity activity) {
        activity.finish();
    }

    /**
     * Refresh list of sent task
     */
    public void doRefreshListSentTask() {
        listTaskH = getAllSentTask();
    }


    public List<PrintResult> getReadyPrintItem(Context context, String taskId) {
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        List<PrintItem> listPrintItem = PrintItemDataAccess.getAll(context, taskH.getScheme().getUuid_scheme());
        List<QuestionSet> listQuestionSet = QuestionSetDataAccess.getAll(context, taskH.getScheme().getUuid_scheme());
        List<TaskD> listTaskD = TaskDDataAccess.getAllByTaskId(context, userId, taskId, ALL_TASK);
        List<PrintResult> listPrintResult = null;

        List<QuestionBean> listQuestionBean = new ArrayList<>();
        for (QuestionSet qs : listQuestionSet)
            listQuestionBean.add((QuestionBean) qs);
        this.matchAnswerToQuestion(listQuestionBean, listTaskD);
        listPrintResult = checkNeedToPrint(listPrintItem, listQuestionBean, taskH);

        listQuestionBean = loadSelectedOptionForQuestionBean(listQuestionBean);
        if (listPrintResult == null || listQuestionBean == null) return Collections.emptyList();

        this.matchPrintItemWithAnswer(listPrintResult, listQuestionBean);
        return listPrintResult;
    }


    private List<PrintResult> checkNeedToPrint(List<PrintItem> listPrintItem,
                                               List<QuestionBean> listQuestionBean, TaskH taskH) {
        List<PrintResult> result = new ArrayList<>();

        for (PrintItem printItem : listPrintItem) {
            String qgid = printItem.getQuestion_group_id();
            String qid = printItem.getQuestion_id();
            boolean shouldSkipPrintItem = false;
            String value = "";

            for (QuestionBean qBean : listQuestionBean) {
                if (qBean.getQuestion_group_id() == qgid && qBean.getQuestion_id() == qid) {
                    boolean isVisible = true;
                    value = qBean.getAnswer();
                    String relevant = qBean.getRelevant_question();

                    if (relevant != null && relevant.length() > 0) {
                        isVisible = Tool.isVisibleByRelevant(relevant, qBean, listQuestionBean);
                    }
                    shouldSkipPrintItem = isVisible;
                    break;
                }
            }
            if (!shouldSkipPrintItem) {
                PrintResult pr = new PrintResult();
                pr.setValue(value);
                pr.setLabel(printItem.getPrint_item_label());
                pr.setUuid_task_h(taskH.getUuid_task_h());
                pr.setPrint_type_id(printItem.getPrint_type_id());
                result.add(pr);
            }
        }
        return result;
    }

    private void matchAnswerToQuestion(List<QuestionBean> listQuestionBean,
                                       List<TaskD> listTaskD) {
        if (listQuestionBean == null || listTaskD == null)
            return;
        // make all question unvisible first
        for (QuestionSet questionSet : listQuestionBean)
            questionSet.setIs_visible(Global.FALSE_STRING);

        for (TaskD taskD : listTaskD) {
            String qgid = taskD.getQuestion_group_id();
            String qid = taskD.getQuestion_id();
            String textAnswer = taskD.getText_answer();
            qLoop:
            for (QuestionBean qb : listQuestionBean) {
                if (qgid == qb.getQuestion_group_id() && qid == qb.getQuestion_id()) {
                    // make it visible
                    qb.setIs_visible(Global.TRUE_STRING);

                    String answerType = qb.getAnswer_type();
                    if (Tool.isOptions(answerType)) {
                        String optId;
                        if (qb.getTag() != null && qb.getTag().equalsIgnoreCase("JOB MH")) {
                            optId = taskD.getUuid_lookup();
                        } else {
                            optId = taskD.getOption_answer_id();
                        }
                        for (OptionAnswerBean optBean : qb.getOptionAnswers()) {
                            if (optId == optBean.getOption_id()) {
                                optBean.setSelected(true);
                                break qLoop;
                            }
                        }
                        qb.getSelectedOptionAnswers().add(new OptionAnswerBean("0", "", taskD.getLov()));
                        qb.setLovCode(taskD.getLov());
                    } else if (Tool.isImage(answerType)) {
                        byte[] imgAnswer = taskD.getImage();
                        qb.setImgAnswer(imgAnswer);
                        if(taskD.getImage_timestamp() != null)
                            qb.setImgTimestamp(taskD.getImage_timestamp());
                        break qLoop;
                    }
                    else {
                        qb.setAnswer(textAnswer);
                        break qLoop;
                    }
                }
            }
        }
    }

    public List<QuestionBean> loadSelectedOptionForQuestionBean(List<QuestionBean> listQuestionBean) {
        List<QuestionBean> loadedBeans = new ArrayList<>(listQuestionBean);

        for (QuestionBean qb : loadedBeans) {
            if (!Tool.isOptions(qb.getAnswer_type())) continue;
            List<OptionAnswerBean> optAnsBean = new ArrayList<>();
            OptionAnswerBean selectedOption = null;

            String lookUpId = qb.getLookupId();
            String flatLovCode = qb.getLovCode();
            if (flatLovCode == null) continue;
            String[] LovCodes = Tool.split(flatLovCode, Global.DELIMETER_DATA);
            for (int i = 0; i < LovCodes.length; i++) {
                String lovCode = LovCodes[i];
                if (lookUpId != null && lovCode != null) {
                    Lookup lookup = LookupDataAccess.getOneByCode(context, lookUpId, lovCode);
                    selectedOption = new OptionAnswerBean(lookup);
                    selectedOption.setSelected(true);
                    optAnsBean.add(selectedOption);
                }
            }
            qb.setSelectedOptionAnswers(optAnsBean);
        }
        return loadedBeans;
    }

    public void matchPrintItemWithAnswer(List<PrintResult> listPrintResult, List<QuestionBean> listQuestionBean) {
        if (listPrintResult == null || listQuestionBean == null) return;
        for (PrintResult printResult : listPrintResult) {
            if (PRINT_ANSWER.equals(printResult.getPrint_type_id())) {
                for (QuestionBean qb : listQuestionBean) {
                    if (qb.getQuestion_label() == printResult.getLabel()) {
                        printResult.setValue(qb.getAnswer());
                        break;
                    }
                }
            } else if (PRINT_TIMESTAMP.equals(printResult.getPrint_type_id())) {
                Date date = new Date();
                String dateString = Formatter.formatDate(date, "dd-MM-yyyy hh:mm");
                printResult.setValue(dateString);
            }
        }
    }
}
