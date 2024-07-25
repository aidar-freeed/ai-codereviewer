package com.adins.mss.svy.fragments;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.todolist.form.TasklistListener;
import com.adins.mss.svy.models.SurveyorSearchRequest;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by olivia.dg on 1/30/2018.
 */

public interface SurveyActivityInterface {
    public void executeSearch(final TaskListener listener, SurveyorSearchRequest request, final String date, final int year, final int month, final String startDate,
                              final String endDate, final int position) throws ParseException, IOException;
    public void getBackgroundTask(TasklistListener listener, boolean isVerification, boolean isBranch);
    public void gotoDetailData(int taskType, String nomorOrder, String uuid_task_h, String formName);
    public void getResultTask(TaskListener listener, List<NameValuePair> param);
}
