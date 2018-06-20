package com.yuantu.gateiddtect.entity;

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
    public String mName;
    public List<AFR_FSDKFace> mFaceList;

    public FaceRegist(String name) {
        mName = name;
        mFaceList = new ArrayList<>();
    }
}