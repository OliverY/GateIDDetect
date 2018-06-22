package com.yuantu.gateiddtect.widget.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
public class ShowPortraitDialog extends CustomDialog {

    private static final String TAG = ShowPortraitDialog.class.getSimpleName();
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_faces)
    TextView tvFaces;

    OnDialogClick click;

    private String name;
    private List<String> imgList;
    private PortraitAdapter adapter;

    public static ShowPortraitDialog newInstance(ArrayList<String> imgList,String name) {
        ShowPortraitDialog dialog = new ShowPortraitDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(Constants.EXTRA.IMG_LIST, imgList);
        args.putString(Constants.EXTRA.NAME, name);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.getAttributes().windowAnimations = R.style.CustomDialog;

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_show_protrait, container);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgList = getArguments().getStringArrayList(Constants.EXTRA.IMG_LIST);
        name = getArguments().getString(Constants.EXTRA.NAME);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        setCancelable(true);
    }

    private void initView() {
        adapter = new PortraitAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        rv.setAdapter(adapter);
        adapter.setNewData(imgList);

        tvName.setText(name);
        tvFaces.setText("采样数："+imgList.size());
    }

    @OnClick(R.id.btn_add)
    public void add() {
        if (click != null) {
            click.add();
        }
        dismiss();
    }

    @OnClick(R.id.btn_delete)
    public void delete() {
        if (click != null) {
            click.delete();
        }
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    public void cancel() {
        if (click != null) {
            click.cancel();
        }
        dismiss();
    }

    public void setClick(OnDialogClick click) {
        this.click = click;
    }

    public interface OnDialogClick {
        void add();

        void delete();

        void cancel();
    }

}
