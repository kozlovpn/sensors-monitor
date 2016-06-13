package com.pkozlov.utils;

import com.pkozlov.results.ResultParser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Павел on 11.06.2016.
 */
public class CalculateUtils {

    private static DecimalFormat df = new DecimalFormat("0.00");

    public static Number calculateAverage(List<String> list) throws ParseException {
        double sum = 0;
        for (String value: list) {
            sum += Double.valueOf(value);
        }
        return df.parse(df.format(sum / list.size()));
    }

    public static String calculateDewPoint(float temperature, float humidity) {
        double a = 17.27;
        double b = 237.7;
        double func = (a * temperature / (b + temperature)) + Math.log(humidity / 100);
        double d = b * func / (a - func);
        return df.format(d);
    }

    public static String calculateDistance(Number rssi) {
        double logD = (-17 + rssi.doubleValue()) / 20;
        double distance = Math.pow(2.718, logD);
        return df.format(distance);
    }
}
