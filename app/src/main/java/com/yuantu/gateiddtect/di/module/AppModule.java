package com.yuantu.gateiddtect.di.module;

import android.app.Application;
import android.content.Context;

import com.yuantu.gateiddtect.arc.ArcManager;
import com.yuantu.gateiddtect.data.DBHelper;
import com.yuantu.gateiddtect.data.DBHelperImpl;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.data.FileHelper;
import com.yuantu.gateiddtect.data.FileHelperImpl;
import com.yuantu.gateiddtect.di.ApplicationContext;

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

    @Provides
    @ApplicationContext
    Context provideContext(){
        return application;
    }

    @Singleton
    @Provides
    ArcManager provideArcManager(){
        return new ArcManager();
    }

    @Provides
    @Singleton
    FileHelper provideFileHelper(FileHelperImpl fileHelper){
        return fileHelper;
    }

    @Provides
    @Singleton
    DBHelper provideDBHelper(DBHelperImpl dbHelper){
        return dbHelper;
    }

    @Singleton
    @Provides
    FaceDB provideFaceDB(DBHelper dbHelper,FileHelper fileHelper){
        return new FaceDB(application,dbHelper,fileHelper);
    }

}
