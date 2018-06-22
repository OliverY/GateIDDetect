package com.yuantu.gateiddtect.widget.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:14
 * -----------------------------------------
 * Description:
 */
public class DetectSucceedDialog extends CustomDialog {

    private static final String TAG = DetectSucceedDialog.class.getSimpleName();
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_percent)
    TextView tvPercent;

    private String name;
    private float percent;
    private String imgUrl;

    public static DetectSucceedDialog newInstance(String name,float percent,String imgUrl) {
        DetectSucceedDialog dialog = new DetectSucceedDialog();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA.NAME,name);
        args.putFloat(Constants.EXTRA.PERCENT,percent);
        args.putString(Constants.EXTRA.IMAGE_PATH,imgUrl);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);
        window.getAttributes().windowAnimations = R.style.CustomDialog;

//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        View rootView = inflater.inflate(R.layout.dialog_detect_succeed, container);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        setCancelable(true);

        name = getArguments().getString(Constants.EXTRA.NAME);
        percent = getArguments().getFloat(Constants.EXTRA.PERCENT,0);
        imgUrl = getArguments().getString(Constants.EXTRA.IMAGE_PATH);
    }

    private void initView() {
        Glide.with(getContext()).load(imgUrl).into(img);
        tvName.setText(name);

        percent = Math.round(percent*10000)/100f;
        tvPercent.setText("匹配度："+percent+"%");
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

}
