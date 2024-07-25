package com.adins.mss.foundation.formatter;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.adins.msm.constant.Global;


public class Reader {

    //Unit Day
    public static String UNIT_DAY = "DAY";
    public static String UNIT_HOUR = "HOUR";
    public static String UNIT_MIN = "MIN";
    public static String UNIT_SEC = "SEC";
    private static int DIGIT_NUMBER = 3;
    private static String DIGIT_DELIMETER = ".";

    public static String replace(String source, char oldChar, char newChar) {
        //Reader.replace(desc, Global.DELIMETER_DATA.charAt(0), Global.NEW_CHAR)

        try {
            String result = null;
            result = source.replace(oldChar, newChar);
            return result;
        } catch (Exception e) {
            FireCrash.log(e);
            return source;
        }
    }

    public static boolean isContainMinus(String source) {
        int idx = 0;
        try {
            idx = source.indexOf("-");
            if (Global.IS_DEV)
                System.out.println("idx$= " + idx);
            if (idx > -1) {
                return true;
            }

        } catch (Exception e) {
            FireCrash.log(e);
            return false;
        }
        return false;
    }

    /**
     * Only allow this character
     * ^[0-9a-zA-Z!@%&*()=-{}[]+;:'"\n\t<>,./?\]*$
     *
     * @param source
     * @return
     */
    public static boolean isInvalidCharacter(String source) {


        try {
            String re = "^[0-9a-zA-Z!@%&*()=\\-{}\\[\\]\\+\\;:'\"\n\t<>,./?\\\\ ]*$";
            if (Global.IS_DEV)
                System.out.println("re " + re);

            Pattern p = Pattern.compile(re);
            Matcher m = p.matcher(source);

            m.matches();
            if (!m.matches()) {

                return true;
            }

        } catch (Exception e) {
            FireCrash.log(e);
            return false;
        }


        return false;
    }

    public static String[] split(String original, String delimeter) {

        Vector nodes = splitToVector(original, delimeter);
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }

