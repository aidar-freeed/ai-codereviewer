package com.adins.mss.base.todolist.form;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.FragmentHost;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

import org.acra.ACRA;

import java.lang.reflect.Field;
import java.util.Locale;

@SuppressLint("NewApi")
public class TaskList_Fragment extends Fragment implements
        TabListener, SearchView.OnQueryTextListener, FragmentHost {

    public static String BUND_KEY_ISERROR = "isError";
    public static String BUND_KEY_MESSAGE = "message";
    public static String BUND_KEY_PAGE = "page";
    public static boolean isMenuClicked = false;
    private static Menu mainMenu;
    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link androidx.fragment.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link androidx.fragment.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    View view;
    private boolean isError = false;
    private String message;
    private Activity context;
    private Fragment activeFragment;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //EMPTY
    }

    public void initialize(View view) {
        if (view instanceof LinearLayout) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(
                    getChildFragmentManager());
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar.getTabCount() != 0) actionBar.removeAllTabs();
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                actionBar.addTab(actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(i))
                        .setTabListener(this));
            }
            mViewPager = (ViewPager) view.findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    try {
                        getActivity().getActionBar().setSelectedNavigationItem(position);
                    } catch (IllegalStateException e) {
                        FireCrash.log(e);
                    }
                }
            });
            if (isError) {
                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());

                if (!dialogBuilder.isShowing()) {
                    dialogBuilder.withTitle("WARNING").
                            withIcon(android.R.drawable.ic_dialog_alert).
                            withMessage(message).isCancelable(true).show();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tasklist__layout, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        context = activity;
        try {
            isError = getArguments().getBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
            message = getArguments().getString(TaskList_Fragment.BUND_KEY_MESSAGE, "");
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mainMenu = menu;
        menu.findItem(R.id.menuMore).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (isMenuClicked == false) {
            int id = item.getItemId();
            if (id == R.id.menuMore) {
                mainMenu.findItem(R.id.mnViewAllHeader).setVisible(true);
                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application))
                    mainMenu.findItem(R.id.mnViewMap).setVisible(true);
                isMenuClicked = false;
            }
            if (id == R.id.mnViewMap) {
                MapsViewerFragment fragment = new MapsViewerFragment();
                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked = true;
            } else if (id == R.id.mnViewAllHeader) {
                AllHeaderViewerFragment viewerFragment = AllHeaderViewerFragment.newInstance(AllHeaderViewerFragment.REQ_PRIORITY_LIST);
                FragmentTransaction transaction = MainMenuActivity.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, viewerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                isMenuClicked = true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        isMenuClicked = false;
        getActivity().getActionBar().setTitle(getString(R.string.title_mn_tasklist));
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar.getTabCount() != 0) actionBar.removeAllTabs();
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public void onPause() {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.removeAllTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        super.onPause();
    }

    @Override
    public void onDestroy() {

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.removeAllTabs();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.removeAllTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        super.onDetach();
        try {
            mainMenu.findItem(R.id.menuMore).setVisible(false);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.removeAllTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        super.onDestroyView();
        try {
            mainMenu.findItem(R.id.menuMore).setVisible(false);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Filterable filterable = (Filterable) getActiveFragment();
        if (filterable != null) {
            filterable.getFilter().filter(newText);
            return true;
        }
        return false;
    }

    @Override
    public Fragment getActiveFragment() {
        return activeFragment;
    }

    @Override
    public void setActiveFragment(Fragment fragment) {
        activeFragment = fragment;
    }

    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
        //EMPTY
    }

    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
        //EMPTY
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new PrioritySectionFragment();
                    break;
                case 1:
                    fragment = new StatusSectionFragment();
                    break;
                default:
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_priority).toUpperCase(l);
                case 1:
                    return getString(R.string.title_status).toUpperCase(l);
                default:
                    break;
            }
            return null;
        }

        public int getPageIcon(int position) {
            switch (position) {
                case 0:
                    return R.drawable.icon_low;
                case 1:
                    return R.drawable.icon_high;
                default:
                    break;
            }
            return 0;
        }

        public int getBakcgroundColor(int position) {
            switch (position) {
                case 0:
                    return R.drawable.highpriority_background;
                case 1:
                    return R.drawable.mediumpriority_background;
                default:
                    break;
            }
            return 0;
        }
    }
}
