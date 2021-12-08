package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    public class PageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mData;
        public PageAdapter(@NonNull FragmentManager fm) {
            super(fm);
            mData = new ArrayList<>();
            mData.add(new RecordFragment());
            mData.add(new ViewFragment());
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles={"Whats on Today?","My Records"};
            return titles[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getCount() {
            return mData.size();
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
        else
        {
            backPressedTime = tempTime;
            FancyToast.makeText(getApplicationContext(), "오늘은 그만 펜을 내려둘까요?",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,false).show();
        }
    }
}