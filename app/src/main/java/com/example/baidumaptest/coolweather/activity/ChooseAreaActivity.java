package com.example.baidumaptest.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.baidumaptest.coolweather.R;
import com.example.baidumaptest.coolweather.db.CoolWeatherDB;
import com.example.baidumaptest.coolweather.model.City;
import com.example.baidumaptest.coolweather.model.County;
import com.example.baidumaptest.coolweather.model.Province;
import com.example.baidumaptest.coolweather.util.HttpCallbackListener;
import com.example.baidumaptest.coolweather.util.HttpUtil;
import com.example.baidumaptest.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alone on 2016/5/13.
 */
public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
//    new an adapter for listview
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
//  province information list
    private List<Province> provinceList;
//    city list
    private List<City> cityList;
//    county list
    private List<County> countyList;
//    the province what we selected
    private Province selectedProvince;
//    the city what we selected;
    private City selectedCity;
//    current  level
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this setence should be writed before setContentView
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
//        get list view from layout and we can use it
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
//        first was a context,second was the item's layout,last was item's data
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
//        set adapter for the listview
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if the current level was province.After  we click,should show the city's list
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryConties();
                }
            }
        });
//        we shou load the province first.
        queryProvinces();
    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvince();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
//                add the province name to the datalist.and show it
                dataList.add(province.getProvinceName());
            }
//            renew it(the listview show)
            adapter.notifyDataSetChanged();
//            in the top
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
//            hasn't any data,we should load from server by http
            queryFromServer(null,"province");
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
//            we can get city data from server by Province code
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
//查询选中市内所有的县，优先从数据库中查询，如果查不到就取服务器查
    private void queryConties() {
        countyList = coolWeatherDB.loadCountry(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountryName());
            }
//            refresh the list view
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
//    then we should query data from server by their code and type
    private void queryFromServer(final String code,final String type) {
       String address;
//        not null and has content,result true
        if(!TextUtils.isEmpty(code)){
        address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
//            get province
        address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
//                after this operate,the data has been add into database
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB,response);

                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if(result){
//                   runOnUiThread 返回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            colseProgressDialog();
//                           show the data in listview。因为这时候数据库中已经有数据了
                            if(type.equals("province")){
                                queryProvinces();
                            }else if(type.equals("city")){
                                queryCities();
                            }else if(type.equals("county")){
                                queryConties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
//            通过runOnUiThread方法回到主线程处理逻辑,不需要start，仅仅是表明在主线程中处理这段逻辑
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                    colseProgressDialog();
                       Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void colseProgressDialog(){
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

//    最后需要捕获back键。根据当前级别来判断应该执行什么操作即返回到什么列表

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}
