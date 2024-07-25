package com.adins.mss.base.timeline.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.timeline.TimelineArrayAdapter;
import com.adins.mss.base.timeline.TimelineImpl;
import com.adins.mss.base.timeline.TimelineListener;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.StatusTabFragment;
import com.adins.mss.base.todolist.form.TaskListTask;
import com.adins.mss.base.util.CustomAnimatorLayout;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder_PL;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.CroppingImageActivity;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.androidquery.AQuery;
import com.soundcloud.android.crop.Crop;

import org.acra.ACRA;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Timeline_Activity extends Fragment implements TimelineListener
//implements OnRefreshListener
{
    public static final int SIMULATED_REFRESH_LENGTH = 5000;
    public static final String ARG_IMAGE_RES = "image_source";
    public static TimelineArrayAdapter arrayAdapter;
    //search
    public static RelativeLayout searchLayout;
    public static boolean haveDoubleMenuVerify = true;
    public static boolean haveDoubleMenuApproval = true;
    public static TimelineHandler timelineHandler;
    private static AQuery query;
    private static Context context;
    private static boolean isTimelineOpen = false;
    TimelineManager manager;
    View view;
    AutoCompleteTextView completeTextView;
    ArrayAdapter<String> adapter;
    List<String> searchDescription = new ArrayList<>();
    private List<Timeline> objects = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TimelineImpl timelineImpl;

    public static TimelineHandler getTimelineHandler() {
        return Timeline_Activity.timelineHandler;
    }

    public static void setTimelineHandler(TimelineHandler timelineHandler) {
        Timeline_Activity.timelineHandler = timelineHandler;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        timelineImpl = new TimelineImpl(getActivity(), this);
        timelineImpl.setContext(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.timeline_content_layout, container,
                false);
        if (view instanceof RelativeLayout) {
            context = view.getContext();
            Timeline_Activity.setTimelineHandler(new TimelineHandler());
            try {
                manager = new TimelineManager(getActivity());
                int range = GlobalData.getSharedGlobalData().getKeepTimelineInDays();
                objects = TimelineManager.getAllTimelineWithLimitedDay(getActivity(), range);

                arrayAdapter = new TimelineArrayAdapter(context, objects);
                arrayAdapter.setMainFragment(getActivity())
                        .withColor(getResources().getColor(R.color.tv_normal))
                        .setContentFrame(R.id.content_frame)
                        .setLogFragment(new LogResultActivity());

                for (Timeline timeline : objects) {
                    searchDescription.add(timeline.getDescription());
                }

                boolean haveMnTasklist = MainMenuActivity.mnTaskList != null;
                boolean haveMnVerificationlist = MainMenuActivity.mnSVYVerify != null;
                boolean haveMnApprovallist = MainMenuActivity.mnSVYApproval != null;
                boolean haveMnVerificationlistByBranch = MainMenuActivity.mnSVYVerifyByBranch != null;
                boolean haveMnApprovallistByBranch = MainMenuActivity.mnSVYApprovalByBranch != null;
                if (haveMnApprovallistByBranch && !haveMnApprovallist) {
                    haveDoubleMenuApproval = false;
                }
                if (haveMnVerificationlistByBranch && !haveMnVerificationlist) {
                    haveDoubleMenuVerify = false;
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorOnCreateView", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorOnCreateView", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Data inflate"));
            }

            query = new AQuery(view);
            searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);

            String temp_uuid_user;
            try {
                temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get UUID User"));
                MainMenuActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());
                try {
                    temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                } catch (Exception e2) {
                    ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get UUID User"));
                    ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getActivity(),
                            "GlobalData", Context.MODE_PRIVATE);
                    temp_uuid_user = sharedPref.getString("UUID_USER", "");
                }
            }
            User user = UserDataAccess.getOne(context, temp_uuid_user);
            if (user == null) {
                String message = getActivity().getString(R.string.data_corrupt);
                DialogManager.showForceExitAlert(getActivity(), message);
                return view;
            }

            query.id(R.id.btnSearch).clicked(this, "searchContent");
            query.id(R.id.edit_header).clicked(this, "changeHeader");
            query.id(R.id.editProfile).clicked(this, "changeProfile");

            query.id(R.id.tmCheckin).clicked(this, "tmCheckIn");
            query.id(R.id.tmSubmit).clicked(this, "tmSubmitted");
            query.id(R.id.tmServerTask).clicked(this, "tmTask");

            query.id(R.id.tmMessage).visibility(View.GONE);

            query.id(R.id.txtName).text(user.getFullname());
            query.id(R.id.txtJob).text(user.getJob_description());

            setCashOnHandUI();

            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
            if (application == null)
                application = GlobalData.getSharedGlobalData().getApplication();
            if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                if (user.getIs_branch() == null) {
                    query.id(R.id.txtDealer).text(user.getDealer_name());
                } else {
                    if (user.getIs_branch().equals("0")) {
                        query.id(R.id.txtDealer).text(user.getDealer_name());
                    } else {
                        query.id(R.id.txtDealer).text(user.getBranch_name());
                    }
                }
                query.id(R.id.tmCheckin).visibility(View.GONE);
            } else {
                query.id(R.id.tmCheckin).visibility(View.VISIBLE);
                query.id(R.id.txtDealer).text(user.getBranch_name());
            }

            query.id(R.id.includeOut).clicked(this, "outstandingTask");
            if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                query.id(R.id.txtOutstanding).text("Order Progress");
            }

            try {
                long taskListCounter = ToDoList.getAllCounter(getActivity());
                query.id(R.id.txtJumlahOutstanding).text(String.valueOf(taskListCounter));
                if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                    long statuslistCounter = ToDoList.getCounterStatusTask(getActivity());
                    query.id(R.id.txtJumlahOutstanding).text(String.valueOf(statuslistCounter));
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorSetTaskListLength", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorSetTaskListLength", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
                query.id(R.id.txtJumlahOutstanding).text("0");
            }
            query.id(android.R.id.list).adapter(arrayAdapter);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshtimeline);

            try {
                mSwipeRefreshLayout.setColorSchemeColors(
                        getResources().getColor(R.color.tv_gray),
                        getResources().getColor(R.color.tv_light),
                        getResources().getColor(R.color.tv_normal),
                        getResources().getColor(R.color.tv_darker));
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorsetColorScheme", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorsetColorScheme", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Color Scheme"));
                if (Global.IS_DEV)
                    e.printStackTrace();
            }

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    initiateRefresh();
                }
            });
            mSwipeRefreshLayout.setRefreshing(true);
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initiateRefresh();
                }
            }, 2000);
        }
        return view;
    }

    @Override
    public void onSuccessBackgroundTask(List<Timeline> timelines) {
        onRefreshComplete(timelines);
    }

    @Override
    public void onSuccessImageBitmap(Bitmap bitmap, int imageView, int defaultImage) {
        if (bitmap != null) query.id(imageView).image(bitmap);
        else query.id(imageView).image(defaultImage);
    }

    private void setCashOnHandUI() {
        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
            query.id(R.id.txtCOH).visibility(View.VISIBLE);
            timelineImpl.setCashOnHand();

            if (timelineImpl.getLimit() > 0 && timelineImpl.isCOHAktif())
                query.id(R.id.txtCOH).text(getActivity().getString(R.string.lblCashOnHand, timelineImpl.getsCOH()));
            else
                query.id(R.id.txtCOH).text(getActivity().getString(R.string.lblCashOnHand_wo_limit,
                        timelineImpl.getsCOH()));
        }
    }

    private void initiateRefresh() {
        try {

            timelineImpl.refreshBackgroundTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorRefresh", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorRefresh", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            Log.d("refresh_test", "2: " + e.getMessage());
        }
    }

    private void onRefreshComplete(List<Timeline> result) {
        // Remove all items from the ListAdapter, and then replace them with the new items
        final List<Timeline> newResult = result;
        try {
            if (isAdded() || arrayAdapter == null) {
                arrayAdapter = new TimelineArrayAdapter(context, newResult);
                arrayAdapter.setMainFragment(getActivity())
                        .withColor(getResources().getColor(R.color.tv_normal))
                        .setContentFrame(R.id.content_frame)
                        .withColor(getResources().getColor(R.color.tv_normal))
                        .setLogFragment(new LogResultActivity());
            }
            arrayAdapter.notifyDataSetChanged();
            query.id(android.R.id.list).adapter(arrayAdapter);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", e.getMessage());
            ACRA.getErrorReporter().putCustomData("ErrorOnRefreshCompleted", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Notify Data Set Changed"));
        }

        // Stop the refreshing indicator
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                try {
                    setCashOnHandUI();

                    try {
                        MainMenuActivity.setDrawerCounter();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", Tool.getSystemDateTime().toLocaleString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
                    }

                    long taskListCounter = ToDoList.getAllCounter(context);
                    Timeline_Activity.query.id(R.id.txtJumlahOutstanding).text(String.valueOf(taskListCounter));
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat Set Cash UI"));
                }
            }
        });

        Utility.freeMemory();
    }

    @Keep
    public void changeHeader(View view) {
        final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
        dialogBuilder.withTitle(getString(com.adins.mss.base.R.string.header__title))
                .withNoMessage()
                .withButton1Text(getString(com.adins.mss.base.R.string.btn_camera)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.flag_edit = 0;
                dialogBuilder.dismiss();
                final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(context.getExternalFilesDir(null) + File.separator + "imgHeader.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        startActivityForResult(intent, Utils.REQUEST_CAMERA);
                    }

                }, 100);

            }
        })
                .withButton2Text(getString(com.adins.mss.base.R.string.btn_galery)).setButton2Click(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Constants.flag_edit = 0;
                dialogBuilder.dismiss();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Crop.pickImage(context, Timeline_Activity.this);
                    }

                }, 100);
            }
        })
                .isCancelable(false)
                .isCancelableOnTouchOutside(true)
                .show();
    }

    @Keep
    public void changeProfile(View view) {
        final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
        dialogBuilder.withTitle(getString(com.adins.mss.base.R.string.profile__title))
                .withNoMessage()
                .withButton1Text(getString(com.adins.mss.base.R.string.btn_camera)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.flag_edit = 1;
                dialogBuilder.dismiss();
                final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(context.getExternalFilesDir(null) + File.separator + "imgProfile.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        startActivityForResult(intent, Utils.REQUEST_CAMERA);
                    }

                }, 100);
            }
        })
                .withButton2Text(getString(com.adins.mss.base.R.string.btn_galery)).setButton2Click(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Constants.flag_edit = 1;
                dialogBuilder.dismiss();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Crop.pickImage(context, Timeline_Activity.this);
                    }

                }, 100);
            }
        })
                .isCancelable(false)
                .isCancelableOnTouchOutside(true)
                .show();
    }

    @Keep
    public void searchContent(View view) {
        String value = completeTextView.getText().toString();

        objects.clear();
        try {
            Timeline timeline = TimelineManager.getTimelineByDescription(getActivity(), value);
            if (timeline != null)
                objects.add(timeline);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorSearchContent", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorSearchContent", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getTimeline by description"));
        }
        closeSearch(objects);

    }

    @Keep
    public void tmSubmitted(View view) {
        try {
            objects = manager.getAllTimelineByType(getActivity(), Global.TIMELINE_TYPE_SUBMITTED);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorSearchContent", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorSearchContent", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getTimeline by submitted"));
        }
        closeSearch(objects);
    }

    @Keep
    public void tmTask(View view) {
        try {
            objects = manager.getAllTimelineByType(getActivity(), Global.TIMELINE_TYPE_TASK);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorSearchContent", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorSearchContent", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getTimeline by task"));
        }
        closeSearch(objects);
    }

    public void tmMessage(View view) {
        try {
            objects = manager.getAllTimelineByType(getActivity(), Global.TIMELINE_TYPE_MESSAGE);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorSearchContent", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorSearchContent", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getTimeline by message"));
        }
        closeSearch(objects);
    }

    @Keep
    public void tmCheckIn(View view) {
        try {
            objects = manager.getAllTimelineByType(getActivity(), Global.TIMELINE_TYPE_CHECKIN);
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorSearchContent", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorSearchContent", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat getTimeline by checkIN"));
        }
        closeSearch(objects);
    }

    @Keep
    public void outstandingTask(View view) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                    Fragment fragment = new StatusTabFragment();
                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                    boolean haveMnTasklist = MainMenuActivity.mnTaskList != null;
                    boolean haveMnVerificationlist = MainMenuActivity.mnSVYVerify != null;
                    boolean haveMnApprovallist = MainMenuActivity.mnSVYApproval != null;
                    boolean haveMnVerificationlistByBranch = MainMenuActivity.mnSVYVerifyByBranch != null;
                    boolean haveMnApprovallistByBranch = MainMenuActivity.mnSVYApprovalByBranch != null;
                    boolean haveMnAssignment = MainMenuActivity.mnSVYAssignment != null;
                    HashMap<String, Boolean> listmenu = new HashMap<>();
                    if (haveMnTasklist)
                        listmenu.put("haveMnTasklist", haveMnTasklist);
                    if (haveMnVerificationlist)
                        listmenu.put("haveMnVerificationlist", haveMnVerificationlist);
                    if (haveMnApprovallist)
                        listmenu.put("haveMnApprovallist", haveMnApprovallist);
                    if (haveMnVerificationlistByBranch)
                        listmenu.put("haveMnVerificationlistByBranch", haveMnVerificationlistByBranch);
                    if (haveMnApprovallistByBranch)
                        listmenu.put("haveMnApprovallistByBranch", haveMnApprovallistByBranch);
                    if (haveMnAssignment)
                        listmenu.put("haveMnAssignment", haveMnAssignment);
                    setMenuOutStanding(listmenu);

                } else {
                    TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                            getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                    task.execute();
                }
            }

        });


    }

    private void setMenuOutStanding(HashMap<String, Boolean> listmenu) {
        boolean haveMnTasklist = false;
        boolean haveMnVerificationlist = false;
        boolean haveMnApprovallist = false;
        boolean haveMnVerificationlistByBranch = false;
        boolean haveMnApprovallistByBranch = false;
        boolean haveMnAssignment = false;
        if (listmenu != null) {
            haveMnTasklist = listmenu.get("haveMnTasklist") != null ? listmenu.get("haveMnTasklist") : false;
            haveMnVerificationlist = listmenu.get("haveMnVerificationlist") != null ? listmenu.get("haveMnVerificationlist") : false;
            haveMnApprovallist = listmenu.get("haveMnApprovallist") != null ? listmenu.get("haveMnApprovallist") : false;
            haveMnVerificationlistByBranch = listmenu.get("haveMnVerificationlistByBranch") != null ? listmenu.get("haveMnVerificationlistByBranch") : false;
            haveMnApprovallistByBranch = listmenu.get("haveMnApprovallistByBranch") != null ? listmenu.get("haveMnApprovallistByBranch") : false;
            haveMnAssignment = listmenu.get("haveMnAssignment") != null ? listmenu.get("haveMnAssignment") : false;
            if (listmenu.size() == 1) {
                if (haveMnVerificationlist) {
                    final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
                    dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                            .withNoMessage()
                            .withVerificationListChoice()
                            .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                            .setVerificationListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .withTaskListChoice()
                            .withStatusListTitle()
                            .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                            .setTaskListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getStatusFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .isCancelable(false)
                            .isCancelableOnTouchOutside(true)
                            .show();
                } else if (haveMnVerificationlistByBranch) {
                    final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
                    dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                            .withNoMessage()
                            .withVerificationListChoiceByBranch()
                            .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                            .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .withTaskListChoice()
                            .withStatusListTitle()
                            .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                            .setTaskListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getStatusFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .isCancelable(false)
                            .isCancelableOnTouchOutside(true)
                            .show();
                } else if (haveMnApprovallistByBranch) {
                    final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
                    dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                            .withNoMessage()
                            .withApprovalListChoiceByBranch()
                            .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                            .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .withTaskListChoice()
                            .withStatusListTitle()
                            .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                            .setTaskListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getStatusFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            })
                            .isCancelable(false)
                            .isCancelableOnTouchOutside(true)
                            .show();
                } else {
                    TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                            getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                    task.execute();
                }
            } else {
                final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
                dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                        .withNoMessage();
                if (haveMnApprovallist) {
                    dialogBuilder.withApprovalListChoice()
                            .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                            .setApprovalListChoiceClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                if (haveMnVerificationlist) {
                    dialogBuilder.withVerificationListChoice()
                            .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                            .setVerificationListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                if (haveMnTasklist) {
                    dialogBuilder.withTaskListChoice()
                            .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                            .setTaskListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                            getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                                    task.execute();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                if (haveMnApprovallistByBranch) {
                    dialogBuilder.withApprovalListChoiceByBranch()
                            .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                            .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                if (haveMnVerificationlistByBranch) {
                    dialogBuilder.withVerificationListChoiceByBranch()
                            .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                            .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                                    FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                    transaction.replace(R.id.content_frame, fragment1);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                if (haveMnAssignment) {
                    dialogBuilder.withAssignmentListChoice()
                            .withAssignmentListTitleAndCounter(String.valueOf(ToDoList.getCounterAssignment(getActivity())))
                            .setAssignmentListChoiceClick(new View.OnClickListener() {

                                @Override
                                public void onClick(View paramView) {
                                    MainMenuActivity.gotoSurveyAssignmentTask();
                                    dialogBuilder.dismiss();
                                }
                            });
                }
                dialogBuilder.isCancelable(false)
                        .isCancelableOnTouchOutside(true)
                        .show();
            }
        }
    }

    private void setMenuOutStanding(boolean haveMnApprovallist, boolean haveMnTasklist, boolean haveMnVerificationlist, boolean haveMnApprovallistByBranch, boolean haveMnVerificationlistByBranch) {
        if (haveMnApprovallist && haveMnTasklist && haveMnVerificationlist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnVerificationlist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnVerificationlist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnVerificationlist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnTasklist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnVerificationlist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnVerificationlist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnVerificationlist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnVerificationlist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnVerificationlist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnVerificationlist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnVerificationlist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnVerificationlist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoice()
                    .withApprovalListTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTask(getActivity())))
                    .setApprovalListChoiceClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withTaskListTitleAndCounter(String.valueOf(ToDoList.getCounterTaskList(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
                            task.execute();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnTasklist) {
            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
            task.execute();
        } else if (haveMnVerificationlist && haveMnApprovallistByBranch && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnVerificationlist && haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnVerificationlist && haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnVerificationlist) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoice()
                    .withVerificationListTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTask(getActivity())))
                    .setVerificationListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnVerificationlistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withVerificationListChoiceByBranch()
                    .withVerificationListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getActivity())))
                    .setVerificationListChoiceByBranchClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getVerificationFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else if (haveMnApprovallistByBranch) {
            final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(context);
            dialogBuilder.withTitle(getActivity().getString(R.string.select_outstanding))
                    .withNoMessage()
                    .withApprovalListChoiceByBranch()
                    .withApprovalListByBranchTitleAndCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getActivity())))
                    .setApprovalListChoiceByBranchClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getApprovalFragmentByBranch();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .withTaskListChoice()
                    .withStatusListTitle()
                    .withStatusListTitleAndCounter(String.valueOf(ToDoList.getCounterStatusTask(getActivity())))
                    .setTaskListChoiceClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View paramView) {
                            Fragment fragment1 = MainMenuActivity.getStatusFragment();
                            FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                            transaction.replace(R.id.content_frame, fragment1);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        } else {
            TaskListTask task = new TaskListTask(getActivity(), getActivity().getString(R.string.progressWait),
                    getActivity().getString(R.string.msgNoTaskList), R.id.content_frame);
            task.execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isTimelineOpen = false;
        try {
            query.id(R.id.new_image_header).recycle(getView());
            query.id(R.id.imageProfile).recycle(getView());
        } catch (NullPointerException e) {
            ACRA.getErrorReporter().putCustomData("errorOnDestroy", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnDestroy", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat recycle view"));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        query.id(R.id.new_image_header).recycle(getView());
        query.id(R.id.imageProfile).recycle(getView());
    }

    @Override
    public void onPause() {
        super.onPause();
        isTimelineOpen = false;
        query.id(R.id.new_image_header).recycle(getView());
        query.id(R.id.imageProfile).recycle(getView());
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            isTimelineOpen = false;
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorOnStop", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnStop", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set isTimelineOpen"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isTimelineOpen = true;
        Utility.freeMemory();
        timelineImpl.refreshImageBitmap(R.id.new_image_header, R.drawable.header_image, timelineImpl.getUser().getImage_cover())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        timelineImpl.refreshImageBitmap(R.id.imageProfile, R.drawable.profile_image, timelineImpl.getUser().getImage_profile())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        try {
            query.id(R.id.txtJumlahOutstanding).text(String.valueOf(timelineImpl.getTaskListCounter()));
            MainMenuActivity.tempPosition = 0;
            setCashOnHandUI();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnResume", Tool.getSystemDateTime().toLocaleString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Cash UI"));
        }
        query.id(R.id.txtOutstanding).text(getActivity().getString(R.string.outstanding_task));
        if (Timeline_Activity.getTimelineHandler() != null)
            Timeline_Activity.getTimelineHandler().sendEmptyMessage(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Utility.freeMemory();
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(getActivity(), CroppingImageActivity.class);
            intent.putExtra(CroppingImageActivity.BUND_KEY_ABSOLUTEPATH, result.getData().toString());
            startActivity(intent);
        } else if (requestCode == Utils.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            if (Constants.flag_edit == 0) {
                File file = new File(context.getExternalFilesDir(null) + File.separator + "imgHeader.jpg");
                Uri outputUri = Uri.fromFile(file);
                Intent intent = new Intent(getActivity(), CroppingImageActivity.class);
                intent.putExtra(CroppingImageActivity.BUND_KEY_ABSOLUTEPATH, outputUri.toString());
                startActivity(intent);
            } else {
                File file = new File(context.getExternalFilesDir(null) + File.separator + "imgProfile.jpg");
                Uri outputUri = Uri.fromFile(file);
                Intent intent = new Intent(getActivity(), CroppingImageActivity.class);
                intent.putExtra(CroppingImageActivity.BUND_KEY_ABSOLUTEPATH, outputUri.toString());
                startActivity(intent);
            }
        }
        Utility.freeMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mnSearch).setVisible(true);
        menu.findItem(R.id.menuMore).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.mnSearch) {
            Constants.inSearchMode = true;
            CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(0, 1, 1, 1, 500, searchLayout, false);
            query.id(R.id.searchLayout)
                    .visibility(View.VISIBLE)
                    .animate(animatorLayout);
            adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.custom_dropdown_item_1line, searchDescription);
            completeTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteSearch);
            completeTextView.setAdapter(adapter);
            completeTextView.requestFocus();
            completeTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(completeTextView, 0);
                }
            }, 500);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void closeSearch(List<Timeline> objects) {

        if (Constants.inSearchMode) {
            arrayAdapter = new TimelineArrayAdapter(context, objects);
            arrayAdapter.setMainFragment(getActivity())
                    .withColor(getResources().getColor(R.color.tv_normal))
                    .setContentFrame(R.id.content_frame)
                    .setLogFragment(new LogResultActivity());
            arrayAdapter.notifyDataSetChanged();
            query.id(android.R.id.list).adapter(arrayAdapter);
            query.id(R.id.autoCompleteSearch).text("");

            CustomAnimatorLayout animatorLayout = new CustomAnimatorLayout(1, 0, 1, 1, 500, searchLayout, true);
            query.id(R.id.searchLayout).animate(animatorLayout);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(completeTextView.getWindowToken(), 0);
                }
            }, 500);
        }
        Constants.inSearchMode = false;

    }

    public class TimelineHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isTimelineOpen) {
                    initiateRefresh();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorTimelineHandler", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorTimelineHandler", Tool.getSystemDateTime().toLocaleString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            }
        }
    }

}