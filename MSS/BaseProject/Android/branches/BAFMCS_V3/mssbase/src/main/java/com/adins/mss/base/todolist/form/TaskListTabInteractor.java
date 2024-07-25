package com.adins.mss.base.todolist.form;

import com.adins.mss.base.todolist.form.helper.TaskFilterParam;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObservable;

public interface TaskListTabInteractor {
    void goToTab(int index);
    TaskPlanFilterObservable<TaskFilterParam> getFilterObservable();

    public interface TabPage{
        String getTabPageName();
        void onEnterPage();
        void onLeavePage();
    }
}
