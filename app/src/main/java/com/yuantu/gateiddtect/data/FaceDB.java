package com.yuantu.gateiddtect.data;

import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.yuantu.gateiddtect.data.model.FaceModel;

import java.util.List;

/**
 * Created by yxj on 2018/06/19.
 */

public interface FaceDB {

    /**
     * 加载完整的数据
     * 1.先从数据库中加载
     * 2.再从文件中读取摘要文件
     */
    void loadFaces();

    /**
     * 更新内存、文件（图片文件，脸纹摘要）、数据库
     *
     * @param id
     * @param face
     * @param bitmap
     */
    void updateFace(long id, AFR_FSDKFace face, Bitmap bitmap);

    /**
     * 添加
     *
     * @param name 姓名,可为中文
     * @param face 人脸信息
     */
    void addFace(String name, AFR_FSDKFace face, Bitmap bitmap);

    /**
     * 删除人脸信息
     * <p>
     * 删除内存、文件、数据库
     *
     * @param id 用户的自增id
     * @return
     */
    boolean delete(long id);

    void destory();

    /**
     * 获取所有的facemodels
     * @return
     */
    List<FaceModel> getAllFaceModel();

}
