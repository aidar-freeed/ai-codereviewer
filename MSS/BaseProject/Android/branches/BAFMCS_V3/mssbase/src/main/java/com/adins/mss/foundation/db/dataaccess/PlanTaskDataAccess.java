package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.PlanTaskDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class PlanTaskDataAccess {

    public static final String STATUS_DRAFTED = "Drafted";
    public static final String STATUS_PLANNED = "Planned";
    public static final String STATUS_STARTED = "Started";
    public static final String STATUS_FINISH = "Finish";

    private static PlanTaskDao getPlanTaskDao(Context context){
        return DaoOpenHelper.getDaoSession(context).getPlanTaskDao();
    }

    public static void addPlan(Context context,PlanTask planTask){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_task_h.eq(planTask));
        int samePlanTask = qb.list().size();
        if(samePlanTask == 0){
            getPlanTaskDao(context).insert(planTask);
        }
    }

    public static void updatePlan(Context context,PlanTask planTask){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_plan_task.eq(planTask.getUuid_plan_task()));
        int samePlanTask = qb.list().size();
        if(samePlanTask > 0){
            getPlanTaskDao(context).update(planTask);
        }
    }

    public static void addUpdatePlans(Context context, List<PlanTask> planTasks){
        getPlanTaskDao(context).insertOrReplaceInTx(planTasks);
    }

    public static void removePlan(Context context,PlanTask planTask){
        getPlanTaskDao(context).delete(planTask);
    }

    public static void removeAllPlans(Context context,String uuidUser){
        QueryBuilder<PlanTask> deleteQuery = getPlanTaskDao(context).queryBuilder();
        deleteQuery.where(PlanTaskDao.Properties.Uuid_user.eq(uuidUser)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static int totalAllPlanFromStart(Context context,String uuidUser){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_user.eq(uuidUser));
        return (int)qb.count();
    }

    public static int getPlanLastSequenceNo(Context context,String uuidUser){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_user.eq(uuidUser)).orderDesc(PlanTaskDao.Properties.Sequence);
        List<PlanTask> plans = qb.list();
        if(plans.size() == 0){
            return 0;
        }

        return plans.get(0).getSequence();
    }

    public static List<PlanTask> getAllPlan(Context context,String uuidUser){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_user.eq(uuidUser)
                ,PlanTaskDao.Properties.Plan_status.notIn(PlanTaskDataAccess.STATUS_FINISH));
        qb.orderAsc(PlanTaskDao.Properties.View_sequence);

        //filter only plan that started today
        List<PlanTask> todayPlans = new ArrayList<>();
        List<PlanTask> allPlans = qb.list();

        //current date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);
        int diff;
        int yesterdayPlans = 0;

        for(PlanTask planTask:allPlans){
            if(planTask.getPlan_start_date() == null){
                todayPlans.add(planTask);
                continue;
            }

            calendar.setTime(planTask.getPlan_start_date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            diff = currDay - calendar.get(Calendar.DAY_OF_MONTH);
            if(diff > 0){
                yesterdayPlans += 1;
                removePlan(context,planTask);
            }
            else {
                todayPlans.add(planTask);
            }
        }
        if(yesterdayPlans > 0){
            //reset shared pref plan task
            TodayPlanRepository todayPlanRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
            if(todayPlanRepository != null){
                todayPlanRepository.setNeedSync(false);
                todayPlanRepository.setLastOffChangePlanInfo(null);
            }
        }

        return todayPlans;
    }

    public static List<PlanTask> findPlanByTaskH(Context context,String uuidTaskh){
        QueryBuilder qb = getPlanTaskDao(context).queryBuilder();
        qb.where(PlanTaskDao.Properties.Uuid_task_h.eq(uuidTaskh));
        qb.limit(1);
        return qb.list();
    }

}
