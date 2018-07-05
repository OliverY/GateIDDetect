package com.yuantu.gateiddtect.ui.splash;

import android.Manifest;
import android.os.Handler;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.ui.MvpBaseActivity;

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
    protected void initPresenter() {
        splashPresenter = new SplashPresenter(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

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
                .navigation(this, new NavCallback() {
                    @Override
                    public void onArrival(Postcard postcard) {
                        finish();
                    }
                });
    }

    @Override
    public void loadDataFinished() {
        handler.postDelayed(() -> {
            hideProgress();
            openMain();
        }, 2000);
    }

    Handler handler = new Handler();
}
