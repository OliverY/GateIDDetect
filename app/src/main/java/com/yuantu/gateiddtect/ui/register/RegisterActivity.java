package com.yuantu.gateiddtect.ui.register;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.ui.MvpBaseActivity;
import com.yuantu.gateiddtect.utils.BitmapUtils;
import com.yuantu.gateiddtect.utils.CountDown;
import com.yuantu.gateiddtect.widget.dialog.RegistPortraitDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yxj on 2018/06/219.
 */

@Route(path = Constants.AROUTER.REGISTER)
public class RegisterActivity extends MvpBaseActivity implements RegisterView, OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;

    private int mWidth, mHeight, mFormat;
    @BindView(R.id.surfaceView)
    public CameraSurfaceView mSurfaceView;
    @BindView(R.id.glsurfaceView)
    public CameraGLSurfaceView mGLSurfaceView;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    private Camera mCamera;
    private long id;
    private String name;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();

    private CountDown countDown;

    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;
    private AFR_FSDKFace mAFR_FSDKFace;
    private UIHandler mUIHandler;

    // 认证成功弹窗时暂停
    boolean pauseDetected = false;

    RegisterPresenter presenter;

    @Override
    public void initPresenter() {
        presenter = new RegisterPresenter(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_new_register;
    }

    @Override
    protected void initView() {

        id = getIntent().getLongExtra(Constants.EXTRA.ID, -1);
        name = getIntent().getStringExtra(Constants.EXTRA.NAME);

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

        mUIHandler = new UIHandler();

        initAFT();

        countDown = new CountDown(3, new CountDown.Callback() {

            @Override
            public void onSuccess() {

                if (pauseDetected) {
                    return;
                }
                pauseDetected = true;
                //把byte[]解析成bitmap，再把bitmap截图，保存起来
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                Bitmap mBitmap = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                if (mBitmap != null) {
                    save(mBitmap);
                }

                hideTips();
            }

            @Override
            public void onPreStart() {
                setTips("开始倒计时");
            }

            @Override
            public void onStep(int count) {
                setTips(count + "");
            }
        }, 1000);
    }


    @Override
    protected void initData() {
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        mFRAbsLoop.shutdown();
        countDown.stop();
        countDown = null;
        stopAFT();

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
            //parameters.setPreviewFpsRange(15000, 30000);
            //parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            //parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            //parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
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

        if (!result.isEmpty() && !pauseDetected) {
            mAFT_FSDKFace = result.get(0).clone();
            mImageNV21 = data.clone();
        }

        // 检测到一张人脸就开始倒计时，中间有中断就停止
        if (result.size() == 1) {
            Log.d("yxj", "检测到人脸");
            countDown.start();
        } else {
            Log.d("yxj", "无检测");
            hideTips();
            countDown.reset();
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

    private void save(Bitmap mBitmap) {
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
        try {
            ImageConverter convert = new ImageConverter();
            convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(mBitmap, data)) {
                Log.d(TAG, "convert ok!");
            }
            convert.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AFD_FSDKEngine engine = new AFD_FSDKEngine();
        AFD_FSDKVersion version = new AFD_FSDKVersion();
        List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
        AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(Constants.Arc.appid, Constants.Arc.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
        if (err.getCode() != AFD_FSDKError.MOK) {
            Message reg = Message.obtain();
            reg.what = MSG_CODE;
            reg.arg1 = MSG_EVENT_FD_ERROR;
            reg.arg2 = err.getCode();
            mUIHandler.sendMessage(reg);
        }
        err = engine.AFD_FSDK_GetVersion(version);
        Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
        err = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

        if (!result.isEmpty()) {
            AFR_FSDKVersion version1 = new AFR_FSDKVersion();
            AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
            AFR_FSDKFace result1 = new AFR_FSDKFace();
            AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(Constants.Arc.appid, Constants.Arc.fr_key);
            Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
            if (error1.getCode() != AFD_FSDKError.MOK) {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_FR_ERROR;
                reg.arg2 = error1.getCode();
                mUIHandler.sendMessage(reg);
            }
            error1 = engine1.AFR_FSDK_GetVersion(version1);
            Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
            error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
            Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
            if (error1.getCode() == error1.MOK) {
                mAFR_FSDKFace = result1.clone();
                int width = result.get(0).getRect().width();
                int height = result.get(0).getRect().height();
                Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Canvas face_canvas = new Canvas(face_bitmap);
                face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);
                Message reg = Message.obtain();

                face_bitmap = BitmapUtils.rotateBitmapAndMirror(face_bitmap, mCameraRotate + 180);

                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_REG;
                reg.obj = face_bitmap;
                mUIHandler.sendMessage(reg);
            } else {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_NO_FEATURE;
                mUIHandler.sendMessage(reg);
            }
            error1 = engine1.AFR_FSDK_UninitialEngine();
            Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
        } else {
            Message reg = Message.obtain();
            reg.what = MSG_CODE;
            reg.arg1 = MSG_EVENT_NO_FACE;
            mUIHandler.sendMessage(reg);
        }
        err = engine.AFD_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
    }

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CODE) {
                if (msg.arg1 == MSG_EVENT_REG) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    showAddDialog(bitmap);
                } else if (msg.arg1 == MSG_EVENT_NO_FEATURE) {
                    Toast.makeText(RegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_NO_FACE) {
                    Toast.makeText(RegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FD_ERROR) {
                    Toast.makeText(RegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FR_ERROR) {
                    Toast.makeText(RegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showAddDialog(Bitmap bitmap) {
        if (id == -1) {// 新增
            new RegistPortraitDialog.Builder(this)
                    .setData(bitmap)
                    .setClick((name) -> {
                        presenter.save(name, mAFR_FSDKFace, bitmap);
                        mUIHandler.postDelayed(() -> {
                            finish();
                        }, 500);
                    })
                    .setOnCancelListener((DialogInterface dialog) -> {
                        pauseDetected = false;
                        countDown.reset();
                    })
                    .show();

        } else {// 再添加

            presenter.update(id, mAFR_FSDKFace, bitmap);
            Dialog dialog = new RegistPortraitDialog.Builder(this)
                    .setData(bitmap)
                    .setBtnText("保存成功")
                    .setEditText(name)
                    .show();
            mUIHandler.postDelayed(() -> {
                dialog.dismiss();
                finish();
            }, 3000);
        }
    }

    private void initAFT() {
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(Constants.Arc.appid, Constants.Arc.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());
    }

    private void stopAFT() {
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());
    }

    private void setTips(String text) {
        if (tvTips.getVisibility() == View.GONE) {
            tvTips.setVisibility(View.VISIBLE);
        }
        tvTips.setText(text);
    }

    private void hideTips() {
        if (tvTips.getVisibility() == View.VISIBLE) {
            tvTips.setVisibility(View.GONE);
        }
    }
}
