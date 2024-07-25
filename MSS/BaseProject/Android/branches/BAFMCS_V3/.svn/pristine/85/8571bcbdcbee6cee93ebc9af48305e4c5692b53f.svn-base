package com.adins.mss.base.autosend;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.services.ScheduledConnectionItem;
import com.adins.mss.foundation.services.ScheduledItem.ScheduledItemHandler;

import java.util.ArrayList;
import java.util.List;

//public class AutoSubmitForm extends ScheduledItem implements ScheduledItemHandler{
public class AutoSubmitForm extends ScheduledConnectionItem implements ScheduledItemHandler {

    Context context;
    private TaskH processedTask;
    private List<TaskD> processedQuestions;
    private boolean isPartial;

    public AutoSubmitForm(Context context, String id, int interval, String url, boolean enc, boolean dec, boolean isPartial) {
        super(id, interval, url, enc, dec);
        processedQuestions = new ArrayList<TaskD>();
        this.context = context;
        this.isPartial = isPartial;
        setHandler(this);
    }

    @Override
    protected String getData() {

        //make sure processedTask and Questions are clear
        processedTask = null;

        // TODO fetch data from DB, add imei code etc
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
//		processedTask = TaskHDataAccess.getAllTaskByStatus(context, uuidUser, TaskHDataAccess.STATUS_SEND_PENDING);
        String task_uuid = "";

        int withImage;
        if (isPartial) {
            withImage = TaskDDataAccess.NON_IMAGE_ONLY;
        } else {
            withImage = TaskDDataAccess.ALL_TASK;
        }
        processedQuestions = TaskDDataAccess.getAllByTaskId(context, uuidUser, task_uuid, withImage);

        //TODO convert to JSON
        Object jsonObject = null;
        String jsonString = Formatter.getJsonFromObject(jsonObject);

        return jsonString;
    }

    @Override
    protected boolean onSuccess(HttpConnectionResult result) {
        //Header status
        String status;
        if (isPartial) {
            status = TaskHDataAccess.STATUS_SEND_UPLOADING;
        } else {
            status = TaskHDataAccess.STATUS_SEND_SENT;
        }
        TaskHDataAccess.updateStatus(context, processedTask, status);

        //Detail status
        TaskDDataAccess.changeStatusList(context, processedQuestions, true);

        //clear
        processedTask = null;
        processedQuestions.clear();

        return true;
    }

    @Override
    protected boolean onFail(HttpConnectionResult result) {
        return false;
    }

}
