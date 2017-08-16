package com.example.jdnew.androidcomponent.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.componentsdk.widget.video.CustomVideoView;
import com.example.jdnew.androidcomponent.R;

/**
 * Created by JDNew on 2017/8/7.
 */

public class VideoTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.activity_video_test);
        CustomVideoView customVideoView = new CustomVideoView(this , linearLayout);
        customVideoView.setDataSource("http://fairee.vicp.net:83/2016rm/0116/baishi160116.mp4");
        linearLayout.addView(customVideoView);

    }
}
