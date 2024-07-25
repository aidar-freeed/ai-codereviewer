package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.CollectionActivity;
import com.adins.mss.dao.CollectionActivityDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class CollectionActivityDataAccess {

    private CollectionActivityDataAccess() {
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
     * get collectionActivity dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static CollectionActivityDao getCollectionActivityDao(Context context) {
        return getDaoSession(context).getCollectionActivityDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add collectionActivity as entity
     *
     * @param context
     * @param collectionActivity
     */
    public static void add(Context context, CollectionActivity collectionActivity) {
        getCollectionActivityDao(context).insertInTx(collectionActivity);
        getDaoSession(context).clear();
    }

    /**
     * add collectionActivity as list entity
     *
     * @param context
     * @param collectionActivityList
     */
    public static void add(Context context, List<CollectionActivity> collectionActivityList) {
        getCollectionActivityDao(context).insertInTx(collectionActivityList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getCollectionActivityDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param collectionActivity
     */
    public static void delete(Context context, CollectionActivity collectionActivity) {
        getCollectionActivityDao(context).delete(collectionActivity);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by uuidTaskH
     *
     * @param context
     * @param uuidTaskH
     */
    public static void delete(Context context, String uuidTaskH) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty()) {
            getCollectionActivityDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    public static void deleteByAgreementNo(Context context, String agreement_no) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Agreement_no.eq(agreement_no));
        qb.build();
        if (!qb.list().isEmpty()) {
            getCollectionActivityDao(context).deleteInTx(qb.list());
        }
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param collectionActivity
     */
    public static void update(Context context, CollectionActivity collectionActivity) {
        getCollectionActivityDao(context).update(collectionActivity);
        getDaoSession(context).clear();
    }

    /**
     * get all data by agreementNo
     *
     * @param context
     * @param agreementNo
     * @return
     */
    public static List<CollectionActivity> getAll(Context context, String agreementNo) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Agreement_no.eq(agreementNo));
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list();
        else return Collections.emptyList();
    }

    public static List<CollectionActivity> getAllbyTask(Context context, String uuidTaskH) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Uuid_task_h.eq(uuidTaskH));
        qb.build();
        if (!qb.list().isEmpty())
            return qb.list();
        else return Collections.emptyList();
    }

    public static void addOrReplace(Context context, CollectionActivity collectionActivity) {
		getCollectionActivityDao(context).insertOrReplaceInTx(collectionActivity);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<CollectionActivity> collectionActivities) {
        getCollectionActivityDao(context).insertOrReplaceInTx(collectionActivities);
        getDaoSession(context).clear();
    }

    private static CollectionActivity getOne(Context context, String agreementNo) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Agreement_no.eq(agreementNo));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    private static CollectionActivity getOneCollAct(Context context, String uuid_CollAct) {
        QueryBuilder<CollectionActivity> qb = getCollectionActivityDao(context).queryBuilder();
        qb.where(CollectionActivityDao.Properties.Uuid_collection_activity.eq(uuid_CollAct));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }
}
