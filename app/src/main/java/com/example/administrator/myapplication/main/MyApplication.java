package com.example.administrator.myapplication.main;

import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.example.administrator.myapplication.mvcHelper.mvc.MVCHelper;

import java.io.File;

import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.util.DeviceUtils;
import utils.CrashHandler;
import utils.TLogUtils;
import utils.view.MyLoadViewFactory;

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //设置Log开关
        TLogUtils.D = true;
        //初始化XUtils3
        x.Ext.init(this);
        //设置debug模式
        x.Ext.setDebug(true);
        // 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局,写法参照DeFaultLoadViewFactory
        MVCHelper.setLoadViewFractory(new MyLoadViewFactory());
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //初始化视频录制
        initSmallVideo(this);
    }


    /**
     * 初始化 小视频录制的配置
     *
     * @param context
     */
    public static void initSmallVideo(Context context) {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(context);
    }
}
