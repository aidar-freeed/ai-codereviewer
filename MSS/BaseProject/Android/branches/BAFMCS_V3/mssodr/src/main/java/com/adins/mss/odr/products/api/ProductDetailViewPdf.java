package com.adins.mss.odr.products.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.db.dataaccess.ProductDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.BuildConfig;
import com.adins.mss.odr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductDetailViewPdf extends AsyncTask<Void, Void, String> {

    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private Product product;
    private String urlFileBrochure;
    private String errMessage;
    public static CountDownTimer timer;

    public ProductDetailViewPdf(FragmentActivity activity, Product product) {
        this.activity = activity;
        this.product = product;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.getting_brochure), true);
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (Tool.isInternetconnected(activity)) {
            ProductDetailViewPdfRequest request = new ProductDetailViewPdfRequest();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
            String dtmUpd = null;
            if(product.getProduct_file()!=null && !"".equalsIgnoreCase(product.getProduct_file())){
                String [] fileName = product.getProduct_file().split("_");
                if(fileName!=null){
                    if(fileName.length>0){
                        dtmUpd = fileName[fileName.length-1];

                        try{
                            dtmUpd = dtmUpd.replace(".pdf", "");
                            Date lastDtmUpd = formatter.parse(dtmUpd);
                            if(lastDtmUpd!=null){
                                request.setDtm_upd(dtmUpd);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }
            request.setUuid_product(product.getUuid_product());
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);
            String url = GlobalData.getSharedGlobalData().getURL_GET_PRODUCT_DETAIL();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    ProductDetailViewPdfResponse response = GsonHelper.fromJson(responseBody, ProductDetailViewPdfResponse.class);

                    if (response.getStatus().getCode() == 0) {
                        List<ProductDetailViewPdfBean> listProductDetail = response.getListDetailProduct();

                        if (listProductDetail != null && !listProductDetail.isEmpty()) {
                            String resultBrochure = listProductDetail.get(0).getProduct_file();
                            Date date = new Date();
                            String dateString = formatter.format(date);
                            GetFilePathAndStatus filepath = getFile(activity.getApplicationContext() ,resultBrochure, "Brosur_" + product.getProduct_name() +"_"+dateString, "pdf");
                            urlFileBrochure = filepath.filePath;
                            product.setProduct_file(urlFileBrochure);
                            ProductDataAccess.addOrReplace(activity, product);
                        } else {
                            errMessage = activity.getString(R.string.no_data_from_server);
                            urlFileBrochure = product.getProduct_file();
                        }
                    }
                } catch (Exception e) {
                    if (Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                    }
                }
            } else {
                errMessage = activity.getString(R.string.server_down);
            }
        } else {
            errMessage = activity.getString(R.string.no_internet_connection);
            urlFileBrochure = product.getProduct_file();
        }
        return urlFileBrochure;
    }

    @Override
    protected void onPostExecute(final String result) {
        NiftyDialogBuilder builder = null;
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (errMessage != null) {
            if (errMessage.equals(activity.getString(R.string.no_data_from_server))) {
                builder =  NiftyDialogBuilder.getInstance(activity);
                builder.withTitle("INFO").withMessage(errMessage).show();
            } else {
                Toast.makeText(activity, errMessage, Toast.LENGTH_SHORT).show();
            }
        }
        if(result!=null && !"".equalsIgnoreCase(result)){
             File file = new File(result);
             if (file.exists()) {
                 if(builder!=null){
                     builder.dismiss();
                 }
                 Uri uri;
                 if (Build.VERSION.SDK_INT >= 24) {
                     uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
                 } else {
                     uri = Uri.fromFile(file);
                 }

                 Intent intent = new Intent(Intent.ACTION_VIEW);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                 intent.setDataAndType(uri, "application/pdf");
                 activity.startActivity(intent);
             }
        }
    }

    private static GetFilePathAndStatus getFile(Context context,String base64, String filename, String extension) {
        GetFilePathAndStatus getFilePathAndStatus = new GetFilePathAndStatus();

        try( FileOutputStream os = new FileOutputStream(getReportPath(context, filename, extension), false)) {
            byte[] pdfAsBytes = Base64.decode(base64, 0);
            os.write(pdfAsBytes);
            os.flush();
            getFilePathAndStatus.filStatus = true;
            getFilePathAndStatus.filePath = getReportPath(context, filename, extension);
            return getFilePathAndStatus;
        } catch (IOException e) {
            e.printStackTrace();
            getFilePathAndStatus.filStatus = false;
            getFilePathAndStatus.filePath = getReportPath(context, filename, extension);
            return getFilePathAndStatus;
        }
    }

    private static String getReportPath(Context context, String filename, String extension) {
        File file = new File(context.getExternalFilesDir(null), "WFIMOSCS/ProdukBrosur");
        if (!file.exists()) {
            file.mkdirs();
        }

        String uriSting = (file.getAbsolutePath() + "/" + filename + "." + extension);
        return uriSting;
    }

    private static class GetFilePathAndStatus {
        public boolean filStatus;
        public String filePath;
    }

}
