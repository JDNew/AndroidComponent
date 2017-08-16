package com.example.componentsdk.core.video;

import android.view.ViewGroup;

import com.example.componentsdk.module.AdValue;
import com.google.gson.Gson;

/**
 * Created by JDNew on 2017/8/10.
 */

public class VideoAdSDK implements VideoAdSlot.AdSDKSlotListener {


    private ViewGroup mParentView;
    private VideoAdSlot mAdSlot;
    private AdValue mAdValue;
    public VideoAdSDK(ViewGroup parentView , String data){
        mParentView = parentView;
        Gson gson = new Gson();
        mAdValue = gson.fromJson(data , AdValue.class);
        load();


    }

    private void load() {
        if (mAdValue != null && mAdValue.resource != null) {
            mAdSlot = new VideoAdSlot(mAdValue , this);
        }
    }

    /**
     * 根据滑动距离来判断是否可以自动播放, 出现超过50%自动播放，离开超过50%,自动暂停
     */
    public void updateAdInScrollView() {
        if (mAdSlot != null) {
            mAdSlot.updateVideoInScrollView();
        }
    }

    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {

    }

    @Override
    public void onClickVideo(String url) {

    }
}
