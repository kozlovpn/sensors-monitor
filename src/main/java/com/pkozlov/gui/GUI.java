package com.pkozlov.gui;

import com.pkozlov.logger.AppLog;
import com.pkozlov.serialport.ComPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jssc.SerialNativeInterface;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.pkozlov.results.ResultParser;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by pavel on 5/29/16.
 */
public class GUI extends Application {

    private Text temperature = new Text("Init...");
    private Text humidity = new Text("Init...");
    private Text voltage = new Text("Init...");
    private String DEGREE = "\u00b0С";
    private final Stage stageForEnterPort = new Stage();
    private final ObservableList<String> chartOptions = FXCollections.observableArrayList(
                    "Temperature",
                    "Humidity"
            );
    private final ObservableList<String> periodOptions = FXCollections.observableArrayList(
            "per hour",
            "per day",
            "per week"
    );

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MeshLogic Sensors Monitor");
        GridPane grid = new GridPane();
        //grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));
        //
        final Text scenetitle = new Text("Real time monitor");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        VBox vboxSceneTitle = new VBox(scenetitle);
        vboxSceneTitle.setAlignment(Pos.CENTER);
        grid.add(vboxSceneTitle, 0, 0, 3, 1);

        final Label tempLabel = new Label("Temperature:");
        tempLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(tempLabel, 0, 1);
        temperature.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(temperature, 1, 1);
        ResultParser.sensorPacket.getTemperatureProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                temperature.setText(newValue.toString() + DEGREE);
            }
        });

        //final PasswordField pwBox = new PasswordField();
        //grid.add(pwBox, 1, 2);

        final Label humidityLabel = new Label("Humidity:");
        humidityLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(humidityLabel, 0, 2);
        humidity.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(humidity, 1, 2);
        ResultParser.sensorPacket.getHumidityProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                humidity.setText(newValue.toString() + "%");
            }
        });

        final Label voltageLabel = new Label("Voltage:");
        voltageLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(voltageLabel, 0, 3);
        voltage.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(voltage, 1, 3);
        ResultParser.sensorPacket.getVoltageProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                voltage.setText(newValue.toString() + "V");
            }
        });
        //
        Button btn = new Button("Calculate dew point");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 4);
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 4, 2, 1);
        actiontarget.setFont(Font.font("Tahoma", FontWeight.LIGHT, 20));
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.BLUE);
                actiontarget.setText("Dew Point is " + calculateDewPoint() + DEGREE);
            }
        });
        //
        Text createChartLabel = new Text("Create chart");
        createChartLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        VBox vboxCreateChartLabel = new VBox(createChartLabel);
        vboxCreateChartLabel.setAlignment(Pos.CENTER);
        //createChartLabel.setTextAlignment(TextAlignment.CENTER);
        grid.add(vboxCreateChartLabel, 0, 5, 3, 1);
        final ComboBox chartList = new ComboBox(chartOptions);
        chartList.setPromptText("Choose chart...");
        chartList.setMaxWidth(200);
        grid.add(chartList, 0, 6);
        final ComboBox periodList = new ComboBox(periodOptions);
        periodList.setPromptText("Choose period...");
        periodList.setMaxWidth(200);
        grid.add(periodList, 1, 6);
        Button showChartBtn = new Button("Create chart");
        grid.add(showChartBtn, 2, 6);
        showChartBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (chartList.getValue() != null && periodList.getValue() != null) {
                    try {
                        createChart(chartList.getValue().toString(), periodList.getValue().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException ee) {
                        ee.printStackTrace();
                    }
                }
            }
        });

        Scene scene = new Scene(grid, 500, 475);
        primaryStage.setScene(scene);
        primaryStage.show();

        ComPort.isNoDevices.addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.equals(false)) {
                    stageForEnterPort.close();
                } else {
                    openWindowToConnectDevice();
                }
            }
        });

        ComPort.isMultiplePorts.addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.equals(false)) {
                    stageForEnterPort.close();
                } else {
                    openWindowForEnterPort();
                }
            }
        });
        ComPort.checkMultiplePorts();
    }

    @Override
    public void stop() {
        ComPort.closePort();
    }

    public static void main(String[] args) throws Throwable {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -7);
        for (int i = 0; i <= 6; i++) {
            System.out.println("current date -7 days: " + AppLog.dateToString(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }
        launch(args);
    }

    public void openWindowToConnectDevice() {
        final GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.TOP_CENTER);
        grid2.setHgap(10);
        grid2.setVgap(30);
        grid2.setPadding(new Insets(10, 10, 10, 10));
        final Scene scene1 = new Scene(grid2, 350, 120);

        Label label = new Label();
        final Button button = new Button();
        GridPane.setHalignment(button, HPos.CENTER);

        stageForEnterPort.setTitle("Connect device");
        label.setText("Please connect MeshLogic device and try again!");
        grid2.add(label, 0, 0);
        button.setText("Try again");
        button.setMaxWidth(100);
        grid2.add(button, 0, 2);
        stageForEnterPort.setScene(scene1);

        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ComPort.checkMultiplePorts();
            }
        });

        stageForEnterPort.show();
    }

    public void openWindowForEnterPort() {
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(10, 10, 10, 10));

        final Scene scene = new Scene(grid, 380, 250);
        Label label = new Label();
        final Button button = new Button();
        final TextField textField = new TextField();
        final Text error = new Text();
        error.setFill(Color.RED);
        GridPane.setHalignment(button, HPos.CENTER);

        stageForEnterPort.setTitle("Enter port name");
        label.setText("Your computer has multiple serial port connections.\n" +
                "Please enter the correct serial port name\nin which the MeshLogic device is connected:");
        grid.add(label, 0, 0);
        grid.add(textField, 0, 1);
        button.setText("OK");
        grid.add(error, 0, 2);
        button.setMaxWidth(100);
        grid.add(button, 0, 3);
        stageForEnterPort.setScene(scene);

        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String portName = textField.getText();
                int osType = SerialNativeInterface.getOsType();
                if (!portName.equals("") && portName.matches("^(ttyUSB|COM)\\d+$") && ((osType == SerialNativeInterface.OS_WINDOWS
                        && portName.contains("COM")) || (osType == SerialNativeInterface.OS_LINUX && portName.contains("tty")))) {
                    ComPort.openPortAndGetData(portName);
                    stageForEnterPort.close();
                } else if (portName.equals("")) {
                    error.setText("Please enter port name!");
                } else {
                    error.setText("Invalid port name!");
                }
            }
        });

        stageForEnterPort.show();
    }

    public void createChart(String chart, String period) throws IOException, ParseException {
        Stage primaryStage = new Stage();
        primaryStage.setTitle(chart + " chart");
        Map<Integer, Number> values;

        NumberAxis x;
        NumberAxis y;

        if (chart.equalsIgnoreCase("temperature")) {
            y = new NumberAxis();
            y.setLabel(chart + ", " + DEGREE);
        } else {
            y = new NumberAxis(0, 100, 10);
            y.setLabel(chart + ", %");
        }

        if (period.contains("hour")) {
            x = new NumberAxis(0, 60, 5);
            x.setLabel("Minutes");
        } else if (period.contains("day")) {
            x = new NumberAxis(0, 23, 1);
            x.setLabel("Hours");
        } else {
            x = new NumberAxis(1, 7, 1);
            x.setLabel("Number of day");
        }

        LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
        numberLineChart.setTitle(chart + " chart " + period);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(chart);
        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();

        if (period.contains("hour")) {
            values = ResultParser.getValuesPerHourFromLog(chart.toLowerCase());
            for (Integer key : values.keySet()) {
                datas.add(new XYChart.Data(key, values.get(key)));
            }
        } else if (period.contains("day")) {
            values = ResultParser.getValuesPerDayFromLog(chart.toLowerCase());
            for (Integer key : values.keySet()) {
                datas.add(new XYChart.Data(key, values.get(key)));
            }
        } else {
            values = ResultParser.getValuesPerWeekFromLog(chart.toLowerCase());
            System.out.println(values);
            for (Integer key : values.keySet()) {
                datas.add(new XYChart.Data(key + 1, values.get(key)));
            }
        }
        series1.setData(datas);

        Scene scene = new Scene(numberLineChart, 800, 700);
        numberLineChart.getData().add(series1);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String calculateDewPoint() {
        DecimalFormat df = new DecimalFormat("0.00");
        float temperature = ResultParser.sensorPacket.getTemperature();
        float humidity = ResultParser.sensorPacket.getHumidity();
        double a = 17.27;
        double b = 237.7;
        double func = (a * temperature / (b + temperature)) + Math.log(humidity / 100);
        double d = b * func / (a - func);
        return df.format(d);
    }
}
