package com.example.sisucon.sisuconsweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.sisucon.sisuconsweather.weatherDB.Country;
import com.example.sisucon.sisuconsweather.weatherDB.StarCountry;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private myAdapter adapter;
    public List<Fragment> viewList;
    private int nowPosition;
    int starNum;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       starNum = DataSupport.findAll(StarCountry.class).size()+1;
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }


        viewPager = (ViewPager)findViewById(R.id.container);
        adapter = new myAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewList = new ArrayList<Fragment>();
        viewList.add(new weatherFragment());
        System.out.println("size======="+DataSupport.findAll(StarCountry.class).size());
        for (int i = 0; i <  DataSupport.findAll(StarCountry.class).size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("weatherID", DataSupport.findAll(StarCountry.class).get(i).getWeatherID());
           weatherFragmentStar temp =  new weatherFragmentStar();
           temp.setArguments(bundle);
            viewList.add(temp);
            System.out.println( DataSupport.findAll(StarCountry.class).get(i).getCountryName());
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPosition= position;
                System.out.println("position = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    public  class myAdapter extends FragmentPagerAdapter
    {
        public myAdapter(FragmentManager fm){super(fm);}

        @Override
        public Fragment getItem(int position) {
            return viewList.get(position);
        }

        @Override
        public int getCount() {
            if (DataSupport.findAll(StarCountry.class).size()>starNum-1)
            {
                finish();
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }
            return starNum;
        }
    }

    public  void setWeatherid(Country country)
    {
        weatherFragment weatherFragment = (weatherFragment) viewList.get(0);
        weatherFragment.requestWeather(country.getWeatherID());
        weatherFragment.setNowCountry(country);
        weatherFragment.closeWindow();
    }

}
