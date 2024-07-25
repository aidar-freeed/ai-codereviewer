package com.adins.mss.base.dialogfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.PrintActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SendResultActivity;
import com.adins.mss.base.util.ByteFormatter;
import com.adins.mss.base.util.SecondFormatter;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;

import zj.com.cn.bluetooth.sdk.Main_Activity1;

public class SendResultDialog extends DialogFragment {

    public static String rvNumber = null;
    private static SendResultActivity INSTANCE = null;
    TextView txtResult;
    TextView txtTimeSent;
    TextView txtDateSize;
    ImageView imgHeader;
    private String taskId;
    //private boolean isPrintable;
    private boolean error;
    private Button btnOk;
    private Button btnPrintPage;
    private NewMainActivity mainActivity;

    public SendResultDialog() {
    }

    public static SendResultDialog newInstance() {
        SendResultDialog fragment = new SendResultDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_dialog_send_result, container, false);

        rvNumber = null;
        txtResult = (TextView) v.findViewById(com.adins.mss.base.R.id.txtResult);
        txtTimeSent = (TextView) v.findViewById(com.adins.mss.base.R.id.txtTimeSent);
        txtDateSize = (TextView) v.findViewById(com.adins.mss.base.R.id.txtDataSize);
        imgHeader = (ImageView) v.findViewById(com.adins.mss.base.R.id.imgHeader);

        btnPrintPage = (Button) v.findViewById(com.adins.mss.base.R.id.btnPrintPage);

        btnOk = (Button) v.findViewById(com.adins.mss.base.R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoTimeline();
            }
        });
        btnPrintPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalData.getSharedGlobalData().getListPrinter().isEmpty()) {
                    final String[] listPrinterDevice = GlobalData.getSharedGlobalData().getListPrinter().split(",");
                    CharSequence printers[] = new CharSequence[listPrinterDevice.length];
                    for (int i = 0; i < listPrinterDevice.length; i++) {
                        String printer[] = listPrinterDevice[i].split("@");
                        printers[i] = printer[0];
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose Printer Driver");
                    builder.setItems(printers, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String printer[] = listPrinterDevice[which].split("@");
                            if ("0".equalsIgnoreCase(printer[1])) {
                                Intent intent = new Intent(getContext(), PrintActivity.class);
                                //intent.putExtra(name, value);
                                intent.putExtra("taskId", taskId);
                                intent.putExtra("source", "submit");
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getContext(), Main_Activity1.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("taskId", taskId);
                                intent.putExtra("source", "submit");
                                startActivity(intent);
                            }

                        }
                    });
                    builder.show();
                }
            }
        });
        Bundle extras = getArguments();

        this.error = extras.getBoolean(Global.BUND_KEY_SURVEY_ERROR);
        if (error) {
            String errMessage = extras.getString(Global.BUND_KEY_SURVEY_ERROR_MSG);
            if (errMessage == null)
                txtResult.setText(extras.getString(Global.BUND_KEY_SEND_RESULT));
            else txtResult.setText(errMessage);
            imgHeader.setImageResource(com.adins.mss.base.R.drawable.ic_failed);
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
            try {
                this.taskId = extras.getString(Global.BUND_KEY_TASK_ID);
                String time = extras.getString(Global.BUND_KEY_SEND_TIME);
                String seconds = SecondFormatter.secondsToString(Long.parseLong(time));
                String mTime = getString(com.adins.mss.base.R.string.time) + seconds;

                String size = extras.getString(Global.BUND_KEY_SEND_SIZE);
                String bytes = ByteFormatter.formatByteSize(Long.parseLong(size));
                String mSize = getString(com.adins.mss.base.R.string.size) + bytes;

                txtTimeSent.setText(mTime);
                txtDateSize.setText(mSize);
            } catch (Exception e) {
                FireCrash.log(e);
                txtTimeSent.setVisibility(View.GONE);
                txtDateSize.setVisibility(View.GONE);
            }
            txtResult.setText(result);

            try {
                TaskH taskH = TaskHDataAccess.getOneTaskHeader(getContext(), taskId);
                Scheme scheme = SchemeDataAccess.getOne(getContext(), taskH.getUuid_scheme());
                if (scheme != null) {
                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                    boolean isTaskPaid = TaskDDataAccess.isTaskPaid(getContext(),
                            uuidUser, taskH.getUuid_task_h());
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getContext(), uuidUser);
                    if (isRVinFront) {
                        btnPrintPage.setVisibility(View.GONE);
                    } else if (!scheme.getIs_printable().equals("1") || !isTaskPaid) {
                        btnPrintPage.setVisibility(View.GONE);
                    } else {
                        btnPrintPage.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO: handle exception
            }
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rvNumber != null && !rvNumber.isEmpty()) {
            btnPrintPage.setVisibility(View.GONE);
        }
        Utility.freeMemory();
    }
}
