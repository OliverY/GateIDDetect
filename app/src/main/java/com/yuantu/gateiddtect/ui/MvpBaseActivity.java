package com.yuantu.gateiddtect.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yuantu.gateiddtect.utils.ProgressUtils;
import com.yuantu.gateiddtect.utils.ToastUtils;

import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Author:  Yxj
 * Time:    2018/7/4 下午10:09
 * -----------------------------------------
 * Description:
 */
public abstract class MvpBaseActivity extends Activity implements BaseView{

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPresenter();
        if(getContentView()!=0){
            setContentView(getContentView());
        }
        ButterKnife.bind(this);
        initView();
        initData();
    }

    public abstract void initPresenter();

    public abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void showProgress() {
        hideProgress();
        progressDialog = ProgressUtils.show(this);
    }

    @Override
    public void hideProgress() {
        ProgressUtils.hide(progressDialog);
    }

    protected void showMessage(String msg) {
        ToastUtils.showShort(this, msg);
    }

    /**
     * 权限申请
     *
     * @param permissions
     */
    protected void requestPermission(Callback callback,String... permissions) {
        new RxPermissions(this)
                .requestEach(permissions)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        if (!permission.granted) {
                            throw new RuntimeException("必须同意该权限");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showMessage("必须同意该权限");
                        callback.requestFailed();
                    }

                    @Override
                    public void onComplete() {
                        callback.requestPermissionComplete();
                    }
                });
    }

    protected interface Callback{
        void requestFailed();
        void requestPermissionComplete();
    }
}
