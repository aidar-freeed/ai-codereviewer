package com.adins.mss.odr.accounts;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.adins.mss.base.GlobalData;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.odr.R;
import com.github.jjobes.slidedatetimepicker.SlidingTabLayout;

import java.util.ArrayList;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class AccountResultActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private Account account;
    private ArrayList<String> productContact;
    public LocationTrackingManager manager;
    public static FragmentManager fragmentManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon(Global.isGPS);

        return super.onPrepareOptionsMenu(menu);
    }

    public static void updateMenuIcon(boolean isGPS) {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
    }

    private static Menu mainMenu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.adins.mss.base.R.id.mnGPS) {
            if (Global.LTM != null) {
                if (Global.LTM.getIsConnected()) {
                    Global.LTM.removeLocationListener();
                    Global.LTM.connectLocationClient();
                } else {
                    StartLocationTracking();
                }
                Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
                findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String uuidAccount = bundle.getString(Global.BUND_KEY_ACCOUNT_ID);
        account = AccountDataAccess.getOne(getApplicationContext(), uuidAccount);
        productContact = bundle.getStringArrayList(Global.BUND_KEY_PRODUCT_ID);

        setContentView(R.layout.account_result_activity);

        fragmentManager = getSupportFragmentManager();

        setupViews();
        initViewPager();
        initTabs();
    }

    @Override
    public void onResume() {
        super.onResume();

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(com.adins.mss.base.R.string.title_mn_account));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);
    }

    private void initTabs() {
        mSlidingTabLayout.setTabText(0, getResources().getString(R.string.tabDetail));
        mSlidingTabLayout.setTabText(1, getResources().getString(R.string.tabContact));
        mSlidingTabLayout.setTabText(2, getResources().getString(R.string.tabOppor));
    }

    private void initViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab_tasklist, R.id.tabTextTaskList);
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.tv_white),
                ContextCompat.getColor(this, R.color.tv_white));
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(this, R.color.tv_white),
                ContextCompat.getColor(this, R.color.tv_white));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void setupViews() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);
        mViewPager.setCurrentItem(0, true);
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
                    fragment = new AccountDetailTabFragment(AccountResultActivity.this, account);
                    break;
                case 1:
                    fragment = new ContactTabFragment(AccountResultActivity.this, account, productContact);
                    break;
                case 2:
                    fragment = new OpportunityTabFragment(AccountResultActivity.this, account);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void StartLocationTracking() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            GeneralParameter gp_distance = GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_DISTANCE_TRACKING);
            try {
                if (gp_distance != null) {
                    int distanceTracking = Integer.parseInt(gp_distance.getGs_value());
                    if (distanceTracking != 0) {
                        manager = new LocationTrackingManager(tm, lm, getApplicationContext());
                        manager.setMinimalDistanceChangeLocation(Integer.parseInt(GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM13_DIST").getGs_value()));
                        manager.setMinimalTimeChangeLocation(5);
                        manager.applyLocationListener(getApplicationContext());
                    }
                }
            } catch (Exception e) {
                manager = new LocationTrackingManager(tm, lm, getApplicationContext());
                manager.setMinimalDistanceChangeLocation(50);
                manager.setMinimalTimeChangeLocation(5);
                manager.applyLocationListener(getApplicationContext());
            }

            if (Global.LTM == null) {
                Global.LTM = manager;
            } else {
                try {
                    Global.LTM = null;
                    Global.LTM = manager;
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
