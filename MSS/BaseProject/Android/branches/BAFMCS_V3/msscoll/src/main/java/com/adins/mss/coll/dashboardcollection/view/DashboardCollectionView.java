package com.adins.mss.coll.dashboardcollection.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.TaskListFragment_new;
import com.adins.mss.base.todolist.form.TaskList_Fragment;
import com.adins.mss.coll.R;
import com.adins.mss.coll.dashboardcollection.DashboardCollDataSource;
import com.adins.mss.coll.dashboardcollection.DashboardCollectionContract;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.dashboardcollection.model.DashboardData;
import com.adins.mss.coll.dashboardcollection.presenter.DashboardCollPresenter;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardCollectionView extends Fragment implements TabLayout.BaseOnTabSelectedListener, DashboardCollectionContract.View {

    //android widget
    private ConstraintLayout contentLayout;
    private ProgressBar targetProgress;
    private TextView outstandingTv, collectAmountProgress, percentageTv, outstandingAmountTv;
    private PieChart collResultPie;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ValueFormatter valueFormatter;

    private DashboardCollectionContract.Presenter presenter;
    private android.app.ProgressDialog progressDialog;
    //data holder
    private int collNum, ptpNum, failNum;
    private List<CollResultDetail> collectDetails;
    private List<CollResultDetail> ptpDetails;
    private List<CollResultDetail> failedDetails;
    private CollResultPagerAdapter tabAdapter;

    //dialog builder
    private NiftyDialogBuilder dialogBuilder;

    private FirebaseAnalytics screenName;

    public DashboardCollectionView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_collection_view, container, false);
        contentLayout = view.findViewById(R.id.dashContentLayout);
        targetProgress = view.findViewById(R.id.dashTargetProgress);
        collResultPie = view.findViewById(R.id.dashCollResultPie);
        tabLayout = view.findViewById(R.id.dashDetailTab);
        viewPager = view.findViewById(R.id.dashViewPager);
        collectAmountProgress = view.findViewById(R.id.dashProgressValue);
        percentageTv = view.findViewById(R.id.dashProgressPercent);
        outstandingTv = view.findViewById(R.id.dashOutstandingValue);
        outstandingAmountTv = view.findViewById(R.id.dashOutstandingAmount);

        screenName = FirebaseAnalytics.getInstance(getActivity());

        outstandingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                    getActivity().getSupportFragmentManager().popBackStack();

                Fragment fragment;
                if (Global.PLAN_TASK_ENABLED) {
                    fragment = new TaskListFragment_new();
                    Bundle bundle = new Bundle();
                    bundle.putInt(TaskList_Fragment.BUND_KEY_PAGE, 1);
                    fragment.setArguments(bundle);
                } else {
                    fragment = new PriorityTabFragment();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getString(R.string.collector_dashboard));
        Global.positionStack.push(1);

        //init presenter and datasource
        presenter = new DashboardCollPresenter(this, new DashboardCollDataSource(getActivity().getApplication()));

        //set data for tabs
        tabLayout.addTab(tabLayout.newTab().setText("Collected"));
        tabLayout.addTab(tabLayout.newTab().setText("PTP"));
        tabLayout.addTab(tabLayout.newTab().setText("Failed"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        tabAdapter = new CollResultPagerAdapter(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        //load data
        if(needShowUserHelp()){
            showUserhelp();
        }
        else {
            initData();
        }
    }

    private void initData() {

        progressDialog = android.app.ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.please_wait_dialog), true);
        presenter.requestDashboardData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_dashboard_coll), null);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        if (Global.ENABLE_USER_HELP &&
                (Global.userHelpGuide.get(DummyDashboardCollView.class.getSimpleName()) != null) ||
                Global.userHelpDummyGuide.get(DummyDashboardCollView.class.getSimpleName()) != null) {
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.adins.mss.base.R.id.mnGuide) {
            if (!Global.BACKPRESS_RESTRICTION) {
                UserHelp.reloadUserHelp(getActivity(), DummyDashboardCollView.class.getSimpleName());
                if (needShowUserHelp()) {
                    showUserhelp();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawCollResultChart() {
        //init value formatter
        valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value == 0 ? "" : (int) value + "";
            }
        };

        List<PieEntry> pieEntries = new ArrayList<>();

        PieData pieData;
        if (ptpNum == 0 && failNum == 0 && collNum == 0) {
            pieEntries.add(new PieEntry(1, ""));
            PieDataSet dataSet = new PieDataSet(pieEntries, "");
            dataSet.setColors(Color.parseColor("#A9A9AA"));
            dataSet.setDrawValues(false);
            pieData = new PieData(dataSet);
        } else {
            PieEntry pieEntry1 = new PieEntry(ptpNum, "PTP");
            pieEntries.add(pieEntry1);

            PieEntry pieEntry2 = new PieEntry(collNum, "Collected");
            pieEntries.add(pieEntry2);

            PieEntry pieEntry3 = new PieEntry(failNum, "Failed");
            pieEntries.add(pieEntry3);

            PieDataSet dataSet = new PieDataSet(pieEntries, "");
            int[] colors = {Color.parseColor("#221E66"), Color.parseColor("#1D6304"), Color.parseColor("#C61E00")};
            dataSet.setColors(colors);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(12f);
            dataSet.setValueFormatter(valueFormatter);
            pieData = new PieData(dataSet);
        }

        collResultPie.getLegend().setEnabled(false);
        collResultPie.setDrawEntryLabels(false);
        collResultPie.setDrawHoleEnabled(true);
        collResultPie.setHoleColor(Color.parseColor("#00000000"));
        collResultPie.setTransparentCircleAlpha(0);
        collResultPie.setDescription(null);
        collResultPie.setHoleRadius(40f);
        collResultPie.setData(pieData);
        collResultPie.invalidate();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        //send data coll result details
        int idx = tab.getPosition();
        switch (idx) {
            case 0:
                tabAdapter.setDataToPage(tab.getPosition(), collectDetails);
                break;
            case 1:
                tabAdapter.setDataToPage(tab.getPosition(), ptpDetails);
                break;
            case 2:
                tabAdapter.setDataToPage(tab.getPosition(), failedDetails);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //no need to be implemented for now
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //no need to be implemented for now
    }

    //dashboard related UI callback
    private void setTotalOutstandingTask(int outstandingTask) {
        outstandingTv.setText(outstandingTask + "");
    }

    private void setOutstandingAmount(double outstandingAmount) {
        String finToCollectAmount = Tool.formatToCurrency(outstandingAmount);
        outstandingAmountTv.setText(finToCollectAmount + " IDR");
    }

    private void setProgressBarValue(double collectedAmount, double targetAmount) {
        //set percentage from target
        double percentage = collectedAmount / targetAmount;
        int finPercentage = (int) (percentage * 100);
        targetProgress.setProgress(finPercentage);
        targetProgress.setMax(100);

        if (percentage >= 100f) {
            Drawable drawable = targetProgress.getIndeterminateDrawable().mutate();
            drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            targetProgress.setIndeterminateDrawable(drawable);
            percentageTv.setTextColor(Color.GREEN);
        }

        percentageTv.setText(finPercentage + " %");

        String finCollectAmount, finTargetAmount;
        finCollectAmount = Tool.formatToCurrency(collectedAmount);
        finTargetAmount = Tool.formatToCurrency(targetAmount);
        collectAmountProgress.setText(finCollectAmount + " / " + finTargetAmount + " IDR");
    }

    private void setTaskCollectedNum(int collectedNum) {
        this.collNum = collectedNum;
    }

    private void setTaskPTPNum(int ptpNum) {
        this.ptpNum = ptpNum;
    }

    private void setTaskFailedNum(int failedNum) {
        this.failNum = failedNum;
    }

    private void setTaskCollectedDetails(List<CollResultDetail> resultList) {
        this.collectDetails = resultList;
    }

    private void setTaskPTPDetails(List<CollResultDetail> resultList) {
        this.ptpDetails = resultList;
    }

    public void setTaskFailedDetails(List<CollResultDetail> resultList) {
        this.failedDetails = resultList;
    }

    @Override
    public void onDashboardDataReceived(DashboardData dashboardData) {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if (dashboardData == null) {
            //hide all layout and show dialog
            showErrorInfo("No Data Available");
            return;
        }

        //setup data to view widget
        setProgressBarValue(dashboardData.getCollectedAmount(), dashboardData.getTargetAmount());
        setTotalOutstandingTask(dashboardData.getOutstandingNum());
        setOutstandingAmount(dashboardData.getOutstandingAmount());
        setTaskCollectedNum(dashboardData.getCollectDetails().size());
        setTaskPTPNum(dashboardData.getPtpDetails().size());
        setTaskFailedNum(dashboardData.getFailedDetails().size());
        setTaskCollectedDetails(dashboardData.getCollectDetails());
        setTaskPTPDetails(dashboardData.getPtpDetails());
        setTaskFailedDetails(dashboardData.getFailedDetails());

        //finish setup data, draw chart
        drawCollResultChart();
        //trigger listener for first tab
        onTabSelected(tabLayout.getTabAt(0));
    }

    @Override
    public void showErrorInfo(String errorMessage) {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if (dialogBuilder == null) {
            dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        }
        contentLayout.setVisibility(View.GONE);
        dialogBuilder.withTitle(getString(R.string.error))
                .withMessage(errorMessage);
        dialogBuilder.show();
    }

    private boolean needShowUserHelp() {
        List<UserHelpViewDummy> userHelpViews = Global.userHelpDummyGuide.get(DummyDashboardCollView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null && userHelpViews.size() > 0;
    }

    private void showUserhelp() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment dummyDashboard = new DummyDashboardCollView();
        transaction.replace(R.id.content_frame, dummyDashboard);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
