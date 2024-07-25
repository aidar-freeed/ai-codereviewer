package com.adins.mss.coll.interfaces;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.coll.R;
import com.adins.mss.coll.networks.ClosingTaskListener;
import com.adins.mss.coll.networks.entities.ClosingTaskRequest;
import com.adins.mss.coll.networks.responses.ClosingTaskResponse;
import com.adins.mss.coll.networks.senders.ClosingTaskSender;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class ClosingTaskImpl implements ClosingTaskInterface {
    private Context context;
    private Activity activity;
    private ClosingTaskListener listener;

    public ClosingTaskImpl(Activity context, ClosingTaskListener listener) {
        this.activity = context;
    }

    @Override
    public void closingTask() {
        if (GlobalData.getSharedGlobalData().getUser() != null) {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

            boolean isDraft = TaskHDataAccess.getAllTaskByStatus(context, uuidUser, TaskHDataAccess.STATUS_SEND_SAVEDRAFT).size() != 0;
            boolean isRvPending = TaskHDataAccess.getOneTaskByStatusRV(context, uuidUser, TaskHDataAccess.STATUS_RV_PENDING) != null;
            boolean isPending = TaskHDataAccess.getAllTaskByStatus(context, uuidUser, TaskHDataAccess.STATUS_SEND_PENDING).size() != 0;

            if (Global.isIsUploading() || isDraft || isRvPending || isPending) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder.withTitle(context.getString(R.string.title_mn_closing_task))
                        .withMessage(context.getString(R.string.msg_still_uploading_closing_task))
                        .withButton1Text(context.getString(R.string.btnCancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .isCancelable(false)
                        .isCancelableOnTouchOutside(true)
                        .show();
            } else {
                ClosingTaskRequest request = new ClosingTaskRequest();
                request.setFlag(ClosingTaskRequest.CLOSING_TASK);

                ClosingTaskSender<ClosingTaskResponse> sender = new ClosingTaskSender<>(
                        activity, request, ClosingTaskResponse.class);
                sender.setListener(listener);
                sender.execute();
            }
        }
    }
}
