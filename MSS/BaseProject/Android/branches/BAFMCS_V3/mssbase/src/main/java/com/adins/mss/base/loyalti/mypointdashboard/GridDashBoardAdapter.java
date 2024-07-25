package com.adins.mss.base.loyalti.mypointdashboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adins.mss.base.R;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;

public class GridDashBoardAdapter extends BaseAdapter {

    private Context context;
    TextView huruftext;
    TextView angkatext;
    ImageView imagearrow;
    TextView angkastatustext;
    LinearLayout rankLayout;

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
            convertView = layoutInflater.inflate(R.layout.dashboard_rank_gridview, null);
        }
        huruftext = convertView.findViewById(R.id.levelRank);
        angkatext = convertView.findViewById(R.id.rankNumber);
        rankLayout = convertView.findViewById(R.id.rankLayout);

        final String[] warna = new String[] {
                "#cf0000",
                "#227322",
                "#592d59",
                "#666666",
                "#2a2a5e",
                "#2f6b6b",
                "#540054",
                "#5c4343",
                "#8a442c",
                "#6e6e25",
                "#a1401a"};

        rankLayout.setBackgroundColor(Color.parseColor(warna[position]));

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

        String hurufpertamacode = String.valueOf(dataGroupRank.get(position).getLEVEL());
        huruftext.setText(hurufpertamacode);
        angkatext.setText(ranknowstring);

        //        TextView dummyTextView = new TextView(context);
//        dummyTextView.setText(String.valueOf(position));
        return convertView;
    }
}

