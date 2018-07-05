package com.yuantu.gateiddtect.data;

import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;

/**
 * Author:  Yxj
 * Time:    2018/7/5 下午12:06
 * -----------------------------------------
 * Description:
 */
public interface FileHelper {

    void saveBitmap(String path,Bitmap bitmap);

    AFR_FSDKFace readFile(String path,byte[] featureData);

    void saveFile(String path,byte[] featureData);

    void deleteFile(String path);

}
