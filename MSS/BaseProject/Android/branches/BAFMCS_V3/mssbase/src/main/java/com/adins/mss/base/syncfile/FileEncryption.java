package com.adins.mss.base.syncfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for encrypting/decrypting files.
 */
public class FileEncryption {

    public static final int AES_Key_Size = 128;
    /**
     * Path to public key file
     */
    public String publicKey;
    /**
     * Path to private key file
     */
    public String privateKey;
    private Cipher pkCipher, aesCipher;
    private byte[] aesKey;
    private SecretKeySpec aeskeySpec;
    private String iv = "FA4AD06616A0F2BB2F94A71F4FAC9469";

    /**
     * Constructor: creates ciphers
     */
    public FileEncryption() throws GeneralSecurityException {
        // create RSA public key cipher
        pkCipher = Cipher.getInstance("RSA");
        // create AES shared key cipher
        aesCipher = Cipher.getInstance("AES");
        // generate key
        //        makeKey();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Creates a new AES key
     * Generate random AES Key
     */
    private void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(hexStringToByteArray(iv), "AES");
    }


    /**
     * Encrypts and then copies the contents of a given file.
     *
     * @param in  Input file to encrypt (source file to encrypt)
     * @param out Out encrypted file (target file encrypted)
     */
    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        try {
            makeKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     *
     * @param in  Input encrypted file (source existing encrypted file)
     * @param out Output unencrypted file (target file unencrypted)
     */
    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        try {
            makeKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }

}
