package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.administrator.myapplication.BluetoothChat.ImageDetailActivity;
import com.example.administrator.myapplication.BluetoothChat.config.CacheConfig;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;

import java.io.File;
import java.util.Date;

import utils.Base64Utils;
import utils.FileUtil;
import utils.LYNBitmapUtils;
import utils.ThreadUtils;
import utils.GlideUtils;

/**
 * Created by 46404 on 2016/11/9.
 */

public class ShowPicUtil {

    public static final String EXTENSION = ".png";

    public static void showPic(Handler handler, final Context mContext, final BluChatMsgBean message, final ProgressBar pb_outgoing, final ImageView iv_pic) {

        if (handler == null) {
            synchronized (Handler.class) {
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 1) {
                                iv_pic.setVisibility(View.VISIBLE);
                                pb_outgoing.setVisibility(View.GONE);
                                String filePath = (String) msg.obj;
                                GlideUtils.display(mContext, iv_pic, filePath);
                            }
                        }
                    };
                }
            }
        }

        String savepath = getPicFilePath(mContext);//文件地址

        if (new File(message.getFilePath()).exists()) {
            pb_outgoing.setVisibility(View.GONE);
            iv_pic.setVisibility(View.VISIBLE);
            savepath = message.getFilePath();
            GlideUtils.display(mContext, iv_pic, savepath);
        } else {
            iv_pic.setVisibility(View.GONE);
            pb_outgoing.setVisibility(View.VISIBLE);
            final Handler finalHandler = handler;
            final String finalSavepath = savepath;
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Base64Utils.decoderBase64File(message.getContent(), finalSavepath);
                        message.setFilePath(finalSavepath);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = finalSavepath;
                        finalHandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //添加大图预览
        final String finalSavepath1 = savepath;
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageDetailActivity.class);
                intent.putExtra(ImageDetailActivity.FILEPATH, finalSavepath1);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(iv_pic, iv_pic.getWidth() / 2, iv_pic.getHeight() / 2, 0, 0);
                ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
            }
        });
    }

    public static String getPicFilePath(Context context) {
        return FileUtil.getDiskFileDir(context, CacheConfig.PIC_BLU) + "/" + new Date() + EXTENSION;
    }
}
