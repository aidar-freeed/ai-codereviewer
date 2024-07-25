package com.adins.mss.base.loyalti.dailypointacquisition;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.barchart.LoyaltyBarChartRenderer;
import com.adins.mss.base.loyalti.barchart.LoyaltyBarDataSet;
import com.adins.mss.base.loyalti.barchart.LoyaltyXLabelFormatter;
import com.adins.mss.base.loyalti.barchart.NonScrollListView;
import com.adins.mss.base.loyalti.barchart.PointClickMarker;
import com.adins.mss.base.loyalti.barchart.pointlegends.PointLegendsAdapter;
import com.adins.mss.base.loyalti.barchart.ranklegends.RankLegendsAdapter;
import com.adins.mss.base.loyalti.dailypointacquisition.contracts.DailyPointContract;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;
import com.adins.mss.base.loyalti.monthlypointacquisition.LoyaltyPointsDataSource;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;
import com.adins.mss.base.loyalti.userhelpdummy.DummyDailyPointsView;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.DialogManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.MPPointD;
import com.google.firebase.analytics.FirebaseAnalytics;


import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyPointsChartView extends Fragment implements DailyPointContract.View, OnChartGestureListener {

    //android views
    private ConstraintLayout chartContent;
    private BarChart barChart;
    private NonScrollListView rankLegends;
    private NonScrollListView pointLegends;
    private ProgressDialog progressDialog;
    private TextView selectedMonthPoint;
    private ConstraintLayout chartHeader;
    private TextView currentMonthPointText;

    private FirebaseAnalytics screenName;

    //presenter
    private DailyPointContract.Presenter presenter;

    //chart data
    private int averagePoint;
    private int maxPoint;
    private int initialIdx;
    private float[][] pointDataSet;
    private RankDetail[][] rankDataSet;
    private List<RankDetail> rankDetails;
    private List<PointDetail> pointDetails;
    private List<PointDetail> pointDetailsDataSet;
    private LoyaltyXLabelFormatter xLabelFormatter;

    //chart colors
    int[] pointDetailColors;
    int[] rankColors;

    public DailyPointsChartView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_point_acquisition, container, false);
        chartContent = view.findViewById(R.id.chartContent);
        barChart = view.findViewById(R.id.monthlyChart);
        rankLegends = view.findViewById(R.id.legendRanks);
        pointLegends = view.findViewById(R.id.legendPoints);

        selectedMonthPoint = view.findViewById(R.id.currentMonthPoint);
        chartHeader = view.findViewById(R.id.chartHeader);
        currentMonthPointText = view.findViewById(R.id.currentMonthPointText);

        Bundle bundle = this.getArguments();
        String[] groupPoint = bundle.getString("GroupPoint").split("-");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(groupPoint[0]) - 1);

        //set daily point logic
        String programStartDate = getArguments() != null ? getArguments().getString("ProgramStartDate") : "";
        int displayMonth = Integer.parseInt(groupPoint[0]) - 1;
        int displayYear = Integer.parseInt(groupPoint[1]);
        ILoyaltyPointsDataSource dataSource = new LoyaltyPointsDataSource(getActivity().getApplication());
        DailyPointsLogic dailyPointsLogic = new DailyPointsLogic(dataSource, programStartDate, displayMonth, displayYear);
        setPresenter(new DailyPointsPresenter(this, dailyPointsLogic));

        screenName = FirebaseAnalytics.getInstance(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String programName = getArguments() != null ? getArguments().getString("MembershipProgramName") : getString(R.string.daily_point_page_title);
        getActivity().setTitle(programName);
        Global.positionStack.push(1);
        chartContent.setVisibility(View.GONE);
        if (needShowUserHelp()) {
            showUserhelp();
        } else {
            loadDailyPointsData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeChartColor(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_daily_graph), null);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(checkUserHelpAvailability()){
            menu.findItem(R.id.mnGuide).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.adins.mss.base.R.id.mnGuide && !Global.BACKPRESS_RESTRICTION) {
            UserHelp.reloadUserHelp(getActivity(), DummyDailyPointsView.class.getSimpleName());
            if (needShowUserHelp()) {
                showUserhelp();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean needShowUserHelp() {
        List<UserHelpViewDummy> userHelpDummyViews = Global.userHelpDummyGuide.get(DummyDailyPointsView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpDummyViews != null && !userHelpDummyViews.isEmpty();
    }

    private void showUserhelp() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment dummyChart = new DummyDailyPointsView();
        transaction.replace(R.id.content_frame,dummyChart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setPresenter(DailyPointContract.Presenter presenter) {
        this.presenter = presenter;
    }

    //kode request poin bulanan
    private void loadDailyPointsData() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait_dialog), true);

        //dummy request
        LoyaltyPointsRequest request = new LoyaltyPointsRequest();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        request.addImeiAndroidIdToUnstructured();
        String loginid = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        String[] loginIdSplit = loginid.split("@");
        request.loginId = loginIdSplit[0];
        Bundle bundle = getArguments();
        if (bundle != null) {
            request.membershipProgramId = bundle.getString("MembershipProgramCode");
            request.pointGroup = bundle.getString("GroupPoint");
        }
        presenter.getDailyPointsData(request);
    }

    //callback data poin bulanan
    @Override
    public void onDataReceived(float[][] pointDataSet, RankDetail[][] rankDataSet, List<PointDetail> pointDetailsDataSet) {
        chartContent.setVisibility(View.VISIBLE);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if (pointDataSet.length < 6) {
            //padding data to avoid large bar width
            float[][] newDataSet = paddingDataSet(pointDataSet, 6 - pointDataSet.length);
            for (int i = pointDataSet.length; i < newDataSet.length; i++) {
                for (int j = 0; j < newDataSet[i].length; j++) {
                    PointDetail dummyPointDetail = new PointDetail(pointDetailsDataSet.get(0).rewardProgram
                            , "0");
                    pointDetailsDataSet.add(dummyPointDetail);
                }
            }
            pointDataSet = newDataSet;
        }
        this.pointDetailsDataSet = pointDetailsDataSet;
        initView(pointDataSet, rankDataSet);
    }

    private float[][] paddingDataSet(float[][] pointDataSet, int padNumber) {
        float[][] newDataset = new float[pointDataSet.length + padNumber][];
        //copy values
        System.arraycopy(pointDataSet,0,newDataset,0,pointDataSet.length);
        //pad values
        for (int j = pointDataSet.length; j < newDataset.length; j++) {
            newDataset[j] = new float[]{0};
        }

        return newDataset;
    }

    @Override
    public void onGetDataFailed(String message) {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        DialogManager.showAlert(getActivity(), DialogManager.TYPE_ERROR, message, getString(R.string.error));
        chartContent.setVisibility(View.GONE);
    }

    //kode untuk chart
    private void setPointDataSet(float[][] pointDataSet) {
        this.pointDataSet = pointDataSet;
    }

    private void setRankDataSet(RankDetail[][] rankDataSet) {
        this.rankDataSet = rankDataSet;
        //set rank colors
        for (int i = 0; i < this.rankDataSet.length; i++) {
            for (int j = 0; j < this.rankDataSet[i].length; j++)
                this.rankDataSet[i][j].colorValue = rankColors[j];
        }
    }

    private void setAveragePoint(int averagePoint) {
        this.averagePoint = averagePoint;
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    private void setxLabelFormatter(LoyaltyXLabelFormatter xLabelFormatter) {
        this.xLabelFormatter = xLabelFormatter;
    }

    private void setRanksLegend(List<RankDetail> rankDetails) {
        this.rankDetails = rankDetails;
    }

    private void setPointsLegend(List<PointDetail> pointDetails) {
        for (int i = 0; i < pointDetails.size(); i++) {
            pointDetails.get(i).colorValue = pointDetailColors[i];
        }
        this.pointDetails = pointDetails;
    }

    public void setInitIdx(int initIdx) {
        initialIdx = initIdx;
    }

    public void initView(float[][] pointDataSet, RankDetail[][] rankDataSet) {
        //set dataset
        setPointDataSet(pointDataSet);
        setRankDataSet(rankDataSet);
        //current month
        setInitIdx(presenter.getCurrentDay());
        //avg
        setAveragePoint((int) presenter.getAvgPoint());
        //max point
        setMaxPoint(presenter.getMaxPoint());
        //current month total point
        chartHeader.setVisibility(View.VISIBLE);
        setSelectedMonthTotalPoint(presenter.getTotalPoints());
        //set days formatter
        String[] daysLabels = presenter.getDays();
        int padData = pointDataSet.length - daysLabels.length;//check different size
        if (padData > 0) {
            String[] padDayLabels = new String[daysLabels.length + padData];
            for (int i = 0; i < padDayLabels.length; i++) {
                padDayLabels[i] = "";//set default label first
                if (i < daysLabels.length) {
                    padDayLabels[i] = daysLabels[i];
                }
            }
            daysLabels = padDayLabels;
        }
        LoyaltyXLabelFormatter xLabelFormat = new LoyaltyXLabelFormatter(daysLabels);
        setxLabelFormatter(xLabelFormat);
        //set point details
        setPointsLegend(presenter.getPointDetails());
        //set ranks
        setRanksLegend(presenter.getRanks());
        //draw chart
        drawChart();
    }

    private void drawChart() {
        if (pointDataSet == null || pointDataSet.length == 0)
            return;

        //set chart pointDataSet
        List<BarEntry> barEntryList = new ArrayList<>();
        for (int i = 0; i < pointDataSet.length; i++) {
            barEntryList.add(new BarEntry(i, pointDataSet[i]));
        }

        int[] stackBarColor = new int[pointDetails.size()];
        for (int i = 0; i < stackBarColor.length; i++) {
            stackBarColor[i] = pointDetails.get(i).colorValue;
        }
        BarDataSet barDataSet = new LoyaltyBarDataSet(pointDetailsDataSet, pointDetails, barEntryList, "");
        barDataSet.setColors(stackBarColor);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        barData.setBarWidth(0.5f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(xLabelFormatter.getLabelCount());
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(xLabelFormatter);
        xAxis.setTextSize(12f);

        //right axis
        YAxis rYAxis = barChart.getAxisRight();
        rYAxis.setEnabled(false);
        rYAxis.setAxisMinimum(0f);
        rYAxis.setDrawGridLines(false);

        //left axis
        YAxis lYAxis = barChart.getAxisLeft();
        lYAxis.setAxisMinimum(0f);
        lYAxis.setAxisMaximum(maxPoint);

        //use custom legends
        barChart.getLegend().setEnabled(false);
        barChart.setDescription(null);
        RankLegendsAdapter rankLegendsAdapter = new RankLegendsAdapter(getActivity(), rankDetails);
        rankLegends.setAdapter(rankLegendsAdapter);
        PointLegendsAdapter pointLegendsAdapter = new PointLegendsAdapter(getActivity(), pointDetails);
        pointLegends.setAdapter(pointLegendsAdapter);

        //set limit line
        LimitLine avgLine = new LimitLine(averagePoint, getString(R.string.average_point, averagePoint));
        avgLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        avgLine.enableDashedLine(20f, 10f, 0f);
        avgLine.setLineWidth(1f);
        avgLine.setTextColor(Color.parseColor("#32CD32"));
        avgLine.setTextSize(12f);
        avgLine.setLineColor(Color.parseColor("#32CD32"));
        lYAxis.addLimitLine(avgLine);

        //chart settings
        //custom render bar
        LoyaltyBarChartRenderer renderer = new LoyaltyBarChartRenderer(barChart, rankDataSet, 0.6f, 8f);
        barChart.setRenderer(renderer);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setExtraOffsets(0, 0, 0, 20f);
        barChart.setFitBars(true);
        barChart.setData(barData);
        PointClickMarker marker = new PointClickMarker(20f, 10f);
        barChart.setMarker(marker);

        //set gesture listener on this
        barChart.setOnChartGestureListener(this);
        //chart viewport setting by initial x axis
        if (initialIdx > pointDataSet.length - 1) {
            initialIdx = 0;
        }
        barChart.moveViewToX(initialIdx - 0.7f);
        //chart viewport range
        barChart.setVisibleXRangeMaximum(6f);
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        float tappedX = me.getX();
        float tappedY = me.getY();
        MPPointD point = barChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(tappedX, tappedY);
        if (point.y < 0) {
            int idx = (int) Math.round(point.x);
            int xLabelSize = barChart.getXAxis().getLabelCount();
            if (idx < 0 || idx >= xLabelSize)
                return;
        }
    }

    //below chart listener implementation not used for now
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //this event handler not used for now
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //this event handler not used for now
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        //this event handler not used for now
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        //this event handler not used for now
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        //this event handler not used for now
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        //this event handler not used for now
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        //this event handler not used for now
    }

    public void setSelectedMonthTotalPoint(int totalPointForCurrentMonth) {

        //SET MONTH LABEL
        Bundle bundle = this.getArguments();
        String[] groupPoint = bundle.getString("GroupPoint").split("-");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,Integer.valueOf(groupPoint[0]) - 1);
        String monthNames = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault());
        currentMonthPointText.setText(getString(R.string.month_total_points, monthNames));

        //SET TOTAL POINT FOR SELECTED MONTH
        selectedMonthPoint.setText(String.valueOf(totalPointForCurrentMonth));

    }

    private boolean checkUserHelpAvailability() {
        List<UserHelpViewDummy> userHelpDummyViews = Global.userHelpDummyGuide.get(DummyDailyPointsView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpDummyViews != null;
    }

    private void initializeChartColor(Context mContext){
        pointDetailColors = new int[] {
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor1),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor2),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor3),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor4),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor5)
        };

        rankColors = new int[] {
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor1),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor2),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor3),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor4)
        };
    }
}

