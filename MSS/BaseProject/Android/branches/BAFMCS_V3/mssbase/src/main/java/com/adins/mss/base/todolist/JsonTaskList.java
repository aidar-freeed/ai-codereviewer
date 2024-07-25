package com.adins.mss.base.todolist;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonTaskList extends MssResponseType {
    @SerializedName("listTaskH")
    private List<TaskH> listTaskH;

    public List<TaskH> getListTaskH() {
        return listTaskH;
    }

    public void setListTaskH(List<TaskH> listTaskH) {
        this.listTaskH = listTaskH;
    }


}