        return result;
    }

    public static Vector splitToVector(String original, String delimeter) {

        Vector nodes = new Vector();
        int index = original.indexOf(delimeter);

        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + delimeter.length());
            index = original.indexOf(delimeter);
        }

        nodes.addElement(original);

        return nodes;
    }

    /**
     * Inta : ditambahkan untuk kepentingan validasi tahun pada tanggal lahir
     *
     * @return
     */
    public static long dateMinimum() {
        long longDate = 0;
        try {
            String MINIMUM_DATE = "01-01-1753";
            Date date = getDateFromDateField(MINIMUM_DATE);
            longDate = date.getTime();
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.IS_DEV) {
                System.out.println("Error get DateMinimum ini READER.JAVA");
                System.out.println(e.getMessage());
            }
        }
        return longDate;
    }

    /**
     * Generate Date to date in string.
     * eg : 01-12-10 14:31
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {

        //01-12-10 14:31

        try {
            if (date != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int datetgl = calendar.get(Calendar.DATE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);

                StringBuffer sb = new StringBuffer();
                sb.append(addZero(datetgl)).append("-");
                sb.append(addZero(month)).append("-");
                sb.append(addZero(year));
                sb.append(" ");
                sb.append(addZero(hour)).append(":");
                sb.append(addZero(minute)).append(":");
                sb.append(addZero(second));

                return sb.toString();

            } else {
                return null;
            }

        } catch (Exception e) {
            FireCrash.log(e);
            return "";
        }


    }

    /**
     * Generate Date to date in string.
     * eg : 01-12-10
     *
     * @param date
     * @return
     */
    public static String getDateStringOnlyDate(Date date) {

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int datetgl = calendar.get(Calendar.DATE);

            StringBuffer sb = new StringBuffer();

            sb.append(addZero(datetgl)).append("-");
            sb.append(addZero(month)).append("-");
            sb.append(addZero(year));
            sb.append(" ");


            return sb.toString();

        } else {
            return null;
        }
    }

    /**
     * Generate Date to date in string.
     * eg : 14:31
     *
     * @param date
     * @return
     */
    public static String getDateStringOnlyTime(Date date) {

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            StringBuffer sb = new StringBuffer();

            sb.append(addZero(hour)).append(":");
            sb.append(addZero(minute)).append(":");
            sb.append(addZero(second));

            return sb.toString();

        } else {
            return null;
        }
    }

    /**
     * Generate format 2 digit.
     * eg : 2 --> 02
     *
     * @param numberInput
     * @return
     */
    public static String addZero(int numberInput) {

        String numberString = "";
        try {
            numberString = String.valueOf(numberInput);

            if (numberString.length() == 1) {
                return 0 + numberString;
            } else if (numberString.length() < 1) {
                return "00";
            } else {
                return numberString;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            return numberString;
        }


    }

    public static String getDateString2(Date date) {

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int datetgl = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            StringBuffer sb = new StringBuffer();
            sb.append(datetgl).append("-").append(month).append("-").append(String.valueOf(year).substring(2));
            sb.append(" ").append(hour).append(":").append(minute);

            return sb.toString();

        } else {
            return null;
        }

    }

    public static Date getDateFromDateField(String dateString) {
        // format String harus = dd-MM-yyyy

        String[] temp = split(dateString, "-");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, Integer.valueOf(temp[0]).intValue());
        cal.set(Calendar.MONTH, Integer.valueOf(temp[1]).intValue());
        cal.set(Calendar.YEAR, Integer.valueOf(temp[2]).intValue());
        Date date = cal.getTime();
        //System.out.println("getDateDate = " + date);
        return date;
    }

    /**
     * Get the diff of 2 Date.
     *
     * @param startDate
     * @param endDate
     * @param unit      --> Reader.UNIT_DAY, Reader.UNIT_HOUR, Reader.UNIT_MIN, Reader.UNIT_SEC
     * @return
     */
    public static long getDifferentDate(Date startDate, Date endDate, String unit) {
        long diff = endDate.getTime() - startDate.getTime();
        if (Reader.UNIT_DAY.equals(unit)) {
            diff = diff / (1000 * 60 * 60 * 24);
        } else if (Reader.UNIT_HOUR.equals(unit)) {
            diff = diff / (1000 * 60 * 60);
        } else if (Reader.UNIT_MIN.equals(unit)) {
            diff = diff / (1000 * 60);
        } else if (Reader.UNIT_SEC.equals(unit)) {
            diff = diff / 1000;
        }
        return diff;
    }

    public static Date getDate(long dateLong) {
        Date date = new Date();
        date.setTime(dateLong);
        return date;
    }

    public static String getDateComplete(Date date) {

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int datetgl = calendar.get(Calendar.DATE);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

//			StringBuffer sb = new StringBuffer();
//			sb.append(datetgl).append("-");
//			sb.append(month).append("-");
//			sb.append(year).append(" ");
//			sb.append(hour).append(":");
//			sb.append(minute).append(":");
//			sb.append(second);

            StringBuffer sb = new StringBuffer();
            sb.append(addZero(datetgl)).append("-");
            sb.append(addZero(month)).append("-");
            sb.append(addZero(year)).append(" ");
            sb.append(addZero(hour)).append(":");
            sb.append(addZero(minute)).append(":");
            sb.append(addZero(second));

            return sb.toString();

        } else {
            return null;
        }

    }

    public static String getNumericDigit(long value) {

        String result = null;
        String numString = String.valueOf(value);
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
            } else {

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

    /**
     * Get the midnight hour.
     * eg : 00:00:00:00
     *
     * @return
     */
    public static Date getMiddleNightDate() {

        Calendar cal = Calendar.getInstance();

        //set jam tngah malam 00:00:00:00
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date date = cal.getTime();

        if (Global.IS_DEV)
            System.out.println("getMiddleNightDate= " + date);

        long datelong = date.getTime();

        if (Global.IS_DEV) {
            System.out.println("datelong= " + datelong);
            System.out.println("midnite= " + getDateString(new Date(datelong)));
        }
        return date;

    }

    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 0=same.
     * -1=date1 before date2.
     * 2=date1 after date2.
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compareDate(Date d1, Date d2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        if (cal1.equals(cal2)) {
            return 0;
        } else if (cal1.before(cal2)) {
            return -1;
        } else {
            return 1;
        }

    }

    /**
     * eg : 12:34.
     * result :  1 2 3 4 -> 1 3 2 4 -> 2 4 1 3.
     *
     * @param date
     * @return
     */
    public static String getKeyDate(Date date) {

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            String jam = addZero(hour);
            String menit = addZero(minute);
            char a = jam.charAt(0);
            char b = jam.charAt(1);
            char c = menit.charAt(0);
            char d = menit.charAt(1);

            StringBuffer result = new StringBuffer();
            result.append(b).append(d).append(a).append(c);
            return result.toString();

        } else {
            return null;
        }
    }

    /**
     * for add 000 in thousandDigit
     * for example -> value = 123; result = 123000
     *
     * @param value - value in String
     * @return
     */
    public static String getThousandDigit(String value) {
        String result = null;
        String numString = value;

        result = value + "000";

        return result;
    }

    /**
     * Gets Currency Digit
     * for example : value=12000; result=12.000
     *
     * @param value - value in String
     * @return
     */
    public static String getCurrencyDigit(String value) {

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

        //Gigin : Result Kalo Pake Rupiah
//		result = "Rp" + getStringDelimeter(numString, CURRENCY_DELIMETER, resultDivide);

        return result;

    }

    public int getIndexString(String source, String findStr) {
        int idx;
        try {
            idx = source.indexOf(findStr);
        } catch (Exception e) {
            FireCrash.log(e);
            idx = -1;
        }
        return idx;
    }
}
