package com.adins.mss.base.todolist.form;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResponseTaskList extends MssResponseType {
    @SerializedName("listTaskList")
    List<TaskH> listTaskList;

    public List<TaskH> getListTaskList() {
        return listTaskList;
    }

    public void setListTaskList(List<TaskH> listTaskList) {
        this.listTaskList = listTaskList;
    }

}
