package com.fro.wifi_temhumcase;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentListener} interface
 * to handle interaction events.
 * Use the {@link SunLightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SunLightFragment extends BaseFragment implements View.OnClickListener, DataCallBack<Data>, CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tag";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentListener mListener;

    private SwitchCompat switchCompat;
    private TextView tv_sun;
    private TextView tv_msg;

    private EditText et_ip;
    private EditText et_port;

    private ConnectTask connectTask;


    private boolean isViewCreated = false;
    Data data;

    private View view;

    public SunLightFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SunLightFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SunLightFragment newInstance(String param1) {
        SunLightFragment fragment = new SunLightFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sun_light, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_ip = (EditText) view.findViewById(R.id.ip_et);
        et_port = (EditText) view.findViewById(R.id.port_et);
        switchCompat = (SwitchCompat) view.findViewById(R.id.connect_tb);
        tv_msg = (TextView) view.findViewById(R.id.connect_msg);
        tv_sun = (TextView) view.findViewById(R.id.sun_tv);

        switchCompat.setOnClickListener(this);
        switchCompat.setOnCheckedChangeListener(this);
        isViewCreated = true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        isViewCreated = false;
    }

    @Override
    void initData(boolean showLoading) {
        Log.e(TAG, TAG + " = initdata");

        et_ip.setText("192.168.42.78");
        et_port.setText("8898");
        tv_msg.setText("打开开关连接");
        tv_msg.setTextColor(Color.GRAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onpause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onresume");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_tb:
                if (switchCompat.isChecked()) {
                    // TODO: 2018-01-18 连接
                    connect();
                    setDontShow(false);
                    createMaterialDialog("hehe","haha",this);
                } else {
                    disconnect();
                }
                break;
        }
    }

    private void connect() {
        String ip = et_ip.getText().toString().trim();
        int port = 0;
        try {
            port = Integer.parseInt(et_port.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "不合法的端口号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ip)) {
            et_ip.setError("不能为空");
            return;
        }
        if (port == 0) {
            et_port.setError("端口非法");
            return;
        }
        disconnect();
        connectTask = new ConnectTask(getActivity(), data, this);
        connectTask.setip(ip, port);
        connectTask.setCIRCLE(true);
        connectTask.setCommand(Constant.SUN_CHK);
        connectTask.execute();
    }

    private void disconnect() {
        if (connectTask != null && connectTask.isSuccess()) {
            connectTask.disconnect();
            connectTask = null;
        }
    }

    @Override
    public void receiveData(Data data) {
//        tv_msg.setText("连接成功");
//        tv_msg.setTextColor(Color.GREEN);
        tv_sun.setText(String.valueOf(data.getSun()));
        switchCompat.setChecked(true);
        // TODO: 2018/5/7  光照阀值判断
        if (data.getSun() > 100) {
            createDialog2("title,", "msg", this);
        }
    }

    @Override
    public void connectSuccess(Data data) {
        tv_msg.setText("连接成功");
        tv_msg.setTextColor(Color.GREEN);
        switchCompat.setChecked(true);
    }

    @Override
    public void connectFailed(Data data) {
        Log.e(TAG, TAG + ",connectFailed");
        tv_msg.setText("连接失败");
        tv_msg.setTextColor(Color.RED);
        switchCompat.setChecked(false);
        tv_sun.setText("0");
    }

    @Override
    public void connectionLost() {
        Log.e(TAG, TAG + ",connectionLost");
        tv_msg.setText("连接丢失，打开开关连接");
        tv_msg.setTextColor(Color.GRAY);
        tv_sun.setText("0");
        switchCompat.setChecked(false);
    }

    @Override
    public void onTaskStart() {
        Log.e(TAG, TAG + ",onTaskStart");
        tv_msg.setText("正在连接");
        tv_msg.setTextColor(Color.BLACK);
        tv_sun.setText("0");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, TAG + "  onHiddenChanged =" + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, TAG + "  setUserVisibleHint =" + isVisibleToUser);
        if (isViewCreated) {
            if (isVisibleToUser) {
                //connect();
            } else {
                disconnect();
                dismissDialog();
            }
        }
    }

    public static String TAG = "SunLightFragment";

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
