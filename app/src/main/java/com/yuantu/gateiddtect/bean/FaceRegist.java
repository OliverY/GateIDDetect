package com.yuantu.gateiddtect.bean;

import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.yuantu.gateiddtect.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/6/20 下午1:51
 * -----------------------------------------
 * Description:
 */
public class FaceRegist {

    public long id;         //  自动生成，不需要赋值
    public String faceId;   //  用于注册虹软的id，16位的uuid（截取前16位）
    public String name;     //  用户的姓名
    public String portrait; //  人脸的图片的url数组
    public List<AFR_FSDKFace> mFaceList;

    public FaceRegist() {
        mFaceList = new ArrayList<>();
    }

    public void addPortrait(String newPortrait){
        if(TextUtils.isEmpty(portrait)){
            portrait = newPortrait;
        }else{
            StringBuilder sb = new StringBuilder(portrait);
            portrait = sb.append(Constants.REGEX.PORTRAIT).append(newPortrait).toString();
        }

        Log.e("TAG","name:"+name+",portrait:"+portrait);
    }

}
