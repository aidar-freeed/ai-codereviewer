package com.adins.mss.coll.models.loyaltymodels;

import com.google.gson.annotations.SerializedName;

public class RankDetail {
    @SerializedName("LEVEL")
    public String level;
    @SerializedName("RANK")
    public String rank;
    public transient int colorValue;

    public RankDetail(String level, String rank, int colorValue) {
        this.level = level;
        this.rank = rank;
        this.colorValue = colorValue;
    }
}
