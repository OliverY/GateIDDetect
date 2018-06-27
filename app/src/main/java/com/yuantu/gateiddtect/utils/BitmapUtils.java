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

    public static Bitmap rotateBitmap(Bitmap srcBitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    public static Bitmap rotateBitmapAndMirror(Bitmap bmp, int degree) {
        Matrix matrix = new Matrix();
        // 旋转 && 镜像
        matrix.postRotate(degree);
        matrix.postScale(1, -1, 100 + bmp.getWidth() / 2, 100 + bmp.getHeight() / 2);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return bitmap;
    }
}
