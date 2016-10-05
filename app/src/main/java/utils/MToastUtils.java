package utils;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.myapplication.main.MyApplication;

/**
 * Created by lyn on 2016/2/23.
 */
public class MToastUtils {
    //toast
    private static Toast mToast;

    public static void showMsg(String msg, Context context) {

        if (mToast != null) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
