package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Message;
import com.adins.mss.dao.MessageDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MessageDataAccess {

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
     * get message dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static MessageDao getMessageDao(Context context) {
        return getDaoSession(context).getMessageDao();
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
     * add message as entity
     *
     * @param context
     * @param message
     */
    public static void add(Context context, Message message) {
        getMessageDao(context).insert(message);
    }

    /**
     * add message as list entity
     *
     * @param context
     * @param messageList
     */
    public static void add(Context context, List<Message> messageList) {
        getMessageDao(context).insertInTx(messageList);
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getMessageDao(context).deleteAll();
    }

    /**
     * @param context
     * @param message
     */
    public static void delete(Context context, Message message) {
        getMessageDao(context).delete(message);
    }

    /**
     * delete all record by user
     *
     * @param context
     * @param uuidUser
     */
    public static void delete(Context context, String uuidUser) {
        QueryBuilder<Message> qb = getMessageDao(context).queryBuilder();
        qb.where(MessageDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        getMessageDao(context).deleteInTx(qb.list());
    }

    /**
     * @param context
     * @param message
     */
    public static void updateMessage(Context context, Message message) {
        getMessageDao(context).update(message);
    }

    /**
     * select * from table where uuid_user = param
     *
     * @param context
     * @param uuidUser
     * @return
     */
    public static List<Message> getAll(Context context, String uuidUser) {
        QueryBuilder<Message> qb = getMessageDao(context).queryBuilder();
        qb.where(MessageDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    /**
     * select message per
     *
     * @param context
     * @return
     */
}
