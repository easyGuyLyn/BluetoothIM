package com.example.administrator.myapplication.main;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.myapplication.BluetoothChat.BluetoothChatActivity;
import com.example.administrator.myapplication.JellyViewPager.JellyViewPagerActivity;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.TestXUtils.activity.X3Activity;
import com.example.administrator.myapplication.WeixinCrop.WeixinCropActivity;
import com.example.administrator.myapplication.calender.CalederActivity;
import com.example.administrator.myapplication.crop.CropMasterActivity;
import com.example.administrator.myapplication.editValtor.EditTextFormExampleActivity;
import com.example.administrator.myapplication.loadingView.LoadingViewActivity;
import com.example.administrator.myapplication.mvcHelper.MovieDetailActivity;
import com.example.administrator.myapplication.notboringactionbar.NoBoringActionBarActivity;
import com.example.administrator.myapplication.squareprogressbar.demo.ProgerssBarActivity;
import com.example.administrator.myapplication.topfloat.TopFloatActivity;
import com.example.administrator.myapplication.ultraPullToRefresh.ultra.PtrClassicFrameLayout;
import com.example.administrator.myapplication.ultraPullToRefresh.ultra.PtrDefaultHandler;
import com.example.administrator.myapplication.ultraPullToRefresh.ultra.PtrFrameLayout;
import com.example.administrator.myapplication.ultraPullToRefresh.ultra.PtrHandler;
import com.example.administrator.myapplication.ultraPullToRefresh.ultra.header.MaterialHeader;
import com.example.administrator.myapplication.weixinPhotoPicker.WeixinPhotoPickerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.CommonUtils;

public class DemoActivity extends AppCompatActivity {
    protected PtrClassicFrameLayout mPtrFrameLayout;
    @Bind(R.id.container)
    CoordinatorLayout container;//snackbar 容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        mPtrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.material_style_ptr_frame_fragment_main);
        initMaterial();
        onRefresh();
    }

    public void initMaterial() {
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, CommonUtils.dip2px(this, 15), 0, CommonUtils.dip2px(this, 10));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setDurationToClose(100);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setLastUpdateTimeRelateObject(this);
    }


    @OnClick(R.id.ll_photopicke)
    public void picker() {
        Intent intent = new Intent(DemoActivity.this, WeixinPhotoPickerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_crop)
    public void CropMatser() {
        Intent intent = new Intent(DemoActivity.this, CropMasterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_cropAsWeixin)
    public void CropAsWeixin() {
        Intent intent = new Intent(DemoActivity.this, WeixinCropActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_JellyViewPager)
    public void JellyViewPager() {
        Intent intent = new Intent(DemoActivity.this, JellyViewPagerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_EditTextVator)
    public void EditText() {
        Intent intent = new Intent(DemoActivity.this, EditTextFormExampleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_NotBoringActionBar)
    public void NotBoringActionBar() {
        Intent intent = new Intent(DemoActivity.this, NoBoringActionBarActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_ProgreeBar)
    public void ProgreeBar() {
        Intent intent = new Intent(DemoActivity.this, ProgerssBarActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_Calender)
    public void calender() {
        Intent intent = new Intent(DemoActivity.this, CalederActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_TopFloat)
    public void TopFloat() {
        Intent intent = new Intent(DemoActivity.this, TopFloatActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_WeChatVideo)
    public void takeVideo() {
        Intent intent = new Intent(DemoActivity.this, X3Activity.class);
        startActivity(intent);

    }

    @OnClick(R.id.ll_Loadingview)
    public void Loadingview() {
        Intent intent = new Intent(DemoActivity.this, LoadingViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_loadmore)
    public void ll_loadmore() {
        Intent intent = new Intent(DemoActivity.this, MovieDetailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_viewpagerBrowser)
    public void ll_viewpagerBrowser() {

    }

    public void onRefresh() {
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(true);
            }
        }, 100);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                Snackbar.make(container, "刷新完毕", Snackbar.LENGTH_LONG).show();
                long delay = 0;
                mPtrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.refreshComplete();
                    }
                }, delay);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
