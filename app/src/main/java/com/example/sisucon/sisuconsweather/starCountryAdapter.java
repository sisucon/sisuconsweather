package com.example.sisucon.sisuconsweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sisucon.sisuconsweather.weatherDB.Country;
import com.example.sisucon.sisuconsweather.weatherDB.StarCountry;

import org.litepal.crud.DataSupport;

import java.util.zip.Inflater;

public class starCountryAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;

    public starCountryAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return DataSupport.findAll(StarCountry.class).size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = layoutInflater.inflate(R.layout.star_country_item,parent,false);
        TextView cityName,cityCode;
        cityName = rootView.findViewById(R.id.star_country_name);
        cityCode = rootView.findViewById(R.id.star_country_id);
        StarCountry starCountry = DataSupport.findAll(StarCountry.class).get(position);
        cityName.setText(starCountry.getCountryName());




        cityCode.setText(starCountry.getWeatherID());
        return rootView;
    }
}
