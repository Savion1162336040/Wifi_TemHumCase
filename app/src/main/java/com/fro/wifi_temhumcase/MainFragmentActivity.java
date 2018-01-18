package com.fro.wifi_temhumcase;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentActivity extends AppCompatActivity implements OnFragmentListener {
    ViewPager pager;
    TabLayout tabLayout;
    List<Fragment> list;
    MainFragmentAdapter mainFragmentAdapter;

    Fragment sunlightFragment;
    Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();
    }

    private void initData() {
        sunlightFragment = SunLightFragment.newInstance("光照");
        tempFragment = TempFragment.newInstance("温湿度");
        list.add(sunlightFragment);
        list.add(tempFragment);
        mainFragmentAdapter.notifyDataSetChanged();
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        pager = (ViewPager) findViewById(R.id.main_pager);
        list = new ArrayList<>();
        mainFragmentAdapter = new MainFragmentAdapter(this.getSupportFragmentManager(), list);
        pager.setAdapter(mainFragmentAdapter);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

