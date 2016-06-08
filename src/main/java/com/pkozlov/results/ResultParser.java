package com.pkozlov.results;

import com.pkozlov.logger.AppLog;
import com.pkozlov.results.packets.NeighborhoodPacket;
import com.pkozlov.results.packets.SensorPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by pavel on 5/29/16.
 */
public class ResultParser {

    public static SensorPacket sensorPacket = new SensorPacket();
    public static NeighborhoodPacket neighborPacket = new NeighborhoodPacket();
    public static int neighborCount;
    public static String packet1;

    public static void parse(String packet) {
        packet1 = packet;
        System.out.println(packet);
        String[] packetSet = packet.split(":");
        String[] dataSet = packetSet[2].trim().split(" ");
        String[] valueSet = getValueSet(dataSet);
        System.out.println(Arrays.asList(valueSet));
        try {
            if (packetSet[1].contains("sensor")) {
                sensorPacket.setSrcId(Long.valueOf(valueSet[0]));
                sensorPacket.setSeq(Integer.valueOf(valueSet[1]));
                sensorPacket.setRfTemperature(Float.valueOf(valueSet[2]));
                sensorPacket.setTemperature(Float.valueOf(valueSet[3]));
                sensorPacket.setHumidity(Float.valueOf(valueSet[4]));
                sensorPacket.setVoltage(Float.valueOf(valueSet[5]));
            } else if (packetSet[1].contains("neighborhood")) {
                String[] valueSet2 = getValueSet(packetSet[3].split(" "));
                neighborPacket.setSrcId(Long.valueOf(valueSet[0]));
                neighborPacket.setSeq(Integer.valueOf(valueSet[1]));
                neighborPacket.setNodeId(Long.valueOf(valueSet2[0]));
                neighborPacket.setlQout(Integer.valueOf(valueSet2[1]));
                neighborPacket.setlQin(Integer.valueOf(valueSet2[2]));
                neighborPacket.setRssi(Integer.valueOf(valueSet2[3]));
                neighborPacket.setElapseTime(Integer.valueOf(valueSet2[4]));
            } else if (packetSet[1].contains("Neighbors")) {
                System.out.println(packet);
                neighborCount = Integer.valueOf(getValue(packetSet[1]));
                neighborPacket.setNodeId(Long.valueOf(valueSet[0]));
                neighborPacket.setlQout(Integer.valueOf(valueSet[1]));
                neighborPacket.setlQin(Integer.valueOf(valueSet[2]));
                neighborPacket.setRssi(Integer.valueOf(valueSet[3]));
                neighborPacket.setElapseTime(Integer.valueOf(valueSet[4]));
            }
        } catch (NumberFormatException e) {
            //nothing to do
        }
    }

