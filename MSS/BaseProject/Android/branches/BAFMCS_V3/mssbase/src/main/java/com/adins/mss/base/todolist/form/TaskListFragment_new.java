package com.adins.mss.base.todolist.form;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;


/**
 * Created by gigin.ginanjar on 15/08/2016.
 */
public class TaskListFragment_new extends Fragment {

    public static boolean isMenuClicked = false;
    private static Menu mainMenu;
    private Fragment fragment = this;
    private TasklistView viewLogic;
    private Trace planTaskTrace;
    private FirebaseAnalytics screenName;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
//        mContext = activity;
        viewLogic = new TasklistView(fragment);
        try {
//            isError = getArguments().getBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
//            message = getArguments().getString(TaskList_Fragment.BUND_KEY_MESSAGE, "");
//            status = getArguments().getString("status");
//            page = getArguments().getInt(TaskList_Fragment.BUND_KEY_PAGE, 0);
            viewLogic.setError(getArguments().getBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false));
            viewLogic.setMessage(getArguments().getString(TaskList_Fragment.BUND_KEY_MESSAGE, ""));
            viewLogic.setStatus(getArguments().getString("status"));
            viewLogic.setPage(getArguments().getInt(TaskList_Fragment.BUND_KEY_PAGE, 1));

        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        planTaskTrace = FirebasePerformance.getInstance().newTrace(getString(R.string.plan_task_trace));
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return viewLogic.initialize(inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        planTaskTrace.start();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_plan_task), null);
    }

    @Override
    public void onPause() {
        super.onPause();
        planTaskTrace.stop();
    }
}
