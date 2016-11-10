package com.example.administrator.myapplication.BluetoothChat;

import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.administrator.myapplication.R;

import java.io.File;

import butterknife.Bind;
import utils.BaseActivity;
import utils.view.PinchImageView;

/**
 * Created by 46404 on 2016/11/10.
 */

public class ImageDetailActivity extends BaseActivity {

    public static final String FILEPATH = "ImageDetailPath";

    @Bind(R.id.img_detail_gesture)
    PinchImageView imgDetailGesture;

    private String filePath;

    @Override
    public void setContentView() {
        setContentView(R.layout.acticity_image_detail);
    }

    @Override
    public void initData() {
        if (getIntent().getStringExtra("ImageDetailPath") == null) return;
        filePath = getIntent().getStringExtra(FILEPATH);
    }

    @Override
    public void setListener() {
        imgDetailGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Uri uri;
        if (filePath.startsWith("http")) {
            uri = Uri.parse(filePath);
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        Glide.with(this)
                .load(uri)
                .placeholder(null)
                .error(R.drawable.default_error)
                .into(imgDetailGesture);
    }
}
