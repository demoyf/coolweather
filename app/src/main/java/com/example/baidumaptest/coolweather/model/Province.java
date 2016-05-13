package com.example.baidumaptest.coolweather.model;

/**
 * Created by alone on 2016/5/13.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
