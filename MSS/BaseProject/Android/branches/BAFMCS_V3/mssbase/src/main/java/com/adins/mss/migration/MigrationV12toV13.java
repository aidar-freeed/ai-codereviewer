package com.adins.mss.migration;

import com.adins.mss.dao.BankAccountOfBranchDao;
import com.adins.mss.dao.BroadcastDao;
import com.adins.mss.dao.ReceiptHistoryDao;

import de.greenrobot.dao.database.Database;

public class MigrationV12toV13 {

    public static void execute(Database db, int oldVersion) {
        if (oldVersion < 13) {
            db.execSQL("ALTER TABLE TR_DEPOSITREPORT_H ADD BRANCH_PAYMENT STRING");
            db.execSQL("ALTER TABLE TR_DEPOSITREPORT_H ADD CODE_CHANNEL STRING");
            db.execSQL("ALTER TABLE TR_DEPOSITREPORT_H ADD NO_TRANSACTION STRING");
            db.execSQL("ALTER TABLE TR_TASK_H ADD BATCH_ID STRING");
            db.execSQL("CREATE TABLE MS_PAYMENTCHANNEL (" +
                    " UUID_PAYMENT STRING PRIMARY KEY," +
                    " USR_CRT STRING," +
                    " DTM_CRT DATE," +
                    " IS_ACTIVE STRING," +
                    " CODE STRING," +
                    " DESCRIPTION STRING," +
                    " PAYMENT_LIMIT DOUBLE)");

            BankAccountOfBranchDao.createTable(db, true);

            db.execSQL("ALTER TABLE MS_USER ADD PILOTING_BRANCH TEXT");

            db.execSQL("ALTER TABLE TR_RECEIPTVOUCHER ADD FLAG_SOURCES STRING");

            db.execSQL("ALTER TABLE TR_TASK_H ADD SURVEY_LOCATION TEXT");
            db.execSQL("ALTER TABLE TR_TASK_H ADD NO_RANGKA TEXT");
            db.execSQL("ALTER TABLE TR_TASK_H ADD NO_PLAT TEXT");
            db.execSQL("ALTER TABLE TR_TASK_H ADD NO_MESIN TEXT");
            db.execSQL("ALTER TABLE TR_TASK_H ADD BATCH_ID TEXT");
            db.execSQL("ALTER TABLE TR_TASK_H ADD SURVEY_LOCATION TEXT");

            BroadcastDao.createTable(db, true);
            ReceiptHistoryDao.createTable(db, true);
        }
    }

}
