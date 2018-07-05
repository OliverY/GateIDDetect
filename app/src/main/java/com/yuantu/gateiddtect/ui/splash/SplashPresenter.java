package com.yuantu.gateiddtect.ui.splash;

import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.ui.BasePresenter;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午8:10
 * -----------------------------------------
 * Description:
 */
public class SplashPresenter implements BasePresenter{

    SplashView view;

    public SplashPresenter(SplashView view) {
        this.view = view;
    }

    public void initDBData(){
        FaceDB.getInstance().loadFaces();
        view.loadDataFinished();
    }

}
