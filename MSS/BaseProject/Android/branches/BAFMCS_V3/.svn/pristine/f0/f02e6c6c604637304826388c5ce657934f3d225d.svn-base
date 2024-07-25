package com.adins.mss.coll.models;

import java.util.List;

import com.adins.mss.coll.R;
import com.adins.mss.dao.DepositReportH;
import com.androidquery.AQuery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DepositReportAdapter extends ArrayAdapter<DepositReportH> {
	private List<DepositReportH> objects;
	private Context context;
	AQuery query;

	public DepositReportAdapter(Context context, List<DepositReportH> objects) {
		super(context, R.layout.new_recapitulation_item, objects);
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
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.new_recapitulation_item, parent, false);
		}
		query = new AQuery(convertView);
		DepositReportH reportH = objects.get(position);

		query.id(R.id.txtTaskTitle).text(reportH.getBatch_id());
		return convertView;
	}
}
