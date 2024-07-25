package com.adins.mss.coll.loyalti.barchart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.adins.mss.coll.models.loyaltymodels.RankDetail;

import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;

public class LoyaltyBarChartRenderer extends BarChartRenderer {

    private RankDetail[][] rankDataSet;
    private Paint textPaint;
    private float textSizeMultiplier;
    private float textOffset;

    public LoyaltyBarChartRenderer(BarChart chart, RankDetail[][] rankDataSet, float textSizeMultiplier, float textOffset) {
        super(chart, chart.getAnimator(), chart.getViewPortHandler());
        this.rankDataSet = rankDataSet;
        this.textPaint = new Paint();
        this.textSizeMultiplier = textSizeMultiplier;
        this.textOffset = textOffset;
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        super.drawDataSet(c, dataSet, index);//draw default bar
        drawRanksText(c,dataSet,index);//tambah draw rank disini
    }

    private void drawRanksText(Canvas canvas,IBarDataSet dataSet,int index){
        BarBuffer buffer = mBarBuffers[index];

        float left;//simpan pos kiri bar
        float right;//simpan pos kanan bar
        float top;//simpan pos atas bar
        float bottom;//simpan pos bawah bar
        float offsetLeft;
        float marginPerRank;

        BarEntry entry = dataSet.getEntryForIndex(index);
        int stackLength = entry.getYVals().length;//dapatkan length stack

        for (int j = 0,rankIdx = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += (4*stackLength)) {
            left = buffer.buffer[j];
            right = buffer.buffer[j + 2];
            top = buffer.buffer[j + 1];
            bottom = buffer.buffer[j + 3];

            float x = (left + right) / 2f;
            float barWidth = right-left;
            textPaint.setTextSize(barWidth * textSizeMultiplier);
            marginPerRank = textPaint.getTextSize();
            offsetLeft = barWidth + textOffset;

            if (!mViewPortHandler.isInBoundsRight(x) || !mViewPortHandler.isInBoundsLeft(x)){
                rankIdx += 1;
                continue;
            }

            //draw ranks text from bottom to top of bar
            if(rankDataSet == null)
                break;

            if(rankIdx >= rankDataSet.length)
                continue;

            for (int r=0; r<rankDataSet[rankIdx].length; r++) {
                drawText(rankDataSet[rankIdx][r],canvas,x-offsetLeft,bottom - ((r+1)*marginPerRank) - (r*textPaint.getTextSize()));
            }
            rankIdx += 1;
        }
    }

    private void drawText(RankDetail rankData,Canvas c,float x, float y){
        textPaint.setColor(rankData.colorValue);
        c.drawText(String.valueOf(rankData.rank),x,y,textPaint);
    }
}
