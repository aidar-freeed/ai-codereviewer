package com.adins.mss.base.loyalti.mypointdashboard;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;

import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPoint.OnListFragmentInteractionListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DashboardMyPointItemRecyclerViewAdapter extends RecyclerView.Adapter<DashboardMyPointItemRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    DetailKompetisiResponse dataDetailKompetisi;


    public DashboardMyPointItemRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context) {
        mListener = listener;
        mContext = context;
    }

    public void setDataDetailKompetisi(DetailKompetisiResponse dataDetailKompetisi) {
        this.dataDetailKompetisi = dataDetailKompetisi;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = dataDetailKompetisi;

        Date mydate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
        SimpleDateFormat dateFormat = new SimpleDateFormat(Global.DATE_STR_FORMAT3);
        String yestr = dateFormat.format(mydate);

        holder.labelPointSaya.setText(mContext.getString(R.string.my_rank, yestr));

        DateFormat inputFormatter1 = new SimpleDateFormat(Global.DATE_STR_FORMAT1);
        Date datetanggal1 = null;
        try {
            datetanggal1 = inputFormatter1.parse(dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_START_DATE());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter2 = new SimpleDateFormat(Global.DATE_STR_FORMAT3);
        String outputtanggal1 = outputFormatter2.format(datetanggal1); //
        Date datetanggal2 = null;
        try {
            datetanggal2 = inputFormatter1.parse(dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_EXPIRED_DATE());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputtanggal2 = outputFormatter2.format(datetanggal2); //

        holder.startDate.setText(outputtanggal1 + " - " + outputtanggal2);
        holder.competitionName.setText(dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_NAME());

        final double d = Double.parseDouble(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD());
        String pointPeriod = Tool.formatToCurrency(d);
        holder.poinPeriod.setText(pointPeriod);

        if (dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD().length() > 4) {
            holder.poinPeriod.setTextSize(12);
        } else {
            holder.poinPeriod.setTextSize(15);
        }

        GetLogoKompetisi getLogoKompetisi = new GetLogoKompetisi(mContext, dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_CODE(), holder.logoKompetisi);
        getLogoKompetisi.execute();

        ArrayList<TeamMember.DataGroupRank> dataGroupRank = dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getDATA_GROUP_RANK();

        holder.myPointLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (d == 0) {
                        Toast.makeText(mContext, mContext.getString(R.string.points_detail_not_found), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("MembershipProgramCode", dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_CODE());
                    bundle.putString("MembershipProgramName", dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_NAME());
                    bundle.putString("ProgramStartDate", dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_START_DATE());
                    bundle.putString("CurrentMonthPoint", dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_MONTH());
                    bundle.putString("PreMonthPoint", dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_MONTH_BEFORE());
                    bundle.putString("GracePointNow", dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD());
                    bundle.putString("GracePointBefore", dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD_BEFORE());
                    mListener.onListFragmentInteraction(bundle);
                }
            }
        });

        GridDashBoardAdapter adapter = new GridDashBoardAdapter(mContext, dataGroupRank);
        holder.gridView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dataDetailKompetisi.getResultList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView competitionName;
        public final TextView poinPeriod;
        public final TextView startDate;
        public final TextView labelPointSaya;
        public final GridView gridView;
        public final ImageView logoKompetisi;
        public final CardView cardView;
        public DetailKompetisiResponse mItem;
        public LinearLayout myPointLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            myPointLayout = view.findViewById(R.id.my_point_layout);
            cardView = view.findViewById(R.id.card_view);
            competitionName = view.findViewById(R.id.namaKompetisi);
            poinPeriod =  view.findViewById(R.id.poinPeriod);
            labelPointSaya = view.findViewById(R.id.labelRank);

            startDate =  view.findViewById(R.id.dateEvent);
            gridView = view.findViewById(R.id.gridview);
            logoKompetisi =  view.findViewById(R.id.logoKompetisi);

            //marquee purpose, since name limit is 200 character
            competitionName.setSelected(true);

        }

    }
}

