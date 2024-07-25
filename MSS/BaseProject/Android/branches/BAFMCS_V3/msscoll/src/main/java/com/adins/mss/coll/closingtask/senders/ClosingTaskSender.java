package com.adins.mss.coll.closingtask.senders;

import android.app.Activity;
import android.app.ProgressDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.closingtask.ClosingTaskAdapter;
import com.adins.mss.coll.closingtask.ClosingTaskFragment;
import com.adins.mss.coll.closingtask.models.ClosingTaskListResponse;
import com.adins.mss.coll.closingtask.models.ClosingTaskRequest;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.JsonParseException;
import com.services.MainServices;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public class ClosingTaskSender<Response extends MssResponseType> extends DataTask<Void, Void, Response> {
    private Activity context;
    private ProgressDialog progressDialog;
    private Class<Response> responseClazz;
    private String flag;
    private ClosingTaskListener listener;
    private boolean isClosingTaskSuccess = false;

    public ClosingTaskSender(Activity context, ClosingTaskRequest request, Class<Response> responseClazz) {
        super(context, request);
        this.context = context;
        this.flag = request.getFlag();
        this.responseClazz = responseClazz;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "", context.getString(R.string.please_wait), true, false);
    }

    @Override
    protected Response onBackgroundResult(HttpConnectionResult serverResult) {
        Response resultBean = null;

        if (serverResult != null) {
            log(serverResult.getResult());
            if (serverResult.isOK()) {
                try {
                    resultBean = GsonHelper.fromJson(serverResult.getResult(), responseClazz);
                } catch (JsonParseException e) {
                    errorMessage = context.getString(R.string.jsonParseFailed);
                } catch (Exception e) {
                    FireCrash.log(e);
                    errorMessage = context.getString(R.string.jsonParseFailed);
                }
            } else {
                errorMessage = context.getString(R.string.jsonParseFailed);
            }
        }

        return resultBean;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        boolean isShowDialog = false;

        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);

        if (!GlobalData.isRequireRelogin()) {
            if (errorMessage != null) {
                dialogBuilder.withTitle(context.getString(R.string.error_capital))
                        .withMessage(errorMessage);
                isShowDialog = true;
            } else if (response == null) {
                dialogBuilder.withTitle(context.getString(R.string.error_capital))
                        .withMessage(context.getString(R.string.empty_data));
                isShowDialog = true;
            } else {
                if (response.getStatus().getCode() == 0) {
                    if (flag.equals(ClosingTaskRequest.CLOSING_TASK)) {
                        dialogBuilder.withTitle(context.getString(R.string.info_capital))
                                .withMessage(response.getStatus().getMessage());

                        // delete all rv number
                        ReceiptVoucherDataAccess.clean(context);
                        LookupDataAccess.deleteByLovGroup(context, "RV NUMBER");

    //                    List<TaskH> processedTask = new ArrayList<>();
                        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                        TaskHDataAccess.updateStatusClosingTask(context , TaskHDataAccess.STATUS_SEND_DELETED, uuidUser);

    //                    processedTask = TaskHDataAccess.getAllTaskByStatusNewAndDownload(context, uuidUser);
    //                    for (int x = 0; x < processedTask.size(); x++){
    //                        TaskHDataAccess.updateStatus(context, processedTask.get(x), TaskHDataAccess.STATUS_SEND_DELETED);
    //                    }

                        if (listener != null) {
                            listener.onClosingTaskSuccess();
                        }
                        //EventBusHelper.post(response);

                        isShowDialog = true;
                        isClosingTaskSuccess = true;
                        EventBusHelper.post(MainServices.ACTION_GET_NOTIFICATION_THREAD);
                    } else if (flag.equals(ClosingTaskRequest.CLOSING_TASK_LIST)) {
                        ClosingTaskListResponse response1 = (ClosingTaskListResponse) response;

                        if (response1.getTaskList() == null || response1.getTaskList().size() == 0) {
                            String message = response1.getStatus().getMessage();
                            if (message == null || message.isEmpty()) {
                                message = context.getString(R.string.msgNoSurvey);
                            }

                            dialogBuilder.withTitle(context.getString(R.string.info_capital)).withMessage(message);
                            isShowDialog = true;
                        } else {
                            ClosingTaskAdapter.getInstance().clear();
                            ClosingTaskAdapter.getInstance().notifyDataSetChanged();
                            ClosingTaskAdapter.getInstance().processData(response1.getTaskList());

                            if (!context.isFinishing()) {
                                Fragment fragment = ClosingTaskFragment.newInstance();
                                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                transaction.replace(R.id.content_frame, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                isShowDialog = false;
                            }
                        }
                    }
                } else {
                    dialogBuilder.withTitle(context.getString(R.string.error_capital))
                            .withMessage(response.getStatus().getMessage());
                    isShowDialog = true;
                }
            }

            if (flag.equals(ClosingTaskRequest.CLOSING_TASK) || isShowDialog) {
                dialogBuilder.isCancelable(false);
                dialogBuilder.isCancelableOnTouchOutside(false);
                dialogBuilder.withButton1Text(context.getString(R.string.btnClose)).
                        setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View paramView) {
                                dialogBuilder.dismiss();

                                if (isClosingTaskSuccess) {
                                    context.onBackPressed();
                                }
                            }
                        });
                dialogBuilder.show();
        }
            //Fix Me
//            if (flag.equals(ClosingTaskRequest.CLOSING_TASK_LIST)) {
//                if (context instanceof MainMenuActivity) {
//                    try {
//                        Global.positionStack.remove(Global.positionStack.lastElement());
//                        ((MainMenuActivity) context).mDrawerListLeft.
//                                setItemChecked(Global.positionStack.lastElement(), true);
//                        MainMenuActivity.tempPosition = Global.positionStack.lastElement();
//                    } catch (Exception ex) {
//                        // empty
//                    }
//                }
//            }
        }
    }

    public void setListener(ClosingTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String getUrl() {
        return GlobalData.getSharedGlobalData().getURL_CLOSING_TASK();
    }
}
