package com.adins.mss.odr.news;

import java.util.List;

import com.adins.mss.dao.MobileContentH;
import com.adins.mss.odr.R;
import com.androidquery.AQuery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsListAdapter extends ArrayAdapter{
	private List<MobileContentH> objects;
	private Context context;
	private AQuery query; 

	public NewsListAdapter(Context context, List<MobileContentH> objects) {
		super(context, R.layout.news_listparent_item_layout);
		this.objects = objects;
		this.context = context;
	}
	@Override
    public int getCount() {
    	return objects.size();
    }

	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_listparent_item_layout, parent, false);
        }
        MobileContentH contentH = objects.get(position);
        TextView textView = (TextView)convertView.findViewById(R.id.titleNewsParent);
        textView.setText(contentH.getContent_name());
//        query.id(R.id.titleNewsParent).text(contentH.getContent_name());
        return convertView;
	}
}
