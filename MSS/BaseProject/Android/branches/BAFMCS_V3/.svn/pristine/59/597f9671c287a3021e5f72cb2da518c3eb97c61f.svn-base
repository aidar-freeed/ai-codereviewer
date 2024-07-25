package com.adins.mss.base.todolist.form;

import android.content.Context;
import android.os.AsyncTask;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;

import java.util.List;

/**
 * Created by kusnendi.muhamad on 27/07/2017.
 */

public interface TasklistInterface {
    public ToDoList getTodoList();

    public String getParam();

    public Scheme getSelectedScheme();

    public void setSelectedScheme(Scheme scheme);

    public int getSelectedTask();

    public void setSelectedTask(int position);

    public int getPtp();

    public void setPtp(int ptp);

    public String getTenorFromValue();

    public void setTenorFromValue(String tenorFrom);

    public String getTenorToValue();

    public void setTenorToValue(String tenorTo);

    public String getOsFromValue();

    public void setOsFromValue(String osFrom);

    public String getOsToValue();

    public void setOsToValue(String osTo);

    public String getCustName();

    public void setCustName(String custName);

    public List<Scheme> listScheme();

    public List<TaskH> listTaskH();

    public List<TaskHSequence> getTaskHSequences();

    public List<TaskH> getSelectedTaskH(int position);

    public List<TaskH> getAllTaskH(Scheme selectedScheme);

    public List<TaskH> getTaskHInHighPriority(Scheme selectedScheme);

    public List<TaskH> getTaskHInLowPriority(Scheme selectedScheme);

    public List<TaskH> getTaskHInNormalPriority(Scheme selectedScheme);

    public List<TaskH> getTaskH(Scheme selectedScheme, int searchType);

    public String getTasklistFromServer(Context context);

    public List<TaskH> refreshBackgroundTask(boolean isGetFromServer, int pos, SwipeRefreshLayout swipeRefreshLayout);

    public AsyncTask<Void, Void, List<TaskH>> refreshBackgroundTask(boolean isGetFromServer);

    public void cancelRefreshTask();

    public void initiateRefresh(String type);

    public void initiateRefresh(boolean getDataFromServer);

    public TasklistImpl.PriorityHandler getPriorityHandler(boolean isPriorityOpen);
}
