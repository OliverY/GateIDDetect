package com.yuantu.gateiddtect.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.yuantu.gateiddtect.data.model.FaceModel;

import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午11:47
 * -----------------------------------------
 * Description:
 */
public interface DBHelper {

    void init(Context context);

    void addFace(FaceModel faceModel);

    void updateFace(FaceModel faceModel);

    List<FaceModel> queryAll();

    FaceModel queryById(long id);

    void deleteFace(long id);

    void clear();

}
