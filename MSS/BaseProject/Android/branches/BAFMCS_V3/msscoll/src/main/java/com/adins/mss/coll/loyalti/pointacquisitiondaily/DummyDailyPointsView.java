package com.adins.mss.coll.loyalti.pointacquisitiondaily;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.loyalti.pointacquisitionmonthly.DummyMonthlyPointView;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class DummyDailyPointsView extends DummyMonthlyPointView {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dummy_point_acquisition_view, container, false);
        barChart = view.findViewById(R.id.dummyMonthlyChart);
        rankLegends = view.findViewById(R.id.dummyLegendRanks);
        pointLegends = view.findViewById(R.id.dummyLegendPoints);
        TextView dummyChartTitle = view.findViewById(R.id.dummyChartTitle);
        dummyChartTitle.setText(getString(R.string.daily_point_chart_title,"X"));
        return view;
    }

    @Override
    protected void showUserHelp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(getActivity(),DummyDailyPointsView.class.getSimpleName(),finishCallback);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    protected ValueFormatter getXValueFormatter() {
        return new IndexAxisValueFormatter(new String[]{"01","02","03","04"});
    }
}
