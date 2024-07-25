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
import com.adins.mss.base.todolist.todayplanrepository.ResponseStartVisit;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.services.AutoSendImageThread;

import java.util.List;

public class StartVisitJob extends Service implements IPlanTaskDataSource.Result<ResponseStartVisit> {

    private TodayPlanRepository todayPlanRepo;
    private final String CHANNEL_ID = "START_VISIT_PLAN_CHANNEL";
    private final String CHANNEL_NAME = "Start Plans Notification";
    private final String CHANNEL_DESC = "Start Plans Notification";

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
        String title = getString(R.string.auto_start_visit_title);
        String content = getString(R.string.auto_start_visit_content);
        notifBuilder.setContentTitle(title);
        notifBuilder.setContentText(content);
        notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        notifBuilder.setProgress(0,0,true);
        return notifBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(13,createNotif());
        startVisit();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startVisit(){
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<PlanTask> currentPlans = PlanTaskDataAccess.getAllPlan(getApplicationContext(),uuidUser);
        todayPlanRepo.startVisit(currentPlans,this);
    }

    //start visit callback
    @Override
    public void onResult(ResponseStartVisit result) {
        if(result != null){
            todayPlanRepo.setStartVisit(true);
            todayPlanRepo.setNeedSync(false);
        }

        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onError(String error) {
        if(!todayPlanRepo.isStartVisit())
            todayPlanRepo.setStartVisit(false);

        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
