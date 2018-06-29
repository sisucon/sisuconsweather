package com.example.sisucon.sisuconsweather.weatherDB;

import org.litepal.crud.DataSupport;

public class CheckBoxIsT extends DataSupport {
    boolean check;

    public CheckBoxIsT(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
