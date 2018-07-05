package com.yuantu.gateiddtect.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.utils.Logger;
import com.yuantu.gateiddtect.utils.PathUtils;
import com.yuantu.gateiddtect.utils.UUIDUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by yxj on 2018/06/19.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();

    String mDBPath;
    public List<FaceModel> mRegister;
    private DBHelper dbHelper;
    private FileHelper fileHelper;

    private FaceDB() {
    }

    private static class Holder {
        private static final FaceDB INSTANCE = new FaceDB();
    }

    public static FaceDB getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mDBPath = context.getExternalCacheDir().getPath();
        initDB(context);
        fileHelper = new FileHelperImpl();
    }

    private void initDB(Context context) {
        dbHelper = DBHelperImpl.getInstance();
        dbHelper.init(context);
    }

    /**
     * 加载完整的数据
     * 1.先从数据库中加载
     * 2.再从文件中读取摘要文件
     */
    public void loadFaces() {
        mRegister = loadDb();
        if (mRegister != null && mRegister.size() != 0) {
//            for (FaceModel face : mRegister) {
//                String path = getFaceFilePath(face.getFaceId());
//                AFR_FSDKFace afr = new AFR_FSDKFace();
//                afr = fileHelper.readFile(path, afr.getFeatureData());
//                face.getFaceList().add(afr);
//            }

            FileInputStream fs = null;
            ExtInputStream bos = null;
            try {
                // 从文件中读取具体人脸信息
                for (FaceModel face : mRegister) {
                    fs = new FileInputStream(getFaceFilePath(face.getFaceId()));
                    bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            face.getFaceList().add(afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                }
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
         */

        FaceModel faceModel = null;
        for (FaceModel model : mRegister) {
            if (model.getId() == id) {
                faceModel = model;
            }
        }

        if (faceModel == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 获取图片地址
        String portraitPath = PathUtils.generateImgName(PathUtils.getPortraitPath(), faceModel.getName());
        Log.i(TAG, "portrait:" + portraitPath);

        // 保存图片，人脸摘要
        fileHelper.saveBitmap(portraitPath, bitmap);
        fileHelper.saveFile(getFaceFilePath(faceModel.getFaceId()), face.getFeatureData());

        // 更新数据库
        faceModel.addPortrait(portraitPath);
        dbHelper.updateFace(faceModel);

        // 更新内存
        faceModel.getFaceList().add(face);

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
        FaceModel faceModel = null;
        for (FaceModel model : mRegister) {
            if (model.getName().equals(name)) {
                faceModel = model;
                add = false;
                break;
            }
        }

        // 获取图片地址
        String portrait = PathUtils.generateImgName(PathUtils.getPortraitPath(), name);
        Log.i(TAG, "portrait:" + portrait);

        String faceId = "";
        if (add) {
            // not registered
            faceId = UUIDUtil.generateUUID();

            // 保存到数据库
            faceModel = new FaceModel();
            faceModel.setFaceId(faceId);
            faceModel.setName(name);
            faceModel.addPortrait(portrait);
            dbHelper.addFace(faceModel);

            mRegister.add(faceModel);
        } else {
            // already exist
            // 更新数据库
            faceModel.addPortrait(portrait);
            dbHelper.updateFace(faceModel);
        }

        // 更新内存
        faceModel.getFaceList().add(face);

        Logger.e("query::"+dbHelper.queryById(faceModel.getId()).toString());

        // 更新文件
        fileHelper.saveBitmap(portrait, bitmap);
        fileHelper.saveFile(getFaceFilePath(faceModel.getFaceId()), face.getFeatureData());

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
            fs = new FileOutputStream(getFaceFilePath(faceId), true);
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
        for (FaceModel faceModel : mRegister) {
            if (faceModel.getId() == id) {
                // 删除文件
                fileHelper.deleteFile(getFaceFilePath(faceModel.getFaceId()));
                String portraitPath = PathUtils.generateImgName(PathUtils.getPortraitPath(), faceModel.getName());
                fileHelper.deleteFile(portraitPath);
                //数据库删除
                dbHelper.deleteFace(faceModel.getId());
                // 删除内存
                mRegister.remove(faceModel);
                find = true;
                break;
            }
        }
        return find;
    }

    /**
     * 从数据库中查列表信息
     *
     * @return
     */
    private List<FaceModel> loadDb() {
        // 从数据库中查询所有的faceId
        return dbHelper.queryAll();
    }

    /**
     * 获取faceData的数据
     *
     * @param faceId
     * @return
     */
    private String getFaceFilePath(String faceId) {
        if (TextUtils.isEmpty(mDBPath)) {
            throw new RuntimeException("mDBPath can not be null");
        }
        return mDBPath + "/" + faceId + ".data";
    }

    public void destory(){
        mRegister.clear();
        mRegister = null;
    }

}
