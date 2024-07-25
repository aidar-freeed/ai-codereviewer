package lib.gegemobile.gddlibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class GoogleDistanceMatrix {

    private boolean isLogging = false;
    private OnDistanceResponseListener mDistanceListener = null;
    private Context mContext = null;

    public GoogleDistanceMatrix(Context context) {
        this.mContext = context;
    }

    public String request(LatLng start, LatLng end, String mode, boolean isAvoidTools) {
        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/distancematrix/json?"
                + "origins=" + start.latitude + "," + start.longitude
                + "&destinations=" + end.latitude + "," + end.longitude);

        if (isAvoidTools)
            sb.append("&avoid=tolls");

        sb.append("&mode=" + mode);
        final String url = sb.toString();
        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(url);
        return url;
    }

    public String request(List<LatLng> starts, LatLng destinations,
                          String mode, boolean isAvoidTools) {

        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/distancematrix/json?");

        if (starts.size() > 0) {
            sb.append("origins=");
            StringBuffer sbwpoint = new StringBuffer();
            for (int i = 0; i < starts.size(); i++) {
                LatLng ltg = starts.get(i);
                sbwpoint.append(ltg.latitude + "," + ltg.longitude);
                if (i != starts.size() - 1)
                    sbwpoint.append("|");
            }
            try {
                sb.append(URLEncoder.encode(sbwpoint.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        sb.append("&destinations=" + destinations.latitude + "," + destinations.longitude);

        if (isAvoidTools)
            sb.append("&avoid=tolls");

        sb.append("&mode=" + mode);
        final String url = sb.toString();

        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(url);
        return url;
    }

    public String request(List<LatLng> starts, List<LatLng> destinations,
                          String mode, boolean isAvoidTools) {

        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/distancematrix/json?");

        if (starts.size() > 0) {
            sb.append("origins=");
            StringBuffer sbwpoint = new StringBuffer();
            for (int i = 0; i < starts.size(); i++) {
                LatLng ltg = starts.get(i);
                sbwpoint.append(ltg.latitude + "," + ltg.longitude);
                if (i != starts.size() - 1)
                    sbwpoint.append("|");
            }
            try {
                sb.append(URLEncoder.encode(sbwpoint.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (destinations.size() > 0) {
            sb.append("&destinations=");
            StringBuffer sbwpoint = new StringBuffer();
            for (int i = 0; i < destinations.size(); i++) {
                LatLng ltg = destinations.get(i);
                sbwpoint.append(ltg.latitude + "," + ltg.longitude);
                if (i != destinations.size() - 1)
                    sbwpoint.append("|");
            }
            try {
                sb.append(URLEncoder.encode(sbwpoint.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (isAvoidTools)
            sb.append("&avoid=tolls");

        sb.append("&mode=" + mode);
        final String url = sb.toString();

        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(url);
        return url;
    }

    public String request(LatLng start, List<LatLng> destinations,
                          String mode, boolean isAvoidTools) {

        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/distancematrix/json?"
                + "origins=" + start.latitude + "," + start.longitude);

        if (destinations.size() > 0) {
            sb.append("&destinations=");
            StringBuffer sbwpoint = new StringBuffer();
            for (int i = 0; i < destinations.size(); i++) {
                LatLng ltg = destinations.get(i);
                sbwpoint.append(ltg.latitude + "," + ltg.longitude);
                if (i != destinations.size() - 1)
                    sbwpoint.append("|");
            }
            try {
                sb.append(URLEncoder.encode(sbwpoint.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (isAvoidTools)
            sb.append("&avoid=tolls");

        sb.append("&mode=" + mode);
        final String url = sb.toString();

        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(url);
        return url;
    }

    public String getDistanceText(DistanceResponseJson json, int startIndex, int endIndex) {
        String distance = "";

        Row row = json.rows.get(startIndex);
        Element element = row.elements.get(endIndex);
        distance = element.distance.text;
        return distance;
    }

    public int getDistanceValue(DistanceResponseJson json, int startIndex, int endIndex) {
        int distance = 0;

        Row row = json.rows.get(startIndex);
        Element element = row.elements.get(endIndex);
        distance = element.distance.value;
        return distance;
    }

    public String getDurationText(DistanceResponseJson json, int startIndex, int endIndex) {
        String distance = "";

        Row row = json.rows.get(startIndex);
        Element element = row.elements.get(endIndex);
        distance = element.duration.text;
        return distance;
    }

    public int getDurationValue(DistanceResponseJson json, int startIndex, int endIndex) {
        int distance = 0;

        Row row = json.rows.get(startIndex);
        Element element = row.elements.get(endIndex);
        distance = element.duration.value;
        return distance;
    }

    public List<Row> getRowsElement(DistanceResponseJson json) {
        return json.rows;
    }

    public void setLogging(boolean state) {
        isLogging = state;
    }

    public void setOnDistanceResponseListener(OnDistanceResponseListener listener) {
        mDistanceListener = listener;
    }

    public interface OnDistanceResponseListener {
        void onResponse(String status, DistanceResponseJson json, GoogleDistanceMatrix gd);
    }

    private class RequestTask extends AsyncTask<String, Void, DistanceResponseJson> {
        protected DistanceResponseJson doInBackground(String... url) {
            HttpConnectionResult resultServer = null;
            String result = "";
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(encrypt, decrypt);
            try {
                resultServer = httpConn.requestToServer(url[0], "", Global.DEFAULTCONNECTIONTIMEOUT);
//				HttpClient httpClient = new DefaultHttpClient();
//				HttpContext localContext = new BasicHttpContext();
//				HttpPost httpPost = new HttpPost(url[0]);
//				HttpResponse response = httpClient.execute(httpPost, localContext);
//				result = EntityUtils.toString(response.getEntity());
                result = resultServer.getResult();
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            DistanceResponseJson response = null;
            try {
                response = GsonHelper.fromJson(result, DistanceResponseJson.class);
            } catch (Exception e) {
                FireCrash.log(e);
                // TODO: handle exception
            }
            return response;
        }

        protected void onPostExecute(DistanceResponseJson json) {
            super.onPostExecute(json);
            if (mDistanceListener != null)
                mDistanceListener.onResponse(json.status, json, GoogleDistanceMatrix.this);
        }

    }

    public class Distance {
        public String text;
        public int value;
    }

    public class Duration {
        public String text;
        public int value;
    }

    public class Element {
        public Distance distance;
        public Duration duration;
        public String status;
    }

    public class Row {
        public List<Element> elements;
    }

    public class DistanceResponseJson {
        public List<String> destination_addresses;
        public List<String> origin_addresses;
        public List<Row> rows;
        public String status;
    }
}
