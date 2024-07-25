package com.adins.mss.base.about.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.about.AboutArrayAdapter;
import com.adins.mss.base.about.activity.JsonVersionResponse.ListValue;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.update.DownloadUpdate;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.androidquery.AQuery;
import com.github.jjobes.slidedatetimepicker.SlidingTabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import org.acra.ACRA;

import java.io.File;
import java.util.ArrayList;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * @author gigin.ginanjar
 */
public class AboutActivity extends Fragment {
    private static ArrayList<Library> library;
    private static int flag;
    public Libs libs;
    AQuery query;
    TextView appDesc;
    TextView appName;
    ListView listAbout;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private View view;
    private FirebaseAnalytics screenName;

    /**
     * @param changeLog
     * @param flag
     */
    public static void setChangeLog(ArrayList<Library> changeLog, int flag) {
        library = new ArrayList<>();
        library = changeLog;
        AboutActivity.flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.new_about_info_tab, container, false);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        query = new AQuery(getActivity());
        libs = new Libs(getActivity(), Libs.toStringArray(R.string.class.getFields()));
        String app_description = "";
        String app_name = "";
        Button btnUpdate = (Button) view.findViewById(R.id.btnCheckUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekVersion();
            }
        });
        //for LeadIn
        if (flag == 0) {
            app_description = libs.getInternLibraries().get(0).getLibraryDescription();
            app_name = libs.getInternLibraries().get(0).getLibraryName();
        } else if (flag == 1) {
            app_description = libs.getInternLibraries().get(1).getLibraryDescription();
            app_name = libs.getInternLibraries().get(1).getLibraryName();
        } else if (flag == 2) {
            app_description = libs.getInternLibraries().get(2).getLibraryDescription();
            app_name = libs.getInternLibraries().get(2).getLibraryName();
        }

        query.id(R.id.appDesc).text(app_description);
        PackageInfo pInfo;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            app_name = pInfo.versionName;
            Global.APP_VERSION = pInfo.versionName;
            Global.BUILD_VERSION = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            if (Global.IS_DEV)
                e.printStackTrace();
        }
        try {
            app_name = library.get(0).getLibraryName() + " v." + Global.APP_VERSION;
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                app_name = pInfo.versionName;
                Global.APP_VERSION = pInfo.versionName;
            } catch (NameNotFoundException e1) {
                if (Global.IS_DEV)
                    e1.printStackTrace();
            }
        }
        query.id(R.id.lblApp).text(app_name);

        AboutArrayAdapter adapter = new AboutArrayAdapter(getContext(), library);
        query.id(android.R.id.list).adapter(adapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(AboutActivity.this.getActivity(),AboutActivity.this.getClass().getSimpleName());
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_about), null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(getParentFragment().getActivity(), getParentFragment().getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkForUpdate(View view) {
        cekVersion();
    }

    public void cekVersion() {
        new AsyncTask<Void, Void, JsonVersionResponse>() {
            String taskId = null;
            private ProgressDialog progressDialog;
            private String errMessage = null;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.progressWait), true);
            }

            @Override
            protected JsonVersionResponse doInBackground(Void... params) {
                JsonVersionResponse result = null;
                if (Tool.isInternetconnected(getActivity())) {
                    MssRequestType request = new MssRequestType();
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_CHECK_UPDATE();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(getActivity(), encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV)
                            e.printStackTrace();
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e1) {
                            FireCrash.log(e1);
                        }
                        errMessage = e.getMessage();
                    }

                    try {
                        if(serverResult == null){
                            return null;
                        }
                        else {
                            if (serverResult.isOK()) {
                                String resultString = serverResult.getResult();
                                JsonVersionResponse response = GsonHelper.fromJson(resultString, JsonVersionResponse.class);
                                if (response.getStatus().getCode() == 0) {
                                    result = response;
                                } else {
                                    errMessage = resultString;
                                }
                            }
                        }
                    }catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    errMessage = getString(R.string.no_internet_connection);

                }
                return result;
            }

            @Override
            protected void onPostExecute(JsonVersionResponse result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if (errMessage != null && !errMessage.isEmpty()) {
                    if(GlobalData.isRequireRelogin()){
                        return;
                    }
                    Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                } else {
                    if (result != null && result.getListValues() != null) {
                        boolean softUpdate = false;
                        boolean forceUpdate = false;
                        String listVersion = "";
                        String otaLink = "";
                        for (ListValue kv : result.getListValues()) {
                            if (kv.getKey().equals(Global.GS_BUILD_NUMBER)) {
                                listVersion = kv.getValue();
                            } else if (kv.getKey().equals(Global.GS_URL_DOWNLOAD)) {
                                otaLink = kv.getValue();
                            }
                        }

                        int serverVersion = 0;
                        try {
                            serverVersion = Integer.valueOf(listVersion);
                        } catch (NumberFormatException nfe) {
                            FireCrash.log(nfe);
                        }

                        String[] versions = Tool.split(listVersion, Global.DELIMETER_DATA);
                        String thisVersion = Tool.split(Global.APP_VERSION, "-")[0];
                        String lastVersionFromServer = null;
                        for (int i = 0; i < versions.length; i++) {
                            lastVersionFromServer = versions[i];
                            if (thisVersion.equals(lastVersionFromServer)) {
                                softUpdate = true;
                            }
                        }

                        if (!softUpdate) {
                            forceUpdate = true;
                        } else {
                            if (thisVersion.equals(lastVersionFromServer))
                                softUpdate = false;
                        }

                        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

                        Boolean loginEnabled = GeneralParameterDataAccess.getOne(getActivity(), uuidUser, Global.GS_VERS_LOGIN).getGs_value().equals("1");
                        int versionLocal = 0;
                        try {
                            versionLocal = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                        } catch (NameNotFoundException e) {
                            if (Global.IS_DEV)
                                e.printStackTrace();
                        }

                        if (forceUpdate) {
                            showAskForceUpdateDialog(otaLink);
                            return;
                        } else if (softUpdate) {
                            showAskForUpdateDialog(otaLink);
                            return;
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.msgNoNewVersion), Toast.LENGTH_SHORT).show();
                        }

                        if (serverVersion > versionLocal) {
                            if (loginEnabled) {
                                showAskForUpdateDialog(otaLink);
                            } else {
                                showAskForceUpdateDialog(otaLink);
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.msgNoNewVersion), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }.execute();
    }

    private void showAskForceUpdateDialog(final String otaLink) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
        builder.withTitle("Server")
                .withMessage(getString(R.string.critical_update))
                .withButton1Text(getString(R.string.update))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUpdate(otaLink);
                    }
                });
        builder.isCancelable(false);
        builder.show();
    }

    private void showAskForUpdateDialog(final String otaLink) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
        builder.withTitle("Server").isCancelableOnTouchOutside(false).isCancelable(false)
                .withMessage(getString(R.string.update_available))
                .withButton1Text(getString(R.string.later))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .withButton2Text(getString(R.string.update))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        openUpdate(otaLink);

                    }
                }).show();
    }

    private void openUpdate(String otaLink) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(getContext().getFilesDir(), "app.apk");
            if (file.exists()) {
                Uri apkURI = FileProvider.getUriForFile(
                        getContext(), getContext().getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                DownloadUpdate downloadUpdate = new DownloadUpdate(getContext());
                downloadUpdate.execute(otaLink);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
