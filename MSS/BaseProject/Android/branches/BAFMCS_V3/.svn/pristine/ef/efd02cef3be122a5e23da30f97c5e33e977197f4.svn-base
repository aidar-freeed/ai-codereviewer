package com.adins.mss.base.syncfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileDataFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Created by loise on 10/13/2017.
 */

/**
 * class contining data to update dialog interface data
 */
class TaskProgress {
    final int percentage;
    final String message;
    final int max;

    TaskProgress(int percentage, String message, int max) {
        this.percentage = percentage;
        this.message = message;
        this.max = max;
    }
}


/**
 * Async task to download files
 */
public class FileDownloader extends AsyncTask<DownloadParams, TaskProgress, String> {

    /**
     * Background Async Task to download file
     */
    ProgressDialog dialog;
    // Progress Dialog
    private ProgressDialog pDialog;
    private WeakReference<Context> context;

    public FileDownloader(Context mContext) {
        context = new WeakReference<>(mContext);
    }

    /**
     * method untuk mendapatkan link bila url menggunakan shortener
     *
     * @param link link awal
     * @return link hasil redirect
     */
    private HttpURLConnection getRedirectedConnection(String link) {
        URL url = null;
        URL secondURL = null;
        HttpURLConnection connect = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(link);
            connect = (HttpURLConnection) url.openConnection();
            connect.setConnectTimeout(Global.DEFAULTCONNECTIONTIMEOUT);
            connect.setInstanceFollowRedirects(false);
            secondURL = new URL(connect.getHeaderField("Location"));
            connection = (HttpURLConnection) secondURL.openConnection();
            connection.setConnectTimeout(Global.DEFAULTCONNECTIONTIMEOUT);
            return connection;
        } catch (java.net.SocketTimeoutException e) {
            Log.e("Error ", e.getMessage());
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //to check if file sha1 matches
    private Boolean checkIsBadFile(String message, String filepath, String sha1) {
        String info = message + " (File found!)";
        boolean isBadFile = false;
        TaskProgress progress = new TaskProgress((1), info, (1));
        publishProgress(progress);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        String savedSha1, newSha1;
        try {
            info = message + " (Checking file integrity...)";
            progress = new TaskProgress((1), info, (1));
            publishProgress(progress);
            File storedFile = new File(filepath);
            savedSha1 = FileSigner.getSha1String(storedFile);
            newSha1 = sha1;
            if (savedSha1.equals(newSha1)) {
                isBadFile = false;
                info = message + " (Success, File signature match!)";
                progress = new TaskProgress((1), info, (1));
                publishProgress(progress);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } else {
                isBadFile = true;
                boolean deleteResult = storedFile.delete();
                if(!deleteResult){
                    Toast.makeText(context.get(), "Failed to delete stored file", Toast.LENGTH_SHORT).show();
                }
                info = message + " (File signature mismatch, deleting file...)";
                progress = new TaskProgress((1), info, (1));
                publishProgress(progress);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isBadFile;
    }

    /**
     * untuk menampilkan progress dialog
     *
     * @param context
     * @return
     */
    protected ProgressDialog showDownloadDialog(Context context) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.downloading_file));
        pDialog.setIndeterminate(false);
        pDialog.setMax(1);
        pDialog.setProgressNumberFormat("%1d KB/%2d KB");
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
        dialog = showDownloadDialog(FileSyncHelper.instance);
        dialog.show();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(DownloadParams... params) {
        //inisialisasi data
        int count;
        String link = "";
        String message = params[0].message;
        String info;
        String fileName = "";
        TaskProgress progress;
        //update progress
        Calendar cal = Calendar.getInstance();
        info = message + "  (Checking storage...)";
        progress = new TaskProgress(0, info, 1);
        publishProgress(progress);
        MobileDataFile savedMetadata;
        MobileDataFile newMetadata;
        newMetadata = params[0].getMetadata();
        newMetadata.setDtm_crt(newMetadata.getDtm_crt());
        newMetadata.setDtm_upd(newMetadata.getDtm_upd());
        //wait supaya bisa dibaca messagenya
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //cek apakah file ada di dalam storage
        boolean updated = false, fileExists = false, needDownloading = true;
        savedMetadata = MobileDataFileDataAccess.getOne(FileSyncHelper.instance, params[0].getMetadata().getId_datafile());
        if (savedMetadata != null) {
            //cek apakah ada update pada data dari server
            if (!savedMetadata.getIs_active().equals(newMetadata.getIs_active()) || !savedMetadata.getHash_sha1().equals(newMetadata.getHash_sha1())) {
                updated = true;
            }
            //cek apakah file didelete oleh user tetapi path tersimpan di db mobile
            if (savedMetadata.getDownloaded_file_path() != null && !savedMetadata.getDownloaded_file_path().isEmpty()) {
                fileExists = new File(savedMetadata.getDownloaded_file_path()).exists();
            }
        }
        //cek apabila file ada dan tidak ada perubahan dari server.
        if (fileExists && !updated) {
            //bila file sudah diimport maka skip file, jika belum diimport, cek integritas dan bila gagal download ulang
            if (savedMetadata.getImport_flag() == null || savedMetadata.getImport_flag() == true)
                needDownloading = checkIsBadFile(message, savedMetadata.getDownloaded_file_path(), newMetadata.getHash_sha1());
            else needDownloading = false;
        }
        //cek bila file sudah inactive, bila inactive maka file didelete dari storage
        else if (fileExists && params[0].getMetadata().getIs_active().equals("0")) {
            info = message + " (File inactive, deleting...)";
            progress = new TaskProgress((1), info, (1));
            boolean deleteResult = new File(savedMetadata.getDownloaded_file_path()).delete();
            if(!deleteResult){
                Toast.makeText(context.get(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
            publishProgress(progress);
            newMetadata.setDownloaded_file_path(null);
            newMetadata.setImport_flag(false);
            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
            needDownloading = false;
        }
        //skip download bila file inactive
        else if (!fileExists && params[0].getMetadata().getIs_active().equals("0")) {
            info = message + " (File inactive, skipping file...)";
            progress = new TaskProgress((1), info, (1));
            publishProgress(progress);
            newMetadata.setDownloaded_file_path(null);
            newMetadata.setImport_flag(false);
            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
            needDownloading = false;
        }
        //proses download file
        if (needDownloading) {
            OutputStream output = null;
            try {
                //connect to url
                info = message + "  (Connecting...)";
                progress = new TaskProgress(0, info, 1);
                publishProgress(progress);
                HttpURLConnection connection;
                connection = getRedirectedConnection(newMetadata.getFile_url());
                if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    link = connection.getURL().toString();
                    String fieldValue = connection.getHeaderField("Content-Disposition");
                    fieldValue = fieldValue.replace(" ", "");
                    if (fieldValue == null || ! fieldValue.contains("filename=\"")) {
                        fileName = link.substring(link.lastIndexOf("/"), link.length());
                    }else{
                        fileName = fieldValue.substring(fieldValue.indexOf("filename=\"") + 10, fieldValue.length() - 1);
                        if(fileName.contains(";")){
                            fileName = fileName.substring(0, fileName.indexOf(";")-1);
                        }
                    }
                    connection.connect();
                    // getting file length
                    int lengthOfFile = connection.getContentLength();

                    // input stream to read file - with 8k buffer
                    try(BufferedInputStream input = new BufferedInputStream(connection.getURL().openStream(), 8192)){
                        //check and create dir
                        File outputDir = new File(params[0].outputfilepath);
                        if (!outputDir.exists()) outputDir.mkdirs();
                        info = message + "  (Connected!)";
                        progress = new TaskProgress(0, info, 1);
                        publishProgress(progress);
                        // Output stream to write file
                        File savedFile = new File(params[0].outputfilepath + fileName);
                        Boolean signatureMatches = false;
                        //cek jika file sudah ada tetapi tidak ada entry di database
                        if (savedFile.exists()) {
                            //bila file inactive delete file
                            if (params[0].getMetadata().getIs_active().equals("0")) {
                                boolean deleteResult = savedFile.delete();
                                if(!deleteResult){
                                    Toast.makeText(context.get(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                                }
                                info = message + " (File inactive, deleting...)";
                                progress = new TaskProgress(1, info, 1);
                                publishProgress(progress);
                                newMetadata.setDownloaded_file_path(null);
                                newMetadata.setImport_flag(false);
                                MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                                needDownloading = false;
                            } else {
                                //bila file active cek integritas data
                                signatureMatches = !checkIsBadFile(message, savedFile.getPath(), newMetadata.getHash_sha1());
                                if (signatureMatches) {
                                    //bila data tidak rusak beri flag untul diimport ke database
                                    newMetadata.setDownloaded_file_path(savedFile.getPath());
                                    newMetadata.setImport_flag(true);
                                    MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                                    needDownloading = false;
                                } else {
                                    //bila data rusak maka coba download ulang
                                    newMetadata.setDownloaded_file_path(null);
                                    newMetadata.setImport_flag(false);
                                    MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                                    needDownloading = true;
                                }

                            }
                        }
                        //download file
                        if (needDownloading && params[0].getMetadata().getIs_active().equals("1")) {
                            String downloadPath = params[0].outputfilepath + fileName;
                            output = new FileOutputStream(downloadPath);
                            byte data[] = new byte[1024];
                            long total = 0;
                            info = message + "  (Downloading...)";
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                progress = new TaskProgress((int) (total / 1024), info, (lengthOfFile / 1024));
                                publishProgress(progress);
                                // writing data to file
                                output.write(data, 0, count);
                            }
                            // flushing output
                            output.flush();

                            boolean badFile;
                            info = message + " (Download Success!)";
                            progress = new TaskProgress((lengthOfFile / 1024), info, (lengthOfFile / 1024));
                            publishProgress(progress);
                            newMetadata.setDownloaded_file_path(params[0].outputfilepath + fileName);
                            newMetadata.setImport_flag(true);
                            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                            //cek integritas data yang didownload
                            badFile = checkIsBadFile(message, downloadPath, newMetadata.getHash_sha1());
                            if (badFile) {
                                //jika file berubah SHA1 nya maka file didelete dan user mendapatkan notifikasi
                                info = "file with id #" + params[0].getMetadata().getId_datafile() + " is corrupted, please contact your administrator";
                                progress = new TaskProgress((1), info, (1));
                                publishProgress(progress);
                                Thread.sleep(1000);
                                newMetadata.setDownloaded_file_path(null);
                                newMetadata.setImport_flag(false);
                                MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                            }
                        }
                        //jika file ditemukan dan belum di import
                        else if (!needDownloading && params[0].getMetadata().getIs_active().equals("1")) {
                            info = message + " (Success, file already downloaded.)";
                            progress = new TaskProgress(1, info, 1);
                            publishProgress(progress);
                            newMetadata.setDownloaded_file_path(savedFile.getPath());
                            newMetadata.setImport_flag(true);
                            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                        }
                        //jika file inactive maka proses download di skip
                        else {
                            info = message + " (Success, skipped download, file not needed.)";
                            progress = new TaskProgress(1, info, 1);
                            publishProgress(progress);
                            newMetadata.setDownloaded_file_path(null);
                            newMetadata.setImport_flag(false);
                            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                info = message + " (ERROR : File not found! Please contact administrator)";
                progress = new TaskProgress(0, info, 1);
                publishProgress(progress);
                Log.e("Error: ", e.getMessage());
            } catch (MalformedURLException e) {
                info = message + " (ERROR : Bad Url!)";
                progress = new TaskProgress(0, info, 1);
                publishProgress(progress);
                Log.e("Error: ", e.getMessage());
            } catch (IOException e) {
                info = message + " (ERROR : Failed to save file!)";
                progress = new TaskProgress(0, info, 1);
                publishProgress(progress);
                Log.e("Error: ", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            finally {
                try {
                    // closing streams
                    if(output != null)
                        output.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        } else if (!fileExists && params[0].getMetadata().getIs_active().equals("0")) {
            info = message + " (File inactive, skipping file...)";
            progress = new TaskProgress((1), info, (1));
            publishProgress(progress);
            newMetadata.setDownloaded_file_path(null);
            newMetadata.setImport_flag(false);
            MobileDataFileDataAccess.addOrReplace(FileSyncHelper.instance, newMetadata);
        } else {
            info = message + " (Success, file already downloaded.)";
            progress = new TaskProgress(1, info, 1);
            publishProgress(progress);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Updating progress bar
     */
    @Override
    protected void onProgressUpdate(TaskProgress... progress) {
        // setting progress progress
        pDialog.setProgress(progress[0].percentage);
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
        //panggil download kembali bila masih ada file yang butuh di download
        if (FileSyncHelper.currentidx < FileSyncHelper.getData().size() - 1) {
            if(FileSyncHelper.instance==null){
                    FileSyncHelper.instance = context.get();
                }
            FileSyncHelper.downloadFiles();
        }
        //mulai proses import bila semua file dari server sudah di cek
        else {
            FileSyncHelper.currentidx = -1;
            FileSyncHelper.activeData = MobileDataFileDataAccess.getAllActive(FileSyncHelper.instance);
            if (!FileSyncHelper.activeData.isEmpty()) {
                if(FileSyncHelper.instance == null)
                    FileSyncHelper.instance = context.get();
                FileSyncHelper.importFiles();
            }
            //bila tidak ada file yang butuh diimport maka proses import di skip
            else {
                if (FileSyncHelper.instance == null) {
                    FileSyncHelper.instance = context.get();
                }
                Toast.makeText(FileSyncHelper.instance, FileSyncHelper.instance.getResources().getString(R.string.database_is_uptodate), Toast.LENGTH_SHORT).show();
                FileSyncHelper.synchronizeCallback();
            }
        }
    }

}


