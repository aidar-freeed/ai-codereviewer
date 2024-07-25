package com.adins.mss.base;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.config.ConfigFileReader;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;
import java.util.Properties;

public class DeveloperOptionActivity extends AppCompatActivity {

    private Button btnSave;
    private Switch switchEncrypt;
    private Switch switchDecrypt;
    private Switch switchAccessToken;
    private Switch switchDevMode;
    private LinearLayout clientIdLayout;
    private EditText edtClientId;

    private boolean isEncrypt;
    private boolean isDecrypt;
    private boolean isAccessTokenEnable;
    private boolean isByPassEnable;

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.new_developer_option_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.fontColorWhite));
        setSupportActionBar(toolbar);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_developer_option), null);
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

    private void initViews() {
        btnSave = (Button) findViewById(R.id.btnSave);
        switchDecrypt = (Switch) findViewById(R.id.switchDecrypt);
        switchEncrypt = (Switch) findViewById(R.id.switchEncrypt);
        switchAccessToken = (Switch) findViewById(R.id.switchAccessToken);
        switchDevMode = (Switch) findViewById(R.id.switchDevMode);
        clientIdLayout = (LinearLayout) findViewById(R.id.layoutClientId);
        edtClientId = (EditText) findViewById(R.id.edtClientId);
        final ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        Properties prop = ConfigFileReader.propertiesFromFile(this, GlobalData.PROPERTY_FILENAME);
        boolean encrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_ENCRYPT, "false"));
        boolean decrypt = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_DECRYPT, "false"));
        String propClientId = prop.getProperty(GlobalData.PROP_CLIENT_ID, "android");
        boolean byPassEnable = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_BYPASS_DEVELOPER, "false"));
        boolean hasEncrypt = sharedPref.getBoolean("IS_ENCRYPT", encrypt);
        boolean hasDecrypt = sharedPref.getBoolean("IS_DECRYPT", decrypt);

        boolean tokenFromPropValue = Boolean.parseBoolean(prop.getProperty(GlobalData.PROP_IS_REQUIRED_ACCESS_TOKEN, "false"));
        boolean isTokenEnable = sharedPref.getBoolean("IS_ACCESS_TOKEN_ENABLE",false);
        String firstSetting = sharedPref.getString("IS_DEV_FIRST_SETTING", Global.TRUE_STRING);
        if(!isTokenEnable && tokenFromPropValue && Global.TRUE_STRING.equalsIgnoreCase(firstSetting)){
            isTokenEnable = tokenFromPropValue;
        }
        final String clientId = sharedPref.getString("CLIENT_ID", propClientId);

        isByPassEnable = sharedPref.getBoolean("IS_BYPASS_ENABLE", byPassEnable);

        switchAccessToken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clientIdLayout.setVisibility(View.VISIBLE);
                    edtClientId.setText(clientId);
                } else {
                    clientIdLayout.setVisibility(View.GONE);
                }
            }
        });
        if (hasEncrypt) {
            switchEncrypt.setChecked(true);
        }
        if (hasDecrypt) {
            switchDecrypt.setChecked(true);
        }
        if (isTokenEnable) {
            switchAccessToken.setChecked(true);
            clientIdLayout.setVisibility(View.VISIBLE);
            edtClientId.setText(clientId);
        } else {
            clientIdLayout.setVisibility(View.GONE);
        }

        if (isByPassEnable) {
            switchDevMode.setChecked(true);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                isEncrypt = switchEncrypt.isChecked();
                isDecrypt = switchDecrypt.isChecked();
                isAccessTokenEnable = switchAccessToken.isChecked();
                isByPassEnable = switchDevMode.isChecked();
                if (isAccessTokenEnable && edtClientId.getText() != null && edtClientId.getText().length() > 0) {
                    String clientId = edtClientId.getText().toString();
                    sharedPrefEditor.putString("CLIENT_ID", clientId);
                    GlobalData.getSharedGlobalData().setClientId(clientId);
                }

                GlobalData.getSharedGlobalData().setEncrypt(isEncrypt);
                GlobalData.getSharedGlobalData().setDecrypt(isDecrypt);
                GlobalData.getSharedGlobalData().setRequiresAccessToken(isAccessTokenEnable);
                GlobalData.getSharedGlobalData().setByPassDeveloper(isByPassEnable);

                String isFirstSetting = sharedPref.getString("IS_DEV_FIRST_SETTING", Global.TRUE_STRING);
                if(Global.TRUE_STRING.equalsIgnoreCase(isFirstSetting)){
                    sharedPrefEditor.putString("IS_DEV_FIRST_SETTING",Global.FALSE_STRING);
                }
                sharedPrefEditor.putBoolean("IS_ENCRYPT", isEncrypt);
                sharedPrefEditor.putBoolean("IS_DECRYPT", isDecrypt);
                sharedPrefEditor.putBoolean("IS_ACCESS_TOKEN_ENABLE", isAccessTokenEnable);
                sharedPrefEditor.putBoolean("IS_BYPASS_ENABLE", isByPassEnable);

                sharedPrefEditor.commit();

                Toast.makeText(DeveloperOptionActivity.this, getString(R.string.options_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}
