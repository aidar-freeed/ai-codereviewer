package com.adins.mss.coll.loyalti.pointacquisitionmonthly;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.coll.R;
import com.adins.mss.coll.loyalti.barchart.LoyaltyBarChartRenderer;
import com.adins.mss.coll.loyalti.barchart.NonScrollListView;
import com.adins.mss.coll.loyalti.barchart.pointlegends.PointLegendsAdapter;
import com.adins.mss.coll.loyalti.barchart.ranklegends.RankLegendsAdapter;
import com.adins.mss.coll.models.loyaltymodels.PointDetail;
import com.adins.mss.coll.models.loyaltymodels.RankDetail;
import com.adins.mss.constant.Global;
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

    //legend colors
    protected int[] pointDetailColors = {Color.parseColor("#FBBA72")
            ,Color.parseColor("#F86624"),
            Color.parseColor("#BA5624") ,
            Color.parseColor("#8F250C")};

    protected int[] rankColors = {Color.parseColor("#FF0000"),
            Color.parseColor("#000000"),
            Color.parseColor("#32CD32"),
            Color.parseColor("#0000FF")};

    public DummyMonthlyPointView() {
        // Required empty public constructor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == com.adins.mss.base.R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                showUserHelp();
            }
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(getActivity(),DummyMonthlyPointView.class.getSimpleName(),finishCallback);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dummy_point_acquisition_view, container, false);
        barChart = view.findViewById(R.id.dummyMonthlyChart);
        rankLegends = view.findViewById(R.id.dummyLegendRanks);
        pointLegends = view.findViewById(R.id.dummyLegendPoints);
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
        dummyRanks[0] = new RankDetail[]{new RankDetail("Level 1","3",rankColors[0])
                ,new RankDetail("Level 2","12",rankColors[1])
                ,new RankDetail("Level 3","20",rankColors[2])};
        dummyRanks[1] = new RankDetail[]{new RankDetail("Level 1","1",rankColors[0])
                ,new RankDetail("Level 2","3",rankColors[1])
                ,new RankDetail("Level 3","5",rankColors[2])};
        dummyRanks[2] = new RankDetail[]{new RankDetail("Level 1","5",rankColors[0])
                ,new RankDetail("Level 2","10",rankColors[1])
                ,new RankDetail("Level 3","15",rankColors[2])};
        dummyRanks[3] = new RankDetail[]{new RankDetail("Level 1","1",rankColors[0])
                ,new RankDetail("Level 2","1",rankColors[1])
                ,new RankDetail("Level 3","6",rankColors[2])};

        return dummyRanks;
    }

    protected float[][] getDummyDataSet() {
        float[][] dummyPoin = new float[4][];
        dummyPoin[0] = new float[]{10,40,20,12};
        dummyPoin[1] = new float[]{5,10,14,8};
        dummyPoin[2] = new float[]{20,25,13,6};
        dummyPoin[3] = new float[]{40,20,20,10};
        return dummyPoin;
    }

    protected List<RankDetail> getDummyRanksLegend(){
        List<RankDetail> dummyRankLegend = new ArrayList<>();
        dummyRankLegend.add(new RankDetail("Level 1","3",rankColors[0]));
        dummyRankLegend.add(new RankDetail("Level 2","12",rankColors[1]));
        dummyRankLegend.add(new RankDetail("Level 3","20",rankColors[2]));
        return dummyRankLegend;
    }

    protected List<PointDetail> getDummyPointCategoriesLegend(){
        List<PointDetail> dummyPointLegend = new ArrayList<>();
        dummyPointLegend.add(new PointDetail("Point Category 1","3",pointDetailColors[0]));
        dummyPointLegend.add(new PointDetail("Point Category 2","3",pointDetailColors[1]));
        dummyPointLegend.add(new PointDetail("Point Category 3","3",pointDetailColors[2]));
        return dummyPointLegend;
    }

    protected ValueFormatter getXValueFormatter(){
        return new IndexAxisValueFormatter(new String[]{"Jan","Feb","Mar","Apr"});
    }

}
