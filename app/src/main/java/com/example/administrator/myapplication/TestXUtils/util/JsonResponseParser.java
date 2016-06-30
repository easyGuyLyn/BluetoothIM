package com.example.administrator.myapplication.TestXUtils.util;

import java.lang.reflect.Type;

import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import android.util.Log;

import com.google.gson.Gson;

/**
 * @author 石岩
 */
public class JsonResponseParser implements ResponseParser {
	//检查服务器返回的响应头信息
	@Override
	public void checkResponse(UriRequest request) throws Throwable {
	}

	/**
	 * 转换result为resultType类型的对象
	 *
	 * @param resultType  返回值类型(可能带有泛型信息)
	 * @param resultClass 返回值类型
	 * @param result      字符串数据
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {
		return new Gson().fromJson(result, resultClass);
	}
}
