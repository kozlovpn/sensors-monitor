package com.pkozlov.serialport;

import com.pkozlov.logger.AppLog;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import com.pkozlov.results.ResultParser;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * Created by pavel on 5/29/16.
 */
public class PortReader implements SerialPortEventListener {

    private static SerialPort serialPort;
    private String data = "";
    private Logger logger;

    public PortReader(SerialPort port) {
        serialPort = port;
        logger = new AppLog().getLogger();
    }

    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0){
            try {
                try {
                    data += new String(serialPort.readBytes(event.getEventValue()), "UTF-8").trim().replaceAll("\n", "")
                            .replaceAll("\r", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (data.trim().matches("^\\d+: (RX|Neighbors).+:.+\\d+$")) {
                    ResultParser.parse(data);
                    logger.info(data);
                    data = "";
                }
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
    }
}
