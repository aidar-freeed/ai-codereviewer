package com.adins.mss.foundation.formatter;

import androidx.annotation.Keep;

import com.adins.mss.constant.Global;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by gigin.ginanjar on 28/09/2016.
 */
//kamil (22/03/2018) : don't edit this class, used for JexlContext in fragmentQuestion
//        context.set("dateformatter", new DateFormatter());
public class DateFormatter {
    @Keep
    public int age(String birthDate) {
        Date birth = null;
        try {
            birth = Formatter.parseDate(birthDate, Global.DATE_STR_FORMAT_GSON);
        } catch (ParseException e) {
            try {
                birth = Formatter.parseDate(birthDate, Global.DATE_STR_FORMAT);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        Date now = new Date();
        long timeBetween = 0L;
        if(birth != null) {
            timeBetween = now.getTime() - birth.getTime();
        }
        double yearsBetween = timeBetween / 3.15576e+10;
        return (int) Math.floor(yearsBetween);
    }
}
