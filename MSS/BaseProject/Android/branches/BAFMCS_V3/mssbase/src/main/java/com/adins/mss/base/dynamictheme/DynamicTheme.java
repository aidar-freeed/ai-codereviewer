package com.adins.mss.base.dynamictheme;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.dao.Theme;
import com.adins.mss.foundation.db.dataaccess.ThemeDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by intishar.fa on 04/09/2018.
 */

public class DynamicTheme {

    private String uuidTheme;
    @SerializedName("version")
    private int version;
    private String applicationType;
    @SerializedName("items")
    private List<ThemeItem> themeItemList;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ThemeItem> getThemeItemList() {
        return themeItemList;
    }

    public void setThemeItemList(List<ThemeItem> themeItemList) {
        this.themeItemList = themeItemList;
    }

    public String getUuidTheme() {
        return uuidTheme;
    }

    public void setUuidTheme(String uuidTheme) {
        this.uuidTheme = uuidTheme;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public DynamicTheme(){}

    public DynamicTheme(Theme theme,List<com.adins.mss.dao.ThemeItem> themeList){//used after get list of theme from saved theme on local db
        if(themeList != null && themeList.size() > 0){
            themeItemList = new ArrayList<>();
            this.uuidTheme = theme.getUuid_theme();
            this.version = Integer.parseInt(theme.getVersion());
            this.applicationType = theme.getApplication_type();

            //set theme item list
            for (com.adins.mss.dao.ThemeItem themeitem :themeList) {
                if(themeitem!=null){
                    ThemeItem tempThemeItem = new ThemeItem();
                    tempThemeItem.setItem_id(themeitem.getUuid_theme_item());
                    tempThemeItem.setItemName(themeitem.getTheme_item());
                    tempThemeItem.setValue(themeitem.getValue());
                    themeItemList.add(tempThemeItem);
                }
            }
        }
    }

    public static Theme toThemeDao(Context context,DynamicTheme dynamicTheme){//used after get dynamictheme object from server to create obj
        if(dynamicTheme == null)
            return null;
        else {
            String version = String.valueOf(dynamicTheme.getVersion());
            Theme theme = new Theme();
            String uuidTheme = null;
            //check uuid from saved theme
            List<Theme> savedThemes = ThemeDataAccess.getThemeByApplicationType(context,
                    GlobalData.getSharedGlobalData().getApplication());
            if(savedThemes != null && savedThemes.size() > 0){
                uuidTheme = savedThemes.get(0).getUuid_theme();
            }
            else {
                uuidTheme = Tool.getUUID();//new theme
            }
            theme.setUuid_theme(uuidTheme);
            theme.setApplication_type(GlobalData.getSharedGlobalData().getApplication());
            theme.setVersion(version);
            return theme;
        }
    }

    public static List<com.adins.mss.dao.ThemeItem> toThemeItemList(Context context,String uuidTheme,List<ThemeItem> themeItemList){
        List<com.adins.mss.dao.ThemeItem> themeItemListDb = new ArrayList<>();
        for (ThemeItem item:themeItemList) {
            if(item == null)
                continue;
            com.adins.mss.dao.ThemeItem temp = new com.adins.mss.dao.ThemeItem();
            temp.setUuid_theme_item(Tool.getUUID());
            temp.setUuid_theme(uuidTheme);
            temp.setTheme_item(item.getItemName());
            temp.setValue(item.getValue());
            themeItemListDb.add(temp);
        }
        return themeItemListDb;
    }
}
