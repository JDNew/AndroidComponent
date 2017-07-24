package com.example.componentsdk.okhttp;

import com.example.componentsdk.okhttp.https.HttpsUtils;
import com.example.componentsdk.okhttp.response.CommonJsonCallback;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by JDNew on 2017/7/21.
 */

public class CommonOkhttpClient {
   private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIME_OUT , TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(TIME_OUT , TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(TIME_OUT , TimeUnit.SECONDS);
        //允许重定向
        okHttpClientBuilder.followRedirects(true);
        //添加https支持
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        okHttpClientBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory() , HttpsUtils.initTrustManager());
        mOkHttpClient = okHttpClientBuilder.build();


    }

    public static Call sendRequest(Request request , CommonJsonCallback callback){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
