package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Image tools
 *
 * @author wei.chen
 */
public class ImageUtil {
    /**
     * 缩放图片
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int bmpWidth = 0, bmpHeight = 0;
        if (bitmap == null || (bmpWidth = bitmap.getWidth()) <= 0 || (bmpHeight = bitmap.getHeight()) <= 0) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale((float) newWidth / bmpWidth, (float) newHeight / bmpHeight);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap zoomBitmapToSquare(Bitmap bitmap) throws OutOfMemoryError {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if (w > h) {
            return zoomBitmap(bitmap, h, h);
        } else {
            return zoomBitmap(bitmap, w, w);
        }
    }

    public static Bitmap zoomToClipView(Bitmap bitmap, int clipLength) throws OutOfMemoryError {
        int bmpWidth = 0, bmpHeight = 0;
        float scale = 0;
        float length = (float) clipLength;
        if (bitmap == null || (bmpWidth = bitmap.getWidth()) <= 0 || (bmpHeight = bitmap.getHeight()) <= 0) {
            return null;
        }
        if (bmpWidth > clipLength && bmpHeight > clipLength) {
            return bitmap;
        } else {
            scale = (length / bmpWidth > length / bmpHeight) ? length / bmpWidth : length / bmpHeight;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 圆形图
     *
     * @param
     * @return
     */
    public static Bitmap toOvalBitmap(Bitmap oldBitmap) throws OutOfMemoryError {
        if (oldBitmap == null) {
            return null;
        }
        Bitmap bitmap = ImageUtil.zoomBitmapToSquare(oldBitmap);
        final int ratio = 2;

        Bitmap output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }

    private static final int MAX_IMAGE_HEIGHT = 768;
    private static final int MAX_IMAGE_WIDTH = 1024;
    private static final int MAX_IMAGE_SIZE = 200 * 1024; // max 500k

    public static Bitmap zipPicture(Context context, Uri picUri) {
        if (picUri == null) {
            return null;
        }

        final UriImage uriImage = new UriImage(context, picUri);

        int width = uriImage.getWidth();
        int height = uriImage.getHeight();

        Bitmap pic = null;
        // Log.d("XXX", "^^^^^^^^^^^^^^^^^^Original picture size: " + size/1024
        // + " K, "+ "width: " + width +" height: " + height);
        if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT || uriImage.getSizeOfImage() > MAX_IMAGE_SIZE) {
            final byte[] result = uriImage.getResizedImageData(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, MAX_IMAGE_SIZE);
            if (result == null) {
                Log.e("XXX", "Fail to zip picture, the original size is " + width + " * " + height);
                return null;
            }
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPurgeable = true;
            // option.inSampleSize = 4; //too aggressive: the bitmap will be
            // uploaded to server, not the the thumbnail
            pic = BitmapFactory.decodeByteArray(result, 0, result.length, option);
            // Log.d("XXX", "^^^^^^^^^^^^^^^^^^Zipped picture width: " +
            // pic.getWidth() +" height: "+ pic.getHeight());
        } else {
            try {
                pic = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(picUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pic;
    }

    public static Bitmap getThumbPicture(Context context, Uri picUri) {
        if (picUri == null) {
            return null;
        }

        final UriImage uriImage = new UriImage(context, picUri);

        int width = uriImage.getWidth();
        int height = uriImage.getHeight();

        Bitmap pic = null;
        // Log.d("XXX", "^^^^^^^^^^^^^^^^^^Original picture size: " + size/1024
        // + " K, "+ "width: " + width +" height: " + height);
        final byte[] result = uriImage.getResizedImageData(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, MAX_IMAGE_SIZE);
        if (result == null) {
            Log.e("XXX", "Fail to zip picture, the original size is " + width + " * " + height);
            return null;
        }
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPurgeable = true;
        option.inSampleSize = 2;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            baos.reset();
            option.inSampleSize++; // too aggressive: the bitmap will be
            // uploaded to server, not the the thumbnail
            pic = BitmapFactory.decodeByteArray(result, 0, result.length, option);
            pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        } while (baos.toByteArray().length / 1024 > 10);

        // Log.d("XXX", "^^^^^^^^^^^^^^^^^^Zipped picture width: " +
        // pic.getWidth() +" height: "+ pic.getHeight());
        return pic;
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBimap(byte[] b) {
        if (b == null || b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static void saveBitmapToFile(String path, String bitName, Bitmap mBitmap) {
        File f = new File(path + bitName);
        try {
            if (!f.exists())
                f.createNewFile();
            else
                return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (null != mBitmap) {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        }

        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotatePicture(Bitmap sourcePic, int degree) {
        if (sourcePic == null) {
            return null;
        }

        Bitmap rotatedBitmap = sourcePic;

        if (degree != 0) {
            boolean mutable = sourcePic.isMutable();
            // Log.e("XXX", "Picture is mutable? " + mutable);
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            try {
                rotatedBitmap = Bitmap.createBitmap(sourcePic, 0, 0, sourcePic.getWidth(), sourcePic.getHeight(), matrix, false);
            } catch (OutOfMemoryError e) {
                // TODO: handle exception
                System.gc();
                System.runFinalization();
                rotatedBitmap = Bitmap.createBitmap(sourcePic, 0, 0, sourcePic.getWidth(), sourcePic.getHeight(), matrix, false);
            }
            sourcePic.recycle();
            sourcePic = null; // release ASAP
        }
        return rotatedBitmap;
    }

    public static BitmapFactory.Options getBitmapOptions(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        return localOptions;
    }

    public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap) {
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(paramInt);
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
        return localBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2) {
        int i = paramOptions.outHeight;
        int j = paramOptions.outWidth;
        int k = 1;
        if ((i > paramInt2) || (j > paramInt1)) {
            int m = Math.round(i / paramInt2);
            int n = Math.round(j / paramInt1);
            k = m > n ? m : n;
        }
        return k;
    }

    public static Bitmap decodeScaleImage(String paramString, int paramInt1, int paramInt2) {
        BitmapFactory.Options localOptions = getBitmapOptions(paramString);
        int i = calculateInSampleSize(localOptions, paramInt1, paramInt2);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
        int degree = readPictureDegree(paramString);
        localBitmap1 = rotatePicture(localBitmap1, degree);

        return localBitmap1;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
     * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
     * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void saveMyBitmap(String fileName, Bitmap mBitmap) {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
     * Bitmap对象转换Drawable对象.
     *
     * @param bitmap 要转化的Bitmap对象
     * @return Drawable 转化完成的Drawable对象//
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if (bitmap == null) {
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

}
