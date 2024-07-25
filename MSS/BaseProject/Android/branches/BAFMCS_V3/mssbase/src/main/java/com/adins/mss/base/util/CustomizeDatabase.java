package com.adins.mss.base.util;

import android.content.Context;

import com.adins.mss.dao.Sync;
import com.adins.mss.dao.SyncDao;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by gigin.ginanjar on 15/03/2016.
 */
public class CustomizeDatabase extends SyncDataAccess {
    public static List<Sync> getAll(Context context) {
        QueryBuilder<Sync> qb = getSyncDao(context).queryBuilder();
        //TODO: Ganti String nya dengan LOV_GROUP yang mau di embedd
        qb.where(SyncDao.Properties.Lov_group.notIn("KOTA", "KELURAHAN", "KECAMATAN", "ASSETMASTER", "KODE POS"));//("PROVINSI", "KOTA", "KELURAHAN", "KECAMATAN", "KODE POS", "ASSET MASTER"));
        qb.build();
        if (qb.list().size() == 0) {
            return null;
        }
        return qb.list();
    }
}
