package com.adins.mss.coll.assignment;

import java.util.List;

import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.coll.R;
import com.androidquery.AQuery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class OrderAssignmentAdapter extends ArrayAdapter<TaskH>{
	private Context context;
	private List<TaskH> objects;
	AQuery query;
	
	public OrderAssignmentAdapter(Context context, List<TaskH> objects) {
		super(context, R.layout.orderassignment_item_layout, objects);
		this.context = context;
		this.objects = objects;
	}
	@Override
    public int getCount() {
		return objects.size();
	}
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.orderassignment_item_layout, parent, false);
        }
        query = new AQuery(convertView);
        TaskH header = objects.get(position);
        
//        query.id(R.id.txtNoOrder).text(header.get);
        query.id(R.id.txtCustomerName).text(header.getCustomer_name());
        return convertView;
	}
}
