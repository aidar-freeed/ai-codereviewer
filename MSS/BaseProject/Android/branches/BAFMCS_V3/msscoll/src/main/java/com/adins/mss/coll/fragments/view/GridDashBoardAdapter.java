package com.adins.mss.coll.fragments.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.MyDashBoardItemRecyclerViewAdapter;
import com.adins.mss.coll.fragments.TeamMember;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;

public class GridDashBoardAdapter extends BaseAdapter {

    private Context context;
    TextView huruftext;
    TextView angkatext;
    ImageView imagearrow;
    TextView angkastatustext;

    ArrayList<TeamMember.DataGroupRank> dataGroupRank = new ArrayList<TeamMember.DataGroupRank>();

    // 1
//    public GridDashBoardAdapter(MyDashBoardItemRecyclerViewAdapter context, ArrayList<String> books) {
//        this.mContext = context;
//        this.books = books;
//    }

    public GridDashBoardAdapter(Context mContext , ArrayList<TeamMember.DataGroupRank> dataGroupRank) {
        this.context = mContext;
        this.dataGroupRank = dataGroupRank;
    }

    // 2
    @Override
    public int getCount() {
        return dataGroupRank.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.dashboard_gridview, null);
        }
        huruftext = convertView.findViewById(R.id.huruf);
        angkatext = convertView.findViewById(R.id.angka);

        final String[] warna = new String[] {
                "#FF0000",
                "#FFFF00",
                "#32CD32",
                "#0000FF",
                "#00FFFF",
                "#FF00FF",
                "#800080",
                "#808080",
                "#C0C0C0",
                "#E9967A",
                "#FFA07A"};


        huruftext.setBackgroundColor(Color.parseColor(warna[position]));

        imagearrow = convertView.findViewById(R.id.arrowstatus);
        angkastatustext = convertView.findViewById(R.id.angkastatus);

        Integer ranknow = Integer.parseInt(dataGroupRank.get(position).getRANK());
        Integer rankbefore;
        if(dataGroupRank.get(position).getRANK_BEFORE() != null){
            rankbefore = Integer.parseInt(dataGroupRank.get(position).getRANK_BEFORE());
        }else {
            rankbefore = Integer.parseInt(dataGroupRank.get(position).getRANK());
        }

        double drankbefore = Double.valueOf(rankbefore);
        String rankbeforestring = Tool.formatToCurrency(drankbefore);

        double dranknow = Double.valueOf(ranknow);
        String ranknowstring = Tool.formatToCurrency(dranknow);

        if(ranknow < rankbefore){
            angkastatustext.setText(rankbeforestring);
            imagearrow.setImageDrawable(context.getDrawable(R.drawable.arrowupgreen_64));
        }else if(ranknow > rankbefore){
            angkastatustext.setText(rankbeforestring);
            imagearrow.setImageDrawable(context.getDrawable(R.drawable.arrowdownred_64));
        }else {
            angkastatustext.setVisibility(View.INVISIBLE);
            imagearrow.setVisibility(View.INVISIBLE);
        }

        String hurufpertamacode = String.valueOf(dataGroupRank.get(position).getLEVEL().charAt(0));
        huruftext.setText(hurufpertamacode);
        angkatext.setText(ranknowstring);

        //        TextView dummyTextView = new TextView(context);
//        dummyTextView.setText(String.valueOf(position));
        return convertView;
    }
}
