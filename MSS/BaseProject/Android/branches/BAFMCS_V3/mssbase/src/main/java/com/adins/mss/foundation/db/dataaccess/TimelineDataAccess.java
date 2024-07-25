package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.TimelineDao;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Tool;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class TimelineDataAccess {

    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_NORMAL = "Normal";

//	private static DaoOpenHelper daoOpenHelper;

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
        /*if(daoOpenHelper==null){
//			if(daoOpenHelper.getDaoSession()==null)
				daoOpenHelper = new DaoOpenHelper(context);
		}
		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
		return daoSeesion;*/
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get timeline dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static TimelineDao getTimelineDao(Context context) {
        return getDaoSession(context).getTimelineDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
		/*if(daoOpenHelper!=null){
			daoOpenHelper.closeAll();
			daoOpenHelper = null;
		}*/
        DaoOpenHelper.closeAll();
    }

    /**
     * add timeline as entity
     *
     * @param context
     * @param timeline
     */
    public static void add(Context context, Timeline timeline) {
        getTimelineDao(context).insertInTx(timeline);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Timeline timeline) {
        getTimelineDao(context).insertOrReplaceInTx(timeline);
        getDaoSession(context).clear();
    }

    /**
     * add timeline as list entity
     *
     * @param context
     * @param timelineList
     */
    public static void add(Context context, List<Timeline> timelineList) {
        getTimelineDao(context).insertInTx(timelineList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getTimelineDao(context).deleteAll();
    }

    /**
     * @param context
     * @param timeline
     */
    public static void delete(Context context, Timeline timeline) {
        getTimelineDao(context).delete(timeline);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getTimelineDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param timeline
     */
    public static void update(Context context, Timeline timeline) {
        getTimelineDao(context).update(timeline);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<Timeline> getAll(Context context, String uuidUser) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_timeline_type = param3 and uuid_user = param2
     *
     * @param context
     * @param uuidUser
     * @param keyTimelineType
     * @return
     */
    public static List<Timeline> getAll(Context context, String uuidUser, String keyTimelineType) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_timeline_type.eq(keyTimelineType));
        qb.build();
        return qb.list();
    }


    /**
     * select timeline by uuidTimeline
     *
     * @param context
     * @param uuidUser
     * @param uuidTimeline
     * @return
     */
    public static Timeline getOneTimeline(Context context, String uuidUser, String uuidTimeline) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_timeline.eq(uuidTimeline));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }

    public static Timeline getOneTimelineByTask(Context context, String uuidUser, String timelineType, String uuidTaskH) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.where(new WhereCondition.StringCondition("UUID_TIMELINE_TYPE in (SELECT UUID_TIMELINE_TYPE FROM MS_TIMELINETYPE WHERE " +
                "TIMELINE_TYPE = '" + timelineType + "')"));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }


    public static Timeline getOneTimelineByTaskH(Context context, String uuidUser, String uuid_taskh_h, String uuid_timeline_type) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_task_h.eq(uuid_taskh_h),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(qb.list().size()-1);
    }

    public static List<Timeline> getTimelineByTaskH(Context context, String uuidUser, String uuid_taskh_h, String uuid_timeline_type) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_task_h.eq(uuid_taskh_h),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.build();
        return qb.list();
    }

    public static Timeline getOneTimelineByTaskH(Context context, String uuidUser, String uuid_taskh_h) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_task_h.eq(uuid_taskh_h));
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list().get(0);
    }

    public static List<Timeline> getAllWithLimitedDay(Context context,
                                                      String uuid_user, int range) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.ge(Tool.getIncrementDate(range)));

        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllDeletedTimeline(Context context,
                                                       String uuid_user, int range) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.le(Tool.getIncrementDate(range)));

        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getTimelineByTask(Context context, String uuidUser, String uuidTaskH) {
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        if (qb.list().size() == 0)
            return null;
        return qb.list();
    }

    public static List<Timeline> getAllDeletedTimelineForMH(Context context, String uuid_user, int range) {
        TimelineType verified = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_VERIFIED);
        TimelineType approved = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_APPROVED);
        TimelineType rejected = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_REJECTED);
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.le(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.in(verified.getUuid_timeline_type(),
                        approved.getUuid_timeline_type(), rejected.getUuid_timeline_type()));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllWithLimitedDayForMH(Context context, String uuid_user, int range) {
        TimelineType verified = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_VERIFIED);
        TimelineType approved = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_APPROVED);
        TimelineType rejected = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_REJECTED);
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.ge(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.in(verified.getUuid_timeline_type(),
                        approved.getUuid_timeline_type(), rejected.getUuid_timeline_type()));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllTimelineForMH(Context context, String uuidUser) {
        TimelineType verified = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_VERIFIED);
        TimelineType approved = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_APPROVED);
        TimelineType rejected = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_REJECTED);
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_timeline_type.in(verified.getUuid_timeline_type(),
                        approved.getUuid_timeline_type(), rejected.getUuid_timeline_type()));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllDeletedTimelineByType(Context context, String uuid_user, int range, String timelineType) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.le(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllWithLimitedDayByType(Context context, String uuid_user, int range, String timelineType) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.ge(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllTimelineByType(Context context, String uuidUser, String timelineType) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllDeletedTimelineTask(Context context, String uuid_user, int range, String timelineType, String priority) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.le(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type),
                TimelineDao.Properties.Priority.eq(priority));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllWithLimitedDayTask(Context context, String uuid_user, int range, String timelineType, String priority) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuid_user),
                TimelineDao.Properties.Dtm_crt.ge(Tool.getIncrementDate(range)),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type),
                TimelineDao.Properties.Priority.eq(priority));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }

    public static List<Timeline> getAllTimelineTask(Context context, String uuidUser, String timelineType, String priority) {
        TimelineType type = TimelineTypeDataAccess.getTimelineTypebyType(context, timelineType);
        String uuid_timeline_type = type.getUuid_timeline_type();
        QueryBuilder<Timeline> qb = getTimelineDao(context).queryBuilder();
        qb.where(TimelineDao.Properties.Uuid_user.eq(uuidUser),
                TimelineDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type),
                TimelineDao.Properties.Priority.eq(priority));
        qb.orderAsc(TimelineDao.Properties.Dtm_crt);
        qb.build();
        return qb.list();
    }
}
