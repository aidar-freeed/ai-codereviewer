package com.adins.mss.base.tasklog;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.dao.TaskH;

import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public interface TaskLogInterface {
    public List<TaskH> getListTaskLog();

    public void callOnlineLog(TaskListener listener);

    public void cancelOnlineLog();
}
