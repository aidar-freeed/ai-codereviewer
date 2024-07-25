package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.form.models.LookupCriteriaBean;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Locale;

public class LookupCriteriaOnlineActivity extends FragmentActivity {
    public static final String KEY_SELECTED_CRITERIA = "KEY_SELECTED_CRITERIA";
    public static final String KEY_WITH_FILTER = "KEY_WITH_FILTER";
    protected static List<LookupCriteriaBean> beanList;
    TableLayout criteriaTableLayout;
    private boolean withFilter;
    private FirebaseAnalytics screenName;

    public static List<LookupCriteriaBean> getBeanList() {
        return beanList;
    }

    public static void setBeanList(List<LookupCriteriaBean> beanList) {
        LookupCriteriaOnlineActivity.beanList = beanList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_lookup_online);
        criteriaTableLayout = (TableLayout) findViewById(R.id.tableCriteriaLayout);
        withFilter = getIntent().getBooleanExtra(KEY_WITH_FILTER, false);
        if (getBeanList() != null) {
            int index = 0;
            for (final LookupCriteriaBean bean : getBeanList()) {
                TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.lookup_criteria_row, criteriaTableLayout, false);
                if (!bean.getValue().equals("No Data Found")) {
                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(KEY_SELECTED_CRITERIA, bean);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
                row.setPadding(0, 10, 0, 10);

                String[] lookupCode = Tool.split(bean.getCode(), Global.DELIMETER_DATA_LOOKUP);
                String[] lookupValue = Tool.split(bean.getValue(), Global.DELIMETER_DATA_LOOKUP);
                StringBuilder lovCode = new StringBuilder();
                StringBuilder lovValue = new StringBuilder();
                for (String code : lookupCode) {
                    if (lovCode.length() != 0)
                        lovCode.append("\n");
                    lovCode.append(code);
                }
                for (String value : lookupValue) {
                    if (lovValue.length() != 0)
                        lovValue.append("\n");
                    lovValue.append(value);
                }

                TextView textDesc = (TextView) row.findViewById(R.id.fieldValue);
                textDesc.setText(lovValue.toString());
                criteriaTableLayout.addView(row);

                // set divider
                if (index + 1 < getBeanList().size()) {
                    TableRow divider = (TableRow) LayoutInflater.from(this).inflate(R.layout.lookup_criteria_divider, criteriaTableLayout, false);
                    criteriaTableLayout.addView(divider);
                    index++;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_lookup_criteria), null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.setBeanList(null);
    }

    @Override
    public void onBackPressed() {
        if (withFilter) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
}

