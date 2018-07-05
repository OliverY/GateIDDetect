package com.yuantu.gateiddtect.data;

import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author:  Yxj
 * Time:    2018/7/5 下午12:11
 * -----------------------------------------
 * Description:
 */
public class FileHelperImpl implements FileHelper {

    @Override
    public void saveBitmap(String name,Bitmap bitmap) {
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

    @Override
    public AFR_FSDKFace readFile(String path,byte[] featureData) {
        FileInputStream fs = null;
        ExtInputStream bos = null;

        try {
            // 从文件中读取具体人脸信息
            fs = new FileInputStream(path);
            bos = new ExtInputStream(fs);
            AFR_FSDKFace afr = null;
            do {
                if (afr != null) {
                    return afr;
                }
                afr = new AFR_FSDKFace();
            } while (bos.readBytes(featureData));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void saveFile(String path,byte[] featureData) {
        // 保存人脸文件
        FileOutputStream fs = null;
        ExtOutputStream bos = null;
        try {
            //save new feature
            fs = new FileOutputStream(path, true);
            bos = new ExtOutputStream(fs);
            bos.writeBytes(featureData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void deleteFile(String path) {
        File delfile = new File(path);
        if (delfile.exists()) {
            delfile.delete();
        }
    }
}
