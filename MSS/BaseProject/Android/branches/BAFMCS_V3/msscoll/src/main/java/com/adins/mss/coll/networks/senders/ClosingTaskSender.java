package com.adins.mss.coll.networks.senders;

import android.app.Activity;
import android.app.ProgressDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.adapters.ClosingTaskAdapter;
import com.adins.mss.coll.fragments.ClosingTaskFragment;
import com.adins.mss.coll.networks.ClosingTaskListener;
import com.adins.mss.coll.networks.entities.ClosingTaskRequest;
import com.adins.mss.coll.networks.responses.ClosingTaskListResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.JsonParseException;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public class ClosingTaskSender<Response extends MssResponseType> extends DataTask<Void, Void, Response> implements IShowError {
    private Activity context;
    private ProgressDialog progressDialog;
    private Class<Response> responseClazz;
    private String flag;
    private ClosingTaskListener listener;
    private boolean isClosingTaskSuccess = false;
    NiftyDialogBuilder dialogBuilder;

    public ClosingTaskSender(Activity context, ClosingTaskRequest request, Class<Response> responseClazz) {
        super(context, request);
        this.context = context;
        this.flag = request.getFlag();
        this.responseClazz = responseClazz;
        dialogBuilder = NiftyDialogBuilder.getInstance(context);
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
                    errorMessage = context.getString(R.string.msgErrorParsingJson);
                } catch (Exception e) {
                    FireCrash.log(e);
                    errorMessage = serverResult.getResult();
                }
            } else {
                errorMessage = serverResult.getResult();
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

                    if (listener != null) {
                        listener.onClosingTaskSuccess();
                    }
                    //EventBusHelper.post(response);

                    isShowDialog = true;
                    isClosingTaskSuccess = true;
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
                        /*Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(ClosingTaskFragment.CLOSING_TASK_LIST,
                                (ArrayList<? extends Parcelable>) response1.getTaskList());*/

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
                                UpdateMenuUI();
                            }
                        }
                    });
            dialogBuilder.show();

            if (flag.equals(ClosingTaskRequest.CLOSING_TASK_LIST)) {
                if (context instanceof NewMainActivity) {
                    try {
                        Global.positionStack.remove(Global.positionStack.lastElement());
//                        ((MainMenuActivity) context).mDrawerListLeft.
//                                setItemChecked(Global.positionStack.lastElement(), true);
                        NewMainActivity.tempPosition = Global.positionStack.lastElement();
                    } catch (Exception ex) {
                        // empty
                    }
                }
            }
        }
    }

    private void UpdateMenuUI() {
        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                try {
                    if (MainMenuActivity.mnTaskList != null)
                        MainMenuActivity.mnTaskList.setCounter(String
                                .valueOf(ToDoList
                                        .getCounterTaskList(context)));
                    if (MainMenuActivity.mnLog != null)
                        MainMenuActivity.mnLog
                                .setCounter(String.valueOf(TaskLogImpl
                                        .getCounterLog(context)));
                    if (MainMenuActivity.menuAdapter != null)
                        MainMenuActivity.menuAdapter.notifyDataSetChanged();
                } catch (Exception e) {             FireCrash.log(e);
                    // TODO: handle exception
                }
            }
        });*/
        try {
//            MainMenuActivity.setDrawerCounter();
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }
    }

    public void setListener(ClosingTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String getUrl() {
        return GlobalData.getSharedGlobalData().getURL_CLOSING_TASK();
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){

        }
    }
}
