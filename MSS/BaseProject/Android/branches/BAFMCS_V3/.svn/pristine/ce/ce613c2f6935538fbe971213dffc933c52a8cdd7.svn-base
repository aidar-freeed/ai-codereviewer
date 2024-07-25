package com.adins.mss.coll.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.checkin.CheckInManager;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.view.PaymentHistoryView;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.foundation.UserHelp.UserHelp;
import com.adins.mss.foundation.location.UpdateMenuIcon;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.acra.ACRA;

import java.util.List;
import java.util.Locale;

import static com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT;

/**
 * Created by adityapurwa on 20/03/15.
 */
public class PaymentHistoryFragment extends AppCompatActivity {
    public static List<PaymentHistoryD> details;
    private PaymentHistoryView view;

    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.new_fragment_payment_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_mn_paymenthistory));
        toolbar.setTitleTextColor(getResources().getColor(com.adins.mss.base.R.color.fontColorWhite));
        setSupportActionBar(toolbar);

        ACRA.getErrorReporter().putCustomData("LAST_CLASS_ACCESSED", getClass().getSimpleName());
        view = new PaymentHistoryView(this);
        view.onCreate();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserHelp.showAllUserHelp(PaymentHistoryFragment.this,PaymentHistoryFragment.this.getClass().getSimpleName());
            }
        }, SHOW_USERHELP_DELAY_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        if(!Global.BACKPRESS_RESTRICTION) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.adins.mss.base.R.id.mnGPS && Global.LTM != null) {
            if (Global.LTM.getIsConnected()) {
                Global.LTM.removeLocationListener();
                Global.LTM.connectLocationClient();
            } else {
                CheckInManager.startGPSTracking(getApplicationContext());
            }
            Animation a = AnimationUtils.loadAnimation(this, com.adins.mss.base.R.anim.gps_rotate);
            findViewById(com.adins.mss.base.R.id.mnGPS).startAnimation(a);
        }
        if(item.getItemId() == R.id.mnGuide && !Global.BACKPRESS_RESTRICTION){
            UserHelp.reloadUserHelp(getApplicationContext(), PaymentHistoryFragment.this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserHelp.showAllUserHelp(PaymentHistoryFragment.this, PaymentHistoryFragment.this.getClass().getSimpleName());
                }
            }, SHOW_USERHELP_DELAY_DEFAULT);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(com.adins.mss.base.R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuIcon();

        if(Global.ENABLE_USER_HELP &&
                (Global.userHelpGuide.get(PaymentHistoryFragment.this.getClass().getSimpleName())!=null) ||
                Global.userHelpDummyGuide.get(PaymentHistoryFragment.this.getClass().getSimpleName()) != null){
            menu.findItem(com.adins.mss.base.R.id.mnGuide).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Firebase screen name
        screenName.setCurrentScreen(this, getString(R.string.screen_name_payment_history), null);
    }

    private static Menu mainMenu;

    public static void updateMenuIcon() {
        UpdateMenuIcon uItem = new UpdateMenuIcon();
        uItem.updateGPSIcon(mainMenu);
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