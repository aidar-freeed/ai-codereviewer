package com.adins.mss.base.syncfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.MobileDataFile;
import com.adins.mss.dao.Sync;
import com.adins.mss.foundation.db.DaoOpenHelper;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.SyncDataAccess;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.dao.database.Database;

/**
 * Created by loise on 10/9/2017.
 */

/**
 * class contining data to update dialog interface data
 */
class ImportDbTaskProgress {
    final int progress;
    final String message;
    final int max;

    /**
     * constructor for data container class
     *
     * @param progress current progress count
     * @param message  message string to display
     * @param max      maximum progress count
     */
    ImportDbTaskProgress(int progress, String message, int max) {
        this.progress = progress;
        this.message = message;
        this.max = max;
    }
}

/**
 * Contains methods for importing MS_LOOKUP table from external file
 */
public class ImportDbFromCsv extends AsyncTask<ImportDbParams, ImportDbTaskProgress, String> {

    private WeakReference<Context> mContext;

    public ImportDbFromCsv() {
    }

    public ImportDbFromCsv(Context context) {
        mContext = new WeakReference<>(context);
    }

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    /**
     * Variable to track current row number when reading from BufferedReader
     */
    public static int rownum = 0;
    /**
     * Background Async Task to download file
     */
    ProgressDialog dialog;
    ImportDbTaskProgress progress;
    // Progress Dialog
    private ProgressDialog pDialog;

    /**
     * Method to measure performance of importing a table from a separate database file
     *
     * @param context current activity context
     * @return total execution time of this method in milliseconds
     */
    public static Long getImportTime(Context context) {
        Long start;
        String path = "";
        start = System.currentTimeMillis();
        try {
            path = context.getExternalFilesDir(null).toString();
            path = path + "/testdb";
            Database db = DaoOpenHelper.getDb(context);
            db.execSQL("ATTACH '" + path + "' AS EXTDB");
            db.execSQL("INSERT OR REPLACE INTO MS_LOOKUP select * from EXTDB.MS_LOOKUP");
            db.execSQL("DETACH 'EXTDB'");
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            Log.e("unknown", path, e);
        }
        return System.currentTimeMillis() - start;
    }

    /**
     * method to get public decryption key from assets
     * <p>
     * IMPORTANT : jangan lupa taruh file public.der yang berisi public key pada folder assets
     *
     * @param context activity context
     * @return
     */
    private String getPublicKey(Context context) {
        File f = new File(context.getCacheDir() + "/public.der");
        if (!f.exists())
            try (FileOutputStream fos = new FileOutputStream(f);
                 InputStream is = context.getAssets().open("public.der")){
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                fos.write(buffer);
            } catch (Exception e) {
                FireCrash.log(e);
                throw new RuntimeException(e);
            }
        return f.getAbsolutePath();
    }

