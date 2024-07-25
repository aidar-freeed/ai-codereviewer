package com.adins.mss.coll.loyalti.barchart;

import com.adins.mss.coll.models.loyaltymodels.GroupPointData;
import com.adins.mss.coll.models.loyaltymodels.PointDetail;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyBarDataSet extends BarDataSet {

    List<PointDetail> pointDetailsDataSet = new ArrayList<>();
    List<PointDetail> pointCategories = new ArrayList<>();

    public LoyaltyBarDataSet(List<PointDetail> pointDetails,List<PointDetail> pointCategories,List<BarEntry> yVals, String label) {
        super(yVals, label);
        pointDetailsDataSet = pointDetails;
        this.pointCategories = pointCategories;
    }

    @Override
    public int getColor(int index) {
        if(index >= pointDetailsDataSet.size())
            return pointCategories.get(0).colorValue;

        PointDetail currentPointDetail = pointDetailsDataSet.get(index);
        int selectedColor = -1;
        for (PointDetail category:pointCategories) {
            if(category.rewardProgram.equals(currentPointDetail.rewardProgram)){
                selectedColor = category.colorValue;
                break;
            }
        }

        return selectedColor;
    }
}
