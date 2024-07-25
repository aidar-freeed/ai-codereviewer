package com.adins.mss.base.loyalti.userhelpdummy;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.adins.mss.base.R;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPoint;
import com.adins.mss.base.loyalti.mypointdashboard.DashboardMyPointItemRecyclerViewAdapter;
import com.adins.mss.base.loyalti.mypointdashboard.GridDashBoardAdapter;
import com.adins.mss.base.loyalti.mypointdashboard.TeamMember;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;

import java.util.ArrayList;

public class DashboardMyPointItemDummyAdapter extends RecyclerView.Adapter<DashboardMyPointItemDummyAdapter.ViewHolder> {

    private final Activity mContext;
    RecyclerView recyclerViewDashBoard;
    DashboardMyPointItemRecyclerViewAdapter adapter;
    public DashboardMyPointItemDummyAdapter(Activity context, RecyclerView recyclerView, DashboardMyPointItemRecyclerViewAdapter adapter) {
        this.recyclerViewDashBoard = recyclerView;
        mContext = context;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ArrayList<TeamMember.DataGroupRank> dataGroupRank = new ArrayList<>();
        TeamMember.DataGroupRank dummyGroupRank1 = new TeamMember().new DataGroupRank();
        dummyGroupRank1.setLEVEL("LEVEL 1");
        dummyGroupRank1.setRANK("1");
        dummyGroupRank1.setRANK_BEFORE("5");

        TeamMember.DataGroupRank dummyGroupRank2 = new TeamMember().new DataGroupRank();
        dummyGroupRank2.setLEVEL("LEVEL 2");
        dummyGroupRank2.setRANK("1");
        dummyGroupRank2.setRANK_BEFORE("10");

        TeamMember.DataGroupRank dummyGroupRank3 = new TeamMember().new DataGroupRank();
        dummyGroupRank3.setLEVEL("LEVEL 3");
        dummyGroupRank3.setRANK("6");
        dummyGroupRank3.setRANK_BEFORE("15");

        dataGroupRank.add(dummyGroupRank1);
        dataGroupRank.add(dummyGroupRank2);
        dataGroupRank.add(dummyGroupRank3);

        GridDashBoardAdapter dashBoardAdapter = new GridDashBoardAdapter(mContext , dataGroupRank);
        holder.gridView.setAdapter(dashBoardAdapter);

        UserHelpGeneralDummy userHelpSvyDummy = new UserHelpGeneralDummy();
        userHelpSvyDummy.showDetailKompetisi(mContext, DashboardMyPoint.class.getSimpleName(), this.recyclerViewDashBoard, this.adapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final GridView gridView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            gridView = (GridView) view.findViewById(R.id.gridview);
        }
    }
}
