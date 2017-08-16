package com.example.jdnew.androidcomponent.view.fragment.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.componentsdk.okhttp.listener.DisposeDataListener;
import com.example.jdnew.androidcomponent.R;

import com.example.jdnew.androidcomponent.adapter.CursorAdapter;
import com.example.jdnew.androidcomponent.module.recommand.BaseRecommandModel;
import com.example.jdnew.androidcomponent.network.http.RequestCenter;
import com.example.jdnew.androidcomponent.view.fragment.BaseFragment;
import com.example.jdnew.androidcomponent.view.home.HomeHeaderLayout;
import com.example.jdnew.androidcomponent.zxing.app.CaptureActivity;

/**
 * Created by JDNew on 2017/7/20.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private View mContentView;
    private Context mContext;
    private TextView qrcode_view;
    private TextView category_view;
    private TextView search_view;
    private ImageView loading_view;
    private ListView list_view;
    private BaseRecommandModel mRecommendData;
    private CursorAdapter mAdpater;
    private static final int REQUEST_QRCODE = 0x01;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestRecommendData();
    }

    private void requestRecommendData() {
        RequestCenter.requestRecommendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.d("TAG" , responseObj.toString());
                mRecommendData = (BaseRecommandModel) responseObj;
                showSuccessView();
            }

            @Override
            public void onFailure(Object responseObj) {
                showErrorView();
            }
        });
    }

    private void showErrorView() {

    }

    private void showSuccessView() {
        if (mRecommendData.data.list != null && mRecommendData.data.list.size() > 0) {
            loading_view.setVisibility(View.GONE);
            list_view.setVisibility(View.VISIBLE);
list_view.addHeaderView(new HomeHeaderLayout(getActivity() , mRecommendData.data.head));
            mAdpater = new CursorAdapter(getActivity() , mRecommendData.data.list);
            list_view.setAdapter(mAdpater);
            list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mAdpater.updateAdInScrollView();
                }
            });


        }else {
            showErrorView();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        initView(mContentView);
        setListener();
        return mContentView;
    }

    private void setListener() {
        qrcode_view.setOnClickListener(this);

    }

    private void initView(View view) {
        qrcode_view = (TextView) view.findViewById(R.id.qrcode_view);
        category_view = (TextView) view.findViewById(R.id.category_view);
        search_view = (TextView) view.findViewById(R.id.search_view);
        loading_view = (ImageView) view.findViewById(R.id.loading_view);
        list_view = (ListView) view.findViewById(R.id.list_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qrcode_view:
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_QRCODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QRCODE:
                if (resultCode == Activity.RESULT_OK) {
                    String code = data.getStringExtra("SCAN_RESULT");
                    if (code.contains("http") || code.contains("https")) {
//                        Intent intent = new Intent(mContext, AdBrowserActivity.class);
//                        intent.putExtra(AdBrowserActivity.KEY_URL, code);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


}
