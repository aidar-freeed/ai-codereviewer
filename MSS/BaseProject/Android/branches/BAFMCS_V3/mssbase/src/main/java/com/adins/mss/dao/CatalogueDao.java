package com.adins.mss.dao;

import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

import com.adins.mss.dao.Catalogue;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MS_CATALOGUE".
*/
public class CatalogueDao extends AbstractDao<Catalogue, String> {

    public static final String TABLENAME = "MS_CATALOGUE";

    /**
     * Properties of entity Catalogue.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid_mkt_catalogue = new Property(0, String.class, "uuid_mkt_catalogue", true, "UUID_MKT_CATALOGUE");
        public final static Property Catalogue_name = new Property(1, String.class, "catalogue_name", false, "CATALOGUE_NAME");
        public final static Property Catalogue_desc = new Property(2, String.class, "catalogue_desc", false, "CATALOGUE_DESC");
        public final static Property Catalogue_file = new Property(3, String.class, "catalogue_file", false, "CATALOGUE_FILE");
        public final static Property Dtm_crt = new Property(4, java.util.Date.class, "dtm_crt", false, "DTM_CRT");
    };


    public CatalogueDao(DaoConfig config) {
        super(config);
    }
    
    public CatalogueDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MS_CATALOGUE\" (" + //
                "\"UUID_MKT_CATALOGUE\" TEXT PRIMARY KEY NOT NULL ," + // 0: uuid_mkt_catalogue
                "\"CATALOGUE_NAME\" TEXT," + // 1: catalogue_name
                "\"CATALOGUE_DESC\" TEXT," + // 2: catalogue_desc
                "\"CATALOGUE_FILE\" TEXT," + // 3: catalogue_file
                "\"DTM_CRT\" INTEGER);"); // 4: dtm_crt
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MS_CATALOGUE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, Catalogue entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid_mkt_catalogue());
 
        String catalogue_name = entity.getCatalogue_name();
        if (catalogue_name != null) {
            stmt.bindString(2, catalogue_name);
        }
 
        String catalogue_desc = entity.getCatalogue_desc();
        if (catalogue_desc != null) {
            stmt.bindString(3, catalogue_desc);
        }
 
        String catalogue_file = entity.getCatalogue_file();
        if (catalogue_file != null) {
            stmt.bindString(4, catalogue_file);
        }
 
        java.util.Date dtm_crt = entity.getDtm_crt();
        if (dtm_crt != null) {
            stmt.bindLong(5, dtm_crt.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Catalogue readEntity(Cursor cursor, int offset) {
        Catalogue entity = new Catalogue( //
            cursor.getString(offset + 0), // uuid_mkt_catalogue
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // catalogue_name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // catalogue_desc
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // catalogue_file
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)) // dtm_crt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Catalogue entity, int offset) {
        entity.setUuid_mkt_catalogue(cursor.getString(offset + 0));
        entity.setCatalogue_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCatalogue_desc(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCatalogue_file(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDtm_crt(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Catalogue entity, long rowId) {
        return entity.getUuid_mkt_catalogue();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Catalogue entity) {
        if(entity != null) {
            return entity.getUuid_mkt_catalogue();
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
