package com.pkozlov.results.packets;

import javafx.beans.property.*;

/**
 * Created by pavel on 5/29/16.
 */
public class NeighborhoodPacket {

    private LongProperty srcId = new SimpleLongProperty();
    private IntegerProperty seq = new SimpleIntegerProperty();
    private LongProperty nodeId = new SimpleLongProperty();
    private IntegerProperty lQout = new SimpleIntegerProperty();
    private IntegerProperty lQin = new SimpleIntegerProperty();
    private IntegerProperty rssi = new SimpleIntegerProperty();
    private IntegerProperty elapseTime = new SimpleIntegerProperty();

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

    public long getNodeId() {
        return nodeId.get();
    }

    public void setNodeId(long nodeId) {
        this.nodeId.set(nodeId);
    }

    public LongProperty getNodeIdProperty() {
        return nodeId;
    }

    public int getlQout() {
        return lQout.get();
    }

    public void setlQout(int lQout) {
        this.lQout.set(lQout);
    }

    public IntegerProperty getLqoutProperty() {
        return lQout;
    }

    public int getlQin() {
        return lQin.get();
    }

    public void setlQin(int lQin) {
        this.lQin.set(lQin);
    }

    public IntegerProperty getLqinProperty() {
        return lQin;
    }

    public int getRssi() {
        return rssi.get();
    }

    public void setRssi(int rssi) {
        this.rssi.set(rssi);
    }

    public IntegerProperty getRssiProperty() {
        return rssi;
    }

    public int getElapseTime() {
        return elapseTime.get();
    }

    public void setElapseTime(int elapseTime) {
        this.elapseTime.set(elapseTime);
    }

    public IntegerProperty getElapseTimeProperty() {
        return elapseTime;
    }
}
