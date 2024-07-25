package com.adins.mss.foundation.formatter;

import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Formatter is a helper class for conversion of numeric and date types to/from String type.
 * The class internally use ThreadLocal object for returning the same number/date formatter instance
 * for the same thread and number/date Sformat <p>
 * Example:<br>
 * <code><pre>
 * DateFormat formatter = Formatter.getDateFormat(Formatter.DFORMAT_DMY); <br>
 * for (int i=0; i < xxx; i++) { <br>
 *    // use 'formatter' <br>
 * } <br>
 * </pre></code>
 * Inner class NumberFormatType is used for specifying number format
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: AdIns</p>
 *
 * @author Andri Gunardi
 * @version 1.0
 */
public class Formatter {
    public static final String DFORMAT_YMD = "yyyyMMdd";
    public static final String DFORMAT_DMY = "ddMMyyyy";

    public static final NumberFormatType NFORMAT_ID_0 = new NumberFormatType('.', ',', 0);
    public static final NumberFormatType NFORMAT_ID_2 = new NumberFormatType('.', ',', 2);
    public static final NumberFormatType NFORMAT_US_0 = new NumberFormatType(',', '.', 0);
    public static final NumberFormatType NFORMAT_US_2 = new NumberFormatType(',', '.', 2);
    public static final NumberFormatType NFORMAT_PLAIN = new NumberFormatType(',', '.', 0, false);

    private static ThreadLocal numberFormatPool = new ThreadLocal();
    private static ThreadLocal dateFormatPool = new ThreadLocal();


    private static String gigabyteSuffix = " Gigabytes";
    private static String megabyteSuffix = " Megabytes";
    private static String kilobyteSuffix = " Kilobytes";
    private static String byteSuffix = " Bytes";
    private static NumberFormat nf = new DecimalFormat("#,###.##");


    /*********************************************************/
    /*********************************************************/


    private Formatter() {
    }

    /**
     * Returns NumberFormat instance for the specified argument 'type'.
     * NumberFormat instance is cache per thread (using ThreadLocal) and per format,
     * so the same thread requesting the same formatType will get the same NumberFormat instance.
     *
     * @param formatType
     * @return NumberFormat
     */
    public static NumberFormat getNumberFormat(NumberFormatType formatType) {
        NumberFormat result = null;
        HashMap numberFormats = (HashMap) numberFormatPool.get();
        if (numberFormats == null) {
            numberFormats = new HashMap<>();
            numberFormatPool.set(numberFormats);
        } else {
            result = (NumberFormat) numberFormats.get(formatType);
        }
        if (result == null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(formatType.thousandSeparator);
            symbols.setDecimalSeparator(formatType.decimalSeparator);
            DecimalFormat dFormat = new DecimalFormat("###,###,###,###,###,###.##", symbols);
            dFormat.setMinimumFractionDigits(formatType.decimal);
            dFormat.setMaximumFractionDigits(formatType.decimal);
            dFormat.setGroupingSize(formatType.showThousandSeparator ? 3 : 0);
            dFormat.setDecimalSeparatorAlwaysShown(formatType.decimal > 0);
            numberFormats.put(formatType, dFormat);
            result = dFormat;
        }

        return result;
    }

    /**
     * Returns (Simple)DateFormat instance for the specified format argument
     * DateFormat instance is cache per thread (using ThreadLocal) and per format,
     * so the same thread requesting the same date format will get the same DateFormat instance.
     *
     * @param format String
     * @return DateFormat
     */
    public static DateFormat getDateFormat(String format) {
        DateFormat result = null;
        HashMap dateFormats = (HashMap) dateFormatPool.get();
        if (dateFormats == null) {
            dateFormats = new HashMap<>();
            dateFormatPool.set(dateFormats);
        } else {
            result = (DateFormat) dateFormats.get(format);
        }
        if (result == null) {
            result = new SimpleDateFormat(format);
            dateFormats.put(format, result);
        }
        return result;
    }

    /**
     * Format specified java.util.Date date using format
     *
     * @param date   Date
     * @param format String
     * @return String
     */
    public static String formatDate(Date date, String format) {
        return getDateFormat(format).format(date);
    }

    /**
     * Format specified java.util.Date dt using formatter to text String
     *
     * @param dt        Date
     * @param formatter DateFormat
     * @return String
     */
    public static String formatDate(Date dt, DateFormat formatter) {
        return formatter.format(dt);
    }

    /**
     * Parse specified String dateStr using format
     *
     * @param dateStr String
     * @param format  String
     * @return Date
     * @throws ParseException
     */
    public static java.util.Date parseDate(String dateStr, String format) throws ParseException {
        return getDateFormat(format).parse(dateStr);
    }

