package com.adins.mss.base.todolist.form.todaysplan;

import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;

import java.util.List;

public interface TodayPlanHandler {
    void addToPlan(List<TaskH> planTasks);
    List<PlanTask> getCurrentPlans();
    int getAllPlansCount();
}
