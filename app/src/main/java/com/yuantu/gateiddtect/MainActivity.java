package com.yuantu.gateiddtect;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.yuantu.gateiddtect.adapter.FaceAdapter;
import com.yuantu.gateiddtect.base.BaseActivity;
import com.yuantu.gateiddtect.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yxj on 2018/06/19.
 */
public class MainActivity extends BaseActivity {
	private final String TAG = MainActivity.class.getSimpleName().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;

	@BindView(R.id.btn_register)
	Button btnRegister;
	@BindView(R.id.btn_detect)
	Button btnDetect;
	@BindView(R.id.rv_face)
	RecyclerView rcFace;
	private FaceAdapter adapter;

	@Override
	public int getContentView() {
		return R.layout.activity_main;
	}

	@Override
	protected void initView() {
		adapter = new FaceAdapter();
		View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_face,null);
		adapter.setEmptyView(emptyView);
		adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
		adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position)->{
			showDialog("是否删除","确定要删除该用户信息吗",
					"删除",()->{
						ToastUtils.showShort(MainActivity.this,"删除");
						GateApp.instance.mFaceDB.delete(GateApp.instance.mFaceDB.mRegister.get(position).mName);
						adapter.notifyItemRemoved(position);

					},"再想想",()->{
						ToastUtils.showShort(MainActivity.this,"等等");
					});
		});
		rcFace.setLayoutManager(new GridLayoutManager(this,4));
		rcFace.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		adapter.setNewData(GateApp.instance.mFaceDB.mRegister);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = GateApp.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = ((GateApp)(MainActivity.this.getApplicationContext())).getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = GateApp.decodeImage(file);
			startRegister(bmp, file);
		}
	}

	@OnClick(R.id.btn_register)
	public void register(){
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		ContentValues values = new ContentValues(1);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		((GateApp)(MainActivity.this.getApplicationContext())).setCaptureImage(uri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
	}

	@OnClick(R.id.btn_detect)
	public void detect(){
		if( ((GateApp)getApplicationContext()).mFaceDB.mRegister.isEmpty() ) {
			Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
		} else {
			//0是后置，1是前置
			startDetector(1);
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}

					// TODO handle non-primary volumes
				} else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(this, contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
							split[1]
					};

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		String end = img_path.substring(img_path.length() - 4);
		if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
			return null;
		}
		return img_path;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param mBitmap
	 */
	private void startRegister(Bitmap mBitmap, String file) {
		Intent it = new Intent(MainActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
		Intent it = new Intent(MainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

}

