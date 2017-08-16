package com.example.componentsdk.widget.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.componentsdk.R;
import com.example.componentsdk.constant.SDKConstant;
import com.example.componentsdk.util.Utils;


/**
 * Created by JDNew on 2017/8/5.
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener, TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {


    private static final String TAG = "CustomVideoView";
    private static final int TIME_MSG = 0X01;//handler发送的消息
    private static final int TIME_INTERVAL = 1000;//时间间隔
    //视频状态
    private static final int STATE_ERROR = -1; //错误
    private static final int STATE_IDLE = 0; //空闲
    private static final int STATE_PLAYING = 1; //播放中
    private static final int STATE_PAUSING = 2; //暂停中
    //允许加载失败后总的尝试加载次数，超过了才算真正的加载失败
    private static final int LOAD_TOTAL_COUNT = 3;
    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager audioManager; //音频控制
    private Surface videoSurface;

    /**
     * Data
     */
    private String mUrl;//加载的视频地址
    private boolean isMute; //是否静音
    private int mScreenWidth, mDestationHeight;//屏幕的宽度，高度是16：9计算出来的

    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;//默认处于空闲状态

    private MediaPlayer mediaPlayer;
    private ScreenEventReceiver mScreenReceiver;
    private ADVideoPlayerListener adVideoPlayerListener;
    private ADFrameImageLoadListener mFrameLoadListener;
    private String mFrameURI;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //每隔一段时间，发送进度消息
                case TIME_MSG:
                    if (isPlaying()) {
                        adVideoPlayerListener.onBufferUpdate(getCurrentPosition());
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INTERVAL);
                    }
                    break;
            }
        }
    };

    /**
     * 获取当前播放位置
     *
     * @return
     */
    public int getCurrentPosition() {
        if (this.mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }



    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();

        registBroadcastReceiver();

    }


    private void initView() {

        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) layoutInflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = (TextureView) mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode();
    }

    /**
     * 小模式状态
     */
    private void initSmallLayoutMode() {
        LayoutParams layoutParams = new LayoutParams(mScreenWidth, mDestationHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(layoutParams);

        mMiniPlayBtn = (Button) mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = (ImageView) mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = (ImageView) mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = (ImageView) mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);

    }


    private void initData() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    public void setDataSource(String url) {
        this.mUrl = url;
    }

    /**
     * 注册锁屏/解锁监听广播
     */
    private void registBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, intentFilter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    adVideoPlayerListener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            adVideoPlayerListener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            adVideoPlayerListener.onClickVideo();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 检查MediaPlayer
     */
    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();

        }
    }

    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (videoSurface != null && videoSurface.isValid()) {
            mediaPlayer.setSurface(videoSurface);
        }else {
            stop();
        }

        return mediaPlayer;


    }


    /**
     * 加载视频url
     */
    public void load() {
        if (playerState != STATE_IDLE) {
            return;
        }

            try {
            showLoadingView();
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();
            mediaPlayer.setDataSource(mUrl);
            mediaPlayer.prepareAsync();


        } catch (Exception e) {

        }
    }


    public void seekAndPause(int position){
        if (playerState != STATE_PLAYING) {
            return;
        }

        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.pause();
                    handler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    //跳到指定点播放视频
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                    handler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 暂停视频
     */
    public void pause() {
        if (playerState != STATE_PLAYING) {
            return;
        }

        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
        }

        showPauseView(false);
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 恢复视频播放
     */
    public void resume() {
        if (playerState != STATE_PAUSING) {
            return;
        }

        if (!isPlaying()) {
            entryResumeState();
            showPauseView(true);
            mediaPlayer.start();
            handler.sendEmptyMessage(TIME_MSG);

        } else {

        }
    }

    private void entryResumeState() {
        setCurrentPlayState(STATE_PLAYING);
        setIsComplete(false);
        setIsRealPause(false);

    }

    public void setIsComplete(boolean isComplete) {
        mIsComplete = isComplete;
    }

    public void setIsRealPause(boolean isRealPause) {
        mIsRealPause = isRealPause;
    }

    /**
     * 回到初始状态
     */
    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }

        showPauseView(false);
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);
        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount++;
            load();
        } else {
            showPauseView(false);
        }

    }

    public void destroy() {

    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }


    /**
     * true is no voice
     *
     * @param mute
     */
    public void mute(boolean mute) {

        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }
    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();
    }

    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    private void setCurrentPlayState(int state) {
        playerState = state;
    }

    /**
     * 异步加载定帧图
     */
    private void loadFrameImage() {
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        mFrameView.setImageResource(R.drawable.xadsdk_img_error);
                    }
                }
            });
        }
    }

    public void setAdVideoPlayerListener(ADVideoPlayerListener adVideoPlayerListener) {
        this.adVideoPlayerListener = adVideoPlayerListener;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoSurface = new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayView();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (adVideoPlayerListener != null) {
                adVideoPlayerListener.onAdVideoLoadSuccess();
            }
            decideCanPlay();

        }
    }

    private void decideCanPlay() {
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT){
            setCurrentPlayState(STATE_PAUSING);
            resume();
        }
            //来回切换页面时，只有 >50,且满足自动播放条件才自动播放

        else{
            setCurrentPlayState(STATE_PLAYING);
            pause();
        }

    }

    /**
     * 视频播放完成回调
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (adVideoPlayerListener != null) {
            adVideoPlayerListener.onAdVideoPlayComplete();
        }

        setIsComplete(true);
        setIsRealPause(true);
        playBack();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * 视频播放失败回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return 返回true是你自己处理，false为系统处理
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        playerState = STATE_ERROR;
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            if (adVideoPlayerListener != null) {
                adVideoPlayerListener.onAdVideoLoadFailed();

            }

            showPauseView(false);

        }
        stop();

        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //锁屏
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
                //解锁
                case Intent.ACTION_USER_PRESENT:


                    if (playerState == STATE_PAUSING) {
                        if (isRealPause()) {
                            pause();
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
            }
        }
    }

    public interface ADVideoPlayerListener{
        public void onBufferUpdate(int time);

        public void onClickFullScreenBtn();

        public void onClickVideo();

        public void onClickBackBtn();

        public void onClickPlay();

        public void onAdVideoLoadSuccess();

        public void onAdVideoLoadFailed();

        public void onAdVideoPlayComplete();

    }


}
