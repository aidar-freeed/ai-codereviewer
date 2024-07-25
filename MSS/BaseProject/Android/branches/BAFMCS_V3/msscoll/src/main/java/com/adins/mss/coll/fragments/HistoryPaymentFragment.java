package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;

import org.acra.ACRA;

import java.util.Locale;

/**
 * Created by winy.firdasari on 03/02/2015.
 */

public class HistoryPaymentFragment extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        // this is not really  necessary as ExpandableListActivity contains an ExpandableList
        setContentView(R.layout.fragment_history_payment);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }
}

