package com.adins.mss.base.log;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.dao.LogoPrint;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.foundation.db.dataaccess.LogoPrintDataAccess;
import com.bixolon.printer.BixolonPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class BixolonPrintManager extends AbstractPrintManager {

    public static final int LINE_LENGTH = 32;
    // bong Oct 28th, 2014 adding code from fif
    protected static final String TAG = null;
    /* PRINT ITEM TYPE */
    public static String PRINT_NO_ANSWER = "001";
    public static String PRINT_ANSWER = "002";
    //Glen 9 Aug 2014, new type : timestamp
    public static String PRINT_TIMESTAMP = "004";
    // bong Oct 28th, 2014 - adding from fif
    public static String PRINT_LOGO = "005";
    public static String PRINT_USER = "006";
    public static String PRINT_LABEL_CENTER = "007";
    public static String PRINT_LABEL_CENTER_BOLD = "008";
    public static String PRINT_PRINTER_ID = "003";
    Context context;
    // bong Oct 28th, 2014 - adding code from fif
    public final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTING:

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
    Bitmap logo;
    List<PrintResult> listOfPrint;
    private BxlService mBxlService;

    // bong Oct 28th, 2014 - adding code from fif
    public BixolonPrintManager(Context context, List<PrintResult> listOfPrint) {
        super(listOfPrint);
        // bong Oct 28th, 2014 adding code from fif
        this.context = context;
        this.listOfPrint = listOfPrint;

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
    }

    @Override
    public boolean connect() throws Exception {
        if (!connected) {
            // bong Oct 28th, 2014 - adding code from fif
            mBxlService = new BxlService(BixolonPrintManager.this);
            if (mBxlService.Connect() == BxlService.BXL_SUCCESS) {
                connected = true;
            } else {
                connected = false;
            }
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


    private String getDataToPrint(List<PrintResult> listOfPrint) {
        int verticalPosition = 96; // 96, sebagai posisi awal
        char start = (char) 27;
        char end = (char) 3;

        StringBuilder sbToPrint = new StringBuilder();
        for (PrintResult bean : listOfPrint) {

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
            if (PRINT_NO_ANSWER.equals(type)) {
                String label = bean.getLabel();
                sbToPrint.append(start).append(
                        "L" + vPosition + "000101111000");
                sbToPrint.append(start).append("D" + label);
            } else if (PRINT_LABEL_CENTER.equals(type)) {
                String label = bean.getLabel();
                // nilai sebenarnya 384, dikurangi aj bt jaga2 kalo slah perhtungan
                Vector vt = FontSato.wrap(374, label.trim());

                for (int j = 0; j < vt.size(); j++) {
                    SentencesSato setn = null;

                    try {
                        setn = (SentencesSato) vt.elementAt(j);
                    } catch (Exception e) {
                        FireCrash.log(e);
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
            } else if (PRINT_ANSWER.equals(type)) {
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

                        Vector vt = FontSato.wrap(166, answer.trim()); // panjang area yang akan diprint

                        for (int j = 0; j < vt.size(); j++) {

                            SentencesSato setn = null;

                            try {
                                setn = (SentencesSato) vt.elementAt(j);
                            } catch (Exception e) {
                                FireCrash.log(e);
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
            } else if (PRINT_USER.equals(type)) {
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
            } else if (PRINT_TIMESTAMP.equals(type)) {
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
            mBxlService.PrintTextSato(this.getDataToPrint(list));
        } else {
            throw new Exception("Device is not connected to the printer.");
        }
        return true;
    }

    private String getDataToPrintZebra() {

        for (PrintResult bean : listOfPrint) {

            String type = bean.getPrint_type_id();
            if (PRINT_NO_ANSWER.equals(type)) {
                StringBuilder sb = new StringBuilder();
                String label = bean.getLabel();
                sb.append(label);
                sb.append(blank);
            } else if (PRINT_LABEL_CENTER.equals(type)) {
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
            } else if (PRINT_USER.equals(type)) {
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
            } else if (PRINT_ANSWER.equals(type)) {
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
            } else if (PRINT_TIMESTAMP.equals(type)) {
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
            } else if (PRINT_LOGO.equals(type)) {
                mBxlService.PrintImageZebra(logo,
                        BxlService.BXL_WIDTH_FULL, BxlService.BXL_ALIGNMENT_CENTER, 20);
            }
            // bong Oct 29th, 2014 - add printer_id
            else if (PRINT_PRINTER_ID.equals(type)) {
                BluetoothDevice bd = mBxlService.getmDevice();
                if (bean.getValue() == null || "".equals(bean.getValue()))
                    bean.setValue(bd.getAddress());
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
                if (PRINT_NO_ANSWER.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                }

                // bong Oct 28th, 2014 - adding code from fif
                else if (PRINT_LABEL_CENTER.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (PRINT_LABEL_CENTER_BOLD.equals(type)) {
                    mBxlService.PrintText(bean.getLabel(),
                            BxlService.BXL_ALIGNMENT_CENTER, BxlService.BXL_FT_BOLD,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (PRINT_LOGO.equals(type)) {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT < 14) {
                        //di bawah ICS masuk sini
                        mBxlService.PrintImage(logo,
                                BxlService.BXL_WIDTH_FULL, BxlService.BXL_ALIGNMENT_CENTER, 20);
                    } else {
                        mBxlService.printBitmap(logo,
                                BixolonPrinter.ALIGNMENT_CENTER, 384, 63, true);
                    }
                } else if (PRINT_USER.equals(type)) {
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
                } else if (PRINT_TIMESTAMP.equals(type)) {
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

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                } else if (PRINT_ANSWER.equals(type)) {
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
                    sb.append(value);

                    mBxlService.PrintText(sb.toString(),
                            BxlService.BXL_ALIGNMENT_LEFT, BxlService.BXL_FT_DEFAULT,
                            BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT);
                }
                // bong Oct 29th, 2014 - add printer_id
                else if (PRINT_PRINTER_ID.equals(type)) {
                    BluetoothDevice bd = mBxlService.getmDevice();
                    if (bean.getValue() == null || "".equals(bean.getValue()))
                        bean.setValue(bd.getAddress());
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
