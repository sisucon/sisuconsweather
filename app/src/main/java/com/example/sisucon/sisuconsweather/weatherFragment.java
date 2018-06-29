package com.example.sisucon.sisuconsweather;

import android.Manifest;
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
import com.example.sisucon.sisuconsweather.weatherDB.Province;
import com.example.sisucon.sisuconsweather.weatherDB.StarCountry;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class weatherFragment extends Fragment {
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
    private CheckBox checkBox;
    private Button navButton;
    private Location location;
    private boolean star;
    private Button starButton,userButton;
    private Country nowCountry;
    private List<Address> result  = null ;
    public Boolean isCheckServer = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View rootView = inflater.inflate(R.layout.activity_weather,container,false);
            bingPicImg = rootView.findViewById(R.id.bing_pic_img);
            drawerLayout = (DrawerLayout)rootView.findViewById(R.id.drawer_layout);
            userButton = rootView.findViewById(R.id.userButton);
            swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
            navButton = (Button)rootView.findViewById(R.id.homeButton);
            weatherImg = (ImageView)rootView.findViewById(R.id.weatherImg);
            drawerLayout = (DrawerLayout)rootView.findViewById(R.id.drawer_layout);
            drawerLayout.setAlpha((float)0.75);
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
            navButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            if (DataSupport.findAll(CheckBoxIsT.class).size()<=0)
            {
                CheckBoxIsT checkBoxIsT = new CheckBoxIsT(true);
                checkBoxIsT.save();
            }
                getAutoServer();
                loadBingPic();
                getLocation();
                List<String> countyList = new ArrayList<>();
                String cityName = getCity(getContext());
                if (cityName!=null)
                {
                    cityName = cityName.split("市")[0];
                    System.out.println(cityName);

                    if (DataSupport.where("countryName = ?",cityName).find(Country.class).size()>0)
                    {
                       Country country = DataSupport.where("countryName = ?",cityName).find(Country.class).get(0);
                       nowCountry = country;
                       String weatherid = country.getWeatherID();
                       star = country.isStar();
                        requestWeather(weatherid);
                    }
                    else
                    {
                        initSQL();
                        Country country = DataSupport.where("countryName = ?",cityName).find(Country.class).get(0);
                        nowCountry = country;
                        String weatherid = country.getWeatherID();
                        star = country.isStar();
                        requestWeather(weatherid);
                    }
                }
            changeStar();

                userButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),UserActivity.class);
                        startActivity(intent);
                    }
                });


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

public void initSQL()
{
    String address = "http://guolin.tech/api/china";
    queryFromServer(address,"province",0);
    String provicnName = result.get(0).getAdminArea().split("省")[0];
        Province province = DataSupport.where("name = ?",provicnName).find(Province.class).get(0);
        String Cityaddress = "http://guolin.tech/api/china/" + province.getProvinceCode();
      queryFromServer(Cityaddress,"city",province.getId());

    for (int i = 0; i < DataSupport.findAll(City.class).size(); i++) {
        City city = DataSupport.findAll(City.class).get(i);
        String Countryaddress = "http://guolin.tech/api/china/" +city.getProvinceCode()+"/"+city.getCityCode();
       queryFromServer(Countryaddress,"county",city.getId());
    }
}

    public void queryFromServer(String address, final String type, final int id) {
        Utils.seedMessage(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                     Utility.handleCityResponse(responseText,id);
                } else if ("county".equals(type)) {
                     Utility.handleCountyResponse(responseText, id);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void getAutoServer()
    {
        try {
            Properties properties = new Properties();
            properties.load(getActivity().getAssets().open("properties"));
            String isCheck = properties.getProperty("isAutoUpdate");
            if (isCheck.equals("1"))
            {
                System.out.println("ischeck=true");
            }
            else
            {
                System.out.println(isCheck);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

    public void setNowCountry(Country country)
    {
nowCountry = country;
star = nowCountry.isStar();
changeStar();
        System.out.println("country = " + country);
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
    public void getLocation() {
        String locationProvider;
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        locationProvider = locationManager.getBestProvider(criteria, true);
        System.out.println("locationProvider = " + locationProvider);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("pass", "onCreate: 没有权限 ");
            return;
        }
        location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            String locationStr = "纬度：" + location.getLatitude() + "\n" + "经度：" + location.getLongitude();
            System.out.println(locationStr);
        }
        else
        {
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(getActivity(), "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
                return;
            }
            //获取Location
            location = locationManager.getLastKnownLocation(locationProvider);
        }
        //监视地理位置变化
    }

    public String getCity(Context context)
    {
        Geocoder geocoder = new Geocoder(context);
        try
        {
            if (location!=null)
            {
                 result = null;
                System.out.println(geocoder);
                result  = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                System.out.println(result.get(0).getLocality());
                return result.get(0).getLocality();
            }
            else
            {
                Toast.makeText(getActivity(),"您的api过低",Toast.LENGTH_SHORT);
                return null;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
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

    public void closeWindow()
    {
        drawerLayout.openDrawer(GravityCompat.START);
        drawerLayout.closeDrawers();
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
