package com.example.administrator.myapplication.BluetoothChat.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.administrator.myapplication.BluetoothChat.BluetoothChatActivity;
import com.example.administrator.myapplication.BluetoothChat.adapter.EmoPagerAdapter;
import com.example.administrator.myapplication.BluetoothChat.adapter.MoreGridAdapter;
import com.example.administrator.myapplication.BluetoothChat.model.MoreBean;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.main.DemoActivity;
import com.example.administrator.myapplication.map.BasicMapActivity;
import com.example.administrator.myapplication.weixinPhotoPicker.photopicker.SelectModel;
import com.example.administrator.myapplication.weixinPhotoPicker.photopicker.intent.PhotoPickerIntent;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.MediaRecorderConfig;


/**
 * Created by 46404 on 2016/11/9.
 */

public class InitMoreViewTools {

    public static void initMoreView(Context context, ViewPager pager_more, CirclePageIndicator cip, ArrayList<String> imagePaths) {

        /**
         * 数据源   在这里添加更多里的业务
         */
        List<MoreBean> mores = new ArrayList<>();
        MoreBean data1 = new MoreBean("demo", R.mipmap.ic_more_demo);
        MoreBean data2 = new MoreBean("图片", R.mipmap.ic_photo);
        MoreBean data3 = new MoreBean("小视频", R.mipmap.ic_vedio);
        MoreBean data4 = new MoreBean("位置", R.mipmap.ic_locations);
        mores.add(data1);
        mores.add(data2);
        mores.add(data3);
        mores.add(data4);
        /**
         *viewPager的页
         */
        List<View> views = new ArrayList<>();
        views.add(getGridView1(context, mores, imagePaths));
        pager_more.setAdapter(new EmoPagerAdapter(views));

        /**为PagerAdapter添加底部的圆形的导航按钮*/
        cip.setFillColor(context.getResources().getColor(R.color.colorAccent));
        cip.setViewPager(pager_more, 0);

    }

    public static View getGridView1(final Context context, List<MoreBean> mores, final ArrayList<String> imagePaths) {
        View view = View.inflate(context, R.layout.more_gridview_layout, null);
        GridView gridview = (GridView) view.findViewById(R.id.gv_more);
        MoreGridAdapter adapter = new MoreGridAdapter(context, mores);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:   //demo
                        Intent intent = new Intent(context, DemoActivity.class);
                        context.startActivity(intent);
                        break;
                    case 1:   //图片
                        PhotoPickerIntent intent1 = new PhotoPickerIntent(context);
                        intent1.setSelectModel(SelectModel.MULTI);
                        intent1.setShowCarema(true); // 是否显示拍照
                        intent1.setMaxTotal(1); // 最多选择照片数量，默认为9
                        intent1.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                        ((Activity) context).startActivityForResult(intent1, BluetoothChatActivity.REQUEST_CAMERA_CODE);
                        break;
                    case 2:   //小视频
                        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                                .doH264Compress(true)
                                .smallVideoWidth(480)
                                .smallVideoHeight(360)
                                .recordTimeMax(6 * 1000)
                                .maxFrameRate(20)
                                .minFrameRate(8)
                                .captureThumbnailsTime(1)
                                .recordTimeMin((int) (1.5 * 1000))
                                .build();
                        MediaRecorderActivity.goSmallVideoRecorder((Activity) context, BluetoothChatActivity.class.getName(), config, true, BluetoothChatActivity.REQUEST_VIDEO_CODE);
                        break;
                    case 3:
                        Intent intent2 = new Intent(context, BasicMapActivity.class);
                        context.startActivity(intent2);
                        break;

                }
            }
        });
        return view;
    }
}
