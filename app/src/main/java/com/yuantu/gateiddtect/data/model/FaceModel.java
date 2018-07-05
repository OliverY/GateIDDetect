package com.yuantu.gateiddtect.data.model;

import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.yuantu.gateiddtect.Constants;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/6/20 上午8:48
 * -----------------------------------------
 * Description: 用于存取数据库
 */

@Entity
public class FaceModel {

    @Id(autoincrement = true)
    private Long id;         //  自动生成，不需要赋值
    @Unique
    private String faceId;   //  用于注册虹软的id，16位的uuid（截取前16位）
    private String name;     //  用户的姓名
    private String portrait; //  肖像的url，数组，以||分割
    @Transient
    private List<AFR_FSDKFace> faceList = new ArrayList<>();

    @Generated(hash = 2084856814)
    public FaceModel(Long id, String faceId, String name, String portrait) {
        this.id = id;
        this.faceId = faceId;
        this.name = name;
        this.portrait = portrait;
    }

    @Generated(hash = 770161393)
    public FaceModel() {
    }

    public Long getId() {
        return id;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public List<AFR_FSDKFace> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<AFR_FSDKFace> faceList) {
        this.faceList = faceList;
    }

    @Override
    public String toString() {
        return "FaceModel{" +
                "id=" + id +
                ", faceId='" + faceId + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", facesize='" + faceList.size() + '\'' +
                '}';
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

    public void setId(Long id) {
        this.id = id;
    }
}
