package com.yuantu.gateiddtect;

import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.yuantu.gateiddtect.bean.FaceRegist;
import com.yuantu.gateiddtect.model.FaceModel;
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

    String mDBPath;
    public List<FaceRegist> mRegister;

    public FaceDB(String path) {
        mDBPath = path;
        mRegister = new ArrayList<>();
    }

    /**
     * 获取faceData的数据
     * @param faceId
     * @return
     */
    private String getFaceDataFilePath(String faceId){
        if(TextUtils.isEmpty(mDBPath)){
           throw new RuntimeException("mDBPath can not be null");
        }
        return mDBPath + "/" + faceId + ".data";
    }

    /**
     * 从数据库中查列表信息
     * @return
     */
    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        // 从数据库中查询所有的faceId
        List<FaceModel> faceModelList = LitePal.findAll(FaceModel.class);

        for (FaceModel faceModel : faceModelList) {
            if (new File(getFaceDataFilePath(faceModel.faceId)).exists()) {
                FaceRegist regist = new FaceRegist();
                regist.id = faceModel.id;
                regist.faceId = faceModel.faceId;
                regist.name = faceModel.name;
                mRegister.add(regist);
            }
        }
        return true;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            FileInputStream fs = null;
            ExtInputStream bos = null;
            try {
                // 从文件中读取具体人脸信息
                for (FaceRegist face : mRegister) {
                    Log.d(TAG, "load name:" + face.faceId + "'s face feature data.");
                    fs = new FileInputStream(getFaceDataFilePath(face.faceId));
                    bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            face.mFaceList.add(afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    Log.d(TAG, "load name: size = " + face.mFaceList.size());
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos!=null){
                        bos.close();
                    }
                    if(fs!=null){
                        fs.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            for (FaceRegist frface : mRegister) {
                if (frface.name.equals(name)) {
                    frface.mFaceList.add(face);
                    uuid = frface.faceId;
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.
                FaceRegist frface = new FaceRegist();
                uuid = UUIDUtil.generateUUID();
                frface.faceId = uuid;
                frface.name = name;
                frface.mFaceList.add(face);

                // 保存到数据库
                FaceModel faceModel = new FaceModel();
                faceModel.faceId = uuid;
                faceModel.name = name;
                faceModel.save();

                mRegister.add(frface);
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
        for (FaceRegist frface : mRegister) {
            if (frface.id == id) {
                File delfile = new File(mDBPath + "/" + frface.faceId + ".data");
                if (delfile.exists()) {
                    delfile.delete();
                }
                //数据库删除
                LitePal.delete(FaceModel.class,id);
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
