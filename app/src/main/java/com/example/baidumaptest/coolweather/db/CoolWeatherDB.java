package com.example.baidumaptest.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.baidumaptest.coolweather.model.City;
import com.example.baidumaptest.coolweather.model.Country;
import com.example.baidumaptest.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alone on 2016/5/13.
 */
public class CoolWeatherDB {
/**
 * DataBase Name,found what database that you want to op
 */
    public static final String DB_NAME = "cool_weather";
//    database version
    public static final int VERSION = 1;
//    get Cool weather database instance
    private static CoolWeatherDB coolWeatherDB;
//    get SQLiteDatabase instance;
    private SQLiteDatabase db;
//    privatization constract
    private CoolWeatherDB(Context context){
//        the first argument was Context(which one can operate the db),
//        the second one was   appoint db's name,third was gotten a cursor after we query the data
//        last was the version of db.we can update this integer larger mean we renew the db
        CoolWeatherOpenHelper dbHlep = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
//        get a writable db
        db = dbHlep.getWritableDatabase();
    }

//  return the instance of coolweather db
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

//    add province instance to db
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
//            i don't konw what role for the second argument
            db.insert("Province",null,values);
        }
    }
//    load all province information
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();
/**       query's argument mean：the first one was dataBase name(String),second column name(String array)
        third was the check of where (has ?),fourth mean what was the place holder(string array)
       fifth group by what column,sixth was having after group by.seventh was order by what
 */
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province p = new Province();
                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                p.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(p);
            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
//    add City instance data into db
    public void saveCity(City city){
        if(city!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }
//   load all city data from db by province id
    public List<City> loadCity(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",
                new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City c = new City();
                c.setId(cursor.getInt(cursor.getColumnIndex("id")));
                c.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                c.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                c.setProvinceId(provinceId);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
//    add Country information into db
    public void saveCountry(Country country){
        ContentValues values = new ContentValues();
        values.put("country_name", country.getCountryName());
        values.put("country_code",country.getCountryCode());
        values.put("city_id",country.getCityId());
        db.insert("Country",null,values);
    }
//    load all country information from db by city id
    public List<Country> loadCountry(int cityId){
        List<Country> list = new ArrayList<>();
        Cursor cursor = db.query("Country",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("counrty_code")));
                country.setCityId(cityId);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
