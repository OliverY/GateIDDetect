package com.yuantu.gateiddtect.model;

import org.litepal.crud.LitePalSupport;

/**
 * Author:  Yxj
 * Time:    2018/6/20 上午8:48
 * -----------------------------------------
 * Description:
 */
public class FaceID extends LitePalSupport {

    public long id;         //  自动生成，不需要赋值
    public String faceId;   //  用于注册虹软的id，16位的uuid（截取前16位）
    public String name;     //  用户的姓名
    public String faceUrl;  //  face摘要文件存放的地址
    public int faceCount;   //  面部照片数量

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

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }

    @Override
    public String toString() {
        return "FaceID{" +
                "id=" + id +
                ", faceId='" + faceId + '\'' +
                ", name='" + name + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", faceCount=" + faceCount +
                '}';
    }
}
