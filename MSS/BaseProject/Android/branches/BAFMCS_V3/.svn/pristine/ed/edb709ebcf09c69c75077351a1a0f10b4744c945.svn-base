package com.adins.mss.svy;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.adins.mss.base.loyalti.userhelpdummy.DashboardMyPointItemDummyAdapter;
import com.adins.mss.base.tasklog.SurveyTaskAdapter;
import com.adins.mss.base.util.Utility;
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

import static com.adins.mss.constant.Global.userHelpDummyGuide;

public class UserHelpSVYDummy {
    private int iconCounter = 0;
    private int viewCounter = 1;
    public void showDummyVerif(final Activity activity, final String idSequence, final RecyclerView recyclerView, final SurveyTaskAdapter newTaskLogAdapter){
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
                        View idView = recyclerView.findViewHolderForLayoutPosition(0).itemView.findViewById(
                                Utility.getViewById(activity, userHelpViewDummy.getViewid())
                        );

                        for(UserHelpIconDummy userHelpIconDummy: userHelpViewDummy.getIconid()){
                            UserHelp.addSequenceUserHelpDummy(activity, sequence, userHelpIconDummy,userHelpViewDummy, idView, tempTooltip);
                        }
                    }

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
                            if(viewCounter < Global.userHelpDummyGuide.get(idSequence).size()) {
                                if(iconCounter < userHelpDummyGuide.get(idSequence).get(viewCounter).getIconid().size()) {
                                    UserHelpViewDummy userHelpViewDummy = userHelpDummyGuide.get(idSequence).get(viewCounter);
                                    try {
                                        ImageView imageView = recyclerView.findViewHolderForLayoutPosition(0).
                                                itemView.findViewById(Utility.getViewById(activity, userHelpViewDummy.getViewid()));
                                        imageView.setImageDrawable(activity.getResources().getDrawable(
                                                Utility.getDrawableById(activity, userHelpViewDummy.getIconid().get(iconCounter).getIcon()))
                                        );
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    iconCounter++;
                                }else{
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
                } catch (Exception e){}
            }
        },0);
    }
}
