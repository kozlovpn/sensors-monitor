package com.pkozlov.gui;

import com.pkozlov.serialport.ComPort;
import com.pkozlov.utils.CalculateUtils;
import com.pkozlov.utils.DateUtils;
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

import java.io.IOException;
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
                    "Температура",
                    "Влажность"
            );
    private final ObservableList<String> periodOptions = FXCollections.observableArrayList(
            "за час",
            "за день",
            "за неделю"
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
        final Text scenetitle = new Text("Мониторинг в реальном времени");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        VBox vboxSceneTitle = new VBox(scenetitle);
        vboxSceneTitle.setAlignment(Pos.CENTER);
        grid.add(vboxSceneTitle, 0, 0, 3, 1);

        final Label tempLabel = new Label("Температура:");
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

        final Label humidityLabel = new Label("Влажность:");
        humidityLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(humidityLabel, 0, 2);
        humidity.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(humidity, 1, 2);
        ResultParser.sensorPacket.getHumidityProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                humidity.setText(newValue.toString() + "%");
            }
        });

        final Label voltageLabel = new Label("Вольтаж:");
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
        Button btn = new Button("Рассчитать точку росы");
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
                actiontarget.setText("Точка росы: " + CalculateUtils.calculateDewPoint(ResultParser.sensorPacket.getTemperature(),
                        ResultParser.sensorPacket.getHumidity()) + DEGREE);
            }
        });
        //
        Text createChartLabel = new Text("Создать график");
        createChartLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        VBox vboxCreateChartLabel = new VBox(createChartLabel);
        vboxCreateChartLabel.setAlignment(Pos.CENTER);
        //createChartLabel.setTextAlignment(TextAlignment.CENTER);
        grid.add(vboxCreateChartLabel, 0, 5, 3, 1);
        final ComboBox chartList = new ComboBox(chartOptions);
        chartList.setPromptText("График...");
        chartList.setMaxWidth(200);
        grid.add(chartList, 0, 6);
        final ComboBox periodList = new ComboBox(periodOptions);
        periodList.setPromptText("Период...");
        periodList.setMaxWidth(200);
        grid.add(periodList, 1, 6);
        Button showChartBtn = new Button("Создать");
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
            System.out.println("current date -7 days: " + DateUtils.dateToString(cal.getTime()));
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
        final Scene scene1 = new Scene(grid2, 380, 120);

        Label label = new Label();
        final Button button = new Button();
        GridPane.setHalignment(button, HPos.CENTER);

        stageForEnterPort.setTitle("Подключите устройство");
        label.setText("Пожалуйста подключите устройство MeshLogic!");
        grid2.add(label, 0, 0);
        button.setText("Повторить");
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

        stageForEnterPort.setTitle("Введите имя порта");
        label.setText("В ваш компьютер подключено несколько\nустройств через последовательный порт.\n" +
                "Пожалуйста введите имя порта в который\nподключено устройство MeshLogic:");
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
                    error.setText("Пожалуйста введите имя порта!");
                } else {
                    error.setText("Неправильное имя порта!");
                }
            }
        });

        stageForEnterPort.show();
    }

    public void createChart(String chart, String period) throws IOException, ParseException {
        Stage primaryStage = new Stage();
        Map<Integer, Number> values;
        String tempTitle = "График температуры ";
        String humTitle = "График влажности ";

        NumberAxis x;
        NumberAxis y;

        if (chart.contains("Темп")) {
            primaryStage.setTitle(tempTitle);
            y = new NumberAxis();
            y.setLabel(chart + ", " + DEGREE);
        } else {
            primaryStage.setTitle(humTitle);
            y = new NumberAxis(0, 100, 10);
            y.setLabel(chart + ", %");
        }

        if (period.contains("час")) {
            x = new NumberAxis(0, 60, 5);
            x.setLabel("Минуты");
        } else if (period.contains("день")) {
            x = new NumberAxis(0, 23, 1);
            x.setLabel("Часы");
        } else {
            x = new NumberAxis(1, 7, 1);
            x.setLabel("Номер дня");
        }

        LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
        if (chart.contains("Темп")) {
            numberLineChart.setTitle(tempTitle + period);
        } else {
            numberLineChart.setTitle(humTitle + period);
        }
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(chart);
        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();

        if (period.contains("час")) {
            values = ResultParser.getValuesPerHourFromLog(chart.toLowerCase());
            for (Integer key : values.keySet()) {
                datas.add(new XYChart.Data(key, values.get(key)));
            }
        } else if (period.contains("день")) {
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
}
