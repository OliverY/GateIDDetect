package com.yuantu.gateiddtect.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuantu.gateiddtect.R;
import com.yxj.dialog.BaseDialog;

import butterknife.BindView;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:14
 * -----------------------------------------
 * Description:
 */
public class DetectSucceedDialog extends BaseDialog {

    private static final String TAG = DetectSucceedDialog.class.getSimpleName();

    protected DetectSucceedDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseDialog.Builder {

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_percent)
        TextView tvPercent;

        private String name;
        private float percent;
        private String imgUrl;

        public Builder(Context context) {
            super(context);
        }

        @Override
        public int setContentView() {
            return R.layout.dialog_detect_succeed;
        }

        @Override
        public void initView(View layout) {
            img = layout.findViewById(R.id.img);
            tvName = layout.findViewById(R.id.tv_name);
            tvPercent = layout.findViewById(R.id.tv_percent);

            Glide.with(context).load(imgUrl).into(img);
            tvName.setText(name);

            percent = Math.round(percent * 10000) / 100f;
            tvPercent.setText("匹配度：" + percent + "%");
        }

        public Builder setData(String name, float percent, String imgUrl) {
            this.name = name;
            this.percent = percent;
            this.imgUrl = imgUrl;
            return this;
        }

    }

}
