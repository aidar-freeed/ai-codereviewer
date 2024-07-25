package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.CollectionActivity;
import com.adins.mss.foundation.formatter.Formatter;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dian.ina on 08/05/2015.
 */
public class CollectionActivityDetailFragment extends AppCompatActivity {
    public static final String COLLECTION_ACTIVITY_DETAIL = "CollectionActivityDetailFragment.COLLECTION_ACTIVITY_DETAIL";
    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_fragment_collection_activity_detail);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        CollectionActivity item = CollectionActivityFragment.itemCollectionActivity;

        Toolbar toolbar = (Toolbar) findViewById(com.adins.mss.base.R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_mn_collectionactivitydet));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        TableLayout table = (TableLayout) findViewById(R.id.tableCollectionDetail);
        screenName = FirebaseAnalytics.getInstance(this);

        int index = 1;
        Class<CollectionActivity> type = CollectionActivity.class;

        for (Method method : type.getMethods()) {
            if (!method.getName().startsWith("get") || method.getName().equals("getClass")) {
                continue;
            }

            View row = LayoutInflater.from(this).inflate(R.layout.view_no_label_value, table, false);
            TextView no = (TextView) row.findViewById(R.id.no);
            TextView label = (TextView) row.findViewById(R.id.fieldName);
            TextView value = (TextView) row.findViewById(R.id.fieldValue);

            String labelField =method.getName().replaceFirst("get", "");

            if(labelField.equalsIgnoreCase("Dtm_crt")||
            		labelField.equalsIgnoreCase("Usr_crt")||
            		labelField.equalsIgnoreCase("Usr_upd")||
            		labelField.equalsIgnoreCase("dtm_upd")||
            		labelField.equalsIgnoreCase("uuid_collection_activity")||
            		labelField.equalsIgnoreCase("Uuid_task_h") ||
                    labelField.equalsIgnoreCase("agreement_no") ||
                    labelField.equalsIgnoreCase("branch_code")){

            }else{
            	no.setText(String.valueOf(index++));
                label.setText(labelField.replace("_", " "));
                try {
                    String stringValue = "";
                    Object itemValue = null;
                    try {
                        itemValue = method.invoke(item);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (itemValue == null) {
                        stringValue = "-";
                    } else if (method.getReturnType() == Date.class) {
                    	stringValue = Formatter.formatDate((Date)itemValue, Global.DATE_STR_FORMAT);
                    } else {
                        stringValue = String.valueOf(itemValue);
                    }

                    value.setText(stringValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                table.addView(row);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_collection_detail_act), null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }
}
