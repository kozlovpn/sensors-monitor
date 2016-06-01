package gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*final Label status = new Label("Loading");
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new EventHandler() {
                    public void handle(Event event) {
                        String statusText = status.getText();
                        status.setText(
                                ("Loading . . .".equals(statusText))
                                        ? "Loading ."
                                        : statusText + " ."
                        );
                    }
                }),
                new KeyFrame(Duration.millis(1000))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);*/


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
        grid.add(actiontarget, 1, 5);
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
    }

    @Override
    public void stop() {
        ComPortTest.closePort();
    }

    public static void main(String[] args) {
        ComPortTest.openPortAndGetData();
        launch(args);
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
