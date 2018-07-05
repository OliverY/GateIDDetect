package com.yuantu.gateiddtect.ui;

import android.Manifest;

import com.alibaba.android.arouter.launcher.ARouter;
import com.yuantu.gateiddtect.Constants;

/**
 * Author:  Yxj
 * Time:    2018/7/4 下午9:03
 * -----------------------------------------
 * Description:
 */
public class SplashActivity extends MvpBaseActivity implements SplashView {

    private String[] mPermission = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SplashPresenter splashPresenter;

    @Override
    public int getContentView() {
        return 0;
    }

    @Override
    protected void initView() {
        splashPresenter = new SplashPresenter(this);
    }

    @Override
    protected void initData() {
        requestPermission(new Callback() {
            @Override
            public void requestFailed() {
                finish();
            }

            @Override
            public void requestPermissionComplete() {
                showProgress();
                new Thread() {
                    @Override
                    public void run() {
                        splashPresenter.initDBData();
                    }
                }.start();
            }
        }, mPermission);
    }

    @Override
    public void openMain() {
        ARouter.getInstance()
                .build(Constants.AROUTER.MAIN)
                .navigation();
    }

    @Override
    public void loadDataFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                openMain();
            }
        });
    }
}
