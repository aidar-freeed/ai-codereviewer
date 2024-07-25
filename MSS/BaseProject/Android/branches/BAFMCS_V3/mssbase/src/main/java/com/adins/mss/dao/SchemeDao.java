package com.adins.mss.dao;

import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

import com.adins.mss.dao.Scheme;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MS_SCHEME".
*/
public class SchemeDao extends AbstractDao<Scheme, String> {

    public static final String TABLENAME = "MS_SCHEME";

    /**
     * Properties of entity Scheme.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_scheme = new Property(0, String.class, "uuid_scheme", true, "UUID_SCHEME");
        public final static Property Scheme_description = new Property(1, String.class, "scheme_description", false, "SCHEME_DESCRIPTION");
        public final static Property Scheme_last_update = new Property(2, java.util.Date.class, "scheme_last_update", false, "SCHEME_LAST_UPDATE");
        public final static Property Is_printable = new Property(3, String.class, "is_printable", false, "IS_PRINTABLE");
        public final static Property Form_id = new Property(4, String.class, "form_id", false, "FORM_ID");
        public final static Property Usr_crt = new Property(5, String.class, "usr_crt", false, "USR_CRT");
        public final static Property Is_preview_server = new Property(6, String.class, "is_preview_server", false, "IS_PREVIEW_SERVER");
        public final static Property Dtm_crt = new Property(7, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
        public final static Property Usr_upd = new Property(8, String.class, "usr_upd", false, "USR_UPD");
        public final static Property Dtm_upd = new Property(9, java.util.Date.class, "dtm_upd", false, "DTM_UPD");
        public final static Property Form_type = new Property(10, String.class, "form_type", false, "FORM_TYPE");
        public final static Property Is_active = new Property(11, String.class, "is_active", false, "IS_ACTIVE");
        public final static Property Form_version = new Property(12, String.class, "form_version", false, "FORM_VERSION");
    };

    private DaoSession daoSession;


    public SchemeDao(DaoConfig config) {
        super(config);
    }
    
    public SchemeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MS_SCHEME\" (" + //
                "\"UUID_SCHEME\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_scheme
                "\"SCHEME_DESCRIPTION\" TEXT," + // 1: scheme_description
                "\"SCHEME_LAST_UPDATE\" INTEGER," + // 2: scheme_last_update
                "\"IS_PRINTABLE\" TEXT," + // 3: is_printable
                "\"FORM_ID\" TEXT," + // 4: form_id
                "\"USR_CRT\" TEXT," + // 5: usr_crt
                "\"IS_PREVIEW_SERVER\" TEXT," + // 6: is_preview_server
                "\"DTM_CRT\" INTEGER," + // 7: dtm_crt
                "\"USR_UPD\" TEXT," + // 8: usr_upd
                "\"DTM_UPD\" INTEGER," + // 9: dtm_upd
                "\"FORM_TYPE\" TEXT," + // 10: form_type
                "\"IS_ACTIVE\" TEXT," + // 11: is_active
                "\"FORM_VERSION\" TEXT);"); // 12: form_version
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MS_SCHEME\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, Scheme entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_scheme());
 
        String scheme_description = entity.getScheme_description();
        if (scheme_description != null) {
            stmt.bindString(2, scheme_description);
        }
 
        java.util.Date scheme_last_update = entity.getScheme_last_update();
        if (scheme_last_update != null) {
            stmt.bindLong(3, scheme_last_update.getTime());
        }
 
        String is_printable = entity.getIs_printable();
        if (is_printable != null) {
            stmt.bindString(4, is_printable);
        }
 
        String form_id = entity.getForm_id();
        if (form_id != null) {
            stmt.bindString(5, form_id);
        }
 
        String usr_crt = entity.getUsr_crt();
        if (usr_crt != null) {
            stmt.bindString(6, usr_crt);
        }
 
        String is_preview_server = entity.getIs_preview_server();
        if (is_preview_server != null) {
            stmt.bindString(7, is_preview_server);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(8, dtm_crt.getTime());
        }
 
        String usr_upd = entity.getUsr_upd();
        if (usr_upd != null) {
            stmt.bindString(9, usr_upd);
        }
 
        java.util.Date dtm_upd = entity.getDtm_upd();
        if (dtm_upd != null) {
            stmt.bindLong(10, dtm_upd.getTime());
        }
 
        String form_type = entity.getForm_type();
        if (form_type != null) {
            stmt.bindString(11, form_type);
        }
 
        String is_active = entity.getIs_active();
        if (is_active != null) {
            stmt.bindString(12, is_active);
        }
 
        String form_version = entity.getForm_version();
        if (form_version != null) {
            stmt.bindString(13, form_version);
        }
    }

    @Override
    protected void attachEntity(Scheme entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Scheme readEntity(Cursor cursor, int offset) {
        Scheme entity = new Scheme( //
            cursor.getString(offset + 0), // uuid_scheme
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // scheme_description
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // scheme_last_update
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // is_printable
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // form_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // usr_crt
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // is_preview_server
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // dtm_crt
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // usr_upd
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // dtm_upd
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // form_type
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // is_active
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // form_version
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Scheme entity, int offset) {
        entity.setUuid_scheme(cursor.getString(offset + 0));
        entity.setScheme_description(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setScheme_last_update(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setIs_printable(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setForm_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUsr_crt(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIs_preview_server(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDtm_crt(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setUsr_upd(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDtm_upd(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setForm_type(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIs_active(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setForm_version(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Scheme entity, long rowId) {
        return entity.getUuid_scheme();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Scheme entity) {
        if(entity != null) {
            return entity.getUuid_scheme();
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
