package com.example.administrator.myapplication.BluetoothChat.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.administrator.myapplication.BluetoothChat.VideoPlayerActivity;
import com.example.administrator.myapplication.BluetoothChat.config.CacheConfig;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;

import java.io.File;
import java.util.Date;

import utils.Base64Utils;
import utils.FileUtil;
import utils.GlideUtils;
import utils.TLogUtils;
import utils.ThreadUtils;

/**
 * Created by 46404 on 2016/11/9.
 */

public class ShowVideoUtil {

    public static final String EXTENSION = ".png";
    public static final String EXTENSION1 = ".mp4";

    public static void showVideo(Handler handler, final Context mContext, final BluChatMsgBean message, final ProgressBar pb_outgoing, final ImageView iv_pic) {

        if (handler == null) {
            synchronized (Handler.class) {
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 1) {
                                String filePath = (String) msg.obj;
                                GlideUtils.display(mContext, iv_pic, filePath);
                            }
                            if (msg.what == 2) {
                                pb_outgoing.setVisibility(View.GONE);
                                iv_pic.setVisibility(View.VISIBLE);
                            }
                        }
                    };
                }
            }
        }

        String ConverSavepath = getPicFilePath(mContext);//视频封面文件地址
        String VideoSavePath = getVideoFilePath(mContext);//视频文件地址

        if (new File(message.getCoverFilePath()).exists()) { //封面逻辑
            ConverSavepath = message.getCoverFilePath();
            GlideUtils.display(mContext, iv_pic, ConverSavepath);
        } else {
            final Handler finalHandler = handler;
            final String finalSavepath = ConverSavepath;
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Base64Utils.decoderBase64File(message.getCoverFileString(), finalSavepath);
                        message.setCoverFilePath(finalSavepath);
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

        if (new File(message.getFilePath()).exists()) { //视频主体逻辑
            VideoSavePath = message.getFilePath();
            pb_outgoing.setVisibility(View.GONE);
            iv_pic.setVisibility(View.VISIBLE);

        } else {
            final String finalVideoSavePath1 = VideoSavePath;
            final Handler finalHandler1 = handler;
            ThreadUtils.newThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Base64Utils.decoderBase64File(message.getContent(), finalVideoSavePath1); //视频得编码
                        message.setFilePath(finalVideoSavePath1);
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = finalVideoSavePath1;
                        finalHandler1.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        final String finalVideoSavePath = VideoSavePath;
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                if (finalVideoSavePath == null) {
                    return;
                }
                intent.putExtra("path", finalVideoSavePath);
                mContext.startActivity(intent);
            }
        });
    }

    public static String getPicFilePath(Context context) {
        return FileUtil.getDiskFileDir(context, CacheConfig.PIC_BLU) + "/" + new Date() + EXTENSION;
    }

    public static String getVideoFilePath(Context context) {
        return FileUtil.getDiskFileDir(context, CacheConfig.VIDEO_BLU) + "/" + new Date() + EXTENSION1;
    }
}
