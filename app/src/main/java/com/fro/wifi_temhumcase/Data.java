package com.fro.wifi_temhumcase;

public class Data {
    public enum ConnectStatus {
        STATUS_DATA_RECEIVING,
        STATUS_CONNECTED,
        STATUS_CONNECTING,
        STATUS_CONNECT_FILED,
        STATUS_CONNECTION_LOST,
        STATUS_NONE
    }

    private ConnectStatus status = ConnectStatus.STATUS_NONE;
    //温度
    private int tem;
    //湿度
    private int hum;
    //光照
    private int sun;

    public int getTem() {
        return tem;
    }

    public void setTem(int tem) {
        this.tem = tem;
    }

    public int getHum() {
        return hum;
    }

    public int getSun() {
        return sun;
    }

    public void setSun(int sun) {
        this.sun = sun;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public ConnectStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectStatus status) {
        this.status = status;
    }
}
