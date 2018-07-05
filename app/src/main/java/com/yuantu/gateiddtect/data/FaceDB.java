package com.yuantu.gateiddtect.data;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.yuantu.gateiddtect.data.bean.FaceRegist;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.utils.FileUtils;
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
     *
     * @param faceId
     * @return
     */
    private String getFaceDataFilePath(String faceId) {
        if (TextUtils.isEmpty(mDBPath)) {
            throw new RuntimeException("mDBPath can not be null");
        }
        return mDBPath + "/" + faceId + ".data";
    }

    /**
     * 从数据库中查列表信息
     *
     * @return
     */
    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        // 从数据库中查询所有的faceId
        List<FaceModel> faceModelList = LitePal.findAll(FaceModel.class);

        for (FaceModel faceModel : faceModelList) {
            if (new File(getFaceDataFilePath(faceModel.getFaceId())).exists()) {
                FaceRegist regist = new FaceRegist();
                regist.id = faceModel.getId();
                regist.faceId = faceModel.getFaceId();
                regist.name = faceModel.getName();
                regist.portrait = faceModel.getPortrait();
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
        return false;
    }

    /**
     * 更新内存、文件（图片文件，脸纹摘要）、数据库
     *
     * @param id
     * @param face
     * @param bitmap
     */
    public void updateFace(long id, AFR_FSDKFace face, Bitmap bitmap) {
        /*
            数据保存进内存及数据库
            face保存进文件中
            图片保存成文件
            （不一定按顺序）
         */

        FaceModel faceModel = LitePal.find(FaceModel.class, id);
        if (faceModel == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 图片保存到本地
        String portrait = FileUtils.generateImgName(FileUtils.getPortraitPath(), faceModel.getName());
        Log.i(TAG, "portrait:" + portrait);
        FileUtils.saveBitmap(bitmap, portrait);

        // 更新数据库
        faceModel.addPortrait(portrait);
        faceModel.save();

        for (FaceRegist frface : mRegister) {
            if (id == frface.id) {
                // 更新内存
                frface.portrait = faceModel.getPortrait();
                frface.mFaceList.add(face);
            }
        }

        //更新文件
        saveArcFaceData(face, faceModel.getFaceId());
    }

    /**
     * 添加
     *
     * @param name 姓名,可为中文
     * @param face 人脸信息
     */
    public void addFace(String name, AFR_FSDKFace face, Bitmap bitmap) {

        /*
            数据保存进内存及数据库
            face保存进文件中
            图片保存成文件
            （不一定按顺序）
         */

        //check if already exist
        boolean add = true;
        FaceRegist frface = null;
        for (FaceRegist faceRegist : mRegister) {
            if (faceRegist.name.equals(name)) {
                frface = faceRegist;
                add = false;
                break;
            }
        }

        // 图片保存到本地
        String portrait = FileUtils.generateImgName(FileUtils.getPortraitPath(), name);
        Log.i(TAG, "portrait:" + portrait);
        FileUtils.saveBitmap(bitmap, portrait);
        String faceId = "";
        if (add) {
            // not registered
            // 保存到内存
            faceId = UUIDUtil.generateUUID();

            // 保存到数据库
            FaceModel faceModel = new FaceModel();
            faceModel.setFaceId(faceId);
            faceModel.setName(name);
            faceModel.addPortrait(portrait);
            faceModel.save();

            FaceModel faceModelDB = LitePal.where("faceId = ?", faceId).findFirst(FaceModel.class);
            frface = new FaceRegist();
            frface.id = faceModelDB.getId();
            frface.faceId = faceModelDB.getFaceId();
            frface.name = faceModelDB.getName();
            frface.portrait = faceModelDB.getPortrait();
            frface.mFaceList.add(face);
            mRegister.add(frface);

        } else {
            // already exist
            // 更新数据库
            FaceModel faceModel = LitePal.find(FaceModel.class, frface.id);
            if (faceModel == null) {
                return;
            }
            faceId = faceModel.getFaceId();
            faceModel.addPortrait(portrait);
            faceModel.save();

            // 保存到内存
            frface.portrait = faceModel.getPortrait();
            frface.mFaceList.add(face);
        }

        saveArcFaceData(face, faceId);
    }

    /**
     * 保存人脸data文件
     *
     * @param face
     * @param faceId
     */
    private void saveArcFaceData(AFR_FSDKFace face, String faceId) {
        // 保存人脸文件
        FileOutputStream fs = null;
        ExtOutputStream bos = null;
        try {
            //save new feature
            fs = new FileOutputStream(getFaceDataFilePath(faceId), true);
            bos = new ExtOutputStream(fs);
            bos.writeBytes(face.getFeatureData());
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

    /**
     * 删除人脸信息
     * <p>
     * 删除内存、文件、数据库
     *
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
                LitePal.delete(FaceModel.class, id);
                mRegister.remove(frface);
                find = true;
                break;
            }
        }
        return find;
    }

}
