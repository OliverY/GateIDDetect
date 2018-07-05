package com.yuantu.gateiddtect.ui.main.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuantu.gateiddtect.Constants;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.data.bean.FaceRegist;

/**
 * Author:  Yxj
 * Time:    2018/6/19 下午1:49
 * -----------------------------------------
 * Description:
 */
public class FaceAdapter extends BaseQuickAdapter<FaceRegist,BaseViewHolder> {

    public FaceAdapter() {
        super(R.layout.item_face);
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceRegist faceRegist) {
        String[] imgArray = faceRegist.portrait.split(Constants.REGEX.PORTRAIT);

        Glide.with(mContext)
                .load(imgArray[0])
                .into((ImageView) helper.getView(R.id.img));
        helper.setText(R.id.tv_name,faceRegist.name);
        helper.setText(R.id.tv_faces,"采样数："+faceRegist.mFaceList.size());
    }
}