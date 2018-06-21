package com.yuantu.gateiddtect.widget.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guo.android_extend.widget.ExtImageView;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:14
 * -----------------------------------------
 * Description:
 */
public class RegistPortraitDialog extends CustomDialog {

    private static final String TAG = RegistPortraitDialog.class.getSimpleName();
    @BindView(R.id.editview)
    EditText etName;
    @BindView(R.id.img)
    ImageView img;

    OnDialogClick click;
    Bitmap bitmap;

    public static RegistPortraitDialog newInstance(Bitmap bitmap) {
        RegistPortraitDialog dialog = new RegistPortraitDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA.PORTRAIT,bitmap);
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
        getDialog().setCanceledOnTouchOutside(true);

        View rootView = inflater.inflate(R.layout.dialog_register, container);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        setCancelable(true);

        bitmap = getArguments().getParcelable(Constants.EXTRA.PORTRAIT);
    }

    private void initView() {
        img.setImageBitmap(bitmap);
    }

    @OnClick(R.id.btn_ok)
    public void add() {
        if (click != null) {
            if(!TextUtils.isEmpty(etName.getText().toString())){
                click.ok(etName.getText().toString());
                dismiss();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void setClick(OnDialogClick click) {
        this.click = click;
    }

    public interface OnDialogClick {
        void ok(String name);
    }

}
