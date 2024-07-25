package com.adins.mss.odr.accounts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.foundation.db.dataaccess.AccountDataAccess;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.adapter.AccountListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.aap on 11/27/2018.
 */

public class FragmentAccountList extends Fragment {
    private ArrayList<String> uuidAccounts;
    private List<Account> accountList;
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private FirebaseAnalytics screenName;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_account_list, container, false);
        screenName = FirebaseAnalytics.getInstance(getActivity());
        uuidAccounts = new ArrayList<>();
        Bundle bundle = getArguments();
        uuidAccounts = bundle.getStringArrayList(Global.BUND_KEY_ACCOUNT_ID);
        accountList = new ArrayList<>();
        if (uuidAccounts != null) {
            for (String uuid : uuidAccounts) {
                Account account = AccountDataAccess.getOne(getActivity(), uuid);
                accountList.add(account);
            }
        }

        list = (RecyclerView) view.findViewById(R.id.accountList);
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new AccountListAdapter(getActivity(), accountList);
        list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(getActivity(), getString(R.string.screen_name_account_list), null);
        getActivity().setTitle(getString(com.adins.mss.base.R.string.title_mn_account));
    }

    @Override
    public void onPause() {
        super.onPause();
//        mIndicator.cleanup();
    }
}
