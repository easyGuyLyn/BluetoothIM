package com.example.administrator.myapplication.mvcHelper.mvc;

/**
 * 用于外部取消请求的处理。
 */
public interface RequestHandle {

    public void cancle();

    public boolean isRunning();

}