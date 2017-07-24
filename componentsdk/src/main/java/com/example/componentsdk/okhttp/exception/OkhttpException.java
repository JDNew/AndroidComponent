package com.example.componentsdk.okhttp.exception;

/**
 * Created by JDNew on 2017/7/23.
 */

public class OkhttpException extends Exception {

    private int ecode;
    private Object emsg;
    public OkhttpException(int ecode , Object emsg){
this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}
