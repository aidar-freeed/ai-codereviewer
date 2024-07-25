package com.adins.mss.base.loyalti.barchart.ranklegends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.model.RankDetail;

import java.util.ArrayList;
import java.util.List;

public class RankLegendsAdapter extends BaseAdapter {

    private List<RankDetail> dataset = new ArrayList<>();
    private Context context;

    public RankLegendsAdapter(Context context,List<RankDetail> dataset) {
        this.context = context;

        for(int i=dataset.size()-1; i>=0; i--){
            this.dataset.add(dataset.get(i));
        }
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
            convertView = LayoutInflater.from(context).inflate(R.layout.rank_legend_item,parent,false);
        }

        RankDetail item = dataset.get(position);
        TextView legendText = convertView.findViewById(R.id.legendText);
        legendText.setTextColor(item.colorValue);
        legendText.setText(item.level);
        return convertView;
    }
}

