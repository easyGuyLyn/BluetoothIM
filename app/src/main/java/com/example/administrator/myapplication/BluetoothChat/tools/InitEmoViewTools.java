package com.example.administrator.myapplication.BluetoothChat.tools;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.administrator.myapplication.BluetoothChat.adapter.EmoGridAdapter;
import com.example.administrator.myapplication.BluetoothChat.adapter.EmoPagerAdapter;
import com.example.administrator.myapplication.BluetoothChat.config.ChatMessageUtils;
import com.example.administrator.myapplication.BluetoothChat.config.MyChatEditText;
import com.example.administrator.myapplication.BluetoothChat.config.TextChatMessage;
import com.example.administrator.myapplication.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */

public class InitEmoViewTools {

    public static void initEmoView(Context context, ViewPager pager_emo, CirclePageIndicator cip, MyChatEditText et_sendmessage) {

        /**初始时表情文件列表*/
        List<TextChatMessage> emos = ChatMessageUtils.textChatMessages;

        /**里面装两个R.layout.emo_gridview_layout*/
        List<View> views = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i, context, emos, et_sendmessage));
        }

        pager_emo.setAdapter(new EmoPagerAdapter(views));

        /**为PagerAdapter添加底部的圆形的导航按钮*/
        cip.setFillColor(context.getResources().getColor(R.color.colorAccent));
        cip.setViewPager(pager_emo, 0);
    }

    /**
     * 膨胀R.layout.emo_gridview_layout视图，为里面的GridView添加适配器，加载21张表情图片
     *
     * @param i
     * @return R.layout.emo_gridview_layout视图，里面加载了21张表情图片
     */
    public static View getGridView(int i, Context context, List<TextChatMessage> emos, final MyChatEditText et_sendmessage) {
        View view = View.inflate(context, R.layout.emo_gridview_layout, null);
        GridView gridview = (GridView) view.findViewById(R.id.gv_emos);
        List<TextChatMessage> list = new ArrayList<>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoGridAdapter adapter = new EmoGridAdapter(context, list);
        gridview.setAdapter(adapter);

        /**
         * 为GridView中的item添加监听器
         * 单击表情时，会将表情中的text（例如\\ue057）插入到MyChatEditText中当前光标位置的后面
         * 例如，输入了"你好"，然后点击表情，拼接后的文本就变成了"你好\ue057"
         * 随后调用MyChatEditText的setText方法设置文本，该自定义控件在继承EditText的基础上重写了setText方法
         * 会自动将"你好\ue057"转化为它对应的表情
         * 转换完毕后，要将光标从你好的好字后面，移动到你好表情符号的表情符号后面
         */
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                TextChatMessage txm = adapter.getItem(position);
                //例如 \\ue057
                String key = txm.text.toString();
                try {
                    if (!TextUtils.isEmpty(key)) {
                        //返回etChatMessage中当前光标所在位置
                        int start = et_sendmessage.getSelectionStart();
                        //将当前MyChatEditText中的文本取出，从当前光标start位置开始，插入key文本
                        CharSequence content = et_sendmessage.getText().insert(start, key);
                        //将拼接后的文本设置为MyChatEditText的显示内容
                        et_sendmessage.setText(content);
                        // 定位光标位置
                        CharSequence info = et_sendmessage.getText();
                        if (info instanceof Spannable) {
                            //重新下设定光标的位置。将光标位置置于现在中显示的内容的后面
                            et_sendmessage.setSelection(start + key.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
