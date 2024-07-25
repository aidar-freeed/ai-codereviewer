package com.adins.mss.base.commons;


import androidx.fragment.app.Fragment;

/**
 * Created by Aditya Purwa on 1/13/2015.
 */
public interface FragmentHost {
    public Fragment getActiveFragment();

    public void setActiveFragment(Fragment fragment);
}
