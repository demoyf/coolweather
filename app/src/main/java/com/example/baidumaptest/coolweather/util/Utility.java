package com.example.baidumaptest.coolweather.util;

import android.text.TextUtils;

import com.example.baidumaptest.coolweather.db.CoolWeatherDB;
import com.example.baidumaptest.coolweather.model.City;
import com.example.baidumaptest.coolweather.model.County;
import com.example.baidumaptest.coolweather.model.Province;

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
}
