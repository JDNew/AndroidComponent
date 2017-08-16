package com.example.jdnew.androidcomponent.network.http;


import com.example.componentsdk.okhttp.CommonOkhttpClient;
import com.example.componentsdk.okhttp.listener.DisposeDataHandle;
import com.example.componentsdk.okhttp.listener.DisposeDataListener;
import com.example.componentsdk.okhttp.request.CommonRequest;
import com.example.componentsdk.okhttp.request.RequestParams;
import com.example.jdnew.androidcomponent.module.recommand.BaseRecommandModel;


/**
 * @author: vision
 * @function:
 * @date: 16/8/12
 */
public class RequestCenter {

    public static void postRequest(String url , RequestParams params , DisposeDataListener
                                   listener , Class<?> clazz){
        CommonOkhttpClient.get(CommonRequest.createGetRequest(url , params) , new DisposeDataHandle(listener , clazz));
    }

    public static void requestRecommendData(DisposeDataListener listener){
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND , null , listener , BaseRecommandModel.class);
    }

}
