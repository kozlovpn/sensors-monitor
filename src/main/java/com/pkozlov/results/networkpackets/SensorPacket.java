package com.pkozlov.results.networkpackets;

import javafx.beans.property.*;

/**
 * Created by pavel on 5/29/16.
 */
public class SensorPacket {

    private LongProperty srcId = new SimpleLongProperty();
    private IntegerProperty seq = new SimpleIntegerProperty();
    private FloatProperty rfTemperature = new SimpleFloatProperty();
    private FloatProperty temperature = new SimpleFloatProperty();
    private FloatProperty humidity = new SimpleFloatProperty();
    private FloatProperty voltage = new SimpleFloatProperty();


    public long getSrcId() {
        return srcId.get();
    }

    public void setSrcId(long srcId) {
        this.srcId.set(srcId);
    }

    public LongProperty getSrcIdProperty() {
        return srcId;
    }

    public int getSeq() {
        return seq.get();
    }

    public void setSeq(int seq) {
        this.seq.set(seq);
    }

    public IntegerProperty getSeqProperty() {
        return seq;
    }

    public float getRfTemperature() {
        return rfTemperature.get();
    }

    public void setRfTemperature(float rfTemperature) {
        this.rfTemperature.set(rfTemperature);
    }

    public FloatProperty getRfemperatureProperty() {
        return rfTemperature;
    }

    public float getTemperature() {
        return temperature.get();
    }

    public void setTemperature(Float temperature) {
        this.temperature.set(temperature);
    }

    public FloatProperty getTemperatureProperty() {
        return temperature;
    }

    public float getHumidity() {
        return humidity.get();
    }

    public void setHumidity(float humidity) {
        this.humidity.set(humidity);
    }

    public FloatProperty getHumidityProperty() {
        return humidity;
    }

    public float getVoltage() {
        return voltage.get();
    }

    public void setVoltage(float voltage) {
        this.voltage.set(voltage);
    }

    public FloatProperty getVoltageProperty() {
        return voltage;
    }
}
