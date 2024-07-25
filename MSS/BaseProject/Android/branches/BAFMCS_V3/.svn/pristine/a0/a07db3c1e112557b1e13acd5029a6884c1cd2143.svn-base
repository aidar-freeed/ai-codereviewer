package com.adins.mss.base.todo.form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adins.mss.base.R;
import com.adins.mss.dao.Scheme;
import com.androidquery.AQuery;

import java.util.List;

public class NewTaskAdapter extends ArrayAdapter<Scheme> {
    AQuery query;
    private List<Scheme> objects;
    private Context context;

    public NewTaskAdapter(Context context,
                          List<Scheme> objects) {
        super(context, R.layout.new_newtask_item, objects);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.new_newtask_item, parent, false);
        }
        query = new AQuery(convertView);
        Scheme scheme = objects.get(position);

        query.id(R.id.txtTaskTitle).text(scheme.getScheme_description());
        return convertView;
    }
}
