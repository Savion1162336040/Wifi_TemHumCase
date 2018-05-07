package com.fro.wifi_temhumcase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Administrator on 2017-12-29.
 */

public abstract class BaseFragment extends Fragment {
    private boolean isVisible = false;
    private boolean isFirst = true;
    private boolean isPrepare = false;
    protected static String PREFERENCE_NAME = "config";
    protected static String PREFERENCE_DONT_SHOW = "dont_show";

    private SharedPreferences sharedPreferences;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        isPrepare = true;
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        lazyLoad();
    }

    private void lazyLoad() {
        if (isFirst && isPrepare && isVisible) {
            initData(true);
            isFirst = false;
        }
    }

    abstract void initData(boolean showLoading);

    protected Dialog dialog;

    protected void createDialog2(String title, String msg, DialogInterface.OnClickListener clickListener) {
        if (dialog != null && dialog.isShowing())
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TextUtils.isEmpty(title) ? "tip" : title);
        builder.setMessage(TextUtils.isEmpty(msg) ? "what you want to say" : msg);
        builder.setCancelable(false);
        builder.setPositiveButton("ok", clickListener);
        dialog = builder.create();
        dialog.setOnShowListener(dialog1 ->
                createNotification(title, msg));
        dialog.show();

    }

    /**
     * 带不再提示dialog,使用MaterialDialog lib
     *
     * @param title
     * @param msg
     * @param clickListener
     */
    protected void createMaterialDialog(String title, String msg, DialogInterface.OnClickListener clickListener) {
        if (getDontShow()) {
            return;
        }
        if (dialog != null && dialog.isShowing())
            return;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        dialog = builder.title(TextUtils.isEmpty(title) ? "tip" : title)
                .content(TextUtils.isEmpty(msg) ? "what you want to say" : msg)
                .onAny((dialog1, which) -> {
                    int w = -1;
                    switch (which) {
                        case POSITIVE:
                            w = -1;
                            break;
                        case NEGATIVE:
                            w = -2;
                            break;
                        case NEUTRAL:
                            w = -3;
                            break;
                    }
                    clickListener.onClick(dialog1, w);
                })
                .positiveText("ok")
                .checkBoxPrompt("dont show agin(auto show when open the switch next time)", false, (buttonView, isChecked) -> setDontShow(isChecked))
                .showListener(dialog1 -> createNotification(title, msg))
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();
    }

    protected boolean getDontShow() {
        return sharedPreferences.getBoolean(PREFERENCE_DONT_SHOW, false);
    }

    protected void setDontShow(boolean boolea) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCE_DONT_SHOW, boolea);
        editor.commit();
    }

    protected void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private String NOTIFICATION_CHANNEL_ID = "1223";
    private String NOTIFICATION_CHANNEL_NAME = "LIMIT_WARNING";

    protected void createNotification(String title, String msg) {
        NotificationManager notificationManagerCompat = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManagerCompat.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getActivity());
        }
        builder.setContentText(TextUtils.isEmpty(msg) ? "what you want to say" : msg);
        builder.setContentTitle(TextUtils.isEmpty(title) ? "tip" : title);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_info_black_24dp));
        builder.setSmallIcon(R.drawable.ic_info_black_24dp);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setTicker(TextUtils.isEmpty(msg) ? "what you want to say" : msg);
        notificationManagerCompat.notify(0x100, builder.build());
    }
}
