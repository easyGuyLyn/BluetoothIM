package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.BluetoothChat.config.ChatMessageUtils;
import com.example.administrator.myapplication.BluetoothChat.config.TimeShowUtil;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;
import com.example.administrator.myapplication.R;


/**
 * Created by Administrator on 2016/11/2.
 */

public class AddNotifitionUtil {

    public static void addNotifition(final Activity activity, final BluChatMsgBean msg, final VoiceRecorder voiceRecorder, Boolean isScreenOn) {
        if (msg == null) {
            return;
        }
        Handler handler = null;
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int contentLayoutId = 0;
        switch (msg.getContentType()) {
            case "1":
                contentLayoutId = R.layout.item_blu_chat_text_receive;
                break;
            case "2":
                contentLayoutId = R.layout.item_blu_chat_text_receive;
                break;
            case "3":
                contentLayoutId = R.layout.item_blu_chat_voice_receive;
                break;
        }
        final Handler finalHandler = handler;
        NotifitionManager.getInstance().notify(activity,
                msg.getSender(),
                "一条新消息！！",
                R.drawable.ic_app_bg,
                contentLayoutId,      //如果不想有弹窗，就传 0  ，下面的回调方法也不用书写
                isScreenOn,
                new NotifitionManager.NotifitionSetView() {
                    @Override
                    public void setView(View v) {
                        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
                        tv_time.setText(TimeShowUtil.getTimeShow(Long.parseLong(msg.getTime())));
                        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                        tv_name.setText(msg.getSender());
                        switch (msg.getContentType()) {
                            case "1":
                                TextView tv_text = (TextView) v.findViewById(R.id.tv_text);
                                tv_text.setText(ChatMessageUtils.toSpannableString(activity, msg.getContent()));
                                break;
                            case "2":

                                break;
                            case "3":
                                LinearLayout ll_voice_info = (LinearLayout) v.findViewById(R.id.ll_voice_info);//语音信息
                                TextView tv_voicePlay_duration = (TextView) v.findViewById(R.id.tv_voicePlay_duration);//语音时长
                                ProgressBar pb_outgoing = (ProgressBar) v.findViewById(R.id.pb_outgoing);//加载进度条
                                RelativeLayout rl_voice_play = (RelativeLayout) v.findViewById(R.id.rl_voice_play);//点击播放区域
                                ImageView iv_audio = (ImageView) v.findViewById(R.id.iv_audio);//播放桢动图
                                tv_voicePlay_duration.setText(msg.getVoiceLength());
                                String voiceFilePath1 = GetVoiceFilePathUtil.initVoice(displayMetrics, finalHandler, activity, msg, ll_voice_info, pb_outgoing, rl_voice_play);
                                VoiceClickPlayUtil.doPlay(false, voiceRecorder, rl_voice_play, iv_audio, voiceFilePath1);
                                break;
                        }


                    }
                });

    }
}
