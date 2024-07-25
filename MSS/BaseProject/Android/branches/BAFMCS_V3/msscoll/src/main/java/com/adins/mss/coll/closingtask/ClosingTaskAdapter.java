package com.adins.mss.coll.closingtask;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adins.mss.coll.closingtask.models.ClosingTaskEntity;

import java.util.List;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskAdapter extends BaseAdapter {
    private static ClosingTaskAdapter instance;
    private List<ClosingTaskEntity> entities;

    public ClosingTaskAdapter() {

    }

    public static ClosingTaskAdapter getInstance() {
        if (instance == null) {
            instance = new ClosingTaskAdapter();
        }

        return instance;
    }

    @Override
    public int getCount() {
        if (entities == null) return 0;

        return entities.size();
    }

    @Override
    public ClosingTaskEntity getItem(int position) {
        if (entities == null) return null;

        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClosingTaskItem item;

        if (convertView == null) {
            item = new ClosingTaskItem(parent.getContext());
        } else {
            item = (ClosingTaskItem) convertView;
        }

        item.bind(getItem(position));
        return item;
    }

    public void clear() {
        if (entities != null) {
            entities.clear();
        }
    }

    public void processData(List<ClosingTaskEntity> ts) {
        if (ts == null || ts.size() <= 0) return;

        if (entities == null) {
            entities = ts;
        } else {
            entities.addAll(ts);
        }
        notifyDataSetChanged();
    }

    public void releaseResources() {
        clear();
        notifyDataSetChanged();

        entities = null;
        instance = null;
    }
}
