package results;

import results.packets.NeighborhoodPacket;
import results.packets.SensorPacket;

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
