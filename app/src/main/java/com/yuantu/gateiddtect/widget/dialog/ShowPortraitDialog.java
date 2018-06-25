package com.yuantu.gateiddtect.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuantu.gateiddtect.R;
import com.yxj.dialog.BaseDialog;

import java.io.File;
import java.util.List;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:14
 * -----------------------------------------
 * Description:
 */
public class ShowPortraitDialog extends BaseDialog {

    private static final String TAG = ShowPortraitDialog.class.getSimpleName();

    protected ShowPortraitDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseDialog.Builder {

        RecyclerView rv;
        TextView tvName;
        TextView tvFaces;
        Button btnAdd;
        Button btnDelete;
        Button btnCancel;

        private String name;
        private List<String> imgList;
        private PortraitAdapter adapter;
        private OnDialogClick click;

        public Builder(Context context) {
            super(context);
        }

        @Override
        public int setContentView() {
            return R.layout.dialog_show_protrait;
        }

        @Override
        public void initView(View layout) {
            rv = layout.findViewById(R.id.rv);
            tvName = layout.findViewById(R.id.tv_name);
            tvFaces = layout.findViewById(R.id.tv_faces);
            btnAdd = layout.findViewById(R.id.btn_add);
            btnDelete = layout.findViewById(R.id.btn_delete);
            btnCancel = layout.findViewById(R.id.btn_cancel);

            tvName.setText(name);
            tvFaces.setText("采样数：" + imgList.size());

            adapter = new PortraitAdapter();
            rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rv.setAdapter(adapter);
            adapter.setNewData(imgList);

            btnAdd.setOnClickListener(v -> {
                if (click != null) {
                    click.add();
                }
                dialog.dismiss();
            });
            btnDelete.setOnClickListener(v -> {
                if (click != null) {
                    click.delete();
                }
                dialog.dismiss();
            });
            btnCancel.setOnClickListener(v -> {
                if (click != null) {
                    click.cancel();
                }
                dialog.dismiss();
            });
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setList(List<String> imgList) {
            this.imgList = imgList;
            return this;
        }

        public Builder setClick(OnDialogClick click) {
            this.click = click;
            return this;
        }

    }

    public interface OnDialogClick {
        void add();

        void delete();

        void cancel();
    }

    static class PortraitAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PortraitAdapter() {
            super(R.layout.item_portrait);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            Log.e("yxj", "item=" + item);
            Glide.with(mContext).load(new File(item)).into((ImageView) helper.itemView);
        }
    }

}
