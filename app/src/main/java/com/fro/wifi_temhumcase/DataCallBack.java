package com.fro.wifi_temhumcase;

/**
 * Created by Administrator on 2018-01-17.
 */

public interface DataCallBack<T> {

    void receiveData(T t);
    void connectSuccess(T t);
    void connectFailed(T t);
    void connectionLost();
    void onTaskStart();
}
