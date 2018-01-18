package com.fro.wifi_temhumcase;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.fro.wifi_temhumcase.R;
import com.fro.util.FROTemHum;
import com.fro.util.StreamUtil;

public class ConnectTask extends AsyncTask<Void, Data, Void> {


    private String ip;
    private int port;
    private String command = "";
    private Context context;
    private Data data;
    private int tem;
    private int hum;
    private Float dataTemp;

    private byte[] read_buff;


    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Boolean STATU = false;
    private Boolean CIRCLE = false;

    private DataCallBack callBack;

    public ConnectTask(Context context, Data data, DataCallBack callBack) {
        if (data == null) {
            data = new Data();
        }
        this.context = context;
        this.data = data;
        this.callBack = callBack;
    }

    public void setCommand(String string) {
        this.command = string;
    }

    public void setip(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Data... values) {
        //连接成功
        if (callBack != null) {
            switch (values[0].getStatus()) {
                case STATUS_CONNECT_FILED:
                    callBack.connectFailed(values[0]);
                    break;
                case STATUS_CONNECTION_LOST:
                    callBack.connectionLost();
                    break;
                case STATUS_CONNECTING:
                    break;
                case STATUS_CONNECTED:
                    callBack.connectSuccess(values[0]);
                    break;
                case STATUS_DATA_RECEIVING:
                    callBack.receiveData(values[0]);
                    break;
                case STATUS_NONE:
                    callBack.connectionLost();
                    break;
            }
        }
    }

    /**
     * 准备
     */
    @Override
    protected void onPreExecute() {
        Log.e("ConnectTask", String.format("正在连接到:%s---%s", ip, port));
        //连接开始
        if (callBack != null)
            callBack.onTaskStart();
    }

    /**
     * 子线程任务
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        mSocket = new Socket();
        mSocketAddress = new InetSocketAddress(this.ip, this.port);
        // socket连接
        try {
            mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
            if (mSocket.isConnected()) {
                Log.e("ConnectTask", String.format("已经连接到:%s---%s", ip, port));
                inputStream = mSocket.getInputStream();// 得到输入流
                outputStream = mSocket.getOutputStream();// 得到输出流
                //connect success
                setCIRCLE(true);
                data.setStatus(Data.ConnectStatus.STATUS_CONNECTED);
            } else {
                //connect failed
                setCIRCLE(false);
                data.setStatus(Data.ConnectStatus.STATUS_CONNECT_FILED);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setCIRCLE(false);
            data.setStatus(Data.ConnectStatus.STATUS_CONNECT_FILED);
            return null;
            //connect failed
        } finally {
            //更新连接状态
            publishProgress(data);
        }

        // 循环读取数据
        while (CIRCLE) {
            // 查询命令(温湿度)
            try {
                StreamUtil.writeCommand(outputStream, command);
                read_buff = StreamUtil.readData(inputStream);
                Thread.sleep(200);
            } catch (IOException e) {
                e.printStackTrace();
                //send or receive failed means lost connection
                data.setStatus(Data.ConnectStatus.STATUS_CONNECTION_LOST);
                publishProgress(data);
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataTemp = FROTemHum.getTemData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
            if (dataTemp != null) {
                data.setTem((int) (float) dataTemp);
            }
            dataTemp = FROTemHum.getHumData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
            if (dataTemp != null) {
                data.setHum((int) (float) dataTemp);
            }
            // TODO: 2018/1/17  解析光照数据
            dataTemp = FROTemHum.getSunData(Constant.SUN_LEN, Constant.SUN_NUM, read_buff);
            if (dataTemp != null) {
                data.setSun((int) (float) dataTemp);
            }
            data.setStatus(Data.ConnectStatus.STATUS_DATA_RECEIVING);
            // 更新界面
            publishProgress(data);
        }
        data.setStatus(Data.ConnectStatus.STATUS_CONNECTION_LOST);
        publishProgress(data);
        return null;
    }

    public void disconnect() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setCIRCLE(false);
        setSTATU(false);
        cancel(true);
    }

    /**
     * 判断socket是否还在连接
     *
     * @return
     */
    public Boolean isSuccess() {
        try {
            return mSocket.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 获取socket
     *
     * @return
     */
    public Socket getmSocket() {
        return mSocket;
    }

    /**
     * 获取输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 获取输出流
     *
     * @return
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Boolean getSTATU() {
        return STATU;
    }

    public void setSTATU(Boolean sTATU) {
        STATU = sTATU;
    }

    public Boolean getCIRCLE() {
        return CIRCLE;
    }

    public void setCIRCLE(Boolean cIRCLE) {
        CIRCLE = cIRCLE;
    }

}
