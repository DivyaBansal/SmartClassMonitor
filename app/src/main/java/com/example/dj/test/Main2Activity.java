package com.example.dj.test;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main2Activity extends FragmentActivity {
    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    android.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            actionBar = getActionBar();
                            actionBar.setSelectedNavigationItem(position);                    }
                });
        Tab.setAdapter(TabAdapter);
        actionBar = getActionBar();
        //Enable Tabs on Action Bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
            @Override
                public void onTabReselected(android.app.ActionBar.Tab tab,
                                            FragmentTransaction ft) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                    Tab.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(android.app.ActionBar.Tab tab,
                                            FragmentTransaction ft) {
                    // TODO Auto-generated method stub

                }};
            //Add New Tab
            actionBar.addTab(actionBar.newTab().setText("Current Values").setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText("Visualize").setTabListener(tabListener));

        }

}

