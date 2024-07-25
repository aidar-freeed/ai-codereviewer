package com.adins.mss.coll.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.R;
import com.adins.mss.coll.fragments.PdfRendererFragment;
import com.adins.mss.coll.models.loyaltymodels.DocumentListDetail;
import com.adins.mss.coll.models.loyaltymodels.GuidelineFaqResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GuidelineAdapter extends RecyclerView.Adapter<GuidelineAdapter.GuidelineViewHolder> {

    private List<DocumentListDetail> documentList;
    private Context context;
    private String documentNameTitle;

    public GuidelineAdapter(List<DocumentListDetail> documentList, Context context) {
        this.documentList = documentList;
        this.context = context;
    }

    @NonNull
    @Override
    public GuidelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guideline, parent, false);
        return new GuidelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuidelineViewHolder holder, int position) {
        final DocumentListDetail document = documentList.get(position);
        holder.documentName.setText(position + 1 + ". " + document.getNamaDocument());
        holder.documentViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentNameTitle = document.getNamaDocument();
                new GetDocumentPdf().execute(document.getUuidGuideline());
            }
        });

    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public class GuidelineViewHolder extends RecyclerView.ViewHolder {
        TextView documentName;
        Button documentViewBtn;

        public GuidelineViewHolder(View view) {
            super(view);
            documentName = itemView.findViewById(R.id.documentName);
            documentViewBtn = itemView.findViewById(R.id.documentViewBtn);
        }
    }

    public class GetDocumentPdf extends AsyncTask<String, Void, String>{
        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",context.getString(R.string.progressWait), true);
        }

        @Override
        protected String doInBackground(String... uuidGuidelines) {
          try {
              if(Tool.isInternetconnected(context.getApplicationContext())){
                  String result = " ";
                  String uuidGuideline = uuidGuidelines[0];
                  GuidelineFaqRequest guidelineFaqRequest = new GuidelineFaqRequest();
                  guidelineFaqRequest.setUuidGudeline(uuidGuideline);
                  guidelineFaqRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                  String json = GsonHelper.toJson(guidelineFaqRequest);
                  String url = GlobalData.getSharedGlobalData().getURL_GET_DOCUMENT_LIST();
                  boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                  boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                  HttpCryptedConnection httpConn = new  HttpCryptedConnection(context.getApplicationContext(), encrypt, decrypt);
                  HttpConnectionResult serverResult = null;
                  // Firebase Performance Trace Network Request
                  HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(
                          url, FirebasePerformance.HttpMethod.POST);
                  Utility.metricStart(networkMetric, json);
                  serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                  Utility.metricStop(networkMetric, serverResult);

                  if(serverResult != null){
                      if(serverResult .isOK()){
                          try {
                              result = serverResult.getResult();
                              GuidelineFaqResponse response = GsonHelper.fromJson(result, GuidelineFaqResponse.class);
                              return response.getBase64pdf();
                          }catch (Exception e){
                              FireCrash.log(e);
                              e.printStackTrace();
                          }
                      }
                  }
              }
          } catch (Exception e) {
              FireCrash.log(e);
              e.printStackTrace();
          }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(progressDialog != null & progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if(result != null){
                if(!result.isEmpty()){
                    try{
                        byte[] pdfAsBytes = Base64.decode(result, Base64.DEFAULT);
                        String fileName = "DocumentPdf.pdf";
                        File pdfFile = saveToPdfFile(pdfAsBytes, fileName);

                        Bundle bundle = new Bundle();
                        bundle.putString("URL_FILE", pdfFile.getAbsolutePath());
                        bundle.putString("documentName", documentNameTitle);

                        PdfRendererFragment fragment = new PdfRendererFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
                        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }catch (Exception e){
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private File saveToPdfFile(byte[] pdfAsBytes, String fileName) throws IOException {
        File pdfFile = new File(context.getFilesDir().getPath(), fileName);
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(pdfAsBytes);
            fos.flush();
            if(fos != null){
                try {
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pdfFile;
    }

}
