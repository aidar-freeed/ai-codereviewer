package com.adins.mss.base.dynamicform.form.questions.viewholder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.JsonRequestLuOnlineView;
import com.adins.mss.base.dynamicform.JsonResponseLuOnlineView;
import com.adins.mss.base.dynamicform.form.models.PhotoDocumentBean;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.view.DynamicQuestionView;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.Base64;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

public class LuOnlineQuestionViewHolder extends RecyclerView.ViewHolder {
    private final TextView mQuestionLabel;
    private final Button btnView;
    private final ImageView imgPhotos;
    private final Activity mActivity;
    private QuestionBean bean;

    public LuOnlineQuestionViewHolder(View itemView, Activity activity) {
        super(itemView);
        mQuestionLabel = (TextView) itemView.findViewById(R.id.questionTextLabel);
        btnView = (Button) itemView.findViewById(R.id.btnView);
        imgPhotos = (ImageView) itemView.findViewById(R.id.imgPhotoLuOnlineAnswer);
        mActivity = activity;
    }

    public void bind(final QuestionBean item, int number) {
        bean = item;
        String qLabel = number + ". " + bean.getQuestion_label();
        mQuestionLabel.setText(qLabel);

        String taskId = DynamicQuestionView.header.getTask_id();
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(mActivity, taskId);
        final String agreementNo = taskH.getAppl_no();

        // Keep Data for not happen moving to Other Question because do Scrolling
        final LinearLayout layoutListDocument = (LinearLayout) itemView.findViewById(R.id.layoutListDocument);
        if (bean.getListImgByteArray() != null) {
            // To avoid trigger many times add photos on 'layoutListDocument'
            layoutListDocument.removeAllViews();

            // Show all list document have been saved on QuestionBean
            if (bean.getListImgByteArray().size() > 0) {
                ArrayList<byte[]> beanListLuDocKeep = bean.getListImgByteArray();
                for (int index = 0; index < beanListLuDocKeep.size(); index++) {
                    ImageView imgView = new ImageView(mActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);

                    final byte[] imageByteArray = beanListLuDocKeep.get(index);
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

                    layoutParams.setMargins(2, 8, 2, 8);
                    layoutParams.gravity = Gravity.CENTER;
                    imgView.setLayoutParams(layoutParams);
                    imgView.setImageBitmap(bitmap);
                    layoutListDocument.addView(imgView);

                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Global.isViewer = !DynamicFormActivity.allowImageEdit;
                            Bundle extras = new Bundle();
                            extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, imageByteArray);
                            extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                            extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                            Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                            intent.putExtras(extras);
                            mActivity.startActivityForResult(intent, Global.REQUEST_EDIT_IMAGE);
                        }
                    });
                }
                layoutListDocument.setVisibility(View.VISIBLE);
                btnView.setVisibility(View.GONE);
            }
        } else {
            layoutListDocument.setVisibility(View.GONE);
            btnView.setVisibility(View.VISIBLE);
        }

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetViewLuOnline getViewLuOnline = new GetViewLuOnline(mActivity, bean, layoutListDocument, btnView, agreementNo, bean.getIdentifier_name());
                getViewLuOnline.execute();
            }
        });

        imgPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.isViewer = true;
                Bundle extras = new Bundle();
                extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, bean.getImgAnswer());
                extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                Intent intent = new Intent(mActivity, ImageViewerActivity.class);
                intent.putExtras(extras);
                mActivity.startActivity(intent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private static class GetViewLuOnline extends AsyncTask<Void, Void, JsonResponseLuOnlineView> {
        private ProgressDialog progressDialog;
        private final Activity activity;
        private final QuestionBean bean;
        private final LinearLayout mLayout;
        private final Button btnView;
        private final String agrNo;
        private final String refId;
        private String errorMessage = "";

        public GetViewLuOnline(Activity activity, QuestionBean bean, LinearLayout mLayout, Button btnView, String argNo, String refId) {
            this.activity = activity;
            this.bean = bean;
            this.mLayout = mLayout;
            this.btnView = btnView;
            this.agrNo = argNo;
            this.refId = refId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.please_wait_rv_mobile), true);
            progressDialog.show();
        }

        @Override
        protected JsonResponseLuOnlineView doInBackground(Void... voids) {
            if (Tool.isInternetconnected(activity)) {
                JsonRequestLuOnlineView request = new JsonRequestLuOnlineView();
                AuditDataType auditData = GlobalData.getSharedGlobalData().getAuditData();
                request.setAudit(auditData);
                request.addImeiAndroidIdToUnstructured();
                request.setAgrNo(agrNo);
                request.setRefId(refId);

                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                String url = GlobalData.getSharedGlobalData().getURL_GET_LU_ONLINE();
                HttpConnectionResult serverResult;

                try {
                    String json = GsonHelper.toJson(request);
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    if (serverResult != null) {
                        try {
                            String result = serverResult.getResult();
                            return GsonHelper.fromJson(result, JsonResponseLuOnlineView.class);
                        } catch (Exception e) {
                            errorMessage = activity.getString(R.string.failed_view_lu_online);
                            ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                            ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", Tool.getSystemDateTime().toLocaleString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari server"));
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage = activity.getString(R.string.failed_view_lu_online);
                    }
                } catch (Exception e) {
                    errorMessage = activity.getString(R.string.failed_view_lu_online);
                    ACRA.getErrorReporter().putCustomData("errorRequestToServer", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorRequestToServer", Tool.getSystemDateTime().toLocaleString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request ke server"));
                    e.printStackTrace();
                }
            } else {
                errorMessage = AppContext.getAppContext().getString(R.string.no_internet_connection);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonResponseLuOnlineView result) {
            super.onPostExecute(result);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (errorMessage.equals("")) {
                if (result != null) {
                    if (result.getStatus().getCode() == 200) {
                        List<PhotoDocumentBean> listLuDoc = result.getData();
                        ArrayList<byte[]> beanListLuDoc = new ArrayList<>();
                        mLayout.removeAllViews();
                        for (int index = 0; index < listLuDoc.size(); index++) {
                            ImageView imgView = new ImageView(activity);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);

                            final byte[] imageByteArray = Base64.decode(result.getData().get(index).getContent());
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

                            layoutParams.setMargins(2, 8, 2, 8);
                            layoutParams.gravity = Gravity.CENTER;
                            imgView.setLayoutParams(layoutParams);
                            imgView.setImageBitmap(bitmap);
                            mLayout.addView(imgView);
                            beanListLuDoc.add(imageByteArray);

                            imgView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Global.isViewer = true;
                                    Bundle extras = new Bundle();
                                    extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, imageByteArray);
                                    extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                                    extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.isViewer);
                                    Intent intent = new Intent(activity, ImageViewerActivity.class);
                                    intent.putExtras(extras);
                                    activity.startActivity(intent);
                                }
                            });
                        }
                        bean.setListImgByteArray(beanListLuDoc);
                        mLayout.setVisibility(View.VISIBLE);
                        btnView.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(activity, result.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
