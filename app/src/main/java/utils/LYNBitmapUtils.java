package utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图像工具类，提供app中与图像操作相关的工具方法
 *
 * @author tarena.sunwei
 */
public class LYNBitmapUtils {
    /**
     * 利用Android的图像混合模式，为黑白（或者灰度）图片提供指定的颜色
     * 混合后，凡是原始图像中黑色（或者灰色）的部分会呈现出指定的颜色（相当于Photoshop中的蒙板效果）
     * 在使用XferMode效果的时候，有DST和SRC图像的概念
     * 其中DST就是Canvas上已经有的图像，而SRC就是将应用到当前操作的图片
     *
     * @param res   利用BitmapFactory解析资源ID为图像时需要的Resources对象
     * @param resId 原始黑白（或者灰度）图像的资源ID
     * @param color 希望黑白（或者灰度）图像中，黑色（或者灰色）部分呈现的颜色
     * @return 进行了混合的图像
     */
    public static Bitmap getBitmapWithBackground(Resources res, int resId, int color) {

        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //����һ����ԭʼͼ��һ���С��ͼ��
        Bitmap target = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        //���´����Ŀհ�Bitmapͼ��target��ΪDST
        Canvas canvas = new Canvas(target);
        //���ϱ���ɫ
        canvas.drawColor(color);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //��resIdͼ����ΪSRCͼ�񣬲���DST_IN���ģʽ����SRC��DST���ӣ����Ӻ�SRC�к�ɫ����ɫ������������DST�е�����
        //��ɫԽ��Խ��͸��Ч��Խ����
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return target;
    }


