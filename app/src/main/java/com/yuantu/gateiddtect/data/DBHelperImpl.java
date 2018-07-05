package com.yuantu.gateiddtect.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yuantu.gateiddtect.data.model.DaoMaster;
import com.yuantu.gateiddtect.data.model.DaoSession;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.utils.Logger;

import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/7/5 下午12:10
 * -----------------------------------------
 * Description:
 */
public class DBHelperImpl implements DBHelper {

    private DaoSession daoSession;

    private DBHelperImpl() {
    }

    private static class Holder {
        private static final DBHelperImpl instance = new DBHelperImpl();
    }

    public static DBHelperImpl getInstance() {
        return Holder.instance;
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "detect.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    @Override
    public void addFace(FaceModel faceModel) {
        daoSession.getFaceModelDao().insertOrReplace(faceModel);
        Logger.i("database addFace success");
    }

    @Override
    public void updateFace(FaceModel faceModel) {
        daoSession.getFaceModelDao().update(faceModel);
        Logger.i("database update face success");
    }

    @Override
    public List<FaceModel> queryAll() {
        // 从数据库中查询所有的faceId
        return daoSession.getFaceModelDao().loadAll();
    }

    @Override
    public FaceModel queryById(long id) {
        return daoSession.getFaceModelDao().load(id);
    }

    @Override
    public void deleteFace(long id) {
        daoSession.getFaceModelDao().deleteByKey(id);
    }

    @Override
    public void clear(){
        daoSession.clear();
    }
}
