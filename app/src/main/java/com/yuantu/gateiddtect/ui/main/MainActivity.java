package com.yuantu.gateiddtect.ui.main;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.GateApp;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.data.FaceDB;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.ui.MvpBaseActivity;
import com.yuantu.gateiddtect.ui.main.adapter.FaceAdapter;
import com.yuantu.gateiddtect.widget.dialog.ShowPortraitDialog;
import com.yxj.dialog.AnimType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yxj on 2018/06/19.
 */

@Route(path = Constants.AROUTER.MAIN)
public class MainActivity extends MvpBaseActivity implements MainView {
    private final String TAG = MainActivity.class.getSimpleName().toString();

    private static final int REQUEST_CODE_OP = 2;
    private static final int REQUEST_CODE_DETECT = 3;

    @BindView(R.id.rv_face)
    RecyclerView rcFace;
    private FaceAdapter adapter;
    private List<FaceModel> faceRegistList;

    private long selectedId = -1;
    private String name = "";

    MainPresenter presenter;

    @Override
    public void initPresenter() {
        presenter = new MainPresenter(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        adapter = new FaceAdapter();
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_face, null);
        adapter.setEmptyView(emptyView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {

            FaceModel faceRegist = faceRegistList.get(position);
            String[] imgArray = faceRegist.getPortrait().split(Constants.REGEX.PORTRAIT);
            ArrayList<String> imgList = new ArrayList<>();
            for (int i = 0; i < imgArray.length; i++) {
                imgList.add(imgArray[i]);
            }

            showPortaitDialog(position, faceRegist, imgList);
        });
        rcFace.setLayoutManager(new GridLayoutManager(this, 2));
        rcFace.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        presenter.loadData();
    }

    @Override
    public void setData(List<FaceModel> faceRegistList) {
        this.faceRegistList = faceRegistList;

//        for(int i=0;i<30;i++){
//            faceRegistList.add(new FaceModel());
//        }

        adapter.setNewData(faceRegistList);
    }

    private void showPortaitDialog(int position, FaceModel faceRegist, ArrayList<String> imgList) {
        new ShowPortraitDialog.Builder(this)
                .setName(faceRegist.getName())
                .setList(imgList)
                .setClick(new ShowPortraitDialog.OnDialogClick() {
                    @Override
                    public void add() {
                        selectedId = faceRegist.getId();
                        name = faceRegist.getName();
                        openRegister();
                    }

                    @Override
                    public void delete() {
                        GateApp.getInstance().getFaceDB().delete(GateApp.getInstance().getFaceDB().getAllFaceModel().get(position).getId());
                        adapter.notifyItemRemoved(position);
                        showMessage("删除成功");
                    }

                    @Override
                    public void cancel() {

                    }
                })
                .setGravity(Gravity.BOTTOM)
                .setDuration(300)
                .setAnim(AnimType.Slidebottom)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OP) {
            // 注册重新刷新页面
            presenter.loadData();
//            adapter.setNewData(faceRegistList);
        }
    }

    @OnClick(R.id.btn_register)
    public void register() {
        openRegister();
    }

    @OnClick(R.id.btn_detect)
    public void detect() {
        if (faceRegistList.isEmpty()) {
            showMessage("没有注册人脸，请先注册！");
        } else {
            //0是后置，1是前置
            openDetect();
        }

    }

    @Override
    public void openRegister() {
        ARouter.getInstance()
                .build(Constants.AROUTER.REGISTER)
                .withLong(Constants.EXTRA.ID, selectedId)
                .withString(Constants.EXTRA.NAME, name)
                .withInt(Constants.EXTRA.CAMERA, 1)
                .navigation(this, REQUEST_CODE_OP);
        selectedId = -1;
    }

    @Override
    public void openDetect() {
        //0是后置，1是前置
        ARouter.getInstance()
                .build(Constants.AROUTER.DETECT)
                .withInt(Constants.EXTRA.CAMERA, 1)
                .navigation(this, REQUEST_CODE_DETECT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GateApp.getInstance().getFaceDB().destory();
    }
}

