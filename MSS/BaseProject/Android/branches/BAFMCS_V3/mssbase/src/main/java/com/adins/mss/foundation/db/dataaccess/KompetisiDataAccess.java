package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.Catalogue;
import com.adins.mss.dao.CatalogueDao;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Kompetisi;
import com.adins.mss.dao.KompetisiDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class KompetisiDataAccess {

    protected static DaoSession getDaoSession(Context context){
        return DaoOpenHelper.getDaoSession(context);
    }

    protected static KompetisiDao getKompetisiDao(Context context) {
        return getDaoSession(context).getKompetisiDao();
    }

    public static void add(Context context, Kompetisi kompetisi){
        getKompetisiDao(context).insert(kompetisi);
        getDaoSession(context).clear();
    }

    public static void add(Context context, List<Kompetisi> kompetisiList){
        getKompetisiDao(context).insertInTx(kompetisiList);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, Kompetisi kompetisi){
        getKompetisiDao(context).insertOrReplaceInTx(kompetisi);
        getDaoSession(context).clear();
    }

    public static void addOrReplace(Context context, List<Kompetisi> kompetisiList){
        getKompetisiDao(context).insertOrReplaceInTx(kompetisiList);
        getDaoSession(context).clear();
    }

    public static void clean(Context context){
        getKompetisiDao(context).deleteAll();
    }

    public static void delete(Context context, Kompetisi kompetisi){
        getKompetisiDao(context).delete(kompetisi);
        getDaoSession(context).clear();
    }

    public static void update(Context context, Kompetisi kompetisi){
        getKompetisiDao(context).update(kompetisi);
    }

    public static List<Kompetisi> getAll(Context context,String uuidUser){
        QueryBuilder<Kompetisi> qb = getKompetisiDao(context).queryBuilder();
        qb.where(KompetisiDao.Properties.Uuid_user.eq(uuidUser));
        qb.build();
        return qb.list();
    }

    public static Kompetisi getOne(Context context, String uuid){
        QueryBuilder<Kompetisi> qb = getKompetisiDao(context).queryBuilder();
        qb.where(KompetisiDao.Properties.Uuid_kompetisi.eq(uuid));
        qb.build();
        if(qb.list().size()==0)
            return null;
        return qb.list().get(0);
    }
}
