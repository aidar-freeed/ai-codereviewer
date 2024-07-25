package com.adins.mss.foundation.http;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.UserSession;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.net.HttpClient;
import com.adins.mss.foundation.http.net.HttpsClient;
import com.adins.mss.foundation.oauth2.OAuthConstants;
import com.adins.mss.foundation.oauth2.OAuthUtils;
import com.adins.mss.foundation.oauth2.OauthErrorResponse;
import com.adins.mss.foundation.oauth2.Token;
import com.adins.mss.foundation.oauth2.store.SharedPreferencesTokenStore;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.services.NotificationThread;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * @author glen.iglesias
 *         <p>
 *         A Class to do HTTP Connection with POST Method, and use encryption via ConnectionCryptor interface
 */
public class HttpConnection {

    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_TENANT = "Tenant";
    public static final String HEADER_CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String HEADER_CONTENT_TYPE_PDF = "application/pdf";
    public static final int FLAG_LOGOUT_STATUS_CODE = 1158;
    public static final int FLAG_UNINSTALL_STATUS_CODE = 1153;
    public static final String ERROR_STATUSCODE_FROM_SERVER = "Your account can not use this application further";
    public static String tenantId;
    /**
     * To dictate if outgoing data need to be encrypted
     */
    private boolean enableEncryption;
    /**
     * To dictate if received data need to be decrypted
     */
    private boolean enableDecryption;
    private ConnectionCryptor cryptor;
    private int defaultConnectionTimeout = 120000;
    private Context mContext;
    private boolean isErrorFromServer = false;
    private boolean isSecureConnection;
    private Activity mActivity;

    public HttpConnection() {
    }

    public HttpConnection(boolean encrypt, boolean decrypt) {
        this.enableEncryption = encrypt;
        this.enableDecryption = decrypt;
    }

    public HttpConnection(Context context, boolean encrypt, boolean decrypt, boolean isSecureConnection) {
        this.enableEncryption = encrypt;
        this.enableDecryption = decrypt;
        this.mContext = context;
        this.isSecureConnection = isSecureConnection;
    }

    public HttpConnection(Context context, boolean encrypt, boolean decrypt) {
        this.enableEncryption = encrypt;
        this.enableDecryption = decrypt;
        this.mContext = context;
        this.isSecureConnection = GlobalData.getSharedGlobalData().isSecureConnection();
    }

    public HttpConnection(boolean encrypt, boolean decrypt, ConnectionCryptor cryptor) {
        this(encrypt, decrypt);

    }

