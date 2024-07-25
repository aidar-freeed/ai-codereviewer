package com.adins.mss.odr.accounts;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.GroupTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.ActivityTodoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/21/2017.
 */

public class OpportunityDetailActivity extends AppCompatActivity {

    public LocationTrackingManager manager;
    private TextView headerId;
    private TextView headerStatus;
    private TextView headerProduct;
    private RecyclerView listTodo;
    private RecyclerView listHistory;
    private RecyclerView.LayoutManager lmTodo;
    private RecyclerView.LayoutManager lmHistory;
    private ActivityTodoAdapter todoAdapter;
    private ActivityTodoAdapter historyAdapter;
    private List<TaskH> todo = new ArrayList<>();
    private List<TaskH> history = new ArrayList<>();

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

        setContentView(R.layout.opportunity_detail_activity);

        Bundle bundle = getIntent().getExtras();
        String groupTaskId = bundle.getString(Global.BUND_KEY_GROUPTASK_ID);
        List<String> taskTodo = bundle.getStringArrayList("taskTodo");
        List<String> taskHistory = bundle.getStringArrayList("taskHistory");
        GroupTask groupTask = GroupTaskDataAccess.getOneHeader(getApplicationContext(), groupTaskId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_mn_account));
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        headerId = (TextView) findViewById(R.id.headerId);
        headerStatus = (TextView) findViewById(R.id.headerStatus);
        headerProduct = (TextView) findViewById(R.id.headerProduct);
        listTodo = (RecyclerView) findViewById(R.id.listTodo);
        listHistory = (RecyclerView) findViewById(R.id.listHistory);
        lmTodo = new LinearLayoutManager(this);
        lmHistory = new LinearLayoutManager(this);
        listTodo.setLayoutManager(lmTodo);
        listHistory.setLayoutManager(lmHistory);

        headerId.setText(groupTask.getGroup_task_id());
        headerStatus.setText(groupTask.getLast_status());
        headerProduct.setText(groupTask.getProduct_name());

        if (taskTodo != null) {
            for (int i = taskTodo.size()-1; i >= 0; i--) {
                TaskH task = TaskHDataAccess.getOneHeader(getApplicationContext(), taskTodo.get(i));
                todo.add(task);
            }

        }
        if (taskHistory != null) {
            for (int i = taskHistory.size()-1; i >= 0; i--) {
                TaskH task = TaskHDataAccess.getOneHeader(getApplicationContext(), taskHistory.get(i));
                history.add(task);
            }
        }

        if (todo != null) {
            todoAdapter = new ActivityTodoAdapter(this, todo);
            listTodo.setAdapter(todoAdapter);
        }
        if (history != null) {
            historyAdapter = new ActivityTodoAdapter(this, history);
            listHistory.setAdapter(historyAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
