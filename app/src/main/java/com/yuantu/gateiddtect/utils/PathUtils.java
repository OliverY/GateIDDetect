package com.yuantu.gateiddtect.utils;

import com.yuantu.gateiddtect.GateApp;

import java.io.File;

/**
 * Author:  Yxj
 * Time:    2018/6/20 下午5:13
 * -----------------------------------------
 * Description:
 */
public class PathUtils {

    public static File getPortraitPath(){
        File portraitDir = GateApp.instance.getExternalFilesDir("portrait");
        if(!portraitDir.exists()){
            portraitDir.mkdirs();
        }
        return portraitDir;
    }

    public static String generateImgName(File dir,String name){
        return new File(dir,name+"_"+System.currentTimeMillis()+".jpg").getAbsolutePath();
    }
}
