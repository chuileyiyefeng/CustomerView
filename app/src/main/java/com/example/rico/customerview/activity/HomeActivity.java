package com.example.rico.customerview.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.rico.customerview.fragment.HomeFragment3;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.ViewPagerAdapter;
import com.example.rico.customerview.fragment.HomeFragment1;
import com.example.rico.customerview.fragment.HomeFragment2;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeActivity extends BaseActivity {
    TabLayout tab;
    ViewPager vp;

    @Override
    public int bindLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void doBusiness() {
        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("view");
        strings.add("viewGroup");
        strings.add("other");
        for (int i = 0; i < strings.size(); i++) {
            tab.addTab(tab.newTab());
        }
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new HomeFragment1());
        list.add(new HomeFragment2());
        list.add(new HomeFragment3());
        vp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), list));
        tab.setupWithViewPager(vp);
        for (int i = 0; i < strings.size(); i++) {
            tab.getTabAt(i).setText(strings.get(i));
        }

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String value = applicationInfo.metaData.getString("CHANNEL_NAME");
            Log.e("CHANNEL_NAME", "doBusiness: " + value);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        TabLayout.Tab tabItem = tab.getTabAt(0);
//        if (tabItem != null) {
//            BadgeDrawable drawable = tabItem.getOrCreateBadge();
//            drawable.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
//            drawable.setBadgeTextColor(ContextCompat.getColor(this, R.color.white));
//            drawable.setNumber(6);
//        }
    }
}
