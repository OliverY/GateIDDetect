package com.yuantu.gateiddtect.data.model;

import android.text.TextUtils;
import android.util.Log;

import com.yuantu.gateiddtect.Constants;

import org.litepal.crud.LitePalSupport;

/**
 * Author:  Yxj
 * Time:    2018/6/20 上午8:48
 * -----------------------------------------
 * Description: 用于存取数据库
 */
public class FaceModel extends LitePalSupport {

    private long id;         //  自动生成，不需要赋值
    private String faceId;   //  用于注册虹软的id，16位的uuid（截取前16位）
    private String name;     //  用户的姓名
    private String portrait; //  肖像的url，数组，以||分割

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "FaceModel{" +
                "id=" + id +
                ", faceId='" + faceId + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
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
}
