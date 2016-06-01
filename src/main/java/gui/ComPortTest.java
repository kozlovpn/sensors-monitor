package gui;

import jssc.*;
import listener.PortReader;

import java.util.Arrays;

/**
 * Created by pavel on 5/29/16.
 */
public class ComPortTest {

    private static SerialPort serialPort;

    public static void openPortAndGetData() {
        //Передаём в конструктор имя порта
        System.out.println(Arrays.asList(SerialPortList.getPortNames()));
        serialPort = new SerialPort("/dev/ttyUSB0");
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

    public static void closePort() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
