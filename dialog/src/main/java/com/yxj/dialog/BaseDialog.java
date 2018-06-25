package com.yxj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yxj.dialog.anim.BaseAnim;

/**
 * Author:  Yxj
 * Time:    2018/6/22 下午5:20
 * -----------------------------------------
 * Description:
 */
public class BaseDialog extends Dialog {

    protected BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 该方法在show之后执行

        // window水平方向全屏显示
        Window window = this.getWindow();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(layoutParams);
    }

    public abstract static class Builder {

        protected Context context;
        protected FrameLayout root;

        protected Dialog dialog;

        private int gravity;
        private long duration = 300;
        private boolean cancelAble = true;
        private boolean cancelOutSide = true;
        private AnimType type;

        public Builder(Context context) {
            this.context = context;
        }

        public Dialog create() {
            root = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.dialog, null);

            dialog = new BaseDialog(context, R.style.Dialog);
            dialog.setContentView(root);

            dialog.setCancelable(cancelAble);
            dialog.setCanceledOnTouchOutside(cancelOutSide);
            dialog.getWindow().setGravity(gravity);

            // 添加动画
            dialog.setOnShowListener((DialogInterface dialog) -> {
                if (type == null) {
                    type = AnimType.Slidebottom;
                }
                BaseAnim anim = type.get();
                anim.setDuration(duration);
                anim.play(root);
            });

            // 添加子view
            if (root.getChildCount() != 0) {
                root.removeAllViews();
            }

            // 这是直接将layout添加到root中
            View layout = LayoutInflater.from(context).inflate(setContentView(), root, true);

            initView(layout);
            return dialog;
        }

        public abstract int setContentView();

        public abstract void initView(View layout);

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setCancelAble(boolean cancelAble) {
            this.cancelAble = cancelAble;
            return this;
        }

        public Builder setCancelOutSide(boolean cancelOutSide) {
            this.cancelOutSide = cancelOutSide;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setAnim(AnimType type) {
            this.type = type;
            return this;
        }

        public Dialog show() {
            if (dialog == null) {
                dialog = create();
            }
            dialog.show();
            return dialog;
        }
    }

}
