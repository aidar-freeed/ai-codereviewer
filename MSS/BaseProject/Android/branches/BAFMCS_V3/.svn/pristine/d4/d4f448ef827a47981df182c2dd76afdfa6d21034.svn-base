package com.adins.mss.base.loyalti.barchart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class PointClickMarker implements IMarker {

    private Entry currClickEntry;
    private Highlight highlight;
    private Paint textPaint;
    private float textOffset;

    private float textSize;

    public PointClickMarker(float textSize, float textOffset) {
        this.textSize = textSize;
        this.textOffset = textOffset;
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
    }

    @Override
    public MPPointF getOffset() {
        return null;
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return null;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        currClickEntry = e;
        this.highlight = highlight;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        if(currClickEntry == null)
            return;
        if(highlight == null)
            return;

        int yVal = 0;
        if(highlight.isStacked()){
            int stackIdx = highlight.getStackIndex();
            BarEntry barEntry = (BarEntry) currClickEntry;
            yVal = (int)barEntry.getYVals()[stackIdx];
        }
        else {
            yVal = (int)currClickEntry.getY();
        }
        canvas.drawText(String.valueOf(yVal),posX - textPaint.getTextSize() + textOffset,posY + textPaint.getTextSize(),textPaint);
    }
}

