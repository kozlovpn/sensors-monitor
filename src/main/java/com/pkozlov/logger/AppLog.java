package com.pkozlov.logger;

import com.pkozlov.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * Created by pavel on 6/5/16.
 */
public class AppLog {

    private static final String loggerDir = "C:/SensorsMonitorLogs/";
    private static String pathToCurrentLogFile = "";

    public Logger getLogger() {
        Logger logger = Logger.getLogger("SensorsMonitorLogging");
        FileHandler fh;

        try {
            File dir = new File(loggerDir);
            for (File lf : dir.listFiles()) {
                String fileName = lf.getName();
                if (fileName.substring(fileName.indexOf("@") + 1, fileName.indexOf(".log")).equals(DateUtils.getCurrentDate())) {
                    pathToCurrentLogFile = loggerDir + lf.getName();
                }
            }
            if (pathToCurrentLogFile.equals("")) {
                File logFile = new File(loggerDir + "sensors-monitor@" + DateUtils.getCurrentDate() + ".log");
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

    public static File getCurrentLogFile() {
        return new File(pathToCurrentLogFile);
    }
}
