package com.adins.mss.base.timeline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.format.DateFormat;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Timeline;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;

import org.acra.ACRA;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 26/07/2017.
 */

public class TimelineImpl implements TimelineInterface {

    private static String tempUuidUser;
    private Activity activity;
    private Context context;
    private Bitmap bitmap;
    private List<Timeline> timelines;
    private String cashLimit;
    private String coh;
    private double limit;
    private double cashOnHand;
    private String sLimit;
    private String sCOH;
    private TimelineListener timelineListener;
    private int position = 0;
    private NewTimelineFragment timelineFragment;
    private static final String DATE_TIME_FORMAT = "yyyy.MM.dd G \'at\' HH:mm:ss z";
    private static final String ERROR_REFRESH_BACKGROUND = "errorRefreshBackgroundTask";

    public TimelineImpl(Activity activity) {
        this.activity = activity;
    }

    public TimelineImpl(Activity activity, TimelineListener timelineListener) {
        this.activity = activity;
        this.timelineListener = timelineListener;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public User getUser() {
        setTempUuidUser(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        return UserDataAccess.getOne(context, getTempUuidUser());
    }

    @Override
    public boolean isCOHAktif() {
        String parameter = GeneralParameterDataAccess.getOne(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                Global.GS_CASHONHAND).getGs_value();
        return parameter != null && parameter.equals(Global.TRUE_STRING);
    }

    @Override
    public AsyncTask<Void, Void, Bitmap> refreshImageBitmap(final int viewId, final int defaultDrawable, final byte[] byteImage) {
        return new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... args) {
                if (byteImage != null) {
                    try {
                        return Utils.byteToBitmap(byteImage);
                    } catch (OutOfMemoryError ex) {
                        ex.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorOutOfMemory", ex.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorOutOfMemory", DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception OutOfMemory"));
                        return null;
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        ACRA.getErrorReporter().putCustomData("errorConvertingByteToBitmap", e.getMessage());
                        ACRA.getErrorReporter().putCustomData("errorConvertingByteToBitmap", DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                        ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert byte to bitmap"));
                        return null;
                    }

                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap res) {
                super.onPostExecute(res);
                if (res != null) {
                    setBitmap(res);
                    timelineListener.onSuccessImageBitmap(res, viewId, defaultDrawable);
                }
            }
        };
    }

    @Override
    public AsyncTask<Void, Void, List<Timeline>> refreshBackgroundTask() {
        final int TASK_DURATION = 2 * 1000; // 2 seconds
        return new AsyncTask<Void, Void, List<Timeline>>() {
            @Override
            protected List<Timeline> doInBackground(Void... params) {
                // Sleep for a small amount of time to simulate a background-task
                try {
                    Thread.sleep(TASK_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    ACRA.getErrorReporter().putCustomData(ERROR_REFRESH_BACKGROUND, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_REFRESH_BACKGROUND, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Thread.sleep"));
                }
                try {
                    int range = GlobalData.getSharedGlobalData().getKeepTimelineInDays();
                    position = getPosition();
                    timelineFragment = new NewTimelineFragment();
                    return timelineFragment.getTimeline(position, range);

                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                    ACRA.getErrorReporter().putCustomData(ERROR_REFRESH_BACKGROUND, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_REFRESH_BACKGROUND, DateFormat.format(DATE_TIME_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set range"));
                }

                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<Timeline> records) {
                super.onPostExecute(records);
                if (records != null) {
                    setTimelines(records);
                    timelineListener.onSuccessBackgroundTask(records);
                }
            }
        };
    }

    public void setCashOnHand() {
        try {
            setCashLimit(GlobalData.getSharedGlobalData().getUser().getCash_limit());
            setLimit(cashLimit != null ? Double.parseDouble(cashLimit) : 0.0);
            setCoh(GlobalData.getSharedGlobalData().getUser().getCash_on_hand());
            setCashOnHand(coh != null ? Double.parseDouble(coh) : 0.0);
            setsLimit(Tool.separateThousand(limit));
            setsCOH(Tool.separateThousand(cashOnHand));
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }


    public List<Timeline> getTimelines() {
        return timelines;
    }

    public void setTimelines(List<Timeline> timelines) {
        this.timelines = timelines;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCashLimit() {
        return cashLimit;
    }

    public void setCashLimit(String cashLimit) {
        this.cashLimit = cashLimit;
    }

    public String getCoh() {
        return coh;
    }

    public void setCoh(String coh) {
        this.coh = coh;
    }

    public double getCashOnHand() {
        return cashOnHand;
    }

    public void setCashOnHand(double cashOnHand) {
        this.cashOnHand = cashOnHand;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public String getsLimit() {
        return sLimit;
    }

    public void setsLimit(String sLimit) {
        this.sLimit = sLimit;
    }

    public String getsCOH() {
        return sCOH;
    }

    public void setsCOH(String sCOH) {
        this.sCOH = sCOH;
    }

    public long getTaskListCounter() {
        return ToDoList.getAllCounter(activity);
    }


    public static String getTempUuidUser() {
        return tempUuidUser;
    }

    public static void setTempUuidUser(String tempUuidUser) {
        TimelineImpl.tempUuidUser = tempUuidUser;
    }
}
