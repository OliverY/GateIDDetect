package com.yuantu.gateiddtect.ui.splash;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.ui.MvpBaseActivity;

import butterknife.BindView;

/**
 * Author:  Yxj
 * Time:    2018/7/4 下午9:03
 * -----------------------------------------
 * Description:
 */
public class SplashActivity extends MvpBaseActivity implements SplashView {

    @BindView(R.id.ll_tips)
    public View tips;

    private String[] mPermission = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SplashPresenter splashPresenter;
    private Runnable openTask;

    @Override
    public void initPresenter() {
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
                startAnim();

                new Thread() {
                    @Override
                    public void run() {
                        splashPresenter.initDBData();
                    }
                }.start();
            }
        }, mPermission);
    }

    private void startAnim() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, -3.5f);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        tips.setAnimation(translateAnimation);
        translateAnimation.start();

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
        openTask = ()->{
                openMain();
        };
        handler.postDelayed(openTask, 2000);
    }

    Handler handler = new Handler();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(openTask);
    }
}
