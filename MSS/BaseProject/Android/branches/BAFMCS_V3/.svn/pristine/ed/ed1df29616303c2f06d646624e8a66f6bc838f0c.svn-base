package com.adins.mss.coll.dummy;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.DashBoardFragment;
import com.adins.mss.coll.fragments.DashBoardFragment.OnListFragmentInteractionListener;
import com.adins.mss.coll.fragments.MyDashBoardItemRecyclerViewAdapter;
import com.adins.mss.coll.fragments.TeamMember;
import com.adins.mss.coll.fragments.dummy.DummyContent.DummyItem;
import com.adins.mss.coll.fragments.view.GridDashBoardAdapter;

import java.util.ArrayList;


/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDashboardItemDummyAdapter extends RecyclerView.Adapter<MyDashboardItemDummyAdapter.ViewHolder> {

    private final Activity mContext;
    RecyclerView recyclerViewDashBoard;
    MyDashBoardItemRecyclerViewAdapter adapter;
    public MyDashboardItemDummyAdapter(Activity context, RecyclerView recyclerView, MyDashBoardItemRecyclerViewAdapter adapter) {
        this.recyclerViewDashBoard = recyclerView;
        mContext = context;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ArrayList<TeamMember.DataGroupRank> dataGroupRank = new ArrayList<>();
        TeamMember.DataGroupRank dummyGroupRank1 = new TeamMember().new DataGroupRank();
        dummyGroupRank1.setLEVEL("JOB");
        dummyGroupRank1.setRANK("1");
        dummyGroupRank1.setRANK_BEFORE("0");

        TeamMember.DataGroupRank dummyGroupRank2 = new TeamMember().new DataGroupRank();
        dummyGroupRank2.setLEVEL("JOB");
        dummyGroupRank2.setRANK("1");
        dummyGroupRank2.setRANK_BEFORE("0");

        dataGroupRank.add(dummyGroupRank1);
        dataGroupRank.add(dummyGroupRank2);

        GridDashBoardAdapter adapter = new GridDashBoardAdapter(mContext , dataGroupRank);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, numbers);
        holder.gridView.setAdapter(adapter);

        UserHelpCOLDummy userHelpColDummy = new UserHelpCOLDummy();
        userHelpColDummy.showDashboardLoyalty(mContext, DashBoardFragment.class.getSimpleName(), this.recyclerViewDashBoard, this.adapter);
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
