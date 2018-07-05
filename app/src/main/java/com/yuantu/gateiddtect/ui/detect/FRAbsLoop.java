package com.yuantu.gateiddtect.ui.detect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.bean.FaceRegist;

import java.io.IOException;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午9:56
 * -----------------------------------------
 * Description:
 */
public class FRAbsLoop extends AbsLoop {

    private static final String TAG = FRAbsLoop.class.getSimpleName();

    AFR_FSDKVersion version = new AFR_FSDKVersion();
    AFR_FSDKEngine engine = new AFR_FSDKEngine();
    AFR_FSDKFace result = new AFR_FSDKFace();
    List<FaceRegist> mResgist;

    byte[] mImageNV21 = null;

    private AFT_FSDKFace mAFT_FSDKFace = null;
    // 认证成功弹窗时暂停
    boolean pauseDetected = false;

    int mWidth, mHeight, mCameraRotate;

    DetectView view;

    public FRAbsLoop(int mWidth, int mHeight, int mCameraRotate, DetectView view) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mCameraRotate = mCameraRotate;
        this.view = view;
    }

    @Override
    public void setup() {
        AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(Constants.Arc.appid, Constants.Arc.fr_key);
        Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
        error = engine.AFR_FSDK_GetVersion(version);
        Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
    }

    @Override
    public void loop() {
        if (!pauseDetected && mImageNV21 != null) {
            final int rotate = mCameraRotate;
            long time = System.currentTimeMillis();
            AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
            Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
            Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
            AFR_FSDKMatching score = new AFR_FSDKMatching();
            float max = 0.0f;
            String name = null;
            FaceRegist targetFr = null;
            for (FaceRegist fr : mResgist) {
                for (AFR_FSDKFace face : fr.mFaceList) {
                    error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                    Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                    if (max < score.getScore()) {
                        max = score.getScore();
                        targetFr = fr;
                        name = fr.name;
                    }
                }
            }

            //crop
            byte[] data = mImageNV21;
            YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
            ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
            yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
            final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
            try {
                ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (max > 0.6f) {
                //fr success.
                final float max_score = max;
                Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                final String mNameShow = name;
                final FaceRegist mFaceRegist = targetFr;
                //识别到就拦截
                pauseDetected = true;

                view.showDetectDialog(mFaceRegist.name, max_score, mFaceRegist.portrait.split(Constants.REGEX.PORTRAIT)[0]);
            }
            mImageNV21 = null;
        }

    }

    public void setmImageNV21(byte[] mImageNV21) {
        this.mImageNV21 = mImageNV21;
    }

    public byte[] getmImageNV21() {
        return mImageNV21;
    }

    public void setPauseDetected(boolean pauseDetected) {
        this.pauseDetected = pauseDetected;
    }

    public boolean isPauseDetected() {
        return pauseDetected;
    }

    public void setmAFT_FSDKFace(AFT_FSDKFace mAFT_FSDKFace) {
        this.mAFT_FSDKFace = mAFT_FSDKFace;
    }

    public void setmResgist(List<FaceRegist> mResgist) {
        this.mResgist = mResgist;
    }


    @Override
    public void over() {
        AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
        Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
    }
}
