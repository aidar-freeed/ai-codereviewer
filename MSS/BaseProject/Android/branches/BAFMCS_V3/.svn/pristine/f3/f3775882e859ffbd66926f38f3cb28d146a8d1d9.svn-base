package com.adins.mss.odr.catalogue;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.Utility;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.db.dataaccess.MobileContentHDataAccess;
import com.adins.mss.odr.R;

import org.acra.ACRA;

import java.util.List;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class FragmentPromotion extends Fragment {
    private List<MobileContentH> promoList;

    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private PromoListAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        promoList = MobileContentHDataAccess.getAll(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.freeMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo, container, false);

        list = (RecyclerView) view.findViewById(R.id.promoList);
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new PromoListAdapter(getActivity(), promoList);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(com.adins.mss.base.R.id.search).setVisibility(View.GONE);
        getActivity().findViewById(com.adins.mss.base.R.id.spinner).setVisibility(View.GONE);
        getActivity().setTitle("PROMOTIONS");
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
