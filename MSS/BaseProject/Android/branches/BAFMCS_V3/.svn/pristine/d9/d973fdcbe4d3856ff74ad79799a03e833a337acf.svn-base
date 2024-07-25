package com.adins.mss.coll.dummy;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.closingtask.ClosingTaskAdapter;
import com.adins.mss.coll.fragments.MyDashBoardItemRecyclerViewAdapter;
import com.adins.mss.coll.fragments.view.DepositReportRecapitulateView;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpIconDummy;
import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpViewDummy;
import com.adins.mss.foundation.UserHelp.Bean.UserHelpView;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.adins.mss.constant.Global.userHelpGuide;
import static com.adins.mss.constant.Global.userHelpDummyGuide;

public class UserHelpCOLDummy {
    private int counter = 0;
    private int iconCounter = 0;
    public void showDummyClosing(final Activity activity, final String idSequence, final ListView listView, final ClosingTaskAdapter closingTaskAdapter){
        final Handler handler = new Handler();
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
                        View idView = listView.getChildAt(0).findViewById(
                                Utility.getViewById(activity, userHelpViewDummy.getViewid())
                        );
                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }

                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            counter+=1;

                            listView.setAdapter(closingTaskAdapter);
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            try {
                                counter+=1;
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
                            listView.setAdapter(closingTaskAdapter);
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                                counter+=1;
                                Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(counter < userHelpDummyGuide.size())
                                            UserHelp.showAllUserHelp(activity,idSequence);
                                    }
                                },100);

                            } catch (Exception e){}
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }
    public static void showDummyDepositReport(final Activity activity, final String idSequence, final DepositReportRecapitulateView view){

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
                            view.containerDummy();
                            View idView = activity.findViewById(
                                    Utility.getViewById(activity, userHelpViewDummy.getViewid())
                            );
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }
                    sequence.setOnItemSkippedListener(new MaterialShowcaseSequence.OnSequenceItemSkippedListener() {
                        @Override
                        public void onSkip() {
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            userHelpGuide.put(idSequence,new ArrayList<UserHelpView>());
                            view.containerInit();
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
                            UserHelp.showAllUserHelp(activity,idSequence);
                            userHelpDummyGuide.put(idSequence,new ArrayList<UserHelpViewDummy>());
                            view.containerInit();
                            try {
                                ObscuredSharedPreferences sharedPref =
                                        ObscuredSharedPreferences.getPrefs(activity.getApplicationContext(), "GlobalData", Context.MODE_PRIVATE);
                                ObscuredSharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                                Gson gson = new Gson();
                                sharedPrefEditor.putString("LAST_TOOLTIP_DUMMY", gson.toJson(userHelpDummyGuide)).apply();
                            } catch (Exception e){}
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }

    public void showDashboardLoyalty(final Activity activity, final String idSequence, final RecyclerView recyclerView, final MyDashBoardItemRecyclerViewAdapter adapter){

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
                                } catch (Exception e){}
                            } else{
                                try {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(counter);
                                    ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                            itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                    imageView.setImageDrawable(activity.getResources().getDrawable(
                                            Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                    );
                                    iconCounter++;
                                    // if there is more than 1 icon, hold adding counter
                                    if(iconCounter < userHelpViewDummy.getIconid().size()) {
                                        counter -= 1;
                                    } else{
                                        iconCounter = 0;
                                    }
                                }catch (Exception e){}
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }

    public interface OnDashboardTabSelected{
        void onNextTab(int counter);
    }

    public void showDashboardColl(final Activity activity, final String idSequence, final UserHelp.OnShowSequenceFinish finishCallback,
                                  final UserHelpCOLDummy.OnDashboardTabSelected onDashboardTabSelected){
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
                            }
                            else{
                                try {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(counter);
                                    //Tab automatically load next, when user help showing collection result detail
                                    if(userHelpViewDummy.getIconid().size()>1 && userHelpViewDummy.getViewid().equalsIgnoreCase("dummyCollResDetail"))
                                        onDashboardTabSelected.onNextTab(iconCounter);

                                    iconCounter++;
                                    // if there is more than 1 icon, hold adding counter
                                    if(iconCounter < userHelpViewDummy.getIconid().size()) {
                                        counter -= 1;
                                    } else{
                                        iconCounter = 0;
                                    }
                                }catch (Exception e){}
                            }
                        }
                    });
                    sequence.start();
                } catch (Exception e){}
            }
        },0);
    }
}