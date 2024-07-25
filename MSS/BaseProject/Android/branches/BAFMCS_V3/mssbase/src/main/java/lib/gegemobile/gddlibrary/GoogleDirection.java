package lib.gegemobile.gddlibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GoogleDirection {
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";
    public final static String MODE_BICYCLING = "bicycling";

    public final static String STATUS_OK = "OK";
    public final static String STATUS_NOT_FOUND = "NOT_FOUND";
    public final static String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public final static String STATUS_MAX_WAYPOINTS_EXCEEDED = "MAX_WAYPOINTS_EXCEEDED";
    public final static String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
    public final static String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public final static String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public final static String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";

    public final static int SPEED_VERY_FAST = 1;
    public final static int SPEED_FAST = 2;
    public final static int SPEED_NORMAL = 3;
    public final static int SPEED_SLOW = 4;
    public final static int SPEED_VERY_SLOW = 5;

    private OnDirectionResponseListener mDirectionListener = null;
    private OnAnimateListener mAnimateListener = null;

    private boolean isLogging = false;

    private LatLng animateMarkerPosition = null;
    private LatLng beginPosition = null;
    private LatLng endPosition = null;
    private ArrayList<LatLng> animatePositionList = null;
    private Marker animateMarker = null;
    private Polyline animateLine = null;
    private GoogleMap gm = null;
    private int step = -1;
    private int animateSpeed = -1;
    private int zoom = -1;
    private double animateDistance = -1;
    private double animateCamera = -1;
    private double totalAnimateDistance = 0;
    private boolean cameraLock = false;
    private boolean drawMarker = false;
    private boolean drawLine = false;
    private boolean flatMarker = false;
    private boolean isCameraTilt = false;
    private boolean isCameraZoom = false;
    private boolean isAnimated = false;

    private Context mContext = null;
    private Runnable r = new Runnable() {
        public void run() {

            animateMarkerPosition = getNewPosition(animateMarkerPosition, endPosition);

            if (drawMarker)
                animateMarker.setPosition(animateMarkerPosition);


            if (drawLine) {
                List<LatLng> points = animateLine.getPoints();
                points.add(animateMarkerPosition);
                animateLine.setPoints(points);
            }

            if ((animateMarkerPosition.latitude == endPosition.latitude
                    && animateMarkerPosition.longitude == endPosition.longitude)) {
                if (step == animatePositionList.size() - 2) {
                    isAnimated = false;
                    totalAnimateDistance = 0;
                    if (mAnimateListener != null)
                        mAnimateListener.onFinish();
                } else {
                    step++;
                    beginPosition = animatePositionList.get(step);
                    endPosition = animatePositionList.get(step + 1);
                    animateMarkerPosition = beginPosition;

                    if (flatMarker && step + 3 < animatePositionList.size() - 1) {
                        float rotation = getBearing(animateMarkerPosition, animatePositionList.get(step + 3)) + 180;
                        animateMarker.setRotation(rotation);
                    }

                    if (mAnimateListener != null)
                        mAnimateListener.onProgress(step, animatePositionList.size());
                }
            }

            if (cameraLock && (totalAnimateDistance > animateCamera || !isAnimated)) {
                totalAnimateDistance = 0;
                float bearing = getBearing(beginPosition, endPosition);
                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(animateMarkerPosition).bearing(bearing);

                if (isCameraTilt)
                    cameraBuilder.tilt(90);
                else
                    cameraBuilder.tilt(gm.getCameraPosition().tilt);

                if (isCameraZoom)
                    cameraBuilder.zoom(zoom);
                else
                    cameraBuilder.zoom(gm.getCameraPosition().zoom);

                CameraPosition cameraPosition = cameraBuilder.build();
                gm.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            if (isAnimated) {
                new Handler().postDelayed(r, animateSpeed);
            }
        }
    };

    public GoogleDirection(Context context) {
        mContext = context;
    }

    public String request(LatLng start, LatLng end, String mode, boolean isAvoidTools) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude);

        if (isAvoidTools)
            sb.append("&avoid=tolls");

        sb.append("&sensor=false&units=metric&mode=" + mode);

        final String url = sb.toString();
        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(new String[]{url});
        return url;
    }

    public String request(LatLng start, LatLng end, List<LatLng> waypoints,
                          String mode, boolean isAvoidTools) {
        //avoid=tolls
        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude);

        if (waypoints.size() > 0) {
            sb.append("&waypoints=optimize:true");
            StringBuffer sbwpoint = new StringBuffer();
            sbwpoint.append("|");
            for (int i = 0; i < waypoints.size(); i++) {
                LatLng ltg = waypoints.get(i);
                sbwpoint.append(ltg.latitude + "," + ltg.longitude);
                if (i != waypoints.size() - 1)
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

        sb.append("&sensor=false&units=metric&mode=" + mode);
        final String url = sb.toString();

        if (isLogging)
            Log.i("GoogleDirection", "URL : " + url);
        new RequestTask().execute(new String[]{url});
        return url;
    }

    public void setLogging(boolean state) {
        isLogging = state;
    }

    public String getStatus(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("status");
        Node node1 = nl1.item(0);
        if (isLogging)
            Log.i("GoogleDirection", "Status : " + node1.getTextContent());
        return node1.getTextContent();
    }

    public String[] getDurationText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        String[] arr_str = new String[nl1.getLength() - 1];
        for (int i = 0; i < nl1.getLength() - 1; i++) {
            Node node1 = nl1.item(i);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            arr_str[i] = node2.getTextContent();
            if (isLogging)
                Log.i("GoogleDirection", "DurationText : " + node2.getTextContent());
        }
        return arr_str;
    }

    public int[] getDurationValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        int[] arr_int = new int[nl1.getLength() - 1];
        for (int i = 0; i < nl1.getLength() - 1; i++) {
            Node node1 = nl1.item(i);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            arr_int[i] = Integer.parseInt(node2.getTextContent());
            if (isLogging)
                Log.i("GoogleDirection", "Duration : " + node2.getTextContent());
        }
        return arr_int;
    }

    public int[] getDurationLegsValue(Document doc) {
        NodeList nl0 = doc.getElementsByTagName("leg");
        int[] arr_int0 = new int[nl0.getLength()];
        for (int j = 0; j < nl0.getLength(); j++) {
            Node node0 = nl0.item(j);
            NodeList nl01 = node0.getChildNodes();
            Node nl1 = nl01.item(getNodeIndex(nl01, "duration"));
            NodeList nl2 = nl1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            arr_int0[j] = Integer.parseInt(node2.getTextContent());
        }

        return arr_int0;
    }

    public int[] getDistanceLegsValue(Document doc) {
        NodeList nl0 = doc.getElementsByTagName("leg");
        int[] arr_int0 = new int[nl0.getLength()];
        for (int j = 0; j < nl0.getLength(); j++) {
            Node node0 = nl0.item(j);
            NodeList nl01 = node0.getChildNodes();
            Node nl1 = nl01.item(getNodeIndex(nl01, "distance"));
            NodeList nl2 = nl1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            arr_int0[j] = Integer.parseInt(node2.getTextContent());
        }

        return arr_int0;
    }

    public String getAllTotalDistanceText(Document doc) {
        int[] alldistance = getDistanceLegsValue(doc);
        int sum = 0;
        for (int i = 0; i < alldistance.length; i++) {
            sum += alldistance[i];
        }

        double sec = sum / 1000;
        int margin = sum % 1000;
        double total = sec + Double.parseDouble("0." + margin);
        String time = " : " + total + " km";
        return time;
    }

    public String getAllTotalDurationText(Document doc) {
        int[] allduration = getDurationLegsValue(doc);
        int sum = 0;
        for (int i = 0; i < allduration.length; i++) {
            sum += allduration[i];
        }

        int count = sum % 60;
        if (count > 30) sum += 60;

        int sec = sum / 60;
        String time = " : " + sec + " min";
        return time;
    }


    public String getTotalDurationText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        if (isLogging)
            Log.i("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return node2.getTextContent();
    }

    public int getTotalDurationValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        if (isLogging)
            Log.i("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String[] getDistanceText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        String[] arr_str = new String[nl1.getLength() - 1];
        for (int i = 0; i < nl1.getLength() - 1; i++) {
            Node node1 = nl1.item(i);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            arr_str[i] = node2.getTextContent();
            if (isLogging)
                Log.i("GoogleDirection", "DurationText : " + node2.getTextContent());
        }
        return arr_str;
    }

    public int[] getDistanceValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        int[] arr_int = new int[nl1.getLength() - 1];
        for (int i = 0; i < nl1.getLength() - 1; i++) {
            Node node1 = nl1.item(i);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            arr_int[i] = Integer.parseInt(node2.getTextContent());
            if (isLogging)
                Log.i("GoogleDirection", "Duration : " + node2.getTextContent());
        }
        return arr_int;
    }

    public int[] getWaypointOrder(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("waypoint_index");
        int[] arr_int = new int[nl1.getLength()];
        for (int i = 0; i < nl1.getLength(); i++) {
            Node node1 = nl1.item(i);
            arr_int[i] = Integer.parseInt(node1.getTextContent());
            if (isLogging)
                Log.i("GoogleDirection", "Waypoint : " + node1.getTextContent());
        }
        return arr_int;
    }

    public String getTotalDistanceText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        if (isLogging)
            Log.i("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return node2.getTextContent();
    }

    public int getTotalDistanceValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        if (isLogging)
            Log.i("GoogleDirection", "TotalDuration : " + node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getStartAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        if (isLogging)
            Log.i("GoogleDirection", "StartAddress : " + node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        if (isLogging)
            Log.i("GoogleDirection", "StartAddress : " + node1.getTextContent());
        return node1.getTextContent();
    }

    public String getCopyRights(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        if (isLogging)
            Log.i("GoogleDirection", "CopyRights : " + node1.getTextContent());
        return node1.getTextContent();
    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude
                            , arr.get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    public ArrayList<LatLng> getSection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    public PolylineOptions getPolyline(Document doc, int width, int color) {
        ArrayList<LatLng> arr_pos = getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(dpToPx(width)).color(color);
        for (int i = 0; i < arr_pos.size(); i++)
            rectLine.add(arr_pos.get(i));
        return rectLine;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void setOnDirectionResponseListener(OnDirectionResponseListener listener) {
        mDirectionListener = listener;
    }

    public void setOnAnimateListener(OnAnimateListener listener) {
        mAnimateListener = listener;
    }

    public void animateDirection(GoogleMap gm, ArrayList<LatLng> direction, int speed
            , boolean cameraLock, boolean isCameraTilt, boolean isCameraZoom
            , boolean drawMarker, MarkerOptions mo, boolean flatMarker
            , boolean drawLine, PolylineOptions po) {
        if (direction.size() > 1) {
            isAnimated = true;
            animatePositionList = direction;
            animateSpeed = speed;
            this.drawMarker = drawMarker;
            this.drawLine = drawLine;
            this.flatMarker = flatMarker;
            this.isCameraTilt = isCameraTilt;
            this.isCameraZoom = isCameraZoom;
            step = 0;
            this.cameraLock = cameraLock;
            this.gm = gm;

            setCameraUpdateSpeed(speed);

            beginPosition = animatePositionList.get(step);
            endPosition = animatePositionList.get(step + 1);
            animateMarkerPosition = beginPosition;

            if (mAnimateListener != null)
                mAnimateListener.onProgress(step, animatePositionList.size());

            if (cameraLock) {
                float bearing = getBearing(beginPosition, endPosition);
                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(animateMarkerPosition).bearing(bearing);

                if (isCameraTilt)
                    cameraBuilder.tilt(90);
                else
                    cameraBuilder.tilt(gm.getCameraPosition().tilt);

                if (isCameraZoom)
                    cameraBuilder.zoom(zoom);
                else
                    cameraBuilder.zoom(gm.getCameraPosition().zoom);

                CameraPosition cameraPosition = cameraBuilder.build();
                gm.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if (drawMarker) {
                if (mo != null)
                    animateMarker = gm.addMarker(mo.position(beginPosition));
                else
                    animateMarker = gm.addMarker(new MarkerOptions().position(beginPosition));

                if (flatMarker) {
                    animateMarker.setFlat(true);

                    float rotation = getBearing(animateMarkerPosition, endPosition) + 180;
                    animateMarker.setRotation(rotation);
                }
            }


            if (drawLine) {
                if (po != null)
                    animateLine = gm.addPolyline(po.add(beginPosition)
                            .add(beginPosition).add(endPosition)
                            .width(dpToPx((int) po.getWidth())));
                else
                    animateLine = gm.addPolyline(new PolylineOptions()
                            .width(dpToPx(5)));
            }

            new Handler().postDelayed(r, speed);
            if (mAnimateListener != null)
                mAnimateListener.onStart();
        }
    }

    public void cancelAnimated() {
        isAnimated = false;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public Marker getAnimateMarker() {
        return animateMarker;
    }

    public Polyline getAnimatePolyline() {
        return animateLine;
    }

    private LatLng getNewPosition(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        double dis = Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));
        if (dis >= animateDistance) {
            double angle = -1;

            if (begin.latitude <= end.latitude && begin.longitude <= end.longitude)
                angle = Math.toDegrees(Math.atan(lng / lat));
            else if (begin.latitude > end.latitude && begin.longitude <= end.longitude)
                angle = (90 - Math.toDegrees(Math.atan(lng / lat))) + 90;
            else if (begin.latitude > end.latitude && begin.longitude > end.longitude)
                angle = Math.toDegrees(Math.atan(lng / lat)) + 180;
            else if (begin.latitude <= end.latitude && begin.longitude > end.longitude)
                angle = (90 - Math.toDegrees(Math.atan(lng / lat))) + 270;

            double x = Math.cos(Math.toRadians(angle)) * animateDistance;
            double y = Math.sin(Math.toRadians(angle)) * animateDistance;
            totalAnimateDistance += animateDistance;
            double finalLat = begin.latitude + x;
            double finalLng = begin.longitude + y;

            return new LatLng(finalLat, finalLng);
        } else {
            return end;
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public void setCameraUpdateSpeed(int speed) {
        if (speed == SPEED_VERY_SLOW) {
            animateDistance = 0.000005;
            animateSpeed = 20;
            animateCamera = 0.0004;
            zoom = 19;
        } else if (speed == SPEED_SLOW) {
            animateDistance = 0.00001;
            animateSpeed = 20;
            animateCamera = 0.0008;
            zoom = 18;
        } else if (speed == SPEED_NORMAL) {
            animateDistance = 0.00005;
            animateSpeed = 20;
            animateCamera = 0.002;
            zoom = 16;
        } else if (speed == SPEED_FAST) {
            animateDistance = 0.0001;
            animateSpeed = 20;
            animateCamera = 0.004;
            zoom = 15;
        } else if (speed == SPEED_VERY_FAST) {
            animateDistance = 0.0005;
            animateSpeed = 20;
            animateCamera = 0.004;
            zoom = 13;
        } else {
            animateDistance = 0.00005;
            animateSpeed = 20;
            animateCamera = 0.002;
            zoom = 16;
        }
    }

    public interface OnDirectionResponseListener {
        public void onResponse(String status, Document doc, GoogleDirection gd);
    }

    public interface OnAnimateListener {
        public void onFinish();

        public void onStart();

        public void onProgress(int progress, int total);
    }

    private class RequestTask extends AsyncTask<String, Void, Document> {
        protected Document doInBackground(String... url) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url[0]);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                InputStream in = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                return builder.parse(in);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Document doc) {
            super.onPostExecute(doc);
            if (mDirectionListener != null)
                mDirectionListener.onResponse(getStatus(doc), doc, GoogleDirection.this);
        }

        private String getStatus(Document doc) {
            NodeList nl1 = doc.getElementsByTagName("status");
            Node node1 = nl1.item(0);
            if (isLogging)
                Log.i("GoogleDirection", "Status : " + node1.getTextContent());
            return node1.getTextContent();
        }
    }
}
