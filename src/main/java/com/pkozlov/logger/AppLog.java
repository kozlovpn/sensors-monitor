package com.pkozlov.logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by pavel on 6/5/16.
 */
public class AppLog {

    private static final String loggerDir = "/home/pavel/Documents/SensorsMonitorLogs/";
    private static String pathToCurrentLogFile = "";

    public Logger getLogger() {
        Logger logger = Logger.getLogger("SensorsMonitorLogging");
        FileHandler fh;

        try {
            File dir = new File(loggerDir);
            for (File lf : dir.listFiles()) {
                String fileName = lf.getName();
                if (fileName.substring(fileName.indexOf("@") + 1, fileName.indexOf(".log")).equals(getCurrentDate())) {
                    pathToCurrentLogFile = loggerDir + lf.getName();
                }
            }
            if (pathToCurrentLogFile.equals("")) {
                File logFile = new File(loggerDir + "sensors-monitor@" + getCurrentDate() + ".log");
                logFile.createNewFile();
                pathToCurrentLogFile = loggerDir + logFile.getName();
            }

            fh = new FileHandler(pathToCurrentLogFile, true);
            logger.addHandler(fh);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }

    public static String getLoggerDir() {
        return loggerDir;
    }

    public static String getCurrentDate() {
        return dateToString(new Date());
    }

    public static File getCurrentLogFile() {
        return new File(pathToCurrentLogFile);
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
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
