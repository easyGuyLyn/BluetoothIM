package com.example.administrator.myapplication.BluetoothChat.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.BluetoothChat.config.ChatMessageUtils;
import com.example.administrator.myapplication.BluetoothChat.config.CommonViewHolder;
import com.example.administrator.myapplication.BluetoothChat.config.TimeShowUtil;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;
import com.example.administrator.myapplication.BluetoothChat.tools.GetVoiceFilePathUtil;
import com.example.administrator.myapplication.BluetoothChat.tools.VoiceClickPlayUtil;
import com.example.administrator.myapplication.BluetoothChat.tools.VoiceRecorder;
import com.example.administrator.myapplication.R;

import java.util.List;

/**
 * Created by Administrator on 2016/10/4.
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TEXT_SEND = 0;
    private static final int TYPE_TEXT_RECEIVE = 1;
    private static final int TYPE_VOCICE_SEND = 2;
    private static final int TYPE_VOICE_RECEIVE = 3;
    private static final int TYPE_PIC_SEND = 4;
    private static final int TYPE_PIC_RECEIVE = 5;
    private Context mContext;
    private List<BluChatMsgBean> mData;
    private String mLocalDeviceName = null;
    private VoiceRecorder mVoiceRecorder; //语音逻辑类
    private android.os.Handler handler;
    private DisplayMetrics displayMetrics;

    public ChatAdapter(Context context, List<BluChatMsgBean> data, String LocalDeviceName, VoiceRecorder voiceRecorder) {
        this.mContext = context;
        mData = data;
        mLocalDeviceName = LocalDeviceName;
        mVoiceRecorder = voiceRecorder;
        displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    public int getItemViewType(int position) {
        BluChatMsgBean msg = mData.get(position);
        if (msg.getContentType().equals("1")) {//文字
            if (msg.getSender().equals(mLocalDeviceName))
                return TYPE_TEXT_SEND;
            else
                return TYPE_TEXT_RECEIVE;
        } else if (msg.getContentType().equals("2")) {//图片
            // TODO: 2016/10/31

        } else if (msg.getContentType().equals("3")) {//语音
            if (msg.getSender().equals(mLocalDeviceName))
                return TYPE_VOCICE_SEND;
            else
                return TYPE_VOICE_RECEIVE;
        } else {
            // TODO: 2016/10/31   其他文件

        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT_SEND:
                return new CommonViewHolder(View.inflate(mContext, R.layout.item_blu_chat_text_send, null));
            case TYPE_TEXT_RECEIVE:
                return new CommonViewHolder(View.inflate(mContext, R.layout.item_blu_chat_text_receive, null));
            case TYPE_VOCICE_SEND:
                return new CommonViewHolder(View.inflate(mContext, R.layout.item_blu_chat_voice_send, null));
            case TYPE_VOICE_RECEIVE:
                return new CommonViewHolder(View.inflate(mContext, R.layout.item_blu_chat_voice_receive, null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CommonViewHolder viewHolder = (CommonViewHolder) holder;
        final BluChatMsgBean message = mData.get(position);
        String connectDeviceName = message.getSender();
        viewHolder.getTv(R.id.tv_time).setText(TimeShowUtil.getTimeShow(Long.parseLong(message.getTime())));
        /**
         * 语音的视图
         */
        LinearLayout ll_voice_info = viewHolder.getLl(R.id.ll_voice_info);//语音信息
        TextView tv_voicePlay_duration = viewHolder.getTv(R.id.tv_voicePlay_duration);//语音时长
        ProgressBar pb_outgoing = viewHolder.getPb(R.id.pb_outgoing);//加载进度条
        RelativeLayout rl_voice_play = viewHolder.getRl(R.id.rl_voice_play);//点击播放区域
        ImageView iv_audio = viewHolder.getIv(R.id.iv_audio);//播放桢动图

        switch (getItemViewType(position)) {
            case TYPE_TEXT_SEND:
                viewHolder.getTv(R.id.tv_name).setText("我");
                SpannableString contentWithEmoSend = ChatMessageUtils.toSpannableString(mContext, message.getContent());
                viewHolder.getTv(R.id.tv_text).setText(contentWithEmoSend);
                break;
            case TYPE_TEXT_RECEIVE:
                viewHolder.getTv(R.id.tv_name).setText(connectDeviceName);
                SpannableString contentWithEmoReceive = ChatMessageUtils.toSpannableString(mContext, message.getContent());
                viewHolder.getTv(R.id.tv_text).setText(contentWithEmoReceive);
                break;
            case TYPE_VOCICE_SEND:
                viewHolder.getTv(R.id.tv_name).setText("我");
                tv_voicePlay_duration.setText(message.getVoiceLength());
                String voiceFilePath = GetVoiceFilePathUtil.initVoice(displayMetrics, handler, mContext, message, ll_voice_info, pb_outgoing, rl_voice_play);
                VoiceClickPlayUtil.doPlay(true, mVoiceRecorder, rl_voice_play, iv_audio, voiceFilePath);
                break;
            case TYPE_VOICE_RECEIVE:
                viewHolder.getTv(R.id.tv_name).setText(connectDeviceName);
                tv_voicePlay_duration.setText(message.getVoiceLength());
                String voiceFilePath1 = GetVoiceFilePathUtil.initVoice(displayMetrics, handler, mContext, message, ll_voice_info, pb_outgoing, rl_voice_play);
                VoiceClickPlayUtil.doPlay(false, mVoiceRecorder, rl_voice_play, iv_audio, voiceFilePath1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
