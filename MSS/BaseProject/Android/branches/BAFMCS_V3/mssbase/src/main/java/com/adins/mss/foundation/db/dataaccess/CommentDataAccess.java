package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Comment;
import com.adins.mss.dao.CommentDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class CommentDataAccess {

//	private static DaoOpenHelper daoOpenHelper;

    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context) {
//		if(daoOpenHelper==null){
////			if(daoOpenHelper.getDaoSession()==null)
//				daoOpenHelper = new DaoOpenHelper(context);
//		}
//		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
//		return daoSeesion;
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get comment dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static CommentDao getCommentDao(Context context) {
        return getDaoSession(context).getCommentDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
//		if(daoOpenHelper!=null){
//			daoOpenHelper.closeAll();
//			daoOpenHelper = null;
//		}
    }

    /**
     * add comment as entity
     *
     * @param context
     * @param comment
     */
    public static void add(Context context, Comment comment) {
        getCommentDao(context).insert(comment);
        getDaoSession(context).clear();
    }

    /**
     * add comment as list entity
     *
     * @param context
     * @param commentList
     */
    public static void add(Context context, List<Comment> commentList) {
        getCommentDao(context).insertInTx(commentList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getCommentDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param comment
     */
    public static void delete(Context context, Comment comment) {
        getCommentDao(context).delete(comment);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyTimeline
     *
     * @param context
     * @param keyTimeline
     */
    public static void delete(Context context, String keyTimeline) {
        QueryBuilder<Comment> qb = getCommentDao(context).queryBuilder();
        qb.where(CommentDao.Properties.Uuid_timeline.eq(keyTimeline));
        qb.build();
        getCommentDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param comment
     */
    public static void update(Context context, Comment comment) {
        getCommentDao(context).update(comment);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_timeline = param
     *
     * @param context
     * @param keyTimeline
     * @return
     */
    public static List<Comment> getAll(Context context, String keyTimeline) {
        QueryBuilder<Comment> qb = getCommentDao(context).queryBuilder();
        qb.where(CommentDao.Properties.Uuid_timeline.eq(keyTimeline));
        qb.build();
        return qb.list();
    }

    /**
     * select comment per
     *
     * @param context
     * @return
     */
}
