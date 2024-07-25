package com.adins.mss.base;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.Locale;

public class ServerLinkActivity extends AppCompatActivity implements OnClickListener {

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_server_link_activity);

        screenName = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        EditText txtServerLink = (EditText) findViewById(R.id.txtServerLink);
        txtServerLink.setText(GlobalData.getSharedGlobalData().getUrlMain());

        Button btnLogin = (Button) findViewById(R.id.btnSaveLink);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        ServerLinkActivity.this.finish();

    }

    @Override
    protected void onResume() {

        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_server_link), null);

        try {
            DialogManager.showGPSAlert(this);
        } catch (Exception e) {
            FireCrash.log(e);

        }
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

    @Override
    public void onClick(View v) {

        Button btn = (Button) v;
        int id = btn.getId();
        if (R.id.btnSaveLink == id) {
            EditText txtServerLink = (EditText) findViewById(R.id.txtServerLink);
            String serverLink = txtServerLink.getText().toString().trim();


            GlobalData.getSharedGlobalData().setUrlMain(serverLink);
            GlobalData.getSharedGlobalData().reloadUrl(this.getApplicationContext());

            //Gigin ~ set URL Header Di Global.URL_HEADER tanpa "m/"
            ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                    "GlobalData", Context.MODE_PRIVATE);

            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putString("URL_HEADER", serverLink);
            sharedPrefEditor.commit();

            ServerLinkActivity.this.finish();
        }
    }
}
