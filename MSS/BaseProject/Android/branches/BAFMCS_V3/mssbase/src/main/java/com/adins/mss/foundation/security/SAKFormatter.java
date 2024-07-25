package com.adins.mss.foundation.security;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.format.DateFormat;

import com.adins.mss.base.crashlytics.FireCrash;

import org.acra.ACRA;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

//import net.rim.device.api.util.Arrays;

public class SAKFormatter {


    /**
     * Static method to decipher a string ciphered with SAKFormatter
     *
     * @param receivedData SAK ciphered string to be deciphered
     * @return SAK deciphered string
     */
    public static String decipherData(String receivedData) {
        String result = null;
        AESKeyGenerator AESKeygen = new AESKeyGenerator();

        if (receivedData != null && receivedData.length() > 0) {
            // Decoding Base64 BouncyCrypto menjadi Hex Value
            byte[] istr = decodebase64(receivedData);

            // Parser byte format dalam Hex Value
            // SHA1Hex + CipherDataHex + AESKeyHex

            byte[] SHA1ByteHex = new byte[40];
            byte[] AESKeyByteHex = new byte[64];
            int datasize = istr.length - (40 + 64);
            byte[] cipherdataByteHex = new byte[datasize];
            if (istr.length > 40) {
                System.arraycopy(istr, 0, SHA1ByteHex, 0, SHA1ByteHex.length);
                System.arraycopy(istr, datasize + 40, AESKeyByteHex, 0,
                        AESKeyByteHex.length);
                System.arraycopy(istr, 40, cipherdataByteHex, 0,
                        cipherdataByteHex.length);
            }

            // Decoding hex dari semua HexValue
            byte[] AESKeyOri = Hex.decode(AESKeyByteHex);
            byte[] cipherdata = Hex.decode(cipherdataByteHex);

            // process Decrypt AES256
            AESManager Aes_Mgr = new AESManager();
            KeyParameter AesKey256 = AESKeygen.generateNewAESKey(AESKeyOri);

            try {
                byte[] dec = Aes_Mgr.decryptAES256(cipherdata, AesKey256);
                result = new String(dec);
//				if (Global.IS_DEV) System.out.println("hasil:" + result);
            } catch (InvalidCipherTextException e) {
                e.printStackTrace();
            }

            boolean IsSha1 = isValidSha1(SHA1ByteHex, result);
            if (IsSha1) {
                return result;
            } else {
                ACRA.getErrorReporter().putCustomData("dataSources", receivedData);
                ACRA.getErrorReporter().putCustomData("errorDecryptData", result);
                ACRA.getErrorReporter().putCustomData("errorDecryptDataTime", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Error: This Data been Hijacked !!!, kena exception saat Decrypt Data : " + result));
                return "Error: This Data been Hijacked !!!";
            }

        }
        return result;

    }

    /**
     * @param sendingData
     * @return
     */
    public static byte[] cipherData(String sendingData) {
        byte[] result = null;
        SHA1KeyGenerator shakeygen = new SHA1KeyGenerator();
        byte[] sha1keybyte = null;
        byte[] sha1key = null;
        AESKeyGenerator aeskeygen = new AESKeyGenerator();
        byte[] seeds = aeskeygen.generateNewSeeds();
//		byte[]seed64 = Hex.decode("b4242668e4862eebe34fd7f97d0c93b59cf66d1a0a42b6ff2812de315134d4f2");
//		if (Global.IS_DEV) System.out.println(new String(seed64));
//		byte[] seeds = seed64;
        KeyParameter aesKey256 = aeskeygen.generateNewAESKey(seeds);
        AESManager aesmg = new AESManager();
//		if (Global.IS_DEV) System.out.println(SendingData);
        try {
//			if (Global.IS_DEV) System.out.println("2."+SendingData);
            byte[] cipher = aesmg.encryptAES256(sendingData.getBytes(), aesKey256);
            byte[] hexcipherbyte = Hex.encode(cipher);
            byte[] decryptcipher = aesmg.decryptAES256(cipher, aesKey256);
//			if (Global.IS_DEV) System.out.println("deCipher: " + new String(decryptcipher));
            String ciphertext = new String(decryptcipher);
            sha1keybyte = ciphertext.getBytes();
            sha1key = shakeygen.generateSHA1(sha1keybyte);

            String datatosend = new String(Hex.encode(sha1key))
                    + new String(hexcipherbyte) + new String(Hex.encode(seeds));

            byte[] senddataenc = decodebase64(datatosend);
            byte[] backdecode = encodebase64(senddataenc);
            result = encodebase64(backdecode);
//			if (Global.IS_DEV) System.out.println("hasiL:"+new String(result));

        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] encodebase64(byte[] datatoencoded) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Base64.encode(datatoencoded, 0, datatoencoded.length, out);
        } catch (IOException e) {

        }
        byte[] base64bytes = out.toByteArray();

        return base64bytes;
    }

    //Glen new return type
    public static String encodebase64ToString(byte[] datatoencoded) {
        byte[] base64bytes = encodebase64(datatoencoded);
        String result = new String(base64bytes);
        return result;
    }

    public static byte[] decodebase64(String data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Base64.decode(data, out);
        } catch (IOException e) {

        }
        byte[] base64bytes = out.toByteArray();

        return base64bytes;
    }

    //Glen new param
    public static byte[] decodebase64(byte[] data) {
        byte[] result = null;
        try {
            result = Base64.decode(data);
        } catch (Exception e) {
            FireCrash.log(e);
        }

        return result;
    }


    public static boolean isValidSha1(byte[] Sha1HexSource, String SourceSha1) {
        boolean result = false;
        if(SourceSha1 == null)
            return result;

        byte[] SourceByte = SourceSha1.getBytes(Charset.forName("UTF-8"));
        SHA1KeyGenerator shakeygen = new SHA1KeyGenerator();
        byte[] SourceSha1Byte = shakeygen.generateSHA1(SourceByte);
        byte[] SourceSha1HexByte = Hex.encode(SourceSha1Byte);
        if (Sha1HexSource.length != SourceSha1HexByte.length)
            return false;
        if (new String(Sha1HexSource).equals(new String(SourceSha1HexByte, Charset.forName("UTF-8")))) {
            result = true;
        }

        return result;
    }

}
