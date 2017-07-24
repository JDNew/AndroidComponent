package com.example.componentsdk.okhttp.listener;

/**
 * Created by JDNew on 2017/7/22.
 */

public class DisposeDataHandle {

    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;
    public String mSource = null;

    public DisposeDataHandle(DisposeDataListener disposeDataListener){
        this.mListener = disposeDataListener;
    }

    public DisposeDataHandle(DisposeDataListener disposeDataListener , Class<?> clazz){
        this.mListener = disposeDataListener;
        this.mClass = clazz;
    }

    public DisposeDataHandle(DisposeDataListener disposeDataListener , String source){
        this.mListener = disposeDataListener;
        this.mSource = source;
    }

}
