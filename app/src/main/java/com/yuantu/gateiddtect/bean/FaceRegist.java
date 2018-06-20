package com.yuantu.gateiddtect.bean;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/6/20 上午8:45
 * -----------------------------------------
 * Description:
 */
public class FaceRegist {
    public String id;
    public List<AFR_FSDKFace> mFaceList;

    public FaceRegist(String id) {
        this.id = id;
        mFaceList = new ArrayList<>();
    }
}