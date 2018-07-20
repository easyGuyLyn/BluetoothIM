package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;

import com.mingle.headsUp.HeadsUp;
import com.mingle.headsUp.HeadsUpManager;

import java.util.List;

import utils.TLogUtils;

/**
 * Created by Administrator on 2016/11/2.
 */

public class NotifitionManager {

    private int code = 1;

    private static NotifitionManager instance = null;

    public static NotifitionManager getInstance() {
        if (instance == null) {
            instance = new NotifitionManager();
        }
        return instance;
    }

    /**
     * 生成一个通知,可以是简单的通知，也可以是弹窗的自定义通知
     *
     * @param activity
     */
    public void notify(final Activity activity, String title, String content, int icon, int custom, Boolean isScreenOn, NotifitionSetView setViewCallBack) {

        //如果聊天界面在前台且亮屏，则可以不通知
        TLogUtils.i("notify_status", isForeground(activity, activity.getLocalClassName()) + "//" + isScreenOn);
        if (isForeground(activity, activity.getLocalClassName()) && isScreenOn) {
            return;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 11, new Intent(activity, activity.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);
        final HeadsUpManager manage1 = HeadsUpManager.getInstant(activity.getApplication());
        HeadsUp headsUp1 = new HeadsUp.Builder(activity)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                //要显示通知栏通知,这个一定要设置
                .setSmallIcon(icon)
                //2.3 一定要设置这个参数,负责会报错
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(content)
                .buildHeadUp();
        if (custom != 0) {
            View view = activity.getLayoutInflater().inflate(custom, null);
            setViewCallBack.setView(view);
            headsUp1.setCustomView(view);
        }
        manage1.notify(code++, headsUp1);
        //获取电源管理器对象
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl.acquire();
        //点亮屏幕
        wl.release();
        //解锁
        KeyguardManager km = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
        //得到键盘锁管理器对象
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //参数是LogCat里用的Tag
        kl.disableKeyguard();
    }


    public interface NotifitionSetView {
        void setView(View v);
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
