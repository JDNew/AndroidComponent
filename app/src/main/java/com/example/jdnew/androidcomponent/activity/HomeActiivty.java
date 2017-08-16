package com.example.jdnew.androidcomponent.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jdnew.androidcomponent.R;
import com.example.jdnew.androidcomponent.activity.base.BaseActivity;
import com.example.jdnew.androidcomponent.view.fragment.home.HomeFragment;
import com.example.jdnew.androidcomponent.view.fragment.home.MessageFragment;
import com.example.jdnew.androidcomponent.view.fragment.home.MineFragment;

/**
 * Created by JDNew on 2017/7/20.
 */

public class HomeActiivty extends BaseActivity implements View.OnClickListener {

    private RelativeLayout content_layout;
    private TextView home_image_view;
    private RelativeLayout home_layout_view;
    private TextView fish_image_view;
    private RelativeLayout pond_layout_view;
    private TextView message_image_view;
    private RelativeLayout message_layout_view;
    private TextView mine_image_view;
    private RelativeLayout mine_layout_view;
    private LinearLayout linearLayout;
    private HomeFragment mHomeFragment;
    private MineFragment mMineFragment;
    private MessageFragment mMessageFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        initView();
        initData();
        setListener();

    }

    public void initView() {
        content_layout = (RelativeLayout) findViewById(R.id.content_layout);
        home_image_view = (TextView) findViewById(R.id.home_image_view);
        home_layout_view = (RelativeLayout) findViewById(R.id.home_layout_view);
        fish_image_view = (TextView) findViewById(R.id.fish_image_view);
        pond_layout_view = (RelativeLayout) findViewById(R.id.pond_layout_view);
        message_image_view = (TextView) findViewById(R.id.message_image_view);
        message_layout_view = (RelativeLayout) findViewById(R.id.message_layout_view);
        mine_image_view = (TextView) findViewById(R.id.mine_image_view);
        mine_layout_view = (RelativeLayout) findViewById(R.id.mine_layout_view);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        home_image_view.setBackgroundResource(R.drawable.comui_tab_home_selected);



    }

    @Override
    public void initData() {
        mHomeFragment = new HomeFragment();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout , mHomeFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void setListener() {
        home_layout_view.setOnClickListener(this);
        message_layout_view.setOnClickListener(this);
        mine_layout_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()){


           case R.id.home_layout_view:

                home_image_view.setBackgroundResource(R.drawable.comui_tab_home_selected);
                pond_layout_view.setBackgroundResource(R.drawable.comui_tab_pond);
                message_image_view.setBackgroundResource(R.drawable.comui_tab_message);
                mine_image_view.setBackgroundResource(R.drawable.comui_tab_person);

               hideFragment(mMineFragment , fragmentTransaction);
               hideFragment(mMessageFragment , fragmentTransaction);
               if (mHomeFragment==null) {
                   mHomeFragment = new HomeFragment();
                   fragmentTransaction.add(R.id.content_layout , mHomeFragment);
               }else {
                   fragmentTransaction.show(mHomeFragment);
               }
                break;
            case R.id.message_layout_view:
                message_image_view.setBackgroundResource(R.drawable.comui_tab_message_selected);
                home_image_view.setBackgroundResource(R.drawable.comui_tab_home);
                pond_layout_view.setBackgroundResource(R.drawable.comui_tab_pond);
                mine_image_view.setBackgroundResource(R.drawable.comui_tab_person);

                hideFragment(mMineFragment , fragmentTransaction);
                hideFragment(mHomeFragment , fragmentTransaction);
                if (mMessageFragment==null) {
                    mMessageFragment = new MessageFragment();
                    fragmentTransaction.add(R.id.content_layout , mMessageFragment);
                }else {
                    fragmentTransaction.show(mMessageFragment);
                }
                break;
            case R.id.mine_layout_view:
                mine_image_view.setBackgroundResource(R.drawable.comui_tab_person_selected);
                home_image_view.setBackgroundResource(R.drawable.comui_tab_home);
                pond_layout_view.setBackgroundResource(R.drawable.comui_tab_pond);
                message_image_view.setBackgroundResource(R.drawable.comui_tab_message);

                hideFragment(mMessageFragment , fragmentTransaction);
                hideFragment(mHomeFragment , fragmentTransaction);
                if (mMineFragment==null) {
                    mMineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.content_layout , mMineFragment);
                }else {
                    fragmentTransaction.show(mMineFragment);
                }
                break;
        }

        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment , FragmentTransaction fragmentTransaction){
        if(fragment != null){
            fragmentTransaction.hide(fragment);
        }
    }
}
