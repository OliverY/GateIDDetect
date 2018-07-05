package com.yuantu.gateiddtect.ui.detect;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.base.BaseActivity;
import com.yuantu.gateiddtect.bean.FaceRegist;
import com.yuantu.gateiddtect.ui.MvpBaseActivity;
import com.yuantu.gateiddtect.widget.dialog.DetectSucceedDialog;
import com.yxj.dialog.AnimType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yxj on 2018/06/219.
 */

@Route(path = Constants.AROUTER.DETECT)
public class DetecterActivity extends MvpBaseActivity implements DetectView, OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private int mWidth, mHeight, mFormat;
    @BindView(R.id.surfaceView)
    CameraSurfaceView mSurfaceView;
    @BindView(R.id.glsurfaceView)
    CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();

    List<AFT_FSDKFace> result = new ArrayList<>();

    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    //    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;

    // 认证成功弹窗时暂停

    DetectPresenter presenter;

    @Override
    public void showDetectDialog(String name, float percent, String imgUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new DetectSucceedDialog.Builder(DetecterActivity.this)
                        .setData(name, percent, imgUrl)
                        .setGravity(Gravity.CENTER)
                        .setAnim(AnimType.FalL)
                        .show();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        presenter.setPauseDetected(false);
                    }
                }, 3000);
            }
        });

    }

    @Override
    public void initPresenter() {
        presenter = new DetectPresenter(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCameraRotate = getIntent().getIntExtra("Camera", 0) == 0 ? 90 : 270;
        mCameraMirror = getIntent().getIntExtra("Camera", 0) == 0 ? false : true;
        mWidth = 1920;
        mHeight = 1080;
        mFormat = ImageFormat.NV21;
        mHandler = new Handler();

        mGLSurfaceView.setOnTouchListener(this);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        mSurfaceView.debug_print_fps(true, false);
    }


    @Override
    protected void initData() {
        initAFTEngine();
        presenter.startDetectLooper(mWidth, mHeight, mCameraRotate);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stopDetectLooper();
        uninitialAFTEngine();
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera = Camera.open(mCameraID);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for (int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {
    }

    @Override
    public boolean startPreviewImmediately() {
        return true;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (presenter.getFRAbsLoop().getmImageNV21() == null) {
            if (!result.isEmpty() && !presenter.isPauseDetected()) {
                mAFT_FSDKFace = result.get(0).clone();
                presenter.setmAFT_FSDKFace(mAFT_FSDKFace);
                presenter.getFRAbsLoop().setmImageNV21(data.clone());
            }
        }
        //copy rects
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CameraHelper.touchFocus(mCamera, event, v, this);
        return false;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!");
        }
    }

    @Override
    public void onClick(View view) {
        // 设置前后摄像头
//		if (view.getId() == R.id.imageButton) {
//			if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
//				mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
//				mCameraRotate = 270;
//				mCameraMirror = true;
//			} else {
//				mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
//				mCameraRotate = 90;
//				mCameraMirror = false;
//			}
//			mSurfaceView.resetCamera();
//			mGLSurfaceView.setRenderConfig(mCameraRotate, mCameraMirror);
//			mGLSurfaceView.getGLES2Render().setViewAngle(mCameraMirror, mCameraRotate);
//		}
    }

    private void initAFTEngine() {
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(Constants.Arc.appid, Constants.Arc.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());
    }

    private void uninitialAFTEngine() {
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());
    }
}
