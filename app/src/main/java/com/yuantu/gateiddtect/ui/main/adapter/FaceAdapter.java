package com.yuantu.gateiddtect.ui.main.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.data.model.FaceModel;
import com.yuantu.gateiddtect.utils.GlideCircleTransform;

/**
 * Author:  Yxj
 * Time:    2018/6/19 下午1:49
 * -----------------------------------------
 * Description:
 */
public class FaceAdapter extends BaseQuickAdapter<FaceModel,BaseViewHolder> {

    public FaceAdapter() {
        super(R.layout.item_face);
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceModel faceRegist) {
        String[] imgArray = faceRegist.getPortrait().split(Constants.REGEX.PORTRAIT);

        Glide.with(mContext)
                .load(imgArray[0])
                .into((ImageView) helper.getView(R.id.img));
        helper.setText(R.id.tv_name,faceRegist.getName());
        helper.setText(R.id.tv_faces,"采样数："+faceRegist.getFaceList().size());
    }
}