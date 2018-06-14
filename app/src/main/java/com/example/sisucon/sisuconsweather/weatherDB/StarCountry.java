package com.example.sisucon.sisuconsweather.weatherDB;

import org.litepal.crud.DataSupport;

public class StarCountry extends DataSupport {
    private int id;
    private String countryName;
    private String weatherID;
    private int cityID;
    private boolean star = true;

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public StarCountry(Country country)
    {
        this.id = country.getid();
        this.countryName = country.getCountryName();
        this.weatherID = country.getWeatherID();
        this.cityID = country.getCityID();
    }
    public StarCountry(){};
    public StarCountry(String countryName, String weatherID, int cityID,boolean star) {
        this.countryName = countryName;
        this.weatherID = weatherID;
        this.cityID = cityID;
        this.star = star;
    }

    public int getid() {
        return id;
    }

    public void setid(int countryID) {
        this.id = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
