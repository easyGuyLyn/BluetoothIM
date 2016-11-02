package com.example.administrator.myapplication.BluetoothChat.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.example.administrator.myapplication.BluetoothChat.BluetoothChatActivity;

/**
 * Created by Administrator on 2016/11/2.
 */

public class requestPermissoinUtils {

    public static void requestSystemAlert(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                        BluetoothChatActivity.MY_PERMISSIONS_REQUEST_SYSTEM_ALERT_WINDOW);
            }
        }
    }
}
