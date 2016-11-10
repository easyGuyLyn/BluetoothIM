package utils;


import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.myapplication.R;

import java.io.File;

import utils.view.GlideCircleTransform;
import utils.view.GlideRoundTransform;

/**
 * Created by lyn on 2016/4/15.
 * Glide框架加载图片
 */
public class GlideUtils {

    /**
     * 加载普通图片
     *
     * @param context   上下文
     * @param imageView ImageView
     * @param path      本地路径或网络路径
     */
    public static void display(Context context, ImageView imageView, String path) {
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_defalut_placehoder)
                .error(R.drawable.ic_defalut_placehoder)
                .centerCrop()
                .crossFade(200)
                .into(imageView);
    }


    /**
     * 加载成圆形图片,多用于头像
     *
     * @param context   上下文
     * @param imageView ImageView
     * @param path      本地路径或网络路径
     */
    public static void displayToCircle(Context context, ImageView imageView, String path) {
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_defalut_placehoder)
                .error(R.drawable.ic_defalut_placehoder)
                .centerCrop()
                .crossFade(200)
                .transform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * 加载成圆角图片
     *
     * @param context   上下文
     * @param imageView ImageView
     * @param path      本地路径或网络路径
     * @param dp        圆角大小
     */
    public static void displayToRound(Context context, ImageView imageView, String path, int dp) {
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_defalut_placehoder)
                .error(R.drawable.ic_defalut_placehoder)
                .centerCrop()
                .crossFade(200)
                .transform(new GlideRoundTransform(context, dp)).into(imageView);
    }
}
