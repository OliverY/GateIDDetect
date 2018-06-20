package com.yuantu.gateiddtect;

import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.yuantu.gateiddtect.bean.FaceRegist;
import com.yuantu.gateiddtect.bean.MyFaceRegist;
import com.yuantu.gateiddtect.model.FaceID;
import com.yuantu.gateiddtect.utils.UUIDUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxj on 2018/06/19.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();

    public static String appid = "6QMfWWYQaceGYb1ipnUcFEdTCHbdn26amgTVNb4FuEz";
    public static String ft_key = "3F9yy2fANTYfWKU61sUmiaE3C78XpDZ62SUjEPGy4xJK";
    public static String fd_key = "3F9yy2fANTYfWKU61sUmiaEAMWPfrwsuHN8h2XEGes27";
    public static String fr_key = "3F9yy2fANTYfWKU61sUmiaEf17SPS1BS5yf4tcc7bc31";
    public static String age_key = "3F9yy2fANTYfWKU61sUmiaEuKuxkCPan8FHb9AbnW6oZ";
    public static String gender_key = "3F9yy2fANTYfWKU61sUmiaF2VKDu7voTfZH4vA2r4vkN";

    String mDBPath;
    public List<MyFaceRegist> mMyRegister;
    AFR_FSDKEngine mFREngine;
    AFR_FSDKVersion mFRVersion;
    boolean mUpgrade;

    public FaceDB(String path) {
        mDBPath = path;
        mMyRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean loadInfo() {
        if (!mMyRegister.isEmpty()) {
            return false;
        }
        // 从数据库中查询所有的faceId
        List<FaceID> faceIDList = LitePal.findAll(FaceID.class);

        for (FaceID faceID : faceIDList) {
            if (new File(mDBPath + "/" + faceID.faceId + ".data").exists()) {
                MyFaceRegist regist = new MyFaceRegist();
                regist.id = faceID.id;
                regist.faceId = faceID.faceId;
                regist.name = faceID.name;
                mMyRegister.add(regist);
            }
        }
        return true;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            try {
                for (MyFaceRegist face : mMyRegister) {
                    Log.d(TAG, "load name:" + face.faceId + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.id + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            face.mFaceList.add(afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                    Log.d(TAG, "load name: size = " + face.mFaceList.size());
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 添加
     * @param name 姓名
     * @param face
     */
    public void addFace(String name, AFR_FSDKFace face) {
        try {
            //check if already registered.
            boolean add = true;
            String uuid = "";
            for (MyFaceRegist frface : mMyRegister) {
                if (frface.name.equals(name)) {
                    frface.mFaceList.add(face);
                    uuid = frface.faceId;
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.
                MyFaceRegist frface = new MyFaceRegist();
                uuid = UUIDUtil.generateUUID();
                frface.faceId = uuid;
                frface.name = name;
                frface.mFaceList.add(face);
                mMyRegister.add(frface);
            }

            //save new feature
            FileOutputStream fs = new FileOutputStream(mDBPath + "/" + uuid + ".data", true);
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeBytes(face.getFeatureData());
            bos.close();
            fs.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除人脸信息
     * @param id 用户的自增id
     * @return
     */
    public boolean delete(long id) {
        //check if already registered.
        boolean find = false;
        for (MyFaceRegist frface : mMyRegister) {
            if (frface.id == id) {
                File delfile = new File(mDBPath + "/" + frface.faceId + ".data");
                if (delfile.exists()) {
                    delfile.delete();
                }
                //数据库删除
                LitePal.delete(FaceID.class,id);
                find = true;
                break;
            }
        }
        return find;
    }

    public boolean upgrade() {
        return false;
    }
}
