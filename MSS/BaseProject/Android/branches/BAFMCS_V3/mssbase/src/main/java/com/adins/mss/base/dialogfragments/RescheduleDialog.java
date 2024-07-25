package com.adins.mss.base.dialogfragments;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonRequestSubmitTask;
import com.adins.mss.base.dynamicform.JsonResponseSubmitTask;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.RescheduleFragment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.androidquery.AQuery;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by olivia.dg on 10/25/2017.
 */

public class RescheduleDialog extends DialogFragment {
    public static final String TASK_RESCHEDULE = "TASK_RESCHEDULE";
    protected View view;
    protected AQuery query;
    private String taskID;
    private String pts_date;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            date.setSeconds(0);
            SimpleDateFormat mFormatter = new SimpleDateFormat(Global.DATE_TIME_STR_FORMAT);
            SimpleDateFormat gsonFormatter = new SimpleDateFormat(Global.DATE_STR_FORMAT_GSON);
            String result = mFormatter.format(date);
            pts_date = gsonFormatter.format(date);
            query.id(R.id.txtDtm).text(result);
        }

        @Override
        public void onDateTimeCancel() {
            //EMPTY
        }
    };

    public RescheduleDialog() {
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED",
                getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        taskID = getArguments().getString("taskId");

        View view = inflater.inflate(R.layout.new_dialog_reschedule, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation2;
        getDialog().getWindow().setAttributes(wmlp);

        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        try {
            query = new AQuery(view);
            query.id(R.id.btnCancel).clicked(this, "cancel_Click");
            query.id(R.id.btnReschedule).clicked(this, "reschedule_Click");
            query.id(R.id.btnDtm).clicked(this, "dtm_Click");
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return view;
    }

    @Keep
    public void cancel_Click(View view) {
        dismiss();
    }

    @Keep
    public void reschedule_Click(View view) {
        if (query.id(R.id.txtDtm).getText().toString().length() == 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.select_reschedule_time),
                    Toast.LENGTH_SHORT).show();
        } else {
            Date dateSelected = null;
            try {
                dateSelected = Formatter.parseDate2(pts_date, Global.DATE_STR_FORMAT_GSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date nowtime = Tool.getSystemDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowtime);
            cal.add(Calendar.DATE, 1);
            nowtime = cal.getTime();
            if (dateSelected != null && dateSelected.before(nowtime)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.date_after_today),
                        Toast.LENGTH_SHORT).show();
            } else {
                new SubmitRescheduleTask().execute(taskID, pts_date);
            }
        }
    }

    @Keep
    public void dtm_Click(View view) {
        showDateTimePicker();
    }

    public void showDateTimePicker() {
        Date nowtime = Tool.getSystemDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowtime);
        cal.add(Calendar.DATE, 1);
        nowtime = cal.getTime();
        new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(nowtime)
                .setIs24HourTime(true)
                .build()
                .show();
    }

    private class SubmitRescheduleTask extends AsyncTask<String, Void, String> {
        String taskId;
        TaskH taskH;
        String size;
        String sec;
        String ptsDate;
        private ProgressDialog progressDialog;
        private String errMessage;

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (Tool.isInternetconnected(getActivity())) {
                try {
                    taskId = params[0];
                    ptsDate = params[1];
                    taskH = TaskHDataAccess.getOneTaskHeader(getActivity(), taskID);
                    SimpleDateFormat formatter = new SimpleDateFormat(Global.DATE_STR_FORMAT_GSON);
                    Date date = null;
                    try {
                        date = formatter.parse(ptsDate);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    taskH.setPts_date(date);
                    String uuidScheme = taskH.getUuid_scheme();
                    String uuidUser = taskH.getUuid_user();
                    TaskH h = TaskHDataAccess.getOneTaskHeader(getActivity(), taskId);
                    h.setUser(null);
                    h.setScheme(null);
                    h.setUuid_user(uuidUser);
                    h.setUuid_scheme(uuidScheme);

                    h.setSubmit_date(Tool.getSystemDateTime());

                    //olivia 28/11 : set submit location
                    LocationInfo loc = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
                    if ("null".equalsIgnoreCase(loc.getLatitude())) {
                        taskH.setLatitude("");
                    } else {
                        taskH.setLatitude(loc.getLatitude());
                    }

                    if ("null".equalsIgnoreCase(loc.getLongitude())) {
                        taskH.setLongitude("");
                    } else {
                        taskH.setLongitude(loc.getLongitude());
                    }

                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(h);
                    task.setTaskD(null);

                    String json = GsonHelper.toJson(task);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_RESCHEDULE();

                    size = String.valueOf(json.getBytes().length);
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;
                    Date startTime = Tool.getSystemDateTime();

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json,
                                Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if(Global.IS_DEV) {
                            e.printStackTrace();
                        }
                    }

                    if(serverResult == null){
                        errMessage = "Rescheduling task "
                                + taskH.getCustomer_name() + " " + getActivity().getString(R.string.failed);
                        return result;
                    }

                    if (serverResult.isOK()) {
                        String resultvalue = serverResult.getResult();
                        JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(
                                resultvalue, JsonResponseSubmitTask.class);
                        if (responseSubmitTask.getStatus().getCode() == 0) {
                            String status = responseSubmitTask.getResult();
                            Date finishTime = Tool.getSystemDateTime();
                            long time = finishTime.getTime() - startTime.getTime();
                            sec = String.valueOf((int) Math.ceil((double) time / 1000));
                            if (status == null)
                                status = "Success";
                            if (status.equalsIgnoreCase("Success")) {
                                result = status;
                                if (responseSubmitTask.getTaskId() != null)
                                    taskId = responseSubmitTask.getTaskId();
                            } else {
                                result = status;
                            }
                        } else {
                            if (Global.IS_DEV) {
                                if (responseSubmitTask.getStatus().getMessage() == null)
                                    System.out.println("AutoSendTaskThread server code :"
                                            + responseSubmitTask.getStatus().getCode());
                                else
                                    System.out.println("AutoSendTaskThread server code :"
                                            + responseSubmitTask.getStatus().getCode()
                                            + ":"
                                            + responseSubmitTask.getStatus().getCode());
                            }
                            result = String.valueOf(responseSubmitTask.getStatus()
                                    .getCode());
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    errMessage = "Rescheduling task "
                            + taskH.getCustomer_name() + " " + getActivity().getString(R.string.failed);
                }
            } else {
                errMessage = getString(R.string.no_internet_connection);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            if (errMessage != null) {
                Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
            } else {
                if (result != null && result.equalsIgnoreCase("Success")) {
                    if (taskId != null && taskId.length() > 0 && !taskId.contains("~")) {

                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                        ToDoList.removeSurveyFromList(taskId);

                        taskH.setSubmit_date(Tool.getSystemDateTime());
                        taskH.setSubmit_duration(sec);
                        taskH.setSubmit_size(size);
                        taskH.setSubmit_result(result);
                        taskH.setTask_id(taskId);
                        taskH.setLast_saved_question(1);
                        taskH.setIs_prepocessed(RescheduleFragment.TASK_RESCHEDULE);
                        TaskHDataAccess.addOrReplace(getActivity(), taskH);

                        TimelineManager.insertTimeline(getActivity(), taskH);

                        String txtResult = getActivity().getString(R.string.reschedule_success, taskH.getCustomer_name());

                        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                                .getInstance(getActivity());
                        dialogBuilder.withTitle(getActivity().getString(R.string.info_capital)).withMessage(txtResult)
                                .withButton1Text(getActivity().getString(R.string.btnOk))
                                .setButton1Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View paramView) {
                                        dialogBuilder.dismiss();
                                        dismiss();
                                        Fragment fragment = new NewTimelineFragment();
                                        FragmentTransaction transaction = NewMainActivity.fragmentManager
                                                .beginTransaction();
                                        transaction.setCustomAnimations(
                                                R.anim.fade_in, R.anim.fade_out);
                                        transaction.replace(R.id.content_frame,
                                                fragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                }).isCancelable(false).show();
                    }
                } else {
                    if(!GlobalData.isRequireRelogin()) {
                        String txtResult = "Rescheduling task "
                                + taskH.getCustomer_name() + " " + getActivity().getString(R.string.failed);
                        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
                                .getInstance(getActivity());
                        dialogBuilder.withTitle(getActivity().getString(R.string.warning_capital)).withMessage(txtResult)
                                .withButton1Text(getActivity().getString(R.string.btnClose))
                                .setButton1Click(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View paramView) {
                                        dialogBuilder.dismiss();
                                        dismiss();
                                    }
                                }).isCancelable(true).show();
                    }
                }
                try {
                    NewMainActivity.setCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "",
                    getActivity().getString(R.string.sending_reschedule_progress), true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //EMPTY
        }
    }
}
