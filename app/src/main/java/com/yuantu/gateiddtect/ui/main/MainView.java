package com.yuantu.gateiddtect.ui.main;

import com.yuantu.gateiddtect.data.model.FaceModel;

import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午8:57
 * -----------------------------------------
 * Description:
 */
public interface MainView {

    void openRegister();

    void openDetect();

    void setData(List<FaceModel> faceRegistList);
}
