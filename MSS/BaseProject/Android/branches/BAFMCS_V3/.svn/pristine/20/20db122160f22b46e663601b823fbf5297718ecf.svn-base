package com.adins.mss.coll;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.coll.fragments.DepositReportRecapitulateFragment;
import com.adins.mss.coll.fragments.DepositReportSummaryFragment;
import com.adins.mss.coll.fragments.DepositReportTransferFragment;
import com.adins.mss.coll.interfaces.NavigatorInterface;

/**
 * Created by kusnendi.muhamad on 28/07/2017.
 */

public class Navigator implements NavigatorInterface {
    public static final String DEPOSIT_REPORT_RECAPITULATE  = "DEPOSIT_REPORT_RECAPITULATE";
    public static final String DEPOSIT_REPORT_SUMMARIZE     = "DEPOSIT_REPORT_SUMMARIZE";
    public static final String DEPOSIT_REPORT_TRANSFER      = "DEPOSIT_REPORT_TRANSFER";

    private Context context;
    private Bundle bundle;

    public Navigator(Context context) {
        this.context = context;
    }

    public Navigator(Context context, Bundle bundle) {
        this.context = context;
        this.bundle  = bundle;
    }

    public void route(String navigate) {
        switch (navigate) {
            case DEPOSIT_REPORT_SUMMARIZE: summarize();
                break;
            case DEPOSIT_REPORT_RECAPITULATE: recapitulate();
                break;
            case DEPOSIT_REPORT_TRANSFER: transfer();
                break;
            default:
                break;
        }
    }

    private void summarize() {
        DepositReportSummaryFragment fragment = new DepositReportSummaryFragment();
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void recapitulate() {
        DepositReportRecapitulateFragment fragment = new DepositReportRecapitulateFragment();
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void transfer() {
        DepositReportTransferFragment fragment = new DepositReportTransferFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMCMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
