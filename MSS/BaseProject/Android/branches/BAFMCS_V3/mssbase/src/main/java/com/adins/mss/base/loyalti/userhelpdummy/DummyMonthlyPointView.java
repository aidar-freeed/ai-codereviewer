package com.adins.mss.base.loyalti.userhelpdummy;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.barchart.LoyaltyBarChartRenderer;
import com.adins.mss.base.loyalti.barchart.NonScrollListView;
import com.adins.mss.base.loyalti.barchart.pointlegends.PointLegendsAdapter;
import com.adins.mss.base.loyalti.barchart.ranklegends.RankLegendsAdapter;
import com.adins.mss.base.loyalti.model.PointDetail;
import com.adins.mss.base.loyalti.model.RankDetail;
import com.adins.mss.constant.Global;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class DummyMonthlyPointView extends Fragment {


    protected BarChart barChart;
    protected NonScrollListView rankLegends;
    protected NonScrollListView pointLegends;
    private View view;
    private NestedScrollView scrollView;

    private static final String LEVEL_1 = "Level 1";
    private static final String LEVEL_2 = "Level 2";
    private static final String LEVEL_3 = "Level 3";

    //chart colors
    int[] pointDetailColors;
    int[] rankColors;

    public DummyMonthlyPointView() {
        // Required empty public constructor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.adins.mss.base.R.id.mnGuide && !Global.BACKPRESS_RESTRICTION) {
            showUserHelp();

        }
        return super.onOptionsItemSelected(item);
    }

    protected UserHelp.OnShowSequenceFinish finishCallback = new UserHelp.OnShowSequenceFinish() {
        @Override
        public void onSequenceFinish() {
            Global.positionStack.pop();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    protected void showUserHelp(){
        final UserHelpGeneralDummy userHelpSvyDummy = new UserHelpGeneralDummy();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userHelpSvyDummy.showChartUserHelp(getActivity(),DummyMonthlyPointView.class.getSimpleName(),view,finishCallback, scrollView);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeChartColor(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dummy_monthly_point, container, false);
        barChart = view.findViewById(R.id.dummyMonthlyChart);
        rankLegends = view.findViewById(R.id.dummyLegendRanks);
        pointLegends = view.findViewById(R.id.dummyLegendPoints);
        scrollView = view.findViewById(R.id.scrollView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Global.positionStack.push(1);
        showDummyChart();
        showUserHelp();
    }

    protected void showDummyChart(){
        List<BarEntry> barEntryList = new ArrayList<>();
        float[][] dummyDataset = getDummyDataSet();
        for(int i = 0; i< dummyDataset.length; i++){
            barEntryList.add(new BarEntry(i, dummyDataset[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntryList,"");
        barDataSet.setColors(pointDetailColors);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(getXValueFormatter());
        xAxis.setTextSize(12f);

        //right axis
        YAxis rYAxis = barChart.getAxisRight();
        rYAxis.setEnabled(false);
        rYAxis.setAxisMinimum(0f);
        rYAxis.setDrawGridLines(false);

        //left axis
        YAxis lYAxis = barChart.getAxisLeft();
        lYAxis.setAxisMinimum(0f);

        //use custom legends
        barChart.getLegend().setEnabled(false);
        barChart.setDescription(null);
        RankLegendsAdapter rankLegendsAdapter = new RankLegendsAdapter(getActivity(),getDummyRanksLegend());
        rankLegends.setAdapter(rankLegendsAdapter);
        PointLegendsAdapter pointLegendsAdapter = new PointLegendsAdapter(getActivity(),getDummyPointCategoriesLegend());
        pointLegends.setAdapter(pointLegendsAdapter);

        //chart settings
        //custom render bar
        RankDetail[][] dummyRankDataSet = getDummyRankDataSet();
        LoyaltyBarChartRenderer renderer = new LoyaltyBarChartRenderer(barChart,dummyRankDataSet,0.3f,0);
        barChart.setRenderer(renderer);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setExtraOffsets(0,0,0,10f);
        barChart.setFitBars(true);
        barChart.setData(barData);
    }

    protected RankDetail[][] getDummyRankDataSet() {
        RankDetail[][] dummyRanks = new RankDetail[4][];
        dummyRanks[0] = new RankDetail[]{new RankDetail(LEVEL_1,"3",rankColors[0])
                ,new RankDetail(LEVEL_2,"12",rankColors[1])
                ,new RankDetail(LEVEL_3,"20",rankColors[2])};
        dummyRanks[1] = new RankDetail[]{new RankDetail(LEVEL_1,"1",rankColors[0])
                ,new RankDetail(LEVEL_2,"3",rankColors[1])
                ,new RankDetail(LEVEL_3,"5",rankColors[2])};
        dummyRanks[2] = new RankDetail[]{new RankDetail(LEVEL_1,"5",rankColors[0])
                ,new RankDetail(LEVEL_2,"10",rankColors[1])
                ,new RankDetail(LEVEL_3,"15",rankColors[2])};
        dummyRanks[3] = new RankDetail[]{new RankDetail(LEVEL_1,"1",rankColors[0])
                ,new RankDetail(LEVEL_2,"1",rankColors[1])
                ,new RankDetail(LEVEL_3,"6",rankColors[2])};

        return dummyRanks;
    }

    protected float[][] getDummyDataSet() {
        float[][] dummyPoin = new float[4][];
        dummyPoin[0] = new float[]{10,40,20,12};
        dummyPoin[1] = new float[]{5,10,14,8};
        dummyPoin[2] = new float[]{40,20,20,10};
        dummyPoin[3] = new float[]{20,25,13,6};
        return dummyPoin;
    }

    protected List<RankDetail> getDummyRanksLegend(){
        List<RankDetail> dummyRankLegend = new ArrayList<>();
        dummyRankLegend.add(new RankDetail(LEVEL_1,"3",rankColors[0]));
        dummyRankLegend.add(new RankDetail(LEVEL_2,"12",rankColors[1]));
        dummyRankLegend.add(new RankDetail(LEVEL_3,"20",rankColors[2]));
        return dummyRankLegend;
    }

    protected List<PointDetail> getDummyPointCategoriesLegend(){
        List<PointDetail> dummyPointLegend = new ArrayList<>();
        dummyPointLegend.add(new PointDetail("Point Category 1","3",pointDetailColors[0]));
        dummyPointLegend.add(new PointDetail("Point Category 2","3",pointDetailColors[1]));
        dummyPointLegend.add(new PointDetail("Point Category 3","3",pointDetailColors[2]));
        dummyPointLegend.add(new PointDetail("Point Category 4","4",pointDetailColors[3]));
        return dummyPointLegend;
    }

    protected ValueFormatter getXValueFormatter(){
        return new IndexAxisValueFormatter(new String[]{"Jan","Feb","Mar","Apr"});
    }


    private void initializeChartColor(Context mContext){
        pointDetailColors = new int[] {
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor1),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor2),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor3),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.graphColor4)
        };

        rankColors = new int[] {
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor1),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor2),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor3),
                androidx.core.content.ContextCompat.getColor(mContext, R.color.rankColor4)
        };
    }

}
