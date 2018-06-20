package com.yuantu.gateiddtect.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuantu.gateiddtect.R;
import com.yuantu.gateiddtect.bean.FaceRegist;

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
        helper.setText(R.id.tv_name,faceRegist.name);
        helper.setText(R.id.tv_faces,"样本数量："+faceRegist.mFaceList.size());
    }
}
