package com.adins.mss.foundation.UserHelp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.depositreport.GetImageTask;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpGuideDummy;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpIconDummy;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpBean;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpPropertiesDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpGuide;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpProperties;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.adins.mss.constant.Global.activeUserHelpGuide;
import static com.adins.mss.constant.Global.user;
import static com.adins.mss.constant.Global.userHelpDummyGuide;
import static com.adins.mss.constant.Global.userHelpGuide;


public class UserHelp {
    private static int viewCounter = 0;
    private static int counter = 0;
    public static boolean isActive = false;

    private static final String TOOLTIP = "TOOLTIP";
    private static final String TOOLTIP_DUMMY = "TOOLTIP_DUMMY";
    private static final String LAST_TOOLTIP = "LAST_TOOLTIP";
    private static final String GLOBAL_DATA = "GlobalData";

    public static void showUserHelp(Activity activity, View id, String text){
        Global.BACKPRESS_RESTRICTION = true;
        new MaterialShowcaseView.Builder(activity)
                .setTarget(id)
                .setDismissText(activity.getApplicationContext().getString(R.string.btnOk))
                .setContentText(text)
                .setDismissBackground(R.drawable.button_background)
                .setMaskColour(Color.parseColor("#dd000000"))
                .withRectangleShape()
                .setSkipText("")
                .show();
    }
    public static void showAllUserHelp(Activity activity, String idSequence) {
        if(Global.ENABLE_USER_HELP) {
            ArrayList<UserHelpView> tempTooltip = new ArrayList<>();
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(0);
            config.setFadeDuration(100);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
            sequence.setConfig(config);
            try {
                Global.BACKPRESS_RESTRICTION = true;
                if(!isActive) {
                    counter = 0;
                    viewCounter = 0;
                    isActive = true;
                    for (UserHelpView userHelpView : userHelpGuide.get(idSequence)) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        if (!userHelpProperties.isRecycled()) {
                            if (viewId == R.id.customer_fragment_form && GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                                continue;
                            }
                            try {
                                final View layout = activity.findViewById(viewId);
                                addSequenceUserHelp(activity, sequence, userHelpView, layout, tempTooltip);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                tempTooltip.add(userHelpView);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                FireCrash.log(ex);
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                return;
            }
            if(counter>0)
                Global.BACKPRESS_RESTRICTION = true;
            else {
                isActive = false;
                Global.BACKPRESS_RESTRICTION = false;
            }
            userHelpGuide.put(idSequence, tempTooltip);
            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), GLOBAL_DATA, Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(LAST_TOOLTIP, gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    public static void showAllUserHelp(Activity activity, String idSequence,OnShowSequenceFinish finishCallback) {
        if(Global.ENABLE_USER_HELP) {
            ArrayList<UserHelpView> tempTooltip = new ArrayList<>();
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(0);
            config.setFadeDuration(100);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
            sequence.setConfig(config);
            try {
                Global.BACKPRESS_RESTRICTION = true;
                if(!isActive) {
                    counter = 0;
                    viewCounter = 0;
                    isActive = true;
                    activeUserHelpGuide = userHelpGuide.get(idSequence);
                    for (UserHelpView userHelpView : activeUserHelpGuide) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        if (!userHelpProperties.isRecycled()) {
                            if (viewId == R.id.customer_fragment_form && GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                                continue;
                            }
                            try {
                                final View layout = activity.findViewById(viewId);
                                addSequenceUserHelp(activity, sequence, userHelpView, layout, tempTooltip,null, finishCallback);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                tempTooltip.add(userHelpView);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                FireCrash.log(ex);
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                return;
            }
            if(counter>0)
                Global.BACKPRESS_RESTRICTION = true;
            else {
                isActive = false;
                Global.BACKPRESS_RESTRICTION = false;
            }
            userHelpGuide.put(idSequence, tempTooltip);
            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), GLOBAL_DATA, Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(LAST_TOOLTIP, gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    public static void showAllUserHelp(Activity activity, String idSequence,RecyclerView recyclerView
            ,OnSequenceShowed showCallback,OnShowSequenceFinish finishCallback) {
        if(Global.ENABLE_USER_HELP) {
            ArrayList<UserHelpView> tempTooltip = new ArrayList<>();
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(0);
            config.setFadeDuration(100);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
            sequence.setConfig(config);
            try {
                Global.BACKPRESS_RESTRICTION = true;
                if(!isActive) {
                    counter = 0;
                    viewCounter = 0;
                    isActive = true;
                    activeUserHelpGuide = userHelpGuide.get(idSequence);
                    for (UserHelpView userHelpView : activeUserHelpGuide) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        View layout = null;
                        if (!userHelpProperties.isRecycled()) {
                            if (viewId == R.id.customer_fragment_form && GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                                continue;
                            }
                            try {
                                layout = activity.findViewById(viewId);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                tempTooltip.add(userHelpView);
                            }
                        }
                        else {
                             if(recyclerView == null)
                                 continue;

                             RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(userHelpProperties.getViewHolderPos());
                             if(viewHolder != null){
                                  layout = viewHolder.itemView.findViewById(viewId);
                             }
                        }

                        if(layout == null)
                            continue;

                        addSequenceUserHelp(activity ,sequence, userHelpView
                                , layout, tempTooltip, showCallback, finishCallback);
                    }
                }
            } catch (Exception ex) {
                FireCrash.log(ex);
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                activeUserHelpGuide.clear();
                return;
            }
            if(counter>0)
                Global.BACKPRESS_RESTRICTION = true;
            else {
                isActive = false;
                activeUserHelpGuide.clear();
                Global.BACKPRESS_RESTRICTION = false;
            }
            userHelpGuide.put(idSequence, tempTooltip);
            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), GLOBAL_DATA, Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(LAST_TOOLTIP, gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    public static void showAllUserHelpWithRecycler(Activity activity, String idSequence, RecyclerView recyclerView, int position) {
        if(Global.ENABLE_USER_HELP) {
            ArrayList<UserHelpView> tempTooltip = new ArrayList<>();
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(0);
            config.setFadeDuration(100);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
            sequence.setConfig(config);
            try {
                Global.BACKPRESS_RESTRICTION = true;
                if(!isActive) {
                    counter = 0;
                    viewCounter = 0;
                    isActive = true;
                    for (UserHelpView userHelpView : userHelpGuide.get(idSequence)) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        View layout;
                        if (!userHelpProperties.isRecycled()) {
                            layout = activity.findViewById(viewId);
                        } else {
                            if (recyclerView.findViewHolderForLayoutPosition(position) != null) {
                                layout = recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(viewId);
                                recyclerView.smoothScrollToPosition(position);
                            } else {
                                tempTooltip.add(userHelpView);
                                continue;
                            }
                        }
                        addSequenceUserHelp(activity, sequence, userHelpView, layout, tempTooltip);
                    }
                }
            } catch (Exception ex) {
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                FireCrash.log(ex);
            }
            if(counter>0)
                Global.BACKPRESS_RESTRICTION = true;
            else {
                isActive = false;
                Global.BACKPRESS_RESTRICTION = false;
            }
            userHelpGuide.put(idSequence, tempTooltip);

            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), GLOBAL_DATA, Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(LAST_TOOLTIP, gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    public static void showAllUserHelpWithListView(Activity activity, String idSequence, ListView listView, int position) {
        if(Global.ENABLE_USER_HELP) {
            ArrayList<UserHelpView> tempTooltip = new ArrayList<>();
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(0);
            config.setFadeDuration(100);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
            sequence.setConfig(config);
            try {
                Global.BACKPRESS_RESTRICTION = true;
                if(!isActive) {
                    counter = 0;
                    viewCounter = 0;
                    isActive = true;
                    for (UserHelpView userHelpView : userHelpGuide.get(idSequence)) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        View layout;
                        if (!userHelpProperties.isRecycled()) {
                            layout = activity.findViewById(viewId);
                        } else {
                            if (listView.getChildAt(position) != null)
                                layout = listView.getChildAt(position).findViewById(viewId);
                            else {
                                tempTooltip.add(userHelpView);
                                continue;
                            }
                        }
                        addSequenceUserHelp(activity, sequence, userHelpView, layout, tempTooltip);
                    }
                }
            }catch (Exception e){
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                e.printStackTrace();
            }
            if(counter>0)
                Global.BACKPRESS_RESTRICTION = true;
            else {
                isActive = false;
                Global.BACKPRESS_RESTRICTION = false;
            }
            userHelpGuide.put(idSequence, tempTooltip);
            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), GLOBAL_DATA, Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(LAST_TOOLTIP, gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    public static void addSequenceUserHelp(final Activity activity, final MaterialShowcaseSequence sequence,
                                           UserHelpView userHelpView, final View layout, ArrayList<UserHelpView> tempTooltip){
        if(Global.ENABLE_USER_HELP) {
            UserHelpProperties userHelpProperties = userHelpView.getProperties();
            try {
                if (layout.getVisibility() == View.VISIBLE) {
                    counter++;
                    if (!userHelpProperties.isSquare())
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                                .setTarget(layout)
                                .setDismissText("OK")
                                .setContentText(userHelpProperties.getText())
                                .setDismissBackground(R.drawable.button_background)
                                .setSkipBackground(R.drawable.button_outline_background)
                                .setMaskColour(Color.parseColor("#dd000000"))
                                .setSkipText("SKIP >")
                                .build());
                    else {
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                                .setTarget(layout)
                                .setDismissText("OK")
                                .setContentText(userHelpProperties.getText())
                                .setMaskColour(Color.parseColor("#dd000000"))
                                .setDismissBackground(R.drawable.button_background)
                                .setSkipBackground(R.drawable.button_outline_background)
                                .setSkipText("SKIP >")
                                .withRectangleShape()
                                .build());
                    }
                } else {
                    tempTooltip.add(userHelpView);
                }
                sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                    @Override
                    public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                        viewCounter++;
                        if(viewCounter >= counter){
                            Global.BACKPRESS_RESTRICTION = false;
                            isActive = false;
                        }
                    }
                });
                sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                    @Override
                    public void onSkip() {
                        Global.BACKPRESS_RESTRICTION = false;
                        isActive = false;
                    }
                });
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                Global.BACKPRESS_RESTRICTION = false;
                tempTooltip.add(userHelpView);
            }
        }
    }

    public interface OnShowSequenceFinish{
        void onSequenceFinish();
    }

    public interface OnSequenceShowed{
        void onSequenceShowed(String prevUserHelp,String currentShowUserHelp,int index);
    }

    public static void addSequenceUserHelp(final Activity activity, final MaterialShowcaseSequence sequence,
                                           final UserHelpView userHelpView, final View layout, ArrayList<UserHelpView> tempTooltip
            , final OnSequenceShowed showCallback, final OnShowSequenceFinish finishCallback){
        if(Global.ENABLE_USER_HELP) {
            UserHelpProperties userHelpProperties = userHelpView.getProperties();
            try {
                counter++;
                if (!userHelpProperties.isSquare())
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                            .setTarget(layout)
                            .setDismissText("OK")
                            .setContentText(userHelpProperties.getText())
                            .setDismissBackground(R.drawable.button_background)
                            .setSkipBackground(R.drawable.button_outline_background)
                            .setMaskColour(Color.parseColor("#dd000000"))
                            .setSkipText("SKIP >")
                            .build());
                else {
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                            .setTarget(layout)
                            .setDismissText("OK")
                            .setContentText(userHelpProperties.getText())
                            .setMaskColour(Color.parseColor("#dd000000"))
                            .setDismissBackground(R.drawable.button_background)
                            .setSkipBackground(R.drawable.button_outline_background)
                            .setSkipText("SKIP >")
                            .withRectangleShape()
                            .build());
                }

                sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
                    @Override
                    public void onShow(final MaterialShowcaseView materialShowcaseView, int i) {
                        if(showCallback == null)
                            return;

                        UserHelpView currUserHelp = activeUserHelpGuide.get(viewCounter);
                        //get previous user help
                        int prevIndex = viewCounter - 1;
                        UserHelpView prevUserHelp = null;
                        if(prevIndex >= 0)
                            prevUserHelp = activeUserHelpGuide.get(prevIndex);

                        if(prevUserHelp != null)
                            showCallback.onSequenceShowed(prevUserHelp.getViewid(),currUserHelp.getViewid(),prevIndex);
                        else
                            showCallback.onSequenceShowed(null,currUserHelp.getViewid(),prevIndex);
                    }
                });

                sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                    @Override
                    public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                        viewCounter++;
                        if(viewCounter >= counter){
                            Global.BACKPRESS_RESTRICTION = false;
                            isActive = false;
                            activeUserHelpGuide.clear();
                            if(finishCallback != null){
                                finishCallback.onSequenceFinish();
                            }
                        }
                    }
                });
                sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                    @Override
                    public void onSkip() {
                        Global.BACKPRESS_RESTRICTION = false;
                        isActive = false;
                        activeUserHelpGuide.clear();
                        if(finishCallback != null){
                            finishCallback.onSequenceFinish();
                        }
                    }
                });
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                activeUserHelpGuide.clear();
                Global.BACKPRESS_RESTRICTION = false;
                tempTooltip.add(userHelpView);
            }
        }
    }

    public static void addSequenceUserHelpDummy(Activity activity, MaterialShowcaseSequence sequence,
                                                UserHelpIconDummy userHelpIconDummy,UserHelpViewDummy userHelpViewDummy,
                                                View layout, ArrayList<UserHelpViewDummy> tempTooltip){
        Global.BACKPRESS_RESTRICTION = true;
        viewCounter = 0;
        if(Global.ENABLE_USER_HELP) {
            UserHelpPropertiesDummy userHelpProperties = userHelpIconDummy.getProperties();
            try {
                if (layout.getVisibility() == View.VISIBLE) {
                    if (!userHelpProperties.isSquare())
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                                .setTarget(layout)
                                .setDismissText("OK")
                                .setContentText(userHelpProperties.getText())
                                .setDismissBackground(R.drawable.button_background)
                                .setSkipBackground(R.drawable.button_outline_background)
                                .setMaskColour(Color.parseColor("#dd000000"))
                                .setSkipText("SKIP >")
                                .build());
                    else {
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                                .setTarget(layout)
                                .setDismissText("OK")
                                .setContentText(userHelpProperties.getText())
                                .setMaskColour(Color.parseColor("#dd000000"))
                                .setDismissBackground(R.drawable.button_background)
                                .setSkipBackground(R.drawable.button_outline_background)
                                .setSkipText("SKIP >")
                                .withRectangleShape()
                                .build());
                    }
                } else {
                    tempTooltip.add(userHelpViewDummy);
                }

                sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                    @Override
                    public void onSkip() {
                        Global.BACKPRESS_RESTRICTION = false;
                    }
                });
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
                tempTooltip.add(userHelpViewDummy);
            }
        }
    }

    public static void initializeUserHelp(Context context, String commonUserHelpUrl, String appUserHelpUrl){
        GetUserGuide getUserGuide = new GetUserGuide(context, commonUserHelpUrl, appUserHelpUrl);

        try {
            getUserGuide.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void reloadUserHelp(Context context, FragmentManager fragmentManager){
        for(Fragment fragment: fragmentManager.getFragments()){
            String fragmentName = fragment.getClass().getSimpleName();
            ObscuredSharedPreferences sharedPref =
                    ObscuredSharedPreferences.getPrefs(context, GLOBAL_DATA, Context.MODE_PRIVATE);
            String tooltipString = sharedPref.getString(TOOLTIP,null);
            String tooltipDummyString = sharedPref.getString(TOOLTIP_DUMMY,null);
            Gson gson = new Gson();

            Map<String, ArrayList<UserHelpView>> tempTooltip;
            Map<String, ArrayList<UserHelpViewDummy>> tempDummyTooltip;
            java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpView>>>(){}.getType();
            java.lang.reflect.Type typeDummy = new TypeToken<Map<String, ArrayList<UserHelpViewDummy>>>(){}.getType();
            tempTooltip = gson.fromJson(tooltipString,type);
            tempDummyTooltip = gson.fromJson(tooltipDummyString,typeDummy);

            Global.userHelpGuide.put(fragmentName,tempTooltip.get(fragmentName));
            Global.userHelpDummyGuide.put(fragmentName,tempDummyTooltip.get(fragmentName));
        }
    }

    public static void reloadUserHelp(Context context, Activity activity){
        String activityName = activity.getClass().getSimpleName();
        ObscuredSharedPreferences sharedPref =
                ObscuredSharedPreferences.getPrefs(context, GLOBAL_DATA, Context.MODE_PRIVATE);
        String tooltipString = sharedPref.getString(TOOLTIP,null);
        String tooltipDummyString = sharedPref.getString(TOOLTIP_DUMMY,null);
        Gson gson = new Gson();

        Map<String, ArrayList<UserHelpView>> tempTooltip;
        Map<String, ArrayList<UserHelpViewDummy>> tempDummyTooltip;
        java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpView>>>(){}.getType();
        java.lang.reflect.Type typeDummy = new TypeToken<Map<String, ArrayList<UserHelpViewDummy>>>(){}.getType();
        tempTooltip = gson.fromJson(tooltipString,type);
        tempDummyTooltip = gson.fromJson(tooltipDummyString,typeDummy);

        Global.userHelpGuide.put(activityName,tempTooltip.get(activityName));
        Global.userHelpDummyGuide.put(activityName,tempDummyTooltip.get(activityName));
    }

    public static void reloadUserHelp(Context context, String sequenceId){
            ObscuredSharedPreferences sharedPref =
                    ObscuredSharedPreferences.getPrefs(context, GLOBAL_DATA, Context.MODE_PRIVATE);
            String tooltipString = sharedPref.getString(TOOLTIP,null);
            String tooltipDummyString = sharedPref.getString(TOOLTIP_DUMMY,null);
            Gson gson = new Gson();

            Map<String, ArrayList<UserHelpView>> tempTooltip;
            Map<String, ArrayList<UserHelpViewDummy>> tempDummyTooltip;
            java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpView>>>(){}.getType();
            java.lang.reflect.Type typeDummy = new TypeToken<Map<String, ArrayList<UserHelpViewDummy>>>(){}.getType();
            tempTooltip = gson.fromJson(tooltipString,type);
            tempDummyTooltip = gson.fromJson(tooltipDummyString,typeDummy);

            Global.userHelpGuide.put(sequenceId,tempTooltip.get(sequenceId));
            Global.userHelpDummyGuide.put(sequenceId,tempDummyTooltip.get(sequenceId));
    }

    public static class GetUserGuide extends AsyncTask<Void,Void,String> {
        private ProgressDialog pDialog;
        private Activity activity;
        Map<String, ArrayList<UserHelpView>> tempUserHelp = new LinkedHashMap<>();
        Map<String, ArrayList<UserHelpViewDummy>> tempUserHelpDummy = new LinkedHashMap<>();
        ObscuredSharedPreferences sharedPref;
        String commonUserHelpUrl;
        String appUserHelpUrl;

        public GetUserGuide(Context activity, String commonUserHelpUrl, String appUserHelpUrl) {
            this.activity = (Activity) activity;
            this.commonUserHelpUrl = commonUserHelpUrl;
            this.appUserHelpUrl = appUserHelpUrl;
            this.sharedPref = ObscuredSharedPreferences.getPrefs(activity, GLOBAL_DATA, Context.MODE_PRIVATE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                pDialog = new ProgressDialog(activity);
                pDialog.setMessage(activity.getString(R.string.updating_apk));
                pDialog.setIndeterminate(false);
                pDialog.setMax(0);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpConn = null;
            String json = "";
            String[] name;
            try {
                URL url = new URL(commonUserHelpUrl);
                name = url.getPath().split("/");
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
                    json = builder.toString();
                }
                else {
                    return activity.getResources().getString(R.string.userhelp_download_error) + name[name.length - 1];
                }

                UserHelpBean userHelpBean = new Gson().fromJson(json, UserHelpBean.class);
                for(UserHelpGuide userHelpGuide: userHelpBean.getGuide()){
                    tempUserHelp.put(userHelpGuide.getActivity(),
                            (ArrayList<UserHelpView>) userHelpGuide.getView());
                }

                if(userHelpBean.getGuideDummy()!=null) {
                    for (UserHelpGuideDummy userHelpGuideDummy : userHelpBean.getGuideDummy()) {
                        tempUserHelpDummy.put(userHelpGuideDummy.getActivity(),
                                (ArrayList<UserHelpViewDummy>) userHelpGuideDummy.getView());
                    }
                }

                url = new URL(appUserHelpUrl);
                httpConn = (HttpURLConnection) url.openConnection();
                responseCode = httpConn.getResponseCode();

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
                    json = builder.toString();
                } else {
                    return activity.getResources().getString(R.string.userhelp_download_error) + name[name.length - 1];
                }

                userHelpBean = new Gson().fromJson(json, UserHelpBean.class);
                for(UserHelpGuide userHelpGuide: userHelpBean.getGuide()){
                    ArrayList<UserHelpView> userHelpView = tempUserHelp.get(userHelpGuide.getActivity());
                    if(userHelpView!=null){
                        userHelpView.addAll(userHelpGuide.getView());
                        tempUserHelp.put(userHelpGuide.getActivity(),userHelpView);
                    } else{
                        tempUserHelp.put(userHelpGuide.getActivity(),
                                (ArrayList<UserHelpView>) userHelpGuide.getView());
                    }
                }

                if(userHelpBean.getGuideDummy()!=null) {
                    for (UserHelpGuideDummy userHelpGuideDummy : userHelpBean.getGuideDummy()) {
                        ArrayList<UserHelpViewDummy> userHelpViewDummies = tempUserHelpDummy.get(userHelpGuideDummy.getActivity());
                        if(userHelpViewDummies!=null){
                            userHelpViewDummies.addAll(userHelpGuideDummy.getView());
                            tempUserHelpDummy.put(userHelpGuideDummy.getActivity(),userHelpViewDummies);
                        } else{
                            tempUserHelpDummy.put(userHelpGuideDummy.getActivity(),
                                    (ArrayList<UserHelpViewDummy>)userHelpGuideDummy.getView());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(httpConn != null)
                    httpConn.disconnect();
                return activity.getResources().getString(R.string.userhelp_exception);
            } finally {
                if(httpConn != null)
                    httpConn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String error) {
            super.onPostExecute(error);
            try {
                if (null != pDialog && pDialog.isShowing()) {
                    pDialog.dismiss();
                    pDialog = null;
                }
            } catch (Exception e) {
                FireCrash.log(e);
            }

            if(error!=null){
                Toast.makeText(activity, error,Toast.LENGTH_SHORT).show();
            } else {
                if(sharedPref.getString(LAST_TOOLTIP,null)!= null){
                    Gson gson = new Gson();
                    String tooltipString = sharedPref.getString(LAST_TOOLTIP,null);
                    Map<String, ArrayList<UserHelpView>> tempTooltip;
                    java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpView>>>(){}.getType();
                    tempTooltip = gson.fromJson(tooltipString,type);

                    Global.userHelpGuide = tempTooltip;
                }

                if(sharedPref.getString("LAST_TOOLTIP_DUMMY",null)!= null){
                    Gson gson = new Gson();
                    String tooltipString = sharedPref.getString("LAST_TOOLTIP_DUMMY",null);
                    Map<String, ArrayList<UserHelpViewDummy>> tempTooltip;
                    java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<UserHelpViewDummy>>>(){}.getType();
                    tempTooltip = gson.fromJson(tooltipString,type);
                    userHelpDummyGuide = tempTooltip;
                }

                if(sharedPref.getString(TOOLTIP, null) != null) {
                    for (Map.Entry<String, ArrayList<UserHelpView>> entry : tempUserHelp.entrySet()) {
                        if(null != entry.getValue()  && !entry.getValue().isEmpty()) {
                            for (UserHelpView view : entry.getValue()) {
                                if (userHelpGuide.containsKey(entry.getKey())) {
                                    UserHelpProperties properties = view.getProperties();
                                    if (userHelpGuide.get(entry.getKey()).contains(view)) {
                                        userHelpGuide.get(entry.getKey()).get(userHelpGuide.get(entry.getKey()).indexOf(view)).setProperties(properties);
                                    }
                                }
                            }
                        }
                    }

                    for (Map.Entry<String, ArrayList<UserHelpView>> entry : tempUserHelp.entrySet()) {
                        if(null != entry.getValue()  && !entry.getValue().isEmpty()) {
                            Collections.sort(entry.getValue(), new Comparator<UserHelpView>() {
                                @Override
                                public int compare(UserHelpView t1, UserHelpView t2) {
                                    if (t1.getProperties().getSequence() > t2.getProperties().getSequence())
                                        return 1;
                                    else if (t1.getProperties().getSequence() < t2.getProperties().getSequence())
                                        return -1;
                                    return 0;
                                }
                            });
                        }
                    }

                    for (Map.Entry<String, ArrayList<UserHelpViewDummy>> entry : tempUserHelpDummy.entrySet()) {
                        if (null != entry.getValue() && !entry.getValue().isEmpty()) {
                            for (UserHelpViewDummy viewDummy : entry.getValue()) {
                                ArrayList<UserHelpIconDummy> needDelete = new ArrayList<>();
                                for (UserHelpIconDummy iconDummy : viewDummy.getIconid()) {
                                    if (userHelpDummyGuide.containsKey(entry.getKey())) {
                                        UserHelpPropertiesDummy properties = iconDummy.getProperties();
                                        List<UserHelpIconDummy> icon = new ArrayList<>();
                                        if (userHelpDummyGuide.get(entry.getKey()).contains(viewDummy)) {
                                            icon = userHelpDummyGuide.get(entry.getKey())
                                                    .get(userHelpDummyGuide.get(entry.getKey()).indexOf(viewDummy)).getIconid();
                                            if (userHelpDummyGuide.get(entry.getKey())
                                                    .get(userHelpDummyGuide.get(entry.getKey()).indexOf(viewDummy)).getIconid()
                                                    .contains(iconDummy)) {
                                                icon.get(icon.indexOf(iconDummy)).setProperties(properties);
                                            }
                                        }
                                        if (iconDummy.getProperties().getJobStatus() != null
                                                && !iconDummy.getProperties().getJobStatus().equalsIgnoreCase(user.getFlag_job())) {
                                            icon.remove(iconDummy);
                                            needDelete.add(iconDummy);
                                        }
                                    }
                                }
                                viewDummy.getIconid().removeAll(needDelete);
                            }
                        }
                    }
                } else {
                    for (Map.Entry<String, ArrayList<UserHelpViewDummy>> entry : tempUserHelpDummy.entrySet()) {
                        if(null != entry.getValue()  && !entry.getValue().isEmpty()) {
                            for (UserHelpViewDummy viewDummy : entry.getValue()) {
                                ArrayList<UserHelpIconDummy> iconDelete = new ArrayList<>();
                                for (UserHelpIconDummy iconDummy : viewDummy.getIconid()) {
                                    if (iconDummy.getProperties().getJobStatus() != null
                                            && !iconDummy.getProperties().getJobStatus().equalsIgnoreCase(user.getFlag_job())) {
                                        iconDelete.add(iconDummy);
                                    }
                                }
                                viewDummy.getIconid().removeAll(iconDelete);
                            }
                        }
                    }

                    for (Map.Entry<String, ArrayList<UserHelpView>> entry : tempUserHelp.entrySet()) {
                        if(null != entry.getValue()  && !entry.getValue().isEmpty()) {
                            Collections.sort(entry.getValue(), new Comparator<UserHelpView>() {
                                @Override
                                public int compare(UserHelpView t1, UserHelpView t2) {
                                    if (t1.getProperties().getSequence() > t2.getProperties().getSequence())
                                        return 1;
                                    else if (t1.getProperties().getSequence() < t2.getProperties().getSequence())
                                        return -1;
                                    return 0;
                                }
                            });
                        }
                    }

                    userHelpGuide = tempUserHelp;
                    userHelpDummyGuide = tempUserHelpDummy;
                }

                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString(TOOLTIP, gson.toJson(tempUserHelp)).apply();
                sharedPrefEditor.putString(TOOLTIP_DUMMY, gson.toJson(tempUserHelpDummy)).apply();
            }
        }
    }

}
