package com.adins.mss.base.commons;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.util.LocaleHelper;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class SettingImpl implements SettingInterface {
    private Context context;

    public SettingImpl(Context context) {
        this.context = context;
    }

    @Override
    public String getLanguage() {
        return LocaleHelper.getLanguage(context);
    }

    @Override
    public void setLanguage(String language) {
        LocaleHelper.setLocale(context, language);
        GlobalData.getSharedGlobalData().setLocale(language);
        NewMainActivity.updateMenuSettings();
    }
}
