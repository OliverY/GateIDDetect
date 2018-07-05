package com.yuantu.gateiddtect;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.leakcanary.LeakCanary;
import com.yuantu.gateiddtect.arc.ArcManager;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.data.model.DaoMaster;
import com.yuantu.gateiddtect.data.model.DaoSession;
import com.yuantu.gateiddtect.utils.Logger;

/**
 * Created by yxj on 2018/06/19.
 */

public class GateApp extends Application {
    private final String TAG = this.getClass().toString();
    public Uri mImage;

    public static GateApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        LeakCanary.install(this);

        //检测Arc是否正常
        ArcManager.getInstance().initCheck();

        ARouter.init(this);

        //从数据库初始化人脸数据
        FaceDB.getInstance().init(this);
        mImage = null;

    }

    public void setCaptureImage(Uri uri) {
        mImage = uri;
    }

    public Uri getCaptureImage() {
        return mImage;
    }

    /**
     * @param path
     * @return
     */
    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
