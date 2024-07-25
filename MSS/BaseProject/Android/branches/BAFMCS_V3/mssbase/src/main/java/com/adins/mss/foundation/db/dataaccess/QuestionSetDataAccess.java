package com.adins.mss.foundation.db.dataaccess;

import android.content.Context;

import com.adins.mss.dao.DaoSession;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.QuestionSetDao;
import com.adins.mss.foundation.db.DaoOpenHelper;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class QuestionSetDataAccess {

    private QuestionSetDataAccess() {
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
     * get printItem dao and you can access the DB
     *
     * @param context
     * @return
     */
    protected static QuestionSetDao getQuestionSetDao(Context context) {
        return getDaoSession(context).getQuestionSetDao();
    }

    /**
     * Clear session, close db and set daoOpenHelper to null
     */
    public static void closeAll() {
        DaoOpenHelper.closeAll();
    }

    /**
     * add questionSet as entity
     *
     * @param context
     * @param questionSet
     */
    public static void add(Context context, QuestionSet questionSet) {
        getQuestionSetDao(context).insertInTx(questionSet);
        getDaoSession(context).clear();
    }

    /**
     * add questionSet as list entity
     *
     * @param context
     * @param questionSetList
     */
    public static void add(Context context, List<QuestionSet> questionSetList) {
        getQuestionSetDao(context).insertInTx(questionSetList);
        getDaoSession(context).clear();
    }

    /**
     * delete all content in table.
     *
     * @param context
     */
    public static void clean(Context context) {
        getQuestionSetDao(context).deleteAll();
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param questionSet
     */
    public static void delete(Context context, QuestionSet questionSet) {
        getQuestionSetDao(context).delete(questionSet);
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyScheme
     *
     * @param context
     */
    public static void delete(Context context, String keyScheme) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.build();
        getQuestionSetDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * delete all record by keyScheme and version
     */
    public static void delete(Context context, String uuidScheme, String schemeVersion) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(uuidScheme), QuestionSetDao.Properties.Form_version.eq(schemeVersion));
        qb.build();
        getQuestionSetDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * @param context
     * @param questionSet
     */
    public static void update(Context context, QuestionSet questionSet) {
        getQuestionSetDao(context).insertOrReplaceInTx(questionSet);
        getDaoSession(context).clear();
    }

    /**
     * add or replace questionSet as list entity
     *
     * @param context
     * @param keyScheme
     * @param listQuestionSet
     */
    public static void addOrReplace(Context context, String keyScheme, List<QuestionSet> listQuestionSet) {
        getQuestionSetDao(context).insertOrReplaceInTx(listQuestionSet);
        getDaoSession(context).clear();
    }

    /**
     * add or replace questionSet as entity
     *
     * @param context
     * @param keyScheme
     * @param questionSet
     */
    public static void addOrReplace(Context context, String keyScheme, QuestionSet questionSet) {
        getQuestionSetDao(context).insertOrReplaceInTx(questionSet);
        getDaoSession(context).clear();
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param keyScheme
     * @param uuidQuestionSet
     * @return
     */
    public static QuestionSet getOne(Context context, String keyScheme, String uuidQuestionSet) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme),
                QuestionSetDao.Properties.Uuid_question_set.eq(uuidQuestionSet));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    public static QuestionSet getOne(String keyScheme, Context context, String questionId) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme),
                QuestionSetDao.Properties.Question_id.eq(questionId));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param keyScheme
     * @param questionId
     * @param questionGroupId
     * @return
     */
    public static QuestionSet getOne(Context context, String keyScheme, String questionId, String questionGroupId) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme),
                QuestionSetDao.Properties.Question_id.eq(questionId),
                QuestionSetDao.Properties.Question_group_id.eq(questionGroupId));
        qb.build();
        if (qb.list().isEmpty())
            return null;
        return qb.list().get(0);
    }

    /**
     * select * from table where uuid_scheme = param
     *
     * @param context
     * @param keyScheme
     * @return
     */
    public static List<QuestionSet> getAll(Context context, String keyScheme) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme));
        qb.orderAsc(QuestionSetDao.Properties.Question_group_order);
        qb.orderAsc(QuestionSetDao.Properties.Question_order);
        qb.build();
        return qb.list();
    }

    /**
     * select * from table where uuid_scheme = param and form_version = param
     *
     * @param context
     * @param keyScheme
     * @param formVersion
     * @return
     */
    public static List<QuestionSet> getAllByFormVersion(Context context, String keyScheme, String formVersion) {
        List<QuestionSet> list = null;

        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme), // Nendi: 2019.09.26 | Tambah penjagaan yang double insert bisa taskD nya jd kosong di form
                QuestionSetDao.Properties.Form_version.eq(formVersion), new WhereCondition.StringCondition("1 GROUP BY QUESTION_ID"));
        qb.orderAsc(QuestionSetDao.Properties.Question_group_order);
        qb.orderAsc(QuestionSetDao.Properties.Question_order);
        qb.build();
        if (qb != null) {
            list = qb.list();
        }
        return list;
    }

    public static void deleteBySchemeVersion(Context context, String keyScheme, List<String> versionScheme) {

        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Uuid_scheme.eq(keyScheme));
        if(!versionScheme.isEmpty()) {
            qb.where(QuestionSetDao.Properties.Form_version.notIn(versionScheme));
        }
        qb.build();
        getQuestionSetDao(context).deleteInTx(qb.list());
        getDaoSession(context).clear();
    }

    /**
     * select * from table where questionID = param1 and questionGroupID = param2
     *
     * @param context
     * @param questionID
     * @param questionGroupID
     * @return
     */
    public static List<QuestionSet> getAllByQuestionIDAndQuestionGroupID(Context context, String questionID, String questionGroupID) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Question_id.eq(questionID)
                , QuestionSetDao.Properties.Question_group_id.eq(questionGroupID));
        qb.orderAsc(QuestionSetDao.Properties.Question_group_order);
        qb.build();
        return qb.list();
    }

    /**
     * @param context
     * @param tag
     * @return
     */
    public static QuestionSet getOneQuestionByTag(Context context, String tag) {
        QueryBuilder<QuestionSet> qb = getQuestionSetDao(context).queryBuilder();
        qb.where(QuestionSetDao.Properties.Tag.eq(tag));
        qb.build();

        if (qb.list().size() == 0) {
            return null;
        }
        return qb.list().get(0);
    }

}
