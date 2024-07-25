package com.adins.mss.base.mainmenu;

import androidx.fragment.app.FragmentActivity;

import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMenuHelper {
    public static void doBackFragment(FragmentActivity activity) {
        NewMainActivity.fragmentManager.popBackStack();
    }

    public static List<String> matchingMenu(List<String> serverMenuTitle, List<String> templateMenuTitle) {
        List<String> newMenu = new ArrayList<String>();

        for (String templateMenu : templateMenuTitle) {
            mLoop:
            for (String serverMenu : serverMenuTitle) {
                if (serverMenu.equalsIgnoreCase(templateMenu)) {
                    newMenu.add(templateMenu);
                    break mLoop;
                }
            }
        }

        return newMenu;
    }

    public static List<Integer> matchingIcon(List<String> MenuTitle, HashMap<String, Integer> templateIcon) {
        List<Integer> newIcon = new ArrayList<Integer>();

        for (String templateMenu : MenuTitle) {
            newIcon.add(templateIcon.get(templateMenu));
        }

        return newIcon;
    }

    public static void showNotAvailableMenuDialog(FragmentActivity activity, String menuTitle) {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
        builder.withTitle(activity.getString(R.string.info_capital))
                .withIcon(android.R.drawable.ic_dialog_info)
                .withMessage(activity.getResources().getString(R.string.menu_not_available, menuTitle))
                .isCancelable(true)
                .show();
    }
}
