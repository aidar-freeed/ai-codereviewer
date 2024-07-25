package com.adins.mss.base.about.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.base.util.Utility;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.github.jjobes.slidedatetimepicker.SlidingTabLayout;

//import static com.google.android.gms.internal.a.R;

/**
 * Created by gigin.ginanjar on 15/08/2016.
 */
public class AboutInfoTab extends Fragment {
    public static boolean isMenuClicked = false;
    private static Menu mainMenu;
    View view;
    private Context mContext;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private boolean isError = false;
    private String message;
    private Fragment activeFragment;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        mContext = activity;
        try {
            isError = getArguments().getBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
            message = getArguments().getString(TaskList_Fragment.BUND_KEY_MESSAGE, "");
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_info_tab, container, false);

        //2017/09/07 | Nendi: add Title on toolbar
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_about));

        setupViews(view);
        initViewPager();
        initTabs();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initTabs() {
        // Set intial date on date tab
        updateAppTab();

        // Set initial time on time tab
        updateDeviceTab();

    }

    @Override
    public void onDestroyView() {
        try {
            mainMenu.findItem(R.id.menuMore).setVisible(false);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        super.onDestroyView();
        Utility.freeMemory();
    }

    private void updateAppTab() {
        mSlidingTabLayout.setTabText(1, getResources().getString(R.string.tabDevice));
    }

    private void updateDeviceTab() {
        mSlidingTabLayout.setTabText(0, getResources().getString(R.string.tabApplication));
    }

    private void initViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        // Setting this custom layout for each tab ensures that the tabs will
        // fill all available horizontal space.

        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab_tasklist, R.id.tabTextTaskList);
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getActivity(), R.color.tv_white),
                ContextCompat.getColor(getActivity(), R.color.tv_white));
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), R.color.tv_white),
                ContextCompat.getColor(getActivity(), R.color.tv_white));
        mSlidingTabLayout.setViewPager(mViewPager);
        if (isError) {
            isError = false;
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());

            if (!dialogBuilder.isShowing()) {
                dialogBuilder.withTitle(getResources().getString(R.string.warning_capital)).
                        withMessage(message).isCancelable(true).show();
            }
        }
    }

    private void setupViews(View v) {
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mSlidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.slidingTabLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
//        isMenuClicked = false;
//        getActivity().getActionBar().setTitle(getString(R.string.title_mn_about));
        Utility.freeMemory();
//        MainMenuActivity.setDrawerPosition(getString(R.string.title_mn_about));
//        if(view!=null) {
//            initViewPager();
//            initTabs();
//        }
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_about));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new AboutActivity();
                    break;
                case 1:
                    fragment = new DeviceInfoFragment();
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
