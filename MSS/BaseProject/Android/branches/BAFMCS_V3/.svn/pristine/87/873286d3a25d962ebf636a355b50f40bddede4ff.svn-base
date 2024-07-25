package com.adins.mss.foundation.formatter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.fasterxml.uuid.Generators;
import com.gadberry.utility.expression.Expression;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@SuppressLint("DefaultLocale")
public class Tool {
    public static final String HEADER_CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String POST_METHOD = "POST";
    public static final int DEFAULT_KEY_LENGTH = 256;

    private static int DIGIT_NUMBER = 3;
    private static String DIGIT_DELIMETER = ".";

    public static String generateKey() {
        try {
            SecretKey secretKey = internalGenerateKey();
            return android.util.Base64.encodeToString(secretKey.getEncoded(), android.util.Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException ignored) {
            FireCrash.log(ignored);
        }
        return null;
    }

    private static SecretKey internalGenerateKey() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(DEFAULT_KEY_LENGTH, random);
        return keyGenerator.generateKey();
    }

    /**
     * @return UUI yang di generate secara random, as unique ID or primary key
     */
    public static String getUUID() {
        String uuid = Generators.timeBasedGenerator().generate().toString().toUpperCase();
        if (GlobalData.getSharedGlobalData().isUuidChange()) {
            uuid = uuid.replace("-", GlobalData.getSharedGlobalData().getUuidDivider());
        }
        return uuid;
    }

    public static StringBuilder inputStreamToString(InputStream is) throws IOException {
        String line = null;
        StringBuilder result = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        while ((line = br.readLine()) != null)
            result.append(line);

        return result;
    }

