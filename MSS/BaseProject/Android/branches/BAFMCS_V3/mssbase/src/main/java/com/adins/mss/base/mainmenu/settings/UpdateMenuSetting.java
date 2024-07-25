package com.adins.mss.base.mainmenu.settings;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.adins.mss.base.R;
import com.adins.mss.base.commons.SettingImpl;
import com.adins.mss.base.commons.SettingInterface;
import com.adins.mss.base.util.LocaleHelper;

/**
 * Created by olivia.dg on 9/20/2017.
 */

public class UpdateMenuSetting {
    private SettingInterface setting;
    private Context context;

    public UpdateMenuSetting(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean updateFlagIcon(Menu mainMenu) {
        MenuItem existingItem = mainMenu.findItem(R.id.mnSetting);
        setting = new SettingImpl(context);
        if (existingItem != null) {
            existingItem.setIcon(R.drawable.ic_repairing_service);
//            if (setting.getLanguage().equals(LocaleHelper.ENGLSIH)) {
//                existingItem.setIcon(R.drawable.english);
//            } else {
//                existingItem.setIcon(R.drawable.bahasa);
//            }
        }
        return true;
    }
}
