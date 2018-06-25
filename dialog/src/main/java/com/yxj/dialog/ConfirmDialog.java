package com.yxj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Author:  Yxj
 * Time:    2018/6/22 下午5:20
 * -----------------------------------------
 * Description:
 */
public class ConfirmDialog extends BaseDialog {

    protected ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseDialog.Builder {

        private TextView tvTitle;
        private TextView tvContent;
        private TextView btnLeft;
        private TextView btnRight;

        private String title;
        private String content;
        private String btnLeftTxt;
        private String btnRightTxt;
        private DialogOnClick btnLeftClick;
        private DialogOnClick btnRightClick;

        public Builder(Context context) {
            super(context);
        }

        @Override
        public int setContentView() {
            return R.layout.dialog_confirm;
        }

        @Override
        public void initView(View layout) {
            tvTitle = layout.findViewById(R.id.tv_title);
            tvContent = layout.findViewById(R.id.tv_content);
            btnLeft = layout.findViewById(R.id.btn_left);
            btnRight = layout.findViewById(R.id.btn_right);

            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            } else {
                tvTitle.setText("标题");
            }

            if (!TextUtils.isEmpty(content)) {
                tvContent.setText(content);
            } else {
                tvContent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(btnLeftTxt)) {
                btnLeft.setText(btnLeftTxt);
                btnLeft.setOnClickListener(v -> {
                    if (btnLeftClick != null) {
                        btnLeftClick.onClick(dialog);
                    }
                });
            } else {
                btnLeft.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(btnRightTxt)) {
                btnRight.setText(btnRightTxt);
                btnRight.setOnClickListener(v -> {
                    if (btnRightClick != null) {
                        btnRightClick.onClick(dialog);
                    }
                });
            } else {
                btnRight.setVisibility(View.GONE);
            }
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setBtnLeft(String btnLeftTxt, DialogOnClick click) {
            this.btnLeftTxt = btnLeftTxt;
            this.btnLeftClick = click;
            return this;
        }

        public Builder setBtnRight(String btnRightTxt, DialogOnClick click) {
            this.btnRightTxt = btnRightTxt;
            this.btnRightClick = click;
            return this;
        }

    }

    public static interface DialogOnClick {
        void onClick(Dialog dialog);
    }
}
