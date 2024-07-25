package com.adins.mss.base.loyalti.barchart.pointlegends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.model.PointDetail;

import java.util.List;

public class PointLegendsAdapter extends BaseAdapter {

    private Context context;
    private List<PointDetail> dataset;

    public PointLegendsAdapter(Context context, List<PointDetail> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public int getCount() {
        return dataset.size();
    }

    @Override
    public Object getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.point_legend_item,parent,false);
        }

        PointDetail item = dataset.get(position);
        ImageView colorRect = convertView.findViewById(R.id.legendColorRect);
        colorRect.setColorFilter(item.colorValue);
        TextView legendText = convertView.findViewById(R.id.legendText);
        legendText.setText(item.rewardProgram);

        return convertView;

    }
}

