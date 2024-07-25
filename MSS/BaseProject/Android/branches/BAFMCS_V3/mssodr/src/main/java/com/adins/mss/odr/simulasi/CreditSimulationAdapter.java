package com.adins.mss.odr.simulasi;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.base.R;
import com.adins.mss.dao.Scheme;
import com.androidquery.AQuery;

public class CreditSimulationAdapter extends ArrayAdapter<Scheme>{
	private List<Scheme> objects;
	private Context context;
	AQuery query;
	public CreditSimulationAdapter(Context context,
			List<Scheme> objects) {
		super(context, R.layout.newtask_item_layout, objects);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.newtask_item_layout, parent, false);
        }
        query = new AQuery(convertView);
        Scheme scheme = objects.get(position);
        
        query.id(R.id.txtTaskTitle).text(scheme.getScheme_description());
        return convertView;
	}
}