    /**
     * method to show progress dialog
     *
     * @param context
     * @return
     */
    protected ProgressDialog showImportDbDialog(Context context) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Processing file...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(1);
        pDialog.setProgressNumberFormat("%1d/%2d Rows");
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        dialog = showImportDbDialog(mContext.get());
        dialog.show();
    }

    @Override
    protected String doInBackground(ImportDbParams... params) {
        //ambil path menuju public key untuk decryption
        String keyPath = getPublicKey(mContext.get());
        String info;
        //ambil message untuk ditampilkan
        String message = params[0].getMessage();
        //tampung objek mobiledatafile yang akan diproses
        MobileDataFile metadata = params[0].getMetadata();
        //ambil lokasi file yang akan di decrypt, unzip dan insert
        File in = new File(params[0].getMetadata().getDownloaded_file_path());
        //ambil nama file tanpa extension
        String fileNameWithOutExt = FilenameUtils.removeExtension(in.getName());
        //file output zip
        File out = new File(GlobalData.getSharedGlobalData().getSavePath() + fileNameWithOutExt + ".zip");
        String outputzip = GlobalData.getSharedGlobalData().getSavePath();
        //file output csv
        String outputcsv = GlobalData.getSharedGlobalData().getSavePath() + fileNameWithOutExt + ".csv";
        int result = -99;
        //update dialog message
        info = message + "  (Decrypting file...)";
        progress = new ImportDbTaskProgress(0, info, 1);
        publishProgress(progress);
        //decrypt file yang sudah di download
        try {
            FileEncryption fe = new FileEncryption();
            fe.publicKey = keyPath;
            fe.decrypt(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        //unzip file
        info = message + "  (Unzipping file...)";
        progress = new ImportDbTaskProgress(0, info, 1);
        publishProgress(progress);
        ArchiveManager am = new ArchiveManager();
        try{
            am.extract(out.getPath(), outputzip);
            //delete zip file
            boolean deleteResult = out.delete();
            if(!deleteResult){
                Toast.makeText(mContext.get(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
            //update message dialog
            info = message + "  (Importing file...)";
            progress = new ImportDbTaskProgress(0, info, 1);
            publishProgress(progress);
            //import file to ms_lookup

        /*
        NOTE : untuk support lebih dari satu table, ambil nama table dari filename dan buat
        switch case didalam proses berikut, lalu buat method terpisah untuk mengimport csv ke table lain
         */
            try {
                result = importLookup(mContext.get(), outputcsv, message);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            Log.i("Result Code : ", String.valueOf(result));
            if (result == 1) {
                //beri flag agar dapat diketahui bila file dudah diinsert dan tidak diinsert ulang
                metadata.setImport_flag(false);
                MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, metadata);
                deleteResult = new File(outputcsv).delete();
                if(!deleteResult){
                    Toast.makeText(mContext.get(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            //delete zip file
            boolean deleteResult = out.delete();
            if(!deleteResult){
                Toast.makeText(mContext.get(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
            return e.getMessage();
        }

        return null;
    }

    /**
     * Updating progress bar
     */
    @Override
    protected void onProgressUpdate(ImportDbTaskProgress... progress) {
        // setting progress progress
        pDialog.setProgress(progress[0].progress);
        pDialog.setMessage(progress[0].message);
        pDialog.setMax(progress[0].max);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String string) {
        Message msg;
        Bundle bundle;
        // dismiss the dialog after the file was downloaded
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (string!=null){
            Toast.makeText(FileSyncHelper.instance, FileSyncHelper.instance.getResources().getString(R.string.import_from_csv_failed), Toast.LENGTH_SHORT).show();
        }
        //cek apakah masih ada file yang harus diinsert, bila ya maka panggil ulang task ini
        if (FileSyncHelper.currentidx < FileSyncHelper.activeData.size() - 1) {
            FileSyncHelper.importFiles();
        } else {
            Toast.makeText(FileSyncHelper.instance, FileSyncHelper.instance.getResources().getString(R.string.database_has_been_updated), Toast.LENGTH_SHORT).show();
            FileSyncHelper.synchronizeCallback();
        }

    }

    /**
     * Method for importing MS_LOOKUP table from a specified CSV file in internal storage and updating MS_SYNC table CSV File format : Line 1 Separator, Line 2 Table Name [separator] total row count, line 3+ data
     *
     * @param context  current activity context
     * @param filePath file path relative to internal storage root
     * @return
     */
    public Integer importLookup(Context context, String filePath, String message) throws InterruptedException {
        rownum = 0;
        ImportDbTaskProgress progress;
        Database db = DaoOpenHelper.getDb(context);
        //query berikut bertujuan untuk mengenerate data untuk
        //diinsert ke table MS_SYNC dengan dtm_upd yang sesuai dengan keadaan di table MS_LOOKUP
        String queryUpdateSync = "select b.UUID_SYNC ,TABEL_NAME as TABEL_NAME,a.LOV_GROUP, \n" +
                "       max(IFNULL(a.dtm_upd, a.dtm_crt)) as DTM_UPD, \n" +
                "       FLAG as FLAG \n" +
                "       from MS_LOOKUP a \n" +
                "       LEFT join MS_SYNC b on a.LOV_GROUP = b.LOV_GROUP group by a.LOV_GROUP";
        String path = filePath;
        FileReader file;
        //membaca file dari path
        try {
            file = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileNotFoundException", path, e);
            return -1;
        }
        //inisialisasi variable

        String line = "";
        List<Lookup> transaction = new ArrayList<>();
        String info;
        String totalRows = "1", tableName;
        try(BufferedReader buffer = new BufferedReader(file)){
            //mengambil separator pada line pertama di csv
            final String separator1 = buffer.readLine();
            //mengambil separator pada line kedua di csv
            final String separator2 = buffer.readLine();
            //mengambil data nama tabel dan jumlah record pada line ketiga di csv
            if(separator2.equalsIgnoreCase(""))
                line=buffer.readLine();
            else
                line = separator2;
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
            //mengambil metadata file pada row kedua di csv (bisa dimodifikasi sesuai kebutuhan
            //data apa saja yang dibutuhkan
            String[] str = line.split(separator1);
            StringBuilder gabung = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                gabung.append(str[i]);
            }
            line = gabung.toString();
            str = line.split("\\|");
            //mengambil nama table diurutan pertama
            tableName = str[0];
            Log.i("Table Name:", tableName);
            //mengambil jumlah row yang akan diinsert dari urutan kedua
            totalRows = str[1];
            Log.i("Total Rows:", totalRows);
            line = buffer.readLine();
            //membaca semua data
            while ((line = buffer.readLine()) != null) {
                if (line.isEmpty()) {
                    Log.i("ImportDbFromCsv","Line Kosong");
                } else {
                    Calendar cal = Calendar.getInstance();
                    Lookup lookup = new Lookup();
                    str = line.split(separator1);
                    gabung = new StringBuilder();
                    for (int i = 0; i < str.length; i++) {
                        gabung.append(str[i]);
                    }
                    line = gabung.toString();
                    str = line.split("\\|");
                    if (!line.equalsIgnoreCase("")) {
                        try {
                            lookup.setUuid_lookup(str[0]);
                            lookup.setOption_id(str[1]);
                            lookup.setCode(str[2]);
                            lookup.setValue(str[3]);
                            lookup.setFilter1(str[4]);
                            lookup.setFilter2(str[5]);
                            lookup.setFilter3(str[6]);
                            lookup.setFilter4(str[7]);
                            lookup.setFilter5(str[8]);
                            if (str[9].isEmpty()) {
                                lookup.setSequence(null);
                            } else {
                                try {
                                    lookup.setSequence(Integer.parseInt(str[9].replace("\"","")));
                                } catch (NumberFormatException e) {
                                    FireCrash.log(e);
                                    e.printStackTrace();
                                    Log.e("NumberFormatException", line, e);
                                    return -2;
                                }
                            }
                            lookup.setUsr_crt(str[10]);
                            if (str[11].isEmpty()) {
                                lookup.setDtm_crt(null);
                            } else {
                                try {
                                    try {
                                        cal.setTime(sdf.parse(str[11]));
                                    } catch (ParseException e) {
                                        cal.setTimeInMillis(System.currentTimeMillis());
                                    }
                                    lookup.setDtm_crt(cal.getTime());
                                } catch (NumberFormatException e) {
                                    FireCrash.log(e);
                                    e.printStackTrace();
                                    Log.e("NumberFormatException", line, e);
                                    return -2;
                                }
                            }
                            lookup.setUsr_crt(str[12]);
                            if (str[13].isEmpty()) {
                                lookup.setDtm_upd(null);
                            } else {
                                try {
                                    try {
                                        cal.setTime(sdf.parse(str[13]));
                                    } catch (ParseException e) {
                                        cal.setTimeInMillis(System.currentTimeMillis());
                                    }
                                    lookup.setDtm_upd(cal.getTime());
                                } catch (NumberFormatException e) {
                                    FireCrash.log(e);
                                    e.printStackTrace();
                                    Log.e("ERROR", line, e);
                                    return -2;
                                }
                            }
                            lookup.setUuid_question_set(str[14]);
                            lookup.setLov_group(str[15]);
                            lookup.setIs_active(str[16]);
                            lookup.setIs_deleted(str[17]);
                            transaction.add(lookup);

                            rownum++;
                        } catch (Exception e){
                            Log.e("Exception", line, e);
                        }
                    }
                }
                //Mengupdate dialog dengan progress membaca line
                if (rownum % 1000 == 0) {
                    info = message + "  (Reading values from file...)";
                    progress = new ImportDbTaskProgress(rownum, info, Integer.parseInt(totalRows));
                    publishProgress(progress);
                }
                //data diinsert dengan ukuran list transaksi 25000 row
                if (transaction.size() >= 25000) {
                    Log.i("Info", "Loop : " + rownum);
                    try {
                        //mengupdate dialog dengan progress sedang insert ke database
                        info = message + "  (Inserting values to database...)";
                        progress = new ImportDbTaskProgress(rownum, info, Integer.parseInt(totalRows));
                        publishProgress(progress);
                        LookupDataAccess.addOrUpdateAll(context, transaction);
                        transaction.clear();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        Log.e("Insert Error", line, e);
                        return -4;
                    }
                }

            }
            //insert sisa row bila lebih kecil dari 25000 row
            if (!transaction.isEmpty()) {
                rownum++;
                Log.i("Info", "Loop : " + rownum);
                info = message + "  (Inserting values to database...)";
                progress = new ImportDbTaskProgress(rownum, info, Integer.parseInt(totalRows));
                publishProgress(progress);
                LookupDataAccess.addOrUpdateAll(context, transaction);
                transaction.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IOException", line, e);
            return -3;
        } finally {
            try {
                //proses update table ms_sync
                Calendar cal2 = Calendar.getInstance();
                Cursor c = db.rawQuery(queryUpdateSync, null);
                if (c.moveToFirst()) {
                    do {
                        cal2.setTimeInMillis(System.currentTimeMillis());
                        Sync s = new Sync();
                        String insertQuery = null;
                        //assigning values
                        s.setUuid_sync(c.getString(0));
                        if (s.getUuid_sync() == null || s.getUuid_sync().isEmpty())
                            s.setUuid_sync(UUID.randomUUID().toString());
                        s.setTabel_name("");
                        s.setLov_group(c.getString(2));
                        String dtmUpd = c.getString(3);
                        if (dtmUpd == null || dtmUpd.isEmpty()) {
                            s.setDtm_upd(null);
                        } else {
                            Date dtm;
                            try {
                                try {
                                    dtm = new Date(Long.parseLong(dtmUpd));
                                } catch (NumberFormatException e) {
                                    FireCrash.log(e);
                                    dtm = new Date(null);
                                }
                                cal2.setTime(dtm);
                                s.setDtm_upd(cal2.getTime());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Log.e("NumberFormatException", s.getDtm_upd().toString(), e);
                            }
                            s.setFlag(null);
                            SyncDataAccess.addOrReplace(context, s);
                        }
                    }
                    while (c.moveToNext());
                }
                c.close();
                //menutup semua objek dan update dialog
                transaction.clear();
                info = message + "  (Import complete...)";
                progress = new ImportDbTaskProgress(rownum, info, Integer.parseInt(totalRows));
                publishProgress(progress);
                Thread.sleep(500);
            }
            catch (SQLiteException e) {
                info = message + "  (Import failed, error updating db...)";
                progress = new ImportDbTaskProgress(rownum, info, Integer.parseInt(totalRows));
                publishProgress(progress);
                Thread.sleep(1500);
                e.printStackTrace();
                Log.e("SQLiteException", "MS_SYNC update failed!", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return 1;
    }


}
