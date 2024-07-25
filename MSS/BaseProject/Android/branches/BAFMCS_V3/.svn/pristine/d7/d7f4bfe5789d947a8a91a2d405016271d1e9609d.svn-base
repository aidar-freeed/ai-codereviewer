package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.dao.MobileContentHDao;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Tool;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MobileContentHDataAccess {
    private MobileContentHDataAccess() {
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
     * get mobileContentH dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static MobileContentHDao getMobileContentHDao(Context context) {
        return getDaoSession(context).getMobileContentHDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    public static void addOrReplace(Context context, MobileContentH mobileContentH) {
        getMobileContentHDao(context).insertOrReplaceInTx(mobileContentH);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<MobileContentH> mobileContentHList) {
        getMobileContentHDao(context).insertOrReplaceInTx(mobileContentHList);
        getDaoSession(context).clear();
    }

    /**
     * add mobileContentH as entity
     *
     * @param context
     * @param mobileContentH
     */
    public static void add(Context context, MobileContentH mobileContentH) {
        getMobileContentHDao(context).insertInTx(mobileContentH);
        getDaoSession(context).clear();
    }

    /**
     * add mobileContentH as list entity
     *
     * @param context
     * @param mobileContentHList
     */
    public static void add(Context context, List<MobileContentH> mobileContentHList) {
        getMobileContentHDao(context).insertInTx(mobileContentHList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getMobileContentHDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param mobileContentH
     */
    public static void delete(Context context, MobileContentH mobileContentH) {
        getMobileContentHDao(context).deleteInTx(mobileContentH);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getMobileContentHDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param mobileContentH
     */
    public static void update(Context context, MobileContentH mobileContentH) {
        getMobileContentHDao(context).updateInTx(mobileContentH);
        getDaoSession(context).clear();
    }

    /**
     * get one mobileContentH
     *
     * @param context
     * @param keyMobileContentH
     * @return
     */
    public static MobileContentH getOne(Context context, String keyMobileContentH) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_mobile_content_h.eq(keyMobileContentH));
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list().get(0);
        else return null;
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<MobileContentH> getAll(Context context, String uuidUser) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_parent_content = param
     *
     * @param context
     * @param uuidUser
     * @param keyParentContent
     * @return
     */
    public static List<MobileContentH> getAll(Context context, String uuidUser, String keyParentContent) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser),
                MobileContentHDao.Properties.Uuid_parent_content.eq(keyParentContent)
                , MobileContentHDao.Properties.Start_date.lt(Tool.getSystemDateTime())
                , MobileContentHDao.Properties.End_date.gt(Tool.getSystemDateTime()));
        qb.build();
        return qb.list();
    }

    public static List<MobileContentH> getAllWithoutDate(Context context, String uuidUser, String keyParentContent) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser),
                MobileContentHDao.Properties.Uuid_parent_content.eq(keyParentContent));
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_parent_content = null
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<MobileContentH> getAllParent(Context context, String uuidUser) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser),
                MobileContentHDao.Properties.Uuid_parent_content.eq("0")
                , MobileContentHDao.Properties.Start_date.lt(Tool.getSystemDateTime())
                , MobileContentHDao.Properties.End_date.gt(Tool.getSystemDateTime())
        );
        qb.build();
        return qb.list();
    }

    public static long getParentCounter(Context context, String uuidUser) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser),
                MobileContentHDao.Properties.Uuid_parent_content.isNull());
        qb.build();
        return qb.count();
    }

    /**
     * @param context
     * @param uuidUser
     * @param uuid_mobile_content_h
     * @return
     */
    public static List<MobileContentH> getContent(Context context, String uuidUser, String uuid_mobile_content_h) {
        QueryBuilder<MobileContentH> qb = getMobileContentHDao(context).queryBuilder();
        qb.where(MobileContentHDao.Properties.Uuid_user.eq(uuidUser),
                MobileContentHDao.Properties.Uuid_mobile_content_h.eq(uuid_mobile_content_h));
        qb.build();
        return qb.list();
    }
}
