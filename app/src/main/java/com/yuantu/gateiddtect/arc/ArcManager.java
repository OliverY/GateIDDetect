package com.yuantu.gateiddtect.arc;

import android.provider.SyncStateContract;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.yuantu.gateiddtect.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午9:08
 * -----------------------------------------
 * Description:
 */
@Singleton
public class ArcManager {

    private static final String TAG = ArcManager.class.getSimpleName();

    @Inject
    public ArcManager() {
    }

    public void init() {
        AFR_FSDKVersion mFRVersion = new AFR_FSDKVersion();
        AFR_FSDKEngine mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(Constants.Arc.appid, Constants.Arc.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
    }

    //    public void destroy() {
//        if (mFREngine != null) {
//            mFREngine.AFR_FSDK_UninitialEngine();
//        }
//    }
}
