package com.adins.mss.base;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.services.NotificationThread.getNotificationIcon;
import static java.lang.Thread.NORM_PRIORITY;

class BackupFormat {
    @SerializedName("taskH")
    private TaskH taskH;

    @SerializedName("taskDList")
    private List<TaskD> taskDList;

    public TaskH getTaskH() {
        return taskH;
    }

    public void setTaskH(TaskH taskH) {
        this.taskH = taskH;
    }

    public List<TaskD> getTaskDList() {
        return taskDList;
    }

    public void setTaskDList(List<TaskD> taskDList) {
        this.taskDList = taskDList;
    }

}

public class Backup {
    private final Context context;
    private  File dir;
    private static final String ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPTION_KEY = "8]6eUcL'b4}xhqC~";

    public static List<String> updatingTask = new ArrayList<>();

    public Backup(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        PackageInfo pInfo;
        String appName = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = pInfo.packageName+"."+BuildConfig.BUILD_TYPE+"/";
        } catch (PackageManager.NameNotFoundException e) {
            if(Global.IS_DEV)
                e.printStackTrace();
        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MssLocal/" + appName + GlobalData.getSharedGlobalData().getUser().getLogin_id();
//        String filePath = context.getExternalFilesDir(null) + "/MssLocal/" + appName + GlobalData.getSharedGlobalData().getUser().getLogin_id();
//        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/MssLocal/" + appName + GlobalData.getSharedGlobalData().getUser().getUuid_user();

        this.dir = new File(filePath);
        // Ensure the directory exists
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Debugging output to check the directory
        Log.d("Backup", "External storage state: " + Environment.getExternalStorageState());
        Log.d("Backup", "Directory path: " + dir.getAbsolutePath());
        Log.d("Backup", "Directory exists: " + dir.exists());
        Log.d("Backup", "Directory readable: " + dir.canRead());
        Log.d("Backup", "Directory writable: " + dir.canWrite());
        Log.d("Backup", "Available space: " + dir.getFreeSpace());
    }

    private void showNotifBackup(String aggrNo, String content) {
        String title;
        if(!aggrNo.isEmpty()) {
            title = "Task Backup - " + aggrNo;
        } else {
            title = "Task Backup";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "backup_channel");
        builder.setSmallIcon(getNotificationIcon());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setPriority(NORM_PRIORITY);
        NotificationCompat.BigTextStyle inboxStyle =
                new NotificationCompat.BigTextStyle();
        // Sets a title for the Inbox in expanded layoutInflater
        inboxStyle.setBigContentTitle(title);
        inboxStyle.bigText(content);
        inboxStyle.setSummaryText(context.getString(com.adins.mss.base.R.string.click_to_open));


        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(inboxStyle);
        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "backup_channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    title,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mNotificationManager.notify(1, builder.build());
    }

