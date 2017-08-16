package com.example.componentsdk.widget.video;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.example.componentsdk.R;
import com.example.componentsdk.module.AdValue;
import com.example.componentsdk.util.Utils;

/**
 * Created by JDNew on 2017/8/10.
 * 视频全屏对话框
 */

public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {
    private FullToSmallListener fullToSmallListener;
    private CustomVideoView mCustomVideoView;
    private AdValue mAdValue;
    private int mPosition; //小屏到全屏时的播放位置
    /**
     * 当前dialog是否为第一次创建且首次获得焦点
     */
    private boolean isFirst = true;

    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private ImageView mBackButton;
    private Bundle mStartBundle;
    private Bundle mEndBundle; //用于Dialog出入场动画
    //动画要执行的平移值
    private int deltaY;
    public VideoFullDialog(@NonNull Context context, CustomVideoView customVideoView
            , AdValue adValue , int position) {
        super(context, R.style.dialog_full_screen);
        mCustomVideoView = customVideoView;
        mAdValue = adValue;
        mPosition = position;
//
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }

    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mBackButton = (ImageView) findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);

        mCustomVideoView.setAdVideoPlayerListener(this);
        mCustomVideoView.mute(false);
        mParentView.addView(mCustomVideoView);

        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    //准备动画所需数据
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mCustomVideoView);
        /**
         * 将desationview移到originalview位置处
         */
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP)
                - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mCustomVideoView.setTranslationY(deltaY);
    }

    //准备入场动画
    private void runEnterAnimation() {
        mCustomVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }
    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {

    }

    public void onClickVideo() {

    }

    /**
     * 关闭按钮监听事件
     */
    public void onClickBackBtn() {
        dismiss();
        if (fullToSmallListener != null) {
            fullToSmallListener.getCurrentPlayPosition(mCustomVideoView.getCurrentPosition());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClickBackBtn();
    }

    /**
     * 焦点改变时的回调
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            mPosition = mCustomVideoView.getCurrentPosition();
            mCustomVideoView.pause();
        } else {
            if (isFirst) {
                mCustomVideoView.seekAndResume(mPosition);
            } else {
                mCustomVideoView.resume();
            }
        }
        isFirst = false;
    }

    @Override
    public void dismiss() {
        mParentView.removeView(mCustomVideoView);

        super.dismiss();

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mCustomVideoView != null) {
            mCustomVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoPlayComplete() {
dismiss();
        if (fullToSmallListener != null) {
            fullToSmallListener.playComplete();
        }
    }

    public void setFullToSmallListener(FullToSmallListener fullToSmallListener) {
        this.fullToSmallListener = fullToSmallListener;
    }

    public interface FullToSmallListener {
        void getCurrentPlayPosition(int position);//全屏播放中返回时的回调

        void playComplete();//全屏播放结束时回调
    }
}
