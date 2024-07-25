package com.adins.mss.base.payment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.adins.mss.base.BuildConfig;
import com.adins.mss.base.dynamicform.form.FragmentQuestion;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.pax.dal.IDAL;
import com.pax.dal.entity.ETermInfoKey;
import com.pax.neptunelite.api.NeptuneLiteUser;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.JCEKeyGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Date;

public class PaxPayment {
    private static final String APP_PACKAGE = "com.pax_bni.edc";
    private static final String SECRET_KEY = "0000000000000000";
    private static final String IDENTIFIER = "adins";
    private static final String MENU_LOGON = "logon";
    private static final String MENU_SALES = "sale";
    private static final String MENU_VOID  = "void";
    private static final String MENU_SETTLEMENT = "settlement";
    public static final String ACTION_SUCCESS_PAYMENT = "com.adins.mss.ACTION_SUCCESS";
    private static final String ACTION_FAIL_PAYMENT = "com.adins.mss.ACTION_FAIL";

    private static PaxPayment INSTANCE;
    private Context context;

    public static PaxPayment getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PaxPayment(context);
        }

        return INSTANCE;
    }

    public PaxPayment(Context context) {
        this.context = context;
    }

    //Logon Method for Initial Request Payment
    public void logon() {
        Intent intent = getIntent(this.context);
        if (intent == null) {
            intentNotAvailable();
            return;
        }
        requestIntent(intent, MENU_LOGON);
    }

    public void sale(String amount, String fee) {
        Intent intent = getIntent(this.context);

        if (intent == null) {
            intentNotAvailable();
            return;
        }
        intent.putExtra("amount", amount);
        intent.putExtra("tipAmount", fee);
        requestIntent(intent, MENU_SALES);
    }

    public void settlement() {
        Intent intent = getIntent(context);
        if (intent == null) {
            intentNotAvailable();
            return;
        }
        requestIntent(intent, MENU_SETTLEMENT);
    }

    private Intent getIntent(Context context) {
        Intent intent = null;
        try {
            intent = context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE);
        } catch (Exception ex) {
            Toast.makeText(context, "Payment Channel not available!", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

        return intent;
    }

    private void requestIntent(Intent intent, String menu) {
        String serialNumber = "";
        try {
            IDAL dal = NeptuneLiteUser.getInstance().getDal(context);
            serialNumber = dal.getSys().getTermInfo().get(ETermInfoKey.SN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Format : DateTime + EDC Serial Number + App + Menu
        String dateTime  = Formatter.formatDate(new Date(), "ddMMyyyyHHmmss");
        String rawContent= dateTime + serialNumber + IDENTIFIER + menu;
        String signature = sign(rawContent);

        //Prepare Intent Request
        intent.putExtra("menu", menu);
        intent.putExtra("app", IDENTIFIER);
        intent.putExtra("DateTime", dateTime);
        intent.putExtra("signature", signature);

        context.startActivity(intent);
    }

    private String sign(String content) {
        Digest digest = new SHA256Digest();
        HMac hmac = new HMac(digest);
        hmac.init(new KeyParameter(SECRET_KEY.getBytes()));
        hmac.update(content.getBytes(), 0, content.getBytes().length);
        byte[] result = new byte[digest.getDigestSize()];
        hmac.doFinal(result, 0);
        String signature = new String(Hex.encode(result));
        return signature.toUpperCase();
    }

    private void intentNotAvailable() {
        Toast.makeText(context, "Payment Channel not available!", Toast.LENGTH_LONG).show();
    }

    public static class PaymentResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String app = intent.getStringExtra("App");
            String response = intent.getStringExtra("Respon"); //OK||FAIL

            if (response.equalsIgnoreCase("OK") && intent.hasExtra("Data")) {
                String result   = intent.getStringExtra("Data");
                Log.d("Result", result);

                if (BuildConfig.DEBUG) Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                if (!result.toUpperCase().contains("SALE")) return;

                Bundle bundle = new Bundle();
                bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SUBMIT_FORM);
                bundle.putString("Result", result);
                Message message = new Message();
                message.setData(bundle);
                FragmentQuestion.questionHandler.sendMessage(message);
            } else {
                Intent notify = new Intent(ACTION_FAIL_PAYMENT);
                context.sendBroadcast(notify);
                Toast.makeText(context, "Transaction Failed.", Toast.LENGTH_LONG).show();
            }

            Log.d("App", app);
        }
    }
}
