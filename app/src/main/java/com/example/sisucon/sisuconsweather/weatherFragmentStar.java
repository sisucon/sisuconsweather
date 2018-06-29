package com.example.sisucon.sisuconsweather;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sisucon.sisuconsweather.gson.Forecast;
import com.example.sisucon.sisuconsweather.gson.Weather;
import com.example.sisucon.sisuconsweather.service.AutoUpdateService;
import com.example.sisucon.sisuconsweather.util.Utility;
import com.example.sisucon.sisuconsweather.util.Utils;
import com.example.sisucon.sisuconsweather.weatherDB.CheckBoxIsT;
import com.example.sisucon.sisuconsweather.weatherDB.City;
import com.example.sisucon.sisuconsweather.weatherDB.Country;
import com.example.sisucon.sisuconsweather.weatherDB.StarCountry;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class weatherFragmentStar extends Fragment {
    private final static String MY_KEY = "edf6fa4ca78f4c89b4b187e23e99e674";
    private ImageView bingPicImg;
    private ScrollView weatherLayout;
    private ImageView weatherImg;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    public SwipeRefreshLayout swipeRefresh;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private boolean star =true;
    private Button starButton,userButton;
    private Country nowCountry;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View rootView = inflater.inflate(R.layout.weather_star,container,false);
            bingPicImg = rootView.findViewById(R.id.bing_pic_img);
            drawerLayout = (DrawerLayout)rootView.findViewById(R.id.drawer_layout);
            swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
            weatherImg = (ImageView)rootView.findViewById(R.id.weatherImg);
            swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
            bingPicImg = (ImageView)rootView.findViewById(R.id.bing_pic_img);
            weatherLayout = (ScrollView) rootView.findViewById(R.id.weather_layout);
            titleCity = (TextView) rootView.findViewById(R.id.title_city);
            titleUpdateTime = (TextView) rootView.findViewById(R.id.title_update_time);
            degreeText = (TextView) rootView.findViewById(R.id.degree_text);
            weatherInfoText = (TextView) rootView.findViewById(R.id.weather_info_text);
            forecastLayout = (LinearLayout) rootView.findViewById(R.id.forecast_layout);
            aqiText = (TextView) rootView.findViewById(R.id.aqi_text);
            pm25Text = (TextView) rootView.findViewById(R.id.pm25_text);
            comfortText = (TextView) rootView.findViewById(R.id.comfort_text);
            carWashText = (TextView) rootView.findViewById(R.id.car_wash_text);
            sportText = (TextView) rootView.findViewById(R.id.sport_text);
            starButton = (Button)rootView.findViewById(R.id.starButton);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(mWeatherId);
                }
            });
            userButton = rootView.findViewById(R.id.userButton);
            userButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),UserActivity.class);
                    startActivity(intent);
                }
            });

            mWeatherId = getArguments().getString("weatherID");
            nowCountry =  DataSupport.where("weatherID = ?", String.valueOf(mWeatherId)).find(Country.class).get(0);
            requestWeather(mWeatherId);
           loadBingPic();
            changeStar();
            starButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        star = !star;
                        System.out.println(star);
                        nowCountry.setStar(star);
                        changeStar();
                        updateStar(nowCountry,star);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            return rootView;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void changeStar()
    {
        if (star)
        {
            starButton.setBackgroundResource(R.drawable.ic_star);
        }
        else
        {
            starButton.setBackgroundResource(R.drawable.ic_unstar);
        }
    }


    public static void updateStar(Country country,Boolean star)
    {
        try {
            if (star)
            {
                country.setStar(star);
                country.save();
                StarCountry starCountry = new StarCountry(country);
                starCountry.save();
            }else
            {
                country.setStar(star);
                country.save();
                DataSupport.where("countryName = ?",String.valueOf(country.getCountryName())).find(StarCountry.class).get(0).delete();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + MY_KEY;
        if (DataSupport.findAll(CheckBoxIsT.class)!=null)
        {
            if (DataSupport.findAll(CheckBoxIsT.class).get(0).isCheck())
            {
                Intent intent = new Intent(getActivity(),AutoUpdateService.class);
               getActivity().startService(intent);
            }
        }
        Utils.seedMessage(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                            swipeRefresh.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }



    private void showWeatherInfo(Weather weather) {


        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        String weatherNum = weather.now.more.weatherCode;
        final String imgUrl = "https://cdn.heweather.com/cond_icon/"+weatherNum+".png";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity()).load(imgUrl).into(weatherImg);
            }
        });
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_items, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        Utils.seedMessage(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
