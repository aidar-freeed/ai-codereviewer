package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;
import android.database.Cursor;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.LookupDao;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LookupDataAccess {

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
     * get lookupDao dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static LookupDao getLookupDao(Context context) {
        return getDaoSession(context).getLookupDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add lookup as entity
     *
     * @param context
     * @param lookup
     */
    public static void add(Context context, Lookup lookup) {
        getLookupDao(context).insertInTx(lookup);
        getDaoSession(context).clear();
    }

    /**
     * add lookup as list entity
     *
     * @param context
     * @param lookupList
     */
    public static void add(Context context, List<Lookup> lookupList) {
        getLookupDao(context).insertInTx(lookupList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getLookupDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param lookup
     * @param context
     */
    public static void delete(Context context, Lookup lookup) {
        getLookupDao(context).delete(lookup);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyQuestionSet
     *
     * @param context
     */
    public static void delete(Context context, String keyQuestionSet) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Uuid_question_set.eq(keyQuestionSet));
        qb.build();
        getLookupDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    public static void deleteByLovGroup(Context context, String lovGroup) {
//        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
//        qb.where(LookupDao.Properties.Lov_group.eq(lovGroup));
        List<String> submittedRv = new ArrayList<>();
        List<TaskH> taskFailed = TaskHDataAccess.getAllTaskInFailedSubmitted(context);
        if (null != taskFailed && taskFailed.size() > 0) {
            for (TaskH taskH : taskFailed) {
                submittedRv.add(taskH.getRv_number());
            }
        }

        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lovGroup),
        LookupDao.Properties.Code.notIn(submittedRv));
        qb.build();
        getLookupDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param lookup
     * @param context
     */
    public static void update(Context context, Lookup lookup) {
        getLookupDao(context).updateInTx(lookup);
        getDaoSession(context).clear();
    }

    public static void addOrUpdateAll(final Context context, final List<Lookup> lookup) {
        getLookupDao(context).insertOrReplaceInTx(lookup);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_question_set = param
     *
     * @param context
     * @param keyQuestionSet
     * @return
     */
    public static List<Lookup> getAll(Context context, String keyQuestionSet) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Uuid_question_set.eq(keyQuestionSet));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByLovGroup(Context context, String lov_group) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByLovGroupTextWithSuggestion(Context context, String lov_group, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like("%"+dynamicFilter + "%"));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilter(Context context, String lov_group, String filter1) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Filter1.like(filter1));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilter(Context context, String lov_group, String filter1, String filter2) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Filter1.like(filter1),
                LookupDao.Properties.Filter2.like(filter2));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilter(Context context, String lov_group, String filter1, String filter2, String filter3) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Filter1.like(filter1),
                LookupDao.Properties.Filter2.like(filter2),
                LookupDao.Properties.Filter3.like(filter3));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilter(Context context, String lov_group, String filter1, String filter2, String filter3, String filter4) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Filter1.like(filter1),
                LookupDao.Properties.Filter2.like(filter2),
                LookupDao.Properties.Filter3.like(filter3),
                LookupDao.Properties.Filter4.like(filter4));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilter(Context context, String lov_group, String filter1, String filter2, String filter3, String filter4, String filter5) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Filter1.like(filter1),
                LookupDao.Properties.Filter2.like(filter2),
                LookupDao.Properties.Filter3.like(filter3),
                LookupDao.Properties.Filter4.like(filter4),
                LookupDao.Properties.Filter5.like(filter5));

        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilterTextWithSuggestion(Context context, String lov_group, String filter1, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(dynamicFilter + "%"));
        if (filter1.contains("%")) {
            if (filter1.length() > 1)
                qb.where(LookupDao.Properties.Filter1.like("%" + filter1 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter1.like(filter1));
        }
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilterTextWithSuggestion(Context context, String lov_group, String filter1, String filter2, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(dynamicFilter + "%"));
        if (filter1.contains("%")) {
            if (filter1.length() > 1)
                qb.where(LookupDao.Properties.Filter1.like("%" + filter1 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter1.eq(filter1));
        }
        if (filter2.contains("%")) {
            if (filter2.length() > 1)
                qb.where(LookupDao.Properties.Filter2.like("%" + filter2 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter2.eq(filter2));
        }
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilterTextWithSuggestion(Context context, String lov_group, String filter1, String filter2, String filter3, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(dynamicFilter + "%"));
        if (filter1.contains("%")) {
            if (filter1.length() > 1)
                qb.where(LookupDao.Properties.Filter1.like("%" + filter1 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter1.eq(filter1));
        }
        if (filter2.contains("%")) {
            if (filter2.length() > 1)
                qb.where(LookupDao.Properties.Filter2.like("%" + filter2 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter2.eq(filter2));
        }
        if (filter3.contains("%")) {
            if (filter3.length() > 1)
                qb.where(LookupDao.Properties.Filter3.like("%" + filter3 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter3.eq(filter3));
        }
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilterTextWithSuggestion(Context context, String lov_group, String filter1, String filter2, String filter3, String filter4, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(dynamicFilter + "%"));
        if (filter1.contains("%")) {
            if (filter1.length() > 1)
                qb.where(LookupDao.Properties.Filter1.like("%" + filter1 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter1.eq(filter1));
        }
        if (filter2.contains("%")) {
            if (filter2.length() > 1)
                qb.where(LookupDao.Properties.Filter2.like("%" + filter2 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter2.eq(filter2));
        }
        if (filter3.contains("%")) {
            if (filter3.length() > 1)
                qb.where(LookupDao.Properties.Filter3.like("%" + filter3 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter3.eq(filter3));
        }
        if (filter4.contains("%")) {
            if (filter4.length() > 1)
                qb.where(LookupDao.Properties.Filter4.like("%" + filter4 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter4.eq(filter4));
        }
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static List<Lookup> getAllByFilterTextWithSuggestion(Context context, String lov_group, String filter1, String filter2, String filter3, String filter4, String filter5, String dynamicFilter) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.eq(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(dynamicFilter + "%"));
        if (filter1.contains("%")) {
            if (filter1.length() > 1)
                qb.where(LookupDao.Properties.Filter1.like("%" + filter1 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter1.eq(filter1));
        }
        if (filter2.contains("%")) {
            if (filter2.length() > 1)
                qb.where(LookupDao.Properties.Filter2.like("%" + filter2 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter2.eq(filter2));
        }
        if (filter3.contains("%")) {
            if (filter3.length() > 1)
                qb.where(LookupDao.Properties.Filter3.like("%" + filter3 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter3.eq(filter3));
        }
        if (filter4.contains("%")) {
            if (filter4.length() > 1)
                qb.where(LookupDao.Properties.Filter4.like("%" + filter4 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter4.eq(filter4));
        }
        if (filter5.contains("%")) {
            if (filter5.length() > 1)
                qb.where(LookupDao.Properties.Filter5.like("%" + filter5 + "%"));
        } else {
            qb.where(LookupDao.Properties.Filter5.eq(filter5));
        }

        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

    public static Lookup getOne(Context context, String uuidLookup,
                                String lov_group) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Uuid_lookup.eq(uuidLookup));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Lookup getOneByCode(Context context, String uuidLookup,
                                      String code) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Code.like(code),
                LookupDao.Properties.Uuid_lookup.eq(uuidLookup));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Lookup getOneByCodeAndlovGroup(Context context, String lov_group,
                                                 String code) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Code.like(code),
                LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Lookup getOneByValueAndlovGroup(Context context, String lov_group,
                                                  String value) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Value.like(value),
                LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select lookup per
     *
     * @param context
     * @return
     */

    public static Lookup getOne(Context context, String uuidLookup) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(
                LookupDao.Properties.Uuid_lookup.eq(uuidLookup));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static Lookup getOneByLovGroup(Context context, String lov_group) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(
                LookupDao.Properties.Lov_group.like(lov_group));
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * add or replace data taskH
     *
     * @param context
     */
    public static void addOrReplace(Context context, Lookup lookup) {
        Lookup nlookup = getOne(context, lookup.getUuid_lookup(), lookup.getLov_group());
        if (nlookup != null) {
            delete(context, nlookup);
        }
        add(context, lookup);
    }

    public static void updateToActive(Context context, String uuid_lookup) {
        Lookup lookup = getOne(context, uuid_lookup);
        if(lookup != null){
            lookup.setIs_active(Global.TRUE_STRING);
            getLookupDao(context).insertOrReplaceInTx(lookup);
        }
    }

    public static List<String> getAllCode(Context context, String lov_group) {
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_ENAME = "SELECT " + LookupDao.Properties.Code.columnName +
                " FROM " + LookupDao.TABLENAME + " WHERE " + LookupDao.Properties.Lov_group.columnName + "='" +
                lov_group + "'";
        Cursor c = getLookupDao(context).getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public static List<String> getAllCodeByFilter(Context context, String lov_group, String filter, int limit) {
        List<String> result = new ArrayList<>();
        String SQL_DISTINCT_ENAME = "SELECT " + LookupDao.Properties.Code.columnName +
                " FROM " + LookupDao.TABLENAME + " WHERE " + LookupDao.Properties.Lov_group.columnName + "='" +
                lov_group + "' AND " +
                LookupDao.Properties.Is_deleted.columnName + "='" + Global.FALSE_STRING + "' AND " +
                LookupDao.Properties.Is_active.columnName + "='" + Global.TRUE_STRING + "' AND " +
                LookupDao.Properties.Code.columnName + " LIKE '" + filter + "%' ORDER BY " +
                LookupDao.Properties.Code.columnName +
                " ASC LIMIT " + limit;
        Cursor c = getLookupDao(context).getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public static List<Lookup> getAllByLovGroupWithFilter(Context context, String lov_group, String filter, int limit) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lov_group),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING),
                LookupDao.Properties.Value.like(filter + "%"));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.limit(limit);
        qb.build();
        return qb.list();
    }

    public static Lookup getOneLastUpdByLovGroup(Context context, String lov_group) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(
                LookupDao.Properties.Lov_group.eq(lov_group));
        qb.orderDesc(LookupDao.Properties.Dtm_upd);
        qb.limit(1);
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static List<Lookup> getAllByLovGroup(Context context, String lovGroup, String branchId, String schemeFlag) {
        QueryBuilder<Lookup> qb = getLookupDao(context).queryBuilder();
        qb.where(LookupDao.Properties.Lov_group.like(lovGroup),
                LookupDao.Properties.Filter1.eq(branchId),
                LookupDao.Properties.Filter2.eq(schemeFlag),
                LookupDao.Properties.Is_deleted.eq(Global.FALSE_STRING),
                LookupDao.Properties.Is_active.eq(Global.TRUE_STRING));
        qb.orderAsc(LookupDao.Properties.Sequence);
        qb.build();
        return qb.list();
    }

}
