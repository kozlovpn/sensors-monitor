package com.pkozlov.serialport;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import jssc.*;

/**
 * Created by pavel on 5/29/16.
 */
public class ComPort {

    private static SerialPort serialPort;
    public static BooleanProperty isMultiplePorts = new SimpleBooleanProperty(false);
    public static BooleanProperty isNoDevices = new SimpleBooleanProperty(false);

    public static void openPortAndGetData(String portName) {
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_WINDOWS) {
            serialPort = new SerialPort(portName);
        } else if (portName.contains("dev")) {
            serialPort = new SerialPort(portName);
        } else {
            serialPort = new SerialPort("/dev/" + portName);
        }

        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(new PortReader(serialPort), SerialPort.MASK_RXCHAR);
        }
        catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    public static void checkMultiplePorts() {
        int portCounts = SerialPortList.getPortNames().length;
        if (portCounts == 1) {
            isNoDevices.setValue(false);
            isMultiplePorts.setValue(false);
            openPortAndGetData(SerialPortList.getPortNames()[0]);
        } else if (portCounts == 0) {
            isNoDevices.setValue(true);
            isMultiplePorts.setValue(false);
        } else {
            isNoDevices.setValue(false);
            isMultiplePorts.setValue(true);
        }
    }

    public static void closePort() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
