package com.services.plantask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.todolist.todayplanrepository.IPlanTaskDataSource;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.services.AutoSendImageThread;

public class ChangePlanService extends Service implements IPlanTaskDataSource.Result<Boolean> {

    private TodayPlanRepository todayPlanRepo;
    private final String CHANNEL_ID = "CHANGE_PLAN_CHANNEL";
    private final String CHANNEL_NAME = "Change Plans Notification";
    private final String CHANNEL_DESC = "Change Plans Notification";

    @Override
    public void onCreate() {
        super.onCreate();
        todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
        if(todayPlanRepo == null){
            todayPlanRepo = new TodayPlanRepository(getApplicationContext());
        }
    }

    private Notification createNotif(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
        notifBuilder.setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
        String title = getString(R.string.auto_changeplan_title);
        String content = getString(R.string.auto_changeplan_content);
        notifBuilder.setContentTitle(title);
        notifBuilder.setContentText(content);
        notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        notifBuilder.setProgress(0,0,true);
        return notifBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(53,createNotif());
        changePlan();
        return START_NOT_STICKY;
    }

    private void changePlan(){
        String[] lastOffChangePlan = todayPlanRepo.getLastOffChangePlanInfo();
        String oldPlan = lastOffChangePlan[0];
        String newPlan = lastOffChangePlan[1];
        todayPlanRepo.changePlan(oldPlan,newPlan,this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResult(Boolean result) {
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onError(String error) {
        stopForeground(true);
        stopSelf();
    }
}
