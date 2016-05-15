package com.example.baidumaptest.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alone on 2016/5/13.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
//    province_code 代表省级代号,所以应该是由这个代号来获取天气
    public static final  String CREATE_PROVINCE = "create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text,province_code text)";
    public static final  String CREATE_CITY = "create table City(" +
            "id integer primary key autoincrement," +
            "city_name text,city_code text,province_id integer)";
    public static final  String CREATE_COUNTRY = "create table County(id integer primary key autoincrement," +
            "county_name text,county_code text,city_id integer)";
    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        在此新建表
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
