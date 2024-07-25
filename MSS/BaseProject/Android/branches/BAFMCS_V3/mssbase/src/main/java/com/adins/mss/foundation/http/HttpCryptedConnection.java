package com.adins.mss.foundation.http;

import android.app.Activity;
import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.foundation.http.HttpConnection.ConnectionCryptor;
import com.adins.mss.foundation.security.SAKFormatter;

/**
 * A subclass of HttpConnection which use SAKFormatter as the encryption engine by default, communicate via ConnectionCryptor interface
 * <br>Still, this class allow the usage of other cryptor, by change the default cryptor in GlobalData
 * <p>
 * <p>The Cryptor used is set from GlobalData
 *
 * @author glen.iglesias
 * @see GlobalData
 */
public class HttpCryptedConnection extends HttpConnection implements ConnectionCryptor {

    /**
     * Construct a HttpCryptedConnection with cryptor as stated on GlobalData, or SAKFormatter by default
     *
     * @param enableEncryption flag for outgoing data encryption
     * @param enableDecryption flag for incoming data decryption
     */
    public HttpCryptedConnection(boolean enableEncryption, boolean enableDecryption) {

        //Use cryptor set on GlobalData, if any
        ConnectionCryptor connCryptor = GlobalData.getSharedGlobalData().getConnectionCryptor();
        if (connCryptor != null) {
            this.setCryptor(connCryptor);
        } else {
            this.setCryptor(this);
        }

        this.setEnableEncryption(enableEncryption);
        this.setEnableDecryption(enableDecryption);
    }

    public HttpCryptedConnection(Context context, boolean enableEncryption, boolean enableDecryption, boolean enableSecureConnection) {
        ConnectionCryptor connCryptor = GlobalData.getSharedGlobalData().getConnectionCryptor();
        if (connCryptor != null) {
            this.setCryptor(connCryptor);
        } else {
            this.setCryptor(this);
        }

        this.setEnableEncryption(enableEncryption);
        this.setEnableDecryption(enableDecryption);
        this.setContext(context);
        this.setSecureConnection(enableSecureConnection);
    }

    public HttpCryptedConnection(Context context, boolean enableEncryption, boolean enableDecryption) {
        ConnectionCryptor connCryptor = GlobalData.getSharedGlobalData().getConnectionCryptor();
        if (connCryptor != null) {
            this.setCryptor(connCryptor);
        } else {
            this.setCryptor(this);
        }

        this.setEnableEncryption(enableEncryption);
        this.setEnableDecryption(enableDecryption);
        this.setContext(context);
        this.setSecureConnection(GlobalData.getSharedGlobalData().isSecureConnection());
    }

    public HttpCryptedConnection(Activity activity, boolean enableEncryption, boolean enableDecryption) {
        ConnectionCryptor connCryptor = GlobalData.getSharedGlobalData().getConnectionCryptor();
        if (connCryptor != null) {
            this.setCryptor(connCryptor);
        } else {
            this.setCryptor(this);
        }

        this.setEnableEncryption(enableEncryption);
        this.setEnableDecryption(enableDecryption);
        this.setActivity(activity);
        this.setContext(activity);
        this.setSecureConnection(GlobalData.getSharedGlobalData().isSecureConnection());
    }

    /**
     * Construct a HttpCryptedConnection with own implementation of ConnectionCryptor to allow custom cryptor
     *
     * @param enableEncryption
     * @param enableDecryption
     * @param cryptor
     */
    public HttpCryptedConnection(boolean enableEncryption, boolean enableDecryption, ConnectionCryptor cryptor) {
        this.setCryptor(cryptor);
        this.setEnableEncryption(enableEncryption);
        this.setEnableDecryption(enableDecryption);
    }

    @Override
    public String encrpyt(String data) {
        byte[] cryptedBytes = SAKFormatter.cipherData(data);
        String result = new String(cryptedBytes);
        return result;
    }

    @Override
    public String decrypt(String data) {
        String result = SAKFormatter.decipherData(data);
        return result;
    }

}
