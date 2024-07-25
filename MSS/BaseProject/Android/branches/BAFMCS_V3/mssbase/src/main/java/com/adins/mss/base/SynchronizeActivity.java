package com.adins.mss.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.synchronize.ProgressListener;
import com.adins.mss.base.synchronize.SynchronizeView;
import com.adins.mss.base.util.LocaleHelper;

import org.acra.ACRA;

import java.util.Locale;

/**
 * Created by winy.firdasari on 05/01/2015.
 */

public abstract class SynchronizeActivity extends AppCompatActivity implements ProgressListener, ThemeLoader.ColorSetLoaderCallback {

    public Activity activity = this;
    private SynchronizeView views;

    public SynchronizeActivity() {
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_synchronize_activity);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        views = new SynchronizeView(activity, getIntentMainMenu(), this);
        loadColorSet();
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

    private void loadColorSet(){
        ThemeLoader themeLoader = new ThemeLoader(this);
        themeLoader.loadSavedColorSet(this);
    }

    protected abstract Intent getIntentMainMenu();

    @Override
    public void onUpdatedValue(float progress) {
        views.progressUpdated(progress);
    }

    @Override
    public void onSyncScheme(boolean value) {
        views.isSyncScheme = value;
    }

    @Override
    public void onSyncQuestion(boolean value) {
        views.isSyncQuestionSet = value;
    }

    @Override
    public void onSyncLookup(boolean value) {
        views.isSyncLookup = value;
    }

    @Override
    public void onSyncHoliday(boolean value) {
        views.isSyncHoliday = value;
    }

    @Override
    public void onSyncPaymentChannel(boolean value) {
        views.isSyncPaymentChannel = value;
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme) {
        views.initialize();
        if(dynamicTheme != null && !dynamicTheme.getThemeItemList().isEmpty())
            views.applyColorTheme(dynamicTheme);
        views.publish();
    }

    @Override
    public void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate) {
        //EMPTY
    }
}
