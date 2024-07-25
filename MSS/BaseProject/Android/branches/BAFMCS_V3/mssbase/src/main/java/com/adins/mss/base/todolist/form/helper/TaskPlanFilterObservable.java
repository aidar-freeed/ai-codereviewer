package com.adins.mss.base.todolist.form.helper;

import com.adins.mss.dao.Scheme;

public interface TaskPlanFilterObservable<T>{
    void subscribeEvent(TaskPlanFilterObserver<T> observer);
    void unsubscribeEvent(TaskPlanFilterObserver<T> observer);
    void emit(T filterData);
    void setSearchFilterText(int schemePos,int priorityPos);
}


