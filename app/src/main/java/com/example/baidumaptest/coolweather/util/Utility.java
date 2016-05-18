package com.example.baidumaptest.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.baidumaptest.coolweather.db.CoolWeatherDB;
import com.example.baidumaptest.coolweather.model.City;
import com.example.baidumaptest.coolweather.model.County;
import com.example.baidumaptest.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alone on 2016/5/13.
 */
// this class was use to analysis and handle the http response
public class Utility {
//     analysis and handle province data from http response
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
//            split by ,    because the data was divide by ,  like 01|北京,02|上海,03|天津,
            String[] allProvince = response.split(",");
//          then split the information of per province
            for(String p:allProvince){
//               妈的这真不会写了，要用正则表达式来分割省的信息，因为|(这是竖)需要一个\来转义，
// 而\本身就需要转义，所以是\\ |
                String[] array = p.split("\\|");
                Province province = new Province();
                province.setProvinceCode(array[0]);
                province.setProvinceName(array[1]);

//                sava Province
                coolWeatherDB.saveProvince(province);
            }
            return true;
        }
        return false;
    }

//    analysis and handle city data from http response
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,
    int provinceId) {
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities!=null&&allCities.length>0){
//                like we handle the province information
                for(String c:allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

//    analysis and handle country information from http response(request from country http)
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties!=null&&allCounties.length>0){
                for(String c:allCounties){
//                    we should know that code in the first index,and name was in the second one
                    String[] array = c.split("\\|");
                    County country = new County();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }
//    handle the json data which the http request response.
    public static void handleWeatherResponse(Context context,String response){
        try{
//           analysis JSON object from the response
            JSONObject jsonObject = new JSONObject(response);
//            get weatherInfo json object from jsonObject by afferent the value weatherinfo
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
//            then we can get the information from weatherInfo by their nature name
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String pubilshTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,pubilshTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
//save the weather information into the SharePreferences file
    private static void saveWeatherInfo(Context context, String cityName,
                                        String weatherCode, String temp1, String temp2,
                                        String weatherDesp, String pubilshTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",pubilshTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();


    }
}
