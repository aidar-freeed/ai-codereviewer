package com.adins.mss.coll.loyalti.pointacquisitionmonthly;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.DashBoardFragment;
import com.adins.mss.coll.loyalti.barchart.LoyaltyBarChartRenderer;
import com.adins.mss.coll.loyalti.barchart.LoyaltyBarDataSet;
import com.adins.mss.coll.loyalti.barchart.LoyaltyXLabelFormatter;
import com.adins.mss.coll.loyalti.barchart.NonScrollListView;
import com.adins.mss.coll.loyalti.barchart.PointClickMarker;
import com.adins.mss.coll.loyalti.barchart.pointlegends.PointLegendsAdapter;
import com.adins.mss.coll.loyalti.barchart.ranklegends.RankLegendsAdapter;
import com.adins.mss.coll.loyalti.pointacquisitiondaily.DailyPointsChartView;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts.ILoyaltyPointsDataSource;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.contracts.MonthlyPointContract;
import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.LoyaltyPointsRequest;
import com.adins.mss.coll.models.loyaltymodels.PointDetail;
import com.adins.mss.coll.models.loyaltymodels.RankDetail;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthlyPointsChartView extends Fragment implements MonthlyPointContract.View,OnChartGestureListener {

    //android views
    private ConstraintLayout chartContent;
    private ConstraintLayout legendContainer;
    private BarChart barChart;
    private NonScrollListView rankLegends;
    private NonScrollListView pointLegends;
    private TextView totalPointsV;
    private ProgressDialog progressDialog;
    private NiftyDialogBuilder dialogBuilder;
    private NestedScrollView scrollView;

    //presenter
    private MonthlyPointContract.Presenter presenter;

    //chart data
    private int averagePoint;
    private int maxPoint;
    private int initialIdx;
    private float[][] pointDataSet;
    private RankDetail[][] rankDataSet;
    private List<RankDetail> rankDetails;
    private List<PointDetail> pointDetailsDataSet;
    private List<PointDetail> pointDetails;
    private LoyaltyXLabelFormatter xLabelFormatter;

    //legend colors
    int[] pointDetailColors = {Color.parseColor("#FBBA72")
            ,Color.parseColor("#F86624"),
            Color.parseColor("#BA5624") ,
            Color.parseColor("#8F250C")};

    int[] rankColors = {Color.parseColor("#FF0000"),
            Color.parseColor("#000000"),
            Color.parseColor("#32CD32"),
            Color.parseColor("#0000FF")};
    private Handler handler;

    public MonthlyPointsChartView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_acquisition, container, false);
        chartContent = view.findViewById(R.id.chartContent);
        barChart = view.findViewById(R.id.monthlyChart);
        rankLegends = view.findViewById(R.id.legendRanks);
        pointLegends = view.findViewById(R.id.legendPoints);
        totalPointsV = view.findViewById(R.id.totalPoint);
        scrollView = view.findViewById(R.id.scrollView);
        legendContainer = view.findViewById(R.id.legendsContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String programName = getArguments()!=null?getArguments().getString("MembershipProgramName"):getString(R.string.monthly_point_page_title);
        String programStartDate = getArguments()!=null?getArguments().getString("ProgramStartDate"):"";
        getActivity().setTitle(programName);
        handler = new Handler(Looper.getMainLooper());

        Global.positionStack.push(1);
        chartContent.setVisibility(View.GONE);

        ILoyaltyPointsDataSource dataSource = new LoyaltyPointsDataSource(getActivity().getApplication());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        MonthlyPointsLogic monthlyPointsLogic = new MonthlyPointsLogic(dataSource,programStartDate,calendar.get(Calendar.YEAR));
        MonthlyPointsPresenter presenter = new MonthlyPointsPresenter(this,monthlyPointsLogic);
        setPresenter(presenter);
        if(needShowUserHelp()){
            showUserhelp();
        }
        else {
            loadMonthlyPointsData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
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
        if(item.getItemId() == com.adins.mss.base.R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                UserHelp.reloadUserHelp(getActivity(), DummyMonthlyPointView.class.getSimpleName());
                if(needShowUserHelp()){
                    showUserhelp();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(MonthlyPointContract.Presenter presenter) {
        this.presenter = presenter;
    }

    //kode request poin bulanan
    private void loadMonthlyPointsData() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait_dialog), true);

        //dummy request
        LoyaltyPointsRequest request = new LoyaltyPointsRequest();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.addImeiAndroidIdToUnstructured();
        String loginid = GlobalData.getSharedGlobalData().getUser().getLogin_id();
        String[] loginIdSplit = loginid.split("@");
        request.loginId = loginIdSplit[0];
        String programCode = "";
        Bundle bundle = getArguments();
        if(bundle!=null)
            programCode = bundle.getString("MembershipProgramCode");
        request.membershipProgramId = programCode;

        presenter.getMonthlyPointsData(request);
    }

    //callback data poin bulanan
    @Override
    public void onDataReceived(float[][] pointDataSet, RankDetail[][] rankDataSet, List<PointDetail> pointDetailsDataSet) {
        chartContent.setVisibility(View.VISIBLE);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if(pointDataSet.length < 3){
            //padding data to avoid large bar width
            float[][] newDataSet = paddingDataSet(pointDataSet,3-pointDataSet.length);
            for(int i = pointDataSet.length; i<newDataSet.length; i++){
                for (int j=0; j<newDataSet[i].length; j++){
                    PointDetail dummyPointDetail = new PointDetail(pointDetailsDataSet.get(0).rewardProgram
                            ,"0");
                    pointDetailsDataSet.add(dummyPointDetail);
                }
            }
            pointDataSet = newDataSet;
        }
        this.pointDetailsDataSet = pointDetailsDataSet;
        initView(pointDataSet,rankDataSet);
    }

    private float[][] paddingDataSet(float[][] pointDataSet,int padNumber){
        float[][] newDataset = new float[pointDataSet.length+padNumber][];
        //copy values
        for (int i=0; i<pointDataSet.length; i++){
            newDataset[i] = pointDataSet[i];
        }
        //pad values
        for (int j=pointDataSet.length; j<newDataset.length; j++){
            newDataset[j] = new float[]{0};
        }

        return newDataset;
    }

    @Override
    public void onGetDataFailed(String message) {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        chartContent.setVisibility(View.GONE);
        if(dialogBuilder == null){
            dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        }
        dialogBuilder.withTitle(getString(R.string.error))
                .withMessage(message);
        dialogBuilder.show();
    }

    //kode untuk chart
    private void setPointDataSet(float[][] pointDataSet) {
        this.pointDataSet = pointDataSet;
    }

    private void setRankDataSet(RankDetail[][] rankDataSet) {
        this.rankDataSet = rankDataSet;
        //set rank colors
        for(int i=0; i<this.rankDataSet.length; i++){
            for (int j=0; j<this.rankDataSet[i].length; j++)
                this.rankDataSet[i][j].colorValue = rankColors[j];
        }
    }

    private void setTotalPoint(int totalPoint) {
        totalPointsV.setText("Total: "+totalPoint);
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
        for(int i=0; i<pointDetails.size(); i++){
            pointDetails.get(i).colorValue = pointDetailColors[i];
        }
        this.pointDetails = pointDetails;
    }

    public void setInitIdx(int initIdx){
        initialIdx = initIdx;
    }

    public void initView(float[][] pointDataSet, RankDetail[][] rankDataSet){
        //set dataset
        setPointDataSet(pointDataSet);
        setRankDataSet(rankDataSet);
        //current month
        setInitIdx(presenter.getCurrentMonth());
        //avg
        setAveragePoint((int)presenter.getAvgPoint());
        //max point
        setMaxPoint(presenter.getMaxPoint());
        //total point
        setTotalPoint(presenter.getTotalPoints());

        //set months formatter
        String[] monthLabels = presenter.getMonths();
        int padData = pointDataSet.length - monthLabels.length;//check different size
        if(padData > 0){
            String[] padMonthLabels = new String[monthLabels.length + padData];
            for(int i=0; i<padMonthLabels.length; i++){
                padMonthLabels[i] = "";//set default label first
                if(i < monthLabels.length){
                    padMonthLabels[i] = monthLabels[i];
                }
            }
            monthLabels = padMonthLabels;
        }
        LoyaltyXLabelFormatter xLabelFormatter = new LoyaltyXLabelFormatter(monthLabels);

        setxLabelFormatter(xLabelFormatter);
        //set point details
        setPointsLegend(presenter.getPointDetails());
        //set ranks
        setRanksLegend(presenter.getRanks());
        //draw chart
        drawChart();
    }

    private boolean needShowUserHelp(){
        List<UserHelpView> userHelpViews = Global.userHelpGuide.get(DummyMonthlyPointView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null && userHelpViews.size() > 0;
    }

    private void showUserhelp(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment dummyChart = new DummyMonthlyPointView();
        transaction.replace(R.id.content_frame,dummyChart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void drawChart(){
        if(pointDataSet == null || pointDataSet.length == 0)
            return;

        //set chart pointDataSet
        List<BarEntry> barEntryList = new ArrayList<>();
        for(int i = 0; i< pointDataSet.length; i++){
            barEntryList.add(new BarEntry(i, pointDataSet[i]));
        }

        int[] stackBarColor = new int[pointDetails.size()];
        for(int i=0; i<stackBarColor.length; i++){
            stackBarColor[i] = pointDetails.get(i).colorValue;
        }
        BarDataSet barDataSet = new LoyaltyBarDataSet(pointDetailsDataSet,pointDetails,barEntryList,"");
        barDataSet.setColors(stackBarColor);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);
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
        RankLegendsAdapter rankLegendsAdapter = new RankLegendsAdapter(getActivity(),rankDetails);
        rankLegends.setAdapter(rankLegendsAdapter);
        PointLegendsAdapter pointLegendsAdapter = new PointLegendsAdapter(getActivity(),pointDetails);
        pointLegends.setAdapter(pointLegendsAdapter);

        //set limit line
        LimitLine avgLine = new LimitLine(averagePoint,getString(R.string.average_point,averagePoint));
        avgLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        avgLine.enableDashedLine(20f,10f,0f);
        avgLine.setLineWidth(1f);
        avgLine.setTextColor(Color.parseColor("#32CD32"));
        avgLine.setTextSize(12f);
        avgLine.setLineColor(Color.parseColor("#32CD32"));
        lYAxis.addLimitLine(avgLine);

        //chart settings
        //custom render bar
        LoyaltyBarChartRenderer renderer = new LoyaltyBarChartRenderer(barChart,rankDataSet,0.3f,0);
        barChart.setRenderer(renderer);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setExtraOffsets(0,0,0,10f);
        barChart.setFitBars(true);
        barChart.setData(barData);
        PointClickMarker marker = new PointClickMarker(30f,0);
        barChart.setMarker(marker);

        //set gesture listener on this
        barChart.setOnChartGestureListener(this);
        //chart viewport setting by initial x axis
        if(initialIdx > pointDataSet.length - 1){
            initialIdx = 0;
        }
        barChart.moveViewToX(initialIdx  - 0.7f);
        //chart viewport range
        barChart.setVisibleXRangeMaximum(4f);
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        float tappedX = me.getX();
        float tappedY = me.getY();
        MPPointD point = barChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(tappedX, tappedY);
        if(point.y < 0){
            int idx = (int)Math.round(point.x);
            int xLabelSize = barChart.getXAxis().getLabelCount();
            if(idx < 0 || idx >= xLabelSize)
                return;

            GroupPointData pointData = presenter.getPointDataAt(idx);
            if(pointData != null)
                goToDailyPointsChart(pointData);
        }
    }

    private void goToDailyPointsChart(GroupPointData groupPointData){
        Bundle bundle = new Bundle();
        bundle.putString("GroupPoint",groupPointData.groupPoint);
        String programCode = getArguments()!=null?getArguments().getString("MembershipProgramCode"):"";
        bundle.putString("MembershipProgramCode",programCode);
        String programName = getArguments()!=null?getArguments().getString("MembershipProgramName"):"";
        bundle.putString("MembershipProgramName",programName);
        String programStartDate = getArguments()!=null?getArguments().getString("ProgramStartDate"):"";
        bundle.putString("ProgramStartDate",programStartDate);

        //fragment transaction
        DailyPointsChartView dailyPointsChart = new DailyPointsChartView();
        dailyPointsChart.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, dailyPointsChart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //below chart listener implementation not used for now
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    private boolean checkUserHelpAvailability() {
        List<UserHelpView> userHelpViews = Global.userHelpGuide.get(DummyMonthlyPointView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpViews != null;
    }
}
