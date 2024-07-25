package com.adins.mss.odr.accounts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.dao.Account;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.foundation.db.dataaccess.GroupTaskDataAccess;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.OpportunityListAdapter;

import java.util.List;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class OpportunityTabFragment extends Fragment {
    private FragmentActivity activity;
    private Account account;
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private String uuidAccount;
    private List<GroupTask> groupTasks;

    public OpportunityTabFragment(FragmentActivity activity, Account account) {
        this.activity = activity;
        this.account = account;
    }

    public OpportunityTabFragment(FragmentActivity activity, List<GroupTask> groupTasks) {
        this.activity = activity;
        this.groupTasks = groupTasks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_contact_tab, container, false);

        list = (RecyclerView) view.findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(activity);
        list.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        uuidAccount = account.getUuid_account();
        groupTasks = GroupTaskDataAccess.getAllByAccount(activity, uuidAccount);

        adapter = new OpportunityListAdapter(activity, groupTasks);
        list.setAdapter(adapter);

        return view;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setRetainInstance(true);
//    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.title_mn_account));

        adapter = new OpportunityListAdapter(activity, groupTasks);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
