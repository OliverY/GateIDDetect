package com.yuantu.gateiddtect.di.module;

import android.app.Application;

import com.yuantu.gateiddtect.arc.ArcManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Author:  Yxj
 * Time:    2018/7/17 下午2:08
 * -----------------------------------------
 * Description:
 */
@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    ArcManager provideArcManager(){
        return new ArcManager();
    }



}
