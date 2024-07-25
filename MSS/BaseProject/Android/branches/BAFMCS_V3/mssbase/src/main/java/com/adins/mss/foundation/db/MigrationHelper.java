package com.adins.mss.foundation.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DaoMaster;
import com.adins.mss.dao.LogoPrintDao;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskSummaryDao;
import com.adins.mss.dao.ThemeDao;
import com.adins.mss.dao.ThemeItemDao;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.TimelineType;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by gigin.ginanjar on 08/09/2016.
 */
public class MigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
    private static MigrationHelper instance;
    private static boolean migrationFinished = false;
    public static MigrationHelper getInstance() {
        if (instance == null) {
            instance = new MigrationHelper();
        }
        return instance;
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            FireCrash.log(e);
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return columns;
    }

    /**
     * migrate Data from old DB
     * how to use : add this sample in onUpgrade() method in DaoMaster.java
     * MigrationHelper.getInstance().migrate(db, [optional]UserDao.class, [optional]ItemDao.class);
     *
     * @param db
     * @param daoClasses
     */
    public void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        generateTempTables(db, daoClasses);
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
        restoreData(db, daoClasses);
    }
    public void migrateFrom2x(Database db) {
        //tr_depositreport_d
        db.execSQL("ALTER TABLE TR_DEPOSITREPORT_D ADD AGREEMENT_NO TEXT");
        //tr_timeline
        db.execSQL("ALTER TABLE TR_TIMELINE ADD NAME TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD ADDRESS TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD AGREEMENT_NO TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD AMOUNT_DUE TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD OVERDUE TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD INSTALLMENT_NO TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD ATTD_ADDRESS TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD PRIORITY TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD IS_VERIFICATION_TASK TEXT");
        db.execSQL("ALTER TABLE TR_TIMELINE ADD COLL_RESULT TEXT");

        //create table
        //tr_task_summary
        TaskSummaryDao.createTable(db,true);
        //tr_theme
        ThemeDao.createTable(db,true);
        //tr_theme_item
        ThemeItemDao.createTable(db,true);
        //tr_logo_print
        LogoPrintDao.createTable(db,true);
    }

    public  void migrateTaskHFrom2x(Context context, List<Timeline> timelineList){
        if(!migrationFinished) {
            TimelineType typeSaveDraft = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_SAVEDRAFT);
            for (Timeline timeline : timelineList) {
                String uuid_task_h = timeline.getUuid_task_h();
                if (!uuid_task_h.equalsIgnoreCase("")) {
                    List<TaskH> oldTaskH = TaskHDataAccess.getTaskById(context, uuid_task_h);
                    if (oldTaskH.get(0).getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT) && oldTaskH.get(0).getSubmit_date() == null) {
                        timeline.setTimelineType(typeSaveDraft);
                        TimelineDataAccess.addOrReplace(context, timeline);
                    }
                }
            }
            migrationFinished = true;
        }
    }

    private void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);

            String divider = "";
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList<>();

            StringBuilder createTableStringBuilder = new StringBuilder();

            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");

            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;

                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName);

                    String type = null;

                    try {
                        type = getTypeByClass(daoConfig.properties[j].type);
                    } catch (Exception exception) {
                        ACRA.getErrorReporter().handleSilentException(exception);
                    }

                    createTableStringBuilder.append(divider).append(columnName).append(" ").append(type);

                    if (daoConfig.properties[j].primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY");
                    }

                    divider = ",";
                }
            }
            createTableStringBuilder.append(");");

            db.execSQL(createTableStringBuilder.toString());

            StringBuilder insertTableStringBuilder = new StringBuilder();

            insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(" FROM ").append(tableName).append(";");

            db.execSQL(insertTableStringBuilder.toString());
        }
    }

    private void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);

            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList();

            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;

                if (getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName);
                }
            }

            StringBuilder insertTableStringBuilder = new StringBuilder();

            insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");

            StringBuilder dropTableStringBuilder = new StringBuilder();

            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);

            db.execSQL(insertTableStringBuilder.toString());
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    private String getTypeByClass(Class<?> type) throws Exception {
        if (type.equals(String.class)) {
            return "TEXT";
        }
        if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if (type.equals(Boolean.class)) {
            return "BOOLEAN";
        }

        Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(type.toString()));
        throw exception;
    }
}
