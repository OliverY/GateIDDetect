package com.yxj.dialog.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AnimationSet;

/**
 * Author:  Yxj
 * Time:    2018/6/25 下午5:09
 * -----------------------------------------
 * Description:
 */
public abstract class BaseAnim {

    protected long mDuration = 300;

    private AnimatorSet animatorSet = new AnimatorSet();

    public void play(View view){
        setupAnimation(view);
        animatorSet.start();
    }

    protected abstract void setupAnimation(View view);

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    private void reset(View view){
        view.setPivotX(view.getMeasuredWidth() / 2.0f);
        view.setPivotY(view.getMeasuredHeight() / 2.0f);
    }
}
