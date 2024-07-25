package com.adins.mss.coll.fragments.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.dynamicform.form.questions.ImageViewerActivity;
import com.adins.mss.base.dynamicform.form.questions.viewholder.ImageQuestionViewHolder;
import com.adins.mss.base.timeline.Constants;
import com.adins.mss.coll.R;
import com.adins.mss.coll.commons.Toaster;
import com.adins.mss.coll.commons.ViewManager;
import com.adins.mss.coll.interfaces.DepositReportImpl;
import com.adins.mss.coll.interfaces.DepositReportInterface;
import com.adins.mss.coll.interfaces.OnCameraInAppListener;
import com.adins.mss.coll.interfaces.callback.DepositReportCallback;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.DepositReportD;
import com.adins.mss.dao.DepositReportH;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.camerainapp.CameraActivity;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.image.Utils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class DepositReportTransferView extends ViewManager {
    private FragmentActivity activity;
    private DepositReportInterface iDepositReport;
    private AppCompatSpinner transferBySpinner;
    private View asBank;
    private View asCashier;
    private EditText editNomorRekening;
    private EditText editNamaBank;
    private EditText editBatchId;
    private EditText editNamaKasir;
    private ImageView imageBukti;
    private Button buttonSelectPhoto;
    private Button buttonSend;
    public Bundle savedInstanceState;
    private View view;
    protected static byte[] byteImage = null;
    public String total     = "";
    public String batchId;
    protected static Bitmap image = null;
    protected static Bitmap tempImage = null;
    private OnCameraInAppListener onCameraInAppListener;
    private DepositReportImpl.SendDepositReportTask sendDepositReportTask;

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public DepositReportTransferView(FragmentActivity activity, OnCameraInAppListener requestActivityResult) {
        super(activity);
        this.activity = activity;
        this.onCameraInAppListener = requestActivityResult;
        iDepositReport= new DepositReportImpl(activity);
    }

    public View layoutInflater(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.new_fragment_deposit_report_transfer, container, false);
        return view;
    }

    @Override
    public void onCreate() {
        if(view!=null) {
            asBank = view.findViewById(R.id.transferAsBank);
            asCashier = view.findViewById(R.id.transferAsCashier);
            transferBySpinner = (AppCompatSpinner) view.findViewById(R.id.transferBySpinner);
            transferBySpinner.setAdapter(new ArrayAdapter<String>(activity, R.layout.spinner_style2, R.id.text_spin, activity.getResources().getStringArray(R.array.transfer_spinner)));

            editNomorRekening = (EditText) view.findViewById(R.id.txtAccountNo);
            editBatchId = (EditText) view.findViewById(R.id.txtBatchId);
            editNamaBank = (EditText) view.findViewById(R.id.txtBankName);
            editNamaKasir = (EditText) view.findViewById(R.id.txtCashierName);
            imageBukti = (ImageView) view.findViewById(R.id.imgBukti);
            buttonSelectPhoto = (Button) view.findViewById(R.id.btnTakePhoto);
            buttonSend = (Button) view.findViewById(R.id.btnSend);

            editBatchId.setText(batchId);

            imageBukti.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View paramView) {
                    if(imageBukti.getDrawable()!=null && image!=null){
                        if(byteImage==null) byteImage = Utils.bitmapToByte(tempImage);
                        imageBukti.setDrawingCacheEnabled(true);
                        imageBukti.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        imageBukti.buildDrawingCache();
                        image = Bitmap.createBitmap(imageBukti.getDrawingCache(true));
                        Global.getSharedGlobal().setIsViewer(true);

                        Global.getSharedGlobal().setIsViewer(true);
                        Bundle extras = new Bundle();
                        extras.putByteArray(ImageViewerActivity.BUND_KEY_IMAGE, byteImage);
                        extras.putInt(ImageViewerActivity.BUND_KEY_IMAGE_QUALITY, Utils.picQuality);
                        extras.putBoolean(ImageViewerActivity.BUND_KEY_IMAGE_ISVIEWER, Global.getSharedGlobal().getIsViewer());

                        Intent intent = new Intent(activity, ImageViewerActivity.class);
                        intent.putExtras(extras);
                        imageBukti.setDrawingCacheEnabled(false);
                        activity.startActivity(intent);
                    }
                }
            });

            buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.flag_edit=2;
                    openCameraApp(activity);
                }
            });

            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSend.setClickable(false);
                    buttonSelectPhoto.setClickable(false);
                    buttonSend.setEnabled(false);

                    if (!GlobalData.getSharedGlobalData().getButtonClicked()) {
                        if(null != editNamaKasir) {
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE) ;
                            imm.hideSoftInputFromWindow(editNamaKasir.getWindowToken(), 0);
                        }

                        if(Tool.isInternetconnected(activity)) {
                            sendDepositReportTask = iDepositReport.sendDepositReport(activity);
                            sendDepositReportTask.image     = image;
                            sendDepositReportTask.tempImage = tempImage;
                            sendDepositReportTask.total     = total;
                            sendDepositReportTask.listener  = MyCallback;
                            sendDepositReportTask.execute(editBatchId.getText().toString(),
                                    editNomorRekening.getText().toString().trim(),
                                    editNamaBank.getText().toString().trim(),
                                    editNamaKasir.getText().toString().trim(),
                                    String.valueOf(asBank.getVisibility()));

                        }else{
                            Toaster.warning(activity, activity.getString(R.string.failed_send_data));
                            buttonSend.setClickable(true);
                            buttonSelectPhoto.setClickable(true);
                            buttonSend.setEnabled(true);
                        }
                    }
                }
            });

            transferBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        asBank.setVisibility(View.VISIBLE);
                        asCashier.setVisibility(View.GONE);
                    } else {
                        asBank.setVisibility(View.GONE);
                        asCashier.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    asBank.setVisibility(View.GONE);
                    asCashier.setVisibility(View.VISIBLE);
                }
            });

            editBatchId.setText(batchId);
        }

    }

    DepositReportCallback MyCallback = new DepositReportCallback() {
        @Override
        public void OnFillHeader(int totalTask, int paidTask, int failTask, int visitTask) {}

        @Override
        public void OnFillDetail(HashMap<DepositReportH, List<DepositReportD>> packedListOfBatch) {}

        @Override
        public void OnLoadReconcileData(List<TaskD> reconcileReport, int totalNeedPrint) {}

        @Override
        public void OnError(boolean value) {
            buttonSend.setClickable(true);
            buttonSelectPhoto.setClickable(true);
            buttonSend.setEnabled(true);
        }

        @Override
        public void OnFinish(boolean value) {
            try {
                imageBukti.setDrawingCacheEnabled(false);
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
       //EMPTY
    }

    private void openCameraApp(FragmentActivity mActivity) {
        if (GlobalData.getSharedGlobalData().isUseOwnCamera()) {
            int quality = Utils.picQuality;
            int thumbHeight = Utils.picHeight;
            int thumbWidht = Utils.picWidth;

            Intent intent = new Intent(mActivity, CameraActivity.class);
            intent.putExtra(CameraActivity.PICTURE_WIDTH, thumbWidht);
            intent.putExtra(CameraActivity.PICTURE_HEIGHT, thumbHeight);
            intent.putExtra(CameraActivity.PICTURE_QUALITY, quality);

            onCameraInAppListener.onImageCapture(intent, Utils.REQUEST_IN_APP_CAMERA);
        }
        else{
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(mActivity.getPackageManager())!=null){
                    File photoFile = null;
                    try {
                        photoFile = ImageQuestionViewHolder.createImageFile(activity.getApplicationContext());
                    } catch (IOException ex) {
                        FireCrash.log(ex);
                    }
                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        activity.startActivityForResult(intent, Utils.REQUEST_CAMERA);
                    }
                }
            } catch (Exception e) {             FireCrash.log(e);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.REQUEST_IN_APP_CAMERA && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Uri uri = Uri.parse(bundle.getString(CameraActivity.PICTURE_URI));
                File file = new File(uri.getPath());
                image = Utils.pathToBitmapWithRotation(file);
                tempImage = Utils.pathToBitmapWithRotation(file);
                byteImage = Utils.pathBitmapToByteWithRotation(file);
                imageBukti.setImageBitmap(image);
            }
        } else if (requestCode == Utils.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Uri uri = Uri.parse(DynamicFormActivity.getmCurrentPhotoPath());
            File file = new File(uri.getPath());
            image = Utils.pathToBitmapWithRotation(file);
            byteImage = Utils.pathBitmapToByteWithRotation(file);
            tempImage = Utils.pathToBitmapWithRotation(file);
            imageBukti.setImageBitmap(image);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            Uri outputUri = data.getData();
            try {
                image = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(outputUri));
                tempImage = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(outputUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBukti.setImageBitmap(image);
        }
    }
}
