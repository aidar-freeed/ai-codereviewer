package com.adins.mss.foundation.location;

import android.view.Menu;
import android.view.MenuItem;

import com.adins.mss.base.R;

public class UpdateMenuIcon {
    public boolean updateGPSIcon(Menu mainMenu) {
        if (null != mainMenu) {
            MenuItem existingItem = mainMenu.findItem(R.id.mnGPS);
            if (existingItem != null) {
                if (LocationTrackingManager.getLocationStatus() == 2) {
                    existingItem.setIcon(
                            R.drawable.location_on);
                } else if (LocationTrackingManager.getLocationStatus() == 1) {
                    existingItem.setIcon(
                            R.drawable.location_far);
                } else {
                    existingItem.setIcon(
                            R.drawable.location_off);
                }
            }
        }
        return true;
    }
}
