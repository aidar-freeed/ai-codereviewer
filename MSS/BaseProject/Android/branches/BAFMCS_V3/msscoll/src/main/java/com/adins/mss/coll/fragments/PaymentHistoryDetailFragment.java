package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.foundation.formatter.Formatter;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by adityapurwa on 23/03/15.
 */
public class PaymentHistoryDetailFragment extends AppCompatActivity {
    public static final String PAYMENT_HISTORY_DETAIL = "PaymentHistoryDetailFragment.PAYMENT_HISTORY_DETAIL";
    public static final String BUND_KEY_TRANSACTIONCODE= "TRANSACTIONCODE";
    private FirebaseAnalytics screenName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.new_fragment_payment_history_detail);
        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_mn_paymenthistorydet));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

    	List<PaymentHistoryD> details = PaymentHistoryFragment.details;
        TableLayout table = (TableLayout) findViewById(R.id.tableCollectionDetail);

        TextView transactionCode = (TextView)findViewById(R.id.transactionCode);
        transactionCode.setText(getIntent().getStringExtra(PaymentHistoryDetailFragment.BUND_KEY_TRANSACTIONCODE));

        for(PaymentHistoryD detail : details) {

            int index = 1;
            Class<PaymentHistoryD> type = PaymentHistoryD.class;

            for (Method method : type.getMethods()) {
                if (!method.getName().startsWith("get") ||  method.getName().equals("getClass")) {
                    continue;
                }

                View row = LayoutInflater.from(this).inflate(
                        R.layout.view_no_label_value, table, false);
                TextView no = (TextView) row.findViewById(R.id.no);
                TextView label = (TextView) row.findViewById(R.id.fieldName);
                TextView value = (TextView) row.findViewById(R.id.fieldValue);

                String labelField = method.getName().replaceFirst("get", "");

                if (!labelField.equalsIgnoreCase("Dtm_crt")
                        && !labelField.equalsIgnoreCase("Dtm_upd")
                        && !labelField.equalsIgnoreCase("Usr_crt")
                        && !labelField.equalsIgnoreCase("Usr_upd")
                        && !labelField.equalsIgnoreCase("Uuid_installment_schedule")
                        && !labelField.equalsIgnoreCase("Uuid_task_h")
                        && !labelField.equalsIgnoreCase("Uuid_payment_history_h")
                        && !labelField.equalsIgnoreCase("Uuid_payment_history_d")) {
                    no.setText(String.valueOf(index++));
                    label.setText(labelField.replace("_", " "));
                    try {
                        String stringValue = "";
                        Object itemValue = null;
                        itemValue = method.invoke(detail);
                        if (itemValue == null) {
                            stringValue = "-";
                        } else if (method.getReturnType() == Date.class) {
                            stringValue = Formatter.formatDate((Date) itemValue,
                                    Global.DATE_STR_FORMAT);
                        } else {
                            stringValue = String.valueOf(itemValue);
                        }
                        if (stringValue.length() > 3) {
                            String part1 = stringValue.substring(stringValue.length() - 3);
                            if (part1.substring(0, 1).equals(".")) {
                                value.setGravity(Gravity.RIGHT);
                            }
                        }
                        value.setText(stringValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    table.addView(row);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_payment_history_detail), null);
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