package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.app.ActivityManager;
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
     * 生成一个自定义的弹窗通知
     *
     * @param activity
     */
    public void notifyCustom(final Activity activity, String title, String content, int icon, int custom, NotifitionSetView setViewCallBack) {

        //如果聊天界面在前台，则可以不通知
        if (isForeground(activity, activity.getLocalClassName())) {
            return;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 11, new Intent(activity, activity.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);
        final HeadsUpManager manage1 = HeadsUpManager.getInstant(activity.getApplication());
        View view = activity.getLayoutInflater().inflate(custom, null);
        setViewCallBack.setView(view);
        HeadsUp headsUp1 = new HeadsUp.Builder(activity)
                .setContentTitle(title).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                //要显示通知栏通知,这个一定要设置
                .setSmallIcon(icon)
                //2.3 一定要设置这个参数,负责会报错
                .setContentIntent(pendingIntent)
                .setContentText(content)
                .buildHeadUp();
        headsUp1.setCustomView(view);
        manage1.notify(code++, headsUp1);
        //获取电源管理器对象
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wl.acquire();
        //点亮屏幕
        wl.release();
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
