package com.example.administrator.myapplication.BluetoothChat;

import android.net.Uri;
import android.widget.MediaController;


import com.example.administrator.myapplication.R;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import utils.BaseActivity;
import utils.TLogUtils;

/**
 * Created by lyn on 2016/6/3.
 */
public class PlayVideoActivity extends BaseActivity {
    private static final String TAG = "PlayVideoActivity";
    @Bind(R.id.VideoView)
    android.widget.VideoView VideoView;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_paly_video);
    }

    @Override
    public void initData() {
        String filePath = getIntent().getStringExtra("path");
        if (filePath != null) {
            doPlayVideo(new File(filePath));
        }
    }

    @Override
    public void setListener() {

    }

    public void doPlayVideo(File file) {
        TLogUtils.d(TAG, "doPlayVideo");
        Uri uri = Uri.fromFile(file);
        VideoView.setMediaController(new MediaController(this));
        VideoView.setVideoURI(uri);
        VideoView.start();
        VideoView.requestFocus();
    }

    @OnClick(R.id.rl_cancel)
    public void cancle() {
        finish();
    }

}
