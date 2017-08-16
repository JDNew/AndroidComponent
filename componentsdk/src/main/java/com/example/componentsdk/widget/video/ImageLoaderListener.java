package com.example.componentsdk.widget.video;

import android.graphics.Bitmap;

/**
 * Created by JDNew on 2017/8/6.
 */

public interface ImageLoaderListener {
    /**
     * 如果图片下载不成功，传null
     *
     * @param loadedImage
     */
    void onLoadingComplete(Bitmap loadedImage);
}
