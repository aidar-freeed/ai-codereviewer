package com.adins.mss.foundation.http.net;

import android.content.Context;

import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.TlsVersion;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Aditya Purwa on 2/23/2015.
 * HTTPs version of HTTPClient.
 */
public class HttpsClient extends HttpClient {
    /**
     * Default keystore name if not specified.
     */
    private static final String DEFAULT_KEYSTORE_NAME = "keystore";

    private KeyStore keystore;
    /**
     * Algorithm for the trust manager. Default to RSA
     */
    private String algorithm = "RSA";
    /**
     * Protocol for the socket. Default to TLS.
     */
    private String protocol = "TLS";
    /**
     * The keystore type. Default to BKS for Android Bouncy Castle provider.
     */
    private String keystoreType = "BKS";

    /**
     * Mark the client to bypass hostname verification.
     */
    private boolean bypassHostnameVerification = false;

    /**
     * Mark the client to accept all certificate. THIS COULD BE DANGEROUS.
     */
    private boolean acceptAllCertificate = false;

    /**
     * Initialize a new instance of secure version of the HTTP client. Call initialize to initialize
     * the keystore.
     *
     * @param context        The context for the client.
     * @param keystoreStream Stream containing the certificate keystore.
     * @param password       Password for the keystore.
     * @throws java.security.cert.CertificateException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @throws java.security.KeyStoreException
     */
    public HttpsClient(Context context, InputStream keystoreStream, String password) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, KeyManagementException {
        super(context);

        KeyStore store = KeyStore.getInstance(getKeystoreType());
        store.load(keystoreStream, password.toCharArray());
        this.keystore = store;

    }

    /**
     * Initialize a new instance of secure version of the HTTP client. Call initialize to initialize
     * the keystore.
     * This will use the default keystore specified in DEFAULT_KEYSTORE_NAME.
     *
     * @param context  The context for the client.
     * @param password Password for the keystore.
     * @throws java.io.IOException
     * @throws java.security.KeyStoreException
     * @throws java.security.cert.CertificateException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyManagementException
     */
    public HttpsClient(Context context, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        this(context, context.getAssets().open(DEFAULT_KEYSTORE_NAME), password);

    }

    /**
     * Initialize a new instance of secure version of the HTTP client. Call initialize to initialize
     * the keystore.
     *
     * @param context  The context for the client.
     * @param keystore The keystore to use.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.security.KeyManagementException
     */
    public HttpsClient(Context context, KeyStore keystore)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(context);

        this.keystore = keystore;
    }

    /**
     * Initialize a new instance of secure version of the HTTP client. Call initialize to initialize
     * the keystore. This will use the default keystore trusted by the Android device.
     *
     * @param context The context for the client.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.security.KeyManagementException
     */
    public HttpsClient(Context context) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(context);
    }

    public HttpsClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super();
    }

    /**
     * Get the keystore value.
     *
     * @return Keystore.
     */
    public KeyStore getKeystore() {
        return keystore;
    }

    /**
     * Set the keystore value, call to initialize may be required to reset the keystore.
     *
     * @param keystore The keystore to use.
     */
    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

    /**
     * Initialize the keystore. If the keystore is null, it will use Android device keystore.
     *
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.security.KeyManagementException
     */
    public void initialize()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        SSLContext sslContext = SSLContext.getInstance(getProtocol());

        //Nendi: 2019-08-12 | Add Connection Spec to specify cipherSuite version
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        client.setConnectionSpecs(Collections.singletonList(spec));
        //End Add connection spec

        if (isBypassHostnameVerification()) {
            client.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } else {
            client.setHostnameVerifier(null);
        }

        if (isAcceptAllCertificate()) {
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        X509Certificate[] acceptedIssuer;

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            acceptedIssuer = chain;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            acceptedIssuer = chain;
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return acceptedIssuer;
                        }
                    }

            }, new SecureRandom());
            client.setSslSocketFactory(sslContext.getSocketFactory());
        } else {
            if (keystore != null) {
                Security.addProvider(new BouncyCastleProvider());
                //KeyPairGenerator kpgen = KeyPairGenerator.getInstance(algorithm, "BC");


                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
                trustManagerFactory.init(keystore);

                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                                // not implemented
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                                // not implemented
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                        }
                };
                sslContext = SSLContext.getInstance(getProtocol());
                sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//                sslContext.init(null, trustAllCerts, new SecureRandom());
                client.setSslSocketFactory(sslContext.getSocketFactory());

            }
        }
    }

    /**
     * Get the protocol used by the SSL. Default to TLS.
     *
     * @return Protocol.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol used by the SSL.
     *
     * @param protocol Protocol to use.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the keystore type used to load the keystore. Default to BKS for Android Bouncy Castle.
     *
     * @return Keystore type.
     */
    public String getKeystoreType() {
        return keystoreType;
    }

    /**
     * Set the keystore type used to load the keystore.
     *
     * @param keystoreType Keystore type.
     */
    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    /**
     * Get the algorithm used by the trust manager. Default to RSA.
     *
     * @return Algorithm used by the trust manager.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Set the algorithm used by the trust manager.
     *
     * @param algorithm Algorithm.
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets the state, whether the HTTPs client will accept all server certificates.
     * THIS COULD BE DANGEROUS.
     *
     * @return True if the client accept all certificates, false otherwise.
     */
    public boolean isAcceptAllCertificate() {
        return acceptAllCertificate;
    }

    /**
     * Sets the state, whether the HTTPs client will accept all server certificates.
     * When set to true, the keystore passed will not be used.
     * May require call to initialize method after set.
     * THIS COULD BE DANGEROUS.
     *
     * @param acceptAllCertificate True to accept all certificates, false otherwise.
     */
    public void setAcceptAllCertificate(boolean acceptAllCertificate) {
        this.acceptAllCertificate = acceptAllCertificate;
    }

    /**
     * Gets the state, whether the HTTPs client will bypass hostname verification.
     * THIS COULD BE DANGEROUS.
     *
     * @return True if the client will bypass hostname check, false otherwise.
     */
    public boolean isBypassHostnameVerification() {
        return bypassHostnameVerification;
    }

    /**
     * Sets the state, whether the HTTPs client will bypass hostname verification.
     * May require call to initialize method after set.
     * THIS COULD BE DANGEROUS.
     *
     * @param bypassHostnameVerification True if the client will bypass hostname check, false otherwise.
     */
    public void setBypassHostnameVerification(boolean bypassHostnameVerification) {
        this.bypassHostnameVerification = bypassHostnameVerification;
    }

}
