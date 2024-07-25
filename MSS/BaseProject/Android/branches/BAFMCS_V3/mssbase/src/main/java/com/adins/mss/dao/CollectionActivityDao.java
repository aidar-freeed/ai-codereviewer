package com.adins.mss.dao;

import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

import com.adins.mss.dao.CollectionActivity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TR_COLLECTIONACTIVITY".
*/
public class CollectionActivityDao extends AbstractDao<CollectionActivity, String> {

    public static final String TABLENAME = "TR_COLLECTIONACTIVITY";

    /**
     * Properties of entity CollectionActivity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_collection_activity = new Property(0, String.class, "uuid_collection_activity", true, "UUID_COLLECTION_ACTIVITY");
        public final static Property Uuid_task_h = new Property(1, String.class, "uuid_task_h", false, "UUID_TASK_H");
        public final static Property Agreement_no = new Property(2, String.class, "agreement_no", false, "AGREEMENT_NO");
        public final static Property Branch_code = new Property(3, String.class, "branch_code", false, "BRANCH_CODE");
        public final static Property Collector_name = new Property(4, String.class, "collector_name", false, "COLLECTOR_NAME");
        public final static Property Activity = new Property(5, String.class, "activity", false, "ACTIVITY");
        public final static Property Result = new Property(6, String.class, "result", false, "RESULT");
        public final static Property Notes = new Property(7, String.class, "notes", false, "NOTES");
        public final static Property Overdue_days = new Property(8, String.class, "overdue_days", false, "OVERDUE_DAYS");
        public final static Property Activity_date = new Property(9, java.util.Date.class, "activity_date", false, "ACTIVITY_DATE");
        public final static Property Ptp_date = new Property(10, java.util.Date.class, "ptp_date", false, "PTP_DATE");
        public final static Property Usr_crt = new Property(11, String.class, "usr_crt", false, "USR_CRT");
        public final static Property Dtm_crt = new Property(12, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
        public final static Property Usr_upd = new Property(13, String.class, "usr_upd", false, "USR_UPD");
        public final static Property Dtm_upd = new Property(14, java.util.Date.class, "dtm_upd", false, "DTM_UPD");
        public final static Property Next_plan_date = new Property(15, java.util.Date.class, "next_plan_date", false, "NEXT_PLAN_DATE");
        public final static Property Next_plan_action = new Property(16, String.class, "next_plan_action", false, "NEXT_PLAN_ACTION");
    };


    public CollectionActivityDao(DaoConfig config) {
        super(config);
    }
    
    public CollectionActivityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TR_COLLECTIONACTIVITY\" (" + //
                "\"UUID_COLLECTION_ACTIVITY\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_collection_activity
                "\"UUID_TASK_H\" TEXT," + // 1: uuid_task_h
                "\"AGREEMENT_NO\" TEXT," + // 2: agreement_no
                "\"BRANCH_CODE\" TEXT," + // 3: branch_code
                "\"COLLECTOR_NAME\" TEXT," + // 4: collector_name
                "\"ACTIVITY\" TEXT," + // 5: activity
                "\"RESULT\" TEXT," + // 6: result
                "\"NOTES\" TEXT," + // 7: notes
                "\"OVERDUE_DAYS\" TEXT," + // 8: overdue_days
                "\"ACTIVITY_DATE\" INTEGER," + // 9: activity_date
                "\"PTP_DATE\" INTEGER," + // 10: ptp_date
                "\"USR_CRT\" TEXT," + // 11: usr_crt
                "\"DTM_CRT\" INTEGER," + // 12: dtm_crt
                "\"USR_UPD\" TEXT," + // 13: usr_upd
                "\"DTM_UPD\" INTEGER," + // 14: dtm_upd
                "\"NEXT_PLAN_DATE\" INTEGER," + // 15: next_plan_date
                "\"NEXT_PLAN_ACTION\" TEXT);"); // 16: next_plan_action
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TR_COLLECTIONACTIVITY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, CollectionActivity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_collection_activity());
 
        String uuid_task_h = entity.getUuid_task_h();
        if (uuid_task_h != null) {
            stmt.bindString(2, uuid_task_h);
        }
 
        String agreement_no = entity.getAgreement_no();
        if (agreement_no != null) {
            stmt.bindString(3, agreement_no);
        }
 
        String branch_code = entity.getBranch_code();
        if (branch_code != null) {
            stmt.bindString(4, branch_code);
        }
 
        String collector_name = entity.getCollector_name();
        if (collector_name != null) {
            stmt.bindString(5, collector_name);
        }
 
        String activity = entity.getActivity();
        if (activity != null) {
            stmt.bindString(6, activity);
        }
 
        String result = entity.getResult();
        if (result != null) {
            stmt.bindString(7, result);
        }
 
        String notes = entity.getNotes();
        if (notes != null) {
            stmt.bindString(8, notes);
        }
 
        String overdue_days = entity.getOverdue_days();
        if (overdue_days != null) {
            stmt.bindString(9, overdue_days);
        }
 
        java.util.Date activity_date = entity.getActivity_date();
        if (activity_date != null) {
            stmt.bindLong(10, activity_date.getTime());
        }
 
        java.util.Date ptp_date = entity.getPtp_date();
        if (ptp_date != null) {
            stmt.bindLong(11, ptp_date.getTime());
        }
 
        String usr_crt = entity.getUsr_crt();
        if (usr_crt != null) {
            stmt.bindString(12, usr_crt);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(13, dtm_crt.getTime());
        }
 
        String usr_upd = entity.getUsr_upd();
        if (usr_upd != null) {
            stmt.bindString(14, usr_upd);
        }
 
        java.util.Date dtm_upd = entity.getDtm_upd();
        if (dtm_upd != null) {
            stmt.bindLong(15, dtm_upd.getTime());
        }
 
        java.util.Date next_plan_date = entity.getNext_plan_date();
        if (next_plan_date != null) {
            stmt.bindLong(16, next_plan_date.getTime());
        }
 
        String next_plan_action = entity.getNext_plan_action();
        if (next_plan_action != null) {
            stmt.bindString(17, next_plan_action);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CollectionActivity readEntity(Cursor cursor, int offset) {
        CollectionActivity entity = new CollectionActivity( //
            cursor.getString(offset + 0), // uuid_collection_activity
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid_task_h
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // agreement_no
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // branch_code
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // collector_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // activity
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // result
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // notes
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // overdue_days
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // activity_date
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // ptp_date
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // usr_crt
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)), // dtm_crt
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // usr_upd
            cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)), // dtm_upd
            cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)), // next_plan_date
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16) // next_plan_action
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CollectionActivity entity, int offset) {
        entity.setUuid_collection_activity(cursor.getString(offset + 0));
        entity.setUuid_task_h(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAgreement_no(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBranch_code(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCollector_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setActivity(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setResult(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNotes(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setOverdue_days(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setActivity_date(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setPtp_date(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setUsr_crt(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setDtm_crt(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
        entity.setUsr_upd(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setDtm_upd(cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)));
        entity.setNext_plan_date(cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)));
        entity.setNext_plan_action(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(CollectionActivity entity, long rowId) {
        return entity.getUuid_collection_activity();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(CollectionActivity entity) {
        if(entity != null) {
            return entity.getUuid_collection_activity();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
