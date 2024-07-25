package com.adins.mss.base.todo;

import android.app.Activity;
import android.content.Context;

import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import java.util.List;


public interface Task {

//    boolean saveTask(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions, String uuid_last_question, boolean send, boolean draft);

//    String saveTask(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions);

//    String[] doPreSubmitNewSurveyTask(Context context, String taskId, boolean isPartial) throws Exception;

    void saveAndSendTask(Activity activity, String taskId, boolean isPrintable, boolean finishActivity);

    //GGI
    void saveAndSendTask(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions);

    void sendTaskManual(Activity activity, SurveyHeaderBean header, boolean finishActivity);

    void saveAndSendTaskOnBackground(Context activity, String taskId, boolean isPrintable, boolean finishActivity);

    void saveAndSendTaskOnBackground(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions, boolean isTaskPaid);
}
