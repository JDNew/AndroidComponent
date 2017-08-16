package com.example.componentsdk.core.video;

/**
 * Created by JDNew on 2017/8/7.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.componentsdk.constant.SDKConstant;
import com.example.componentsdk.core.AdParameters;
import com.example.componentsdk.module.AdValue;
import com.example.componentsdk.util.Utils;
import com.example.componentsdk.widget.video.CustomVideoView;
import com.example.componentsdk.widget.video.VideoFullDialog;

import static android.os.Build.VERSION_CODES.M;

/**
 * 视频业务逻辑层
 */
public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {


    private AdSDKSlotListener mSlotListener;
    private AdValue mAdValue;
    private CustomVideoView mCustomVideoView;
    private Context mContext;
    private ViewGroup mParentView;
    private boolean canPause = false; //是否可自动暂停
    private int lastArea = 0; //防止将要滑入滑出时播放器状态的改变

    public VideoAdSlot(AdValue adValue, AdSDKSlotListener adSDKSlotListener) {
        mSlotListener = adSDKSlotListener;
        mAdValue = adValue;
        mParentView = adSDKSlotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mCustomVideoView = new CustomVideoView(mContext, mParentView);
        if (mCustomVideoView != null) {
            mCustomVideoView.setDataSource(mAdValue.resource);
            mCustomVideoView.setAdVideoPlayerListener(this);
        }

        mParentView.addView(mCustomVideoView);

    }

    /**
     * 实现滑入播放，滑出暂停
     */
    public void updateVideoInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //还未出现在屏幕上，不做处理
        if (currentArea <= 0) {
            return;
        }

        //listview的item滑出时会大小骤然增至100，所以在这里做处理
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }

        if (currentArea <= SDKConstant.VIDEO_SCREEN_PERCENT) {
            if (canPause) {
                pauseVideo();
                canPause = false;
            }
            lastArea = 0;
            mCustomVideoView.setIsComplete(false);
            mCustomVideoView.setIsRealPause(false);
            return;
        }

        if (isRealPause() || isComplete()) {
            pauseVideo();
            canPause = false;
        }

        //满足自动播放条件或者用户主动点击播放，开始播放
        if (Utils.canAutoPlay(mContext, AdParameters.getCurrentSetting())
                || isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            mCustomVideoView.setIsRealPause(false);
        } else {
            pauseVideo();
            mCustomVideoView.setIsRealPause(true); //不能自动播放则设置为手动暂停效果
        }
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(mCustomVideoView);
        VideoFullDialog videoFullDialog = new VideoFullDialog(mContext, mCustomVideoView, mAdValue, getPosition());
        videoFullDialog.setFullToSmallListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });

        videoFullDialog.setViewBundle(bundle);
        videoFullDialog.show();
    }

    private void bigPlayComplete() {
        if (mCustomVideoView.getParent() == null) {
            mParentView.addView(mCustomVideoView);
        }
        mCustomVideoView.isShowFullBtn(true);
        mCustomVideoView.mute(true);
        mCustomVideoView.setAdVideoPlayerListener(this);
        mCustomVideoView.seekAndPause(0);
        canPause = false;
    }

    private void backToSmallMode(int position) {

        if (mCustomVideoView.getParent() == null) {
            mParentView.addView(mCustomVideoView);
        }

        mCustomVideoView.isShowFullBtn(true);
        mCustomVideoView.mute(true);
        mCustomVideoView.setAdVideoPlayerListener(this);
        mCustomVideoView.seekAndResume(position);

    }

    @Override
    public void onClickVideo() {
//点击视频跳转到个webview，不写。
    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadSuccess();
        }

    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadFailed();
        }
    }

    @Override
    public void onAdVideoPlayComplete() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadComplete();
        }
        mCustomVideoView.setIsRealPause(true);
    }

    /**
     * 获取当前播放位置
     *
     * @return
     */
    private int getPosition() {
        return mCustomVideoView.getCurrentPosition() / SDKConstant.MILLION_UNIT;
    }

    /**
     * 获取视频总时间
     *
     * @return
     */
    private int getDuration() {
        return mCustomVideoView.getDuration() / SDKConstant.MILLION_UNIT;
    }

    private boolean isPlaying() {
        if (mCustomVideoView != null) {
            return mCustomVideoView.isPlaying();
        }
        return false;
    }

    private boolean isRealPause() {
        if (mCustomVideoView != null) {
            return mCustomVideoView.isRealPause();
        }
        return false;
    }

    private boolean isComplete() {
        if (mCustomVideoView != null) {
            return mCustomVideoView.isComplete();
        }
        return false;
    }


    private void pauseVideo() {
        if (mCustomVideoView != null) {
            mCustomVideoView.seekAndPause(0);//当视频滑出屏幕，然后返回时，要从头开始播放，所以设为0
        }
    }

    private void resumeVideo() {
        if (mCustomVideoView != null) {
            mCustomVideoView.resume();
        }
    }

    //传递消息到appcontext层
    public interface AdSDKSlotListener {

        public ViewGroup getAdParent();

        public void onAdVideoLoadSuccess();

        public void onAdVideoLoadFailed();

        public void onAdVideoLoadComplete();

        public void onClickVideo(String url);
    }
}
