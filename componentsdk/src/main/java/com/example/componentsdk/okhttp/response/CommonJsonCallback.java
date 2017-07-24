package com.example.componentsdk.okhttp.response;

import android.os.Handler;
import android.os.Looper;


import com.example.componentsdk.okhttp.exception.OkhttpException;
import com.example.componentsdk.okhttp.listener.DisposeDataHandle;
import com.example.componentsdk.okhttp.listener.DisposeDataListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by JDNew on 2017/7/23.
 */

public class CommonJsonCallback implements Callback {

    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";
    protected final String COOKIE_STORE = "Set-Cookie";

    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle disposeDataHandle) {
        this.mListener = disposeDataHandle.mListener;
        this.mClass = disposeDataHandle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());


    }

    @Override
    public void onFailure(Call call, final IOException ioexception) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkhttpException(NETWORK_ERROR, ioexception));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }


        });
    }

    private void handleResponse(Object responseObj) {
        if (responseObj == null && responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkhttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            if (result.has(RESULT_CODE)) {
                if (result.getInt(RESULT_CODE) == RESULT_CODE_VALUE) {
                    if (mClass == null) {
                        mListener.onSuccess(result);
                    } else {
                        Gson gson = new Gson();
                        Object object = gson.fromJson(responseObj.toString(), mClass);
                        if (object != null) {
                            mListener.onSuccess(object);
                        } else {
                            mListener.onFailure(new OkhttpException(JSON_ERROR, EMPTY_MSG));
                        }
                    }
                } else {
                    mListener.onFailure(new OkhttpException(OTHER_ERROR, result.get(RESULT_CODE)));
                }
            }


        } catch (Exception e) {
            mListener.onFailure(new OkhttpException(OTHER_ERROR, e.getMessage()));
            e.printStackTrace();
        }


    }
}
