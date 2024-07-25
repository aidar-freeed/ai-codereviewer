package com.adins.mss.base.dynamicform.form.resolver;

import com.adins.mss.constant.Global;
import com.gadberry.utility.CalendarUtils;
import com.gadberry.utility.expression.Resolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gigin.ginanjar on 15/09/2016.
 */
public class DateResolver implements Resolver {

    private SimpleDateFormat sdf = new SimpleDateFormat(Global.DATE_STR_FORMAT);

    @Override
    public boolean canResolve(String path) {
        return resolve(path) != null || path.equals("NULL");
    }

    private Date getDate(String path) {
        try {
            return sdf.parse(path);
        } catch (ParseException e) {
        }
        return null;
    }

    private CalendarUtils.Unit getDateUnit(String path) {
        if (path.equals("|YEAR|")) {
            return CalendarUtils.Unit.YEAR;
        } else if (path.equals("|MONTH|")) {
            return CalendarUtils.Unit.MONTH;
        } else if (path.equals("|DAY|")) {
            return CalendarUtils.Unit.DAY;
        } else if (path.equals("|HOUR|")) {
            return CalendarUtils.Unit.HOUR;
        } else if (path.equals("|MINUTE|")) {
            return CalendarUtils.Unit.MINUTE;
        } else if (path.equals("|SECOND|")) {
            return CalendarUtils.Unit.SECOND;
        } else if (path.equals("|MILLISECOND|")) {
            return CalendarUtils.Unit.MILLISECOND;
        }
        return null;
    }

    private boolean isDate(String path) {
        return getDate(path) != null;
    }

    private boolean isDateUnit(String path) {
        return getDateUnit(path) != null;
    }

    @Override
    public Object resolve(String path) {
        if (isDate(path)) {
            return getDate(path);
        } else if (isDateUnit(path)) {
            return getDateUnit(path);
        }
        return null;
    }
}
