package com.adins.mss.base.loyalti.barchart;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class LoyaltyXLabelFormatter extends ValueFormatter {

    private String[] labels;

    public LoyaltyXLabelFormatter(String[] labels) {
        this.labels = labels;
    }

    public int getLabelCount(){
        if(labels == null)
            return 0;
        return labels.length;
    }

    @Override
    public String getFormattedValue(float value) {
        int idx = Math.round(value);
        if(labels.length == 0)
            return "";

        if(idx < 0 || idx >= labels.length)
            return "";

        return labels[idx];
    }
}

