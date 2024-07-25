package com.adins.mss.foundation.security;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.SecureRandom;

//import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;

public class AESKeyGenerator {

    private static final SecureRandom sr = new SecureRandom();

    public AESKeyGenerator() {
        super();
    }

    public KeyParameter generateNewAESKey(byte[] seeds) {

        // byte[] seeds = sr.generateSeed(32);
        // if (Global.IS_DEV) System.out.println("key:" + new String(seeds) + " -key Hex:"
        // + new String(Hex.encode(seeds)));
        // if (Global.IS_DEV) System.out.println("key length:" + seeds.length);
        KeyParameter keyParam = new KeyParameter(seeds);
        return keyParam;
    }

    public byte[] generateNewSeeds() {
        byte[] seeds = sr.generateSeed(32);
        return seeds;
    }

    public byte[] generateAESKey(String phrase, byte[] salt, int count) {
        PKCS5S1ParametersGenerator paramGen = new PKCS5S1ParametersGenerator(
                new SHA1Digest());

        byte[] phraseInBytes = PKCS5S1ParametersGenerator
                .PKCS5PasswordToBytes(phrase.toCharArray());

        paramGen.init(phraseInBytes, salt, count);

        CipherParameters ciphParams = paramGen.generateDerivedParameters(64);

        KeyParameter keyParams = (KeyParameter) ciphParams;

        byte[] key = keyParams.getKey();

        // String hex = getHexString(key);
        // if (Global.IS_DEV) System.out.println(phrase);
        // if (Global.IS_DEV) System.out.println("Got: " + hex);
        // if (Global.IS_DEV) System.out.println("Got2: " + new String(key));
        return key;
    }

    private String getHexString(byte[] hex) {
        StringBuffer buff = new StringBuffer();

        for (int n = 0; n < hex.length; n++) {
            byte b = hex[n];
            int i = b & 0xFF;

            String string = Integer.toHexString(i);

            // Append leading zero if required
            if (string.length() == 1) {
                string = "0" + string;
            }

            buff.append(string);
        }

        return buff.toString();
    }
}
