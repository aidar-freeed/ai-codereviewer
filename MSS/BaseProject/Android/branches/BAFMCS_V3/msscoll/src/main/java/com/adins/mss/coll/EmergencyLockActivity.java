package com.adins.mss.coll;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.coll.services.EmergencyService;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Emergency;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.EmergencyDataAccess;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyLockActivity extends AppCompatActivity {

    CircleImageView emAnimateSuccess;
    CircleImageView emPending, emAnimatePending;
    ImageView emSuccess;
    TextView textSuccess, textPending, textSuccessDescription;
    Button btnCancel;

    public static EmergencyHandler emergencyHandler;
    public static ReleaseEmergencyHandler releaseEmergencyHandler;
    private Handler waitCancelHandler;
    private Runnable waitCancelRunnable;
    private FirebaseAnalytics screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenName = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_emergency_lock);

        emAnimatePending = findViewById(R.id.animate_pending);
        emPending = findViewById(R.id.image_pending);
        emAnimateSuccess = findViewById(R.id.animate_success);
        emSuccess = findViewById(R.id.image_success);
        textSuccess = findViewById(R.id.text_success);
        textSuccessDescription = findViewById(R.id.text_success_description);
        textPending = findViewById(R.id.text_pending);
        btnCancel = findViewById(R.id.btn_cancel);

        emAnimatePending.setVisibility(View.VISIBLE);
        emPending.setVisibility(View.VISIBLE);
        emAnimatePending.setAnimation(loadAnimation(R.anim.wave));

        try {
            textPending.setText(GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_TEXT_EMERGENCY_MC).getGs_value());
        } catch (Exception e){
            textPending.setText(Global.DEFAULT_EMERGENCY_PENDING_TEXT);
        }

        emergencyHandler = new EmergencyHandler();
        releaseEmergencyHandler = new ReleaseEmergencyHandler();
        long waitTime = Global.DEFAULT_EMERGENCY_CANCEL_SEND;
        try {
            waitTime = Long.parseLong(GeneralParameterDataAccess.getOne(getApplicationContext(),GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_CANCEL_EMERGENCY_MC).getGs_value()) * Global.SECOND;
        } catch (Exception e){

        }
        String emergencyState = GlobalData.getSharedGlobalData().getUser().getIs_emergency();
        if(emergencyState.equalsIgnoreCase(Global.NO_EMERGENCY)||
                emergencyState.equalsIgnoreCase(Global.EMERGENCY_SEND_PENDING)){
            GlobalData.getSharedGlobalData().getUser().setIs_emergency(Global.EMERGENCY_SEND_PENDING);
            waitCancelHandler = new Handler();
            waitCancelRunnable = new Runnable() {
                @Override
                public void run() {
                    btnCancel.setVisibility(View.VISIBLE);
                }
            };
            waitCancelHandler.postDelayed(waitCancelRunnable,waitTime);
            Intent service = new Intent(this, EmergencyService.class);
            startService(service);
        } else if(emergencyState.equalsIgnoreCase(Global.EMERGENCY_SEND_SUCCESS)){
            emergencySentLayout();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        screenName.setCurrentScreen(this, getString(R.string.screen_name_emergency_lock), null);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void emergencySentLayout(){
        emSuccess.setImageResource(R.drawable.em_check_with_animation);
        emSuccess.setVisibility(View.VISIBLE);
        emAnimateSuccess.setVisibility(View.VISIBLE);
        textSuccess.setVisibility(View.VISIBLE);
        textSuccessDescription.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        textSuccess.setAnimation(loadAnimation(R.anim.fade_in));
        textSuccessDescription.setAnimation(loadAnimation(R.anim.fade_in));
        if(emSuccess.getDrawable() instanceof AnimatedVectorDrawableCompat){
            ((AnimatedVectorDrawableCompat) emSuccess.getDrawable()).start();
        } else if( emSuccess.getDrawable() instanceof AnimatedVectorDrawable){
            ((AnimatedVectorDrawable) emSuccess.getDrawable()).start();
        }
        emAnimatePending.setVisibility(View.GONE);
        textPending.setVisibility(View.GONE);
        emPending.setVisibility(View.GONE);
        emAnimatePending.setAnimation(null);
    }

    private Animation loadAnimation(int id){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), id);

        return animation;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void doCancel(View view) {
        GlobalData.getSharedGlobalData().getUser().setIs_emergency(Global.NO_EMERGENCY);
        UserDataAccess.addOrReplace(getApplicationContext(),GlobalData.getSharedGlobalData().getUser());

        List<Emergency> emergencies= EmergencyDataAccess.getByUser(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
        if(emergencies.size() > 0){
            EmergencyDataAccess.clean(getApplicationContext());
        }
        Intent service = new Intent(this, EmergencyService.class);
        stopService(service);
        finish();
    }

    public class EmergencyHandler extends Handler{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(@NonNull Message msg) {
            emergencySentLayout();
            waitCancelHandler.removeCallbacks(waitCancelRunnable);
            super.handleMessage(msg);
        }

    }

    public class ReleaseEmergencyHandler extends Handler{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (NewMainActivity.AutoSendLocationHistoryService != null)
                getApplicationContext().stopService(NewMainActivity.AutoSendLocationHistoryService);
            User user = GlobalData.getSharedGlobalData().getUser();
            String uuid_user = user.getUuid_user();
            GeneralParameter gp = GeneralParameterDataAccess.getOne(getApplicationContext(),
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_ENABLE_EMERGENCY_MC);
            if(null != gp) {
                gp.setGs_value(msg.obj + "");
                GeneralParameterDataAccess.addOrReplace(getApplicationContext(), gp);
            }
            EmergencyDataAccess.delete(getApplicationContext(), uuid_user);
            user.setIs_emergency(Global.NO_EMERGENCY);
            UserDataAccess.addOrReplace(getApplicationContext(),user);
            Intent intent = new Intent(getApplicationContext(), NewMCMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        Intent service = new Intent(this, EmergencyService.class);
        stopService(service);
        super.onDestroy();
    }
}