    public static Map<Integer, Number> getValuesPerDayFromLog(String index) throws IOException, ParseException {
        File logFile = AppLog.getCurrentLogFile();
        List<String> lines = Files.readAllLines(Paths.get(logFile.getAbsolutePath()));
        Map<Integer, Number> hourToAverageValue = new HashMap<Integer, Number>();
        Map<Integer, List<String>> hourToValues = new HashMap<Integer, List<String>>();
        Calendar calForLine = Calendar.getInstance();
        boolean isTemp = index.equals("temperature");

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("sensor")) {
                String currentSensorLine = lines.get(i);
                String currentDateForLine = lines.get(i - 1).substring(0, lines.get(i - 1).indexOf(" com"));
                calForLine.setTime(new Date(currentDateForLine));
                int hourForLine = calForLine.get(Calendar.HOUR_OF_DAY);
                System.out.println(hourForLine);
                String sensorPacket = currentSensorLine.substring(currentSensorLine.indexOf("INFO: ") + 6,
                        currentSensorLine.length());
                String[] packetSet = sensorPacket.split(":");
                String[] dataSet = packetSet[2].trim().split(" ");
                String[] valueSet = getValueSet(dataSet);

                if (hourToValues.containsKey(hourForLine) && isTemp) {
                    hourToValues.get(hourForLine).add(valueSet[3]);
                } else if (hourToValues.containsKey(hourForLine) && !isTemp){
                    hourToValues.get(hourForLine).add(valueSet[4]);
                } else if (!hourToValues.containsKey(hourForLine) && isTemp) {
                    hourToValues.put(hourForLine, new ArrayList<String>());
                    hourToValues.get(hourForLine).add(valueSet[3]);
                } else {
                    hourToValues.put(hourForLine, new ArrayList<String>());
                    hourToValues.get(hourForLine).add(valueSet[4]);
                }
            }
        }
        for (Integer key : hourToValues.keySet()) {
            hourToAverageValue.put(key, calculateAverage(hourToValues.get(key)));
        }
        return hourToAverageValue;
    }

    private static Number calculateAverage(List<String> list) throws ParseException {
        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0;
        for (String value: list) {
            sum += Double.valueOf(value);
        }
        return df.parse(df.format(sum / list.size()));
    }

    public static Map<Integer, Number> getValuesPerHourFromLog(String index) throws IOException, ParseException {
        File logFile = AppLog.getCurrentLogFile();
        List<String> lines = Files.readAllLines(Paths.get(logFile.getAbsolutePath()));
        Map<Integer, Number> minuteToAverageValue = new HashMap<Integer, Number>();
        Map<Integer, List<String>> minuteToValues = new HashMap<Integer, List<String>>();
        boolean isTemp = index.equals("temperature");

        Calendar calForLine = Calendar.getInstance();
        int oneHourBackFromCurTime = calForLine.get(Calendar.HOUR) - 1;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("sensor")) {
                String currentSensorLine = lines.get(i);
                String dateForLine = lines.get(i - 1).substring(0, lines.get(i - 1).indexOf(" com"));
                calForLine.setTime(new Date(dateForLine));
                int hourForLine = calForLine.get(Calendar.HOUR);
                String sensorPacket = currentSensorLine.substring(currentSensorLine.indexOf("INFO: ") + 6,
                        currentSensorLine.length());
                String[] packetSet = sensorPacket.split(":");
                String[] dataSet = packetSet[2].trim().split(" ");
                String[] valueSet = getValueSet(dataSet);

                if (hourForLine == oneHourBackFromCurTime) {
                    int minute = calForLine.get(Calendar.MINUTE);
                    if (minuteToValues.containsKey(minute) && isTemp) {
                        minuteToValues.get(minute).add(valueSet[3]);
                    } else if (minuteToValues.containsKey(minute) && !isTemp){
                        minuteToValues.get(minute).add(valueSet[4]);
                    } else if (!minuteToValues.containsKey(minute) && isTemp) {
                        minuteToValues.put(minute, new ArrayList<String>());
                        minuteToValues.get(minute).add(valueSet[3]);
                    } else {
                        minuteToValues.put(minute, new ArrayList<String>());
                        minuteToValues.get(minute).add(valueSet[4]);
                    }
                }
            }
        }
        for (Integer key : minuteToValues.keySet()) {
            minuteToAverageValue.put(key, calculateAverage(minuteToValues.get(key)));
        }
        return minuteToAverageValue;
    }

    public static Map<Integer, Number> getValuesPerWeekFromLog(String index) throws IOException, ParseException {
        List<File> sevenFiles = new ArrayList<File>();
        List<String> sevenDates = new ArrayList<String>();
        File loggerDir = new File(AppLog.getLoggerDir());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -7);

        for (int i = 0; i <= 6; i++) {
            sevenDates.add(AppLog.dateToString(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }

        for (File file : loggerDir.listFiles()) {
            String fileDate = file.getName().substring(file.getName().indexOf("@") + 1, file.getName().indexOf(".log"));
            if (sevenDates.contains(fileDate)) {
                sevenFiles.add(file);
                sevenDates.remove(fileDate);
            }
        }
        sevenFiles.sort(new Comparator<File>() {
            public int compare(File o1, File o2) {
                Date fileDate1 = AppLog.stringToDate(o1.getName().substring(o1.getName().indexOf("@") + 1, o1.getName().indexOf(".log")));
                Date fileDate2 = AppLog.stringToDate(o2.getName().substring(o2.getName().indexOf("@") + 1, o2.getName().indexOf(".log")));
                return fileDate1.compareTo(fileDate2);
            }
        });

        Map<Integer, Number> dayToAverageValue = new HashMap<Integer, Number>();
        Map<Integer, List<String>> dayToValues = new HashMap<Integer, List<String>>();
        boolean isTemp = index.equals("temperature");
        for (int j = 0; j < sevenFiles.size(); j++) {
            System.out.println("fileName: " + sevenFiles.get(j).getName());
            List<String> lines = Files.readAllLines(Paths.get(sevenFiles.get(j).getAbsolutePath()));
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("sensor")) {
                    String currentSensorLine = lines.get(i);
                    String sensorPacket = currentSensorLine.substring(currentSensorLine.indexOf("INFO: ") + 6,
                            currentSensorLine.length());
                    String[] packetSet = sensorPacket.split(":");
                    String[] dataSet = packetSet[2].trim().split(" ");
                    String[] valueSet = getValueSet(dataSet);

                    if (dayToValues.containsKey(j) && isTemp) {
                        dayToValues.get(j).add(valueSet[3]);
                    } else if (dayToValues.containsKey(j) && !isTemp){
                        dayToValues.get(j).add(valueSet[4]);
                    } else if (!dayToValues.containsKey(j) && isTemp) {
                        dayToValues.put(j, new ArrayList<String>());
                        dayToValues.get(j).add(valueSet[3]);
                    } else {
                        dayToValues.put(j, new ArrayList<String>());
                        dayToValues.get(j).add(valueSet[4]);
                    }
                }
            }
        }
        System.out.println(dayToValues);
        for (Integer key : dayToValues.keySet()) {
            dayToAverageValue.put(key, calculateAverage(dayToValues.get(key)));
        }
        return dayToAverageValue;
    }

    private static String getValue(String par) {
        par = par.trim();
        if (par.contains("Neighbors")) {
            return par.substring(par.indexOf("(") + 1, par.indexOf(")"));
        }
        return par.substring(par.indexOf("=") + 1, par.length());
    }

    private static String[] getValueSet(String[] dataSet) {
        List<String> l = new ArrayList<String>();
        for (String d : dataSet) {
            d = d.trim();
            if (!d.isEmpty()) {
                if (d.matches("^.+\\d+\\.\\d+")) {
                    l.add(d.replaceAll("[^.0-9]", ""));
                } else {
                    l.add(d.replaceAll("\\D+", ""));
                }
            }
        }
        return l.toArray(new String[l.size()]);
    }
}
