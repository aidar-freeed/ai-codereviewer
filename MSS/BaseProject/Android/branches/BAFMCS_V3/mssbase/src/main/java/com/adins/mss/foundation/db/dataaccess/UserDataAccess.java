package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.User;
import com.adins.mss.dao.UserDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class UserDataAccess {

    private UserDataAccess() {
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
     * get user dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static UserDao getUserDao(Context context) {
        return getDaoSession(context).getUserDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add user as entity
     *
     * @param context
     * @param user
     */
    public static void add(Context context, User user) {
        getUserDao(context).insertInTx(user);
        getDaoSession(context).clear();
    }

    /**
     * add user as list entity
     *
     * @param context
     * @param userList
     */
    public static void  add(Context context, List<User> userList) {
        getUserDao(context).insertInTx(userList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, User user) {
        getUserDao(context).insertOrReplaceInTx(user);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<User> userList) {
        getUserDao(context).insertOrReplaceInTx(userList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getUserDao(context).deleteAll();
    }

    /**
     * @param context
     * @param user
     */
    public static void delete(Context context, User user) {
        getUserDao(context).delete(user);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();
        qb.where(UserDao.Properties.Uuid_user.eq(uuidUser));
        qb.build().forCurrentThread();
        getUserDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param user
     */
    public static void update(Context context, User user) {
        getUserDao(context).update(user);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<User> getAll(Context context, String uuidUser) {
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();
        qb.where(UserDao.Properties.Uuid_user.eq(uuidUser));
        qb.build().forCurrentThread();
        if (qb.list() == null) return new ArrayList<>();
        else return qb.list();
    }

    public static List<User> getAll(Context context) {
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();
        qb.build().forCurrentThread();
        if (qb.list() == null) return new ArrayList<>();
        else return qb.list();
    }

    public static List<User> getAllUserActive(Context context){
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();

//		if (GlobalData.getSharedGlobalData().getUser() != null && GlobalData.getSharedGlobalData().getUser()
//				.getUuid_user() != null) {
//			qb.whereOr(UserDao.Properties.Facebook_id.eq("1"), UserDao.Properties.Uuid_user.eq(GlobalData.getSharedGlobalData().getUser()
//					.getUuid_user()));
//		}else{
        qb.where(UserDao.Properties.Facebook_id.eq("1"));
//		}

        qb.build().forCurrentThread();
        List<User> userListName = qb.list();
        if(qb.list() == null) return null;
        else return qb.list();
    }

    public static User getOne(Context context, String uuidUser) {
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();
        qb.where(UserDao.Properties.Uuid_user.eq(uuidUser));
        qb.build().forCurrentThread();
        if (qb.list() != null) {
            if (!qb.list().isEmpty()) return qb.list().get(0);
            else return null;
        } else return null;
    }

    public static User getOne(Context context) {
        QueryBuilder<User> qb = getUserDao(context).queryBuilder();
        qb.build().forCurrentThread();
        if (qb.list() != null) {
            if (!qb.list().isEmpty()) return qb.list().get(0);
            else return null;
        } else return null;
    }
}
