package com.example.baidumaptest.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alone on 2016/5/13.
 */
//this class was use to handle Http request
public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
         new Thread(new Runnable() {
             HttpURLConnection connection;
             @Override
             public void run() {
                 try{
                     URL url = new URL(address);
//                     get connection by url open
                     connection = (HttpURLConnection)url.openConnection();
//                     then we should choose what we want to operate.the method was setRequestMethod,argument was Upper String
                     connection.setRequestMethod("GET");
//                     set connect time out,which was the app loss of response for connect
                     connection.setConnectTimeout(8000);
//                     set read time out
                     connection.setReadTimeout(8000);
//                     get input stream instance
                     InputStream in = connection.getInputStream();
//                 the buffered reader help us read the character.
//                 base on Input stream reader.the input stream reader can read the byte from input stream
                     BufferedReader bf = new BufferedReader(new InputStreamReader(in));
//                     String builder can multiple append
                     StringBuilder resopnse = new StringBuilder();
//                     line was use to jugde does have next line,if it has,append line to the bulider
                     String line;
                     while((line=bf.readLine())!=null){
                        resopnse.append(line);
                     }
                     if(listener!=null){
                         listener.onFinish(resopnse.toString());
                     }
                 }catch (Exception e){
                     if(listener!=null){
                         listener.onError(e);
                     }
                    e.printStackTrace();
                 }finally {
//                     remember to close connect(disconnect)
                     if(connection!=null){
                        connection.disconnect();
                     }
                 }
             }
         }).start();
    }
}
