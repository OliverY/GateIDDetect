package com.yuantu.gateiddtect.ui.register;

import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.ui.BasePresenter;

/**
 * Author:  Yxj
 * Time:    2018/7/5 上午10:42
 * -----------------------------------------
 * Description:
 */
public class RegisterPresenter implements BasePresenter {

    RegisterView view;

    public RegisterPresenter(RegisterView view) {
        this.view = view;
    }

    public void save(String name, AFR_FSDKFace mAFR_FSDKFace, Bitmap bitmap) {
        GateApp.getInstance().getFaceDB().addFace(name,mAFR_FSDKFace,bitmap);
    }

    public void update(long id, AFR_FSDKFace mAFR_FSDKFace, Bitmap bitmap) {
        GateApp.getInstance().getFaceDB().updateFace(id, mAFR_FSDKFace, bitmap);
    }
}
