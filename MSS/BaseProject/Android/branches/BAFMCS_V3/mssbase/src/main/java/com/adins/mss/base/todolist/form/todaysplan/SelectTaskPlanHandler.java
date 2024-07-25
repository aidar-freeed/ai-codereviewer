package com.adins.mss.base.todolist.form.todaysplan;

import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;

import java.util.ArrayList;
import java.util.List;

public class SelectTaskPlanHandler {

    private List<TaskH> selectedTask = new ArrayList<>();
    private TodayPlanHandler todayPlanHandler;
    private OnSelectedChange changeListener;

    public interface OnSelectedChange{
        void onSelectedChange(List<TaskH> newSelected,int startSequence);
    }

    public SelectTaskPlanHandler(TodayPlanHandler todayPlanHandler) {
        this.todayPlanHandler = todayPlanHandler;
    }

    public void setChangeListener(OnSelectedChange changeListener) {
        this.changeListener = changeListener;
    }

    public List<TaskH> getSelectedTask() {
        return selectedTask;
    }

    public List<PlanTask> generatePlanTasks(){
        return null;
    }

    public void selectTask(TaskH taskH){
        if(todayPlanHandler == null)
            return;
        int currPlanSize = todayPlanHandler.getAllPlansCount();
        if(selectedTask.contains(taskH))
            return;

        selectedTask.add(taskH);
        if(changeListener != null)
            changeListener.onSelectedChange(selectedTask,currPlanSize);
    }

    public void selectAllTask(List<TaskH> selecteds){
        if(todayPlanHandler == null)
            return;
        int currPlanSize = todayPlanHandler.getAllPlansCount();

        selectedTask.clear();
        selectedTask.addAll(selecteds);
        if(changeListener != null)
            changeListener.onSelectedChange(selectedTask,currPlanSize);
    }

    public void deselectAllTask(){
        selectedTask.clear();
        if(changeListener != null)
            changeListener.onSelectedChange(selectedTask,0);
    }

    public void deselectTask(TaskH taskH){
        if(todayPlanHandler == null)
            return;
        int currPlanSize = todayPlanHandler.getAllPlansCount();
        if(!selectedTask.contains(taskH))
            return;

        selectedTask.remove(taskH);
        if(changeListener != null)
            changeListener.onSelectedChange(selectedTask,currPlanSize);
    }

    public void clearSelections(){
        if(todayPlanHandler == null)
            return;
        int currPlanSize = todayPlanHandler.getAllPlansCount();

        selectedTask.clear();
        if(changeListener != null){
            changeListener.onSelectedChange(selectedTask,currPlanSize);
        }
    }

}
