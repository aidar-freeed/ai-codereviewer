package com.adins.mss.base.mainmenu.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dialogfragments.ChangePasswordDialog;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.fragments.SettingFragment;
import com.adins.mss.base.mainmenu.MainMenuHelper;
import com.adins.mss.base.mainmenu.NewMenuItem;
import com.adins.mss.base.mainmenu.adapter.NewMainMenuAdapter;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineImpl;
import com.adins.mss.base.timeline.TimelineListener;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.MenuDataAccess;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewMenuFragment extends Fragment implements TimelineListener, ThemeLoader.ColorSetLoaderCallback {
    public static NewMenuItem mnTaskList;
    public static NewMenuItem mnSurveyAssign;
    public static NewMenuItem mnSurveyVerif;
    public static NewMenuItem mnSurveyApproval;
    public static NewMenuItem mnVerifByBranch;
    public static NewMenuItem mnApprovalByBranch;
    public static NewMainMenuAdapter adapter;
    private static NewMainMenuAdapter.OnItemClickListener itemClickListener;
    private static AQuery query;
    private static Menu mainMenu;
    private List<NewMenuItem> menuItems;
    private TimelineImpl timelineImpl;
    private BottomNavigationView bottomNav;
    private TextView counter;
    private CircleImageView profilePic;
    private RecyclerView recyclerView;
    private NewMainMenuAdapter.OnItemClickListener onItemClickListener = new NewMainMenuAdapter.OnItemClickListener() {
        @Override
        public NewMenuItem OnItemClick(NewMenuItem menuItem, int position) {
            switch (menuItem.getName()) {
                case "Tasklist":

                    break;
                case "Timeline":
                    Fragment fragment = new NewTimelineFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.commit();
                    break;
                default:
                    break;
            }
            return menuItem;
        }
    };
    private View accountlayout;
    private int syncMenuPosition;

    public NewMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewMenuFragment.
     */
    public static NewMenuFragment newInstance(NewMainMenuAdapter.OnItemClickListener onItemClickListener) {
        NewMenuFragment fragment = new NewMenuFragment();

        itemClickListener = onItemClickListener;
        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (null != getActivity().findViewById(R.id.mnGPS))
            getActivity().findViewById(R.id.mnGPS).clearAnimation();

        mainMenu = menu;

        List<String> listMenu = getMatchToolbarMenu();
        boolean isHaveChangePass = false;
        boolean isHaveSettings = false;

        // olivia : set tampilan toolbar untuk masing" density
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(displayMetrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM || displayMetrics.densityDpi == DisplayMetrics.DENSITY_HIGH){
            for (int i = 0; i < listMenu.size(); i++) {
                if (listMenu.get(i).equalsIgnoreCase(getString(R.string.title_mn_changepassword)))
                    isHaveChangePass = true;
                else if (listMenu.get(i).equalsIgnoreCase(getString(R.string.title_mn_setting)))
                    isHaveSettings = true;
            }
            if (isHaveChangePass || isHaveSettings)
                menu.findItem(R.id.more).setVisible(true);
        }else{
            for (int i = 0; i < listMenu.size(); i++) {
                if (listMenu.get(i).equalsIgnoreCase(getString(R.string.title_mn_changepassword)))
                    menu.findItem(R.id.mnChangePassword).setVisible(true);
                else if (listMenu.get(i).equalsIgnoreCase(getString(R.string.title_mn_setting)))
                    menu.findItem(R.id.mnSetting).setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        List<String> listMenu = getServerMenuTitle();

        // olivia : set tampilan toolbar untuk masing" density
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                if (!Global.isMenuMoreClicked) {
                    if (id == R.id.more) {
                        for (int i = 0; i < listMenu.size(); i++) {
                            if (listMenu.get(i).equalsIgnoreCase(getString(R.string.mn_changepassword)))
                                mainMenu.findItem(R.id.moreChangePassword).setVisible(true);
                            else if (listMenu.get(i).equalsIgnoreCase(getString(R.string.mn_setting)))
                                mainMenu.findItem(R.id.moreSetting).setVisible(true);
                        }
                        Global.isMenuMoreClicked = false;
                    }
                    if (id == R.id.moreChangePassword) {
                        ChangePasswordDialog fragment = new ChangePasswordDialog();
                        fragment.show(fragmentManager, "Change Password");
                        Global.isMenuMoreClicked = true;
                    } else if (id == R.id.moreSetting) {
                        Global.isMenuMoreClicked = true;
                        getActivity().setTitle(getString(R.string.mn_setting));
                        Fragment fragment = new SettingFragment();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                break;
            case DisplayMetrics.DENSITY_HIGH:
                if (!Global.isMenuMoreClicked) {
                    if (id == R.id.more) {
                        for (int i = 0; i < listMenu.size(); i++) {

                            if (listMenu.get(i).equalsIgnoreCase(getString(R.string.mn_changepassword))) {
                                //resize icon agar rapi
                                Drawable icon = mainMenu.findItem(R.id.moreChangePassword).getIcon();
                                Bitmap resizedIconBitmap = Utils.getResizedBitmap(((BitmapDrawable) icon).getBitmap(),50,50);
                                Drawable resizedIcon = new BitmapDrawable(getResources(),resizedIconBitmap);

                                mainMenu.findItem(R.id.moreChangePassword).setIcon(resizedIcon);
                                mainMenu.findItem(R.id.moreChangePassword).setVisible(true);
                            }
                            else if (listMenu.get(i).equalsIgnoreCase(getString(R.string.mn_setting))) {
                                //resize icon agar rapi
                                Drawable icon = mainMenu.findItem(R.id.moreSetting).getIcon();
                                Bitmap resizedIconBitmap = Utils.getResizedBitmap(((BitmapDrawable) icon).getBitmap(),45,45);
                                Drawable resizedIcon = new BitmapDrawable(getResources(),resizedIconBitmap);

                                mainMenu.findItem(R.id.moreSetting).setIcon(resizedIcon);
                                mainMenu.findItem(R.id.moreSetting).setVisible(true);
                            }
                        }
                    }
                    if (id == R.id.moreChangePassword) {
                        ChangePasswordDialog fragment = new ChangePasswordDialog();
                        fragment.show(fragmentManager, "Change Password");
                        Global.isMenuMoreClicked = true;
                    } else if (id == R.id.moreSetting) {
                        getActivity().setTitle(getString(R.string.mn_setting));
                        Fragment fragment = new SettingFragment();
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                break;
            default:
                if (id == R.id.mnSetting) {
                    getActivity().setTitle(getString(R.string.mn_setting));
                    Fragment fragment = new SettingFragment();
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_up, 0, R.anim.slide_up, 0);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (id == R.id.mnChangePassword) {
                    ChangePasswordDialog fragment = new ChangePasswordDialog();
                    fragment.show(fragmentManager, "Change Password");
                }
                break;
        }
        if(id == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelpWithRecycler(NewMenuFragment.this.getActivity(),
                            NewMenuFragment.this.getClass().getSimpleName(),
                            recyclerView,
                            syncMenuPosition);
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems = getMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.new_fragment_menu, container, false);

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle("MENU");

        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottomNav);
        counter = (TextView) getActivity().findViewById(R.id.counter);

        profilePic = (CircleImageView) view.findViewById(R.id.imageProfile);

        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile(view);
            }
        });

        query = new AQuery(view);

        String temp_uuid_user;
        try {
            temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get UUID User"));
            NewMainActivity.InitializeGlobalDataIfError(getActivity().getApplicationContext());
            try {
                temp_uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            } catch (Exception e2) {
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetUUIDUser", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat get UUID User"));
                ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getActivity(),
                        "GlobalData", Context.MODE_PRIVATE);
                temp_uuid_user = sharedPref.getString("UUID_USER", "");
            }
        }
        User user = UserDataAccess.getOne(view.getContext(), temp_uuid_user);
        if (user == null) {
            String message = getActivity().getString(R.string.data_corrupt);
            DialogManager.showForceExitAlert(getActivity(), message);
            return view;
        }
        query.id(R.id.txtName).text(user.getFullname());
        query.id(R.id.txtJob).text(user.getJob_description());

        setCashOnHandUI();

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
        } else {
            query.id(R.id.txtDealer).text(user.getBranch_name());
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountlayout = view.findViewById(R.id.account);
        RecyclerView mainMenu = (RecyclerView) view.findViewById(R.id.menuRecycle);
        recyclerView = mainMenu;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mainMenu.setHasFixedSize(true);
        mainMenu.setLayoutManager(layoutManager);

        adapter = new NewMainMenuAdapter(view.getContext(), menuItems, itemClickListener);
        mainMenu.setAdapter(adapter);
        loadColorSet();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelpWithRecycler(NewMenuFragment.this.getActivity(),
                        NewMenuFragment.this.getClass().getSimpleName(),
                        recyclerView,
                        syncMenuPosition);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    public User getUser() {
        return UserDataAccess.getOne(getContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
    }

    @Override
    public void onResume() {
        super.onResume();

        Global.isMenuMoreClicked = false;

        try {
            if (mnTaskList != null)
                mnTaskList.setCounter(String.valueOf(ToDoList.getCounterTaskList(getContext())));
            if (mnSurveyAssign != null)
                mnSurveyAssign.setCounter(String.valueOf(GlobalData.getSharedGlobalData().getCounterAssignment()));
            if (mnSurveyVerif != null)
                mnSurveyVerif.setCounter(String.valueOf(ToDoList.getCounterVerificationTask(getContext())));
            if (mnSurveyApproval != null)
                mnSurveyApproval.setCounter(String.valueOf(ToDoList.getCounterApprovalTask(getContext())));
            if (mnVerifByBranch != null)
                mnVerifByBranch.setCounter(String.valueOf(ToDoList.getCounterVerificationTaskByBranch(getContext())));
            if (mnApprovalByBranch != null)
                mnApprovalByBranch.setCounter(String.valueOf(ToDoList.getCounterApprovalTaskByBranch(getContext())));
            counter.setText(String.valueOf(ToDoList.getCounterTaskList(getContext())));
        } catch (Exception e) {
            FireCrash.log(e);
            e.getMessage();
        }

        bottomNav.getMenu().findItem(R.id.menuNav).setEnabled(false);

        getActivity().findViewById(R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle("MENU");

        timelineImpl.refreshImageBitmap(R.id.imageProfile, R.drawable.profile_pic, getUser().getImage_profile())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setCashOnHandUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        query.id(R.id.imageProfile).recycle(getView());
        bottomNav.getMenu().findItem(R.id.menuNav).setEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            query.id(R.id.imageProfile).recycle(getView());
        } catch (NullPointerException e) {
            ACRA.getErrorReporter().putCustomData("errorOnDestroy", e.getMessage());
            ACRA.getErrorReporter().putCustomData("errorOnDestroy", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat recycle view"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        query.id(R.id.imageProfile).recycle(getView());
        bottomNav.getMenu().findItem(R.id.menuNav).setEnabled(true);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        timelineImpl = new TimelineImpl(getActivity(), this);
        timelineImpl.setContext(activity);
    }

    public ArrayList<String> getTemplateToolbarMenu() {
        ArrayList<String> templateMenuTitle = new ArrayList<>();
        templateMenuTitle.add(getString(R.string.mn_changepassword));
        templateMenuTitle.add(getString(R.string.mn_setting));
        return templateMenuTitle;
    }

    public List<String> getToolbarMenu() {
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            NewMainActivity.InitializeGlobalDataIfError(getActivity());
        }
        List<String> menu = MainMenuHelper.matchingMenu(getServerMenuTitle(), getTemplateToolbarMenu());
        return menu;
    }

    public List<String> getMatchToolbarMenu() {
        List<String> newMenu = new ArrayList<>();
        List<String> oldMenu = getToolbarMenu();

        for (int i = 0; i < oldMenu.size(); i++) {
            if (oldMenu.get(i).equals(getString(R.string.mn_changepassword)))
                newMenu.add(getString(R.string.title_mn_changepassword));
            else if (oldMenu.get(i).equals(getString(R.string.mn_setting)))
                newMenu.add(getString(R.string.title_mn_setting));
        }
        return newMenu;
    }

    public HashMap<String, Integer> getTemplateIcon() {
        HashMap<String, Integer> templateIcon = new HashMap<>();
        templateIcon.put(getString(R.string.mn_newtask), R.drawable.menu_newtask);
        templateIcon.put(getString(R.string.mn_tasklist), R.drawable.menu_tasklist);
        templateIcon.put(getString(R.string.mn_log), R.drawable.menu_log);
        templateIcon.put(getString(R.string.mn_absentin), R.drawable.menu_attendance);
        templateIcon.put(getString(R.string.mn_synchronize), R.drawable.menu_synchronize);
        templateIcon.put(getString(R.string.mn_exit), R.drawable.menu_exit);
        templateIcon.put(getString(R.string.mn_about), R.drawable.menu_about);
        templateIcon.put(getString(R.string.mn_surveyperformance), R.drawable.menu_surveyperformance);
        templateIcon.put(getString(R.string.mn_reportsummary), R.drawable.menu_surveyperformance);
        templateIcon.put(getString(R.string.mn_depositreport), R.drawable.menu_deposit);
        templateIcon.put(getString(R.string.mn_depositreport_ac), R.drawable.menu_deposit);
        templateIcon.put(getString(R.string.mn_depositreport_pc), R.drawable.menu_deposit);
        templateIcon.put(getString(R.string.mn_closing_task), R.drawable.menu_closingtask);
        templateIcon.put(getString(R.string.mn_surveyassign), R.drawable.menu_surveyassignment);
        templateIcon.put(getString(R.string.mn_surveyreassign), R.drawable.menu_surveyreassignment);
        templateIcon.put(getString(R.string.mn_surveyverification), R.drawable.menu_surveyverification);
        templateIcon.put(getString(R.string.mn_surveyapproval), R.drawable.menu_surveyapproval);
        templateIcon.put(getString(R.string.mn_verification_bybranch), R.drawable.menu_surveyverification);
        templateIcon.put(getString(R.string.mn_approval_bybranch), R.drawable.menu_surveyapproval);
        templateIcon.put(getString(R.string.mn_checkorder), R.drawable.menu_checkorder);
        templateIcon.put(getString(R.string.mn_cancelorder), R.drawable.menu_cancelorder);
        templateIcon.put(getString(R.string.mn_promo), R.drawable.menu_promotion);
        templateIcon.put(getString(R.string.mn_orderassign), R.drawable.menu_surveyassignment);
        templateIcon.put(getString(R.string.mn_orderreassign), R.drawable.menu_surveyreassignment);
        templateIcon.put(getString(R.string.mn_neworder), R.drawable.menu_newtask);
        templateIcon.put(getString(R.string.mn_dashboard), R.drawable.icon_coin);
        templateIcon.put(getString(R.string.mn_dashboard_collection),R.drawable.ic_dashboard_coll);
        templateIcon.put(getString(R.string.mn_guideline_faq), R.drawable.menu_guideline_faq);


        return templateIcon;
    }

    public ArrayList<String> getTemplateMenuTitle() {
        ArrayList<String> templateMenuTitle = new ArrayList<>();
        templateMenuTitle.add(getString(R.string.mn_newtask));
        templateMenuTitle.add(getString(R.string.mn_newlead));
        templateMenuTitle.add(getString(R.string.mn_account));
        templateMenuTitle.add(getString(R.string.mn_products));
        templateMenuTitle.add(getString(R.string.mn_opportunities));
        templateMenuTitle.add(getString(R.string.mn_followup));
        templateMenuTitle.add(getString(R.string.mn_catalogue));
        templateMenuTitle.add(getString(R.string.mn_marketingreport));
        templateMenuTitle.add(getString(R.string.mn_neworder));
        templateMenuTitle.add(getString(R.string.mn_tasklist));
        templateMenuTitle.add(getString(R.string.mn_taskApproval));
        templateMenuTitle.add(getString(R.string.mn_log));
        templateMenuTitle.add(getString(R.string.mn_checkorder));
        templateMenuTitle.add(getString(R.string.mn_cancelorder));
        templateMenuTitle.add(getString(R.string.mn_orderassign));
        templateMenuTitle.add(getString(R.string.mn_orderreassign));
        templateMenuTitle.add(getString(R.string.mn_promo));
        templateMenuTitle.add(getString(R.string.mn_surveyassign));
        templateMenuTitle.add(getString(R.string.mn_surveyreassign));
        templateMenuTitle.add(getString(R.string.mn_surveyverification));
        templateMenuTitle.add(getString(R.string.mn_surveyapproval));
        templateMenuTitle.add(getString(R.string.mn_verification_bybranch));
        templateMenuTitle.add(getString(R.string.mn_approval_bybranch));
        templateMenuTitle.add(getString(R.string.mn_surveyperformance));
        templateMenuTitle.add(getString(R.string.mn_closing_task));
        templateMenuTitle.add(getString(R.string.mn_depositreport));
        templateMenuTitle.add(getString(R.string.mn_depositreport_ac));
        templateMenuTitle.add(getString(R.string.mn_depositreport_pc));
        templateMenuTitle.add(getString(R.string.mn_reportsummary));
        templateMenuTitle.add(getString(R.string.mn_absentin));
        templateMenuTitle.add(getString(R.string.mn_dashboard));
        templateMenuTitle.add(getString(R.string.mn_dashboard_collection));
        templateMenuTitle.add(getString(R.string.mn_synchronize));
        templateMenuTitle.add(getString(R.string.mn_about));
        templateMenuTitle.add(getString(R.string.mn_guideline_faq));
        templateMenuTitle.add(getString(R.string.mn_exit));

        return templateMenuTitle;
    }

    public ArrayList<String> getServerMenuTitle() {
        ArrayList<String> serverMenuTitle = new ArrayList<>();
        try {
            List<com.adins.mss.dao.Menu> menu = MenuDataAccess.getAll(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user());

            for (com.adins.mss.dao.Menu menu2 : menu) {
                serverMenuTitle.add(menu2.getUuid_menu());
            }
        } catch (Exception e) {
            FireCrash.log(e);

        }
        return serverMenuTitle;
    }

    public List<String> getMainMenuTitle() {
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            NewMainActivity.InitializeGlobalDataIfError(getActivity());
        }
        return MainMenuHelper.matchingMenu(getServerMenuTitle(), getTemplateMenuTitle());
    }

    public List<Integer> getMainMenuIcon() {
        return MainMenuHelper.matchingIcon(getMainMenuTitle(), getTemplateIcon());
    }

    // olivia : update bahasa indonesia untuk menu
    public List<String> getMatchMenuLanguage() {
        List<String> newMenu = new ArrayList<>();
        List<String> oldMenu = getMainMenuTitle();

        for (int i = 0; i < oldMenu.size(); i++) {
            if (oldMenu.get(i).equals(getString(R.string.mn_newtask)))
                newMenu.add(getString(R.string.title_mn_newtask));
            else if (oldMenu.get(i).equals(getString(R.string.mn_tasklist)))
                newMenu.add(getString(R.string.title_mn_tasklist));
            else if (oldMenu.get(i).equals(getString(R.string.mn_log)))
                newMenu.add(getString(R.string.title_mn_log));
            else if (oldMenu.get(i).equals(getString(R.string.mn_checkorder)))
                newMenu.add(getString(R.string.title_mn_checkorder));
            else if (oldMenu.get(i).equals(getString(R.string.mn_cancelorder)))
                newMenu.add(getString(R.string.title_mn_cancelorder));
            else if (oldMenu.get(i).equals(getString(R.string.mn_orderassign)))
                newMenu.add(getString(R.string.title_mn_orderassign));
            else if (oldMenu.get(i).equals(getString(R.string.mn_orderreassign)))
                newMenu.add(getString(R.string.title_mn_orderreassign));
            else if (oldMenu.get(i).equals(getString(R.string.mn_promo)))
                newMenu.add(getString(R.string.title_mn_promo));
            else if (oldMenu.get(i).equals(getString(R.string.mn_surveyassign)))
                newMenu.add(getString(R.string.title_mn_surveyassign));
            else if (oldMenu.get(i).equals(getString(R.string.mn_surveyreassign)))
                newMenu.add(getString(R.string.title_mn_surveyreassign));
            else if (oldMenu.get(i).equals(getString(R.string.mn_surveyverification)))
                newMenu.add(getString(R.string.title_mn_surveyverification));
            else if (oldMenu.get(i).equals(getString(R.string.mn_surveyapproval)))
                newMenu.add(getString(R.string.title_mn_surveyapproval));
            else if (oldMenu.get(i).equals(getString(R.string.mn_verification_bybranch)))
                newMenu.add(getString(R.string.title_mn_verification_bybranch));
            else if (oldMenu.get(i).equals(getString(R.string.mn_approval_bybranch)))
                newMenu.add(getString(R.string.title_mn_approval_bybranch));
            else if (oldMenu.get(i).equals(getString(R.string.mn_closing_task)))
                newMenu.add(getString(R.string.title_mn_closing_task));
            else if (oldMenu.get(i).equals(getString(R.string.mn_depositreport)))
                newMenu.add(getString(R.string.title_mn_depositreport));
            else if (oldMenu.get(i).equals(getString(R.string.mn_reportsummary)))
                newMenu.add(getString(R.string.title_mn_reportsummary));
            else if (oldMenu.get(i).equals(getString(R.string.mn_surveyperformance)))
                newMenu.add(getString(R.string.title_mn_surveyperformance));
            else if (oldMenu.get(i).equals(getString(R.string.mn_absentin)))
                newMenu.add(getString(R.string.title_mn_absentin));
            else if (oldMenu.get(i).equals(getString(R.string.mn_synchronize)))
                newMenu.add(getString(R.string.title_mn_synchronize));
            else if (oldMenu.get(i).equals(getString(R.string.mn_about)))
                newMenu.add(getString(R.string.title_mn_about));
            else if (oldMenu.get(i).equals(getString(R.string.mn_exit)))
                newMenu.add(getString(R.string.title_mn_exit));
            else if (oldMenu.get(i).equals(getString(R.string.mn_account)))
                newMenu.add(getString(R.string.title_mn_account));
            else if (oldMenu.get(i).equals(getString(R.string.mn_catalogue)))
                newMenu.add(getString(R.string.title_mn_catalogue));
            else if (oldMenu.get(i).equals(getString(R.string.mn_newlead)))
                newMenu.add(getString(R.string.title_mn_newlead));
            else if (oldMenu.get(i).equals(getString(R.string.mn_products)))
                newMenu.add(getString(R.string.title_mn_products));
            else if (oldMenu.get(i).equals(getString(R.string.mn_opportunities)))
                newMenu.add(getString(R.string.title_mn_opportunities));
            else if (oldMenu.get(i).equals(getString(R.string.mn_followup)))
                newMenu.add(getString(R.string.title_mn_followup));
            else if (oldMenu.get(i).equals(getString(R.string.mn_marketingreport)))
                newMenu.add(getString(R.string.title_mn_marketingreport));
            else if (oldMenu.get(i).equals(getString(R.string.mn_neworder)))
                newMenu.add(getString(R.string.title_mn_neworder));
            else if (oldMenu.get(i).equals(getString(R.string.mn_dashboard)))
                newMenu.add(getString(R.string.title_mn_dashboard));
            else if (oldMenu.get(i).equals(getString(R.string.mn_dashboard_collection)))
                newMenu.add(getString(R.string.title_mn_dashboard_collection));
            else if (oldMenu.get(i).equals(getString(R.string.mn_depositreport_ac)))
                newMenu.add(getString(R.string.title_mn_depositreportAC));
            else if (oldMenu.get(i).equals(getString(R.string.mn_depositreport_pc)))
                newMenu.add(getString(R.string.title_mn_depositreportPC));
            else if(oldMenu.get(i).equals(getString(R.string.mn_guideline_faq)))
                newMenu.add(getString(R.string.title_mn_guideline_faq));
        }
        return newMenu;
    }

    private List<NewMenuItem> getMenu() {
        List<NewMenuItem> menuItems = new ArrayList<>();

        List<String> menuTitle = getMatchMenuLanguage();
        List<Integer> menuIcon = getMainMenuIcon();

        for (int i = 0; i < menuTitle.size(); i++) {
            if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_tasklist))) {
                mnTaskList = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnTaskList);
            } else if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyassign))) {
                mnSurveyAssign = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnSurveyAssign);
            } else if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyverification))) {
                mnSurveyVerif = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnSurveyVerif);
            } else if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_surveyapproval))) {
                mnSurveyApproval = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnSurveyApproval);
            } else if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_verification_bybranch))) {
                mnVerifByBranch = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnVerifByBranch);
            } else if (menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_approval_bybranch))) {
                mnApprovalByBranch = new NewMenuItem(menuTitle.get(i), menuIcon.get(i), "0");
                menuItems.add(mnApprovalByBranch);
            } else
                menuItems.add(new NewMenuItem(menuTitle.get(i), menuIcon.get(i), null));

            if(menuTitle.get(i).equalsIgnoreCase(getString(R.string.title_mn_synchronize))){
                syncMenuPosition = i;
            }
        }
        return menuItems;
    }

    public void changeProfile(View view) {
        final NiftyDialogBuilder_PL dialogBuilder = NiftyDialogBuilder_PL.getInstance(getContext());
        dialogBuilder.withTitle(getString(com.adins.mss.base.R.string.profile__title)).withNoMessage()
                .withButton1Text(getString(com.adins.mss.base.R.string.btn_camera)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.flag_edit = 1;
                dialogBuilder.dismiss();
                final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(getContext().getExternalFilesDir(null) + File.separator + "imgProfile.jpg");

                // olivia : untuk sdk >= 24 harus pakai FileProvider
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
                } else
                    uri = Uri.fromFile(file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
                        Crop.pickImage(getContext(), NewMenuFragment.this);
                    }

                }, 100);
            }
        }).isCancelable(false).isCancelableOnTouchOutside(true).show();
    }

    private void setCashOnHandUI() {
        if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {

            String cashLimit = GlobalData.getSharedGlobalData().getUser().getCash_limit();
            if(cashLimit == null || cashLimit.equalsIgnoreCase("null")){
                cashLimit = "0";
            }
            double limit = Double.parseDouble(cashLimit);

            String cashOnHand = GlobalData.getSharedGlobalData().getUser().getCash_on_hand();
            if(cashOnHand == null || cashOnHand.equalsIgnoreCase("null")){
                cashOnHand = "0";
            }
            double coh = Double.parseDouble(cashOnHand);

            String setLimit = Tool.separateThousand(limit);
            String setCOH = Tool.separateThousand(coh);

            if (limit > 0 && isCOHAktif()) {
                query.id(R.id.txtCurrentCOH).visibility(View.VISIBLE);
                query.id(R.id.txtLimitCOH).visibility(View.VISIBLE);
                query.id(R.id.txtCurrentCOH).text(setCOH);
                query.id(R.id.txtLimitCOH).text(setLimit);
            } else {
                query.id(R.id.txtCurrentCOH).visibility(View.VISIBLE);
                query.id(R.id.txtCurrentCOH).text(setCOH);
            }
        }
    }

    public boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(getActivity(), GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
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
            File file = new File(getContext().getExternalFilesDir(null) + File.separator + "imgProfile.jpg");
            Uri outputUri = Uri.fromFile(file);
            Intent intent = new Intent(getActivity(), CroppingImageActivity.class);
            intent.putExtra(CroppingImageActivity.BUND_KEY_ABSOLUTEPATH, outputUri.toString());
            startActivity(intent);
        }
        Utility.freeMemory();
    }

    @Override
    public void onSuccessBackgroundTask(List<Timeline> timelines) {
        //EMPTY
    }

    @Override
    public void onSuccessImageBitmap(Bitmap bitmap, int imageView, int defaultImage) {
        if (bitmap != null) query.id(imageView).image(bitmap);
        else query.id(imageView).image(defaultImage);
    }

    private void applyColorTheme(DynamicTheme colorSet){
        int accountBgColor = Color.parseColor(ThemeUtility.getColorItemValue(colorSet,"bg_solid"));
        ThemeUtility.setViewBackground(accountlayout,accountBgColor);
    }

    private void loadColorSet(){
        ThemeLoader themeLoader = new ThemeLoader(this.getContext());
        themeLoader.loadSavedColorSet(this);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null){
            applyColorTheme(dynamicTheme);
        }
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
        //EMPTY
    }
}
