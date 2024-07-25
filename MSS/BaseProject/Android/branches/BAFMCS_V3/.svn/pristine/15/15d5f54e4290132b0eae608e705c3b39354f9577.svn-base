package com.adins.mss.foundation.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.LogoPrint;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.foundation.db.dataaccess.LogoPrintDataAccess;
import com.bixolon.printer.BixolonPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class BixolonPrintManager extends AbstractPrintManager_ {
    protected static final String TAG = "TaskManager";
    Context context;
    public final Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTING:
                            // tried
                            break;
                        case BixolonPrinter.STATE_CONNECTED:
                            connected = true;
                            break;
                        case BixolonPrinter.STATE_NONE:
                            connected = false;
                            break;
                        default:
                            break;
                    }
                    break;
                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME);
                    break;
                case BixolonPrinter.MESSAGE_TOAST:
                    Toast.makeText(
                            context.getApplicationContext(),
                            msg.getData()
                                    .getString(BixolonPrinter.KEY_STRING_TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    Bitmap logo = null;
    private BxlService mBxlService;

    public BixolonPrintManager(Context context, List<PrintResult> list) {
        super(list);

        //olivia : utk dapat logo sesuai nama tenant
        String tenant = GlobalData.getSharedGlobalData().getTenant();
        int pos = tenant.indexOf("@");
        String tenantName = tenant.substring(pos+1, tenant.length());

        LogoPrint logoPrint = LogoPrintDataAccess.getOne(context, tenantName);
        if (logoPrint != null) {
            this.logo = BitmapFactory.decodeByteArray(logoPrint.getImage_bitmap(), 0, logoPrint.getImage_bitmap().length);
        } else {
            BitmapDrawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.adins_logo, context.getTheme());
            } else {
                drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.adins_logo);
            }
            this.logo = drawable.getBitmap();
        }

        this.context = context;
    }

    @Override
    public boolean connect() throws Exception {
        if (!connected) {
            mBxlService = new BxlService(BixolonPrintManager.this);
            connected = mBxlService.Connect() == BxlService.BXL_SUCCESS;
        }
        return connected;
    }

    @Override
    public boolean disconnect() throws Exception {
        if (connected && mBxlService != null) {
            mBxlService.Disconnect();
            mBxlService = null;
            connected = false;
        }
        return true;
    }

    private String getDataToPrint() {
        int verticalPosition = 96; // 96, sebagai posisi awal
        char start = (char) 27;
        char end = (char) 3;

        StringBuilder sbToPrint = new StringBuilder();
        for (PrintResult bean : Constant.getListOfPrintItem()) {
            String type = bean.getPrint_type_id();
            String vPosition = "";

            if ((verticalPosition + 33) < 100) {
                verticalPosition = verticalPosition + 33;
                vPosition = "00" + verticalPosition;
            } else if ((verticalPosition + 33) < 1000) {
                verticalPosition = verticalPosition + 33;
                vPosition = "0" + verticalPosition;
            } else {
                vPosition = "" + verticalPosition;
                verticalPosition = verticalPosition + 33;

            }
            if (Global.PRINT_NEW_LINE.equals(type)) {
                String label = bean.getLabel();
                sbToPrint.append(start).append(
                        "L" + vPosition + "000101111000");
                sbToPrint.append(start).append("D" + label);
            } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                String label = bean.getLabel();
                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG, "eato " + e);
                    }

                    // ------------- agar posisi bisa ditengah,
                    if(setn == null)
                        continue;

                    int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                    String sHposition = "0";

                    if (iHposition < 10) {
                        sHposition = "000" + iHposition;
                    } else if (iHposition < 100) {
                        sHposition = "00" + iHposition;
                    } else if (iHposition < 1000) {
                        sHposition = "0" + iHposition;
                    }
                    // -------------

                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + sHposition
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());

                        verticalPosition = verticalPosition + 33;
                    }

                }
            } else if (Global.PRINT_ANSWER.equals(type)) {
                String label = bean.getLabel();
                String answer = bean.getValue();
                if (answer != null && !answer.equals("")) {
                    if (label.equals("")
                            || label == null
                            || label.equals("null")) { // jika pertanyaan tidak ada labelnya maka posisi ditengahkan

                        // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                        Vector vt = FontSato.wrap(374, answer.trim());

                        for (int j = 0; j < vt.size(); j++) {

                            SentencesSato setn = null;

                            try {
                                setn = (SentencesSato) vt.elementAt(j);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                            }

                            // ------------- agar posisi bisa ditengah,
                            int iHposition = (384 - setn.getLenghtSentemce()) / 2;
                            String sHposition = "0";

                            if (iHposition < 10) {
                                sHposition = "000" + iHposition;
                            } else if (iHposition < 100) {
                                sHposition = "00" + iHposition;
                            } else if (iHposition < 1000) {
                                sHposition = "0" + iHposition;
                            }
                            // -------------

                            if (j == 0) {
                                sbToPrint.append(start).append("L"
                                        + vPosition
                                        + sHposition
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            } else {
                                sbToPrint.append(start).append("L"
                                        + vPrintPosition(verticalPosition)
                                        + sHposition
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                                verticalPosition = verticalPosition + 33;
                            }
                        }

                    } else {

                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "000101111000");
                        sbToPrint.append(start).append("D" + label);

                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "020001111000");
                        sbToPrint.append(start).append("D:");

                        Vector vt;

                        vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                        for (int j = 0; j < vt.size(); j++) {

                            SentencesSato setn = null;

                            try {
                                setn = (SentencesSato) vt.elementAt(j);
                            } catch (Exception e) {
                                FireCrash.log(e);
                                if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                            }
                            if (j == 0) {
                                sbToPrint.append(start).append("L"
                                        + vPosition
                                        + "0208"
                                        + "01111000");
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            } else {
                                sbToPrint.append(start).append("L"
                                        + vPrintPosition(verticalPosition)
                                        + "0208"
                                        + "01111000");
                                verticalPosition = verticalPosition + 33;
                                sbToPrint.append(start).append("D"
                                        + setn.getSentence().trim());
                            }

                        }
                    }
                } else {
                    verticalPosition = verticalPosition - 33;
                }
            } else if (Global.PRINT_USER_NAME.equals(type)) {
                String label = bean.getLabel();
                String answer = GlobalData.getSharedGlobalData().getUser().getFullname();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            } else if (Global.PRINT_LOGIN_ID.equals(type)) {
                String label = bean.getLabel();
                String answer = GlobalData.getSharedGlobalData().getUser().getLogin_id();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            } else if (Global.PRINT_TIMESTAMP.equals(type)) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                Date date = new Date();
                String answer = df.format(date);
                String label = bean.getLabel();

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "000101111000");
                sbToPrint.append(start).append("D" + label);

                sbToPrint.append(start).append("L"
                        + vPosition
                        + "020001111000");
                sbToPrint.append(start).append("D:");

                Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                for (int j = 0; j < vt.size(); j++) {

                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        if (Global.IS_DEV) Log.i(TAG,"eato " + e);
                    }
                    if (j == 0) {
                        sbToPrint.append(start).append("L"
                                + vPosition
                                + "0208"
                                + "01111000");
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    } else {
                        sbToPrint.append(start).append("L"
                                + vPrintPosition(verticalPosition)
                                + "0208"
                                + "01111000");
                        verticalPosition = verticalPosition + 33;
                        sbToPrint.append(start).append("D"
                                + setn.getSentence().trim());
                    }

                }
            }
        }
        // ENDING LINE
        verticalPosition = verticalPosition + 33;
        sbToPrint.append(start).append(
                "L" + vPrintPosition(verticalPosition) + "000101222000");
        sbToPrint.append(start).append("D----------------");

        sbToPrint.append(start).append("Q0001").append(start).append("Z");
        sbToPrint.append(end);

        return sbToPrint.toString();
    }

    public String vPrintPosition(int verticalPosition) {
        String vPosition = "";
        if ((verticalPosition + 33) < 100) {
            verticalPosition = verticalPosition + 33;
            vPosition = "00" + verticalPosition;
        } else if ((verticalPosition + 33) < 1000) {
            verticalPosition = verticalPosition + 33;
            vPosition = "0" + verticalPosition;
        } else {
            vPosition = "" + verticalPosition;
        }

        return vPosition;
    }

    @Override
    public boolean printSato() throws Exception {
        if (connected) {
            mBxlService.PrintTextSato(this.getDataToPrint());
        } else {
            throw new Exception("Device is not connected to the printer.");
        }
        return true;
    }

    private String getDataToPrintZebra() {
        for (PrintResult bean : Constant.getListOfPrintItem()) {
            String type = bean.getPrint_type_id();
            if (Global.PRINT_NEW_LINE.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                sb.append(label);
                sb.append(blank);
            } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                int labelLen = label.length();
                //1 line = 48 char
                int spaceLength = (48 - labelLen) / 2;
                while (spaceLength > 0) {
                    sb.append(blank);
                    spaceLength--;
                }
                sb.append(label);
            } else if (Global.PRINT_USER_NAME.equals(type) || Global.PRINT_LOGIN_ID.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                int labelLen = label.length();
                sb.append(label);
                int spaceLength = separatorStart - labelLen;
                while (spaceLength > 0) {
                    sb.append(blank);
                    spaceLength--;
                }
                sb.append(valueSeparator);
                sb.append(blank);

                String value = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                value = (value == null) ? "" : value;
                sb.append(value);
            } else if (Global.PRINT_ANSWER.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                int labelLen = label.length();
                if (label == null || label.equals("")) {
                    //question no label = center
                    String value = bean.getValue();
                    int valueLen = value.length();
                    int spaceLength = (48 - valueLen) / 2;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(value);
                } else {
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = bean.getValue();
                    value = (value == null) ? "" : value;
                    sb.append(value);
                }
            } else if (Global.PRINT_TIMESTAMP.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                int labelLen = label.length();
                sb.append(label);
                int spaceLength = separatorStart - labelLen;
                while (spaceLength > 0) {
                    sb.append(blank);
                    spaceLength--;
                }
                sb.append(valueSeparator);
                sb.append(blank);

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                Date date = new Date();
                String value = df.format(date);
                value = (value == null) ? "" : value;
                sb.append(value);
            }
        }

        return "";
    }

    @Override
    public boolean printZebra() throws Exception {
        if (connected) {
            this.getDataToPrintZebra();
        } else {
            throw new Exception("Device is not connected to the printer.");
        }
        return true;
    }

    @Override
    public boolean print() throws Exception {
        boolean isNeedFeedLine = true;
        if (connected) {
            for (PrintResult bean : list) {
                String type = bean.getPrint_type_id();
                if (Global.PRINT_NEW_LINE.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_BRANCH_ADDRESS.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_BRANCH_NAME.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LABEL_CENTER.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LABEL.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LABEL_BOLD.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_BOLD,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LABEL_CENTER_BOLD.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_BOLD,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LOGO.equals(type)) {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT < 14) {
                        //di bawah ICS masuk sini
                        mBxlService.PrintImage(logo,
                                BxlService.BXL_WIDTH_FULL, BxlService.BXL_ALIGNMENT_CENTER, 20);
                    } else {
                        mBxlService.printBitmap(logo,
                                BixolonPrinter.ALIGNMENT_CENTER, 384, 63, true);
                    }


                } else if (Global.PRINT_ANSWER.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = bean.getValue();


                    value = (value == null) ? "" : value;


                    if ("".equalsIgnoreCase(value)) {
                        isNeedFeedLine = false;
                    } else {

                        if (null == label || "".equalsIgnoreCase(label)) {
                            mBxlService.PrintText(value,
                                    BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_DEFAULT,
                                    BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                        } else {
                            sb.append(value);
                            mBxlService.PrintText(sb.toString(),
                                    BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                                    BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                        }


                    }
                } else if (Global.PRINT_USER_NAME.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = GlobalData.getSharedGlobalData().getUser().getFullname();
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_LOGIN_ID.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    String value = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_BT_ID.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);
                    String value = "?";
                    try {
                        value = bean.getValue();
                    } catch (Exception e) {
                        FireCrash.log(e);

                    }
                    value = (value == null) ? "" : value;
                    sb.append(value);

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (Global.PRINT_TIMESTAMP.equals(type)) {
                    StringBuilder sb = new StringBuilder();

                    String label = bean.getLabel();
                    int labelLen = label.length();
                    sb.append(label);
                    int spaceLength = separatorStart - labelLen;
                    while (spaceLength > 0) {
                        sb.append(blank);
                        spaceLength--;
                    }
                    sb.append(valueSeparator);
                    sb.append(blank);

                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Date date = new Date();
                    String value = df.format(date);

                    value = (value == null) ? "" : value;
                    sb.append(value);

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                }
                if (isNeedFeedLine) {
                    mBxlService.LineFeed(1);
                } else {
                    isNeedFeedLine = true;
                }

            }
            mBxlService.LineFeed(2);
        } else {
            throw new Exception("Device is not connected to the printer.");
        }

        return true;
    }
}
