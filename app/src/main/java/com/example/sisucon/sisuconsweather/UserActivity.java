package com.example.sisucon.sisuconsweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.example.sisucon.sisuconsweather.weatherDB.CheckBoxIsT;
import com.example.sisucon.sisuconsweather.weatherDB.StarCountry;

import org.litepal.crud.DataSupport;

public class UserActivity extends AppCompatActivity {
    private Switch aSwitch;
    private ListView starListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_layout);
        aSwitch = findViewById(R.id.autoServerCheck);
        starListView = findViewById(R.id.starListView);
        starListView.setAdapter(new starCountryAdapter(this));
        aSwitch.setChecked(DataSupport.findAll(CheckBoxIsT.class).get(0).isCheck());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DataSupport.deleteAll(CheckBoxIsT.class);
                CheckBoxIsT checkBoxIsT = new CheckBoxIsT(isChecked);
                checkBoxIsT.save();
            }
        });
    }
}
