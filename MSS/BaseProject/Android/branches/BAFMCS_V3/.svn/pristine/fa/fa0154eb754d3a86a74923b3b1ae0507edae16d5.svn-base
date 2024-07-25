package com.adins.mss.base.loyalti.monthlypointacquisition;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.barchart.LoyaltyBarChartRenderer;
import com.adins.mss.base.loyalti.barchart.LoyaltyBarDataSet;
import com.adins.mss.base.loyalti.barchart.LoyaltyXLabelFormatter;
import com.adins.mss.base.loyalti.barchart.NonScrollListView;
import com.adins.mss.base.loyalti.barchart.PointClickMarker;
import com.adins.mss.base.loyalti.barchart.pointlegends.PointLegendsAdapter;
import com.adins.mss.base.loyalti.barchart.ranklegends.RankLegendsAdapter;
import com.adins.mss.base.loyalti.dailypointacquisition.DailyPointsChartView;
import com.adins.mss.base.loyalti.model.GroupPointData;
import com.adins.mss.base.loyalti.model.LoyaltyPointsRequest;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.ILoyaltyPointsDataSource;
import com.adins.mss.base.loyalti.monthlypointacquisition.contract.MonthlyPointContract;
import com.adins.mss.base.loyalti.userhelpdummy.DummyMonthlyPointView;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
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
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthlyPointsChartView extends Fragment implements MonthlyPointContract.View, OnChartGestureListener {

    //android views
    private Context mContext;
    private ConstraintLayout chartContent;
    private BarChart barChart;
    private NonScrollListView rankLegends;
    private NonScrollListView pointLegends;
    private TextView totalPointsV;
    private TextView currentMonthPoint;
    private TextView dailyAverageMonth;
    private TextView dailyAveragePeriod;
    private TextView preCurrentMonthPoint;
    private TextView preDailyAverageMonth;
    private TextView preTotalPoints;
    private ProgressDialog progressDialog;
    private NiftyDialogBuilder dialogBuilder;
    private ImageView arrowDailyAverageMonth;
    private ConstraintLayout chartHeader;

    private static final String CURRENT_MONTH_POINT = "CurrentMonthPoint";
    private static final String PRE_MONTH_POINT = "PreMonthPoint";
    private static final String GRACE_POINT_NOW = "GracePointNow";
    private static final String MEMBERSHIP_PROGRAM_NAME = "MembershipProgramName";
    private static final String PROGRAM_START_DATE = "ProgramStartDate";
    private static final String MEMBERSHIP_PROGRAM_CODE = "MembershipProgramCode";

    private FirebaseAnalytics screenName;

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


    //chart colors
    int[] pointDetailColors;
    int[] rankColors;

    public MonthlyPointsChartView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_point_acquisition, container, false);
        chartContent = view.findViewById(R.id.chartContent);
        barChart = view.findViewById(R.id.monthlyChart);
        rankLegends = view.findViewById(R.id.legendRanks);
        pointLegends = view.findViewById(R.id.legendPoints);
        totalPointsV = view.findViewById(R.id.totalPoints);
        currentMonthPoint = view.findViewById(R.id.currentMonthPoint);
        dailyAveragePeriod = view.findViewById(R.id.dailyAveragePeriod);
        dailyAverageMonth = view.findViewById(R.id.dailyAverageMonth);

        chartHeader = view.findViewById(R.id.chartHeader);

        preCurrentMonthPoint = view.findViewById(R.id.rataCurrentMonth);
        preDailyAverageMonth = view.findViewById(R.id.rataAverageMonth);
        preTotalPoints = view.findViewById(R.id.rataTotalPoints);

        arrowDailyAverageMonth = view.findViewById(R.id.upDownAverageMonth);

        screenName = FirebaseAnalytics.getInstance(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String programName = getArguments() != null ? getArguments().getString(MEMBERSHIP_PROGRAM_NAME) : getString(R.string.monthly_point_page_title);
        String programStartDate = getArguments() != null ? getArguments().getString(PROGRAM_START_DATE) : "";
        getActivity().setTitle(programName);

        Global.positionStack.push(1);
        chartContent.setVisibility(View.GONE);

        ILoyaltyPointsDataSource dataSource = new LoyaltyPointsDataSource(getActivity().getApplication());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        MonthlyPointsLogic monthlyPointsLogic = new MonthlyPointsLogic(dataSource, programStartDate, calendar.get(Calendar.YEAR));
        setPresenter(new MonthlyPointsPresenter(this, monthlyPointsLogic));
        if (checkUserHelpNotEmpty()) {
            showUserhelp();
        } else {
            loadMonthlyPointsData();
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
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_monthly_graph), null);
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
            UserHelp.reloadUserHelp(getActivity(), DummyMonthlyPointView.class.getSimpleName());
            if (checkUserHelpNotEmpty()) {
                showUserhelp();
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
        if (bundle != null)
            programCode = bundle.getString(MEMBERSHIP_PROGRAM_CODE);
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
        if (pointDataSet.length < 3) {
            //padding data to avoid large bar width
            float[][] newDataSet = paddingDataSet(pointDataSet, 3 - pointDataSet.length);
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
        chartContent.setVisibility(View.GONE);
        if (dialogBuilder == null) {
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
        setInitIdx(presenter.getCurrentMonth());
        //avg
        setAveragePoint((int) presenter.getAvgPoint());
        //max point
        setMaxPoint(presenter.getMaxPoint());
        //Initialize Card Header
        chartHeader.setVisibility(View.VISIBLE);
        setTotalPointsV();
        setCurrentMonthPoint();
        setDailyAverageMonth();
        setDailyAveragePeriod();

        //set months formatter
        String[] monthLabels = presenter.getMonths();
        int padData = pointDataSet.length - monthLabels.length;//check different size
        if (padData > 0) {
            String[] padMonthLabels = new String[monthLabels.length + padData];
            for (int i = 0; i < padMonthLabels.length; i++) {
                padMonthLabels[i] = "";//set default label first
                if (i < monthLabels.length) {
                    padMonthLabels[i] = monthLabels[i];
                }
            }
            monthLabels = padMonthLabels;
        }
        setxLabelFormatter(new LoyaltyXLabelFormatter(monthLabels));
        //set point details
        setPointsLegend(presenter.getPointDetails());
        //set ranks
        setRanksLegend(presenter.getRanks());
        //draw chart
        drawChart();
    }

    private boolean checkUserHelpNotEmpty() {
        List<UserHelpViewDummy> userHelpDummyViews = Global.userHelpDummyGuide.get(DummyMonthlyPointView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpDummyViews != null && !userHelpDummyViews.isEmpty();
    }

    private void showUserhelp() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment dummyChart = new DummyMonthlyPointView();
        transaction.replace(R.id.content_frame,dummyChart);
        transaction.addToBackStack(null);
        transaction.commit();
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
        LoyaltyBarChartRenderer renderer = new LoyaltyBarChartRenderer(barChart, rankDataSet, 0.3f, 0);
        barChart.setRenderer(renderer);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setExtraOffsets(0, 0, 0, 10f);
        barChart.setFitBars(true);
        barChart.setData(barData);
        PointClickMarker marker = new PointClickMarker(30f, 0);
        barChart.setMarker(marker);

        //set gesture listener on this
        barChart.setOnChartGestureListener(this);
        //chart viewport setting by initial x axis
        if (initialIdx > pointDataSet.length - 1) {
            initialIdx = 0;
        }
        barChart.moveViewToX(initialIdx - 0.7f);
        //chart viewport range
        barChart.setVisibleXRangeMaximum(4f);
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

            GroupPointData pointData = presenter.getPointDataAt(idx);
            if (pointData != null)
                goToDailyPointsChart(pointData);
        }
    }

    private void goToDailyPointsChart(GroupPointData groupPointData) {
        Bundle bundle = new Bundle();
        bundle.putString("GroupPoint", groupPointData.groupPoint);

        String programCode = getArguments() != null ? getArguments().getString(MEMBERSHIP_PROGRAM_CODE) : "";
        bundle.putString(MEMBERSHIP_PROGRAM_CODE, programCode);

        String programName = getArguments() != null ? getArguments().getString(MEMBERSHIP_PROGRAM_NAME) : "";
        bundle.putString(MEMBERSHIP_PROGRAM_NAME, programName);

        String programStartDate = getArguments() != null ? getArguments().getString(PROGRAM_START_DATE) : "";
        bundle.putString(PROGRAM_START_DATE, programStartDate);

        String currentMonthPoints = getArguments() != null ? getArguments().getString(CURRENT_MONTH_POINT) : "";
        bundle.putString(CURRENT_MONTH_POINT, currentMonthPoints);

        String preMonthPoint = getArguments() != null ? getArguments().getString(PRE_MONTH_POINT) : "";
        bundle.putString(PRE_MONTH_POINT, preMonthPoint);

        //fragment transaction
        DailyPointsChartView dailyPointsChart = new DailyPointsChartView();
        dailyPointsChart.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, dailyPointsChart);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //below chart listener implementation not used for now
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //not used for now
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

    public void setCurrentMonthPoint() {
        double dpoinmonth = Double.parseDouble(getArguments() != null ? getArguments().getString(CURRENT_MONTH_POINT) : "");
        String poinMonth = Tool.formatToCurrency(dpoinmonth);
        currentMonthPoint.setText(poinMonth);

        Integer pointmonthnow = Integer.parseInt(getArguments() != null ? getArguments().getString(CURRENT_MONTH_POINT) : "");
        Integer pointmonthbefore = Integer.parseInt(getArguments() != null ? getArguments().getString(PRE_MONTH_POINT) : "");
        int difference = pointmonthnow - pointmonthbefore;

        if (difference < 0) {
            preCurrentMonthPoint.setText("" + difference);
            preCurrentMonthPoint.setTextColor(Color.parseColor("#FF0000"));
        } else if (difference > 0) {
            preCurrentMonthPoint.setText("+" + difference);
            preCurrentMonthPoint.setTextColor(Color.parseColor("#008000"));
        } else {
            preCurrentMonthPoint.setVisibility(View.INVISIBLE);
        }
    }

    public void setDailyAveragePeriod() {
        Integer pointPeriod = Integer.parseInt(getArguments() != null ? getArguments().getString(GRACE_POINT_NOW) : "");

        DateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat outputFormatter1 = new SimpleDateFormat("MM/dd/yyyy");


        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String waktusekarang = sdf.format(now);

        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(getArguments() != null ? getArguments().getString(PROGRAM_START_DATE) : "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String output1 = outputFormatter1.format(date1); //

        Date firstDate = null;
        try {
            firstDate = sdf.parse(output1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date secondDate = null;
        try {
            secondDate = sdf.parse(waktusekarang);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        int days = (int) diff;

        int dailyAvgPeriodPoint;
        if (days <= 1) {
            dailyAvgPeriodPoint = 0;
        } else {
            dailyAvgPeriodPoint = pointPeriod / days;
        }

        String poinharirata = Tool.formatToCurrency(dailyAvgPeriodPoint);
        dailyAveragePeriod.setText(poinharirata);
    }

    public boolean isCompetitionMonthEqualsCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        Date compeDate = null;
        Date currentDate = calendar.getTime();

        DateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat simpleMonth = new SimpleDateFormat("M");

        try {
            compeDate = inputFormatter1.parse(getArguments() != null ? getArguments().getString(PROGRAM_START_DATE) : "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputMonthCompe = simpleMonth.format(compeDate);
        String outputCurrMonth = simpleMonth.format(currentDate);

        return outputCurrMonth.equalsIgnoreCase(outputMonthCompe);
    }

    public void setDailyAverageMonth() {
        Integer pointmonthnow = Integer.parseInt(getArguments() != null ? getArguments().getString(CURRENT_MONTH_POINT) : "");
        Integer pointmonthbefore = Integer.parseInt(getArguments() != null ? getArguments().getString(PRE_MONTH_POINT) : "");

        Date compeDate = null;

        DateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat simpleDate1 = new SimpleDateFormat("d");
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        String totaltanggal = simpleDate1.format(now);
        Integer tanggalHariIni = Integer.parseInt(totaltanggal);

        try {
            compeDate = inputFormatter1.parse(getArguments() != null ? getArguments().getString(PROGRAM_START_DATE) : "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tglCompe = simpleDate1.format(compeDate);
        Integer tanggalKompe = Integer.parseInt(tglCompe);

        Integer kalender8total;
        if ((tanggalHariIni - 1) == 0) {
            kalender8total = 0;
        } else {
            if (isCompetitionMonthEqualsCurrentMonth() && tanggalHariIni.equals(tanggalKompe)) {
                kalender8total = 0;
            }
            else if (isCompetitionMonthEqualsCurrentMonth()){
                kalender8total = pointmonthnow / (tanggalHariIni - tanggalKompe);
            } else {
                kalender8total = pointmonthnow / (tanggalHariIni - 1);
            }
        }

        String poinhari = Tool.formatToCurrency(kalender8total);

        int kalender8totalbefore;
        if ((tanggalHariIni - 1) == 0) {
            kalender8totalbefore = 0;
        } else {
            if (isCompetitionMonthEqualsCurrentMonth() && tanggalHariIni.equals(tanggalKompe)) {
                kalender8totalbefore = 0;
            }
            else if (isCompetitionMonthEqualsCurrentMonth()) {
                kalender8totalbefore = pointmonthbefore / (tanggalHariIni - tanggalKompe);
            } else {
                kalender8totalbefore = pointmonthbefore / (tanggalHariIni - 1);
            }
        }

        String poin8beforestring = Tool.formatToCurrency(kalender8totalbefore);

        if (kalender8total < kalender8totalbefore) {
            dailyAverageMonth.setText(poinhari);
            preDailyAverageMonth.setText(poin8beforestring);
            arrowDailyAverageMonth.setImageDrawable(getContext().getResources().getDrawable(R.drawable.arrowdownred));
        } else if (kalender8total > kalender8totalbefore && kalender8totalbefore != 0) {
            dailyAverageMonth.setText(poinhari);
            preDailyAverageMonth.setText(poin8beforestring);
            arrowDailyAverageMonth.setImageDrawable(getContext().getResources().getDrawable(R.drawable.arrowupgreen));
        } else {
            dailyAverageMonth.setText(poinhari);
            dailyAverageMonth.setVisibility(View.VISIBLE);
            preDailyAverageMonth.setVisibility(View.INVISIBLE);
            arrowDailyAverageMonth.setVisibility(View.INVISIBLE);
        }
    }

    public void setTotalPointsV() {

        final double d = Double.parseDouble(getArguments() != null ? getArguments().getString(GRACE_POINT_NOW) : "");
        String pointPeriod = Tool.formatToCurrency(d);
        totalPointsV.setText(pointPeriod);

        Integer gracepointnow = Integer.parseInt(getArguments() != null ? getArguments().getString(GRACE_POINT_NOW) : "");
        Integer gracepointbefore = Integer.parseInt(getArguments() != null ? getArguments().getString("GracePointBefore") : "");

        String range;
        if (gracepointnow < gracepointbefore) {
            range = String.valueOf(gracepointbefore - gracepointnow);
            preTotalPoints.setText("-" + range);
            preTotalPoints.setTextColor(Color.parseColor("#FF0000"));
        } else if (gracepointnow > gracepointbefore) {
            range = String.valueOf(gracepointnow - gracepointbefore);
            preTotalPoints.setText("+" + range);
            preTotalPoints.setTextColor(Color.parseColor("#008000"));
        } else {
            preTotalPoints.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkUserHelpAvailability() {
        List<UserHelpViewDummy> userHelpDummyViews = Global.userHelpDummyGuide.get(DummyMonthlyPointView.class.getSimpleName());
        return Global.ENABLE_USER_HELP && userHelpDummyViews != null;
    }

    private void initializeChartColor(Context mContext){
        pointDetailColors = new int[] {
                ContextCompat.getColor(mContext, R.color.graphColor1),
                ContextCompat.getColor(mContext, R.color.graphColor2),
                ContextCompat.getColor(mContext, R.color.graphColor3),
                ContextCompat.getColor(mContext, R.color.graphColor4),
                ContextCompat.getColor(mContext, R.color.graphColor5)
        };

        rankColors = new int[] {
                ContextCompat.getColor(mContext, R.color.rankColor1),
                ContextCompat.getColor(mContext, R.color.rankColor2),
                ContextCompat.getColor(mContext, R.color.rankColor3),
                ContextCompat.getColor(mContext, R.color.rankColor4)
        };
    }
}
