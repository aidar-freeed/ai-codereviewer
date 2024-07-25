package com.adins.mss.base.autosend;

import android.content.Context;

import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.services.ScheduledConnectionItem;
import com.adins.mss.foundation.services.ScheduledItem.ScheduledItemHandler;

import java.util.ArrayList;
import java.util.List;

public class AutoSubmitImage extends ScheduledConnectionItem implements ScheduledItemHandler {

    Context context;
    /**
     * stored question which are being sent
     */
    private List<TaskD> imageQuestions;

    public AutoSubmitImage(Context context, String id, int interval, String url, boolean enc, boolean dec) {
        super(id, interval, url, enc, dec);
        imageQuestions = new ArrayList<TaskD>();
        this.context = context;
        setHandler(this);
    }

    @Override
    protected String getData() {
        // TODO fetch pending image
        imageQuestions = null;

        // TODO convert to JSON

        return null;
    }

    @Override
    protected boolean onSuccess(HttpConnectionResult result) {
        TaskDDataAccess.changeStatusList(context, imageQuestions, true);
        imageQuestions.clear();
        return false;
    }

    @Override
    protected boolean onFail(HttpConnectionResult result) {
        return false;
    }

}
