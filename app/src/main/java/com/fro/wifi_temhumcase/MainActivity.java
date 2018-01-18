package com.fro.wifi_temhumcase;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.AsyncTask;

public class MainActivity extends Fragment {

    private static String TAG = "MainActivity";

    private EditText ip_et;
    private EditText ip_et2;
    private EditText port_et;
    private EditText port_et2;
    private SwitchCompat connect_tb;
    private TextView error_tv;
    private TextView tem_tv;
    private TextView hum_tv;
    private TextView sun_tv;

    private Context context;
    private ConnectTask connectTask;
    private ConnectTask sunConnectTask;
    private Data data;


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 控件
        bindView();
        // 数据
        initData();
        // 事件
        initEvent();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        context = getActivity();
        data = new Data();
        error_tv.setText("请点击连接！");
        ip_et.setText(Constant.IP);
        port_et.setText(String.valueOf(Constant.port));
    }

    /**
     * 绑定控件
     */
    private void bindView() {
//        ip_et = (EditText) view.findViewById(R.id.ip_et);
//        ip_et2 = (EditText) view.findViewById(R.id.ip_et2);
//        port_et = (EditText) view.findViewById(R.id.port_et);
//        port_et2 = (EditText) view.findViewById(R.id.port_et2);
//        connect_tb = (SwitchCompat) view.findViewById(R.id.connect_tb);
//        error_tv = (TextView) view.findViewById(R.id.error_tv);
//        tem_tv = (TextView) view.findViewById(R.id.tem_tv);
//        hum_tv = (TextView) view.findViewById(R.id.hum_tv);
//        sun_tv = (TextView) view.findViewById(R.id.sun_tv);
    }

    final DataCallBack<Data> suncallBack = new DataCallBack<Data>() {
        @Override
        public void receiveData(Data o) {
            error_tv.setText("连接成功");
            error_tv.setTextColor(context.getResources().getColor(R.color.green));
            sun_tv.setText(String.valueOf(o.getSun()));
            tem_tv.setText(String.valueOf(o.getTem()));
            hum_tv.setText(String.valueOf(o.getHum()));
        }

        @Override
        public void connectSuccess(Data data) {

        }

        @Override
        public void connectFailed(Data o) {
            error_tv.setText("连接失败");
            error_tv.setTextColor(context.getResources().getColor(R.color.red));
            sun_tv.setText(String.valueOf(o.getSun()));
            tem_tv.setText(String.valueOf(o.getTem()));
            hum_tv.setText(String.valueOf(o.getHum()));
        }

        @Override
        public void connectionLost() {

        }

        @Override
        public void onTaskStart() {
            //connect_tb.setChecked(false);
            error_tv.setText("正在连接...");
        }
    };


    /**
     * 按钮监听
     */
    private void initEvent() {
        // 连接
        connect_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 获取IP和端口
                    String IP = ip_et.getText().toString().trim();
                    String IP2 = ip_et2.getText().toString().trim();
                    String port = port_et.getText().toString().trim();
                    String port2 = port_et2.getText().toString().trim();
                    if (checkIpPort(IP, port)) {
                        Constant.IP = IP;
                        Constant.port = Integer.parseInt(port);
                    } else {
                        Toast.makeText(context, "IP和端口输入不正确,请重输！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 开启任务
                    if (!TextUtils.isEmpty(IP) && !TextUtils.isEmpty(port)) {
                        connectTask = new ConnectTask(context, data, suncallBack);
                        connectTask.setip(IP, Integer.valueOf(port));
                        connectTask.setCIRCLE(true);
                        connectTask.execute();
                    }
                    if (!TextUtils.isEmpty(IP2) && !TextUtils.isEmpty(port2)) {
                        sunConnectTask = new ConnectTask(context, data, suncallBack);
                        sunConnectTask.setCIRCLE(true);
                        sunConnectTask.setip(IP2, Integer.valueOf(port2));
                        sunConnectTask.execute();
                    }
                } else {
                    // 取消任务
                    if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
                        connectTask.setCIRCLE(false);
                        connectTask.setSTATU(false);
                        // 如果Task还在运行，则先取消它
                        connectTask.cancel(true);
                        try {
                            connectTask.getmSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (sunConnectTask != null && sunConnectTask.getStatus() == AsyncTask.Status.RUNNING) {
                        sunConnectTask.setCIRCLE(false);
                        sunConnectTask.setSTATU(false);
                        // 如果Task还在运行，则先取消它
                        sunConnectTask.cancel(true);
                        try {
                            sunConnectTask.getmSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    error_tv.setTextColor(context.getResources().getColor(R.color.gray));
                    error_tv.setText("请点击连接！");
                }
            }
        });
    }

    /**
     * IP地址可用端口号验证，可用端口号（1024-65536）
     *
     * @param IP
     * @param port
     * @return
     */
    private boolean checkIpPort(String IP, String port) {
        boolean isIpAddress = false;
        boolean isPort = false;

        if (IP == null || IP.length() < 7 || IP.length() > 15 || "".equals(IP)
                || port == null || port.length() < 4 || port.length() > 5) {
            return false;
        }
        //判断IP格式和范围
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(IP);

        isIpAddress = mat.find();

        //判断端口
        int portInt = Integer.parseInt(port);
        if (portInt > 1024 && portInt < 65536) {
            isPort = true;
        }

        return (isIpAddress && isPort);
    }

//    @Override
//    public void finish() {
//        super.finish();
//        // 取消任务
//        if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
//            connectTask.setCIRCLE(false);
//            connectTask.setSTATU(false);
//            // 如果Task还在运行，则先取消它
//            connectTask.cancel(true);
//            try {
//                connectTask.getmSocket().close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
