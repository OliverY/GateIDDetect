package com.yuantu.gateiddtect.di.component;

import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Author:  Yxj
 * Time:    2018/7/17 下午2:09
 * -----------------------------------------
 * Description:
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(GateApp app);

    FaceDB getFaceDB();
}
