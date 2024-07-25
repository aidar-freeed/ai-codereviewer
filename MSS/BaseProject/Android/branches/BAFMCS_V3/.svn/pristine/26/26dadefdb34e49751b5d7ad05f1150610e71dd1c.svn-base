package com.adins.mss.coll;

import com.adins.mss.foundation.formatter.Formatter;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void checkSignature() throws Exception {
        String keys = "0000000000000000";
        String text = "190320191338010820135747mokasale";

        Digest digest = new SHA256Digest();
        HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(keys.getBytes()));
        hMac.update(text.getBytes(), 0, text.getBytes().length);
        byte[] result = new byte[digest.getDigestSize()];
        hMac.doFinal(result, 0);
        String signature = new String(Hex.encode(result));
        System.out.println(signature);
    }

    @Test
    public void dateTimeTest() {
        String dateTime  = Formatter.formatDate(new Date(), "ddMMyyyyHHmmss");
        System.out.println(dateTime);
    }

    @Test
    public void testDataResult() {
        String result = "00000001|000000000002|DEBIT|553210**********5437|EXPDATE : 08/17|SWIPE|SALE|000003|000004|06Mar2012|10:52:58|123456789012|12345|5,000|";
        String[] data = result.split("\\|");
        String terminalId = data[0];
        String merchantId = data[1];
        String cardType   = data[2];
        String cardNumber = data[3];
        String expireDate = data[4];
        String entryMode  = data[5];
        String trxType    = data[6];
        String batchId    = data[7];
        String traceId    = data[8];
        String dateTrx    = data[9];
        String timeTrx    = data[10];
        String reference  = data[11];
        String approvalId = data[12];
        String totalAmount= data[13];
    }
}