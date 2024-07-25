package com.adins.mss.base.todolist.form;

import android.app.Activity;
import android.content.Context;
import com.adins.mss.base.todolist.form.followup.FollowUpTabFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.form.helper.TaskFilterParam;
import com.adins.mss.base.todolist.form.helper.TaskPlanFilterObservable;
import com.adins.mss.base.todolist.form.todaysplan.TodayPlanFragment;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 27/07/2017.
 */

public class TasklistView implements TaskListTabInteractor, TabLayout.BaseOnTabSelectedListener {
    public static boolean isMenuClicked = false;
    public Activity activity;
    View view;
    private Context mContext;
    private ViewPagerAdapter mViewPagerAdapter;
    public static ViewPager mViewPager;
    public static TabLayout mSlidingTabLayout;
    private TabItem dummyTaskListTab, dummyTodayPlanTab;
    private boolean isError = false;
    private int page;
    private String message;
    private String status = "";
    private Fragment fragment;
    private Fragment activeFragment;

    public static String TASKLIST_TAB_PAGE_TAG = "Task List";
    public static String TODAYSPLAN_TAB_PAGE_TAG = "Today's Plan";
    public static String FOLLOWUP_TAB_PAGE_TAG = "Follow Up";

    List<TabPage> managedTabs = new ArrayList<>();

    public TasklistView() {
    }

    public TasklistView(Context context) {
        this.mContext = context;
    }

    public TasklistView(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.mContext = fragment.getContext();
    }

    public View initialize(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.tasklist_fragment_new, container, false);
        setupViews(view);
        initTabs();
        initViewPager();
        return view;
    }

    public void initTabs() {
        // Set intial date on date tab
        updatePriorityTab();
        // Set initial time on time tab
        updateTodayPlanTab();
        updateFollowUpTab();
        mSlidingTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mSlidingTabLayout.addOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mSlidingTabLayout));
    }

    private void updateFollowUpTab() {
        TabLayout.Tab tasklistTab = mSlidingTabLayout.newTab();
        tasklistTab.setText("Follow Up");
        View view = createCustomTabView("Follow Up");
        tasklistTab.setCustomView(view);
        mSlidingTabLayout.addTab( tasklistTab, 2);
    }

    private void updateTodayPlanTab() {
        TabLayout.Tab tasklistTab = mSlidingTabLayout.newTab();
        View view = createCustomTabView(mContext.getString(R.string.todays_plan_tab_page));
        tasklistTab.setCustomView(view);
        tasklistTab.setText(mContext.getString(R.string.todays_plan_tab_page));
        mSlidingTabLayout.addTab( tasklistTab, 1);
    }

    private void updatePriorityTab() {
        TabLayout.Tab tasklistTab = mSlidingTabLayout.newTab();
        View view = createCustomTabView(mContext.getString(R.string.tasklist_tab_page));
        tasklistTab.setCustomView(view);
        tasklistTab.setText(mContext.getString(R.string.tasklist_tab_page));
        mSlidingTabLayout.addTab( tasklistTab, 0);
    }

    public void initViewPager() {
        if(mViewPagerAdapter == null)
            mViewPagerAdapter = new ViewPagerAdapter(fragment.getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        try {
            if (status.equals("failed")) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        if (isError) {
            isError = false;
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);

            if (!dialogBuilder.isShowing()) {
                dialogBuilder.withTitle("WARNING").
                        withIcon(android.R.drawable.ic_dialog_alert).
                        withMessage(message).isCancelable(true).show();
            }
        }

        if (page == 1) {
            mViewPager.setCurrentItem(1);
        }
    }

    private View createCustomTabView(String title) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_text_size, null);
        TextView textView = view.findViewById(R.id.custom_text);
        textView.setText(title);
        return view;
    }

    public void setupViews(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mSlidingTabLayout = v.findViewById(R.id.slidingTabLayout);
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.activeFragment = activeFragment;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void goToTab(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public TaskPlanFilterObservable<TaskFilterParam> getFilterObservable() {
        PriorityTabFragment priorityTabFragment = (PriorityTabFragment) mViewPagerAdapter.managedFragment.get(TASKLIST_TAB_PAGE_TAG);
        return priorityTabFragment;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        for(TabPage tabPage:managedTabs){
            String tabTag = tab.getText().toString();
            if(tabTag.equals(tabPage.getTabPageName())){
                tabPage.onEnterPage();
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        for(TabPage tabPage:managedTabs){
            String tabTag = tab.getText().toString();
            if(tabTag.equals(tabPage.getTabPageName())){
                tabPage.onLeavePage();
                break;
            }
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public HashMap<String,Fragment> managedFragment = new HashMap<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    if(managedFragment.get(TASKLIST_TAB_PAGE_TAG) != null){
                        fragment = managedFragment.get(TASKLIST_TAB_PAGE_TAG);
                    }
                    else {
                        PriorityTabFragment taskfragment = new PriorityTabFragment();
                        taskfragment.setTabInteractor(TasklistView.this);
                        managedTabs.add(taskfragment);
                        TodayPlanFragment todayPlanFragment = (TodayPlanFragment) managedFragment.get(TODAYSPLAN_TAB_PAGE_TAG);
                        if(todayPlanFragment != null){
                            taskfragment.setTodayPlanHandler(todayPlanFragment);
                        }
                        else {
                            todayPlanFragment = new TodayPlanFragment();
                            todayPlanFragment.setTabInteractor(TasklistView.this);
                            managedFragment.put(TODAYSPLAN_TAB_PAGE_TAG,todayPlanFragment);
                            taskfragment.setTodayPlanHandler(todayPlanFragment);
                            managedTabs.add(todayPlanFragment);
                        }
                        fragment = taskfragment;
                        managedFragment.put(TASKLIST_TAB_PAGE_TAG,fragment);
                    }
                    break;
                case 1:
                    if(managedFragment.get(TODAYSPLAN_TAB_PAGE_TAG) != null){
                        fragment = managedFragment.get(TODAYSPLAN_TAB_PAGE_TAG);
                    }
                    else {
                        fragment = new TodayPlanFragment();
                        ((TodayPlanFragment) fragment).setTabInteractor(TasklistView.this);
                        managedFragment.put(TODAYSPLAN_TAB_PAGE_TAG,fragment);
                        managedTabs.add(((TodayPlanFragment) fragment));
                    }
                    break;
                case 2:
                    if(managedFragment.get(FOLLOWUP_TAB_PAGE_TAG) != null){
                        fragment = managedFragment.get(FOLLOWUP_TAB_PAGE_TAG);
                    }else {
                        fragment = new FollowUpTabFragment();
                        ((FollowUpTabFragment) fragment).setTabInteractor(TasklistView.this);
                        managedFragment.put(FOLLOWUP_TAB_PAGE_TAG, fragment);
                        managedTabs.add((FollowUpTabFragment) fragment);
                    }
                    break;

                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
