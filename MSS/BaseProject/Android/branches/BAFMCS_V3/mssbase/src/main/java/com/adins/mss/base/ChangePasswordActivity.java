package com.adins.mss.base;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.util.LocaleHelper;

import org.acra.ACRA;

import java.util.Locale;

/**
 * Created by adityapurwa on 15/04/15.
 */
public class ChangePasswordActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Fragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ChangePasswordFragment.AS_ACTIVITY, true);
        fragment.setArguments(args);
        FragmentTransaction
                transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentRoot, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
