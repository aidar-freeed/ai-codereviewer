package com.adins.mss.base.mainmenu.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.Common;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.SettingImpl;
import com.adins.mss.base.commons.SettingInterface;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;

import java.util.Locale;

public class SettingActivity extends Activity {

    private Button btnSave;
    private RadioButton rdIndonesia;
    private RadioButton rdEnglish;
    private SettingInterface setting;
    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnSave = (Button) findViewById(R.id.btnSave);
        rdIndonesia = (RadioButton) findViewById(R.id.rdBahasa);
        rdEnglish = (RadioButton) findViewById(R.id.rdEnglish);

        Global.isMenuMoreClicked = true;

        common = new CommonImpl();
        setting = new SettingImpl(getApplicationContext());
        String language = setting.getLanguage();

        if (language.equals(LocaleHelper.BAHASA_INDONESIA)) {
            rdIndonesia.setChecked(true);
        } else {
            rdEnglish.setChecked(true);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdEnglish.isChecked()) {
                    setting.setLanguage(LocaleHelper.ENGLSIH);
                } else if (rdIndonesia.isChecked()) {
                    setting.setLanguage(LocaleHelper.BAHASA_INDONESIA);
                }
                common.setAuditData();
                Global.isMenuMoreClicked = false;
                finish();
            }
        });
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
