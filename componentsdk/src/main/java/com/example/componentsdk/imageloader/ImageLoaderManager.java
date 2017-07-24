package com.example.componentsdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.componentsdk.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by JDNew on 2017/7/24.
 */

public class ImageLoaderManager {

    private final static int THREAD_COUNT = 4;//最多可以有多少线程
    private final static int PROPRITY = 2; //图片加载的优先级
    private final static int DISK_CACHE_SIZE = 50 * 1024; //最多可以缓存多少图片
    private final static int CONNECTION_TIME_OUT = 5 * 1000;//连接超时的时间
    private final static int READ_TIME_OUT = 30 * 1000;//读取的超时时间

    private static ImageLoaderManager mInstance = null;
    private ImageLoader mLoader = null;

    private ImageLoaderManager(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(THREAD_COUNT) //配置图片下载线程的最大数量
                .threadPriority(Thread.NORM_PRIORITY - PROPRITY) //
                .denyCacheImageMultipleSizesInMemory() //防止缓存多套尺寸的图片到我们的内容中
                .memoryCache(new WeakMemoryCache()) //使用弱引用内存缓存
                .diskCacheSize(DISK_CACHE_SIZE) //分配硬盘缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //使用MD5命名文件
                .tasksProcessingOrder(QueueProcessingType.LIFO) //图片下载顺序
                .defaultDisplayImageOptions(getDefaultOptions())
                .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))
                .writeDebugLogs() //debug模式下输出日志
                .build();
        ImageLoader.getInstance().init(configuration);
        mLoader = ImageLoader.getInstance();


    }

    private DisplayImageOptions getDefaultOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.xadsdk_img_error)
                .showImageOnFail(R.drawable.xadsdk_img_error)
                .cacheInMemory(true) //设置图片可以缓存在内存
                .cacheOnDisk(true) //设置图片可以缓存在硬盘
                .bitmapConfig(Bitmap.Config.RGB_565) //使用的图片解码类型
                .decodingOptions(new BitmapFactory.Options()) //图片解码配置
                .build();

        return options;

    }


    public static ImageLoaderManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ImageLoaderManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderManager(context);
                }
            }
        }

        return mInstance;
    }

    public void displayImage(ImageView imageView , String url , DisplayImageOptions options , ImageLoadingListener listener){
        if (mLoader != null) {
            mLoader.displayImage(url , imageView , options , listener);
        }

    }

    public void displayImage(ImageView imageView , String url){
        displayImage(imageView , url , null , null);
    }

    public void displayImage(ImageView imageView , String url , ImageLoadingListener listener){
        displayImage(imageView , url , null , listener);
    }

}
