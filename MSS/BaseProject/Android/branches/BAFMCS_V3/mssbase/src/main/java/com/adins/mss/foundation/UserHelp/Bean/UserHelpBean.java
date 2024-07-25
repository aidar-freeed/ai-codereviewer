package com.adins.mss.foundation.UserHelp.Bean;

import com.adins.mss.foundation.UserHelp.Bean.Dummy.UserHelpGuideDummy;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserHelpBean {
    @SerializedName("guide")
    private ArrayList<UserHelpGuide> guide;

    @SerializedName("guideDummy")
    private  ArrayList<UserHelpGuideDummy> guideDummy;

    public ArrayList<UserHelpGuide> getGuide() {
        return guide;
    }

    public void setGuide(ArrayList<UserHelpGuide> guide) {
        this.guide = guide;
    }

    public ArrayList<UserHelpGuideDummy> getGuideDummy() {
        return guideDummy;
    }

    public void setGuideDummy(ArrayList<UserHelpGuideDummy> guideDummy) {
        this.guideDummy = guideDummy;
    }
}
