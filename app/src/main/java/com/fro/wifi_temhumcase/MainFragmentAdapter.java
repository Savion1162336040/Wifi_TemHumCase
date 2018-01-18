package com.fro.wifi_temhumcase;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-29.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> tabList;
    private final FragmentManager fragmentManager;

    public MainFragmentAdapter(FragmentManager fm, List<Fragment> fragmentMap) {
        super(fm);
        this.fragmentManager = fm;
        this.tabList = fragmentMap;
//        formatFragments();
    }

//    private void formatFragments() {
//        fragmentList.clear();
//        for (Fragment tab : tabList) {
//            if (tab.enable()) {
//                addFragment(tab);
//            }
//        }
//    }

    private void addFragment(Fragment tab) {
        //Fragment fragment = NewsContentFragment.newInstance(tab.getTag(), tab);
        tabList.add(tab);
    }

    public void addTabs(List<Fragment> tabs) {
        tabList.addAll(tabs);
//        formatFragments();
    }

    public void addTab(Fragment tab) {
        tabList.add(tab);
//        formatFragments();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).getArguments().getString("tag");
    }

    @Override
    public Fragment getItem(int position) {
        return tabList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return Math.min(tabList.size(), tabList.size());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("FragmentAdapter", String.format("instantiateItem: position = %s", position));
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e("FragmentAdapter", String.format("destroyItem: position = %s,object = %s", position, object.toString()));
        super.destroyItem(container, position, object);
    }
}
