package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.GroupUser;
import com.adins.mss.dao.GroupUserDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class GroupUserDataAccess {

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
     * get groupUser dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static GroupUserDao getGroupUserDao(Context context) {
        return getDaoSession(context).getGroupUserDao();
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
     * add groupUser as entity
     *
     * @param context
     * @param groupUser
     */
    public static void add(Context context, GroupUser groupUser) {
        getGroupUserDao(context).insert(groupUser);
    }

    /**
     * add groupUser as list entity
     *
     * @param context
     * @param groupUserList
     */
    public static void add(Context context, List<GroupUser> groupUserList) {
        getGroupUserDao(context).insertInTx(groupUserList);
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getGroupUserDao(context).deleteAll();
    }

    /**
     * @param groupUser
     * @param context
     */
    public static void delete(Context context, GroupUser groupUser) {
        getGroupUserDao(context).delete(groupUser);
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<GroupUser> qb = getGroupUserDao(context).queryBuilder();
        qb.where(GroupUserDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getGroupUserDao(context).deleteInTx(qb.list());
    }

    /**
     * @param groupUser
     * @param context
     */
    public static void update(Context context, GroupUser groupUser) {
        getGroupUserDao(context).update(groupUser);
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<GroupUser> getAll(Context context, String uuidUser) {
        QueryBuilder<GroupUser> qb = getGroupUserDao(context).queryBuilder();
        qb.where(GroupUserDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select groupUser per user
     *
     * @param context
     * @return
     */

}
