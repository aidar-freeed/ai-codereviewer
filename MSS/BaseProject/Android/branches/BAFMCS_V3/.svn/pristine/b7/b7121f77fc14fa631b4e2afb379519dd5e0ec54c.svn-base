package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.coll.Navigator;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.fragments.DepositReportDetailActivity;
import com.adins.mss.coll.interfaces.DepositReportImpl;
import com.adins.mss.coll.interfaces.DepositReportInterface;
import com.adins.mss.coll.interfaces.NavigatorInterface;
import com.adins.mss.coll.models.DepositReportAdapter;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.foundation.db.dataaccess.DepositReportHDataAccess;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class DepositReportView extends ViewManager implements ThemeLoader.ColorSetLoaderCallback {
    private Activity activity;
    private Context context;
    private DepositReportAdapter adapter;
    private NavigatorInterface navigator;
    private DepositReportInterface iDepositReport;
    private static Menu mainMenu;
    private View view;
    Button recapitulateButton;

    public DepositReportView(Activity activity){
        super(activity);
        this.activity = activity;
        this.context  = activity.getApplicationContext();
        this.navigator= new Navigator(context);
        iDepositReport= new DepositReportImpl(context);
    }

    public View layoutInflater(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.new_fragment_deposit_report, container, false);
        return view;
    }

    @Override
    public void publish() {
        //Prepare Print Item for Deposit
        iDepositReport.insertPrintItemForDeposit();
    }

    @Override
    public void onCreate() {
        recapitulateButton = (Button) view.findViewById(R.id.btnRecapitulate);
        recapitulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.route(Navigator.DEPOSIT_REPORT_RECAPITULATE);
                //recapitulate();
            }
        });
        //Cleanup deposit report before today
        cleanup();
        //Load Data From Server
        iDepositReport.getDepositReportH(activity, this);
    }

    private void loadSavedTheme(){
        ThemeLoader themeLoader = new ThemeLoader(context);
        themeLoader.loadSavedColorSet(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
    }

    @Override
    public void onOptionsItemSelected(int id) {
        switch (id) {
            case R.id.menuMore:
                mainMenu.findItem(R.id.recapitulation).setVisible(true);
                mainMenu.findItem(R.id.summary).setVisible(true);
                mainMenu.findItem(R.id.mnViewMap).setVisible(false);
                break;

            case R.id.recapitulation:
                navigator.route(Navigator.DEPOSIT_REPORT_RECAPITULATE);
                break;

            case R.id.summary:
                navigator.route(Navigator.DEPOSIT_REPORT_SUMMARIZE);
                break;
            default:
                break;
        }
    }

    private void cleanup() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();

        DepositReportHDataAccess.deleteDepositReport(activity, today);
    }

    @Override
    public void onCompleteTask(Object result) {
        final List<DepositReportH> reports = (List<DepositReportH>) result;
        ListView list = (ListView) activity.findViewById(R.id.listRecapitulation);
        adapter = new DepositReportAdapter(context, reports);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO Auto-generated method stub
                DepositReportDetailActivity.report = reports.get(position);
                Intent intent = new Intent(context, DepositReportDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        if(dynamicTheme != null && dynamicTheme.getThemeItemList().size() > 0){
            applyTheme(dynamicTheme);
        }
    }

    private  void applyTheme(DynamicTheme dynamicTheme){
        int btnPressedColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_pressed"));
        int btnNormalColor = Color.parseColor(ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_normal"));
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] {}  // normal
        };

        int[] colorlist = new int[]{
                btnPressedColor,
                btnNormalColor
        };
        ColorStateList colorStateList = new ColorStateList(states,colorlist);
        ThemeUtility.setViewBackground(recapitulateButton,colorStateList);
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {

    }
}