    public static java.util.Date parseDate2(String dateStr, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            convertedDate = null;
        }
        return convertedDate;
    }

    /**
     * Parse specified String dateStr using formatter to java.util.Date
     *
     * @param dateStr   String
     * @param formatter DateFormat
     * @return Date
     * @throws ParseException
     */
    public static Date parseDate(String dateStr, DateFormat formatter) throws ParseException {
        return formatter.parse(dateStr);
    }

    /**
     * Converts a byte size into a human-readable string, such as "1.43 MB" or "27 KB".
     * The values used are based on powers of 1024, ie 1 KB = 1024 bytes, not 1000 bytes.
     *
     * @param byteSize the byte size of some item
     * @return a human-readable description of the byte size
     */
    public static String formatByteSize(long byteSize) {
        String result = null;
        try {
            if (byteSize > Math.pow(1024, 3)) {
                // Report gigabytes
                result = nf.format(byteSize / Math.pow(1024, 3)) + gigabyteSuffix;
            } else if (byteSize > Math.pow(1024, 2)) {
                // Report megabytes
                result = nf.format(byteSize / Math.pow(1024, 2)) + megabyteSuffix;
            } else if (byteSize > 1024) {
                // Report kilobytes
                result = nf.format(byteSize / Math.pow(1024, 1)) + kilobyteSuffix;
            } else if (byteSize >= 0) {
                // Report bytes
                result = byteSize + byteSuffix;
            }
        } catch (NumberFormatException e) {
            return byteSize + byteSuffix;
        }
        return result;
    }

    /**
     * change format Long Date to "dd/MM/yyyy"
     *
     * @param date
     * @return
     */
    public static String dateToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DATE);

        return (d < 10 ? "0" : "") + d + "/" + (m < 10 ? "0" : "") + m
                + "/" + y;
    }

    //mengubah format long ke time "dd/MM/yyyy" --> bangkit 2011-10-26

    /**
     * change format Long Date to HH:mm
     *
     * @param date
     * @return
     */
    public static String timeToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);

        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }

    // mengubah format long ke time "HH:mm" --> bangkit 2011-10-26

    /**
     * change format Long Date to HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String timeSecToString(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);

        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "" + s);
    }

    // mengubah format long ke time "HH:mm:ss" --> bangkit 2011-10-26

    public static String dateTimeToString(long date) {
        return dateToString(date) + " " + timeToString(date);
    }

    public static String dateTimeSecToString(long date) {
        return dateToString(date) + " " + timeSecToString(date);
    }

    /**
     * Change format dd/MM/yyyy HH:mm to long Date.
     * eg :  "09/07/2011 12:45" or "09/07/2011" to Date
     *
     * @param s
     * @return
     */
    public static long stringToDate(String s) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, 2)));
        c.set(Calendar.MONTH, Integer.parseInt(s.substring(3, 5)) - 1);
        c.set(Calendar.YEAR, Integer.parseInt(s.substring(06, 10)));
        if (s.length() > 10) {
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(11, 13)));
            c.set(Calendar.MINUTE, Integer.parseInt(s.substring(14, 16)));
        }

        return c.getTime().getTime();
    }


    // mengubah format string tanggal dd/MM/yyyy HH:mm ke date --> bangkit 2011-10-26
    // cth format nya "09/07/2011 12:45" atau "09/07/2011"
    // mengubah format long ke time "HH:mm:ss" --> bangkit 2011-10-26

    /**
     * @param object
     * @return
     */
    public static String getJsonFromObject(Object object) {
        return GsonHelper.toJson(object);
    }

    public static String getDateTimeFormat(String type) {
        if (Global.AT_DATE.equals(type)) {
            return Global.DATE_STR_FORMAT;
        } else if (Global.AT_TIME.equals(type)) {
            return Global.TIME_STR_FORMAT;
        } else if (Global.AT_DATE_TIME.equals(type)) {
            return Global.DATE_TIME_STR_FORMAT;
        } else {
            return Global.DATE_STR_FORMAT;
        }
    }

    public static Boolean stringToBoolean(String stringBoolean) {
        boolean b = false;
        if (stringBoolean.equals("0")) b = false;
        else b = stringBoolean.equals("1");
        return b;
    }

    public static String booleanToString(boolean b) {
        String stringBoolean;
        if (b) stringBoolean = "1";
        else stringBoolean = "0";
        return stringBoolean;
    }

    /**
     * Inner static class for specifying number format
     * <p>Copyright: Copyright (c) 2004</p>
     * <p>Company: AdIns</p>
     *
     * @author Andri Gunardi
     * @version 1.0
     */
    public static class NumberFormatType {
        private int decimal;
        private char thousandSeparator;
        private char decimalSeparator;
        private boolean showThousandSeparator;

        /**
         * Create instance of NumberFormatType with specified parameters and displays thousand separator
         *
         * @param thousandSeparator char
         * @param decimalSeparator  char
         * @param decimal           int
         */
        public NumberFormatType(char thousandSeparator, char decimalSeparator, int decimal) {
            this(thousandSeparator, decimalSeparator, decimal, true);
        }

        /**
         * Create instance of NumberFormatType with specified parameters
         *
         * @param thousandSeparator     char
         * @param decimalSeparator      char
         * @param decimal               int
         * @param showThousandSeparator boolean
         */
        public NumberFormatType(char thousandSeparator, char decimalSeparator, int decimal, boolean showThousandSeparator) {
            this.decimal = decimal;
            this.thousandSeparator = thousandSeparator;
            this.decimalSeparator = decimalSeparator;
            this.showThousandSeparator = showThousandSeparator;
        }

        public boolean equals(Object object) {
            if (!(object instanceof NumberFormatType)) {
                return false;
            }
            if (this == object) { // optimization
                return true;
            }
            NumberFormatType other = (NumberFormatType) object;
            return other.thousandSeparator == this.thousandSeparator &&
                    other.decimalSeparator == this.decimalSeparator &&
                    other.showThousandSeparator == this.showThousandSeparator &&
                    other.decimal == this.decimal;
        }

        public int hashCode() {
            int result = 5; // arbitrary
            result = 7 * result + (int) this.thousandSeparator;
            result = 7 * result + (int) this.decimalSeparator;
            result = 7 * result + (this.showThousandSeparator ? 1 : 0);
            result = 7 * result + this.decimal;
            return result;
        }
    }

    public static void clearFormatter(){
        numberFormatPool.remove();
        dateFormatPool.remove();
    }

}
