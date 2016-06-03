package serialport;

import gui.GUI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import jssc.*;

/**
 * Created by pavel on 5/29/16.
 */
public class ComPort {

    private static SerialPort serialPort;
    public static BooleanProperty isMultiplePorts = new SimpleBooleanProperty();

    public static void openPortAndGetData(String portName) {
        //Передаём в конструктор имя порта
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_WINDOWS) {
            serialPort = new SerialPort(portName);
        } else if (portName.contains("dev")) {
            serialPort = new SerialPort(portName);
        } else {
            serialPort = new SerialPort("/dev/" + portName);
        }

        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(serialPort), SerialPort.MASK_RXCHAR);
            //Отправляем запрос устройству
            serialPort.writeString("Get data");
        }
        catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    public static void checkMultiplePorts() {
        if (SerialPortList.getPortNames().length == 1) {
            openPortAndGetData(SerialPortList.getPortNames()[0]);
        } else {
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
