package com.yuantu.gateiddtect.bean;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/6/20 下午1:51
 * -----------------------------------------
 * Description:
 */
public class MyFaceRegist {

    public long id;         //  自动生成，不需要赋值
    public String faceId;   //  用于注册虹软的id，16位的uuid（截取前16位）
    public String name;     //  用户的姓名
    public List<AFR_FSDKFace> mFaceList;

    public MyFaceRegist() {
        mFaceList = new ArrayList<>();
    }
}
