package com.fro.wifi_temhumcase;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017-12-29.
 */

public abstract class BaseFragment extends Fragment {
    private boolean isVisible = false;
    private boolean isFirst = true;
    private boolean isPrepare = false;

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
        lazyLoad();
    }

    private void lazyLoad() {
        if (isFirst && isPrepare && isVisible) {
            initData(true);
            isFirst = false;
        }
    }

    abstract void initData(boolean showLoading);
}
