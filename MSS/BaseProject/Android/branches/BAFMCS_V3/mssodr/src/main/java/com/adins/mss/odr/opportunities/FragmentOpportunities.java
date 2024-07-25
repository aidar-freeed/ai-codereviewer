package com.adins.mss.odr.opportunities;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.foundation.db.dataaccess.GroupTaskDataAccess;
import com.adins.mss.odr.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class FragmentOpportunities extends Fragment {
    private FragmentActivity activity;
    private List<Product> productList;
    private RecyclerView list;
    private OpportunityMenuListAdapter adapter;
    private List<Account> accountList;
    private List<String> uuidAccount;
    private List<GroupTask> groupTasks;
    private FirebaseAnalytics screenName;

    private List<Map<Integer,List<GroupTask>>> listTaskHistory = new ArrayList<>();
    Map<Integer, List<GroupTask>> oneListTaskHistory = new HashMap<Integer, List<GroupTask>>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenName = FirebaseAnalytics.getInstance(getActivity());
        View view = inflater.inflate(R.layout.fragment_opportunities, container, false);

        list = (RecyclerView) view.findViewById(R.id.opportunityList);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);

        List<Account> listAcc = AccountDataAccess.getAll(getActivity());
        accountList=new ArrayList<>();
        if (listAcc != null){
            for(int i=0;i<listAcc.size();i++){
                accountList.add(listAcc.get(i));
            }

            uuidAccount=new ArrayList<>();
            for(int i=0;i<listAcc.size();i++){
                uuidAccount.add(accountList.get(i).getUuid_account());
            }

            for(int i=0;i<uuidAccount.size();i++){
                oneListTaskHistory.put(i, GroupTaskDataAccess.getAllByAccount(getActivity(), uuidAccount.get(i)));
                listTaskHistory.add(oneListTaskHistory);
            }
        }

        adapter = new OpportunityMenuListAdapter(getActivity(), listTaskHistory);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_opportunities_page), null);
        // getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        //  getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        // getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_catalogue));
    }

    @Override
    public void onPause() {
        super.onPause();
//        mIndicator.cleanup();
    }
}