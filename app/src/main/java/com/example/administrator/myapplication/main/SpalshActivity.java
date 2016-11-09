package com.example.administrator.myapplication.main;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.administrator.myapplication.BluetoothChat.BluetoothChatActivity;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.loadingView.view.WSEatBeans;

import butterknife.Bind;
import utils.BaseActivity;

/**
 * Created by 46404 on 2016/11/9.
 */

public class SpalshActivity extends BaseActivity {

    @Bind(R.id.load_eat)
    WSEatBeans loadEat;
   // private WSEatBeans loadEat;
    private Handler handler;
    /**
     * 延时加载
     */
    private static final long WAIT_TIME = 2990l;

    @Override
    public void setContentView() {
        setContentView(R.layout.acticity_spalsh);
      //  loadEat = (WSEatBeans) findViewById(R.id.load_eat);
    }

    @Override
    public void initData() {
        handler = new Handler(Looper.myLooper());
        loadEat.startAnimator();
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SpalshActivity.this, BluetoothChatActivity.class));
                finish();
            }
        }, WAIT_TIME);
    }

}
