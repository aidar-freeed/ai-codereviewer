package com.adins.mss.foundation.oauth2;


import android.app.Activity;
import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.util.Base64;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.InvalidTokenException;
import com.adins.mss.foundation.http.net.HttpClient;
import com.adins.mss.foundation.http.net.HttpsClient;
import com.adins.mss.foundation.security.SAKFormatter;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.adins.mss.foundation.http.HttpConnection.HEADER_TENANT;

public class OAuthUtils {

    private static final String TAG = "OAuthUtils";

    public static String getProtectedResourceHttp(OAuth2Client client, Token token, String path) {

        String resourceURL = client.getSite() + path;

        HttpClient httpClient = new HttpClient();
        httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);

        Request request = new Request.Builder().url(resourceURL)
                .addHeader(OAuthConstants.AUTHORIZATION,
                        getAuthorizationHeaderForAccessToken(token
                                .getAccessToken()))
                .get()
                .build();
        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseString = "";
        int code = -1;
        try {
            code = response.code();
            if (code >= 400) {
                throw new RuntimeException(
                        "Could not access protected resource. Server returned http code: "
                                + code);
            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new RuntimeException(priorResponse.message());
            }
            responseString = response.body().string();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            if (decrypt) {
                responseString = SAKFormatter.decipherData(responseString);
            }
            if (Global.IS_DEV) Log.i(TAG, responseString);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseString;
    }

    public static String getProtectedResourceHttps(OAuth2Client client, Token token, String path) {

        String resourceURL = client.getSite() + path;

        HttpsClient httpClient = null;
        try {
            httpClient = new HttpsClient();
            httpClient.setAcceptAllCertificate(true);
            httpClient.setBypassHostnameVerification(true);
            httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);
            httpClient.initialize();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }


        Request request = new Request.Builder().url(resourceURL)
                .addHeader(OAuthConstants.AUTHORIZATION,
                        getAuthorizationHeaderForAccessToken(token
                                .getAccessToken()))
                .get()
                .build();
        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseString = "";
        int code = -1;
        try {
            code = response.code();
            if (code >= 400) {
                throw new RuntimeException(
                        "Could not access protected resource. Server returned http code: "
                                + code);
            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new RuntimeException(priorResponse.message());
            }
            responseString = response.body().string();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            if (decrypt) {
                responseString = SAKFormatter.decipherData(responseString);
            }
            if (Global.IS_DEV) Log.i(TAG, responseString);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseString;
    }

    public static Token getAccessTokenHttp(Context context, OAuth2Config oauthDetails) {
        HttpClient httpClient = new HttpClient(context);
        httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);

