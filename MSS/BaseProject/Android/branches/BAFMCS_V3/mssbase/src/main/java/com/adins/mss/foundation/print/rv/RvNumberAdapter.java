package com.adins.mss.foundation.print.rv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.base.R;
import com.adins.mss.dao.ReceiptVoucher;

import java.util.List;

/**
 * Created by angga.permadi on 5/11/2016.
 */
public class RvNumberAdapter extends ArrayAdapter<ReceiptVoucher> {
    private List<ReceiptVoucher> entities;
    private int resource;

    public RvNumberAdapter(Context context, int resource, List<ReceiptVoucher> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.entities = objects;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RvNumberItem item;

        if (convertView == null) {
            item = new RvNumberItem(parent.getContext(), R.layout.spinner_style_hint);
        } else {
            item = (RvNumberItem) convertView;
        }

        item.bind(entities.get(position), 1);
        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        RvNumberItem item;
        if (convertView == null) {
            item = new RvNumberItem(parent.getContext(), resource);
        } else {
            item = (RvNumberItem) convertView;
        }

        item.bind(entities.get(position), position);
        return item;
    }
}
