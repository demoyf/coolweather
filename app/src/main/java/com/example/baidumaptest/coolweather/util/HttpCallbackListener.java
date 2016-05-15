package com.example.baidumaptest.coolweather.util;

/**
 * Created by alone on 2016/5/13.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
