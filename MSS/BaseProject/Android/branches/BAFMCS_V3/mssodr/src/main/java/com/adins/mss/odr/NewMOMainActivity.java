package com.adins.mss.odr;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.about.activity.AboutActivity;
import com.adins.mss.base.mainmenu.NewMenuItem;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.todo.form.GetSchemeTask;
import com.adins.mss.base.todolist.form.NewToDoListTabFragment;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.odr.accounts.GetAccount;
import com.adins.mss.odr.catalogue.api.GetPromo;
import com.adins.mss.odr.followup.FragmentFollowUpSearch;
import com.adins.mss.odr.marketingreport.MarketingReportFragment;
import com.adins.mss.odr.news.NewsHeaderTask;
import com.adins.mss.odr.opportunities.FragmentOpportunities;
import com.adins.mss.odr.products.FragmentProduct;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPoint;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.services.ForegroundServiceNotification;
import com.services.MOApplicationRunningService;
import com.services.MainServices;
import com.services.NotificationThread;

import java.util.Locale;

/**
 * Created by Wendy.Alexander on 02/10/2019.
 */
public class NewMOMainActivity extends NewMainActivity {

    private FirebaseAnalytics screenName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NewMainActivity.setMss(this.getClass());
        super.onCreate(savedInstanceState);

        navigation = findViewById(com.adins.mss.base.R.id.bottomNav);
        navigation.getMenu().findItem(R.id.taskListNav).setTitle("Check Order");
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navCounter = findViewById(com.adins.mss.base.R.id.counter);
        navCounter.setVisibility(View.INVISIBLE);

        screenName = FirebaseAnalytics.getInstance(this);
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(getApplicationContext()), 1);

        if (savedInstanceState == null) {
            Fragment frag = new NewTimelineFragment();
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            trans.add(com.adins.mss.base.R.id.content_frame, frag);
            trans.commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try {
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            if (null == locale) {
                locale = new Locale(LocaleHelper.ENGLSIH);
            }
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_odr_main_menu), null);

        if (Global.REDIRECT_TIMELINE.equals(Global.getREDIRECT())) {
            super.openTimeline();
            Global.setREDIRECT(null);
            return;
        }

        if (!ForegroundServiceNotification.isServiceAvailable(this, MOApplicationRunningService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(getLocationServiceIntent());
            } else {
                startService(getLocationServiceIntent());
            }
        }

        Global.syncIntent = new Intent(getApplicationContext(), MOSynchronizeActivity.class);
        if (NewMainActivity.getMss() == null) {
            NewMainActivity.setMss(this.getClass());
            MainServices.mainClass = mss;
        }
        NewMainActivity.setMainMenuClass(NewMOMainActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Intent getLocationServiceIntent() {
        //Location service turned off for MO, Application Running Notification instead
        return new Intent(this, MOApplicationRunningService.class);
    }

    protected void actionFromNotif(Intent intent) {
        try {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(NotificationThread.TASKLIST_NOTIFICATION_KEY)) {
                    if (Global.PLAN_TASK_ENABLED) {
                        logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.mn_plantask));
                    } else {
                        logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_tasklist));
                    }

                    Fragment fragment1 = new NewToDoListTabFragment();
                    FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                    transaction.replace(R.id.content_frame, fragment1);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                } else if (action.equals(Global.MAINMENU_NOTIFICATION_KEY)) {
                    gotoTimeline();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        actionFromNotif(intent);
    }


    @Override
    protected Intent getIntentSynchronize() {
        return new Intent(this, MOSynchronizeActivity.class);
    }

    @Override
    protected void ApplicationMenu(NewMenuItem menuItem) {
        super.ApplicationMenu(menuItem);
        Global.isNewlead = false;
        if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_newlead))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_newlead));
            gotoNewLead();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_account))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_account));
            gotoAccount();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_followup))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_followup));
            gotoFollowUp();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_marketingreport))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_marketingreport));
            gotoMktReport();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_catalogue))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_catalogue));
            gotoCatalogue();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_products))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_products));
            gotoProducts();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_opportunities))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_opportunities));
            gotoOpportunities();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_cancelorder))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_cancelorder));
            gotoCancelOrder();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_checkorder))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_checkorder));
            gotoCheckOrder();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_promo))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_promo));
            gotoPromo();
        } else if (menuItem.getName().equalsIgnoreCase(getString(com.adins.mss.base.R.string.title_mn_neworder))) {
            logEventCountMenuAccessed(getString(com.adins.mss.base.R.string.title_mn_neworder));
            gotoNewTask();
        } else if (menuItem.getName().equalsIgnoreCase(getString(R.string.title_mn_dashboard))) {
            logEventCountMenuAccessed(getString(R.string.title_mn_dashboard));
            goToDashboardMyPoint();
        }
    }

    private void gotoOpportunities() {
        fragment = new FragmentOpportunities();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale,
                com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoNewLead() {
        Global.isNewlead = true;
        GetSchemeTask task = new GetSchemeTask(this, new MONewTaskActivity(), true);
        task.execute();
    }

    @Override
    public void gotoTimeline() {
        super.gotoTimeline();
    }

    @Override
    protected void gotoAbout() {
        AboutActivity.setChangeLog(ChangeLog.getChangeLog(this), 1);
        super.gotoAbout();
    }

    protected void gotoAccount() {
        GetAccount task = new GetAccount(this);
        task.execute();
    }

    protected void gotoFollowUp() {
        fragment = new FragmentFollowUpSearch();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale,
                com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoMktReport() {
        fragment = new MarketingReportFragment();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale,
                com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoCatalogue() {
        GetPromo task = new GetPromo(this);
        task.execute();
    }

    private void gotoProducts() {
        fragment = new FragmentProduct();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale,
                com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoCancelOrder() {
        fragment = new OrderFilterActivity();
        Bundle args = new Bundle();
        args.putInt(Global.BUND_KEY_TASK_TYPE, Global.TASK_CANCEL_ORDER);
        fragment.setArguments(args);

        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction
                .setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoCheckOrder() {
        fragment = new CheckOrderActivity();
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction
                .setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void gotoNewTask() {
        Global.isNewlead = false;
        GetSchemeTask task = new GetSchemeTask(this, new MONewTaskActivity(), true);
        task.execute();
    }

    protected void gotoPromo() {
        NewsHeaderTask task = new NewsHeaderTask(this, getResources().getString(R.string.progressWait), getString(R.string.msgNoList), R.id.content_frame);
        task.execute();
    }

    protected void goToDashboardMyPoint(){
        fragment = new DashboardMyPoint();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (Global.BACKPRESS_RESTRICTION) {
                return false;
            }

            int i = menuItem.getItemId();
            if (i == com.adins.mss.base.R.id.timelineNav) {
                gotoTimeline();
                return true;
            } else if (i == com.adins.mss.base.R.id.menuNav) {
                gotoMainMenu();
                return true;
            } else if (i == com.adins.mss.base.R.id.taskListNav) {
                gotoCheckOrder();
                return true;
            }
            return false;
        }
    };
}

