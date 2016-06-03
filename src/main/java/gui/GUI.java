package gui;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import jssc.SerialNativeInterface;
import serialport.ComPort;
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
import results.ResultParser;

import java.text.DecimalFormat;

/**
 * Created by pavel on 5/29/16.
 */
public class GUI extends Application {

    private Text rfTemperature = new Text("Init...");
    private Text temperature = new Text("Init...");
    private Text humidity = new Text("Init...");
    private Text voltage = new Text("Init...");
    private String DEGREE = "\u00b0С";
    public static final String portName = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*final Label temperature = new Label("Loading");
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new EventHandler() {
                    public void handle(Event event) {
                        String statusText = temperature.getText();
                        temperature.setText(
                                ("Loading . . .".equals(statusText))
                                        ? "Loading ."
                                        : statusText + " ."
                        );
                    }
                }),
                new KeyFrame(Duration.millis(1000))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();*/


        primaryStage.setTitle("MeshLogic Sensors Monitor");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));
        //
        final Text scenetitle = new Text("Real time monitor");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

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
        grid.add(hbBtn, 1, 4);
        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 5, 2, 1);
        actiontarget.setFont(Font.font("Tahoma", FontWeight.LIGHT, 20));
        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.BLUE);
                actiontarget.setText("Dew Point is " + calculateDewPoint() + DEGREE);
            }
        });

        Scene scene = new Scene(grid, 500, 475);
        primaryStage.setScene(scene);
        primaryStage.show();

        if (ComPort.isMultiplePorts.get()) {
            openWindowForEnterPort();
        }
    }

    @Override
    public void stop() {
        ComPort.closePort();
    }

    public static void main(String[] args) {
        ComPort.checkMultiplePorts();
        launch(args);
    }

    public static void openWindowForEnterPort() {
        final Stage stage = new Stage();
        stage.setTitle("Enter port name");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Label label = new Label("Your computer has multiple serial port connections.\n" +
                "Please enter the correct serial port name\nin which the MeshLogic device is connected:");
        grid.add(label, 0, 0);
        final TextField textField = new TextField();
        grid.add(textField, 0, 1);
        final Text error = new Text();
        error.setFill(Color.RED);
        grid.add(error, 0, 2);
        Button button = new Button("OK");
        button.setMaxWidth(100);
        GridPane.setHalignment(button, HPos.CENTER);
        grid.add(button, 0, 3);
        final Scene scene = new Scene(grid, 380, 250);
        stage.setScene(scene);
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String portName = textField.getText();
                int osType = SerialNativeInterface.getOsType();
                if (!portName.equals("") && portName.matches("^(ttyUSB|COM)\\d+$") && ((osType == SerialNativeInterface.OS_WINDOWS
                        && portName.contains("COM")) || (osType == SerialNativeInterface.OS_LINUX && portName.contains("tty")))) {
                    ComPort.openPortAndGetData(portName);
                    stage.close();
                } else if (portName.equals("")) {
                    error.setText("Please enter port name!");
                } else {
                    error.setText("Invalid port name!");
                }
            }
        });
        stage.show();
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
