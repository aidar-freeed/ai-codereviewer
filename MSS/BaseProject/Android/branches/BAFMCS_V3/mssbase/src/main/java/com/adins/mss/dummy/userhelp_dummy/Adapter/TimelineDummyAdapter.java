package com.adins.mss.dummy.userhelp_dummy.Adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.R;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.dummy.userhelp_dummy.UserHelpGeneralDummy;

public class TimelineDummyAdapter extends RecyclerView.Adapter<TimelineDummyAdapter.DummyViewHolder> {
    RecyclerView rv;
    NewTimelineFragment activity;
    public TimelineDummyAdapter(NewTimelineFragment activity, RecyclerView rv) {
        this.rv = rv;
        this.activity = activity;
    }

    @NonNull
    @Override
    public DummyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dummy_userhelp_timeline_item, viewGroup, false);
        DummyViewHolder viewHolder = new DummyViewHolder (v);
        UserHelpGeneralDummy userHelpGeneralDummy = new UserHelpGeneralDummy();
        userHelpGeneralDummy.showDummyTimeline(activity.getActivity(), activity.getClass().getSimpleName(), rv, activity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DummyViewHolder dummyViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class DummyViewHolder extends RecyclerView.ViewHolder {
        public DummyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
