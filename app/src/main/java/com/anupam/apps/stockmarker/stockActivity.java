package com.anupam.apps.stockmarker;

import android.app.ActionBar;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.TextView;

public class stockActivity extends AppCompatActivity {
    public static String stockTicker;
    private static final String TAG = stockActivity.class.getSimpleName();
    private SectionsPageAdapter mSectionAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    static Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        stockTicker = getIntent().getStringExtra("Symbol");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(stockTicker);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mSectionAdapter.addFragment(new currentFragment(),"CURRENT");
        mSectionAdapter.addFragment(new historyFragment(),"HISTORICAL");
        mSectionAdapter.addFragment(new newsFragment(),"NEWS");
        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionAdapter);
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mTabLayout.setupWithViewPager(mViewPager);
        Log.d(TAG,"Stock Activity Intent: "+stockTicker);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
