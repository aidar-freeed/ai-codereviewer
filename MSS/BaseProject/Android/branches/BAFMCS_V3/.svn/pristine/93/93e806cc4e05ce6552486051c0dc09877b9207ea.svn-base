package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.dao.TimelineTypeDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class TimelineTypeDataAccess {

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
     * get timelineType dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static TimelineTypeDao getTimelineTypeDao(Context context) {
        return getDaoSession(context).getTimelineTypeDao();
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
     * add timelineType as entity
     *
     * @param context
     * @param timelineType
     */
    public static void add(Context context, TimelineType timelineType) {
        TimelineTypeDao timelineTypeDao = getTimelineTypeDao(context);
        QueryBuilder<TimelineType> qb = timelineTypeDao.queryBuilder();
        qb.where(TimelineTypeDao.Properties.Timeline_type.eq(timelineType.getTimeline_type()));
        if(qb.list().size() == 0)
            timelineTypeDao.insert(timelineType);
    }

    /**
     * add timelineType as list entity
     *
     * @param context
     * @param timelineTypeList
     */
    public static void add(Context context, List<TimelineType> timelineTypeList) {
        getTimelineTypeDao(context).insertInTx(timelineTypeList);
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getTimelineTypeDao(context).deleteAll();
    }

    /**
     * @param context
     * @param timelineType
     */
    public static void delete(Context context, TimelineType timelineType) {
        getTimelineTypeDao(context).delete(timelineType);
    }

    /**
     * delete all record by keyTimelineType
     *
     * @param context
     */
    public static void delete(Context context, String keyTimelineType) {
        QueryBuilder<TimelineType> qb = getTimelineTypeDao(context).queryBuilder();
        qb.where(TimelineTypeDao.Properties.Uuid_timeline_type.eq(keyTimelineType));
        qb.build();
        getTimelineTypeDao(context).deleteInTx(qb.list());
    }

    /**
     * @param context
     * @param timelineType
     */
    public static void update(Context context, TimelineType timelineType) {
        getTimelineTypeDao(context).update(timelineType);
    }

    /**
     * select * from table where uuid_timeline_type = param
     *
     * @param context
     * @param uuid_timeline_type
     * @return
     */
    public static List<TimelineType> getAll(Context context, String uuid_timeline_type) {
        QueryBuilder<TimelineType> qb = getTimelineTypeDao(context).queryBuilder();
        qb.where(TimelineTypeDao.Properties.Uuid_timeline_type.eq(uuid_timeline_type));
        qb.build();
        return qb.list();
    }

    public static List<TimelineType> getAll(Context context) {
        QueryBuilder<TimelineType> qb = getTimelineTypeDao(context).queryBuilder();
        return qb.list();
    }

    public static TimelineType getTimelineTypebyType(Context context, String TimelineType) {
        QueryBuilder<TimelineType> qb = getTimelineTypeDao(context).queryBuilder();
        qb.where(TimelineTypeDao.Properties.Timeline_type.eq(TimelineType));
        qb.build();
        return qb.list().get(0);
    }
}
