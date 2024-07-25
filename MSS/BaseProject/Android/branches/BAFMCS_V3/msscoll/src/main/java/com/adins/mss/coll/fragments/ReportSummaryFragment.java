package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.interfaces.ReportSummaryImpl;
import com.adins.mss.coll.interfaces.ReportSummaryInterface;
import com.adins.mss.coll.models.ReportSummaryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.formatter.Tool;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by Aditya Purwa on 2/16/2015.
 */
public class ReportSummaryFragment extends Fragment implements TaskListener {
    private View view;
    private TextView tanggal;
    private TextView collector;
    private TextView totalToBeCollected;
    private TextView totalPaid;
    private ReportSummaryInterface iReportSummary;
    TableLayout table;
    private View row;
    private TextView txt_Aggrmn_No;
    private TextView txt_Status;
    private TextView txt_result;
    private List<ReportSummaryResponse> listTask = new ArrayList<>();

    private FirebaseAnalytics screenName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        Context context= this.getContext();
        iReportSummary = new ReportSummaryImpl(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_reportsummary));

        tanggal = (TextView) view.findViewById(R.id.txtDate);
        totalToBeCollected = (TextView) view.findViewById(R.id.txtCollected);
        totalPaid = (TextView) view.findViewById(R.id.txtPaid);
        table = (TableLayout) view.findViewById(R.id.tableSummary);

        tanggal.setText(getString(R.string.text_empty));
        totalToBeCollected.setText(getString(R.string.text_empty));
        totalPaid.setText(getString(R.string.text_empty));

        iReportSummary.getReportSummary(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(ReportSummaryFragment.this.getActivity(),ReportSummaryFragment.this.getClass().getSimpleName());
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.new_fragment_report_summary, container, false);
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserHelp.showAllUserHelp(ReportSummaryFragment.this.getActivity(), ReportSummaryFragment.this.getClass().getSimpleName());
                    }
                }, SHOW_USERHELP_DELAY_DEFAULT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_report_summary), null);
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().setTitle(getString(R.string.title_mn_reportsummary));
//	    getActivity().getActionBar().setTitle(getString(R.string.title_mn_reportsummary));
//	    getActivity().getActionBar().removeAllTabs();
//	    getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onAttach(Context context) {
        setHasOptionsMenu(true);
        super.onAttach(context);
    }

    @Override
    public void onCompleteTask(Object result) {
        ReportSummaryResponse reportSummaryResponse = (ReportSummaryResponse) result;
        tanggal.setText(reportSummaryResponse.getCurrent_date());
        totalToBeCollected.setText(Tool.separateThousand(String.format(Locale.US, "%.0f",reportSummaryResponse.getTotal_to_be_paid())));
        totalPaid.setText(Tool.separateThousand(String.format(Locale.US, "%.0f",reportSummaryResponse.getTotal_received())));

        listTask = reportSummaryResponse.getList_task();
        if (listTask != null) {
            for (ReportSummaryResponse listResponse : listTask) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.new_fragment_report_summary_row, table, false);
                txt_Aggrmn_No = (TextView) row.findViewById(R.id.fieldAgrmnNo);
                txt_Status = (TextView) row.findViewById(R.id.fieldStatus);
                txt_result = (TextView) row.findViewById(R.id.fieldResult);

                txt_Aggrmn_No.setText(listResponse.getAgreementNo());
                txt_Status.setText(listResponse.getStatusTask());
                if(!"-".equals(listResponse.getAmountPaid())){
                    txt_result.setText(Tool.separateThousand(String.format(Locale.US, "%.0f", Double.parseDouble(listResponse.getAmountPaid()))));
                } else if (listResponse.getPtp().length() > 1) {
                    txt_result.setText(listResponse.getPtp());
                } else {
                    txt_result.setText(listResponse.getNote());
                }

                table.addView(row);
            }
        }
    }

    @Override
    public void onCancelTask(boolean value) {
        //
    }

    @Override
    public void onLocalData(Object result) {

    }

    //    private static Menu mainMenu;
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        mainMenu = menu;
//    }
//
//	@Override
//    public boolean onOptionsItemSelected(NewMenuItem item) {
//        // Handle item selection
//        int id = item.getItemId();
//        if (id == R.id.menuMore) {
//        	mainMenu.findItem(R.id.mnViewMap).setVisible(false);
//            mainMenu.findItem(R.id.mnViewAllHeader).setVisible(false);
//		}
//
//        return super.onOptionsItemSelected(item);
//    }
}
