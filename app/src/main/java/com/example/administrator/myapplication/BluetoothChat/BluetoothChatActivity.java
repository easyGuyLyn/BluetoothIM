package com.example.administrator.myapplication.BluetoothChat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.BluetoothChat.adapter.ChatAdapter;
import com.example.administrator.myapplication.BluetoothChat.blu.BluetoothChatService;
import com.example.administrator.myapplication.BluetoothChat.config.MyChatEditText;
import com.example.administrator.myapplication.BluetoothChat.config.TextChatMessage;
import com.example.administrator.myapplication.BluetoothChat.config.WaitDialog;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;
import com.example.administrator.myapplication.BluetoothChat.tools.GetBytesWithHeadInfoUtil;
import com.example.administrator.myapplication.BluetoothChat.tools.InitEmoViewTools;
import com.example.administrator.myapplication.BluetoothChat.tools.VocieTouchListener;
import com.example.administrator.myapplication.BluetoothChat.tools.VoiceRecorder;
import com.example.administrator.myapplication.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.Base64Utils;
import utils.BaseActivity;
import utils.CommonUtils;
import utils.GsonUtil;
import utils.TLogUtils;
import utils.ThreadUtils;
import utils.ToastUtils;

public class BluetoothChatActivity extends BaseActivity {
    // Debugging
    private static final String TAG = "BluetoothChat";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST_CONNECT_FAIL = 5;
    public static final int MESSAGE_TOAST_CONNECT_LOST = 6;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_adress";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    private String mLocalDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.rv_speech)
    RecyclerView rv_speech; //聊天列表
    @Bind(R.id.rl_chat_control)
    LinearLayout rl_chat_control;
    @Bind(R.id.btn_send)
    TextView btn_send; //发送按钮
    @Bind(R.id.btn_more)
    ImageView btn_more; //更多按钮
    //文字
    @Bind(R.id.edittext_layout)
    RelativeLayout edittext_layout;
    @Bind(R.id.et_sendmessage)
    MyChatEditText et_sendmessage;
    @Bind(R.id.btn_set_mode_keyboard)
    ImageView btn_set_mode_keyboard; //切换文字输入按钮
    //语音
    @Bind(R.id.btn_press_to_speak)
    RelativeLayout btn_press_to_speak;
    @Bind(R.id.btn_set_mode_voice)
    ImageView btn_set_mode_voice; //切换语音按钮
    @Bind(R.id.recording_container)
    RelativeLayout recordingContainer;// 一个容器，装载显示录音时声音变化的ivRecord和录音时提示信息的tvVoiceTips
    @Bind(R.id.recording_hint)
    TextView recordingHint;//显示提示信息的组件
    @Bind(R.id.mic_image)
    ImageView micImage;//layoutRecord中,根据录音时语音的大小加载不同图片的ImageView控件
    private Drawable[] micImages; // 话筒动画,里面装载5张图片，根据音量大小，让ivRecord加载不同的图片
    private VoiceRecorder voiceRecorder; //语音逻辑类
    public final static int VOICE_REFRESH = 11;
    public final static int VOICE_LONG = 13;
    public final static int VOICE_UP = 14;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 29;
    //底部
    @Bind(R.id.ll_footer_chat_activity_container)
    LinearLayout ll_footer_chat_activity_container;//底部展开的所有父容器
    @Bind(R.id.rl_footer_chat_activity_container_emo)
    RelativeLayout rl_footer_chat_activity_container_emo;//表情面板的容器
    //表情
    @Bind(R.id.iv_emoticons)
    ImageView iv_emoticons; //呼唤表情面板按钮
    @Bind(R.id.cpi_footer_chat_activity_emo_indicator)
    CirclePageIndicator cip;  //ViewPager中的界面圆形界面指示器（第三方类库）
    @Bind(R.id.vp_footer_chat_activity_pager_emo)
    ViewPager pager_emo;  //layoutEmo中管理加载表情的界面的ViewPager
    private List<TextChatMessage> emos;//装载表情图片的列表
    //全局
    private WaitDialog waitDialog;
    private ChatAdapter speechAdapter;
    private List<BluChatMsgBean> mData = new ArrayList<>();
    private Boolean isNeedSrollByItself = true;
    private Boolean isDestroyed = false;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bluetooth_chat);
    }

    @Override
    public void initData() {
        init();
    }

    @Override
    public void setListener() {
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            //提示用户打开；
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //直接打开；
            //	mBluetoothAdapter.enable();
            //	 setupTask();
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void init() {
        toolbar.setTitle(getString(R.string.BluTittle));
        toolbar.setSubtitle(getString(R.string.notConnect));
        setSupportActionBar(toolbar);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtils.showMsg(getString(R.string.Bluetoothisnotavailable));
            finish();
            return;
        }
        InitEmoViewTools.initEmoView(this, emos, pager_emo, cip, et_sendmessage);//初始化表情相关业务
        micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01), getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03), getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05), getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07), getResources().getDrawable(R.drawable.record_animate_08)};
        voiceRecorder = new VoiceRecorder(BluetoothChatActivity.this, mHandler); /*初始化语音相关*/
    }

    public void setupTask() {
        waitDialog = new WaitDialog(this);
        mLocalDeviceName = mBluetoothAdapter.getName();
        mChatService = new BluetoothChatService(this, mHandler);
        rv_speech.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        speechAdapter = new ChatAdapter(this, mData, mLocalDeviceName, voiceRecorder);
        rv_speech.setAdapter(speechAdapter);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE://蓝牙连接状态改变后的回调
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        /**蓝牙相关*/
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.connecttedTo) + " " + mConnectedDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(getString(R.string.connectting));
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(getString(R.string.notConnect));
                            break;
                    }
                    break;
                case MESSAGE_WRITE: //成功发送消息后的回调
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMsg = new String(writeBuf);
                    BluChatMsgBean beanWrite = GsonUtil.GsonToBean(writeMsg, BluChatMsgBean.class);
                    if (beanWrite == null) {
                        return;
                    }
                    addMsg(beanWrite);
                    et_sendmessage.setText("");
                    waitDialog.dismiss();
                    break;
                case MESSAGE_READ: //成功读取消息后的回调
                    byte[] readBuf = (byte[]) msg.obj;
                    String deviceName = msg.getData().getString(DEVICE_NAME);
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    TLogUtils.d("json_readMessage", readMessage);
                    BluChatMsgBean beanRead = GsonUtil.GsonToBean(readMessage, BluChatMsgBean.class);
                    if (beanRead == null) {
                        return;
                    }
                    addMsg(beanRead);
                    break;
                case MESSAGE_DEVICE_NAME://获得连接设备名后的回调
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    ToastUtils.showMsg(getString(R.string.connecttedTo) + " " + mConnectedDeviceName);
                    break;
                case MESSAGE_TOAST_CONNECT_LOST://掉线的回调
                    ToastUtils.showMsg(msg.getData().getString(TOAST));
                    String adress = msg.getData().getString(DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(adress);
                    if (!isDestroyed) {
                        mChatService.start();// Start the service over to restart listening mode
                    }
                    break;
                case MESSAGE_TOAST_CONNECT_FAIL://连接失败的回调
                    if (!isDestroyed) {
                        mChatService.start(); // Start the service over to restart listening mode
                    }
                    ToastUtils.showMsg(msg.getData().getString(TOAST));
                    break;
                /**语音相关*/
                case VOICE_UP: //主动发送
                    int length_ = msg.arg1;
                    if (length_ > 0) {
                        TLogUtils.d("lyn", voiceRecorder.getVoiceFilePath(length_) + ",时长:" + length_);
                        sendVoiceMsg(voiceRecorder.getVoiceFilePath(length_), length_);
                    } else {
                        Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case VOICE_LONG: //太长了，就会到时间了自动发送
                    recordingHint.setText(R.string.msg_msg_voice_press_speak);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    int length = msg.arg1;
                    if (length > 0) {
                        TLogUtils.d("lyn", voiceRecorder.getVoiceFilePath(length) + ",时长:" + length);
                        sendVoiceMsg(voiceRecorder.getVoiceFilePath(length), length);
                    } else {
                        Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case VOICE_REFRESH://话筒动画
                    if (msg.arg1 <= 1)
                        micImage.setImageDrawable(micImages[0]);
                    else
                        micImage.setImageDrawable(micImages[msg.arg1 >= 8 ? 7 : msg.arg1 - 1]);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blutooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent serverIntent = null;
        if (id == R.id.secure_connect_scan) {
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        } else if (id == R.id.insecure_connect_scan) {
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        } else if (id == R.id.discoverable) {
            ensureDiscoverable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ensureDiscoverable() {//重新搜索设备
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void setStatus(String status) {//设置此时蓝牙的连接状态在副标题上
        if (toolbar != null) {
            toolbar.setSubtitle(status);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupTask();
                } else {
                    ToastUtils.showMsg(getString(R.string.Bluetoothisnotavailable));
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {//连接设备
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    public void initListener() {
        rv_speech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtils.hiddenInput(BluetoothChatActivity.this);
                ll_footer_chat_activity_container.setVisibility(View.GONE);
                return false;
            }
        });
        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    btn_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                } else {
                    btn_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rv_speech.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView)) {
                    isNeedSrollByItself = true;
                } else {
                    isNeedSrollByItself = false;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        btn_press_to_speak.setOnTouchListener(new VocieTouchListener(voiceRecorder, recordingHint, recordingContainer, mHandler, this));
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {//recyclerView是否处于底部
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    @OnClick(R.id.btn_set_mode_keyboard)
    public void setToKeyBoard() {
        btn_set_mode_keyboard.setVisibility(View.GONE);
        btn_set_mode_voice.setVisibility(View.VISIBLE);
        edittext_layout.setVisibility(View.VISIBLE);
        btn_press_to_speak.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_set_mode_voice)
    public void setToVoice() {
        btn_set_mode_keyboard.setVisibility(View.VISIBLE);
        btn_set_mode_voice.setVisibility(View.GONE);
        edittext_layout.setVisibility(View.GONE);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        ll_footer_chat_activity_container.setVisibility(View.GONE);
        CommonUtils.hiddenInput(BluetoothChatActivity.this);
    }

    @OnClick(R.id.btn_more)
    public void setToMore() {
        ToastUtils.showMsg("暂不支持更多功能~");
    }

    @OnClick(R.id.iv_emoticons)
    public void setToEmj() {
        et_sendmessage.requestFocus();
        if (ll_footer_chat_activity_container.getVisibility() == View.VISIBLE) {
            if (rl_footer_chat_activity_container_emo.getVisibility() == View.VISIBLE) {
                ll_footer_chat_activity_container.setVisibility(View.GONE);
                rl_footer_chat_activity_container_emo.setVisibility(View.GONE);
            } else {
                rl_footer_chat_activity_container_emo.setVisibility(View.VISIBLE);
            }
        } else {
            if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                CommonUtils.hiddenInput(BluetoothChatActivity.this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_footer_chat_activity_container.setVisibility(View.VISIBLE);
                        rl_footer_chat_activity_container_emo.setVisibility(View.VISIBLE);
                    }
                }, 200);
            } else {
                ll_footer_chat_activity_container.setVisibility(View.VISIBLE);
                rl_footer_chat_activity_container_emo.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.et_sendmessage)
    public void clickEtmsg() {
        CommonUtils.showInput(BluetoothChatActivity.this, et_sendmessage);
        ll_footer_chat_activity_container.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_send)
    public void setToSend() {
        sendTextMessage(et_sendmessage.getText().toString());
    }

    private void sendTextMessage(String message) {//发送一条文字消息
        waitDialog.sendMsg();
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            waitDialog.dismiss();
            ToastUtils.showMsg(getString(R.string.notConnect));
            return;
        }
        if (message.length() > 0) {
            BluChatMsgBean bcmr = new BluChatMsgBean("1", mConnectedDeviceName, message, System.currentTimeMillis() + "", mLocalDeviceName);
            String json = GsonUtil.GsonString(bcmr);
            mChatService.write(json, GetBytesWithHeadInfoUtil.getByteArry(json));
        }
    }

    public void sendVoiceMsg(final String voiceFilePath, final int length) {//发送一条语音
        waitDialog.sendMsg();
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            waitDialog.dismiss();
            ToastUtils.showMsg(getString(R.string.notConnect));
            return;
        }
        ThreadUtils.newThread(new Runnable() {
            @Override
            public void run() {
                String encode = null;
                try {
                    encode = Base64Utils.encodeBase64File(voiceFilePath);   //语音暂时用Base64  问题不大
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (encode.length() > 0) {
                    BluChatMsgBean sendBean = new BluChatMsgBean("3", mConnectedDeviceName, encode, System.currentTimeMillis() + "", mLocalDeviceName);
                    sendBean.setFilePath(voiceFilePath);
                    sendBean.setVoiceLength(length + "");
                    String json = GsonUtil.GsonString(sendBean);
                    mChatService.write(json, GetBytesWithHeadInfoUtil.getByteArry(json));
                }
            }
        });
    }

    public void addMsg(BluChatMsgBean msg) {//增加了一条消息
        mData.add(msg);
        speechAdapter.notifyItemInserted(mData.size() - 1);
        if (isNeedSrollByItself) rv_speech.scrollToPosition(mData.size() - 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    voiceRecorder.startRecording();
                } else {
                    ToastUtils.showMsg("你必须允许录音权限才能发送语音");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
            //停止播放
            if (VoiceRecorder.isPlaying && VoiceRecorder.currentPlayListener != null) {
                VoiceRecorder.currentPlayListener.stopPlayVoice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        if (mChatService != null) mChatService.stop();
    }

    /**
     * 主页面 ,重写 onKeyDown方法
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //两秒之内按返回键就会退出
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //这里使用home键，永远只是调到后台而已 ，不finish
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
