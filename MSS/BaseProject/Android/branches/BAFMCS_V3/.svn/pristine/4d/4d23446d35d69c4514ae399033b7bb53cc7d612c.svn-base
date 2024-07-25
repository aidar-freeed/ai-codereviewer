package com.adins.mss.base.loyalti.userhelpdummy;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.adins.mss.base.R;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

public class DummyDailyPointsView extends DummyMonthlyPointView{

    private View view;
    private NestedScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dummy_daily_point, container, false);
        barChart = view.findViewById(R.id.dummyMonthlyChart);
        rankLegends = view.findViewById(R.id.dummyLegendRanks);
        pointLegends = view.findViewById(R.id.dummyLegendPoints);
        scrollView = view.findViewById(R.id.scrollView);
        return view;
    }

    @Override
    protected void showUserHelp() {
        final UserHelpGeneralDummy userHelpSvyDummy = new UserHelpGeneralDummy();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userHelpSvyDummy.showChartUserHelp(getActivity(),DummyDailyPointsView.class.getSimpleName(),view,finishCallback, scrollView);
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    protected ValueFormatter getXValueFormatter() {
        return new IndexAxisValueFormatter(new String[]{"01","02","03","04"});
    }
}
