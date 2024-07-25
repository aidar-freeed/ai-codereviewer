package com.adins.mss.coll.dashboardcollection.view;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adins.mss.coll.R;
import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardCollResultDetail extends Fragment implements CollResultPagerAdapter.PagerDataChangeListener {

    RecyclerView collResultList;
    private DashCollResultItemAdapter adapter;
    private TextView resultHeaderTitle;
    private ConstraintLayout titleHeader;

    public DashboardCollResultDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_coll_result_detail, container, false);
        collResultList = view.findViewById(R.id.dashCollResultList);
        titleHeader = view.findViewById(R.id.dashCollResultListHeader);
        titleHeader.setVisibility(View.GONE);
        resultHeaderTitle = view.findViewById(R.id.dashResultHeader);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        collResultList.setLayoutManager(linearLayoutManager);
        collResultList.setHasFixedSize(true);

        DividerItemDecoration divDecorator = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        collResultList.addItemDecoration(divDecorator);
        adapter = new DashCollResultItemAdapter(getActivity(),new ArrayList<CollResultDetail>());
        collResultList.setAdapter(adapter);
    }

    @Override
    public void onPagerDataChange(int idx, List<CollResultDetail> data) {
        if(data.size() == 0){
            titleHeader.setVisibility(View.GONE);
            return;
        }

        titleHeader.setVisibility(View.VISIBLE);
        switch (idx){
            case 0:
                resultHeaderTitle.setText(getString(R.string.amount_paid));
                break;
            case 1:
                resultHeaderTitle.setText(getString(R.string.ptp_date));
                break;
            case 2:
                resultHeaderTitle.setText(getString(R.string.notes));
                break;
            default:
                break;
        }

        adapter.notifyDataChange(data);
    }
}
