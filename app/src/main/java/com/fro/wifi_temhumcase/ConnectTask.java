package com.fro.wifi_temhumcase;

import android.content.Context;
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

public class ConnectTask extends AsyncTask<Void, Void, Void> {


    private String ip;
    private int port;
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

    public void setip(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        if (STATU) {
//            error_tv.setTextColor(context.getResources().getColor(R.color.green));
//            error_tv.setText("连接正常！");
            if (callBack != null)
                callBack.receiveData(data);
        } else {
//            error_tv.setTextColor(context.getResources().getColor(R.color.red));
//            error_tv.setText("连接失败！");
            if (callBack != null)
                callBack.connectFailed(data);
        }
//        tem_tv.setText(String.valueOf(data.getTem()));
//        hum_tv.setText(String.valueOf(data.getHum()));
//        sun_tv.setText(String.valueOf(data.getSun()));
    }

    /**
     * 准备
     */
    @Override
    protected void onPreExecute() {
//		error_tv.setText("正在连接...");
        Log.e("ConnectTask", String.format("正在连接到:%s---%s", ip, port));
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
        try {
            // socket连接
            mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
            if (mSocket.isConnected()) {
                setSTATU(true);
                Log.e("ConnectTask", String.format("已经连接到:%s---%s", ip, port));
                inputStream = mSocket.getInputStream();// 得到输入流
                outputStream = mSocket.getOutputStream();// 得到输出流
            } else {
                setSTATU(false);
            }

            // 循环读取数据
            while (CIRCLE) {
                // 查询温湿度
                StreamUtil.writeCommand(outputStream, Constant.TEMHUM_CHK);
                //发送获取光照命令
                StreamUtil.writeCommand(outputStream, Constant.SUN_CHK);
                outputStream.write("eof".getBytes());
                outputStream.flush();
                Thread.sleep(200);
                read_buff = StreamUtil.readData(inputStream);
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

                // 更新界面
                publishProgress();
                Thread.sleep(200);
            }
        } catch (IOException e) {
            setSTATU(false);
            publishProgress();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect(){
        if (mSocket!=null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setCIRCLE(false);
        setSTATU(false);
        cancel(true);
        if (callBack != null) {
            callBack.connectionLost();
        }
    }

    /**
     * 判断socket是否还在连接
     *
     * @return
     */
    public Boolean isSuccess() {
        try {
            return mSocket.isConnected();
        }catch (Exception e){
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
