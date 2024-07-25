package com.adins.mss.base.dynamictheme;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.*;
import com.adins.mss.foundation.db.dataaccess.ThemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.ThemeItemDataAccess;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by intishar.fa on 04/09/2018.
 */

public class ThemeLoader {

    private Context context;
    Thread loadThread;
    Handler handler;
    DynamicTheme serverTheme;

    public interface ColorSetLoaderCallback{
        void onHasLoaded(DynamicTheme dynamicTheme);
        void onHasLoaded(DynamicTheme dynamicTheme, boolean needUpdate);
    }

    public ThemeLoader(Context context) {
        //mRemoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context;
        /*// Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(configSettings);*/
    }

    public void loadThemeFromUrl(final String themeUrl, final ColorSetLoaderCallback callbackObj) {
        if(themeUrl == null) {
            callbackObj.onHasLoaded(null,false);
            return;
        }

        handler = new Handler(Looper.getMainLooper());
        loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpConn = null;
                try {
                    URL url = new URL(themeUrl);
                    httpConn = (HttpURLConnection) url.openConnection();
                    int responseCode = httpConn.getResponseCode();

                    // always check HTTP response code first
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStreamReader inputStreamReader = new InputStreamReader(httpConn.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null){
                            builder.append(line);
                        }
                        bufferedReader.close();
                        String jsonTheme = builder.toString();
                        serverTheme = new Gson().fromJson(jsonTheme,DynamicTheme.class);

                        handler.post(new Runnable() {//run on main thread
                            @Override
                            public void run() {
                                if(loadThread != null)
                                    loadThread.interrupt();
                                checkVersion(serverTheme,callbackObj);//execute in main thread
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {//run on main thread
                            @Override
                            public void run() {
                                if(loadThread != null)
                                    loadThread.interrupt();
                                callbackObj.onHasLoaded(null,false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(httpConn != null)
                        httpConn.disconnect();
                    handler.post(new Runnable() {//run on main thread
                        @Override
                        public void run() {
                            if(loadThread != null)
                                loadThread.interrupt();
                            callbackObj.onHasLoaded(null,false);
                        }
                    });
                }
                finally {
                    if(httpConn != null)
                        httpConn.disconnect();
                }
            }
        });
        loadThread.start();
        /*long cacheExpiration = 0;

        //expire the cache immediately for development mode.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // task successful. Activate the fetched data
                            mRemoteConfig.activateFetched();
                            String colorSetJson = mRemoteConfig.getString("color_set_config");
                            DynamicTheme serverTheme = new Gson().fromJson(colorSetJson,DynamicTheme.class);
                            checkVersion(serverTheme,callbackObj);
                        } else {
                            System.out.println("Cannot fetch remote config.");
                        }
                    }
                });*/

    }

    private void checkVersion(DynamicTheme serverColorSet, ColorSetLoaderCallback callbackObj){
        List<Theme> themeList = ThemeDataAccess.getThemeByApplicationType(context,
                GlobalData.getSharedGlobalData().getApplication());
        int savedVersion = -1;
        if(themeList != null && themeList.size() > 0){
            savedVersion = Integer.parseInt(themeList.get(0).getVersion());
        }
        boolean needUpdate;

        //check version from server, compare with saved dynamictheme version
        int serverVersion = serverColorSet.getVersion();
        if(serverVersion > savedVersion){//if there is new version from server
            saveColorSet(serverColorSet);//overwrite current saved dynamictheme
            needUpdate = true;
        }
        else{
            needUpdate = false;
        }
        callbackObj.onHasLoaded(serverColorSet,needUpdate);
    }

    public void saveColorSet(DynamicTheme dynamicTheme){
        Theme themeObj = DynamicTheme.toThemeDao(context,dynamicTheme);
        List<com.adins.mss.dao.ThemeItem> themeItemList = DynamicTheme.toThemeItemList(context,themeObj.getUuid_theme()
                ,dynamicTheme.getThemeItemList());
        ThemeItemDataAccess.deleteAllItemByUuidTheme(context,
                themeObj.getUuid_theme());//first, delete all theme item corresponded to uuid_theme
        ThemeDataAccess.addOrReplace(context,themeObj);
        ThemeItemDataAccess.addOrReplace(context,themeItemList);
    }

    public void loadSavedColorSet(ColorSetLoaderCallback callbackObj){
        DynamicTheme colorSet = null;
        List<Theme> themeList = null;
        String applicationType = GlobalData.getSharedGlobalData().getApplication();
        if(applicationType != null && !applicationType.equals(""))
            themeList = ThemeDataAccess.getThemeByApplicationType(context,applicationType);
        else
            themeList = ThemeDataAccess.getAll(context);
        if(themeList != null && themeList.size() > 0){
            String uuid_theme = themeList.get(0).getUuid_theme();
            List<com.adins.mss.dao.ThemeItem> themeItemList = ThemeItemDataAccess.getAllByUuidTheme(context,uuid_theme);
            if(themeItemList != null && themeItemList.size() > 0){
                Theme theme = themeItemList.get(0).getTheme();
                colorSet = new DynamicTheme(theme,themeItemList);
            }
        }
        callbackObj.onHasLoaded(colorSet);
    }

}
