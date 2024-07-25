package com.adins.mss.base.mainmenu.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.mainmenu.NewMenuItem;

import java.util.List;

/**
 * Created by developer on 9/6/17.
 */

public class NewMainMenuAdapter extends RecyclerView.Adapter<NewMainMenuAdapter.MainMenuHolder> {

    private Context context;
    private List<NewMenuItem> menuItems;
    private OnItemClickListener onItemClickListener;

    public NewMainMenuAdapter(Context context, List<NewMenuItem> menuItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.menuItems = menuItems;
        this.onItemClickListener = onItemClickListener;
    }

    public NewMenuItem getMenuItem(String title) {
        for (NewMenuItem model : menuItems) {
            if (model.getName().equals(title)) {
                return model;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MainMenuHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public MainMenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainMenuHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(MainMenuHolder holder, final int position) {
        final NewMenuItem item = menuItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemIcon.setImageDrawable(context.getResources().getDrawable(item.getIcon()));

        if (item.getCounter() != null) {
            holder.itemCounter.setVisibility(View.VISIBLE);
            holder.itemCounter.setText(item.getCounter());
        } else
            holder.itemCounter.setVisibility(View.GONE);

        if (null != holder.itemView) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onItemClickListener.OnItemClick(item, position);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (menuItems != null) ? menuItems.size() : 0;
    }

    public NewMenuItem OnItemClick(NewMenuItem menuItem, int position) {
        return onItemClickListener.OnItemClick(menuItem, position);
    }

    public static class MainMenuHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemIcon;
        TextView itemCounter;

        public MainMenuHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.new_menu_item, viewGroup, false));

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemIcon = (ImageView) itemView.findViewById(R.id.itemIcon);
            itemCounter = (TextView) itemView.findViewById(R.id.counter);
        }
    }

    public static class OnItemClickListener {
        public NewMenuItem OnItemClick(NewMenuItem menuItem, int position) {
            System.out.println(position);
            return menuItem;
        }
    }
}
