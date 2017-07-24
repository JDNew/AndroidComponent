package com.example.componentsdk.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by JDNew on 2017/7/21.
 *
 * 接收请求参数，生成request对象
 */

public class CommonRequest {

    /**
     *
     * @param url
     * @param params
     * @return 返回一个创建好的request对象
     */
    public static Request createPostRequest(String url , RequestParams params){

        FormBody.Builder mFormBodyBuild = new FormBody.Builder();
        if (params != null) {
            for(Map.Entry<String , String> entry : params.urlParams.entrySet()){
                mFormBodyBuild.add(entry.getKey() , entry.getValue());
            }
        }

        FormBody mFormBody = mFormBodyBuild.build();
        return new Request.Builder().url(url).post(mFormBody).build();
    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url , RequestParams params){
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String , String> entry : params.urlParams.entrySet()){
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        return new Request.Builder().url(urlBuilder.substring(0 , urlBuilder.length() - 1))
                .get().build();


    }
}