    public byte[] encrypt(String backupData) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] ciphertextBytes = backupData.getBytes("UTF-8");
            return cipher.doFinal(ciphertextBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Error err) {
            err.printStackTrace();
            return null;
        }
    }

    public String decrypt(InputStream encryptedStream) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            CipherInputStream cipherInputStream = new CipherInputStream(encryptedStream, cipher);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return new String(output.toByteArray(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Error err) {
            err.printStackTrace();
            return null;
        }
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException {
        byte[] keyBytes = ENCRYPTION_KEY.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
        return secretKeySpec;
    }

    public BackupFormat getCurrentLocal(String uuidTaskH) {
        if (dir.exists()) {
            try {
                File backupData = new File(dir, uuidTaskH);
                FileInputStream inputStream = new FileInputStream(backupData);

                String jsonBackup = decrypt(inputStream);

                if (jsonBackup != null) {
                    Type type = new TypeToken<BackupFormat>() {}.getType();
                    return new Gson().fromJson(jsonBackup, type);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } catch (Error err){
                err.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public void removeTask(List<TaskH> taskHList) {
        if(null != taskHList && taskHList.size() > 0) {
            new RemoveTask(taskHList).execute();
        }
    }

    private int removeBackup(List<TaskH> taskHList, int attempt) {
        int deletedCount = 0;

        if (taskHList.size() > 0 && attempt <= 10) {
            List<TaskH> tempTaskH = new ArrayList<>(taskHList);

            for (TaskH taskH : tempTaskH) {
                File file = new File(dir, taskH.getTask_id());
                if (!updatingTask.contains(taskH.getUuid_task_h())) {
                    if (file.exists()) {
                        if (file.delete()) {
                            taskHList.remove(taskH);
                            deletedCount++;
                        } else{
                            taskHList.remove(taskH);
                        }
                    } else {
                        taskHList.remove(taskH);
                    }
                }
            }

            if (taskHList.size() > 0) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                deletedCount += removeBackup(taskHList, attempt + 1);
            }
        }

        return deletedCount;
    }

    private class RemoveTask extends AsyncTask<Void, Void, Integer> {
        List<TaskH> taskHList;

        private RemoveTask(List<TaskH> taskHList) {
            this.taskHList = taskHList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (taskHList.size() == 1) {
                showNotifBackup(taskHList.get(0).getTask_id(), context.getString(R.string.delete_backup_progress));
            } else if (taskHList.size() > 1) {
                showNotifBackup("", context.getString(R.string.delete_backup_progress));
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                return removeBackup(new ArrayList<>(taskHList), 1);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if(integer > 0) {
                if(taskHList.size() == 1) {
                    showNotifBackup(taskHList.get(0).getTask_id(), context.getString(R.string.delete_backup_success));
                } else if(taskHList.size() > 1) {
                    if(taskHList.size() == integer) {
                        showNotifBackup("", context.getString(R.string.delete_backup_success));
                    } else {
                        showNotifBackup("", context.getString(R.string.delete_backup_half_failed));
                    }
                }
            } else {
                if(taskHList.size() == 1) {
                    showNotifBackup(taskHList.get(0).getTask_id(), context.getString(R.string.delete_backup_failed));
                } else if(taskHList.size() > 1) {
                    showNotifBackup("", context.getString(R.string.delete_backup_failed));
                }
            }
        }

    }

    public void performBackup(TaskH taskH) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            //called from non main thread
            if (null != taskH) {
                performBackups(taskH);
            }
        } else {
            //called from main thread
            if (null != taskH) {
                new PerformBackupTask(taskH).execute();
            }
        }
    }

    private void performBackups(TaskH taskH) {
        updatingTask.add(taskH.getUuid_task_h());
        showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_progress));

        BackupFormat backupFormat = new BackupFormat();

        boolean isSuccess = false;

        try {
            List<TaskD> taskDList = TaskDDataAccess.getListByTaskH(context, taskH.getUuid_task_h());

            backupFormat.setTaskH(taskH);
            backupFormat.setTaskDList(taskDList);

            Gson gsonBackupFormat = new Gson();
            byte[] encryptedJson = encrypt(gsonBackupFormat.toJson(backupFormat));

            FileOutputStream outputStream = null;

            try {
                if(!dir.exists()) {
                    dir.mkdirs();
                }

                long requiredSpace = encryptedJson.length;
                long availableSpace = dir.getFreeSpace();

                if (requiredSpace < availableSpace) {
                    File file = new File(dir, taskH.getTask_id());
                    outputStream = new FileOutputStream(file);
                    outputStream.write(encryptedJson);
                    outputStream.close();

                    isSuccess = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                assert outputStream != null;
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }

        if(isSuccess) {
            showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_success));
        } else {
            showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_failed));
        }

        updatingTask.remove(taskH.getUuid_task_h());
    }

    private class PerformBackupTask extends AsyncTask<Void, Void, Boolean> {
        BackupFormat backupFormat;
        ProgressDialog progressDialog;
        TaskH taskH;

        private PerformBackupTask(TaskH taskH) {
            this.taskH = taskH;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                progressDialog = ProgressDialog.show(context,
                        "", context.getString(R.string.progressWait), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            updatingTask.add(taskH.getUuid_task_h());
            showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_progress));

            backupFormat = new BackupFormat();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if(!dir.exists()) {
                dir.mkdirs();
            }

            backupFormat = new BackupFormat();
            try {

                List<TaskD> taskDList = TaskDDataAccess.getListByTaskH(context, taskH.getUuid_task_h());

                backupFormat.setTaskH(taskH);
                backupFormat.setTaskDList(taskDList);

                Gson gsonBackupFormat = new Gson();
                byte[] encryptedJson = encrypt(gsonBackupFormat.toJson(backupFormat));

                FileOutputStream outputStream = null;

                try {
                    long requiredSpace = encryptedJson.length;
                    long availableSpace = dir.getFreeSpace();

                    if (requiredSpace > availableSpace) {
                        return false;
                    }

                    File file = new File(dir, taskH.getTask_id());
                    outputStream = new FileOutputStream(file);
                    outputStream.write(encryptedJson);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    assert outputStream != null;
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (null != progressDialog && progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            updatingTask.remove(taskH.getUuid_task_h());
            if(aBoolean) {
                showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_success));
            } else {
                showNotifBackup(taskH.getTask_id(), context.getString(R.string.update_backup_failed));
            }
        }
    }

    public void performRestore() {
        RetrieveBackupTask retrieveBackup = new RetrieveBackupTask();
        retrieveBackup.execute();
    }

    private class RetrieveBackupTask extends AsyncTask<Void, Void, Integer> {
        ProgressDialog progressDialog;
        BackupFormat backupFormat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "", context.getString(R.string.progressWait), true);
            showNotifBackup("", context.getString(R.string.restore_backup_progress));
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String uuidTaskHList[] = dir.list();

            int restoredTask = 0;

            if(null != uuidTaskHList && uuidTaskHList.length > 0) {
                for (int i = 0; i < uuidTaskHList.length; i++) {
                    backupFormat = getCurrentLocal(uuidTaskHList[i]);

                    if (backupFormat == null) {
                        continue;
                    }

                    try {
                        TaskHDataAccess.addOrReplace(context, backupFormat.getTaskH());
                        TaskDDataAccess.addOrReplace(context, backupFormat.getTaskDList());

                        restoredTask++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return restoredTask;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (null != progressDialog && progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(integer == 0) {
                showNotifBackup("", context.getString(R.string.restore_backup_failed));
            } else {
                showNotifBackup("", context.getString(R.string.restore_backup_success) + integer);
            }
        }

    }

}