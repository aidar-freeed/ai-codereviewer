package com.adins.mss.coll.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.DashBoardFragment.OnListFragmentInteractionListener;
import com.adins.mss.coll.fragments.dummy.DummyContent.DummyItem;
import com.adins.mss.coll.fragments.view.GridDashBoardAdapter;
import com.adins.mss.foundation.formatter.Tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDashBoardItemRecyclerViewAdapter extends RecyclerView.Adapter<MyDashBoardItemRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private DetailKompetisiResponse dataDetailKompetisi;


    public MyDashBoardItemRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context) {
        mListener = listener;
        mContext = context;
    }

    public DetailKompetisiResponse getDataDetailKompetisi() {
        return dataDetailKompetisi;
    }

    public void setDataDetailKompetisi(DetailKompetisiResponse dataDetailKompetisi) {
        this.dataDetailKompetisi = dataDetailKompetisi;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = dataDetailKompetisi;
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        Date mydate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String yestr = dateFormat.format(mydate);

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat simpleDate1 = new SimpleDateFormat("d");
        holder.labelPointSaya.setText(mContext.getString(R.string.my_point_competition, yestr));

        DateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
        Date datetanggal1 = null;
        try {
            datetanggal1 = inputFormatter1.parse(dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_START_DATE());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter1 = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat outputFormatter2 = new SimpleDateFormat("dd MMM yyyy");
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
        holder.totalPoinKompetisi.setText(pointPeriod);

        if (dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD().length() > 6) {
            holder.totalPoinKompetisi.setTextSize(16);
        } else if (dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD().length() > 4) {
            holder.totalPoinKompetisi.setTextSize(20);
        } else {
            holder.totalPoinKompetisi.setTextSize(25);
        }

        Integer gracepointnow = Integer.parseInt(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD());
        Integer gracepointbefore = Integer.parseInt(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_PERIOD_BEFORE());
        if (gracepointnow < gracepointbefore) {
            holder.upDownGracePoint.setText("-" + String.valueOf(gracepointbefore - gracepointnow));
            holder.upDownGracePoint.setTextColor(Color.parseColor("#FF0000"));
        } else if (gracepointnow > gracepointbefore) {
            holder.upDownGracePoint.setText("+" + String.valueOf(gracepointnow - gracepointbefore));
            holder.upDownGracePoint.setTextColor(Color.parseColor("#008000"));
        } else {
            holder.upDownGracePoint.setVisibility(View.INVISIBLE);
        }

        double dpoinmonth = Double.parseDouble(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_MONTH());
        String poinMonth = Tool.formatToCurrency(dpoinmonth);
        holder.pointMonth.setText(poinMonth);

        Integer pointmonthnow = Integer.parseInt(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_MONTH());

        Integer pointmonthbefore = Integer.parseInt(dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getPOINT_MONTH_BEFORE());
        double dpoinmonthbefore = Double.valueOf(pointmonthbefore);
        String poinmonthbeforestring = Tool.formatToCurrency(dpoinmonthbefore);

        if (pointmonthnow < pointmonthbefore) {
            holder.textarrowUpDownMonth.setText(poinmonthbeforestring);
            holder.arrowUpDownMonth.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrowdownred));
        } else if (pointmonthnow > pointmonthbefore) {
            holder.textarrowUpDownMonth.setText(poinmonthbeforestring);
            holder.arrowUpDownMonth.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrowupgreen));
        } else {
            holder.textarrowUpDownMonth.setVisibility(View.INVISIBLE);
            holder.arrowUpDownMonth.setVisibility(View.INVISIBLE);
        }

        GetLogoKompetisi getLogoKompetisi = new GetLogoKompetisi(mContext, dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_CODE(), holder.logoKompetisi);
        getLogoKompetisi.execute();

        //DAILY AVERAGE MONTH
        String totaltanggal = simpleDate1.format(now);
        Integer tanggalHariIni = Integer.parseInt(totaltanggal);

        Integer kalender8total;
        if ((tanggalHariIni - 1) == 0) {
            kalender8total = 0;
        } else {
            kalender8total = pointmonthnow / (tanggalHariIni - 1);
        }

        String poinhari = Tool.formatToCurrency(kalender8total);

        Integer kalender8totalbefore;
        if ((tanggalHariIni - 1) == 0) {
            kalender8totalbefore = 0;
        } else {
            kalender8totalbefore = pointmonthbefore / (tanggalHariIni - 1);
        }

        String poin8beforestring = Tool.formatToCurrency(kalender8totalbefore);

        if (kalender8total < kalender8totalbefore) {
            holder.textratatotalhari.setText(poinhari);
            holder.textarrowUpDownRata.setText(poin8beforestring);
            holder.arrowUpDownRata.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrowdownred));
        } else if (kalender8total > kalender8totalbefore && kalender8totalbefore >= 0) {
            holder.textratatotalhari.setText(poinhari);
            holder.textarrowUpDownRata.setText(poin8beforestring);
            holder.arrowUpDownRata.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrowupgreen));
        } else {
            holder.textratatotalhari.setText(poinhari);
            holder.textratatotalhari.setVisibility(View.VISIBLE);
            holder.textarrowUpDownRata.setVisibility(View.INVISIBLE);
            holder.arrowUpDownRata.setVisibility(View.INVISIBLE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String waktusekarang = sdf.format(now);

        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(dataDetailKompetisi.getResultList().get(position).getMEMBERSHIP_PROGRAM_START_DATE());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String output1 = outputFormatter1.format(date1); //

        Date firstDate = null;
        try {
            firstDate = sdf.parse(output1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date secondDate = null;
        try {
            secondDate = sdf.parse(waktusekarang);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        int days = (int) (long) diff;

        int dailyAvgPeriodPoint;
        if (days <= 1) {
            dailyAvgPeriodPoint = 0;
        } else {
            dailyAvgPeriodPoint = gracepointnow / days;
        }

        String poinharirata = Tool.formatToCurrency(dailyAvgPeriodPoint);
        holder.textratatotal.setText(poinharirata);


        ArrayList<TeamMember.DataGroupRank> dataGroupRank = dataDetailKompetisi.getResultList().get(position).getTEAM_MEMBER().get(0).getDATA_GROUP_RANK();

        holder.totalPoinKompetisi.setOnClickListener(new View.OnClickListener() {
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
        private final TextView competitionName;
        private final TextView totalPoinKompetisi;
        private final TextView pointMonth;
        private final TextView startDate;
        private final TextView labelPointSaya;
        private final GridView gridView;
        private final ImageView logoKompetisi;
        private final ImageView arrowUpDownMonth;
        private final ImageView arrowUpDownRata;
        private final TextView textarrowUpDownMonth;
        private final TextView textarrowUpDownRata;
        private final TextView textratatotal;
        private final TextView textratatotalhari;
        private final TextView upDownGracePoint;
        private DetailKompetisiResponse mItem;

        private ViewHolder(View view) {
            super(view);
            competitionName = (TextView) view.findViewById(R.id.namaKompetisi);
            totalPoinKompetisi = (TextView) view.findViewById(R.id.poinPeriod);
            pointMonth = (TextView) view.findViewById(R.id.angka1);
            textratatotalhari = (TextView) view.findViewById(R.id.angka2);
            labelPointSaya = (TextView) view.findViewById(R.id.labelPoinSaya);

            startDate = (TextView) view.findViewById(R.id.dateEvent);
            gridView = (GridView) view.findViewById(R.id.gridview);
            logoKompetisi = (ImageView) view.findViewById(R.id.logoKompetisi);

            arrowUpDownMonth = (ImageView) view.findViewById(R.id.updownallcalender);
            arrowUpDownRata = (ImageView) view.findViewById(R.id.updownallcalender2);

            textarrowUpDownMonth = (TextView) view.findViewById(R.id.updatePointMonth);
            textarrowUpDownRata = (TextView) view.findViewById(R.id.updatePointRata);
            textratatotal = (TextView) view.findViewById(R.id.angka3);
            upDownGracePoint = (TextView) view.findViewById(R.id.updownGracePoin);

        }
    }
}
