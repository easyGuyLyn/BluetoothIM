package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.myapplication.BluetoothChat.VideoPlayerActivity;
import com.example.administrator.myapplication.BluetoothChat.config.CacheConfig;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;

import java.io.File;
import java.util.Date;

import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.views.SurfaceVideoView;
import utils.Base64Utils;
import utils.FileUtil;
import utils.ThreadUtils;
import utils.ToastUtils;

/**
 * Created by 46404 on 2016/11/9.
 */

public class ShowVideoUtil {

    public static final String EXTENSION = ".png";
    public static final String EXTENSION1 = ".mp4";

    public static void showVideo(Handler handler, final Context mContext, final BluChatMsgBean message, final ProgressBar pb_outgoing, final SurfaceVideoView svv) {

        if (handler == null) {
            synchronized (Handler.class) {
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 2) {
                                pb_outgoing.setVisibility(View.GONE);
                                svv.setVisibility(View.VISIBLE);
                                initSurfaceVideoView(svv, message.getFilePath(), mContext);
                            }
                        }
                    };
                }
            }
        }

        String VideoSavePath = getVideoFilePath(mContext);//视频文件地址

        //视频主体逻辑
        if (null != message.getFilePath()) {
            if (new File(message.getFilePath()).exists()) { //如果本地已经有了
                pb_outgoing.setVisibility(View.GONE);
                svv.setVisibility(View.VISIBLE);
                initSurfaceVideoView(svv, message.getFilePath(), mContext);

            } else {   //从json里拿
                final String finalVideoSavePath1 = VideoSavePath;
                final Handler finalHandler1 = handler;
                ThreadUtils.newThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Base64Utils.decoderBase64File(message.getContent(), finalVideoSavePath1);
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
        }

        svv.setOnClickListener(new View.OnClickListener() {  //预览全屏视频播放
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("path", message.getFilePath());
                mContext.startActivity(intent);
            }
        });
    }


    private static void initSurfaceVideoView(final SurfaceVideoView svv, String VideoSavePath, final Context context) {

        svv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                svv.setVolume(SurfaceVideoView.getSystemVolumn(context));
                svv.start();
            }
        });

        svv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtils.showMsg("该视频播放失败~");
                return false;
            }
        });

        svv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        // 音频和视频数据不正确
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (!((Activity) context).isFinishing())
                            svv.pause();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (!((Activity) context).isFinishing())
                            svv.start();
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        if (DeviceUtils.hasJellyBean()) {
                            svv.setBackground(null);
                        } else {
                            svv.setBackgroundDrawable(null);
                        }
                        break;
                }
                return false;
            }
        });
        svv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!((Activity) context).isFinishing())
                    svv.reOpen();
            }
        });

        svv.setVideoPath(VideoSavePath);
    }


    public static String getPicFilePath(Context context) {
        return FileUtil.getDiskFileDir(context, CacheConfig.PIC_BLU) + "/" + new Date() + EXTENSION;
    }

    public static String getVideoFilePath(Context context) {
        return FileUtil.getDiskFileDir(context, CacheConfig.VIDEO_BLU) + "/" + new Date() + EXTENSION1;
    }
}
