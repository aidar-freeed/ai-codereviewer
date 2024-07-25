package com.adins.mss.base.loyalti.barchart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.adins.mss.base.loyalti.model.RankDetail;

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
        super.drawDataSet(c, dataSet, index);//draw default bar dimulai dari index x = 0
        drawRanksText(c,dataSet,index);//tambah draw rank disini
    }

    private void drawRanksText(Canvas canvas,IBarDataSet dataSet,int index){
        BarBuffer buffer = mBarBuffers[index];

        float left;//simpan pos kiri bar
        float right;//simpan pos kanan bar
        float bottom;//simpan pos bawah bar
        float offsetLeft;
        float marginPerRank;
        int stackLength;

        for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += (4*stackLength)) {
            if(rankDataSet == null || index >= rankDataSet.length)
                break;

            stackLength = getStackLength(dataSet,index);//dapatkan length stack bar di xAxis=index untuk menentukan nilai buffer j selanjutnya.
            if(stackLength == 0)
                break;

            left = buffer.buffer[j];
            right = buffer.buffer[j + 2];
            bottom = buffer.buffer[j + 3];

            float x = (left + right) / 2f;
            float barWidth = right-left;
            if (!mViewPortHandler.isInBoundsRight(x) || !mViewPortHandler.isInBoundsLeft(x)){//render rank dimulai dari nilai coordinate(pixel) x bar yang terlihat di viewport
                index += 1;
                continue;
            }

            textPaint.setTextSize(barWidth * textSizeMultiplier);
            marginPerRank = textPaint.getTextSize();
            offsetLeft = barWidth + textOffset;

            //draw ranks text from bottom to top of bar
            for (int r=0; r<rankDataSet[index].length; r++) {
                drawText(rankDataSet[index][r],canvas,x-offsetLeft,bottom - ((r+1)*marginPerRank) - (r*textPaint.getTextSize()));
            }
            index += 1;
        }
    }

    private int getStackLength(IBarDataSet dataSet, int xIndex){
        if(xIndex > dataSet.getXMax()){
            return 0;
        }
        BarEntry entry = dataSet.getEntryForIndex(xIndex);
        return entry.getYVals().length;//dapatkan length stack untuk index bar berikutnya
    }

    private void drawText(RankDetail rankData,Canvas c,float x, float y){
        textPaint.setColor(rankData.colorValue);
        c.drawText(String.valueOf(rankData.rank),x,y,textPaint);
    }
}

