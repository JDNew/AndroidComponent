package com.example.jdnew.androidcomponent.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;

/**
 * Created by JDNew on 2017/7/20.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static MyApplication getInstance(){
        return myApplication;
    }
}
