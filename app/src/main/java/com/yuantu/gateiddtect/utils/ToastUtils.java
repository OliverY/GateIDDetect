package com.yuantu.gateiddtect.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Author:  Yxj
 * Time:    2018/6/19 下午3:04
 * -----------------------------------------
 * Description:
 */
public class ToastUtils {

    public static void showShort(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
