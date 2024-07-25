package com.adins.mss.base.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.R;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadUpdate extends AsyncTask<String,String,String> {
    private ProgressDialog pDialog;
    private Context context;
    public DownloadUpdate(Context context ){
        this.context = context;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.updating_apk));
        pDialog.setIndeterminate(false);
        pDialog.setMax(0);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setProgressNumberFormat("%1d KB/%2d KB");
        pDialog.setCancelable(false);
        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(true);
            }
        });
        pDialog.show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        File file = new File(context.getFilesDir(), "app.apk");
        if(file.exists()) {
            boolean deleteResult = file.delete();
            if(!deleteResult){
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        InputStream input = null;

        File directory = new File(context.getFilesDir(), "app.apk");
        publishProgress("0","0",context.getString(R.string.updating_apk));
        try(OutputStream output = new FileOutputStream(directory)) {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            connection.setConnectTimeout(Global.DOWNLOADUPDATECONNECTIONTIMEOUT);
            // getting file length
            int lengthOfFile = connection.getContentLength();
            input = new BufferedInputStream(url.openStream(), 8192);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(total / 1024), ""+lengthOfFile/1024,context.getString(R.string.updating_apk));

                output.write(data, 0, count);
            }

            output.flush();

            return null;
        } catch (FileNotFoundException e) {
            Log.e("Error: ", e.getMessage());
            return "File not found! Please contact administrator";
        } catch (MalformedURLException e) {
            Log.e("Error: ", e.getMessage());
            return "Bad Url!";
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return context.getString(R.string.update_failed);
        } finally {
            try {
                if(input != null)
                    input.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Updating progress bar
     */
    @Override
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
        pDialog.setMax(Integer.parseInt(progress[1]));
        pDialog.setMessage(progress[2]);
    }


    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();

        File file = new File(context.getFilesDir(), "app.apk");
        if(message==null && isAPK(file)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkURI = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else{
            if(message==null){
                message = context.getString(R.string.update_failed);
            }
            if(file.exists()) {
                boolean deleteResult = file.delete();
                if(!deleteResult){
                    Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            }
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle(context.getString(R.string.btnExit))
                    .withMessage(message)
                    .withButton1Text("OK")
                    .setButton1Click(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(true)
                    .show();
        }
    }
    public static boolean isAPK(File file) {

        ZipEntry zEntry = null;
        String dexFile = "classes.dex";
        String manifestFile = "AndroidManifest.xml";
        boolean hasDex = false;
        boolean hasManifest = false;

        try(FileInputStream fis =  new FileInputStream(file);
            ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(fis))) {

            while ((zEntry = zipIs.getNextEntry()) != null) {
                if (zEntry.getName().equalsIgnoreCase(dexFile)) {
                    hasDex = true;
                } else if (zEntry.getName().equalsIgnoreCase(manifestFile)) {
                    hasManifest = true;
                }
                if (hasDex && hasManifest) {
                    return true;
                }
            }

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
