package com.adins.mss.odr.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.dao.TaskH;
import com.adins.mss.odr.R;
import com.adins.mss.odr.model.JsonResponseServer.ResponseServer;
import com.androidquery.AQuery;

import java.util.List;

public class OrderAssignmentAdapter extends ArrayAdapter<ResponseServer>{
	private Context context;
	private List<TaskH> objects;
	private List<ResponseServer> responseServer;
	private boolean isLookup = false;
	AQuery query;
	
	public OrderAssignmentAdapter(Context context, List<ResponseServer> objects, boolean isLookup) {
		super(context, R.layout.orderassignment_item_layout, objects);
		this.context = context;
		this.responseServer = objects;
		this.isLookup=isLookup;
	}
	@Override
    public int getCount() {
		return responseServer.size();
	}
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.orderassignment_item_layout, parent, false);
        }
        query = new AQuery(convertView);
        ResponseServer header = responseServer.get(position);
        if(isLookup){
        	if(header.getKey()!=null)
            	query.id(R.id.txtNoOrder).text(header.getFlag());
        }else{
        	if(header.getKey()!=null)
            	query.id(R.id.txtNoOrder).text(header.getKey());
        }        
        query.id(R.id.txtCustomerName).text(header.getValue());
        return convertView;
	}
}
