package com.yuantu.gateiddtect.ui.detect;

import com.arcsoft.facetracking.AFT_FSDKFace;
import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.ui.BasePresenter;

import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午9:48
 * -----------------------------------------
 * Description:
 */
public class DetectPresenter implements BasePresenter {

    private DetectView view;

    private FRAbsLoop mFRAbsLoop;

    public DetectPresenter(DetectView view) {
        this.view = view;
    }

    public void startDetectLooper(int mWidth, int mHeight, int mCameraRotate) {
        mFRAbsLoop = new FRAbsLoop(mWidth, mHeight, mCameraRotate, view);
        mFRAbsLoop.setmResgist(loadFaceData());
        mFRAbsLoop.start();
    }

    public void stopDetectLooper() {
        mFRAbsLoop.shutdown();
    }

    public List<FaceModel> loadFaceData() {
        return FaceDB.getInstance().mRegister;
    }

    public FRAbsLoop getFRAbsLoop() {
        return mFRAbsLoop;
    }

    public void setPauseDetected(boolean pauseDetected) {
        mFRAbsLoop.setPauseDetected(pauseDetected);
    }

    public boolean isPauseDetected() {
        return mFRAbsLoop.isPauseDetected();
    }

    public void setmAFT_FSDKFace(AFT_FSDKFace mAFT_FSDKFace) {
        mFRAbsLoop.setmAFT_FSDKFace(mAFT_FSDKFace);
    }
}
