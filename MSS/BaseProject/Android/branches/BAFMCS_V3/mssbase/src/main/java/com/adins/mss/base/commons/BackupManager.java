package com.adins.mss.base.commons;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.formatter.Formatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import de.greenrobot.dao.database.Database;

public class BackupManager {
    public static String ACTION_BACKUP_TASK = "com.adins.mss.ACTION_BACKUP_TASK";
    private Context context;

    public BackupManager(Context context) {
        this.context = context;
    }

    private void copyAsset(File destination) {

        try (InputStream is = this.context.getAssets().open("master");
             OutputStream os = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backup(String entity, String params) {
        // Initialize Firebase Instance
        Database db = DaoOpenHelper.getDb(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("backup");

        String loginid = (GlobalData.getSharedGlobalData() != null && GlobalData.getSharedGlobalData().getUser() != null)
                ? GlobalData.getSharedGlobalData().getUser().getLogin_id() : Build.ID;
        String nowaday = Formatter.formatDate(new Date(), Global.DATE_STR_FORMAT_GSON);

        StorageReference backup;
        File extdb  = this.copy(entity);
        String constraint = "WHERE 1";
        db.execSQL("ATTACH '" + extdb.getPath() + "' AS EXT");

        switch (entity) {
            case "tasks":
                db.execSQL("INSERT INTO EXT.MS_SCHEME SELECT * FROM MS_SCHEME");
                db.execSQL("INSERT INTO EXT.MS_QUESTIONSET SELECT * FROM MS_QUESTIONSET");
                db.execSQL("INSERT INTO EXT.TR_TASK_H SELECT * FROM TR_TASK_H");
                db.execSQL("INSERT INTO EXT.TR_TASK_D SELECT * FROM TR_TASK_D");
                break;
            case "lov":
                String query = "INSERT INTO EXT.MS_LOOKUP SELECT * FROM MS_LOOKUP ";
                query = query.concat(constraint);
                if (params != null && !params.equals("")) {
                    query = query.concat(" AND ").concat(params);
                }
                db.execSQL(query);
                break;
            case "deposit":
                db.execSQL("INSERT INTO EXT.TR_DEPOSITREPORT_D SELECT * FROM TR_DEPOSITREPORT_D");
                db.execSQL("INSERT INTO EXT.TR_DEPOSITREPORT_H SELECT * FROM TR_DEPOSITREPORT_H");
                db.execSQL("INSERT INTO EXT.TR_TASK_H SELECT * FROM TR_TASK_H");
                db.execSQL("INSERT INTO EXT.TR_TASK_D SELECT * FROM TR_TASK_D");
                break;
            default:
                break;
        }

        db.execSQL("DETACH 'EXT'");
        Uri uri= Uri.fromFile(extdb);
        backup = reference.child(loginid + "_" + nowaday + "_" + uri.getLastPathSegment());
        this.upload(uri, backup);
    }

    public void backup(String action, TaskH taskH) {
        // Initialize Firebase Instance
        Database db = DaoOpenHelper.getDb(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("backup");

        String serial  = Build.SERIAL;
        String loginid = (GlobalData.getSharedGlobalData() != null && GlobalData.getSharedGlobalData().getUser() != null)
                ? GlobalData.getSharedGlobalData().getUser().getLogin_id() : serial;
        String nowaday = Formatter.formatDate(new Date(), Global.DATE_STR_FORMAT_GSON);

        File extdb;
        StorageReference backup;
        if (action.equalsIgnoreCase(ACTION_BACKUP_TASK)) {
            extdb  = this.copy("tasks");

            db.execSQL("ATTACH '" + extdb.getPath() + "' AS EXT");
            db.execSQL("INSERT INTO EXT.MS_SCHEME SELECT * FROM MS_SCHEME");
            db.execSQL("INSERT INTO EXT.MS_QUESTIONSET SELECT * FROM MS_QUESTIONSET");
            db.execSQL("INSERT INTO EXT.TR_TASK_H SELECT * FROM TR_TASK_H");
            db.execSQL("INSERT INTO EXT.TR_TASK_D SELECT * FROM TR_TASK_D");
            db.execSQL("DETACH 'EXT'");

            Uri uri= Uri.fromFile(extdb);
            backup = reference.child(loginid + "_" + nowaday + "_" + uri.getLastPathSegment() + "_" + taskH.getCustomer_name().replaceAll(" ", "_"));
            this.upload(uri, backup);
        } else {
            this.copy("master");
        }

        // Check Internal Storage and Report it
        Utility.checkInternalStorage(context);
    }

    private void upload(Uri dataUri, final StorageReference reference) {
        UploadTask uploadTask = reference.putFile(dataUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("BackupManager","Total Transferred: " + taskSnapshot.getBytesTransferred());
            }
        });

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("BackupManager","Download link: " + reference.getDownloadUrl().toString());
                }
            }
        });

    }

    private File copy(String filename) {
        File extdb = context.getDatabasePath(filename);

        if (extdb.exists()) {
            boolean result = extdb.delete();
            if(!result){
                Toast.makeText(context, "Failed to delete directory", Toast.LENGTH_SHORT).show();
            }
        }

        this.copyAsset(extdb);
        return extdb;
    }
}