        String clientId = oauthDetails.getClientId();
        String clientSecret = oauthDetails.getClientSecret();
        String scope = oauthDetails.getScope();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add(OAuthConstants.GRANT_TYPE,
                oauthDetails.getGrantType());
        formEncodingBuilder.add(OAuthConstants.USERNAME,
                oauthDetails.getUsername());
        formEncodingBuilder.add(OAuthConstants.PASSWORD,
                oauthDetails.getPassword());
        if (isValid(clientId)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_ID,
                    clientId);
        }
        if (isValid(clientSecret)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_SECRET, clientSecret);
        }
        if (isValid(scope)) {
            formEncodingBuilder.add(OAuthConstants.SCOPE,
                    scope);
        }
        formEncodingBuilder.add("application", GlobalData.getSharedGlobalData().getApplication());

        RequestBody body = formEncodingBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(oauthDetails.getTokenEndPointUrl())
                .addHeader(HttpConnection.HEADER_CONTENT_TYPE_KEY, HttpConnection.HEADER_CONTENT_TYPE_URL_ENCODED);
        String loginId = oauthDetails.getUsername();
        String[] idNtenant = Tool.split(loginId, "@");
        if (idNtenant.length > 1)
            builder.addHeader(HEADER_TENANT, idNtenant[1]);
        builder.post(body);


        Request request = builder.build();

        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Token accessToken = null;
        if (response != null) {
            int code = response.code();
            if (code >= 400) {
                OauthErrorResponse errorResponse = null;
                try {
                    errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                if (null != errorResponse && errorResponse.getError_description().contains("token")) {
                    throw new RuntimeException(errorResponse.error_description);
                }
                if (Global.IS_DEV) Log.i(TAG, "Authorization server expects Basic authentication");
                // Add Basic Authorization header
                builder.removeHeader(OAuthConstants.AUTHORIZATION);
                builder.addHeader(
                        OAuthConstants.AUTHORIZATION,
                        getBasicAuthorizationHeader(oauthDetails.getUsername(),
                                oauthDetails.getPassword()));
                if (Global.IS_DEV) Log.i(TAG,"Retry with login credentials");

                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                request = builder.build();
                try {
                    response = httpClient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                code = response.code();
                if (code >= 400) {
                    if (Global.IS_DEV) Log.i(TAG, "Retry with client credentials");
                    builder.removeHeader(OAuthConstants.AUTHORIZATION);
                    builder.addHeader(
                            OAuthConstants.AUTHORIZATION,
                            getBasicAuthorizationHeader(
                                    oauthDetails.getClientId(),
                                    oauthDetails.getClientSecret()));

                    try {
                        response.body().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    request = builder.build();
                    try {
                        response = httpClient.execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    code = response.code();
                    if (code >= 400) {
                        if (code == OAuthConstants.HTTP_BAD_REQUEST || code == OAuthConstants.HTTP_UNAUTHORIZED) {
                            errorResponse = null;
                            try {
                                errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                                throw new RuntimeException(errorResponse.error_description);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        throw new RuntimeException(
                                "Could not retrieve access token for user: "
                                        + oauthDetails.getUsername());
                    }
                }

            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new RuntimeException(priorResponse.message());
            }
            Map<String, ?> map = handleResponse(response);
            accessToken = new Token(new Long((Integer) map.get(OAuthConstants.EXPIRES_IN)), (String) map.get(OAuthConstants.TOKEN_TYPE), (String) map.get(OAuthConstants.REFRESH_TOKEN), (String) map.get(OAuthConstants.ACCESS_TOKEN));

        } else {
            throw new RuntimeException(context.getString(R.string.connection_failed));
        }

        return accessToken;
    }

    public static Token getAccessTokenHttps(Context context, OAuth2Config oauthDetails) {
        HttpsClient httpClient = null;
        try {
            httpClient = new HttpsClient(context);
            httpClient.setAcceptAllCertificate(true);
            httpClient.setBypassHostnameVerification(true);
            httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);
            httpClient.initialize();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        String clientId = oauthDetails.getClientId();
        String clientSecret = oauthDetails.getClientSecret();
        String scope = oauthDetails.getScope();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add(OAuthConstants.GRANT_TYPE,
                oauthDetails.getGrantType());
        formEncodingBuilder.add(OAuthConstants.USERNAME,
                oauthDetails.getUsername());
        formEncodingBuilder.add(OAuthConstants.PASSWORD,
                oauthDetails.getPassword());
        if (isValid(clientId)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_ID,
                    clientId);
        }
        if (isValid(clientSecret)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_SECRET, clientSecret);
        }
        if (isValid(scope)) {
            formEncodingBuilder.add(OAuthConstants.SCOPE,
                    scope);
        }

        RequestBody body = formEncodingBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(oauthDetails.getTokenEndPointUrl())
                .addHeader(HttpConnection.HEADER_CONTENT_TYPE_KEY, HttpConnection.HEADER_CONTENT_TYPE_URL_ENCODED);
        String loginId = oauthDetails.getUsername();
        String[] idNtenant = Tool.split(loginId, "@");
        if (idNtenant.length > 1)
            builder.addHeader(HEADER_TENANT, idNtenant[1]);
        builder.post(body);

        Request request = builder.build();

        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Token accessToken = null;
        if (response != null) {
            int code = response.code();
            if (code >= 400) {

                if(response.code() == 500){
                    OauthErrorResponse errorResponse = null;
                    try {
                        errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                        throw new RuntimeException(errorResponse.error_description);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (Global.IS_DEV) Log.i(TAG, "Authorization server expects Basic authentication");
                // Add Basic Authorization header
                builder.removeHeader(OAuthConstants.AUTHORIZATION);
                builder.addHeader(
                        OAuthConstants.AUTHORIZATION,
                        getBasicAuthorizationHeader(oauthDetails.getUsername(),
                                oauthDetails.getPassword()));
                if (Global.IS_DEV) Log.i(TAG, "Retry with login credentials");

                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                request = builder.build();
                try {
                    response = httpClient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                code = response.code();
                if (code >= 400) {
                    if (Global.IS_DEV) Log.i(TAG, "Retry with client credentials");
                    builder.removeHeader(OAuthConstants.AUTHORIZATION);
                    builder.addHeader(
                            OAuthConstants.AUTHORIZATION,
                            getBasicAuthorizationHeader(
                                    oauthDetails.getClientId(),
                                    oauthDetails.getClientSecret()));

                    try {
                        response.body().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    request = builder.build();
                    try {
                        response = httpClient.execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    code = response.code();
                    if (code >= 400) {
                        if (code == OAuthConstants.HTTP_BAD_REQUEST || code == OAuthConstants.HTTP_UNAUTHORIZED) {
                            OauthErrorResponse errorResponse = null;
                            try {
                                errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                                throw new RuntimeException(errorResponse.error_description);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        throw new RuntimeException(
                                "Could not retrieve access token for user: "
                                        + oauthDetails.getUsername());
                    }
                }

            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new RuntimeException(priorResponse.message());
            }
            Map<String, ?> map = handleResponse(response);
            accessToken = new Token(new Long((Integer) map.get(OAuthConstants.EXPIRES_IN)), (String) map.get(OAuthConstants.TOKEN_TYPE), (String) map.get(OAuthConstants.REFRESH_TOKEN), (String) map.get(OAuthConstants.ACCESS_TOKEN));

        } else {
            throw new RuntimeException(context.getString(R.string.connection_failed));
        }

        return accessToken;
    }

    public static Token refreshAccessTokenHttp(WeakReference<Activity> activity, Token token, OAuth2Config oauthDetails) {
        HttpClient httpClient = new HttpClient();
        httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);

        String clientId = oauthDetails.getClientId();
        String clientSecret = oauthDetails.getClientSecret();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add(OAuthConstants.GRANT_TYPE, "refresh_token");
        formEncodingBuilder.add(OAuthConstants.REFRESH_TOKEN, token.getRefreshToken());

        if (isValid(clientId)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_ID, clientId);
        }
        if (isValid(clientSecret)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_SECRET, clientSecret);
        }
        RequestBody body = formEncodingBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(oauthDetails.getTokenEndPointUrl())
                .addHeader(HttpConnection.HEADER_CONTENT_TYPE_KEY, HttpConnection.HEADER_CONTENT_TYPE_URL_ENCODED);
        String loginId = oauthDetails.getUsername();
        String[] idNtenant = Tool.split(loginId, "@");
        if (idNtenant.length > 1)
            builder.addHeader(HEADER_TENANT, idNtenant[1]);
        builder.post(body);

        Request request = builder.build();

        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Token accessToken = null;
        try {
            int code = response.code();
            if (code >= 400) {
                if (Global.IS_DEV) Log.i(TAG, "Retry with client credentials");
                builder.removeHeader(OAuthConstants.AUTHORIZATION);
                builder.addHeader(
                        OAuthConstants.AUTHORIZATION,
                        getBasicAuthorizationHeader(
                                oauthDetails.getClientId(),
                                oauthDetails.getClientSecret()));

                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                request = builder.build();
                try {
                    response = httpClient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                code = response.code();
                if (code >= 400) {
                    if (code == OAuthConstants.HTTP_BAD_REQUEST || code == OAuthConstants.HTTP_UNAUTHORIZED) {
                        OauthErrorResponse errorResponse = null;
                        try {
                            errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                            throw new InvalidTokenException(errorResponse.error_description);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    throw new InvalidTokenException(
                            "Could not retrieve access token for user: "
                                    + oauthDetails.getUsername());
                }
            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new InvalidTokenException(priorResponse.message());
            }
            Map<String, ?> map = handleResponse(response);
            accessToken = new Token(new Long((Integer) map.get(OAuthConstants.EXPIRES_IN)), (String) map.get(OAuthConstants.TOKEN_TYPE), (String) map.get(OAuthConstants.REFRESH_TOKEN), (String) map.get(OAuthConstants.ACCESS_TOKEN));
        } catch (InvalidTokenException e) {
            e.printStackTrace();
            throw new InvalidTokenException(e.getMessage());
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            throw new RuntimeException(activity.get().getString(R.string.connection_failed));
        }

        return accessToken;
    }

    public static Token refreshAccessTokenHttps(WeakReference<Activity> activity, Token token, OAuth2Config oauthDetails) {
        HttpsClient httpClient = null;
        try {
            httpClient = new HttpsClient();
            httpClient.setAcceptAllCertificate(true);
            httpClient.setBypassHostnameVerification(true);
            httpClient.setConnectionTimeout(Global.DEFAULTCONNECTIONTIMEOUT);
            httpClient.initialize();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        String clientId = oauthDetails.getClientId();
        String clientSecret = oauthDetails.getClientSecret();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add(OAuthConstants.GRANT_TYPE,
                "refresh_token");
        formEncodingBuilder.add(OAuthConstants.REFRESH_TOKEN,
                token.getRefreshToken());

        if (isValid(clientId)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_ID,
                    clientId);
        }
        if (isValid(clientSecret)) {
            formEncodingBuilder.add(OAuthConstants.CLIENT_SECRET, clientSecret);
        }
        RequestBody body = formEncodingBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(oauthDetails.getTokenEndPointUrl())
                .addHeader(HttpConnection.HEADER_CONTENT_TYPE_KEY, HttpConnection.HEADER_CONTENT_TYPE_URL_ENCODED);
        String loginId = oauthDetails.getUsername();
        String[] idNtenant = Tool.split(loginId, "@");
        if (idNtenant.length > 1)
            builder.addHeader(HEADER_TENANT, idNtenant[1]);
        builder.post(body);

        Request request = builder.build();

        Response response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Token accessToken = null;
        try {
            int code = response.code();
            if (code >= 400) {
                if (Global.IS_DEV) Log.i(TAG, "Retry with client credentials");
                builder.removeHeader(OAuthConstants.AUTHORIZATION);
                builder.addHeader(
                        OAuthConstants.AUTHORIZATION,
                        getBasicAuthorizationHeader(
                                oauthDetails.getClientId(),
                                oauthDetails.getClientSecret()));

                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                request = builder.build();
                try {
                    response = httpClient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                code = response.code();
                if (code >= 400) {
                    if (code == OAuthConstants.HTTP_BAD_REQUEST || code == OAuthConstants.HTTP_UNAUTHORIZED) {
                        OauthErrorResponse errorResponse = null;
                        try {
                            errorResponse = GsonHelper.fromJson(response.body().string(), OauthErrorResponse.class);
                            throw new InvalidTokenException(errorResponse.error_description);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    throw new InvalidTokenException(
                            "Could not retrieve access token for user: "
                                    + oauthDetails.getUsername());
                }
            }
            Response priorResponse = response.priorResponse();
            if (priorResponse != null && priorResponse.code() != HttpStatus.SC_OK) {
                throw new InvalidTokenException(priorResponse.message());
            }
            Map<String, ?> map = handleResponse(response);
            accessToken = new Token(new Long((Integer) map.get(OAuthConstants.EXPIRES_IN)), (String) map.get(OAuthConstants.TOKEN_TYPE), (String) map.get(OAuthConstants.REFRESH_TOKEN), (String) map.get(OAuthConstants.ACCESS_TOKEN));
        } catch (InvalidTokenException e) {
            e.printStackTrace();
            throw new InvalidTokenException(e.getMessage());
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            throw new RuntimeException(activity.get().getString(R.string.connection_failed));
        }

        return accessToken;
    }

    public static Token handleJsonResponseServer(Response response) {
        String result = null;
        try {
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Token token = GsonHelper.fromJson(result, Token.class);
            return token;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static Map handleResponse(Response response) {
        String contentType = OAuthConstants.JSON_CONTENT;
        if (response.body().contentType() != null) {
            contentType = response.body().contentType().toString();
            if (Global.IS_DEV) Log.i(TAG, contentType);

        }
        if (contentType.contains(OAuthConstants.JSON_CONTENT)) {
            return handleJsonResponse(response);
        } else if (contentType.contains(OAuthConstants.XML_CONTENT)) {
            return handleXMLResponse(response);
        } else {
            // Unsupported Content type
            throw new RuntimeException(
                    "Cannot handle "
                            + contentType
                            + " content type. Supported content types include JSON, and XML");
        }

    }

    public static Map handleJsonResponse(Response response) {
        JSONObject oauthLoginResponse = null;
        Map<String, Object> outMap = new HashMap<>();
        try {
            String result = response.body().string();
            oauthLoginResponse = new JSONObject(result);
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if(oauthLoginResponse == null){
            return outMap;
        }

        Iterator<String> keysIterator = oauthLoginResponse.keys();
        while (keysIterator.hasNext()) {
            String keyStr = keysIterator.next();
            Object value = null;
            try {
                value = oauthLoginResponse.get(keyStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            outMap.put(keyStr, value);
            if (Global.IS_DEV) Log.i(TAG, String.format("  %s = %s", keyStr, value));
        }

        return outMap;
    }

    public static Map<String, String> handleXMLResponse(Response response) {
        Map<String, String> oauthResponse = new HashMap<>();
        try {

            String xmlString = response.body().string();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            if (decrypt) {
                xmlString = SAKFormatter.decipherData(xmlString);
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(xmlString));
            Document doc = db.parse(inStream);

            if (Global.IS_DEV) Log.i(TAG, "********** XML Response Received **********");
            parseXMLDoc(null, doc, oauthResponse);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            throw new RuntimeException(
                    "Exception occurred while parsing XML response");
        }
        return oauthResponse;
    }

    public static void parseXMLDoc(Element element, Document doc,
                                   Map<String, String> oauthResponse) {
        NodeList child = null;
        if (element == null) {
            child = doc.getChildNodes();

        } else {
            child = element.getChildNodes();
        }
        for (int j = 0; j < child.getLength(); j++) {
            if (child.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element childElement = (Element) child
                        .item(j);
                if (childElement.hasChildNodes()) {
                    if (Global.IS_DEV) Log.i(TAG, childElement.getTagName() + " : " + childElement.getTextContent());
                    oauthResponse.put(childElement.getTagName(),
                            childElement.getTextContent());
                    parseXMLDoc(childElement, null, oauthResponse);
                }

            }
        }
    }

    public static String getAuthorizationHeaderForAccessToken(String accessToken) {
        return OAuthConstants.BEARER + " " + accessToken;
    }

    public static String getBasicAuthorizationHeader(String username,
                                                     String password) {
        return OAuthConstants.BASIC + " "
                + encodeCredentials(username, password);
    }

    public static String encodeCredentials(String username, String password) {
        String cred = username + ":" + password;
        String encodedValue = null;
        byte[] encodedBytes = Base64.encode(cred.getBytes(), Base64.NO_WRAP);
        encodedValue = new String(encodedBytes);
        if (Global.IS_DEV) Log.i(TAG, "encodedBytes " + new String(encodedBytes));

        byte[] decodedBytes = Base64.decode(encodedBytes, Base64.NO_WRAP);
        if (Global.IS_DEV) Log.i(TAG, "decodedBytes " + new String(decodedBytes));

        return encodedValue;

    }

    public static boolean isValid(String str) {
        return (str != null && str.trim().length() > 0);
    }

}
