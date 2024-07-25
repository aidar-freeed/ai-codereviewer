package com.adins.mss.base.cleansing;

import android.content.Context;

import com.adins.mss.dao.DaoMaster;
import com.adins.mss.foundation.db.dataaccess.CollectionHistoryDataAccess;
import com.adins.mss.foundation.db.dataaccess.CommentDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportDDataAccess;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.GroupUserDataAccess;
import com.adins.mss.foundation.db.dataaccess.HolidayDataAccess;
import com.adins.mss.foundation.db.dataaccess.ImageResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.InstallmentScheduleDataAccess;
import com.adins.mss.foundation.db.dataaccess.LocationInfoDataAccess;
import com.adins.mss.foundation.db.dataaccess.LoggerDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
import com.adins.mss.foundation.db.dataaccess.MessageDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentHDataAccess;
import com.adins.mss.foundation.db.dataaccess.PaymentHistoryDDataAccess;
import com.adins.mss.foundation.db.dataaccess.PaymentHistoryHDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskSummaryDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;

import de.greenrobot.dao.database.Database;

//import com.adins.mss.foundation.db.dataaccess.PaymentHistoryDataAccess;

public class ApplicationCleansingManager {
    /**
     * This method is used to delete all data in local storage database
     *
     * @param context
     */
    public static void deleteAllDataInAllTable(Context context) {
        ////////////////////////////////

        CommentDataAccess.clean(context);
        TimelineDataAccess.clean(context);
        TimelineTypeDataAccess.clean(context);
        MessageDataAccess.clean(context);

        LoggerDataAccess.clean(context);

        LocationInfoDataAccess.clean(context);

        MenuDataAccess.clean(context);
        ReceiptVoucherDataAccess.clean(context);
        InstallmentScheduleDataAccess.clean(context);
        DepositReportDDataAccess.clean(context);
        DepositReportHDataAccess.clean(context);
        CollectionHistoryDataAccess.clean(context);
        //PaymentHistoryDataAccess.clean(context);
        PaymentHistoryDDataAccess.clean(context);
        PaymentHistoryHDataAccess.clean(context);
        ImageResultDataAccess.clean(context);
        PrintResultDataAccess.clean(context);
        LookupDataAccess.clean(context);
        QuestionSetDataAccess.clean(context);
        PrintItemDataAccess.clean(context);
        TaskDDataAccess.clean(context);
        TaskHDataAccess.clean(context);
        TaskSummaryDataAccess.clean(context);
        SchemeDataAccess.clean(context);
        MobileContentDDataAccess.clean(context);
        MobileContentHDataAccess.clean(context);

        GroupUserDataAccess.clean(context);

        GeneralParameterDataAccess.clean(context);

        UserDataAccess.clean(context);

        SyncDataAccess.clean(context);
        HolidayDataAccess.clean(context);
        ////////////////////////////////////////

    }

    /**
     * This method is used to drop all atable in local storage database
     *
     * @param db
     */
    public static void dropAllTable(Database db) {
        DaoMaster.dropAllTables(db, true);
    }
}
