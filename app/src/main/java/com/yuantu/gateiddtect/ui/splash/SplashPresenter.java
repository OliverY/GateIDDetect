package com.yuantu.gateiddtect.ui.splash;

import com.yuantu.gateiddtect.GateApp;
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
        GateApp app = GateApp.instance;
        app.mFaceDB.loadFaces();
        view.loadDataFinished();
    }

}