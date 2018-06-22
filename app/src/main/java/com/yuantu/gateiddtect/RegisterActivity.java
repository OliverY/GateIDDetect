package com.yuantu.gateiddtect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtImageView;
import com.yuantu.gateiddtect.base.BaseActivity;
import com.yuantu.gateiddtect.widget.dialog.RegistPortraitDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxj on 2018/06/19.
 */

public class RegisterActivity extends BaseActivity implements SurfaceHolder.Callback {
	private final String TAG = this.getClass().toString();
	private final static int MSG_CODE = 0x1000;
	private final static int MSG_EVENT_REG = 0x1001;
	private final static int MSG_EVENT_NO_FACE = 0x1002;
	private final static int MSG_EVENT_NO_FEATURE = 0x1003;
	private final static int MSG_EVENT_FD_ERROR = 0x1004;
	private final static int MSG_EVENT_FR_ERROR = 0x1005;
	private UIHandler mUIHandler;
	// Intent data.
	private String mFilePath;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBitmap;
	private Rect src = new Rect();
	private Rect dst = new Rect();
	private Thread view;
	private AFR_FSDKFace mAFR_FSDKFace;

	private long id;

	@Override
	public int getContentView() {
		return R.layout.activity_register;
	}

	@Override
	protected void initView() {
		id = getIntent().getLongExtra(Constants.EXTRA.ID,-1);

		//initial data.
		if (!getIntentData(getIntent().getExtras())) {
			Log.e(TAG, "getIntentData fail!");
			this.finish() ;
		}

		mUIHandler = new UIHandler();
		mBitmap = GateApp.decodeImage(mFilePath);
		src.set(0,0,mBitmap.getWidth(),mBitmap.getHeight());
		mSurfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);
		mSurfaceView.getHolder().addCallback(this);
		view = new Thread(new Runnable() {
			@Override
			public void run() {
				while (mSurfaceHolder == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

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
				err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
				Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
				while (mSurfaceHolder != null) {
					Canvas canvas = mSurfaceHolder.lockCanvas();
					if (canvas != null) {
						Paint mPaint = new Paint();
						boolean fit_horizontal = canvas.getWidth() / (float)src.width() < canvas.getHeight() / (float)src.height() ? true : false;
						float scale = 1.0f;
						if (fit_horizontal) {
							scale = canvas.getWidth() / (float)src.width();
							dst.left = 0;
							dst.top = (canvas.getHeight() - (int)(src.height() * scale)) / 2;
							dst.right = dst.left + canvas.getWidth();
							dst.bottom = dst.top + (int)(src.height() * scale);
						} else {
							scale = canvas.getHeight() / (float)src.height();
							dst.left = (canvas.getWidth() - (int)(src.width() * scale)) / 2;
							dst.top = 0;
							dst.right = dst.left + (int)(src.width() * scale);
							dst.bottom = dst.top + canvas.getHeight();
						}
						canvas.drawBitmap(mBitmap, src, dst, mPaint);
						canvas.save();
						canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
						canvas.translate(dst.left / scale, dst.top / scale);
						for (AFD_FSDKFace face : result) {
							mPaint.setColor(Color.RED);
							mPaint.setStrokeWidth(10.0f);
							mPaint.setStyle(Paint.Style.STROKE);
							canvas.drawRect(face.getRect(), mPaint);
						}
						canvas.restore();
						mSurfaceHolder.unlockCanvasAndPost(canvas);
						break;
					}
				}

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
					if(error1.getCode() == error1.MOK) {
						mAFR_FSDKFace = result1.clone();
						int width = result.get(0).getRect().width();
						int height = result.get(0).getRect().height();
						Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
						Canvas face_canvas = new Canvas(face_bitmap);
						face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);
						Message reg = Message.obtain();
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
		});
		view.start();
	}

	@Override
	protected void initData() {

	}

	/**
	 * @note bundle data :
	 * String IMAEG_PATH
	 *
	 * @param bundle
	 */
	private boolean getIntentData(Bundle bundle) {
		try {
			mFilePath = bundle.getString(Constants.EXTRA.IMAEG_PATH);
			if (mFilePath == null || mFilePath.isEmpty()) {
				return false;
			}
			Log.i(TAG, "getIntentData:" + mFilePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		try {
			view.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class UIHandler extends android.os.Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_CODE) {
				if (msg.arg1 == MSG_EVENT_REG) {
					Bitmap bitmap = (Bitmap) msg.obj;
					if(id == -1){//新增
						showAddDialog(bitmap);
					}else{//添加
						GateApp.instance.mFaceDB.updateFace(id,mAFR_FSDKFace,bitmap);
						finish();
					}
				} else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
					Toast.makeText(RegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_NO_FACE ){
					Toast.makeText(RegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
					Toast.makeText(RegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FR_ERROR){
					Toast.makeText(RegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void showAddDialog(Bitmap bitmap){
		RegistPortraitDialog dialog = RegistPortraitDialog.newInstance(bitmap);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		dialog.show(ft,"showRegist");
		dialog.setClick((name)->{
			GateApp.instance.mFaceDB.addFace(name, mAFR_FSDKFace,bitmap);
			finish();
		});

	}

}
