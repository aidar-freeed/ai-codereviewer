package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.ImageResult;
import com.adins.mss.dao.ImageResultDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ImageResultDataAccess {

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
     * get imageResult dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static ImageResultDao getImageResultDao(Context context) {
        return getDaoSession(context).getImageResultDao();
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
     * add imageResult as entity
     *
     * @param context
     * @param imageResult
     */
    public static void add(Context context, ImageResult imageResult) {
        getImageResultDao(context).insert(imageResult);
    }

    /**
     * add imageResult as list entity
     *
     * @param context
     * @param imageResultList
     */
    public static void add(Context context, List<ImageResult> imageResultList) {
        getImageResultDao(context).insertInTx(imageResultList);
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getImageResultDao(context).deleteAll();
    }

    /**
     * @param context
     * @param imageResult
     */
    public static void delete(Context context, ImageResult imageResult) {
        getImageResultDao(context).delete(imageResult);
    }

    /**
     * delete all record by keyTaskH
     *
     * @param context
     * @param keyTaskH
     */
    public static void delete(Context context, String keyTaskH) {
        QueryBuilder<ImageResult> qb = getImageResultDao(context).queryBuilder();
        qb.where(ImageResultDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build();
        getImageResultDao(context).deleteInTx(qb.list());
    }

    /**
     * @param context
     * @param imageResult
     */
    public static void update(Context context, ImageResult imageResult) {
        getImageResultDao(context).update(imageResult);
    }

    /**
     * select * from table where uuid_task_h = param
     *
     * @param context
     * @param keyTaskH
     * @return
     */
    public static List<ImageResult> getAll(Context context, String keyTaskH) {
        QueryBuilder<ImageResult> qb = getImageResultDao(context).queryBuilder();
        qb.where(ImageResultDao.Properties.Uuid_task_h.eq(keyTaskH));
        qb.build();
        return qb.list();
    }

    /**
     * select imageResult per
     *
     * @param context
     * @return
     */

}
