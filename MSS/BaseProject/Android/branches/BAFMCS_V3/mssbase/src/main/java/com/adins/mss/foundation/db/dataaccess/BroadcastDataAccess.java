package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Broadcast;
import com.adins.mss.dao.BroadcastDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class BroadcastDataAccess {
    /**
     * use to generate dao session that you can access modelDao
     *
     * @param context --> context from activity
     * @return
     */
    protected static DaoSession getDaoSession(Context context){
		/*if(daoOpenHelper==null){
//			if(daoOpenHelper.getDaoSession()==null)
				daoOpenHelper = new DaoOpenHelper(context);
		}
		DaoSession daoSeesion = daoOpenHelper.getDaoSession();
		return daoSeesion;*/
        return DaoOpenHelper.getDaoSession(context);
    }

    /**
     * get user dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static BroadcastDao getBroadcastDao(Context context){
        return getDaoSession(context).getBroadcastDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     *
     */
    public static void closeAll(){
		/*if(daoOpenHelper!=null){
			daoOpenHelper.closeAll();
			daoOpenHelper = null;
		}*/
        DaoOpenHelper.closeAll();
    }

    /**
     * add user as entity
     *
     * @param context
     * @param broadcast
     *
     *
     */
    public static void addOrUpdate(Context context, Broadcast broadcast){
        getBroadcastDao(context).insertOrReplaceInTx(broadcast);
        getDaoSession(context).clear();
    }

    public static Broadcast getById(Context context, String uuidBroadcast){
        QueryBuilder<Broadcast> qb = getBroadcastDao(context).queryBuilder();
        qb.where(BroadcastDao.Properties.Uuid_broadcast.eq(uuidBroadcast));
        qb.build().forCurrentThread();
        if(qb.list() != null){
            if(qb.list().size()==1) return qb.list().get(0);
            else return null;
        }
        else return null;
    }

    public static List<Broadcast> getAllNotShown(Context context){
        QueryBuilder<Broadcast> qb = getBroadcastDao(context).queryBuilder();
        qb.where(BroadcastDao.Properties.Is_shown.eq(false));
        qb.build().forCurrentThread();
        if(!qb.list().equals(null)){
            if(qb.list().size()>0) return qb.list();
            else return null;
        }
        else return null;
    }

    public static void deleteByUuid(Context context, String uuidBroadcast){
        QueryBuilder<Broadcast> qb = getBroadcastDao(context).queryBuilder();
        qb.where(BroadcastDao.Properties.Uuid_broadcast.eq(uuidBroadcast));
        qb.build().forCurrentThread();
        getBroadcastDao(context).deleteInTx(qb.list());
    }
}
