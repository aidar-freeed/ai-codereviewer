package com.adins.mss.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import com.adins.mss.base.commons.Helper;
import com.adins.mss.base.commons.ModeledActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamictheme.DynamicTheme;
import com.adins.mss.base.dynamictheme.ThemeLoader;
import com.adins.mss.base.dynamictheme.ThemeUtility;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.login.LoginImpl;
import com.adins.mss.base.login.LoginModel;
import com.adins.mss.base.syncfile.DownloadParams;
import com.adins.mss.base.syncfile.FileDownloader;
import com.adins.mss.base.syncfile.FileSyncHelper;
import com.adins.mss.base.syncfile.ImportDbFromCsv;
import com.adins.mss.base.syncfile.ImportDbParams;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileDataFile;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.location.LocationTrackingManager;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.services.RefreshToken;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.adins.mss.base.login.DefaultLoginModel.LOGIN_PREFERENCES;


/**
 * Created by Aditya Purwa on 1/6/2015.
 * Activity for login.
 */
public abstract class LoginActivity extends AppCompatActivity implements ModeledActivity<DefaultLoginModel> {

    public static Context instance;
    private List<MobileDataFile> data;
    public static List<MobileDataFile> activeData;
    public static int currentidx = 0;
    public static MobileDataFile metadata;
    //variable penampung
    static String link, savePath, message;
    static ProgressDialog pDialog;
    private final String DevModeEnable = "$ADIMOBILEDEVMODEON$";
    private final String DevModeDisable = "$ADIMOBILEDEVMODEOFF$";
    private final String txtDevModeOn = "ENABLE DEVELOPER MODE";
    private final String txtDevModeOff = "DISABLE DEVELOPER MODE";
    private final String txtDevModeOnId = "AKTIFKAN MODE PENGEMBANGAN";
    private final String txtDevModeOffId = "MATIKAN MODE PENGEMBANGAN";
    public LocationTrackingManager manager;
    protected ImageView logo;
    private LoginModel dataContext;
    private ObscuredSharedPreferences loginPreferences;
    //bong 1 apr 15 add menu to serverLinkActivity
    private Menu mainMenu;
    private LoginImpl loginImpl;
    protected ThemeLoader themeLoader;
    private Toolbar toolbar;
    private View loginHeader;
    private Button loginButton;
    private Fragment locPermissionFragment;
    protected boolean hasLogged;

    public void downloadFiles() {
        currentidx++;
        int i = currentidx;
        metadata = data.get(i);
        message = "Downloading file " + (i + 1) + " out of " + data.size() + " files.";
        link = data.get(i).getFile_url();
        savePath = GlobalData.getSharedGlobalData().getSavePath();
        if (link != null && !link.isEmpty()) {
            DownloadParams parameters = new DownloadParams(savePath, instance, message, metadata);
            FileDownloader downloader = new FileDownloader(instance);
            downloader.execute(parameters);
        }
    }

    public static void importFiles() {
        currentidx++;
        int i = currentidx;
        metadata = activeData.get(i);
        message = "Importing file " + (i + 1) + " out of " + activeData.size() + " files.";
        ImportDbParams importParams = new ImportDbParams(instance, message, metadata);
        ImportDbFromCsv importer = new ImportDbFromCsv();
        importer.execute(importParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);

        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Global.IS_DEV = false;
        enableMenuItem(false);
        if (Global.IS_DEV) {
            enableMenuItem(true);
        }
        return true;
    }

