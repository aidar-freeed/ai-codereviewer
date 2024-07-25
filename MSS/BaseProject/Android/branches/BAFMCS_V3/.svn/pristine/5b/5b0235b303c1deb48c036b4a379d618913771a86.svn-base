package com.adins.mss.coll.dashboardcollection.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;
import com.adins.mss.coll.dummy.UserHelpCOLDummy;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.formatter.Tool;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DummyDashboardCollView extends Fragment implements TabLayout.BaseOnTabSelectedListener {

    private ConstraintLayout contentLayout;
    private ProgressBar targetProgress;
    private TextView outstandingTv, collectAmountProgress, percentageTv, outstandingAmountTv;
    private PieChart collResultPie;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ValueFormatter valueFormatter;

    //data holder
    private int collNum, ptpNum, failNum;
    private List<CollResultDetail> collectDetails;
    private List<CollResultDetail> ptpDetails;
    private List<CollResultDetail> failedDetails;
    private CollResultPagerAdapter tabAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dummy_dashboard_coll_view, container, false);
        contentLayout = view.findViewById(R.id.dummyDashContentLayout);
        targetProgress = view.findViewById(R.id.dummyDashTargetProgress);
        collResultPie = view.findViewById(R.id.dummyDashCollResultPie);
        tabLayout = view.findViewById(R.id.dummyDashDetailTab);
        viewPager = view.findViewById(R.id.dummyDashViewPager);
        collectAmountProgress = view.findViewById(R.id.dummyDashProgressValue);
        percentageTv = view.findViewById(R.id.dummyDashProgressPercent);
        outstandingTv = view.findViewById(R.id.dummyDashOutstandingValue);
        outstandingAmountTv = view.findViewById(R.id.dummyDashOutstandingAmount);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Sample " + getString(R.string.collector_dashboard));
        Global.positionStack.push(1);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabAdapter = new CollResultPagerAdapter(this.getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        initDummyData();

        UserHelpCOLDummy userHelpCOLDummy = new UserHelpCOLDummy();
        userHelpCOLDummy.showDashboardColl(getActivity(), DummyDashboardCollView.class.getSimpleName(), finishCallback, onDashboardTabSelected);
    }

    private void initDummyData() {

        setProgressBarValue(5000000, 20000000);
        setTotalOutstandingTask(7);
        setOutstandingAmount(10000000);
        setTaskCollectedNum(1);
        setTaskPTPNum(1);
        setTaskFailedNum(1);
        setTaskCollectedDetails();
        setTaskPTPDetails();
        setTaskFailedDetails();

        //finish setup data, draw dummy pie chart
        drawCollResultChart();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onTabSelected(tabLayout.getTabAt(0));
            }
        },1000);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    private void setTotalOutstandingTask(int outstandingTask) {
        outstandingTv.setText(outstandingTask + "");
    }

    private void setOutstandingAmount(double outstandingAmount) {
        String finToCollectAmount = Tool.formatToCurrency(outstandingAmount);
        outstandingAmountTv.setText(finToCollectAmount + " IDR");
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

    private void setTaskCollectedDetails() {
        List<CollResultDetail> temp = new ArrayList<>();
        temp.add(new CollResultDetail(0, "OJK0001", "Customer 1", "5000000"));
        this.collectDetails = temp;
    }

    private void setTaskPTPDetails() {
        List<CollResultDetail> temp = new ArrayList<>();
        temp.add(new CollResultDetail(1, "OJK0002", "Customer 2", "01-01-20"));
        this.ptpDetails = temp;
    }

    private void setTaskFailedDetails() {
        List<CollResultDetail> temp = new ArrayList<>();
        temp.add(new CollResultDetail(2, "OJK0003", "Customer 3", "Tidak Bertemu Konsumen"));
        this.failedDetails = temp;
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
            int[] colors = {Color.parseColor("#EA9431"), Color.parseColor("#008000"), Color.RED};
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

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    protected UserHelp.OnShowSequenceFinish finishCallback = new UserHelp.OnShowSequenceFinish() {
        @Override
        public void onSequenceFinish() {
            Global.positionStack.pop();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    protected  UserHelpCOLDummy.OnDashboardTabSelected onDashboardTabSelected = new UserHelpCOLDummy.OnDashboardTabSelected() {
        @Override
        public void onNextTab(int counter) {
            onTabSelected(tabLayout.getTabAt(counter));
        }
    };
}
