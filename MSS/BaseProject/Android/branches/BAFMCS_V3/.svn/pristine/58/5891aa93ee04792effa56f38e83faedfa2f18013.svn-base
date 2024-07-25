package com.adins.mss.coll.dashboardcollection.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.adins.mss.coll.dashboardcollection.model.CollResultDetail;

import java.util.ArrayList;
import java.util.List;

public class CollResultPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    List<PagerDataChangeListener> pagerDataListener = new ArrayList<>();

    public interface PagerDataChangeListener{
        void onPagerDataChange(int idx,List<CollResultDetail> data);
    }

    public CollResultPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int i) {
        DashboardCollResultDetail fragment = null;
        if(pagerDataListener.size() + 1 > tabCount){
            fragment = (DashboardCollResultDetail) pagerDataListener.get(i);
        }
        else {
            fragment = new DashboardCollResultDetail();
            pagerDataListener.add(fragment);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void setDataToPage(int index, List<CollResultDetail> data){
        if(index >= pagerDataListener.size()){
            return;
        }

        pagerDataListener.get(index).onPagerDataChange(index,data);
    }

}
