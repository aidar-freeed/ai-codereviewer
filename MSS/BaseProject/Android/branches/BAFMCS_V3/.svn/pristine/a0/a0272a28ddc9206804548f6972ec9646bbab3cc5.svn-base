package com.adins.mss.base.timeline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.R.anim;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.tasklog.TaskLogListTask;
import com.adins.mss.base.timeline.activity.Timeline_Activity;
import com.adins.mss.base.timeline.comment.activity.CommentActivity;
import com.adins.mss.base.todolist.form.StatusSectionFragment;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.Timeline;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelineArrayAdapter extends ArrayAdapter<Timeline> {

    protected AQuery query;
    FragmentActivity mainActivity;
    Fragment endFragment;
    Fragment taskListFragment;
    Fragment logFragment;
    Fragment customerFragment;
    int content_frame;
    private Context mContext;
    private List<Timeline> objects = null;
    private int color = Color.BLACK;
    private ArrayList<Timeline> objectSort = new ArrayList<>();

    public TimelineArrayAdapter(Context context,
                                List<Timeline> objects) {
        super(context, R.layout.timeline_item_layout, objects);
        this.mContext = context;
        this.objects = objects;
        for (int i = objects.size() - 1; i >= 0; i--) {
            Timeline object = objects.get(i);
            objectSort.add(object);
        }

    }

    public TimelineArrayAdapter(FragmentActivity mainActivity, Fragment endFragment, int contentFrame,
                                List<Timeline> objects, int color) {
        super(mainActivity, R.layout.timeline_item_layout, objects);
        this.objects = objects;
        this.color = color;
        this.mContext = mainActivity;
        this.mainActivity = mainActivity;
        this.endFragment = endFragment;
        this.content_frame = contentFrame;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (objects.isEmpty())
            count = 1;
        else
            count = objects.size();
        return count;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timeline_item_layout, parent, false);
        }
        query = new AQuery(convertView);

        if (objects.isEmpty()) {
            getLayoutIfDataNotFound();
        } else {
            final String timeline_type;
            final String description;
            String str_dtm_crt = "";

            final Timeline timeline = objectSort.get(position);
            final String uuid_timeline = timeline.getUuid_timeline();
            description = timeline.getDescription();
            final String latitude = timeline.getLatitude();
            final String longitude = timeline.getLongitude();
            Date dtm_crt = timeline.getDtm_crt();
            Date today = Tool.getSystemDate();
            Date now = new Date();
            try {
                if (dtm_crt.before(today)) {
                    str_dtm_crt = Formatter.formatDate(dtm_crt, Global.DATE_TIMESEC_TIMELINE_FORMAT_OLD);
                } else {
                    long times = now.getTime() - dtm_crt.getTime();
                    if (times < 60000) {
                        str_dtm_crt = mContext.getString(R.string.moment_ago);
                    } else if (times < 3600000) {
                        times = times / 60000;
                        str_dtm_crt = times + " " + mContext.getString(R.string.minute_ago);
                    } else {
                        times = times / 3600000;
                        str_dtm_crt = times + " " + mContext.getString(R.string.hour_ago);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }
            final String uuid_task_h = timeline.getUuid_task_h();
            timeline_type = timeline.getTimelineType().getTimeline_type();
            byte[] byte_bitmap = timeline.getByte_image();
            Bitmap bm = null;
            if (byte_bitmap != null) {
                try {
                    bm = Utils.byteToBitmap(byte_bitmap);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

            String timeline_type_txt = "";
            if (timeline_type.equals(Global.TIMELINE_TYPE_APPROVAL)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_approval);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_APPROVED)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_approved);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_CHECKIN)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_checkin);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_MESSAGE)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_message);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_TASK)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_task);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_REJECTED)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_rejected);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_SUBMITTED)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_submitted);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_VERIFICATION)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_verification);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_VERIFIED)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_verified);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_UPLOADING)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_uploading);
            } else if (timeline_type.equals(Global.TIMELINE_TYPE_PENDING)) {
                timeline_type_txt = mContext.getString(R.string.timeline_type_pending);
            }
            query.id(R.id.txtTitle).text(timeline_type_txt).visibility(View.VISIBLE);
            query.id(R.id.txtDesc).text(description).visibility(View.VISIBLE);
            query.id(R.id.txtTime).text(str_dtm_crt).visibility(View.VISIBLE);
            query.id(R.id.buttonLayout).visibility(View.GONE);
            query.id(R.id.txtComment).visibility(View.VISIBLE);

            try {
                if (Global.TIMELINE_TYPE_CHECKIN.equals(timeline_type) || Global.TIMELINE_TYPE_CHECKOUT.equals(timeline_type)) {
                    try {
                        if (bm != null)
                            query.id(R.id.timelineImage).visibility(View.VISIBLE).image(bm);
                        else
                            query.id(R.id.timelineImage).visibility(View.VISIBLE).image(R.drawable.img_notavailable);
                    } catch (Exception e) {
                        FireCrash.log(e);

                    }
                } else {
                    query.id(R.id.timelineImage).visibility(View.GONE);
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }


            setTitleClick(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (MainMenuActivity.Force_Uninstall) {
                        DialogManager.UninstallerHandler(mainActivity);
                    } else {
                        if (Global.TIMELINE_TYPE_MESSAGE.equals(timeline_type)) {
                            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                            dialogBuilder.withTitle(timeline_type)
                                    .withMessage(description)
                                    .show();
                        } else if (isLog(timeline_type)) {
                            try {
                                TaskH taskH = timeline.getTaskH();
                                String taskId = taskH.getTask_id();
                                taskH = TaskHDataAccess.getOneTaskHeader(mContext, taskId);
                                String status = taskH.getStatus();
                                if (Global.TIMELINE_TYPE_PENDING.equals(timeline_type) || Global.TIMELINE_TYPE_UPLOADING.equals(timeline_type) && status.equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                    Fragment fragment = new Fragment();
                                    Bundle argument = new Bundle();
                                    if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                                        fragment = new StatusSectionFragment();
                                    } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) || Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                                        fragment = new TaskListFragment_new();
                                        argument.putString("status", "failed");
                                    }
                                    try {
                                        fragment.setArguments(argument);
                                        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                        transaction.replace(R.id.content_frame, fragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                    }
                                } else {
                                    TaskLogListTask task = new TaskLogListTask(mainActivity, getContext().getString(R.string.progressWait),
                                            getContext().getString(R.string.msgNoSent), content_frame, new LogResultActivity());
                                    task.execute();
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                        .withIcon(android.R.drawable.ic_dialog_info)
                                        .withMessage(mContext.getString(R.string.task_not_available))
                                        .show();
                            }
                        } else if (Global.TIMELINE_TYPE_TASK.equals(timeline_type)) {
                            try {
                                Fragment fragment = new TaskListFragment_new();
                                Bundle argument = new Bundle();
                                argument.putBoolean(Global.BUND_KEY_ISERROR, false);
                                argument.putString(Global.BUND_KEY_MESSAGE, "");
                                fragment.setArguments(argument);
                                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                transaction.replace(R.id.content_frame, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        } else if (Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type)) {
                            try {
                                if (Timeline_Activity.haveDoubleMenuApproval) {
                                    Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                transaction.replace(R.id.content_frame, fragment1);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                        } else if (Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type)) {
                            try {
                                if (Timeline_Activity.haveDoubleMenuVerify) {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(anim.activity_open_translate, anim.activity_close_scale);
                                transaction.replace(R.id.content_frame, fragment1);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        }
                    }

                }
            });

            setDescriptionClick(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (MainMenuActivity.Force_Uninstall) {
                        DialogManager.UninstallerHandler(mainActivity);
                    } else {
                        if (Global.TIMELINE_TYPE_MESSAGE.equals(timeline_type)) {
                            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                            dialogBuilder.withTitle(timeline_type)
                                    .withMessage(description)
                                    .show();
                        } else if (isLog(timeline_type)) {
                            TaskH taskH = timeline.getTaskH();
                            if (taskH != null) {
                                try {
                                    String taskId = taskH.getTask_id();
                                    taskH = TaskHDataAccess.getOneTaskHeader(mContext, taskId);
                                    if (taskH.getScheme() != null) {
                                        SurveyHeaderBean header = new SurveyHeaderBean(taskH);
                                        Fragment fragment = CustomerFragment.create(header);
                                        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                        transaction.replace(R.id.content_frame, fragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                        MainMenuActivity.tempPosition = 0;
                                    } else {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                                .withIcon(android.R.drawable.ic_dialog_info)
                                                .withMessage(mContext.getString(R.string.task_cant_seen))
                                                .show();
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                    dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                            .withIcon(android.R.drawable.ic_dialog_info)
                                            .withMessage(mContext.getString(R.string.task_not_available))
                                            .show();
                                }
                            } else {
                                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                        .withIcon(android.R.drawable.ic_dialog_info)
                                        .withMessage(mContext.getString(R.string.task_not_available))
                                        .show();
                            }
                        } else if (Global.TIMELINE_TYPE_TASK.equals(timeline_type) ||
                                Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type) ||
                                Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type)) {
                            TaskH taskH = timeline.getTaskH();
                            try {

                                String taskId = taskH.getTask_id();

                                TaskH nTaskH = TaskHDataAccess.getOneTaskHeader(mContext, taskId);
                                if (Global.TIMELINE_TYPE_APPROVAL.equals(timeline_type))
                                    nTaskH = TaskHDataAccess.getOneHeader(mContext, uuid_task_h);

                                String appl = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                if (Global.TIMELINE_TYPE_TASK.equals(timeline_type) && Global.APPLICATION_COLLECTION.equalsIgnoreCase(appl)) {
                                    String cashLimit = GlobalData.getSharedGlobalData().getUser().getCash_limit();
                                    double limit = cashLimit != null ? Double.parseDouble(cashLimit) : 0.0;
                                    String coh = GlobalData.getSharedGlobalData().getUser().getCash_on_hand();
                                    double cashOnHand = coh != null ? Double.parseDouble(coh) : 0.0;
                                    if (isCOHAktif() && limit > 0 && cashOnHand > limit) {
                                        DialogManager.showAlertNotif(mContext, mContext.getString(R.string.limit_coh), "Cash On Hand");
                                        return;
                                    }
                                }

                                if (nTaskH != null) {
                                    if (nTaskH.getScheme() != null) {
                                        SurveyHeaderBean header = new SurveyHeaderBean(nTaskH);
                                        Fragment fragment = CustomerFragment.create(header);
                                        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                        transaction.replace(R.id.content_frame, fragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                        MainMenuActivity.tempPosition = 0;
                                    } else {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                                .withIcon(android.R.drawable.ic_dialog_info)
                                                .withMessage(mContext.getString(R.string.task_cant_seen))
                                                .show();
                                    }
                                } else {
                                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                    if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_reassign_svy))
                                                .show();
                                    } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_reassign_col))
                                                .show();
                                    } else {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_not_available2))
                                                .show();
                                    }
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (MainMenuActivity.mnSVYApproval != null) {
                                    if (Global.TIMELINE_TYPE_VERIFICATION.equals(timeline_type)) {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                                .withIcon(android.R.drawable.ic_dialog_info)
                                                .withMessage(mContext.getString(R.string.task_verified))
                                                .show();
                                    }
                                } else {
                                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                    if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_reassign_svy))
                                                .show();
                                    } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_reassign_col))
                                                .show();
                                    } else {
                                        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                                        dialogBuilder.withTitle(mContext.getString(R.string.warning_capital))
                                                .withIcon(android.R.drawable.ic_dialog_alert)
                                                .withMessage(mContext.getString(R.string.task_not_available2))
                                                .show();
                                    }
                                }
                            }
                        } else if (Global.TIMELINE_TYPE_APPROVED.equals(timeline_type)) {
                            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                            dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                    .withIcon(android.R.drawable.ic_dialog_info)
                                    .withMessage(mContext.getString(R.string.task_approved_view))
                                    .show();
                        } else if (Global.TIMELINE_TYPE_REJECTED.equals(timeline_type)) {
                            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                            dialogBuilder.withTitle(mContext.getString(R.string.info_capital))
                                    .withIcon(android.R.drawable.ic_dialog_info)
                                    .withMessage(mContext.getString(R.string.task_rejected_view))
                                    .show();
                        }
                    }

                }
            });
            setImageClick(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (MainMenuActivity.Force_Uninstall) {
                        DialogManager.UninstallerHandler(mainActivity);
                    } else {
                        if (Global.TIMELINE_TYPE_CHECKIN.equals(timeline_type) || Global.TIMELINE_TYPE_CHECKOUT.equals(timeline_type)) {
                            Intent intent = new Intent(mContext, MapsViewer.class);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
            setCommentClick(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (MainMenuActivity.Force_Uninstall) {
                        DialogManager.UninstallerHandler(mainActivity);
                    } else {
                        Intent mIntent = new Intent(mainActivity, CommentActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString(Constants.KEY_TIMELINE, uuid_timeline);
                        mIntent.putExtras(mBundle);
                        mainActivity.startActivity(mIntent);
                    }
                }
            });
        }
        return convertView;
    }

    /**
     * Method for set OnclickListener in Title of Timeline
     *
     * @param click - View OnclickListener
     * @return
     */
    public TimelineArrayAdapter setTitleClick(View.OnClickListener click) {
        query.id(R.id.txtTitle).clicked(click);
        return this;
    }

    private boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(mContext, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
    }

    /**
     * Method for set OnclickListener in Image of Timeline
     *
     * @param click - View OnclickListener
     * @return
     */
    public TimelineArrayAdapter setImageClick(View.OnClickListener click) {
        query.id(R.id.timelineImage).clicked(click);
        return this;
    }

    /**
     * Method for set OnclickListener in Description of Timeline
     *
     * @param click - View OnclickListener
     * @param click
     * @return
     */
    public TimelineArrayAdapter setDescriptionClick(View.OnClickListener click) {
        query.id(R.id.txtDesc).clicked(click);
        return this;
    }

    /**
     * Method for set OnclickListener in Button Comment of Timeline
     *
     * @param click - View OnclickListener
     * @return
     */
    public TimelineArrayAdapter setCommentClick(View.OnClickListener click) {
        query.id(R.id.txtComment).clicked(click);
        return this;
    }

    public TimelineArrayAdapter setTaskListFragment(Fragment taskListFragment) {
        this.taskListFragment = taskListFragment;
        return this;
    }

    public TimelineArrayAdapter setLogFragment(Fragment logFragment) {
        this.logFragment = logFragment;
        return this;
    }

    public TimelineArrayAdapter setCustomerFragment(Fragment customerFragment) {
        this.customerFragment = customerFragment;
        return this;
    }

    public TimelineArrayAdapter withColor(int color) {
        this.color = color;
        return this;
    }

    public TimelineArrayAdapter setContentFrame(int contentFrame) {
        this.content_frame = contentFrame;
        return this;
    }

    public TimelineArrayAdapter setMainFragment(FragmentActivity mainActivity) {
        this.mainActivity = mainActivity;
        return this;
    }

    public void getLayoutIfDataNotFound() {
        query.id(R.id.txtTitle).visibility(View.GONE);
        query.id(R.id.txtDesc).text(R.string.data_not_found).visibility(View.VISIBLE);
        query.id(R.id.txtTime).visibility(View.GONE);
        query.id(R.id.buttonLayout).visibility(View.GONE);
        query.id(R.id.txtComment).visibility(View.GONE);
        query.id(R.id.timelineImage).visibility(View.GONE);
    }

    public void setData(List<Timeline> data) {
        clear();
        if (data != null) {
            objects = data;
            for (int i = objects.size() - 1; i >= 0; i--) {
                Timeline object = objects.get(i);
                objectSort.add(object);
            }
        }
    }

    public boolean isLog(String timeline_type) {
        return Global.TIMELINE_TYPE_SUBMITTED.equals(timeline_type)
                || Global.TIMELINE_TYPE_VERIFIED.equals(timeline_type)
                || Global.TIMELINE_TYPE_APPROVED.equals(timeline_type)
                || Global.TIMELINE_TYPE_REJECTED.equals(timeline_type)
                || Global.TIMELINE_TYPE_PENDING.equals(timeline_type)
                || Global.TIMELINE_TYPE_UPLOADING.equals(timeline_type);
    }
}
