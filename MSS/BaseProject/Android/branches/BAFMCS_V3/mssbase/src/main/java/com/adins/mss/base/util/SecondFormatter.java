package com.adins.mss.base.util;

import com.adins.mss.foundation.formatter.Tool;

public class SecondFormatter {
    private static String secondSuffix = " Seconds";
    private static String minuteSuffix = " Minutes";
    private static String hourSuffix = " Hours";

    public static String secondsToDelimitedTime(long second, String delimiter) {
        int seconds = (int) (second % 60);
        int minutes = (int) ((second / 60) % 60);
        int hours = (int) ((second / 3600) % 24);

        String secondsStr = Tool.appendZeroForDateTime(seconds, false);
        String minutesStr = Tool.appendZeroForDateTime(minutes, false);
        String hoursStr = Tool.appendZeroForDateTime(hours, false);

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hoursStr).append(delimiter);
        }
        if (minutes > 0) {
            sb.append(minutesStr).append(delimiter);
        }
        sb.append(secondsStr);

        return sb.toString();
    }

    public static String secondsToString(long second) {
        int seconds = (int) (second % 60);
        int minutes = (int) ((second / 60) % 60);
        int hours = (int) ((second / 3600) % 24);

        String secondsStr = Tool.appendZeroForDateTime(seconds, false) + secondSuffix;
        String minutesStr = Tool.appendZeroForDateTime(minutes, false) + minuteSuffix;
        String hoursStr = Tool.appendZeroForDateTime(hours, false) + hourSuffix;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hoursStr).append(' ');
        }
        if (minutes > 0) {
            sb.append(minutesStr).append(' ');
        }
        sb.append(secondsStr);

        return sb.toString();
    }
}
