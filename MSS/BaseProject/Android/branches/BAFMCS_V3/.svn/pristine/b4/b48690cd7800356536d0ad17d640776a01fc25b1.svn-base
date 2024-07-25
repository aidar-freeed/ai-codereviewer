package com.adins.mss.base.dynamicform;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.PrintActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.SecondHelper;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.ByteFormatter;
import com.adins.mss.base.util.LocaleHelper;
import com.adins.mss.base.util.SecondFormatter;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import java.util.List;
import java.util.Locale;

import zj.com.cn.bluetooth.sdk.Main_Activity1;

public class SendResultActivity extends Activity implements OnClickListener {
    public static String rvNumber = null;
    private static SendResultActivity INSTANCE = null;
    TextView txtResult;
    TextView txtTimeSent;
    TextView txtDateSize;
    ImageView imgHeader;
    private String taskId;
    private TaskH taskH;
    //private boolean isPrintable;
    private boolean error;
    private Button btnOk;
    private Button btnPrintPage;

    public static SendResultActivity getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_result_layout);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rvNumber != null && !rvNumber.isEmpty()) {
            btnPrintPage.setVisibility(View.GONE);
        }
        Utility.freeMemory();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        Locale locale;
        try{
            locale = new Locale(GlobalData.getSharedGlobalData().getLocale());
            context = LocaleHelper.wrap(newBase, locale);
        } catch (Exception e) {
            locale = new Locale(LocaleHelper.ENGLSIH);
            context = LocaleHelper.wrap(newBase, locale);
        } finally {
            super.attachBaseContext(context);
        }
    }

    private void initialize() {
        rvNumber = null;
        INSTANCE = this;
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtTimeSent = (TextView) findViewById(R.id.txtTimeSent);
        txtDateSize = (TextView) findViewById(R.id.txtDataSize);
        imgHeader = (ImageView) findViewById(R.id.imgHeader);


        //bong 9 apr 15 - add button to page print
        btnPrintPage = (Button) findViewById(R.id.btnPrintPage);

        btnOk = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this);
        btnPrintPage.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        this.error = extras.getBoolean(Global.BUND_KEY_SURVEY_ERROR);
        if (error) {
            String errMessage = extras.getString(Global.BUND_KEY_SURVEY_ERROR_MSG);
            if (errMessage == null)
                txtResult.setText(extras.getString(Global.BUND_KEY_SEND_RESULT));
            else txtResult.setText(errMessage);
            imgHeader.setImageResource(R.drawable.ic_submit_error);
            this.taskId = extras.getString(Global.BUND_KEY_TASK_ID);
            if (taskId.contains("refused"))
                taskId = "Connection Refused";
            txtTimeSent.setText(taskId);
            btnPrintPage.setVisibility(View.GONE);
            txtDateSize.setVisibility(View.GONE);
            if (taskId.contains("been deleted")) {
                imgHeader.setVisibility(View.GONE);
            }
        } else {

            String result = extras.getString(Global.BUND_KEY_SEND_RESULT);
            //this.isPrintable = extras.getBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE);

            try {
                this.taskId = extras.getString(Global.BUND_KEY_TASK_ID);
                String time = extras.getString(Global.BUND_KEY_SEND_TIME);
                String seconds = SecondFormatter.secondsToString(Long.parseLong(time));
                String mTime = getString(R.string.time) + seconds;

                String size = extras.getString(Global.BUND_KEY_SEND_SIZE);
                String bytes = ByteFormatter.formatByteSize(Long.parseLong(size));
                String mSize = getString(R.string.size) + bytes;

                txtTimeSent.setText(mTime);
                txtDateSize.setText(mSize);
            } catch (Exception e) {
                FireCrash.log(e);
                txtTimeSent.setVisibility(View.GONE);
                txtDateSize.setVisibility(View.GONE);
            }
            txtResult.setText(result);


            //TODO bong cek jika bukan taskId maka pakai uuid nya
            try {
                taskH = TaskHDataAccess.getOneTaskHeader(getApplicationContext(), taskId);
                Scheme scheme = SchemeDataAccess.getOne(getApplicationContext(), taskH.getUuid_scheme());
                if (scheme != null) {
                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    boolean isTaskPaid = TaskDDataAccess.isTaskPaid(SendResultActivity.this,
                            uuidUser, taskH.getUuid_task_h());
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(SendResultActivity.this, uuidUser);
                    if (isRVinFront) {
                        btnPrintPage.setVisibility(View.GONE);
                    } else if (!scheme.getIs_printable().equals("1") || !isTaskPaid) {
                        btnPrintPage.setVisibility(View.GONE);
                    } else {
                        btnPrintPage.setVisibility(View.VISIBLE);
                        /*btnPrintPage.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO pindah ke halaman print
								Intent intent = new Intent(getApplicationContext(), PrintActivity.class);
								intent.putExtra("taskId", taskId);
								startActivity(intent);
								finish();
							}
						});*/
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO: handle exception
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.btnOK) {
            onBackPressed();
        } else if (id == R.id.btnPrintPage) {
            //Nendi: 2019-01-15 ~ Bugfix: Print result not available
            List<PrintResult> items = PrintResultDataAccess.getAll(v.getContext(), taskId);
            if (items.size() == 0) {
                TaskManager.generatePrintResult(v.getContext(), taskH);
            }
            SecondHelper.Companion.doPrint(this, taskId, "submit");
//            if (!GlobalData.getSharedGlobalData().getListPrinter().isEmpty()) {
//                final String[] listPrinterDevice = GlobalData.getSharedGlobalData().getListPrinter().split(",");
//                CharSequence printers[] = new CharSequence[listPrinterDevice.length];
//                for (int i = 0; i < listPrinterDevice.length; i++) {
//                    String printer[] = listPrinterDevice[i].split("@");
//                    printers[i] = printer[0];
//                }
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Choose Printer Driver");
//                builder.setItems(printers, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String printer[] = listPrinterDevice[which].split("@");
//                        if ("0".equalsIgnoreCase(printer[1])) {
//                            Intent intent = new Intent(SendResultActivity.this, PrintActivity.class);
//                            //intent.putExtra(name, value);
//                            intent.putExtra("taskId", taskId);
//                            intent.putExtra("source", "submit");
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(SendResultActivity.this, Main_Activity1.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("taskId", taskId);
//                            intent.putExtra("source", "submit");
//                            startActivity(intent);
//                        }
//
//                    }
//                });
//                builder.show();
//            }
        }
    }
}
