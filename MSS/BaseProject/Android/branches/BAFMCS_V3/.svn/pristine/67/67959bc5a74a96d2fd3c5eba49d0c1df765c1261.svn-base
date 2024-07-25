package com.adins.mss.base.mainmenu;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.FragmentQuestion;
import com.adins.mss.constant.Global;


public class UpdateMenuGPS {

    /**
     *
     */
    public static void SetMenuIcon() {
        // TODO Auto-generated method stub
        try {
            NewMainActivity.updateMenuIcon(Global.isGPS);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        try {
            DynamicFormActivity.updateMenuIcon();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        try {
            FragmentQuestion.updateMenuIcon();
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }
}
