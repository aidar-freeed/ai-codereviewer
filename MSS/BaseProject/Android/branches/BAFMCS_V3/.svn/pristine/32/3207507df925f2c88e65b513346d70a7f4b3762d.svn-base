package com.adins.mss.base.todolist.todayplanrepository;

import android.content.Context;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TodayPlanRepository implements Comparator<PlanTask> {

    private List<PlanTask> currentPlans;
    private IPlanTaskDataSource dataSource;
    private List<PlanTaskRepoListener> listeners;
    private Context context;
    private int lastPlanSequenceNo;
    private boolean isStartVisit;
    private boolean needSync;//used for check if current plans has changed from last start visit
    private String[] lastOffChangePlanInfo;
    private boolean hasDeletedPlanTask;

    @Override
    public int compare(PlanTask o1, PlanTask o2) {
        return o1.getSequence() - o2.getSequence();
    }

    public interface PlanTaskRepoListener{
        void onRepoChange(List<PlanTask> plans);
        void onError(String errMsg);
    }

    public TodayPlanRepository(Context context) {
        this.context = context;
        this.dataSource = new PlanTaskDataSource(context);
        isStartVisit = dataSource.getStartVisitOnlineStatus();
        needSync = dataSource.getNeedSyncStatus();
        lastOffChangePlanInfo = dataSource.getLastOfflineChangePlan();
    }

    public void addListener(PlanTaskRepoListener listener){
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        if(listeners.contains(listener)){
            return;//has added to list of listeners
        }
        listeners.add(listener);
    }

    public void removeListener(PlanTaskRepoListener listener){
        if(listeners == null){
            return;
        }
        listeners.remove(listener);
    }

    public int getLastPlanSequenceNo() {
        return lastPlanSequenceNo;
    }

    public void updateLastPlanSequenceNo(){
        lastPlanSequenceNo = dataSource.getLastSequenceNo();
    }

    public int getLastPlansCount() {
        return currentPlans.size();
    }

    public void updateLastPlansCount(){
        lastPlanSequenceNo = dataSource.getTotalPlanFromStart();
    }

    public boolean isStartVisit() {
        return isStartVisit;
    }

    public boolean isHasDeletedPlanTask() {
        return hasDeletedPlanTask;
    }

    public void setHasDeletedPlanTask(boolean hasDeletedPlanTask) {
        this.hasDeletedPlanTask = hasDeletedPlanTask;
    }

    public void setStartVisit(boolean startVisit) {
        isStartVisit = startVisit;
        dataSource.saveStartVisitOnlineStatus(isStartVisit);
    }

    public boolean isNeedSync() {
        return needSync;
    }

    public void setNeedSync(boolean needSync) {
        this.needSync = needSync;
        dataSource.saveNeedSyncStatus(needSync);
    }

    public String[] getLastOffChangePlanInfo() {
        return lastOffChangePlanInfo;
    }

    public void setLastOffChangePlanInfo(String[] lastOffChangePlanInfo) {
        this.lastOffChangePlanInfo = lastOffChangePlanInfo;
        if(lastOffChangePlanInfo == null)
            dataSource.saveLastChangePlanOffline(null,null);
        else{
            dataSource.saveLastChangePlanOffline(lastOffChangePlanInfo[0],lastOffChangePlanInfo[1]);
        }
    }

    private void applyChange(List<PlanTask> planTasks){
        if(listeners != null && !listeners.isEmpty()){
            for (PlanTaskRepoListener listener:listeners){
                if(listener == null)
                    continue;
                listener.onRepoChange(planTasks);
            }
        }
    }

    private void applyChange(String message){
        if(listeners != null && !listeners.isEmpty()){
            for (PlanTaskRepoListener listener:listeners){
                if(listener == null)
                    continue;
                listener.onError(message);
            }
        }
    }

    private void decideStartedPlan(){
        for (PlanTask planTask:currentPlans) {
            if(planTask.getPlan_status().equals(PlanTaskDataAccess.STATUS_STARTED)){
                Global.setPlanStarted(true);
                Global.setCurrentPlanTask(planTask.getUuid_task_h());
                break;
            }
        }
    }

    public void loadPlans(){
        dataSource.loadPlans(new IPlanTaskDataSource.Result<List<PlanTask>>() {
            @Override
            public void onResult(List<PlanTask> result) {
                if(result == null)
                    return;

                lastPlanSequenceNo = dataSource.getLastSequenceNo();
                currentPlans = result;
                decideStartedPlan();
                applyChange(result);
            }

            @Override
            public void onError(String error) {
                applyChange(error);
            }
        });
    }

    public void updatePlan(final List<PlanTask> planTasks){
        dataSource.updatePlanStatus(planTasks, new IPlanTaskDataSource.Result<List<PlanTask>>() {
            @Override
            public void onResult(List<PlanTask> result) {
                //if plan status is finish -> decide next plan
                if(result != null){
                    currentPlans = result;//update cached result
                    for (PlanTask planTask:currentPlans) {
                        if(planTask.getPlan_status().equals(PlanTaskDataAccess.STATUS_STARTED)){
                            Global.setPlanStarted(true);
                            Global.setCurrentPlanTask(planTask.getUuid_task_h());
                            break;
                        }
                    }
                    applyChange(result);
                }
            }

            @Override
            public void onError(String error) {
                applyChange(error);
            }
        });
    }

    public void updatePlanByTaskH(TaskH taskH,String planStatus){
        if(taskH == null){
            return;
        }

        if(planStatus.equals(PlanTaskDataAccess.STATUS_FINISH)){
            List<PlanTask> updatedPlans = new ArrayList<>();

            //update previous finish plan status to finish
            PlanTask updatedPlan = getPlanTaskByTaskH(taskH.getTask_id());
            updatedPlan.setPlan_status(PlanTaskDataAccess.STATUS_FINISH);
            updatedPlans.add(updatedPlan);
            dataSource.updatePlanStatus(updatedPlans);//update plan

            if(!Global.getCurrentPlanTask().equals(updatedPlan.getUuid_task_h())){//jika plan aktif bukan plan yang berubah.
                //no need decide next plan, remove plan from view
                currentPlans.remove(updatedPlan);
                updatePlanViewSequence();
                return;
            }

            //if updated plan is active plan, decide next plan and remove current plan from view
            PlanTask nextPlan = decideNextPlan(updatedPlan);
            if(nextPlan != null){
                nextPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                Global.setCurrentPlanTask(nextPlan.getUuid_task_h());
                updatedPlans.add(nextPlan);
            }else {
                Global.setCurrentPlanTask(null);
            }

            //update plan view sequence
            updatePlanViewSequence();
        }
        else {
            PlanTask updatedPlan = getPlanTaskByTaskH(taskH.getTask_id());
            updatedPlan.setPlan_status(planStatus);
            List<PlanTask> updatedPlans = new ArrayList<>();
            updatedPlans.add(updatedPlan);
            updatePlan(updatedPlans);
        }
    }

    public void changeTaskhFromPlan(String oldUuidTaskH,String newUuidTaskH){
        PlanTask oldCurrentPlan = getPlanTaskByTaskH(oldUuidTaskH);
        //create new copied plan for new task id
        PlanTask newChangedIdPlan = new PlanTask(Tool.getUUID());
        newChangedIdPlan.setPlan_crt_date(oldCurrentPlan.getPlan_crt_date());
        newChangedIdPlan.setPlan_start_date(oldCurrentPlan.getPlan_start_date());
        newChangedIdPlan.setUuid_task_h(newUuidTaskH);
        newChangedIdPlan.setPlan_status(oldCurrentPlan.getPlan_status());
        newChangedIdPlan.setSequence(oldCurrentPlan.getSequence());
        newChangedIdPlan.setUuid_user(oldCurrentPlan.getUuid_user());
        newChangedIdPlan.setView_sequence(oldCurrentPlan.getView_sequence());

        //remove old plan and add new plan
        int planIdx = currentPlans.indexOf(oldCurrentPlan);
        currentPlans.remove(oldCurrentPlan);
        oldCurrentPlan.setPlan_status(PlanTaskDataAccess.STATUS_FINISH);//change old plan status to finish
        currentPlans.add(planIdx,newChangedIdPlan);

        if(Global.getCurrentPlanTask().equals(oldUuidTaskH)){
            Global.setCurrentPlanTask(newUuidTaskH);
        }

        //update plan data
        List<PlanTask> changedPlans = new ArrayList<>();
        changedPlans.add(newChangedIdPlan);
        changedPlans.add(oldCurrentPlan);
        PlanTaskDataAccess.addUpdatePlans(context,changedPlans);

        loadPlans();//refresh today plan view
    }

    public PlanTask decideNextPlan(PlanTask currentPlan){
        PlanTask nextPlan = null;

        currentPlans.remove(currentPlan);
        if(currentPlans.isEmpty())
            return null;
        List<PlanTask> sortedPlanTaskAcend = new ArrayList<>(currentPlans);
        //sort by lowest sequence
        Collections.sort(sortedPlanTaskAcend,this);
        nextPlan = sortedPlanTaskAcend.get(0);
        int nextPlanIdx = currentPlans.indexOf(nextPlan);
        currentPlans.remove(nextPlanIdx);
        currentPlans.add(0,nextPlan);

        return nextPlan;
    }

    public String nextPlanBeforeSubmit(String uuidCurrentPlan){
        if(uuidCurrentPlan == null || uuidCurrentPlan.equals(""))
            return null;

        if (null != Global.getCurrentPlanTask() && !Global.getCurrentPlanTask().equals(uuidCurrentPlan)) {
            return Global.getCurrentPlanTask();
        }

        PlanTask currentPlan = getPlanTaskByTaskH(uuidCurrentPlan);
        if(currentPlan == null)
            return null;

        if(currentPlans.isEmpty())
            return null;

        List<PlanTask> sortedPlanTaskAcend = new ArrayList<>(currentPlans);
        //exclude current plan
        sortedPlanTaskAcend.remove(currentPlan);
        if(sortedPlanTaskAcend.isEmpty()){
            return null;
        }

        //sort by lowest sequence
        Collections.sort(sortedPlanTaskAcend,this);
        PlanTask nextPlan = sortedPlanTaskAcend.get(0);

        return nextPlan.getUuid_task_h();
    }

    public PlanTask getPlanTaskByTaskH(String uuidTaskh){
        PlanTask result = null;
        if(currentPlans != null && !currentPlans.isEmpty()){
            for(PlanTask planTask:currentPlans){
                if(planTask.getUuid_task_h().equals(uuidTaskh)){
                    result = planTask;
                    break;
                }
            }
        }
        return result;
    }

    public void startVisit(List<PlanTask> planTasks, IPlanTaskDataSource.Result<ResponseStartVisit> callback){
        RequestStartVisit requestStartVisit = new RequestStartVisit();
        requestStartVisit.setIsStartVisit(this.isStartVisit);
        List<PlanTaskSequence> activityList = new ArrayList<>();
        for(PlanTask planTask:planTasks){
            if(planTask == null)
                continue;
            PlanTaskSequence activity = new PlanTaskSequence(planTask.getUuid_task_h()
                    ,String.valueOf(planTask.getSequence()));
            activityList.add(activity);
        }
        requestStartVisit.setActivityList(activityList);
        requestStartVisit.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        dataSource.startVisit(requestStartVisit,callback);
    }

    public boolean isAllowChangePlan(){
        List<PlanTask> result = PlanTaskDataAccess.findPlanByTaskH(context,Global.getCurrentPlanTask());
        if(result.isEmpty()){
            return false;
        }

        PlanTask currentPlan = result.get(0);
        if(currentPlan == null)
            return false;

        TaskH currentPlantaskH = TaskHDataAccess.getOneHeader(context,currentPlan.getUuid_task_h());
        if(currentPlantaskH == null && !hasDeletedPlanTask){//task is manualy deleted but plan still exist
            return false;
        }
        else if(currentPlantaskH == null){
            return true;
        }

        //only allow if current plan taskh status is not failed draft task
        //check if current plan is failed draft task
        if(currentPlantaskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
            List<Timeline> timelines = currentPlantaskH.getTimelineList();
            if(!timelines.isEmpty()){
                TimelineType timelineType = timelines.get(timelines.size() - 1).getTimelineType();
                if(timelineType != null){
                    TimelineType typeFailedDraft = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_FAILEDDRAFT);
                    if(typeFailedDraft.getTimeline_type().equals(timelineType.getTimeline_type())){
                        return false;
                    }
                }
            }
        }

        //check for pending submit
        if(currentPlantaskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_FAILED)){
            //check if task coll result is paid/collected
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            boolean isTaskPaid = TaskDDataAccess.isTaskPaid(context,uuidUser,currentPlantaskH.getUuid_task_h());
            if(isTaskPaid){
                return false;
            }
        }

        return true;
    }

    public void changePlan(final String oldPlanUuidTaskh, final String newPlanUuidTaskh, final IPlanTaskDataSource.Result<Boolean> callback){
        if(callback == null)
            return;

        RequestChangePlan requestChangePlan = new RequestChangePlan(newPlanUuidTaskh,oldPlanUuidTaskh);
        requestChangePlan.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        dataSource.changePlan(requestChangePlan, new IPlanTaskDataSource.Result<ResponseChangePlan>() {
            @Override
            public void onResult(ResponseChangePlan result) {
                if(result != null){
                    //reset last changeplan offline info
                   lastOffChangePlanInfo = null;
                   dataSource.saveLastChangePlanOffline(null,null);

                   List<PlanTask> oldPlanResult = PlanTaskDataAccess.findPlanByTaskH(context,oldPlanUuidTaskh);
                   PlanTask oldPlan = oldPlanResult.isEmpty()?null:oldPlanResult.get(0);
                   PlanTask newPlan = getPlanTaskByTaskH(newPlanUuidTaskh);
                   if(newPlan == null){//if null, then it is change plan from revisit
                       List<PlanTask> searchedPlans = PlanTaskDataAccess.findPlanByTaskH(context,newPlanUuidTaskh);
                       if(searchedPlans.isEmpty()){
                           return;
                       }
                       newPlan = searchedPlans.get(0);
                       newPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                       currentPlans.add(0,newPlan);//just add to top
                   }
                   else {
                       //move new plan to top
                       newPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                       currentPlans.remove(newPlan);
                       currentPlans.add(0,newPlan);
                   }

                   if(oldPlan!=null){
                       oldPlan.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);
                       PlanTaskDataAccess.updatePlan(context,oldPlan);
                       TaskH oldTaskh = TaskHDataAccess.getOneUnsentTaskHeader(context,oldPlan.getUuid_task_h());
                       if(hasDeletedPlanTask || (oldTaskh != null && oldTaskh.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT))){//move old plan to bottom if draft task
                           currentPlans.remove(oldPlan);
                           currentPlans.add(oldPlan);
                       }
                   }

                   updatePlanViewSequence();
                   Global.setCurrentPlanTask(newPlan.getUuid_task_h());
                   callback.onResult(true);
                }
            }

            @Override
            public void onError(String error) {
                if(GlobalData.isRequireRelogin() || "Plan Not Approved Yet.".equals(error)) {
                    callback.onError(error);
                    return;
                }
                if(error.equals("Offline")){
                    //save offline changeplan info
                    lastOffChangePlanInfo = new String[]{oldPlanUuidTaskh,newPlanUuidTaskh};
                    dataSource.saveLastChangePlanOffline(oldPlanUuidTaskh,newPlanUuidTaskh);

                    PlanTask oldPlan = getPlanTaskByTaskH(Global.getCurrentPlanTask());
                    PlanTask newPlan = getPlanTaskByTaskH(newPlanUuidTaskh);
                    if(newPlan == null){//if null, then it is change plan from revisit
                        List<PlanTask> searchedPlans = PlanTaskDataAccess.findPlanByTaskH(context,newPlanUuidTaskh);
                        if(searchedPlans.isEmpty()){
                            return;
                        }
                        newPlan = searchedPlans.get(0);
                        currentPlans.add(0,newPlan);//just add to top
                    }
                    else {
                        //move new plan to top
                        currentPlans.remove(newPlan);
                        currentPlans.add(0,newPlan);
                    }

                    if(oldPlan != null)
                        oldPlan.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);

                    if(newPlan != null)
                        newPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);

                    //move old plan to bottom if draft task
                    if(oldPlan!=null){
                        TaskH oldTaskh = TaskHDataAccess.getOneUnsentTaskHeader(context,oldPlan.getUuid_task_h());
                        if(oldTaskh.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                            currentPlans.remove(oldPlan);
                            currentPlans.add(oldPlan);
                        }
                    }

                    updatePlanViewSequence();

                    if(newPlan != null){
                        Global.setCurrentPlanTask(newPlan.getUuid_task_h());
                    }
                    callback.onResult(true);
                    return;
                }

                callback.onResult(false);
            }
        });
    }

    private void updatePlanViewSequence(){
        if(currentPlans == null || currentPlans.isEmpty()){
            return;
        }

        for(int i=0; i<currentPlans.size(); i++){
            PlanTask tempPlan = currentPlans.get(i);
            tempPlan.setView_sequence(i+1);//set new view sequence
        }
        updatePlan(currentPlans);
    }

    public List<PlanTask> getCachedPlans(){
        return currentPlans;
    }

    public void generatePlansFromTaskList(final List<TaskH> taskList){
        if(taskList == null || taskList.isEmpty())
            return;

        //check if has plan before
        dataSource.loadPlans(new IPlanTaskDataSource.Result<List<PlanTask>>() {
            @Override
            public void onResult(List<PlanTask> result) {
                //check each sequence of taskh
                List<PlanTask> generatedPlans = new ArrayList<>();
                for(TaskH taskH: taskList){
                    Integer seqNo = taskH.getSeq_no();
                    //if 0 then dont generate plan
                    if(seqNo == null || seqNo == 0){
                        continue;
                    }

                    //if not 0 then generate plan and set status as planned
                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    PlanTask planTask = new PlanTask();
                    planTask.setUuid_plan_task(Tool.getUUID());
                    planTask.setUuid_user(uuidUser);
                    planTask.setUuid_task_h(taskH.getUuid_task_h());
                    planTask.setPlan_crt_date(new Date());
                    planTask.setPlan_start_date(new Date());
                    planTask.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);
                    planTask.setSequence(seqNo);

                    //add to plan list
                    generatedPlans.add(planTask);
                }

                if (generatedPlans.isEmpty()) {
                    return;
                }

                //if has, return
                if (result != null && !result.isEmpty()) {
                    PlanTaskDataAccess.removeAllPlans(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                }

                //sort plan list ascending by seqNo
                Collections.sort(generatedPlans,TodayPlanRepository.this);

                for(int i=0; i<generatedPlans.size(); i++){
                    PlanTask sortedPlan = generatedPlans.get(i);
                    //set each plan view sequence
                    sortedPlan.setView_sequence(i+1);
                    if(i==0){
                        sortedPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);
                        Global.setCurrentPlanTask(sortedPlan.getUuid_task_h());
                    }
                }

                //add all plans to db
                currentPlans = generatedPlans;
                //set start visit to true
                setStartVisit(true);
                PlanTaskDataAccess.addUpdatePlans(context,generatedPlans);
                lastPlanSequenceNo = dataSource.getLastSequenceNo();
                Global.setPlanStarted(true);
            }

            @Override
            public void onError(String error) {
                //EMPTY
            }
        });
    }

    public void changePlanRevisit(String revisitUuidTaskh){
        PlanTask oldPlan = getPlanTaskByTaskH(Global.getCurrentPlanTask());
        PlanTask newPlan = getPlanTaskByTaskH(revisitUuidTaskh);
        if(newPlan == null){//if null, then it is change plan from revisit
            List<PlanTask> searchedPlans = PlanTaskDataAccess.findPlanByTaskH(context,revisitUuidTaskh);
            if(searchedPlans.isEmpty()){
                return;
            }
            newPlan = searchedPlans.get(0);
            currentPlans.add(0,newPlan);//just add to top
        }
        else {
            //move new plan to top
            currentPlans.remove(newPlan);
            currentPlans.add(0,newPlan);
        }

        if(oldPlan != null)
            oldPlan.setPlan_status(PlanTaskDataAccess.STATUS_PLANNED);

        if(newPlan != null)
            newPlan.setPlan_status(PlanTaskDataAccess.STATUS_STARTED);

        //move old plan to bottom if draft task
        if(oldPlan!=null){
            TaskH oldTaskh = TaskHDataAccess.getOneUnsentTaskHeader(context,oldPlan.getUuid_task_h());
            if(oldTaskh.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                currentPlans.remove(oldPlan);
                currentPlans.add(oldPlan);
            }
        }

        updatePlanViewSequence();

        if(newPlan != null){
            Global.setCurrentPlanTask(newPlan.getUuid_task_h());
        }
    }

    private PlanTask getPlanWithUnsentTaskh(){
        PlanTask result = null;
        TaskH taskH;
        for(PlanTask plan:currentPlans){
            if(plan == null){
                continue;
            }
            taskH = TaskHDataAccess.getOneUnsentTaskHeader(context,plan.getUuid_task_h());
            if(taskH != null){
                return plan;
            }
        }
        return result;
    }

    public void checkPlanAfterSync(){
        //getCurrent plans
        dataSource.loadPlans(new IPlanTaskDataSource.Result<List<PlanTask>>() {
            @Override
            public void onResult(List<PlanTask> result) {
                currentPlans = result;
                if(result.isEmpty()){
                    Global.setCurrentPlanTask(null);
                    return;
                }
                PlanTask startedPlan = null;
                for (PlanTask plan:currentPlans) {
                    if(plan != null && PlanTaskDataAccess.STATUS_STARTED.equals(plan.getPlan_status())){
                        startedPlan = plan;
                        break;
                    }
                }
                if(startedPlan == null){
                    Global.setCurrentPlanTask(null);
                    return;
                }

                TaskH planTaskh = TaskHDataAccess.getOneUnsentTaskHeader(context,startedPlan.getUuid_task_h());
                if(planTaskh != null){
                    Global.setPlanStarted(true);
                    Global.setCurrentPlanTask(startedPlan.getUuid_task_h());
                    return;
                }

                final PlanTask newPlan = getPlanWithUnsentTaskh();
                if(newPlan == null){
                    Global.setCurrentPlanTask(null);
                    return;
                }

                changePlan(startedPlan.getUuid_task_h(), newPlan.getUuid_task_h(), new IPlanTaskDataSource.Result<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        Global.setPlanStarted(true);
                        Global.setCurrentPlanTask(newPlan.getUuid_task_h());
                    }

                    @Override
                    public void onError(String error) {
                        if(GlobalData.isRequireRelogin()){
                            DialogManager.showForceExitAlert(context,context.getString(R.string.msgLogout));
                            return;
                        }
                        Toast.makeText(context, context.getString(R.string.error_change_plan), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