    public static String submitData(String postURI, String data) throws Exception {
        String result = null;

        byte[] bytesOfData = data.getBytes();
        String contentLength = String.valueOf(bytesOfData.length);

        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(postURI);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod(POST_METHOD);
            httpConn.setRequestProperty(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_URL_ENCODED);
            httpConn.setRequestProperty(HEADER_CONTENT_LENGTH, contentLength);
            httpConn.setConnectTimeout(2 * Global.MINUTE);
            httpConn.setReadTimeout(2 * Global.MINUTE);
            httpConn.connect();

            DataOutputStream dos = null;

            OutputStream outputStream = httpConn.getOutputStream();
            dos = new DataOutputStream(outputStream);
            dos.write(bytesOfData);
            dos.flush();
            dos.close();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream content = httpConn.getInputStream();
                result = Tool.inputStreamToString(content).toString();
            } else {
                throw new IOException("Connection to server failed: " + responseCode + " "
                        + httpConn.getResponseMessage());
            }
        } finally {
            if (httpConn != null)
                httpConn.disconnect();
        }

        return result;
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            String hex = null;
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);

            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] split(String original, String delimeter) {
        List<String> nodes = splitToVector(original, delimeter);

        String[] result = new String[nodes.size()];
        if (!nodes.isEmpty()) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = nodes.get(loop);
            }
        }

        return result;
    }

    public static List<String> splitToVector(String original, String delimeter) {
        List<String> nodes = new ArrayList<>();
        int index = original.indexOf(delimeter);

        while (index >= 0) {
            nodes.add(original.substring(0, index));
            original = original.substring(index + delimeter.length());
            index = original.indexOf(delimeter);
        }

        nodes.add(original);

        return nodes;
    }

    /**
     * Explode string <i>str</i> with specified delimiter to array of string
     *
     * @param str       String
     * @param delimiter String
     * @return String[]
     */
    public static String[] explode(String str, String delimiter) {
        StringTokenizer st = new StringTokenizer(str, delimiter);
        String[] result = new String[st.countTokens()];
        for (int i = 0; i < result.length; i++)
            result[i] = st.nextToken();
        return result;
    }

    /**
     * Convert array of string to string with specified delimiter
     *
     * @param arr       String[] Ex: new String[] {"VAL1", "VAL2", "VAL3"}
     * @param delimiter String
     * @return String Ex: "VAL1,VAL2,VAL3"
     */
    public static String implode(String[] arr, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0)
                result.append(delimiter);
            result.append(arr[i]);
        }
        return result.toString();
    }

    public static boolean gpsEnabled(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean hasBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null;
    }

    /**
     * Get current system date time
     *
     * @return Date
     */
    public static Date getSystemDateTime() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getIncrementDate(int daysToIncrement) {
        Calendar cal = Calendar.getInstance();
        if (Global.IS_DEV)
            System.out.println("Now : " + cal.getTime());
        cal.add(Calendar.DATE, daysToIncrement);
        if (Global.IS_DEV)
            System.out.println("Date after increment: " + cal.getTime());
        Date newDate = cal.getTime();
        return newDate;
    }

    /**
     * Get current system date (time part is removed/set to zero)
     *
     * @return Date
     */
    public static Date getSystemDate() {
        return truncTime(getSystemDateTime());
    }

    /**
     * Truncate time part (hour, minute, seconds, millis) from date
     *
     * @param date Date
     * @return Date
     */
    public static Date truncTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String appendZeroForDateTime(int i, boolean plusOne) {
        String result = null;
        if (i < 10) {
            if (plusOne)
                i++;
            if (i < 10)
                result = "0" + i;
            else result = String.valueOf(i);
        } else {
            if (plusOne)
                i++;
            result = String.valueOf(i);
        }
        return result;
    }

    public static int dpToPixel(float scale, int dps) {
        return (int) (dps * scale + 0.5f);
    }

    //Glen Iglesias, 3 July 2014, new converter for dp and pixels, without providing scale
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static boolean isEmptyString(String string) {
        return string == null || "".equals(string.trim());
    }

    /**
     * Method to check whether the specified argument is parsable to Integer
     *
     * @param num String
     * @return boolean
     */
    public static boolean isInteger(String num) {
        boolean result = true;
        try {
            Integer.parseInt(num);
        } catch (Exception e) {
            FireCrash.log(e);
            result = false;
        }
        return result;
    }

    public static double getAspectRatio(int width, int height) {
        return (double) width / (double)height;
    }

    public static String getErrorCode() {

        if (Global.IS_DEV) System.out.println("***getErrorCode");

        Date date = new Date();
        String errorCode = Reader.getKeyDate(date);
        try {
            String keyId = "USER_ID";
            if (Global.IS_DEV) System.out.println("keyId= " + keyId);
            char keyId2 = keyId.charAt(keyId.length() - 2);
            errorCode = errorCode + keyId2;
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV) System.out.println("error getErrorCode");
        }
        if (Global.IS_DEV) System.out.println("getErrorCode errorCode= " + errorCode);
        return errorCode;
    }

    //Glen 17 Oct 2014, new tool to get thousand separator
    public static String separateThousand(String text) {
        try {
            Double decimalNumber = Double.parseDouble(text);
            String result = Tool.separateThousand(decimalNumber);
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return text;
        }
    }

    public static String separateThousand(String text, boolean hasDecimal) {
        try {
            Double decimalNumber = Double.parseDouble(text);
            String result = Tool.separateThousand(decimalNumber, hasDecimal);
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return text;
        }
    }

    public static String separateThousand(double number) {
        String separatedAnswer = String.format(Locale.US, "%,.2f", number);
        return separatedAnswer;
    }

    public static String separateThousand(double number, boolean hasDecimal) {
        String separatedAnswer = (hasDecimal) ? String.format(Locale.US, "%,.2f", number) : String.format(Locale.US, "%,.0f", number);
        return separatedAnswer;
    }

    public static String formatToCurrency(double value){
        return String.format(Locale.US,"%,.0f",value).replace(",",".");
    }

    public static int[] getThumbnailResolution(int oriW, int oriH) {
        int[] resolution = new int[2];


        double widthRatio = (double)oriW / Global.THUMBNAIL_WIDTH;
        double heightRatio = (double)oriH / Global.THUMBNAIL_HEIGHT;
        double ratio = Math.max(widthRatio, heightRatio);
        if (ratio == 0) ratio = 1;
        resolution[0] = (int) Math.floor(oriW / ratio);
        resolution[1] = (int) Math.floor(oriH / ratio);

        return resolution;
    }


    public static boolean isImage(String answerType) {
        boolean val = false;

        if (answerType.equals(Global.AT_IMAGE) ||
                answerType.equals(Global.AT_IMAGE_W_LOCATION) ||
                answerType.equals(Global.AT_IMAGE_W_GPS_ONLY) ||
                answerType.equals(Global.AT_ID_CARD_PHOTO)

            //Glen 7 Oct 2014, new AT
//					answerType.equals(Global.AT_IMAGE_W_LOCATION_TIMESTAMP)||
//					answerType.equals(Global.AT_IMAGE_W_GPS_ONLY_TIMESTAMP)||
//					answerType.equals(Global.AT_IMAGE_W_TIMESTAMP)||

//					answerType.equals(Global.AT_DRAWING)
                ) {
            val = true;
        }

        return val;
    }

    public static boolean isHaveLocation(String answerType) {
        boolean val = false;

        if (answerType.equals(Global.AT_LOCATION) ||
                answerType.equals(Global.AT_IMAGE_W_LOCATION) ||
                answerType.equals(Global.AT_IMAGE_W_GPS_ONLY)) {
            val = true;
        }

        return val;
    }

    public static boolean isHaveImage(List<QuestionBean> listOfQuestions) {
        boolean hasImage = false;

        for (QuestionBean bean : listOfQuestions) {

            //Glen 22 Aug 2014, skip check if image is not shown
            if (!bean.isVisible()) continue;

            byte[] image = bean.getImgAnswer();
            if (image != null && image.length > 0)
                return true;
        }

        return hasImage;
    }

    public static int countSelectedOption(List<OptionAnswerBean> listOptions) {
        int count = 0;

        for (OptionAnswerBean bean : listOptions) {
            if (bean.isSelected()) {
                count++;
            }
        }

        return count;
    }

    public static int getSelectedIndex(List<OptionAnswerBean> listOptions) {
        int selected = -1;

        int i = 0;
        for (OptionAnswerBean bean : listOptions) {
            if (bean.isSelected()) {
                selected = i;
                break;
            }
            i++;
        }

        return selected;
    }

    public static boolean isOptions(String answerType) {
        boolean isOptions = false;

        if (answerType.equals(Global.AT_MULTIPLE)
                || answerType.equals(Global.AT_MULTIPLE_W_DESCRIPTION)
                || answerType.equals(Global.AT_MULTIPLE_ONE_DESCRIPTION)
                || answerType.equals(Global.AT_RADIO)
                || answerType.equals(Global.AT_RADIO_W_DESCRIPTION)
                || answerType.equals(Global.AT_DROPDOWN)
                || answerType.equals(Global.AT_DROPDOWN_W_DESCRIPTION)) {
            isOptions = true;
        }

        return isOptions;
    }

    public static boolean isOptionsWithDescription(String answerType) {
        boolean isOptions = false;

        if (answerType.equals(Global.AT_MULTIPLE_W_DESCRIPTION)
                || answerType.equals(Global.AT_RADIO_W_DESCRIPTION)
            //|| answerType.equals(Global.AT_DROPDOWN_W_DESCRIPTION)
                ) {
            isOptions = true;
        }

        return isOptions;
    }

    public static QuestionBean getBeanWithIdentifier(String identifier, List<QuestionBean> beans) {
        QuestionBean qBean = null;
        for (QuestionBean bean : beans) {
            String identifier_name = bean.getIdentifier_name();
            if (identifier.equalsIgnoreCase(identifier_name)) {
                qBean = bean;
                break;
            }
        }
        return qBean;
    }

    public static boolean isVisibleByRelevant(String relevant,
                                              QuestionBean qBean, List<QuestionBean> listQuestionBean) {
        boolean result = false;
        String convertedExpression = relevant;
        if (convertedExpression == null || convertedExpression.length() == 0) return true;
        else {
            boolean needReplacing = true;
            while (needReplacing) {
                // replace application modifier
                convertedExpression = replaceModifiers(convertedExpression);
                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    String answers[] = Tool.replaceFirstModifier(qBean, listQuestionBean, true);
                    if (answers.length == 1) convertedExpression = answers[0];
                    else {
                        //NOTE: going into in-depth loop, won't go outside of this 'else'
                        for (int i = 0; i < answers.length; i++) {
                            String convertedSubExpression = answers[i];
                            boolean isVisible = Tool.isVisibleByRelevant(convertedSubExpression, qBean, listQuestionBean);
                            if (isVisible) return true;
                        }
                        return false;
                    }
                } else needReplacing = false;
            }
        }
        try {
            result = Expression.evaluate(convertedExpression).toBoolean();
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return false;
        }
    }

    //=== Question Relevant ===//
    public static String replaceModifiers(String convertedExpression) {
        // replace branch modifier
        String branch = GlobalData.getSharedGlobalData().getUser().getBranch_id();
        // replace user modifier
        String user = GlobalData.getSharedGlobalData().getUser().getLogin_id();

        return convertedExpression.replace(QuestionBean.PLACEMENT_KEY_BRANCH, branch).replace(QuestionBean.PLACEMENT_KEY_USER, user);
    }

    public static String[] replaceFirstModifier(QuestionBean qBean,
                                                List<QuestionBean> listQuestionBean, boolean saveAffectedBean) {
        String relevant = qBean.getRelevant_question();
        String convertedExpression = relevant;

        int idxOfOpenBrace = convertedExpression.indexOf('{');
        if (idxOfOpenBrace != -1) {
            int idxOfCloseBrace = convertedExpression.indexOf('}');
            String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
            QuestionBean bean = Tool.getBeanWithIdentifier(identifier, listQuestionBean);
            if (bean != null) {
                if (!bean.isVisible()) return new String[0];
                String flatAnswer = QuestionBean.getAnswer(bean);
                if (flatAnswer != null && flatAnswer.length() > 0) {
                    String answers[] = Tool.split(flatAnswer, Global.DELIMETER_DATA);
                    String replacedExpression[] = new String[answers.length];
                    for (int i = 0; i < answers.length; i++) {
                        String convertedSubExpression = convertedExpression.replace("{" + identifier + "}", answers[i]);
                        replacedExpression[i] = convertedSubExpression;
                    }
                    if (saveAffectedBean) bean.addToAffectedQuestionBeanCalculation(qBean);

                    return replacedExpression;
                } else    //if there's no answer, just hide the question
                    return new String[0];
            } else {
                String replacedExpressions[] = new String[1];
                replacedExpressions[0] = convertedExpression.replace("{" + identifier + "}", "\"\"");
                return replacedExpressions;
            }
        }
        return new String[0];
    }

    public static String deleteAll(String strValue, String charToRemove) {
        return strValue.replaceAll(charToRemove, "");

    }

    public static String getNumericDigit(String value) {

        String result = null;
        String numString = value;
        int numLenght = numString.length();

        int resultDivide = numLenght / DIGIT_NUMBER;
        int resultMod = numLenght % DIGIT_NUMBER;

        if (Global.IS_DEV) {
            System.out.println("resultDivide= " + resultDivide);
            System.out.println("resultMod= " + resultMod);
        }
        if (resultMod == 0) {
            resultDivide = resultDivide - 1;
        }

        result = getStringDelimeter(numString, DIGIT_DELIMETER, resultDivide);

        return result;

    }

    private static String getStringDelimeter(String numString, String delimeter, int resultDivide) {

        StringBuffer sb = new StringBuffer();
        int begin = 0;
        int beginSubs = 0;
        for (int i = resultDivide; i >= 0; i--) {

            begin = numString.length() - DIGIT_NUMBER + 1;

            beginSubs = begin - 1;

            if (beginSubs <= 0) {
                beginSubs = 0;
            }

            if (beginSubs == 0) {
                sb.insert(0, numString.substring(beginSubs, numString.length()));
            } else {
                sb.insert(0, "." + numString.substring(beginSubs, numString.length()));
            }

            if (i > 0) {
                numString = numString.substring(0, beginSubs);
            }

        }

        return sb.toString();
    }

    public static boolean isInternetconnected(Context ct) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities nCap = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    if (nCap != null) {
                        return nCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    }
                    else {
                        return false;
                    }
                }
                else {
                    final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null) {
                        return networkInfo.isConnected();
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return false;
        }
    }

    public static void openPdfFile(Activity activity, String filename) {
        filename = "/" + activity.getString(R.string.app_name) + "/" + filename;
        File file = new File(activity.getApplicationContext().getExternalFilesDir(null) + filename);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.no_pdf_viewer), Toast.LENGTH_SHORT).show();
        }
    }

    public static void CopyAssetsPDF(Activity activity, String filename) {
        AssetManager assetManager = activity.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Logger.e("tag", e.getMessage());
        }
        if(files != null) {
            for (String file : files) {
                if (file.equalsIgnoreCase(filename)) {
                    try (InputStream in = assetManager.open(file);
                         OutputStream out = new FileOutputStream(activity.getApplicationContext().getExternalFilesDir(null) + "/" + activity.getString(R.string.app_name) + "/" + file)) {
                        copyFile(in, out);
                        out.flush();
                        break;
                    } catch (Exception e) {
                        FireCrash.log(e);
                        Logger.e("tag", e.getMessage());
                    }
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void installProvider(Context context){
        try {
            ProviderInstaller.installIfNeeded(context);
        } catch (GooglePlayServicesRepairableException e) {
            FireCrash.log(e);
        } catch (GooglePlayServicesNotAvailableException e) {
            FireCrash.log(e);
        }
    }
}