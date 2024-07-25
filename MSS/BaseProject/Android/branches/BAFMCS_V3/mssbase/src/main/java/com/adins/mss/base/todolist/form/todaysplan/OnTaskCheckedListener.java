package com.adins.mss.base.todolist.form.todaysplan;

import com.adins.mss.dao.TaskH;

public interface OnTaskCheckedListener {
    void onTaskChecked(TaskH taskH, int position);
    void onTaskUnchecked(TaskH taskH,int position);
}
