package com.adins.mss.svy.models;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.adins.mss.svy.R;

/**
 * Created by winy.firdasari on 20/01/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    private LinkedHashMap<String, String> listResult = new LinkedHashMap<>();

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public ExpandableListAdapter(Context context, LinkedHashMap<String, String> listResult) {
        this._context = context;
        this.listResult = listResult;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.result_order_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.txtStatusOrder);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    public String getKey(int pos) {
        return String.valueOf(listResult.keySet().toArray()[pos]);
    }

    public String getValues(String key) {
        return String.valueOf(listResult.get(key));
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listResult.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
//        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.new_survey_assignment_item, null);
        }

//        TextView lblListHeader = (TextView) convertView
//                .findViewById(R.id.txtOrderList);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
//        lblListHeader.setText(headerTitle);

        String noOrder = getKey(groupPosition);
        String custName = getValues(noOrder);

        TextView lblNoOrder = (TextView) convertView.findViewById(R.id.txtNoOrder);
        TextView lblCustName = (TextView) convertView.findViewById(R.id.txtCustomerName);

        lblNoOrder.setText(noOrder);
        lblCustName.setText(custName);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    public void removeGroup(int position){    	
    	_listDataChild.remove(_listDataHeader.get(position));
    	_listDataHeader.remove(position);
    	notifyDataSetChanged();
    }
}
