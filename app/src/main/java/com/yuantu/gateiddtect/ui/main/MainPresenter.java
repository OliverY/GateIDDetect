package com.yuantu.gateiddtect.ui.main;

import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.ui.BasePresenter;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午8:56
 * -----------------------------------------
 * Description:
 */
public class MainPresenter implements BasePresenter {

    MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void loadData() {
        view.setData(GateApp.instance.mFaceDB.mRegister);
    }


}
