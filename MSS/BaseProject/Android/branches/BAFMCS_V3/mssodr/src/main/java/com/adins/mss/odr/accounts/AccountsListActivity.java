package com.adins.mss.odr.accounts;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.AccountListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/16/2017.
 */

public class AccountsListActivity extends AppCompatActivity {

    private ArrayList<String> uuidAccounts;
    private List<Account> accountList;
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    public LocationTrackingManager manager;
    public static FragmentManager fragmentManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
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

        uuidAccounts = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        uuidAccounts = bundle.getStringArrayList(Global.BUND_KEY_ACCOUNT_ID);

        setContentView(R.layout.new_account_list_activity);

        list = (RecyclerView) findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        list.setLayoutManager(layoutManager);

        accountList = new ArrayList<>();
        if (uuidAccounts != null) {
            for (String uuid : uuidAccounts) {
                Account account = AccountDataAccess.getOne(getApplicationContext(), uuid);
                accountList.add(account);
            }
        }

        adapter = new AccountListAdapter(AccountsListActivity.this, accountList);
        list.setAdapter(adapter);

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(com.adins.mss.base.R.string.title_mn_account));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.freeMemory();
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