    private void enableMenuItem(boolean enable) {
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && KeyEvent.KEYCODE_MENU == keyCode) {
            if(Global.IS_DEV)
                mainMenu.performIdentifierAction(R.id.menuItem, 0);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (getString(R.string.lblServerLinkId).equals(title)) {
            startActivity(new Intent(LoginActivity.this, ServerLinkActivity.class));
        }
        if (getString(R.string.lblDevOption).equals(title)) {
            startActivity(new Intent(LoginActivity.this, DeveloperOptionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //delete downloaded update file
        File file = new File(getApplicationContext().getFilesDir(), "app.apk");
        if(file.exists()) {
            boolean result = file.delete();
            if(!result){
                Toast.makeText(this, "Cannot delete downloaded update file", Toast.LENGTH_SHORT).show();
            }
        }

        if(hasLogged && GlobalData.getSharedGlobalData().getUser() != null)
            return;//dipatch if has logged


        LocaleHelper.onCreate(getApplicationContext());
        setContentView(R.layout.new_login_activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(
                    View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        logo = (ImageView) findViewById(R.id.logoMobile);
        loginHeader = findViewById(R.id.loginHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loginImpl = new LoginImpl(this);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check location permission whether to show educational UI about permission or not
        if (Global.ENABLE_LOC_PERMISSION_UI) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    showLocationPermissionInformativeUI();
                } else {
                    Utility.checkPermissionGranted(LoginActivity.this);
                }
            } else {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_DENIED &&
                        PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_DENIED) {
                    showLocationPermissionInformativeUI();
                } else {
                    Utility.checkPermissionGranted(LoginActivity.this);
                }
            }
        } else {
            Utility.checkPermissionGranted(LoginActivity.this);
        }

        ObscuredSharedPreferences sharedPreferences = ObscuredSharedPreferences.getPrefs(this,
                LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("is_expired") && sharedPreferences.getBoolean("is_expired", false)) {
            sharedPreferences.edit().putBoolean("is_expired", false).apply();
            FileSyncHelper.senderID = 0;
            FileSyncHelper.startFileSync(this);
            return;
        }

        if (null != loginImpl) { // Penjagaan ketika masuk kembali ke APP (kejadian null saat di collection)
            if (loginImpl.isRooted()) {
                DialogManager.showRootAlert(this, getApplicationContext());
            }

            if (loginImpl.checkPlayServices(this)) {
                // Then we're good to go!
            }
        }

        DialogManager.showTimeProviderAlert(this);

        if (Helper.isDevEnabled(this) && GlobalData.getSharedGlobalData().isDevEnabled() && !GlobalData.getSharedGlobalData().isByPassDeveloper()) {
            DialogManager.showTurnOffDevMode(this);
        }

        if(hasLogged)
            return;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //prevent from activity destroyed before dialog closed
        if(dataContext != null){
            DefaultLoginModel defaultLoginModel = (DefaultLoginModel)dataContext;
            defaultLoginModel.closeProgress();
        }

        if(hasLogged)
            return;

        if (loginImpl.getLocationManager() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    loginImpl.removeUpdateLocation();
                }
            } else {
                loginImpl.removeUpdateLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(LocaleHelper.getLanguage(newBase));
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(Locale.getDefault().getLanguage());
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    private void initialize() {
        PackageInfo pInfo;
        try {
            new RefreshToken(getBaseContext()).onTokenRefresh();
        } catch (Exception e){
            FireCrash.log(e);
        }
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Global.APP_VERSION = pInfo.versionName;
            Global.BUILD_VERSION = pInfo.versionCode;

        } catch (NameNotFoundException e) {
            if (Global.IS_DEV)
                e.printStackTrace();
        }

        TextView tvAppVersion = (TextView) findViewById(R.id.contentVersion);
        String versioning = getString(R.string.app_name) + " v." + Global.APP_VERSION;
        tvAppVersion.setText(versioning);

        loginImpl.initializePreferences();
        loginPreferences = loginImpl.getLoginPreferences();

        setModel(getNewDefaultLoginModel(this));
        attachEventListener();

        String language = LocaleHelper.getLanguage(this);
        LocaleHelper.setLocale(this,language);

        TextView androidId = (TextView) findViewById(R.id.androidId);
        TextView divider = (TextView) findViewById(R.id.divider);
        final String android_id;
        if(Build.VERSION.SDK_INT > 28){
            android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            androidId.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }else {
            android_id = "";
            androidId.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        androidId.setText(android_id);
        androidId.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Clip", android_id);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),getString(R.string.copied),Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected abstract DefaultLoginModel getNewDefaultLoginModel(Context context);

    private void attachEventListener() {
        loginButton = (Button) findViewById(R.id.btnLogin);
        final CheckBox checkRememberMe = (CheckBox) findViewById(R.id.checkRememberMe);
        final EditText editUserId = (EditText) findViewById(R.id.txtUser);
        final EditText editPassword = (EditText) findViewById(R.id.txtPassword);

        editUserId.setText(loginPreferences.getString(DefaultLoginModel.LOGIN_PREFERENCES_USERNAME, getString(R.string.txtempty)));
        editPassword.setText(loginPreferences.getString(DefaultLoginModel.LOGIN_PREFERENCES_PASSWORD, getString(R.string.txtempty)));
        checkRememberMe.setChecked(loginPreferences.getBoolean(DefaultLoginModel.LOGIN_PREFERENCES_REMEMBER_ME, false));
        if (checkRememberMe.isChecked()) {
            getModel().setRememberMe(true);
        }
        getModel().setUsername(editUserId.getText().toString());
        getModel().setPassword(editPassword.getText().toString());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginButton.getText().equals(txtDevModeOn) || loginButton.getText().equals(txtDevModeOnId)) {
                    ObscuredSharedPreferences.Editor sharedPrefEditor = loginImpl.getSharedPref().edit();
                    sharedPrefEditor.putBoolean("IS_DEV", true);
                    sharedPrefEditor.commit();
                    Global.IS_DEV = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableMenuItem(true);
                            editPassword.setText("");
                        }
                    });
                } else if (loginButton.getText().equals(txtDevModeOff) || loginButton.getText().equals(txtDevModeOffId)) {
                    ObscuredSharedPreferences.Editor sharedPrefEditor = loginImpl.getSharedPref().edit();
                    sharedPrefEditor.putBoolean("IS_DEV", false);
                    sharedPrefEditor.commit();
                    Global.IS_DEV = false;
                    runOnUiThread(new Runnable() {
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
                if (devMode != null && devMode.length() > 0 && devMode.equals(DevModeEnable) && loginImpl.isCan_access_developer_mode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(getString(R.string.enableDevMode));
                        }
                    });
                } else if (devMode != null && devMode.length() > 0 && devMode.equals(DevModeDisable)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(getString(R.string.disableDevMode));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setText(getString(R.string.btnLogin));
                        }
                    });
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

    @Override
    public DefaultLoginModel getModel() {
        return (DefaultLoginModel) dataContext;
    }

    @Override
    public void setModel(DefaultLoginModel model) {
        dataContext = model;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (Utility.checkPermissionResult(LoginActivity.this, permissions, grantResults)) {
                    loginImpl.bindLocationListener();
                    if (Global.ENABLE_LOC_PERMISSION_UI) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    int color;
    protected void applyColorTheme(DynamicTheme dynamicTheme){//apply color set to views
        String commonBgColor = ThemeUtility.getColorItemValue(dynamicTheme,"bg_solid");
        String btnNormalColor = ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_normal");
        String btnPressedColor = ThemeUtility.getColorItemValue(dynamicTheme,"btn_bg_pressed");
        color = Color.parseColor(commonBgColor);
        ThemeUtility.setToolbarColor(toolbar,color);
        ThemeUtility.setStatusBarColor(this,color);
        ThemeUtility.setViewBackground(loginHeader,color);
        ThemeUtility.setViewBackground(loginButton,color);

        //create color state list for button states
        int[][] btnstates = new int[][] {
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] {}  // normal
        };

        int[] btncolorlist = new int[]{
                Color.parseColor(btnPressedColor),
                Color.parseColor(btnNormalColor)
        };
        ColorStateList btnColorStateList = new ColorStateList(btnstates,btncolorlist);
        ThemeUtility.setViewBackground(loginButton,btnColorStateList);
    }

    protected void fetchConfig() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();

        defaultConfig = new HashMap<>();
        defaultConfig.put("cipher_unsupported_device", Global.SQLITE_CIPHER_UNSUPPORTED);

        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        remoteConfig.setDefaultsAsync(defaultConfig);
        remoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isComplete() && !task.isSuccessful())
                    return;

                remoteConfig.activate();
                GlobalData.getSharedGlobalData().setRemoteConfig(remoteConfig);

                String sqliteCipherException = remoteConfig.getString("cipher_unsupported_device");
                Global.IS_DBENCRYPT = (!sqliteCipherException.contains(Build.MODEL)) && Global.IS_DBENCRYPT;
            }
        });

        GlobalData.getSharedGlobalData().setRemoteConfig(remoteConfig);
    }

    public void showLocationPermissionInformativeUI(){
        locPermissionFragment = new LocationPermissionInformationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_activity,locPermissionFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (Global.ENABLE_LOC_PERMISSION_UI && null != locPermissionFragment && locPermissionFragment.isVisible()) {
            Toast.makeText(this ,R.string.allow_all_permission, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    public Map<String, Object> defaultConfig;


}