    /**
     * 将图像文件保存为指定的文件
     *
     * @param file     将要保存的文件位置
     * @param bitmap   要保存的图像文件
     * @param isDelete 如果存在同名文件，是否将原文件删除掉
     */
    public static void saveBitmap(File file, Bitmap bitmap, boolean isDelete) {
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(CompressFormat.JPEG, 100, out)) {
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将内存中的Bimtap图像文件保存为本地指定的文件路径中，保存时进行压缩（压缩比为0~100,100为不压缩）
     *
     * @param dirPath  保存目录
     * @param filename 保存的文件名字
     * @param bitmap   要被保存的bitmap图像文件
     * @param compress 保存时进行压缩（压缩比为0~100,100为不压缩）
     * @param isDelete 如果存在同名的文件是否将原文件删除
     */
    public static void saveBitmap(String dirPath, String filename, Bitmap bitmap, int compress, boolean isDelete) {


        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dirPath, filename);
        // �����ڼ�ɾ��-Ĭ��ֻ����һ��
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(CompressFormat.JPEG, compress, out)) {
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 将内存中的Bimtap图像文件保存为本地指定的文件路径中
     *
     * @param dirPath  保存目录
     * @param filename 保存的文件名字
     * @param bitmap   要被保存的bitmap图像文件
     * @param isDelete 如果存在同名的文件是否将原文件删除
     */
    public static void saveBitmap(String dirPath, String filename, Bitmap bitmap, boolean isDelete) {
        saveBitmap(dirPath, filename, bitmap, 100, isDelete);
    }

    /**
     * ��ȡpath·��ָ��ģ��������ͼ�����������ʱ��ԭʼͼ���Ƿ�������ת����ת�˶��ٶ�
     *
     * @param path ͼ�񱣴�·��
     * @return �����ͼ�����������ʱ��ͼ����ת�˶��ٶ�
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将指定的图像旋转指定的角度
     *
     * @param angle  指定的旋转角度
     * @param bitmap 指定的图像
     * @return 被旋转了指定角度后的图像
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        //ʹ��postRotate����˼������������¼���angle�ȽǶ�
        //���ʹ��setRotate����˼ʱ�����нǶ��滻Ϊ�����еĽǶ�
        matrix.postRotate(angle);
        Bitmap roatatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return roatatedBitmap;
    }

    /**
     * 将指定位置的图像进行压缩后再读入内存
     *
     * @param filePath 原始图像的保存位置
     * @param act      提供获取屏幕高度和宽度的上下文对象
     * @return 对指定位置的图像进行压缩后的图像
     */
    public static Bitmap getSampleSizeBitmap(String filePath, Activity act) {
        Bitmap bitmap = null;
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        int screenWidth = CommonUtils.getScreenWidth(act);
        int screenHeight = CommonUtils.getScreenHeight(act);
        int widthSampleSize = (int) Math.ceil(width * 1.0 / screenWidth);
        int heightSampleSize = (int) Math.ceil(height * 1.0 / screenHeight);
        int sampleSize = Math.max(widthSampleSize, heightSampleSize);
        /**
         * sampleSize大于1，说明图像的width大于屏幕的width并且（或者）图像的height大于屏幕的height
         * 因此有必要进行适当的压缩
         * 没有必要在一个小手机屏幕上显示如此巨大的图像
         */
        if (sampleSize > 1) {
            opts.inSampleSize = sampleSize;
            opts.inJustDecodeBounds = false;
        }
        bitmap = BitmapFactory.decodeFile(filePath, opts);
        return bitmap;
    }

    /**
     * 将指定位置图像根据指定的宽度和高度进行压缩后再读入内存
     *
     * @param filePath     原始图像的保存位置
     * @param targetWidth  显示图像区域的宽度
     * @param targetHeight 显示图像区域的高度
     * @return 压缩后的图像
     */
    public static Bitmap getSampleSizeBitmap(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        int widthSampleSize = (int) Math.ceil(width * 1.0 / targetWidth);
        int heightSampleSize = (int) Math.ceil(height * 1.0 / targetHeight);
        int sampleSize = Math.max(widthSampleSize, heightSampleSize);
        /**
         * sampleSize大于1，说明图像的width大于屏幕的width并且（或者）图像的height大于屏幕的height
         * 因此有必要进行适当的压缩
         * 没有必要在一个小手机屏幕上显示如此巨大的图像
         */
        if (sampleSize > 1) {
            opts.inSampleSize = sampleSize;
            opts.inJustDecodeBounds = false;
        }
        bitmap = BitmapFactory.decodeFile(filePath, opts);
        return bitmap;
    }


    /**
     * 根据res/drawable_xxxx中图像文件的名称，获得它的resId值
     * 例如文件名称为ic_launcher，可以返回R.drawable.ic_launcher对应的值
     *
     * @param context 获取Resources对象需要一个Context对象
     * @param name    drawable_xxxx文件夹中图像的名称
     * @return 返回图像的资源id值
     */
    public static int getResId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /**
     * 根据res/drawable_xxxx中的文件的名称，获得Bitmap格式的图像
     *
     * @param context 获取Resources对象需要一个Context对象
     * @param name    drawable_xxxx文件夹中图像的名称
     * @return 返回name所对应的图像本身
     */
    public static Bitmap getBitmapFromDrawableName(Context context, String name) {
        return BitmapFactory.decodeResource(context.getResources(), getResId(context, name));
    }

    /**
     * 重载方法，提供Options供BitmapFactory使用
     *
     * @param context
     * @param name
     * @param opts
     * @return
     */
    public static Bitmap getBitmapFromDrawableName(Context context, String name, Options opts) {
        return BitmapFactory.decodeResource(context.getResources(), getResId(context, name), opts);
    }

    /**
     * 将指定的图像转为Base64格式的字符串
     *
     * @param bitmap 将被转换的图像
     * @return Base64格式字符串形式的图像
     */
    public static String bitmap2Base64(Bitmap bitmap) {
        String base64 = "";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, bos);
            byte[] bytes = bos.toByteArray();
            base64 = Base64.encodeToString(bytes, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
    }

    /**
     * 将一个Base64格式的字符串转化为Bitmap图像
     *
     * @param base64 Base64编码的字符串形式的图像
     * @return
     */
    public static Bitmap base642Bitmap(String base64) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = Base64.decode(base64, 0);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
