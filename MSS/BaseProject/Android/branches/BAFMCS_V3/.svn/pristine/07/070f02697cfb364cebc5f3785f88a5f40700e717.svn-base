package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Holiday;
import com.adins.mss.dao.HolidayDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class HolidayDataAccess {
    private HolidayDataAccess() {
    }

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
        return DaoOpenHelper.getDaoSession(context);
    }


    /**
     * get Holiday dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static HolidayDao getHolidayDao(Context context) {
        return getDaoSession(context).getHolidayDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add holiday as entity
     *
     * @param context
     * @param holiday
     */
    public static void add(Context context, Holiday holiday) {
        getHolidayDao(context).insert(holiday);
        getDaoSession(context).clear();
    }

    /**
     * add holiday as list entity
     *
     * @param context
     * @param listHoliday
     */
    public static void add(Context context, List<Holiday> listHoliday) {
        getHolidayDao(context).insertInTx(listHoliday);
        getDaoSession(context).clear();
    }


    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getHolidayDao(context).deleteAll();
        getDaoSession(context).clear();
    }


    /**
     * @param context
     * @param holiday
     */
    public static void delete(Context context, Holiday holiday) {
        getHolidayDao(context).deleteInTx(holiday);
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param holiday
     */
    public static void update(Context context, Holiday holiday) {
        getHolidayDao(context).update(holiday);
        getDaoSession(context).clear();
    }


    /**
     * add or replace data taskH
     *
     * @param context
     * @param holiday
     */
    public static void addOrReplace(Context context, Holiday holiday) {
        getHolidayDao(context).insertOrReplaceInTx(holiday);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Holiday> holiday) {
        getHolidayDao(context).insertOrReplaceInTx(holiday);
        getDaoSession(context).clear();
    }

    public static Holiday getOne(Context context, String uuid_holiday) {
        QueryBuilder<Holiday> qb = getHolidayDao(context).queryBuilder();
        qb.where(HolidayDao.Properties.Uuid_holiday.eq(uuid_holiday));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Holiday getOneByDate(Context context, Date date) {
        QueryBuilder<Holiday> qb = getHolidayDao(context).queryBuilder();
        qb.where(HolidayDao.Properties.H_date.eq(date));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Holiday getLastUpated(Context context) {
        QueryBuilder<Holiday> qb = getHolidayDao(context).queryBuilder();
        qb.orderDesc(HolidayDao.Properties.Dtm_upd);
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static List<Holiday> getAllHolidays(Context context) {
        QueryBuilder<Holiday> qb = getHolidayDao(context).queryBuilder();
        qb.where(HolidayDao.Properties.Flag_holiday.eq("1"));
        qb.build();
        if (qb.list().isEmpty())
            return Collections.emptyList();
        return qb.list();
    }


    public static Holiday getDay(Context context, Date date) {


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        QueryBuilder<Holiday> qb = getHolidayDao(context).queryBuilder();
        qb.where(HolidayDao.Properties.H_date.ge(cal.getTime()));
        qb.orderAsc(HolidayDao.Properties.H_date);
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

}
