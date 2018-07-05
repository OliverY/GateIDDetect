package com.yuantu.gateiddtect.ui;

import com.yuantu.gateiddtect.GateApp;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午8:10
 * -----------------------------------------
 * Description:
 */
public class SplashPresenter {

    SplashView view;

    public SplashPresenter(SplashView view) {
        this.view = view;
    }

    public void initDBData(){
        GateApp app = GateApp.instance;
        app.mFaceDB.loadFaces();
        view.loadDataFinished();
    }

}
