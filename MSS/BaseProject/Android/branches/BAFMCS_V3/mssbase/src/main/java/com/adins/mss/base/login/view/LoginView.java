package com.adins.mss.base.login.view;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.commons.ViewImpl;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.login.LoginImpl;
import com.adins.mss.base.login.LoginInterface;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.services.RefreshToken;

/**
 * Created by kusnendi.muhamad on 01/08/2017.
 */

public class LoginView extends ViewImpl {

    private final String DevModeEnable = "$ADIMOBILEDEVMODEON$";
    private final String DevModeDisable = "$ADIMOBILEDEVMODEOFF$";
    private final String txtDevModeOn = "ENABLE DEV MODE";
    private final String txtDevModeOff = "DISABLE DEV MODE";
    private Menu mainMenu;
    private LoginInterface Login;
    private DefaultLoginModel dataContex;
    private ObscuredSharedPreferences loginPreferences;

    public LoginView(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void publish() {
        //
    }

    @Override
    public void onCreate() {
        Login = new LoginImpl(activity);
        initialize();
    }

    @Override
    public void onResume() {
        if (Login.isRooted()) {
            DialogManager.showRootAlert(activity, activity.getApplicationContext());
        }

        if (checkPlayServices(activity)) {
            // Then we're good to go!
        }

        showGPSAlert(activity);
        DialogManager.showTimeProviderAlert(activity);
        if (Helper.isDevEnabled(activity) && GlobalData.getSharedGlobalData().isDevEnabled()) {
            if (!GlobalData.getSharedGlobalData().isByPassDeveloper()) {
                DialogManager.showTurnOffDevMode(activity);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
    }

    @Override
    public void onDestroy() {
        if (Login.getLocationManager() != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                } else {
                    Login.removeUpdateLocation();
                }
            } else {
                Login.removeUpdateLocation();
            }
        }
    }

    private void initialize() {
        PackageInfo pInfo;
        try {

            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            Global.APP_VERSION = pInfo.versionName;
            Global.BUILD_VERSION = pInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        TextView tvAppVersion = (TextView) activity.findViewById(R.id.contentVersion);
        String versioning = activity.getString(R.string.app_name) + " v." + Global.APP_VERSION;
        tvAppVersion.setText(versioning);

        Login.initializePreferences();
        loginPreferences = Login.getLoginPreferences();

        attachEventListener();
    }

    private void attachEventListener() {
        final Button loginButton = (Button) activity.findViewById(R.id.buttonLogin);
        final Button exitButton = (Button) activity.findViewById(R.id.buttonExit);
        final CheckBox checkShowPassword = (CheckBox) activity.findViewById(R.id.checkShowPassword);
        final CheckBox checkRememberMe = (CheckBox) activity.findViewById(R.id.checkRememberMe);
        final EditText editUserId = (EditText) activity.findViewById(R.id.editUserId);
        final EditText editPassword = (EditText) activity.findViewById(R.id.editPassword);

        editUserId.setText(loginPreferences.getString(DefaultLoginModel.LOGIN_PREFERENCES_USERNAME, activity.getString(R.string.text_empty)));
        editPassword.setText(loginPreferences.getString(DefaultLoginModel.LOGIN_PREFERENCES_PASSWORD, activity.getString(R.string.text_empty)));
        checkRememberMe.setChecked(loginPreferences.getBoolean(DefaultLoginModel.LOGIN_PREFERENCES_REMEMBER_ME, false));
        if (checkRememberMe.isChecked()) {
            getModel().setRememberMe(true);
        }
        getModel().setUsername(editUserId.getText().toString());
        getModel().setPassword(editPassword.getText().toString());

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getModel().exit();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RefreshToken().onTokenRefresh();
                if (loginButton.getText().equals(txtDevModeOn)) {
                    ObscuredSharedPreferences.Editor sharedPrefEditor = Login.getSharedPref().edit();
                    sharedPrefEditor.putBoolean("IS_DEV", true);
                    sharedPrefEditor.commit();
                    Global.IS_DEV = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableMenuItem(true);
                            editPassword.setText("");
                        }
                    });
                } else if (loginButton.getText().equals(txtDevModeOff)) {
                    ObscuredSharedPreferences.Editor sharedPrefEditor = Login.getSharedPref().edit();
                    sharedPrefEditor.putBoolean("IS_DEV", false);
                    sharedPrefEditor.commit();
                    Global.IS_DEV = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableMenuItem(false);
                            editPassword.setText("");
                        }
                    });
                } else {
                    getModel().login();
                }
            }
        });

        editUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //EMPTY
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getModel().setUsername(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //EMPTY
            }
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //EMPTY
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getModel().setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String devMode = editPassword.getText().toString().trim();
                if (devMode != null && devMode.length() > 0 && devMode.equals(DevModeEnable)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(activity.getString(R.string.enableDevMode));
                        }
                    });
                } else if (devMode != null && devMode.length() > 0 && devMode.equals(DevModeDisable)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(activity.getString(R.string.disableDevMode));
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(activity.getString(R.string.btnLogin));
                        }
                    });
                }
            }
        });

        checkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editPassword.setTransformationMethod(null);
                } else {
                    editPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        checkRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getModel().setRememberMe(isChecked);
            }
        });

    }

    public DefaultLoginModel getModel() {
        return dataContex;
    }

    public void setModel(DefaultLoginModel dataContex) {
        this.dataContex = dataContex;
    }

    public void enableMenuItem(boolean enable) {
        if (enable) {
            mainMenu.findItem(R.id.menuItem).setVisible(true);
            mainMenu.findItem(R.id.serverLink).setVisible(true);
            mainMenu.findItem(R.id.devOption).setVisible(true);
        } else {
            mainMenu.findItem(R.id.menuItem).setVisible(false);
            mainMenu.findItem(R.id.serverLink).setVisible(false);
            mainMenu.findItem(R.id.devOption).setVisible(false);
        }
    }
}
