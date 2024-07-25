package com.adins.mss.base.synchronize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.adins.mss.foundation.sync.Synchronize;
import com.adins.mss.foundation.sync.SynchronizeItem;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kusnendi.muhamad on 27/07/2017.
 */

public class SynchronizeView extends SynchronizeImpl implements Synchronize.SynchronizeListener {

    public Bundle savedInstaceState;
    TextView progressLabel;
    TextView syncLabel;
    ImageView ring1;
    ImageView ring2;
    RelativeLayout layout;

    public SynchronizeView(Activity activity, Intent intentMainMenu, ProgressListener progressListener) {
        super(activity, intentMainMenu, progressListener);
        this.activity = activity;
    }

    public void setSavedInstaceState(Bundle savedInstaceState) {
        this.savedInstaceState = savedInstaceState;
    }

    public void applyColorTheme(DynamicTheme colorSet){
        int syncBgColor = Color.parseColor(ThemeUtility.getColorItemValue(colorSet,"bg_solid"));
        ThemeUtility.setViewBackground(layout,syncBgColor);
        ThemeUtility.setStatusBarColor(activity,syncBgColor);
    }

    public void initialize() {
        progressLabel = (TextView) activity.findViewById(R.id.progressLabel);
        syncLabel = (TextView) activity.findViewById(R.id.syncLabel);
        layout = (RelativeLayout) activity.findViewById(R.id.synchronize_activity);

        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application))
            layout.setBackgroundResource(R.drawable.bg_sync_coll_new);
        else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application))
            layout.setBackgroundResource(R.drawable.bg_sync_survey);
        else if (Global.APPLICATION_ORDER.equalsIgnoreCase(application))
            layout.setBackgroundResource(R.drawable.bg_sync_order);

        ring1 = (ImageView) activity.findViewById(R.id.ring1);
        ring2 = (ImageView) activity.findViewById(R.id.ring2);
        Animation rotate = AnimationUtils.loadAnimation(activity, R.anim.rotate_anim);
        Animation reverse = AnimationUtils.loadAnimation(activity, R.anim.rotate_reverse_anim);
        ring1.startAnimation(rotate);
        ring2.startAnimation(reverse);

        TextView tvAppVersion = (TextView) activity.findViewById(R.id.contentVersion);
        String versioning = activity.getString(R.string.app_name) + " v." + Global.APP_VERSION;
        tvAppVersion.setText(versioning);
    }

    @Override
    public void progressUpdated(float progress) {
        if (!isSyncScheme && !isSyncQuestionSet && !isSyncLookup) {
            syncLabel.setText(activity.getString(R.string.sync_scheme));
        } else if (isSyncScheme && !isSyncQuestionSet && !isSyncLookup) {
            syncLabel.setText(activity.getString(R.string.sync_question_set));
        } else if (isSyncScheme && isSyncQuestionSet && !isSyncPaymentChannel && !isSyncLookup && !isSyncHoliday) {
            syncLabel.setText(activity.getString(R.string.sync_channel));
        } else if (isSyncScheme && isSyncQuestionSet && isSyncPaymentChannel && !isSyncLookup && !isSyncHoliday) {
            syncLabel.setText(activity.getString(R.string.sync_lookup));
            if (progress >= 100) {
                isSyncLookup = true;
            }
        } else if (isSyncScheme && isSyncQuestionSet && isSyncPaymentChannel && isSyncLookup && !isSyncHoliday) {
            syncLabel.setText(activity.getString(R.string.sync_holiday));
            if (progress >= 100 && isHoliday) {
                isSyncHoliday = true;
            }
            if (progress == -0.0 && isHoliday) {
                isSyncHoliday = true;
            }
        }
        if (progress > 100)
            progress = 100;
        if (progress != 0.0) {
            progressLabel.setText((int) progress + "%");
        } else {
            progressLabel.setText("100%");
        }

        if ((syncMax == 0 || progress >= 100) && (!isFinish && isSyncLookup && isSyncHoliday && isSyncPaymentChannel)) {
            saveLastSyncToDB();

            // Submit Last Sync Success
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            Date date = Tool.getSystemDateTime();
            String format = Global.DATE_TIME_SEC_MS_STR_FORMAT;
            String dateNow = Formatter.formatDate(date, format);
            SubmitSyncParamSuccess syncParamSuccess = new SubmitSyncParamSuccess(activity, uuidUser, dateNow);
            syncParamSuccess.execute();

            Intent timelineIntent = IntentMainMenu;
            activity.startActivity(timelineIntent);
            activity.finish();
            isFinish = true;

            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();

            String day = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
            String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

            ObscuredSharedPreferences synchronizationPreference = ObscuredSharedPreferences.getPrefs(activity, SynchronizeImpl.SYNCHRONIZATION_PREFERENCE, Context.MODE_PRIVATE);
            if (Global.APPLICATION_ORDER.equalsIgnoreCase(application)) {
                synchronizationPreference.edit().putString("MOSyncDate", day + month + year).commit();
            } else if (Global.APPLICATION_SURVEY.equalsIgnoreCase(application)) {
                synchronizationPreference.edit().putString("MSSyncDate", day + month + year).commit();
            } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                synchronizationPreference.edit().putString("MCSyncDate", day + month + year).commit();
            }
        }
    }

    @Override
    public void synchronizeFailed(SynchronizeItem syncItem,
                                  HttpConnectionResult errorResult, int numOfRetries) {
        sync.resumeSync();
    }

}
