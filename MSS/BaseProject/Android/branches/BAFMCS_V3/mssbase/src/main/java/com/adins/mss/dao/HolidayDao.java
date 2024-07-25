package com.adins.mss.dao;

import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

import com.adins.mss.dao.Holiday;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MS_HOLIDAY".
*/
public class HolidayDao extends AbstractDao<Holiday, String> {

    public static final String TABLENAME = "MS_HOLIDAY";

    /**
     * Properties of entity Holiday.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_holiday = new Property(0, String.class, "uuid_holiday", true, "UUID_HOLIDAY");
        public final static Property H_date = new Property(1, java.util.Date.class, "h_date", false, "H_DATE");
        public final static Property H_desc = new Property(2, String.class, "h_desc", false, "H_DESC");
        public final static Property Flag_holiday = new Property(3, String.class, "flag_holiday", false, "FLAG_HOLIDAY");
        public final static Property Dtm_upd = new Property(4, java.util.Date.class, "dtm_upd", false, "DTM_UPD");
        public final static Property Flag_day = new Property(5, String.class, "flag_day", false, "FLAG_DAY");
        public final static Property Branch = new Property(6, String.class, "branch", false, "BRANCH");
    };


    public HolidayDao(DaoConfig config) {
        super(config);
    }
    
    public HolidayDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MS_HOLIDAY\" (" + //
                "\"UUID_HOLIDAY\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_holiday
                "\"H_DATE\" INTEGER," + // 1: h_date
                "\"H_DESC\" TEXT," + // 2: h_desc
                "\"FLAG_HOLIDAY\" TEXT," + // 3: flag_holiday
                "\"DTM_UPD\" INTEGER," + // 4: dtm_upd
                "\"FLAG_DAY\" TEXT," + // 5: flag_day
                "\"BRANCH\" TEXT);"); // 6: branch
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MS_HOLIDAY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, Holiday entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_holiday());
 
        java.util.Date h_date = entity.getH_date();
        if (h_date != null) {
            stmt.bindLong(2, h_date.getTime());
        }
 
        String h_desc = entity.getH_desc();
        if (h_desc != null) {
            stmt.bindString(3, h_desc);
        }
 
        String flag_holiday = entity.getFlag_holiday();
        if (flag_holiday != null) {
            stmt.bindString(4, flag_holiday);
        }
 
        java.util.Date dtm_upd = entity.getDtm_upd();
        if (dtm_upd != null) {
            stmt.bindLong(5, dtm_upd.getTime());
        }
 
        String flag_day = entity.getFlag_day();
        if (flag_day != null) {
            stmt.bindString(6, flag_day);
        }
 
        String branch = entity.getBranch();
        if (branch != null) {
            stmt.bindString(7, branch);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Holiday readEntity(Cursor cursor, int offset) {
        Holiday entity = new Holiday( //
            cursor.getString(offset + 0), // uuid_holiday
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // h_date
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // h_desc
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // flag_holiday
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // dtm_upd
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // flag_day
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // branch
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Holiday entity, int offset) {
        entity.setUuid_holiday(cursor.getString(offset + 0));
        entity.setH_date(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setH_desc(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFlag_holiday(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDtm_upd(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setFlag_day(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBranch(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Holiday entity, long rowId) {
        return entity.getUuid_holiday();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Holiday entity) {
        if(entity != null) {
            return entity.getUuid_holiday();
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
