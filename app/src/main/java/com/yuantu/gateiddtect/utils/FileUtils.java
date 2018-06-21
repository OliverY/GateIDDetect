package com.yuantu.gateiddtect.utils;

import android.graphics.Bitmap;

import com.yuantu.gateiddtect.GateApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author:  Yxj
 * Time:    2018/6/20 下午5:13
 * -----------------------------------------
 * Description:
 */
public class FileUtils {

    public static void saveBitmap(Bitmap bitmap,String name){
        FileOutputStream fos = null;
        try {
            File file = new File(name);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
