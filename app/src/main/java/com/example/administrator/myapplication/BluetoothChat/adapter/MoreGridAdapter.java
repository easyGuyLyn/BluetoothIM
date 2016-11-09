package com.example.administrator.myapplication.BluetoothChat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.BluetoothChat.model.MoreBean;
import com.example.administrator.myapplication.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**

 */
public class MoreGridAdapter extends ListAdapter<MoreBean> {
    public MoreGridAdapter(Context context, List<MoreBean> datasource) {
        super(context, datasource);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = getInflater().inflate(R.layout.item_more_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        MoreBean bean = getItem(position);

        vh.iv.setBackgroundResource(bean.getDrawabeResourcecId());
        vh.tv.setText(bean.getTaskName());

        return convertView;
    }

    public class ViewHolder {
        /**
         * 图标
         */
        @Bind(R.id.iv_item_more_layout)
        ImageView iv;
        /**
         * 名字
         */
        @Bind(R.id.tv_item_more)
        TextView tv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
