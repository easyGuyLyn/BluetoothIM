package com.example.administrator.myapplication.BluetoothChat.config;



import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
/**
 * 自定义单体视图组件，继承自EditText
 * 当输入文本的时候，如果在文本中检测到\\ue421这样的表情符号
 * 将普通的String转为可以图文混排的SpannableString
 * 进行显示
 *
 * @author tarena.sunwei
 *
 */
public class MyChatEditText extends EditText{

	public MyChatEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyChatEditText(Context context) {
		super(context);
	}

	/**
	 * 重写EditText的setText方法
	 * CharSequence是一个接口规范，它的实现类包括String和SpannableString
	 * 重写的方法中，利用withEmo方法返回SpannableString，
	 * 使用SpannableString的原因是，如果参数text中是带有\\ue421这样格式的内容
	 * withEmo方法会将\\ue421转为对应的表情图片，并在text字符串\\ue421的起始位置用
	 * 一个ImageSpan将其替代，最终得到一个图文混排的内容格式
	 *
	 */
	@Override
	public void setText(CharSequence text, BufferType type) {

		if(!TextUtils.isEmpty(text)){
			super.setText(withEmo(text.toString()), type);
			return;
		}
		super.setText(text, type);
	}
	/**
	 * 将字符串中的表情描述性文字转为真正的表情图片
	 *
	 * @param text 可能带有表情描述性文字的字符串
	 * @return 图文混排的SpannableString格式内容
	 */
	private CharSequence withEmo(String text) {
		return ChatMessageUtils.toSpannableString(getContext(), text);
	}
}
