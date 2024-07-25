package com.services.models;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssResponseType;

import java.util.List;

public class JsonResponseRetrieveTaskList extends MssResponseType {
    List<TaskH> listTaskList;

    public List<TaskH> getListTaskList() {
        return listTaskList;
    }

    public void setListTaskList(List<TaskH> listTaskList) {
        this.listTaskList = listTaskList;
    }


}
