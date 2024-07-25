package com.adins.mss.dummy.userhelp_dummy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPointItemRecyclerViewAdapter;
import com.adins.mss.base.tasklog.LogResultActivity;
import com.adins.mss.base.tasklog.NewTaskLogAdapter;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.PriorityViewAdapter;
import com.adins.mss.base.todolist.form.todaysplan.UnplanTaskAdapter;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dummy.userhelp_dummy.Adapter.PriorityDummyAdapter;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpIconDummy;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpProperties;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.gson.Gson;

import org.apache.commons.jexl2.UnifiedJEXL;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.adins.mss.constant.Global.activeUserHelpGuide;
import static com.adins.mss.constant.Global.userHelpDummyGuide;
import static com.adins.mss.constant.Global.userHelpGuide;
import static com.adins.mss.foundation.UserHelp.UserHelp.isActive;

public class UserHelpGeneralDummy {
    private int counter = 0;
    int iconCounter = 1;
    int viewCounter = 0;
    private int listKompeIconCounter = 0;

    public void showDummyTimeline(final Activity activity, final String idSequence, final RecyclerView recyclerView, final NewTimelineFragment newTimelineFragment){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserHelpViewDummy> tempTooltip = new ArrayList<>();
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(0);
                config.setFadeDuration(100);

                final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
                sequence.setConfig(config);
                try {
                    for(UserHelpViewDummy userHelpViewDummy : userHelpDummyGuide.get(idSequence)){
                        final int view = Utility.getViewById(activity,userHelpViewDummy.getViewid());
                        View idView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(view);
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(0);
                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                            Utility.getViewById(activity,userHelpViewDummy.getViewid())
                    );
                    imageView.setImageDrawable(activity.getResources().getDrawable(
                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(0).getIcon()))
                    );

                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            newTimelineFragment.inflateRealTimelineTask();
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();

                                userHelpGuide.put(idSequence, new ArrayList<UserHelpView>());
                                sharedPrefEditor.putString("LAST_TOOLTIP", gson.toJson(userHelpGuide)).apply();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Global.BACKPRESS_RESTRICTION = false;

                        }
                    });

                    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                        @Override
                        public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                            if(viewCounter < userHelpDummyGuide.get(idSequence).size()) {
                                if(iconCounter < userHelpDummyGuide.get(idSequence).get(viewCounter).getIconid().size()) {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                            itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                    imageView.setImageDrawable(activity.getResources().getDrawable(
                                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                    );
                                    iconCounter++;
                                } else{
                                    viewCounter++;
                                    iconCounter = 0;
                                    if(viewCounter < userHelpDummyGuide.get(idSequence).size()){
                                        UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                        ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                                itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                        imageView.setImageDrawable(activity.getResources().getDrawable(
                                                Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                        );
                                        iconCounter++;
                                    }else{
                                        UserHelp.showAllUserHelp(newTimelineFragment.getActivity(),
                                                newTimelineFragment.getClass().getSimpleName());
                                        newTimelineFragment.inflateRealTimelineTask();
                                        userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                                        try {
                                            ObscuredSharedPreferences sharedPref =
                                                    ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                            Gson gson = new Gson();
                                            sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        },0);

    }

    public void showDummyTaskList(final Activity activity, final String idSequence, final RecyclerView recyclerView, final PriorityTabFragment priorityTabFragment, final PriorityViewAdapter priorityViewAdapter){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserHelpViewDummy> tempTooltip = new ArrayList<>();
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(0);
                config.setFadeDuration(100);

                final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
                sequence.setConfig(config);
                try {
                    for(UserHelpViewDummy userHelpViewDummy : userHelpDummyGuide.get(idSequence)){
                        View idView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                                Utility.getViewById(activity,userHelpViewDummy.getViewid())
                        );
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(0);
                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                            Utility.getViewById(activity,userHelpViewDummy.getViewid())
                    );
                    imageView.setImageDrawable(activity.getResources().getDrawable(
                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(0).getIcon()))
                    );

                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            if(priorityViewAdapter instanceof UnplanTaskAdapter){
                                UnplanTaskAdapter unplanTaskAdapter = (UnplanTaskAdapter)priorityViewAdapter;
                                recyclerView.setAdapter(unplanTaskAdapter);
                            }else {
                                recyclerView.setAdapter(priorityViewAdapter);
                            }
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                userHelpGuide.put(idSequence, new ArrayList<UserHelpView>());
                                sharedPrefEditor.putString("LAST_TOOLTIP", gson.toJson(userHelpGuide)).apply();
                            } catch (Exception e){}
                            Global.BACKPRESS_RESTRICTION = false;

                        }
                    });

                    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                        @Override
                        public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                            if(viewCounter < userHelpDummyGuide.get(idSequence).size()) {
                                if(iconCounter < userHelpDummyGuide.get(idSequence).get(viewCounter).getIconid().size()) {
                                    try {
                                        UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                        ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                                itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                        imageView.setImageDrawable(activity.getResources().getDrawable(
                                                Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                        );
                                    }catch (Exception e){
                                        FireCrash.log(e);
                                    }
                                    iconCounter++;
                                } else{
                                    viewCounter++;
                                    iconCounter = 0;
                                    if(viewCounter < userHelpDummyGuide.get(idSequence).size()){
                                        try {
                                            UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                            ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                                    itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                            imageView.setImageDrawable(activity.getResources().getDrawable(
                                                    Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                            );
                                        }catch (Exception e){

                                        }
                                        iconCounter++;
                                    }else{
                                        UserHelp.showAllUserHelp(priorityTabFragment.getActivity(),
                                                priorityTabFragment.getClass().getSimpleName());
                                        if(priorityViewAdapter instanceof UnplanTaskAdapter){
                                            UnplanTaskAdapter unplanTaskAdapter = (UnplanTaskAdapter)priorityViewAdapter;
                                            recyclerView.setAdapter(unplanTaskAdapter);
                                        }else {
                                            recyclerView.setAdapter(priorityViewAdapter);
                                        }
                                        userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                                        try {
                                            ObscuredSharedPreferences sharedPref =
                                                    ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                            Gson gson = new Gson();
                                            sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                        } catch (Exception e){

                                        }
                                    }
                                }
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }

    public void showDummyLog(final Activity activity, final String idSequence, final RecyclerView recyclerView, final LogResultActivity logResultActivity, final NewTaskLogAdapter newTaskLogAdapter){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserHelpViewDummy> tempTooltip = new ArrayList<>();
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(0);
                config.setFadeDuration(100);

                final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
                sequence.setConfig(config);
                try {
                    for(UserHelpViewDummy userHelpViewDummy : userHelpDummyGuide.get(idSequence)){
                        View idView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                                Utility.getViewById(activity, userHelpViewDummy.getViewid())
                        );
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    final UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(0);
                    try {
                        ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                                Utility.getViewById(activity, userHelpViewDummy.getViewid())
                        );
                        imageView.setImageDrawable(activity.getResources().getDrawable(
                                Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(0).getIcon()))
                        );
                    } catch (Exception e){}
                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            recyclerView.setAdapter(newTaskLogAdapter);
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                            } catch (Exception e){}
                            Global.BACKPRESS_RESTRICTION = false;
                        }
                    });

                    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                        @Override
                        public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                            if(viewCounter < userHelpDummyGuide.get(idSequence).size()) {
                                if(iconCounter < userHelpDummyGuide.get(idSequence).get(viewCounter).getIconid().size()) {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                            itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                    imageView.setImageDrawable(activity.getResources().getDrawable(
                                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                    );
                                    iconCounter++;
                                } else{
                                    viewCounter++;
                                    iconCounter = 0;
                                    if(viewCounter < userHelpDummyGuide.get(idSequence).size()){
                                        UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                        ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                                itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                        imageView.setImageDrawable(activity.getResources().getDrawable(
                                                Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                        );
                                        iconCounter++;
                                    }else{
                                        Global.BACKPRESS_RESTRICTION = false;
                                        recyclerView.setAdapter(newTaskLogAdapter);
                                        userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                                        try {
                                            ObscuredSharedPreferences sharedPref =
                                                    ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                            ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                            Gson gson = new Gson();
                                            sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                        } catch (Exception e){}
                                    }
                                }
                            }
                        }
                    });

                    sequence.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        },0);
    }

    public void showUnplanTasklistUserHelp(Activity activity, String idSequence,PriorityTabFragment fragment, RecyclerView recyclerView, UnplanTaskAdapter adapter
            , UserHelp.OnSequenceShowed showCallback, UserHelp.OnShowSequenceFinish finishCallback) {
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
                    viewCounter = 0;
                    isActive = true;
                    activeUserHelpGuide = userHelpGuide.get(idSequence);
                    for (UserHelpView userHelpView : activeUserHelpGuide) {
                        int viewId = Utility.getViewById(activity, userHelpView.getViewid());
                        UserHelpProperties userHelpProperties = userHelpView.getProperties();
                        View layout = null;
                        if (!userHelpProperties.isRecycled()) {
                            if (viewId == com.adins.mss.base.R.id.customer_fragment_form && GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
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

                        addUnplanSequenceListener(activity,sequence,fragment
                                ,adapter,userHelpView,layout,recyclerView,tempTooltip,showCallback,finishCallback);
                    }
                }
            } catch (Exception ex) {
                FireCrash.log(ex);
                Global.BACKPRESS_RESTRICTION = false;
                isActive = false;
                activeUserHelpGuide.clear();
                return;
            }

            userHelpGuide.put(idSequence, tempTooltip);
            try {
                ObscuredSharedPreferences sharedPref =
                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                Gson gson = new Gson();
                sharedPrefEditor.putString("LAST_TOOLTIP", gson.toJson(userHelpGuide)).apply();
            } catch (Exception e) {
            }
            sequence.start();
        }
    }

    private void addUnplanSequenceListener(final Activity activity, final MaterialShowcaseSequence sequence
            , final PriorityTabFragment fragment, final UnplanTaskAdapter adapter, final UserHelpView userHelpView, final View layout
            , final RecyclerView recyclerView, ArrayList<UserHelpView> tempTooltip
            , final UserHelp.OnSequenceShowed showCallback, final UserHelp.OnShowSequenceFinish finishCallback){
        UserHelpProperties userHelpProperties = userHelpView.getProperties();
        try {
            if (!userHelpProperties.isSquare())
                sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                        .setTarget(layout)
                        .setDismissText("OK")
                        .setContentText(userHelpProperties.getText())
                        .setDismissBackground(com.adins.mss.base.R.drawable.button_background)
                        .setSkipBackground(com.adins.mss.base.R.drawable.button_outline_background)
                        .setMaskColour(Color.parseColor("#dd000000"))
                        .setSkipText("SKIP >")
                        .build());
            else {
                sequence.addSequenceItem(new MaterialShowcaseView.Builder(activity)
                        .setTarget(layout)
                        .setDismissText("OK")
                        .setContentText(userHelpProperties.getText())
                        .setMaskColour(Color.parseColor("#dd000000"))
                        .setDismissBackground(com.adins.mss.base.R.drawable.button_background)
                        .setSkipBackground(com.adins.mss.base.R.drawable.button_outline_background)
                        .setSkipText("SKIP >")
                        .withRectangleShape()
                        .build());
            }

            final String defaultTasklistId = fragment.getClass().getSimpleName();
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
                    if(viewCounter >= activeUserHelpGuide.size()){
                        viewCounter = 0;
                        Global.BACKPRESS_RESTRICTION = false;
                        isActive = false;
                        activeUserHelpGuide.clear();
                        if(finishCallback != null){
                            finishCallback.onSequenceFinish();
                        }

                        PriorityDummyAdapter priorityDummyAdapter = new PriorityDummyAdapter();
                        recyclerView.setAdapter(priorityDummyAdapter);
                        UserHelpGeneralDummy generalDummy = new UserHelpGeneralDummy();
                        generalDummy.showDummyTaskList(activity,defaultTasklistId,recyclerView,fragment,adapter);
                    }
                }
            });
            sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                @Override
                public void onSkip() {
                    Global.BACKPRESS_RESTRICTION = false;
                    isActive = false;
                    viewCounter = 0;
                    activeUserHelpGuide.clear();
                    recyclerView.setAdapter(adapter);
                    if(finishCallback != null){
                        finishCallback.onSequenceFinish();
                    }
                }
            });
        } catch (UnifiedJEXL.Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            activeUserHelpGuide.clear();
            Global.BACKPRESS_RESTRICTION = false;
            tempTooltip.add(userHelpView);
        }
    }

    public void showDetailKompetisi(final Activity activity, final String idSequence, final RecyclerView recyclerView, final DashboardMyPointItemRecyclerViewAdapter adapter){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserHelpViewDummy> tempTooltip = new ArrayList<>();
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(0);
                config.setFadeDuration(100);

                final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
                sequence.setConfig(config);
                try {
                    for(UserHelpViewDummy userHelpViewDummy : Objects.requireNonNull(Global.userHelpDummyGuide.get(idSequence))){
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            View idView = activity.findViewById(
                                    Utility.getViewById(activity, userHelpViewDummy.getViewid())
                            );
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            Global.BACKPRESS_RESTRICTION = false;
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            userHelpGuide.put(idSequence,new ArrayList<UserHelpView>());
                            recyclerView.setAdapter(adapter);
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();

                                userHelpGuide.put(idSequence, new ArrayList<UserHelpView>());
                                sharedPrefEditor.putString("LAST_TOOLTIP", gson.toJson(userHelpGuide)).apply();
                            } catch (Exception e){}
                        }
                    });

                    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                        @Override
                        public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                            counter++;
                            if(counter >= userHelpDummyGuide.get(idSequence).size()){
                                Global.BACKPRESS_RESTRICTION = false;
                                userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                                recyclerView.setAdapter(adapter);
                                try {
                                    ObscuredSharedPreferences sharedPref =
                                            ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                    ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                    Gson gson = new Gson();
                                    sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else{
                                try {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(counter);
                                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                            itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                    imageView.setImageDrawable(activity.getResources().getDrawable(
                                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(listKompeIconCounter).getIcon()))
                                    );
                                    listKompeIconCounter++;
                                    // if there is more than 1 icon, hold adding counter
                                    if(listKompeIconCounter < userHelpViewDummy.getIconid().size()) {
                                        counter -= 1;
                                    } else{
                                        listKompeIconCounter = 0;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        },0);
    }

    public void showChartUserHelp(final Activity activity, final String idSequence, final View view,
                                  final UserHelp.OnShowSequenceFinish finishCallback, final NestedScrollView scrollView){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserHelpViewDummy> tempTooltip = new ArrayList<>();
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(0);
                config.setFadeDuration(100);

                final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
                sequence.setConfig(config);
                try {
                    for(UserHelpViewDummy userHelpViewDummy : Global.userHelpDummyGuide.get(idSequence)){
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            View idView = activity.findViewById(
                                    Utility.getViewById(activity, userHelpViewDummy.getViewid())
                            );
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            Global.BACKPRESS_RESTRICTION = false;
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            userHelpGuide.put(idSequence,new ArrayList<UserHelpView>());
                            if(finishCallback != null){
                                finishCallback.onSequenceFinish();
                            }
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();

                                userHelpGuide.put(idSequence, new ArrayList<UserHelpView>());
                                sharedPrefEditor.putString("LAST_TOOLTIP", gson.toJson(userHelpGuide)).apply();
                            } catch (Exception e){}
                        }
                    });

                    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
                        @Override
                        public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                            counter++;
                            if(counter >= userHelpDummyGuide.get(idSequence).size()){
                                Global.BACKPRESS_RESTRICTION = false;
                                userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                                if(finishCallback != null){
                                    finishCallback.onSequenceFinish();
                                }
                                try {
                                    ObscuredSharedPreferences sharedPref =
                                            ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                    ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                    Gson gson = new Gson();
                                    sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                } catch (Exception e){}
                            } else{
                                //Scroll down when showing daily graph
                                if (counter == 2 && idSequence.equalsIgnoreCase("DummyDailyPointsView")){
                                    scrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.scrollTo(0, scrollView.getBottom());
                                        }
                                    });
                                }
                                //Scroll down when showing monthly graph
                                if (counter == 5 && idSequence.equalsIgnoreCase("DummyMonthlyPointView")){
                                    scrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.scrollTo(0, scrollView.getBottom());
                                        }
                                    });
                                }
                                try {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(counter);
                                    ImageView imageView = view.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                    imageView.setImageDrawable(activity.getResources().getDrawable(
                                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(listKompeIconCounter).getIcon()))
                                    );
                                    listKompeIconCounter++;
                                    // if there is more than 1 icon, hold adding counter
                                    if (listKompeIconCounter < userHelpViewDummy.getIconid().size()) {
                                        counter -= 1;
                                    } else {
                                        listKompeIconCounter = 0;
                                    }
                                } catch (Exception e) {}
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }

}