    /**
     * A static method to check if internet connection is available
     * <p>Snippet from : <br>http://stackoverflow.com/questions/9570237/android-check-internet-connection
     *
     * @return true if it is connected, false if it is not
     */
    public static boolean isInternetConnectedTo() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            return ipAddr != null;
        } catch (Exception e) {
            FireCrash.log(e);
            return false;
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    //=== Properties ===//
    public boolean isEnableEncryption() {
        return enableEncryption;
    }

    public void setEnableEncryption(boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isSecureConnection() {
        return isSecureConnection;
    }

    public void setSecureConnection(boolean isSecureConnection) {
        this.isSecureConnection = isSecureConnection;
    }

    public boolean isEnableDecryption() {
        return enableDecryption;
    }

    public void setEnableDecryption(boolean enableDecryption) {
        this.enableDecryption = enableDecryption;
    }

    public ConnectionCryptor getCryptor() {
        return cryptor;
    }

    public void setCryptor(ConnectionCryptor cryptor) {
        this.cryptor = cryptor;
    }

    public int getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }

    public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
    }

    public boolean isHaveTenant(String loginId) {
        String[] idNtenant = Tool.split(loginId, "@");
        return idNtenant.length > 1;
    }

    //=== Methods ===//

    public String getTenantId() {
        if (tenantId == null || tenantId.isEmpty()) {
            String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
            String[] idNtenant = Tool.split(loginId, "@");
            if (idNtenant.length > 1)
                tenantId = idNtenant[1];
        }
        return tenantId;
    }

    /**
     * Method to make a HTTP POST request to server, returning HttpConnectionResult data model
     * <p>
     * <p>NOTE: need to run in background thread
     *
     * @param url               target url to make HTTP Connection
     * @param data              content to send to target url
     * @param connectionTimeout
     * @return HttpConnectionResult object, containing returned status code, error message (if any), and returned message from server (if succeed)
     */
    public HttpConnectionResult requestHTTPPost(String url, String data, int connectionTimeout) throws Exception {
        HttpConnectionResult result = null;
        GlobalData.setRequireRelogin(false);
        try {
            HttpClient client = new HttpClient(mContext);
            client.setConnectionTimeout((long) connectionTimeout);

            if (enableEncryption) {
                data = cryptor.encrpyt(data);
            }
            RequestBody body = RequestBody.create(MediaType.parse(HEADER_CONTENT_TYPE_KEY), data);

            Request.Builder builder = new Request.Builder();
            builder.url(url)
                    .addHeader(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_URL_ENCODED);
            if (GlobalData.getSharedGlobalData().isRequiresAccessToken()) {
                if (GlobalData.getSharedGlobalData().getToken().isExpired()) {
                    Token token = GlobalData.getSharedGlobalData().getToken().refresh(getActivity(), GlobalData.getSharedGlobalData().getoAuth2Client());
                    SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(mContext);
                    tokenStore.store(GlobalData.getSharedGlobalData().getoAuth2Client().getUsername(), token);
                    GlobalData.getSharedGlobalData().setToken(token);
                }
                builder.addHeader(OAuthConstants.AUTHORIZATION,
                        OAuthUtils.getAuthorizationHeaderForAccessToken(
                                GlobalData.getSharedGlobalData().getToken().getAccessToken()));
                if (GlobalData.getSharedGlobalData().getUser() != null) {
                    if (isHaveTenant(GlobalData.getSharedGlobalData().getUser().getLogin_id()))
                        builder.addHeader(HEADER_TENANT, getTenantId());
                } else if (DefaultLoginModel.tenantId != null && !DefaultLoginModel.tenantId.isEmpty()) {
                    builder.addHeader(HEADER_TENANT, DefaultLoginModel.tenantId);
                }
            }
            builder.post(body);
            Request request = builder.build();

            Response responsePost = null;

            try {
                responsePost = client.execute(request);

                int statusCode = responsePost.code();

                if (responsePost.priorResponse() != null && responsePost.priorResponse().code() != HttpStatus.SC_OK) {
                    result = new HttpConnectionResult(null, responsePost.priorResponse().message());
                    return result;
                }
                if (statusCode == HttpStatus.SC_OK) {
                    String strResult = null;
                    InputStream in = responsePost.body().byteStream();
                    strResult = IOUtils.toString(in, Charset.defaultCharset());
                    if (enableDecryption) {
                        strResult = cryptor.decrypt(strResult);
                    }
                    try {
                        MssResponseType mss = GsonHelper.fromJson(strResult, MssResponseType.class);
                        if (mss != null) {
                            int code = mss.getStatus().getCode();
                            if (code == FLAG_LOGOUT_STATUS_CODE) {
                                if (getContext() != null) {
                                    isErrorFromServer = false;
                                    GlobalData.setRequireRelogin(true);
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        // UI code goes here
                                        try {
                                            Intent intent = new Intent(Global.FORCE_LOGOUT_ACTION);
                                            LocalBroadcastManager.getInstance(AppContext.getAppContext()).sendBroadcast(intent);
                                            if (mContext != null) {
                                                DialogManager.showForceExitAlert(mContext, mContext.getString(R.string.msgLogout));
                                            }
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });
                            } else if (code == FLAG_UNINSTALL_STATUS_CODE) {
                                if (getActivity() != null) {
                                    isErrorFromServer = true;
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        // UI code goes here
                                        try {
                                            if (getActivity() != null) {
                                                DialogManager.UninstallerHandler(getActivity());
                                            }
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });
                            }
                            com.adins.mss.foundation.notification.Notification.getSharedNotification().clearNotif(mContext, 9);
                        }
                    } catch (JsonSyntaxException jse) {
                        jse.printStackTrace();
                        if (mContext != null) {
                            if (mActivity != null && !mActivity.isFinishing()) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager mNotificationManager =
                                                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                                        builder.setSmallIcon(NotificationThread.getNotificationIcon());
                                        NotificationCompat.BigTextStyle inboxStyle =
                                                new NotificationCompat.BigTextStyle();
                                        // Sets a title for the Inbox in expanded layout
                                        inboxStyle.setBigContentTitle(mContext.getString(R.string.no_internet_connection));
                                        inboxStyle.bigText(mContext.getString(R.string.connection_failed));
                                        inboxStyle.setSummaryText(mContext.getString(R.string.connection_failed_wifi));
                                        builder.setPriority(Thread.NORM_PRIORITY);
                                        builder.setContentTitle(mContext.getString(R.string.no_internet_connection));
                                        builder.setContentText(mContext.getString(R.string.connection_failed));
                                        builder.setSubText(mContext.getString(R.string.connection_failed_wifi));
                                        builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                        builder.setStyle(inboxStyle);
                                        builder.setAutoCancel(true);

                                        mNotificationManager.notify(9, builder.build());
                                    }
                                });
                            }
                        }
                    } catch (JsonParseException jpe) {
                        jpe.printStackTrace();
                    } catch (IllegalStateException ise) {
                        ise.printStackTrace();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                    //generate result
                    if (isErrorFromServer) {
                        result = new HttpConnectionResult(null, ERROR_STATUSCODE_FROM_SERVER);
                    } else {
                        result = new HttpConnectionResult(statusCode, responsePost.message(), strResult);
                    }
                } else if (statusCode == OAuthConstants.HTTP_BAD_REQUEST || statusCode == OAuthConstants.HTTP_UNAUTHORIZED) {
                    OauthErrorResponse errorResponse = null;
                    try {
                        errorResponse = GsonHelper.fromJson(responsePost.body().string(), OauthErrorResponse.class);
                        result = new HttpConnectionResult(statusCode, responsePost.message(), errorResponse.error_description);
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = new HttpConnectionResult(statusCode, responsePost.message(), responsePost.message());
                    }

                    if (statusCode == OAuthConstants.HTTP_UNAUTHORIZED) {
                        onInvalidToken();
                    }
                } else {
                    //generate failed result
                    result = new HttpConnectionResult(statusCode, responsePost.message(), responsePost.message());
                }
            }
            catch (UnknownHostException e){
                FireCrash.log(e);
                String msg = null;
                if(mContext != null)
                    msg = mContext.getString(R.string.no_internet_connection);
                else if(mActivity != null)
                    msg = mActivity.getString(R.string.no_internet_connection);
                result = new HttpConnectionResult(null, msg);
                return result;
            }
            catch (SocketTimeoutException e) {
                FireCrash.log(e);
                String msg = null;
                if(mContext != null)
                    msg = mContext.getString(R.string.connection_timeout);
                else if(mActivity != null)
                    msg = mActivity.getString(R.string.connection_timeout);
                result = new HttpConnectionResult(null, msg);
                return result;
            }
            catch (Exception e) {
                FireCrash.log(e);
                String msg;
                msg = e.getMessage();
                if (msg == null) msg = e.getClass().getName();
                result = new HttpConnectionResult(null, msg);
                return result;
            } finally {
                if (responsePost != null) {
                    try {
                        responsePost.body().close();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            }
        } catch (InvalidTokenException e) {
            onInvalidToken();
        } catch (Exception e) {
            FireCrash.log(e);
            String msg;
            msg = e.getMessage();
            if (msg == null)
                msg = e.getClass().getName();
            result = new HttpConnectionResult(null, msg);
        }

        return result;
    }

    /**
     * Method to make a HTTPS POST request to server, returning HttpConnectionResult data model
     * <p>
     * <p>NOTE: need to run in background thread
     *
     * @param url               target url to make HTTP Connection
     * @param data              content to send to target url
     * @param connectionTimeout connection time out in milisecond
     * @return HttpConnectionResult object, containing returned status code, error message (if any), and returned message from server (if succeed)
     */
    public HttpConnectionResult requestHTTPSPost(String url, String data, int connectionTimeout) throws Exception {
        HttpConnectionResult result = null;

        try {
            HttpsClient client = new HttpsClient(mContext);
            client.setAcceptAllCertificate(true);
            client.setBypassHostnameVerification(true);
            client.setConnectionTimeout((long) connectionTimeout);
            client.initialize();

            if (enableEncryption) {
                data = cryptor.encrpyt(data);
            }

            RequestBody body = RequestBody.create(MediaType.parse(HEADER_CONTENT_TYPE_KEY), data);
            Request.Builder builder = new Request.Builder();
            builder.url(url)
                    .addHeader(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_URL_ENCODED);
            if (GlobalData.getSharedGlobalData().isRequiresAccessToken()) {
                if (GlobalData.getSharedGlobalData().getToken() != null &&
                        GlobalData.getSharedGlobalData().getToken().isExpired()) {
                    Token token = GlobalData.getSharedGlobalData().getToken().refresh(getActivity(), GlobalData.getSharedGlobalData().getoAuth2Client());
                    SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(mContext);
                    tokenStore.store(GlobalData.getSharedGlobalData().getoAuth2Client().getUsername(), token);
                    GlobalData.getSharedGlobalData().setToken(token);
                }
                builder.addHeader(OAuthConstants.AUTHORIZATION,
                        OAuthUtils.getAuthorizationHeaderForAccessToken(
                                GlobalData.getSharedGlobalData().getToken().getAccessToken()));
                if (GlobalData.getSharedGlobalData().getUser() != null) {
                    if (isHaveTenant(GlobalData.getSharedGlobalData().getUser().getLogin_id()))
                        builder.addHeader(HEADER_TENANT, getTenantId());
                } else if (DefaultLoginModel.tenantId != null && !DefaultLoginModel.tenantId.isEmpty()) {
                    builder.addHeader(HEADER_TENANT, DefaultLoginModel.tenantId);
                }
            }
            builder.post(body);
            Request request = builder.build();

            Response responsePost = null;

            try {
                responsePost = client.execute(request);

                int statusCode = responsePost.code();

                if (responsePost.priorResponse() != null && responsePost.priorResponse().code() != HttpStatus.SC_OK) {
                    result = new HttpConnectionResult(null, responsePost.priorResponse().message());
                    return result;
                }

                if (statusCode == HttpStatus.SC_OK) {
                    String strResult = null;
                    InputStream in = responsePost.body().byteStream();
                    strResult = IOUtils.toString(in, Charset.defaultCharset());
                    if (enableDecryption) {
                        strResult = cryptor.decrypt(strResult);
                    }

                    try {
                        MssResponseType mss = GsonHelper.fromJson(strResult, MssResponseType.class);
                        if (mss != null) {
                            int code = mss.getStatus().getCode();
                            if (code == FLAG_LOGOUT_STATUS_CODE) {
                                if (getActivity() != null) {
                                    isErrorFromServer = false;
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        // UI code goes here
                                        try {
                                            if (getActivity() != null) {
                                                DialogManager.showForceExitAlert(getActivity(), mContext.getString(R.string.msgLogout));
                                            }
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });

                            } else if (code == FLAG_UNINSTALL_STATUS_CODE) {
                                if (getActivity() != null) {
                                    isErrorFromServer = true;
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        // UI code goes here
                                        try {
                                            if (getActivity() != null) {
                                                DialogManager.UninstallerHandler(getActivity());
                                            }
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JsonSyntaxException jse) {
                        jse.printStackTrace();
                        if (mContext != null) {
                            if (mActivity != null && !mActivity.isFinishing()) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager mNotificationManager =
                                                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                                        builder.setSmallIcon(NotificationThread.getNotificationIcon());
                                        NotificationCompat.BigTextStyle inboxStyle =
                                                new NotificationCompat.BigTextStyle();
                                        // Sets a title for the Inbox in expanded layout
                                        inboxStyle.setBigContentTitle(mContext.getString(R.string.no_internet_connection));
                                        inboxStyle.bigText(mContext.getString(R.string.connection_failed));
                                        inboxStyle.setSummaryText(mContext.getString(R.string.connection_failed_wifi));
                                        builder.setPriority(Thread.NORM_PRIORITY);
                                        builder.setContentTitle(mContext.getString(R.string.no_internet_connection));
                                        builder.setContentText(mContext.getString(R.string.connection_failed));
                                        builder.setSubText(mContext.getString(R.string.connection_failed_wifi));
                                        builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                        builder.setStyle(inboxStyle);
                                        builder.setAutoCancel(true);

                                        mNotificationManager.notify(9, builder.build());
                                    }
                                });
                            }
                        }
                    } catch (JsonParseException jpe) {
                        jpe.printStackTrace();
                    } catch (IllegalStateException ise) {
                        ise.printStackTrace();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                    //generate result
                    if (isErrorFromServer) {
                        result = new HttpConnectionResult(null, ERROR_STATUSCODE_FROM_SERVER);
                    } else {
                        result = new HttpConnectionResult(statusCode, responsePost.message(), strResult);
                    }
                } else if (statusCode == OAuthConstants.HTTP_BAD_REQUEST || statusCode == OAuthConstants.HTTP_UNAUTHORIZED) {
                    OauthErrorResponse errorResponse = null;
                    try {
                        errorResponse = GsonHelper.fromJson(responsePost.body().string(), OauthErrorResponse.class);
                        result = new HttpConnectionResult(statusCode, responsePost.message(), errorResponse.error_description);
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = new HttpConnectionResult(statusCode, responsePost.message(), responsePost.message());
                    }

                    if (statusCode == OAuthConstants.HTTP_UNAUTHORIZED) {
                        onInvalidToken();
                    }
                } else {
                    //generate failed result
                    result = new HttpConnectionResult(statusCode, responsePost.message(), responsePost.message());
                }
            }
            catch (UnknownHostException e){
                FireCrash.log(e);
                String msg = null;
                if(mContext != null)
                    msg = mContext.getString(R.string.no_internet_connection);
                else if(mActivity != null)
                    msg = mActivity.getString(R.string.no_internet_connection);
                result = new HttpConnectionResult(null, msg);
                return result;
            }
            catch (SocketTimeoutException e) {
                FireCrash.log(e);
                String msg = null;
                if(mContext != null)
                    msg = mContext.getString(R.string.connection_timeout);
                else if(mActivity != null)
                    msg = mActivity.getString(R.string.connection_timeout);
                result = new HttpConnectionResult(null, msg);
                return result;
            }
            catch (Exception e) {
                FireCrash.log(e);
                String msg;
                msg = e.getMessage();
                if (msg == null) msg = e.getClass().getName();
                result = new HttpConnectionResult(null, msg);
                return result;
            } finally {
                if (responsePost != null) {
                    try {
                        responsePost.body().close();
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            }
        } catch (InvalidTokenException e) {
            onInvalidToken();
        } catch (Exception e) {
            FireCrash.log(e);
            String msg;
            msg = e.getMessage();
            if (msg == null)
                msg = e.getClass().getName();
            else{
                if(msg.contains("failed to connect to")){
                    if(mContext != null)
                        msg = mContext.getString(R.string.connection_timeout);
                    else if(mActivity != null)
                        msg = mActivity.getString(R.string.connection_timeout);
                }
            }
            result = new HttpConnectionResult(null, msg);
        }

        return result;
    }

    /**
     * A variation of requestHTTPPost which will throw Exception if connection failed
     *
     * @param url               target url to make HTTP Connection
     * @param data              content to send to target url
     * @param connectionTimeout
     * @throws Exception if connection failed
     * @return HttpConnectionResult object, containing returned status code, error message (if any), and returned message from server (if succeed)
     */
    public HttpConnectionResult requestHTTPPost2(String url, String data, int connectionTimeout) throws Exception {
        HttpConnectionResult result = requestHTTPPost(url, data, connectionTimeout);
        if (result.getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Connection to server failed: " + result.getStatusCode() + " "
                    + result.getReasonPhrase());
        } else {
            return result;
        }
    }

    public HttpConnectionResult requestToServer(String url, String data, int connectionTimeout) throws Exception {
        HttpConnectionResult result;
        if (isSecureConnection) {
            result = requestHTTPSPost(url, data, connectionTimeout);
        } else {
            result = requestHTTPPost(url, data, connectionTimeout);
        }
        return result;
    }

    public HttpConnectionResult requestToServer(String url, String data) throws Exception {
        HttpConnectionResult result;
        if (isSecureConnection) {
            result = requestHTTPSPost(url, data, defaultConnectionTimeout);
        } else {
            result = requestHTTPPost(url, data, defaultConnectionTimeout);
        }
        return result;
    }

    /**
     * Do a HTTP Request with POST Method and use default timeout
     *
     * @param url  target url to make HTTP Connection
     * @param data content to send to target url
     * @return HttpConnectionResult object, containing returned status code, error message (if any), and returned message from server (if succeed)
     */
    public HttpConnectionResult requestHTTPPost(String url, String data) throws Exception {
        HttpConnectionResult result = requestHTTPPost(url, data, defaultConnectionTimeout);
        return result;
    }

    /**
     * Do a HTTP Request with POST Method and use default timeout, but throw exception if connection failed
     *
     * @param url  target url to make HTTP Connection
     * @param data content to send to target url
     * @throws Exception if connection failed
     * @return HttpConnectionResult object, containing returned status code, error message (if any), and returned message from server (if succeed)
     */
    public HttpConnectionResult requestHTTPPost2(String url, String data) throws Exception {
        HttpConnectionResult result = requestHTTPPost2(url, data, defaultConnectionTimeout);
        return result;
    }

    /**
     * Method to convert InputStream into readable String
     *
     * @param is InputStream to be converted
     * @throws IOException
     * @return Result of InputStream conversion
     */
    public String inputStreamToString(InputStream is) throws IOException {
        String result;
        String line = null;
        StringBuilder stringbuilder = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = br.readLine()) != null)
                stringbuilder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        result = stringbuilder.toString();
        return result;
    }

    private void onInvalidToken() {
        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getContext(), "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        if (!hasLogged || !GlobalData.getSharedGlobalData().isRequiresAccessToken()) return;

        UserSession.setInvalidToken(true);

        if (getActivity() != null) {
            isErrorFromServer = true;
            Utility.stopAllServices(getActivity());

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    // UI code goes here
                    try {
                        if (getActivity() != null) {
                            DialogManager.showForceExitAlert(getActivity(),
                                    mContext.getString(R.string.failed_refresh_token));
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        // empty
                    }
                }
            });
        }
    }

    //Interface to encryption outside of this class

    /**
     * Interface for HttpConnection class to communicate with encryption engine
     *
     * @author glen.iglesias
     */
    public interface ConnectionCryptor {
        String encrpyt(String data);

        String decrypt(String data);
    }


}
