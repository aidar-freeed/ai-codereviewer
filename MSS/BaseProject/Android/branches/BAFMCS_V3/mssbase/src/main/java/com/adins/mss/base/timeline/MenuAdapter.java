package com.adins.mss.base.timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adins.mss.base.R;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<MenuModel> {

    private final Context context;
    private final ArrayList<MenuModel> modelsArrayList;
    private int background = R.drawable.activated_background_indicator;

    public MenuAdapter(Context context, ArrayList<MenuModel> modelsArrayList, int background) {

        super(context, R.layout.menu_list_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.background = background;
    }


    public MenuModel getMenuModel(String title) {
        for (MenuModel model : modelsArrayList) {
            if (model.getTitle().equals(title)) {
                return model;
            }
        }
        return new MenuModel("NoTitle");
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView;
        if (!modelsArrayList.get(position).isGroupHeader()) {
            rowView = inflater.inflate(R.layout.menu_list_item, parent, false);

            RelativeLayout menuLayout = (RelativeLayout) rowView.findViewById(R.id.menu_layout);
            menuLayout.setBackgroundResource(background);

            // 3. Get icon,title & counter views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
            TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
            TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);

            // 4. Set the text for textView
            imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getTitle());
            counterView.setText(modelsArrayList.get(position).getCounter());
            if (null == modelsArrayList.get(position).getCounter()) {
                counterView.setVisibility(View.GONE);
            }
        } else {
            rowView = inflater.inflate(R.layout.menu_header_item, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.header);
            titleView.setText(modelsArrayList.get(position).getTitle());

        }

        // 5. retrn rowView
        return rowView;
    }
}
