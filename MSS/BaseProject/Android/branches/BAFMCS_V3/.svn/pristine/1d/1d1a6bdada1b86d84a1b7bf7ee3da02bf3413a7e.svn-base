package com.adins.mss.coll.dummy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adins.mss.coll.closingtask.ClosingTaskItem;

public class ClosingTaskDummyAdapter extends BaseAdapter {
    private static ClosingTaskDummyAdapter instance;

    public ClosingTaskDummyAdapter() {
    }

    public static ClosingTaskDummyAdapter getInstance() {
        if (instance == null) {
            instance = new ClosingTaskDummyAdapter();
        }

        return instance;
    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return  null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClosingTaskItem item;

        if (convertView == null) {
            item = new ClosingTaskItem(parent.getContext());
        } else {
            item = (ClosingTaskItem) convertView;
        }

        return item;
    }
}
