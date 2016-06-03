package serialport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import results.ResultParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by pavel on 5/29/16.
 */
public class PortReader implements SerialPortEventListener {

    private static SerialPort serialPort;
    private String data = "";

    public PortReader(SerialPort port) {
        serialPort = port;
    }

    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0){
            try {
                //Получаем ответ от устройства, обрабатываем данные и т.д.
                try {
                    data += new String(serialPort.readBytes(event.getEventValue()), "UTF-8").trim().replaceAll("\n", "")
                            .replaceAll("\r", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (data.trim().matches("^\\d+: (RX|Neighbors).+:.+\\d+$")) {
                    ResultParser.parse(data);
                    data = "";
                }
                //И снова отправляем запрос
                //serialPort.writeString("Get data");
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
    }
}
