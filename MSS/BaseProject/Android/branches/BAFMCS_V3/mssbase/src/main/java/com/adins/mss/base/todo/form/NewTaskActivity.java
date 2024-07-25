package com.adins.mss.base.todo.form;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.CustomerFragment;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.androidquery.AQuery;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.Calendar;
import java.util.List;

public abstract class NewTaskActivity extends Fragment {
    protected View view;
    protected AQuery query;
    protected List<Scheme> objects;
    private User user = GlobalData.getSharedGlobalData().getUser();
    private FirebaseAnalytics screenName;

    protected abstract NewTaskAdapter getNewTaskAdapter();

    protected TaskH setNewTaskH(Scheme scheme) {
        TaskH taskH = null;
        taskH = new TaskH();
        taskH.setUuid_task_h(Tool.getUUID());
        taskH.setUser(user);
        taskH.setScheme(scheme);
        taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
        taskH.setIs_prepocessed(TaskHDataAccess.STATUS_SEND_INIT);
        taskH.setIs_verification("0");
        return taskH;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_new_task), null);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                /*try {
					if (MainMenuActivity.mnTaskList != null)
						MainMenuActivity.mnTaskList.setCounter(String
								.valueOf(ToDoList
										.getCounterTaskList(getActivity())));
					if (MainMenuActivity.mnLog != null)
						MainMenuActivity.mnLog
								.setCounter(String.valueOf(TaskLogImpl
										.getCounterLog(getActivity())));
					if (MainMenuActivity.menuAdapter != null)
						MainMenuActivity.menuAdapter.notifyDataSetChanged();					
				} catch (Exception e) {             FireCrash.log(e);
					// TODO: handle exception
				}*/
                try {
                    MainMenuActivity.setDrawerCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorOnResume", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorOnResume", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
                }
            }
        });
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_newtask));
        getActivity().getActionBar().removeAllTabs();
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        screenName = FirebaseAnalytics.getInstance(getActivity());
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.newtask_layout, container, false);
            query = new AQuery(view);
            query.id(android.R.id.list).adapter(getNewTaskAdapter());
            query.id(android.R.id.list).itemClicked(this, "itemClick");
            if (SchemeDataAccess.getAll(getContext()).size() == 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_scheme_found), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorInflateView", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorInflateView", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat inflate view"));
        }
        return view;
    }

    @Keep
    public void itemClick(AdapterView<?> parent, View v, int position, long id) {
        Scheme selectedScheme = getNewTaskAdapter().getItem(position);
        TaskH selectedTask = setNewTaskH(selectedScheme);
        SurveyHeaderBean header = new SurveyHeaderBean(selectedTask);
        // TODO Action Lempar ke Customer ACtivity
        Bundle bundle = new Bundle();
        bundle.putSerializable(CustomerFragment.SURVEY_HEADER, header);
        bundle.putInt(CustomerFragment.SURVEY_MODE, Global.MODE_NEW_SURVEY);
        Fragment fragment = CustomerFragment.create(bundle);

        for (int i = 1; i < getFragmentManager().getBackStackEntryCount(); i++)
            NewMainActivity.fragmentManager.popBackStack();

        FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
