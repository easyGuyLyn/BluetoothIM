package com.example.administrator.myapplication.textureView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.example.administrator.myapplication.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, TextureView.SurfaceTextureListener {

    private TextureView mSurface;
    private DrawingThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurface.setOnTouchListener(this);
        mSurface.setSurfaceTextureListener(this);
    }

    @Override
    public void onClick(View v) {
        // ??????????????  
        mSurface.animate().rotation(mSurface.getRotation() < 180.f ? 180.f : 0.f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mThread.addItem((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mThread = new DrawingThread(new Surface(surface),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        mThread.updateSize(width, height);
        mThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mThread.updateSize(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mThread.quit();
        mThread = null;

        // ???? true ??????????? Surface  
        return true;
    }

    /**
     * ?? SurfaceTexture ????????????????÷??????????в???
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class DrawingThread extends HandlerThread implements Handler.Callback {

        private static final int MSG_ADD = 100;
        private static final int MSG_MOVE = 101;
        private static final int MSG_CLEAR = 102;

        private int mDrawingWidth, mDrawingHeight;
        private boolean mRunning = false;

        private Surface mDrawingSurface;
        private Rect mSurfaceRect;
        private Paint mPaint;

        private Handler mReceiver;
        private Bitmap mIcon;
        private ArrayList<DrawingItem> mLocations;

        private class DrawingItem {
            // ???λ????  
            int x, y;
            // ??????????  
            boolean horizontal, vertical;

            public DrawingItem(int x, int y, boolean horizontal, boolean vertical) {
                this.x = x;
                this.y = y;
                this.horizontal = horizontal;
                this.vertical = vertical;
            }
        }


        public DrawingThread(Surface surface, Bitmap icon) {
            super("DrawingThread");
            mDrawingSurface = surface;
            mSurfaceRect = new Rect();
            mLocations = new ArrayList<>();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mIcon = icon;
        }

        @Override
        protected void onLooperPrepared() {
            mReceiver = new Handler(getLooper(), this);
            // ??????  
            mRunning = true;
            mReceiver.sendEmptyMessage(MSG_MOVE);
        }

        @Override
        public boolean quit() {
            // ??????????е????  
            mRunning = false;
            mReceiver.removeCallbacksAndMessages(null);
            return super.quit();
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD:
                    // ???????λ????????????????????????????????????  
                    DrawingItem newItem = new DrawingItem(msg.arg1, msg.arg2,
                            Math.round(Math.random()) == 0,
                            Math.round(Math.random()) == 0);
                    mLocations.add(newItem);
                    break;
                case MSG_CLEAR:

                    // ??????е????  
                    mLocations.clear();
                    break;
                case MSG_MOVE:
                    // ?????????????κ?????  
                    if (!mRunning) return true;

                    // ?????  
                    try {
                        // ???? SurfaceView??????????????? Canvas  
                        Canvas canvas = mDrawingSurface.lockCanvas(mSurfaceRect);
                        // ??????? Canvas  
                        canvas.drawColor(Color.BLACK);
                        // ??????????  
                        for (DrawingItem item : mLocations) {
                            // ????λ??  
                            item.x += (item.horizontal ? 5 : -5);
                            if (item.x >= (mDrawingWidth - mIcon.getWidth())) {
                                item.horizontal = false;
                            }
                            if (item.x <= 0) {
                                item.horizontal = true;
                            }
                            item.y += (item.vertical ? 5 : -5);
                            if (item.y >= (mDrawingHeight - mIcon.getHeight())) {
                                item.vertical = false;
                            }
                            if (item.y <= 0) {
                                item.vertical = true;
                            }
                            canvas.drawBitmap(mIcon, item.x, item.y, mPaint);
                        }
                        // ???? Canvas???????????????  
                        mDrawingSurface.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            // ????????  
            if (mRunning) {
                mReceiver.sendEmptyMessage(MSG_MOVE);
            }
            return true;
        }

        public void updateSize(int width, int height) {
            mDrawingWidth = width;
            mDrawingHeight = height;
            mSurfaceRect.set(0, 0, mDrawingWidth, mDrawingHeight);
        }

        public void addItem(int x, int y) {
            // ??? Message ??????λ????????????  
            Message msg = Message.obtain(mReceiver, MSG_ADD, x, y);
            mReceiver.sendMessage(msg);
        }

        public void clearItems() {
            mReceiver.sendEmptyMessage(MSG_CLEAR);
        }
    }
}