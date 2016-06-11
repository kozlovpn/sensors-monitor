package com.pkozlov.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Павел on 11.06.2016.
 */
public class DateUtils {

    public static String getCurrentDate() {
        return dateToString(new Date());
    }

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    public static Date getHourMinuteSecond(Date date) throws ParseException {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.parse(df.format(date));
    }

    public static Date stringToDate(String dateString) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", new Locale("ru"));
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
