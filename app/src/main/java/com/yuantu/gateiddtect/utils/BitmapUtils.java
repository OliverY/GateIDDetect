package com.yuantu.gateiddtect.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Author:  Yxj
 * Time:    2018/6/27 上午9:56
 * -----------------------------------------
 * Description:
 */
public class BitmapUtils {

    public static Bitmap rotateBitmap(Bitmap srcBitmap,int degree){
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),srcBitmap.getHeight(),matrix,true);
    }
}
