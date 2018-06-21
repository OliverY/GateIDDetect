package com.yuantu.gateiddtect.widget.dialog;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuantu.gateiddtect.R;

import java.io.File;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:34
 * -----------------------------------------
 * Description:
 */
public class PortraitAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public PortraitAdapter() {
        super(R.layout.item_portrait);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Log.e("yxj","item="+item);
        Glide.with(mContext).load(new File(item)).into((ImageView) helper.itemView);
    }
}
